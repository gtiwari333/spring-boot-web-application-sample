# Kubernetes Deployment

Local Kubernetes deployment for the spring-boot-web-application-sample using Minikube.

For simpler local development, use `docker-compose.yml` at the repo root instead.

## Prerequisites

- [Minikube](https://minikube.sigs.k8s.io/docs/start/)
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
> image on the next `kubectl rollout restart deployment/<name> -n app-platform`.

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
kubectl create configmap keycloak-realm \
  --from-file=main-app/main-webapp/src/main/resources/keycloak/ \
  -n app-platform

# 4. Deploy infrastructure services (database first)
kubectl apply -f k8s/base/mysql.yaml
kubectl apply -f k8s/base/activemq.yaml
kubectl apply -f k8s/base/emailhog.yaml
kubectl apply -f k8s/base/zipkin.yaml
kubectl apply -f k8s/base/keycloak.yaml

# 5. Wait for MySQL to be ready before Java apps start connecting
kubectl wait --for=condition=ready pod -l app=mysql -n app-platform --timeout=120s

# Use `kubectl port-forward service/mysql 3306:3306 -n app-platform` to allow host to connect to mysql running inside minikube

# 6. Deploy Java services
kubectl apply -f k8s/base/java-apps.yaml

# 7. Deploy ingress
kubectl apply -f k8s/base/ingress.yaml

# 8. Watch pods come up
kubectl get pods -n app-platform -w
```

## Keycloak redirect fix

```bash
# Get Minikube IP
minikube ip
# e.g. 192.168.49.2

# Add to /etc/hosts on your laptop
echo "192.168.49.2 keycloak-external" | sudo tee -a /etc/hosts

# Verify
ping keycloak
```

## Keycloak redirect full picture

```
/etc/hosts on host:
  192.168.49.2  keycloak-external

Browser login flow:
  browser  →  keycloak-external:30080  (resolves via /etc/hosts → NodePort) ✓

Pod backchannel:
  main-webapp pod  →  keycloak:8080  (Kubernetes DNS → ClusterIP) ✓

Token issuer from browser's perspective:
  http://keycloak-external:30080/realms/seedapp  ✓

Token issuer from pod's perspective (backchannel dynamic):
  http://keycloak:8080/realms/seedapp  ✓
```

## Monitor kubernetes cluster by running portainer inside minikube

Add a /etc/hosts entry on the laptop so keycloak resolves to the Minikube IP, making the hostname consistent for both the browser and the pods:

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



## Access the applications

```bash
# Get the URL for the main app
minikube service main-webapp -n app-platform --url

# Or use port-forwarding for any service
kubectl port-forward -n app-platform service/main-webapp   8080:8080
kubectl port-forward -n app-platform service/keycloak      8081:8080
kubectl port-forward -n app-platform service/zipkin        9411:9411
kubectl port-forward -n app-platform service/emailhog      8025:8025
kubectl port-forward -n app-platform service/activemq      8161:8161
```

| Service  | URL                   |
|----------|-----------------------|
| Main app | http://localhost:8080 |
| Keycloak | http://localhost:8081 |
| Zipkin   | http://localhost:9411 |
| EmailHog | http://localhost:8025 |
| ActiveMQ | http://localhost:8161 |

## Useful commands

```bash
# View all deployments
kubectl get deployments -n app-platform

# View all instances(pods)
kubectl get pods -n app-platform

# to restart all pods from all deployments
kubectl rollout restart deployment -n app-platform
 
# restart from a specific deployment
kubectl rollout restart deployment <deployment-name>
 
# Check logs for a service
kubectl logs -f deployment/<DEPLOYMENT_NAME> -n app-platform

# Describe a pod (useful when a pod won't start)
kubectl describe pod <pod-name> -n app-platform

# Check events (first place to look when something is wrong)
kubectl get events -n app-platform --sort-by='.lastTimestamp'

# Scale a service (load balancing is automatic)
kubectl scale deployment <DEPLOYMENT_NAME> -n app-platform --replicas=5

# Open the Minikube dashboard
minikube dashboard
```

## Tear down

```bash
# Delete all resources in the namespace
kubectl delete namespace app-platform

# Or stop Minikube entirely
minikube stop
```

## Troubleshooting

**Pod stuck in `Pending`** — usually a resource issue. Check with:
```bash
kubectl describe pod <pod-name> -n app-platform
```
Increase Minikube memory if needed: `minikube start --memory=10240`

**Pod stuck in `ImagePullBackOff`** — the image wasn't built inside Minikube's Docker daemon.
Re-run `eval $(minikube docker-env)` and then `./build-docker-images.sh`.

**Java app can't connect to MySQL** — MySQL readiness probe may not have passed yet.
Check with `kubectl logs deployment/mysql -n app-platform` and wait for "ready for connections".

**Keycloak realm not imported** — the ConfigMap may be missing or stale. Re-run:
```bash
kubectl delete configmap keycloak-realm -n app-platform
kubectl create configmap keycloak-realm \
  --from-file=main-app/main-webapp/src/main/resources/keycloak/ \
  -n app-platform
kubectl rollout restart deployment/keycloak -n app-platform
```
