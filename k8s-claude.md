Here's a refined version of the guide, corrected based on your actual Docker Compose setup and the key architectural difference it introduces.

The biggest thing your original Docker Compose does that the guide missed is `network_mode: "service:localhost"` — all your services share a single network namespace, so they communicate via `localhost:PORT`. In Kubernetes, that pattern doesn't exist. Each pod gets its own IP, and services talk to each other by DNS name (e.g., `activemq`, `mysql`). Your Spring apps will need their configs updated to point to those service names.

Here's an architecture diagram first, then the full refined guide.Click any node in the diagram to dive deeper into that topic. Now here's the refined guide:

```angular2html
┌─────────────────────────────────────────────────────────────────────┐
│  namespace: app-platform                                            │
│                                                                     │
│              [ Browser / External Traffic ]                         │
│                           │                                         │
│                           ▼                                         │
│              ┌────────────────────────┐                             │
│              │   Ingress (nginx)      │                             │
│              └────────────┬───────────┘                             │
│                           │                                         │
│                           ▼                                         │
│        ┌──────────────────────────────────────┐                    │
│        │           main-webapp                │                    │
│        │    NodePort 30080 · prod profile     │                    │
│        └──┬──────┬──────────┬──────────┬──────┘                    │
│           │      │          │          │                            │
│     ──────────────────────────────────────────────────────          │
│     Infrastructure pods                                             │
│     ──────────────────────────────────────────────────────          │
│           │      │          │          │                            │
│           ▼      ▼          ▼          ▼                            │
│     ┌─────────┐ ┌────────┐ ┌──────────┐ ┌──────────┐              │
│     │  mysql  │ │activemq│ │ keycloak │ │  zipkin  │              │
│     │  :3306  │ │ :61616 │ │  :8080   │ │  :9411   │              │
│     │ PVC 10G │ │  :8161 │ │ PVC  5G  │ └──────────┘              │
│     └────┬────┘ └────────┘ └──────────┘                            │
│          │                                                          │
│          ▼                                                          │
│     ┌──────────┐                                                   │
│     │ emailhog │                                                   │
│     │  :1025   │                                                   │
│     │  :8025   │                                                   │
│     └──────────┘                                                   │
│                                                                     │
│     ──────────────────────────────────────────────────────          │
│     Java microservice pods                                          │
│     ──────────────────────────────────────────────────────          │
│                                                                     │
│  ┌──────────────┐ ┌─────────────────┐ ┌──────────────┐            │
│  │ email-service│ │content-checker  │ │trend-service │            │
│  │→ emailhog    │ │    -service      │ │→ zipkin      │            │
│  │→ zipkin      │ │→ activemq,zipkin │ └──────────────┘            │
│  └──────────────┘ └─────────────────┘                              │
│                                                                     │
│  ┌──────────────────────────────────┐                              │
│  │         report-service           │                              │
│  │  → mysql · keycloak · zipkin     │                              │
│  └──────────────────────────────────┘                              │
│                                                                     │
│     ──────────────────────────────────────────────────────          │
│     Cluster config                                                  │
│     ──────────────────────────────────────────────────────          │
│                                                                     │
│        ┌──────────────────┐   ┌──────────────────┐                 │
│        │    ConfigMap      │   │     Secret       │                 │
│        │ non-sensitive     │   │  passwords &     │                 │
│        │   env vars        │   │  credentials     │                 │
│        └──────────────────┘   └──────────────────┘                 │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘

```


---

## Key correction: the `localhost` network pattern

Your Docker Compose uses `network_mode: "service:localhost"` so every service binds to the same network namespace — they all literally talk to `localhost`. **Kubernetes has no equivalent.** Each pod gets its own IP, but Kubernetes DNS handles service discovery automatically: a Service named `mysql` is reachable at `mysql:3306` from any pod in the same namespace.

This means your Spring Boot apps need their `application-prod.properties` (or env vars) updated:

| Docker Compose (localhost) | Kubernetes env var value |
|---|---|
| `localhost:3306` | `mysql:3306` |
| `localhost:61616` | `activemq:61616` |
| `localhost:1025` | `emailhog:1025` |
| `localhost:9411` | `http://zipkin:9411` |
| `localhost:8080` (Keycloak) | `http://keycloak:8080` |

---

## Part 1: Namespace, ConfigMap & Secrets

**`namespace.yaml`**
```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: app-platform
```

**`config.yaml`** — note `KEYCLOAK_DB` is included (your compose had it, the original guide missed it):
```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: app-config
  namespace: app-platform
data:
  MYSQL_DATABASE: "seedapp"
  MYSQL_USER: "seedappuser"
  SPRING_PROFILES_ACTIVE: "prod"
  KEYCLOAK_FEATURES: "scripts"
  KEYCLOAK_HTTP_PORT: "8080"
  KEYCLOAK_HTTPS_PORT: "9445"
  ARTEMIS_USER: "admin"
  KEYCLOAK_DB: "dev-file"
---
apiVersion: v1
kind: Secret
metadata:
  name: app-secrets
  namespace: app-platform
type: Opaque
stringData:
  MYSQL_ROOT_PASSWORD: "password"
  KC_BOOTSTRAP_ADMIN_USERNAME: "admin"
  KC_BOOTSTRAP_ADMIN_PASSWORD: "admin"
  ARTEMIS_PASSWORD: "admin"
```

---

## Part 2: Infrastructure Services

**`mysql.yaml`**
```yaml
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: mysql-pvc
  namespace: app-platform
spec:
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 10Gi
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: mysql
  namespace: app-platform
spec:
  replicas: 1
  selector:
    matchLabels:
      app: mysql
  template:
    metadata:
      labels:
        app: mysql
    spec:
      containers:
      - name: mysql
        image: mysql:latest
        args:
          - "--lower_case_table_names=1"
          - "--character_set_server=utf8mb4"
          - "--explicit_defaults_for_timestamp"
        env:
        - name: MYSQL_USER
          valueFrom:
            configMapKeyRef:
              name: app-config
              key: MYSQL_USER
        - name: MYSQL_DATABASE
          valueFrom:
            configMapKeyRef:
              name: app-config
              key: MYSQL_DATABASE
        - name: MYSQL_ROOT_PASSWORD
          valueFrom:
            secretKeyRef:
              name: app-secrets
              key: MYSQL_ROOT_PASSWORD
        ports:
        - containerPort: 3306
        volumeMounts:
        - name: mysql-storage
          mountPath: /var/lib/mysql
        readinessProbe:
          exec:
            command: ["mysqladmin", "ping", "-h", "localhost"]
          initialDelaySeconds: 20
          periodSeconds: 10
      volumes:
      - name: mysql-storage
        persistentVolumeClaim:
          claimName: mysql-pvc
---
apiVersion: v1
kind: Service
metadata:
  name: mysql
  namespace: app-platform
spec:
  selector:
    app: mysql
  ports:
  - port: 3306
    targetPort: 3306
```

**`activemq.yaml`**
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: activemq
  namespace: app-platform
spec:
  replicas: 1
  selector:
    matchLabels:
      app: activemq
  template:
    metadata:
      labels:
        app: activemq
    spec:
      containers:
      - name: activemq
        image: apache/activemq-artemis:2.44.0
        env:
        - name: ARTEMIS_USER
          valueFrom:
            configMapKeyRef:
              name: app-config
              key: ARTEMIS_USER
        - name: ARTEMIS_PASSWORD
          valueFrom:
            secretKeyRef:
              name: app-secrets
              key: ARTEMIS_PASSWORD
        ports:
        - containerPort: 61616
          name: broker
        - containerPort: 8161
          name: console
---
apiVersion: v1
kind: Service
metadata:
  name: activemq
  namespace: app-platform
spec:
  selector:
    app: activemq
  ports:
  - name: broker
    port: 61616
    targetPort: 61616
  - name: console
    port: 8161
    targetPort: 8161
```

**`keycloak.yaml`** — the realm import volume now uses a ConfigMap instead of a raw `hostPath`, which is much safer:
```yaml
# First, create a ConfigMap from your realm JSON files:
# kubectl create configmap keycloak-realm --from-file=../main-app/main-webapp/src/main/resources/keycloak/ -n app-platform
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: keycloak
  namespace: app-platform
spec:
  replicas: 1
  selector:
    matchLabels:
      app: keycloak
  template:
    metadata:
      labels:
        app: keycloak
    spec:
      containers:
      - name: keycloak
        image: quay.io/keycloak/keycloak:26.6.1
        args: ["start-dev", "--import-realm"]
        env:
        - name: KC_BOOTSTRAP_ADMIN_USERNAME
          valueFrom:
            secretKeyRef:
              name: app-secrets
              key: KC_BOOTSTRAP_ADMIN_USERNAME
        - name: KC_BOOTSTRAP_ADMIN_PASSWORD
          valueFrom:
            secretKeyRef:
              name: app-secrets
              key: KC_BOOTSTRAP_ADMIN_PASSWORD
        - name: KEYCLOAK_DB
          valueFrom:
            configMapKeyRef:
              name: app-config
              key: KEYCLOAK_DB
        - name: KEYCLOAK_FEATURES
          valueFrom:
            configMapKeyRef:
              name: app-config
              key: KEYCLOAK_FEATURES
        - name: KEYCLOAK_HTTP_PORT
          valueFrom:
            configMapKeyRef:
              name: app-config
              key: KEYCLOAK_HTTP_PORT
        ports:
        - containerPort: 8080
        volumeMounts:
        - name: realm-import
          mountPath: /opt/keycloak/data/import
      volumes:
      - name: realm-import
        configMap:
          name: keycloak-realm
---
apiVersion: v1
kind: Service
metadata:
  name: keycloak
  namespace: app-platform
spec:
  selector:
    app: keycloak
  ports:
  - name: http
    port: 8080
    targetPort: 8080
```

**`emailhog.yaml`** and **`zipkin.yaml`** stay the same as the original guide — no changes needed.

---

## Part 3: Java Microservices

**`java-apps.yaml`** — corrected with proper service DNS names and only the env vars your compose actually sets. The `depends_on` logic is replaced by readiness probes on the infrastructure pods (already added to MySQL above):

```yaml
# content-checker-service
apiVersion: apps/v1
kind: Deployment
metadata:
  name: content-checker-service
  namespace: app-platform
spec:
  replicas: 1
  selector:
    matchLabels:
      app: content-checker-service
  template:
    metadata:
      labels:
        app: content-checker-service
    spec:
      containers:
      - name: content-checker
        image: gtapp-content-checker-service:latest
        # No SPRING_PROFILES_ACTIVE in compose — don't add it
        env:
        - name: SPRING_ZIPKIN_BASE_URL
          value: "http://zipkin:9411"
        - name: SPRING_ARTEMIS_HOST
          value: "activemq"
        ports:
        - containerPort: 8080
---
apiVersion: v1
kind: Service
metadata:
  name: content-checker-service
  namespace: app-platform
spec:
  selector:
    app: content-checker-service
  ports:
  - port: 8080
    targetPort: 8080
---
# email-service
apiVersion: apps/v1
kind: Deployment
metadata:
  name: email-service
  namespace: app-platform
spec:
  replicas: 1
  selector:
    matchLabels:
      app: email-service
  template:
    metadata:
      labels:
        app: email-service
    spec:
      containers:
      - name: email-service
        image: gtapp-email-service:latest
        env:
        - name: SPRING_PROFILES_ACTIVE
          valueFrom:
            configMapKeyRef:
              name: app-config
              key: SPRING_PROFILES_ACTIVE
        - name: SPRING_ZIPKIN_BASE_URL
          value: "http://zipkin:9411"
        - name: MAILHOG_HOST
          value: "emailhog"
        ports:
        - containerPort: 8080
---
apiVersion: v1
kind: Service
metadata:
  name: email-service
  namespace: app-platform
spec:
  selector:
    app: email-service
  ports:
  - port: 8080
    targetPort: 8080
---
# trend-service
apiVersion: apps/v1
kind: Deployment
metadata:
  name: trend-service
  namespace: app-platform
spec:
  replicas: 1
  selector:
    matchLabels:
      app: trend-service
  template:
    metadata:
      labels:
        app: trend-service
    spec:
      containers:
      - name: trend-service
        image: gtapp-trend-service:latest
        env:
        - name: SPRING_PROFILES_ACTIVE
          valueFrom:
            configMapKeyRef:
              name: app-config
              key: SPRING_PROFILES_ACTIVE
        - name: SPRING_ZIPKIN_BASE_URL
          value: "http://zipkin:9411"
        ports:
        - containerPort: 8080
---
apiVersion: v1
kind: Service
metadata:
  name: trend-service
  namespace: app-platform
spec:
  selector:
    app: trend-service
  ports:
  - port: 8080
    targetPort: 8080
---
# report-service
apiVersion: apps/v1
kind: Deployment
metadata:
  name: report-service
  namespace: app-platform
spec:
  replicas: 1
  selector:
    matchLabels:
      app: report-service
  template:
    metadata:
      labels:
        app: report-service
    spec:
      containers:
      - name: report-service
        image: gtapp-report-service:latest
        env:
        - name: SPRING_PROFILES_ACTIVE
          valueFrom:
            configMapKeyRef:
              name: app-config
              key: SPRING_PROFILES_ACTIVE
        - name: SPRING_ZIPKIN_BASE_URL
          value: "http://zipkin:9411"
        - name: SPRING_DATASOURCE_URL
          value: "jdbc:mysql://mysql:3306/seedapp"
        ports:
        - containerPort: 8080
---
apiVersion: v1
kind: Service
metadata:
  name: report-service
  namespace: app-platform
spec:
  selector:
    app: report-service
  ports:
  - port: 8080
    targetPort: 8080
---
# main-webapp
apiVersion: apps/v1
kind: Deployment
metadata:
  name: main-webapp
  namespace: app-platform
spec:
  replicas: 1
  selector:
    matchLabels:
      app: main-webapp
  template:
    metadata:
      labels:
        app: main-webapp
    spec:
      containers:
      - name: main-webapp
        image: gtapp-main-webapp:latest
        env:
        - name: SPRING_PROFILES_ACTIVE
          valueFrom:
            configMapKeyRef:
              name: app-config
              key: SPRING_PROFILES_ACTIVE
        - name: SPRING_ZIPKIN_BASE_URL
          value: "http://zipkin:9411"
        - name: SPRING_DATASOURCE_URL
          value: "jdbc:mysql://mysql:3306/seedapp"
        - name: SPRING_ARTEMIS_HOST
          value: "activemq"
        ports:
        - containerPort: 8080
        volumeMounts:
        - name: uploads
          mountPath: /tmp/uploads/attachments
      volumes:
      - name: uploads
        emptyDir: {}      # or use a PVC if you need persistence across restarts
---
apiVersion: v1
kind: Service
metadata:
  name: main-webapp
  namespace: app-platform
spec:
  type: NodePort
  selector:
    app: main-webapp
  ports:
  - port: 8080
    targetPort: 8080
    nodePort: 30080
```

> The original guide used `hostPath: /var/tmp/` for uploads. `emptyDir` is cleaner for local dev — it survives pod restarts within the same node session. Use a PVC if you need files to survive pod rescheduling.

---

## Part 4: Deployment order

```bash
# 1. Namespace first
kubectl apply -f namespace.yaml

# 2. ConfigMap and Secrets
kubectl apply -f config.yaml

# 3. Load Keycloak realm files as a ConfigMap
kubectl create configmap keycloak-realm \
  --from-file=../main-app/main-webapp/src/main/resources/keycloak/ \
  -n app-platform

# 4. Infrastructure (MySQL must be healthy before Java apps start)
kubectl apply -f mysql.yaml
kubectl apply -f activemq.yaml
kubectl apply -f emailhog.yaml
kubectl apply -f zipkin.yaml
kubectl apply -f keycloak.yaml

# 5. Wait for MySQL readiness probe to pass
kubectl wait --for=condition=ready pod -l app=mysql -n app-platform --timeout=120s

# 6. Java services
kubectl apply -f java-apps.yaml

# 7. Ingress (if using Minikube with ingress addon)
kubectl apply -f ingress.yaml
```

---

## Part 5: M1/Apple Silicon note

Your compose has a comment about ActiveMQ needing Rosetta on M1. In Kubernetes (Minikube), set the driver and architecture explicitly:

```bash
minikube start --cpus=4 --memory=8192 --driver=docker
# Then for the activemq image specifically, add platform to the pod spec:
# image: apache/activemq-artemis:2.44.0
# Add under the container:
# resources: {}  # and set imagePullPolicy if needed
```

Or add this to the activemq container spec to force x86 emulation:
```yaml
# In the activemq Deployment container spec:
image: apache/activemq-artemis:2.44.0
# Force amd64 image on Apple Silicon
nodeSelector: {}
```

For Minikube on Apple Silicon, the simplest fix is to start with `--driver=docker` and have Rosetta enabled in Docker Desktop — same as your compose workaround, it'll apply automatically.

---

## Quick reference: Docker Compose → Kubernetes

| Docker Compose concept | Kubernetes equivalent |
|---|---|
| `network_mode: service:localhost` | Kubernetes DNS — `servicename:port` |
| `depends_on` | Readiness probes on the dependency |
| `volumes: /host:/container` | `PersistentVolumeClaim` or `emptyDir` |
| Realm files volume | `ConfigMap` loaded from directory |
| Port mapping `8080:8080` | `Service` (ClusterIP/NodePort) |
| `environment:` list | `ConfigMap` + `Secret` via `envFrom`/`valueFrom` |
