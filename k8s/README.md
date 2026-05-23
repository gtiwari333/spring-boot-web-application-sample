# Kubernetes Deployment

Local Kubernetes deployment for the spring-boot-web-application-sample using Minikube.

For simpler local development, use `docker-compose.yml` at the repo root instead.

## Prerequisites

- [Minikube](https://minikube.sigs.k8s.io/docs/start/) with a supported driver (`docker`, `kvm2`, etc.)
- [kubectl](https://kubernetes.io/docs/tasks/tools/)
- Docker Desktop (with Rosetta enabled on Apple Silicon)

## First-time setup

```bash
minikube start --cpus=4 --memory=8192 --driver=docker
minikube addons enable ingress
minikube addons enable metrics-server
```

## Build Docker images for apps

Minikube runs its own Docker daemon, separate from your laptop's Docker. Images must be
built inside it, otherwise pods will fail with `ImagePullBackOff`.

Run from the **repo root**:

```bash
# Point your shell at Minikube's Docker daemon (do this once per terminal session)
eval $(minikube docker-env)

# Generate docker images for all application images
# comment 'mvnd  -T 5 clean package install' if you've already built the project
./build-docker-images.sh
```

> **Apple Silicon (M1/M2):** ActiveMQ requires x86 emulation. Enable it in
> Docker Desktop → Settings → Features in development → "Use Rosetta for x86/amd64 emulation"
> before building.

> **Rebuilding after code changes:** re-run `eval $(minikube docker-env)` if you opened
> a new terminal, then `./build-docker-images.sh` again. Kubernetes will pick up the new
> image on the next `kubectl rollout restart deployment/<name> -n sample-app-ns`.

## IMPORTANT !!!

Kubernetes was designed for stateless workloads. It's perfect at restarting, rescheduling, and scaling pods — but those same behaviors are dangerous for a database.

MySQL should not be deployed through kubernetes, instead it should be a 'managed DB' or 'dedicated VM'.

*we are deploying MySQL from k8s for testing purpose only*

```
Kubernetes cluster
├── main-webapp         ┐
├── email-service       │
├── content-checker     ├─ all stateless, scale freely
├── trend-service       │
├── report-service      ┘
├── activemq            ← acceptable in k8s with persistent volume
├── zipkin              ← acceptable in k8s
└── keycloak            ← acceptable in k8s (backed by external DB)

Outside Kubernetes
├── MySQL               ← managed, backups, failover handled for you
└── (optional) Keycloak backed by that same DB instance
```


## Deploy

Run all commands from the **repo root**.

```bash
# 1. Create the namespace
kubectl apply -f k8s/base/namespace.yaml

# 2. Apply ConfigMap and Secrets
kubectl apply -f k8s/base/config.yaml

# 3. Load Keycloak realm files as a ConfigMap
# delete existing for updates
kubectl delete configmap keycloak-realm -n sample-app-ns

kubectl create configmap keycloak-realm \
  --from-file=main-app/main-webapp/src/main/resources/keycloak/ \
  -n sample-app-ns

# 4. Deploy infrastructure services (database first)
kubectl apply -f k8s/base/mysql.yaml
kubectl apply -f k8s/base/activemq.yaml
kubectl apply -f k8s/base/emailhog.yaml
kubectl apply -f k8s/base/zipkin.yaml
kubectl apply -f k8s/base/keycloak.yaml

# 5. Wait for MySQL and KeyCloak to be ready before Java apps start connecting
kubectl wait --for=condition=ready pod -l app=mysql -n sample-app-ns --timeout=120s
kubectl wait --for=condition=ready pod -l app=keycloak -n sample-app-ns --timeout=120s

# Use `kubectl port-forward service/mysql 3306:3306 -n sample-app-ns` to allow host to connect to mysql running inside minikube

# 6. Deploy Java services
kubectl apply -f k8s/base/java-apps.yaml

# 7. Deploy ingress
kubectl apply -f k8s/base/ingress.yaml

# 8. Watch pods come up
kubectl get pods -n sample-app-ns -w
```

## /etc/hosts entries

The Ingress routes by `Host` header. Both the browser **and the Spring Boot pod** need to resolve the hostnames to the Minikube IP.

```bash
# Get Minikube IP
minikube ip
# e.g. 192.168.49.2

# Add to /etc/hosts on your laptop
echo "192.168.39.116 app.local keycloak.local zipkin.local emailhog.local" | sudo tee -a /etc/hosts

# Verify
ping keycloak.local
```

> **hostAliases:** The `java-apps.yaml` main-webapp deployment includes a `hostAliases` entry so the pod can resolve `keycloak.local` internally. Update the IP there to match `minikube ip` if you restart Minikube.

## Access the app

| URL | Purpose |
|---|---|
| `http://app.local` | Spring Boot main webapp |
| `http://keycloak.local` | Keycloak Admin Console (`admin` / `admin`) |
| `http://zipkin.local` | Zipkin tracing UI |
| `http://emailhog.local` | MailHog (captured emails) |

## Keycloak redirect full picture

```
/etc/hosts on host:
  <minikube-ip>  keycloak.local  app.local

Browser login flow:
  browser  →  keycloak.local  (resolves via /etc/hosts → Ingress → keycloak:8080) ✓

Pod backchannel:
  main-webapp pod  →  keycloak:8080  (Kubernetes DNS → ClusterIP) ✓
  (or via hostAliases → keycloak.local → Ingress → keycloak:8080)

Token issuer from browser's perspective:
  http://keycloak.local/realms/seedapp  ✓

Token issuer from pod's perspective:
  http://keycloak:8080/realms/seedapp  ✓
```

## Monitor kubernetes cluster by running portainer inside minikube

```bash
# 1. Add the Portainer Helm repo
helm repo add portainer https://portainer.github.io/k8s/
helm repo update

# 2. Create a namespace for Portainer
kubectl create namespace portainer

# 3. Install Portainer using NodePort (easiest for Minikube)
helm install portainer portainer/portainer \
  --namespace portainer \
  --set service.type=NodePort \
  --set service.nodePort=30777 \
  --set service.httpNodePort=30778

# 4. Wait for Portainer to be ready
kubectl wait --for=condition=ready pod -l app.kubernetes.io/name=portainer \
  -n portainer --timeout=120s

# 5. Get the URL and open
minikube service portainer -n portainer --url
```



## Useful commands

```bash
# View all deployments
kubectl get deployments -n sample-app-ns

# View all instances(pods)
kubectl get pods -n sample-app-ns

# to restart all pods from all deployments
kubectl rollout restart deployment -n sample-app-ns
 
# restart from a specific deployment
kubectl rollout restart deployment <deployment-name> -n sample-app-ns

# eg:

kubectl rollout restart deployment/email-service -n sample-app-ns
kubectl rollout restart deployment/report-service -n sample-app-ns
kubectl rollout restart deployment/content-checker-service -n sample-app-ns
kubectl rollout restart deployment/main-webapp -n sample-app-ns
kubectl rollout restart deployment/trend-service -n sample-app-ns

kubectl rollout restart deployment/keycloak -n sample-app-ns
kubectl rollout restart deployment/zipkin -n sample-app-ns
kubectl rollout restart deployment/emailhog -n sample-app-ns
kubectl rollout restart deployment/mysql -n sample-app-ns
 
# Check logs for a service
kubectl logs -f deployment/<DEPLOYMENT_NAME> -n sample-app-ns
kubectl logs deployment/content-checker-service -n sample-app-ns --tail=20


# Describe a pod (useful when a pod won't start)
kubectl describe pod <pod-name> -n sample-app-ns

# Check events (first place to look when something is wrong)
kubectl get events -n sample-app-ns --sort-by='.lastTimestamp'

# Scale a service (load balancing is automatic)
kubectl scale deployment <DEPLOYMENT_NAME> -n sample-app-ns --replicas=5


# Open the Minikube dashboard
minikube dashboard
```

## Tear down

```bash
# Delete all resources in the namespace
kubectl delete namespace sample-app-ns

# Or stop Minikube entirely
minikube stop
```

## Troubleshooting

**Pod stuck in `Pending`** — usually a resource issue. Check with:
```bash
kubectl describe pod <pod-name> -n sample-app-ns
```
Increase Minikube memory if needed: `minikube start --memory=10240`

**Pod stuck in `ImagePullBackOff`** — the image wasn't built inside Minikube's Docker daemon.
Re-run `eval $(minikube docker-env)` and then `./build-docker-images.sh`.

**Java app can't connect to MySQL** — MySQL readiness probe may not have passed yet.
Check with `kubectl logs deployment/mysql -n sample-app-ns` and wait for "ready for connections".

**Keycloak realm not imported** — the ConfigMap may be missing or stale. Re-run:
```bash
kubectl delete configmap keycloak-realm -n sample-app-ns
kubectl create configmap keycloak-realm \
  --from-file=main-app/main-webapp/src/main/resources/keycloak/ \
  -n sample-app-ns
kubectl rollout restart deployment/keycloak -n sample-app-ns
```
