#!/usr/bin/env bash
set -euo pipefail

# Build GraalVM native images + Docker containers for all Spring Boot services.
# Requires GraalVM JDK 25.
#
#   sdk install java 25-graal
#   sdk use java 25-graal

MODULES=(
    content-checker/content-checker-service
    email/email-service
    trend/trend-service
    main-app/report-service
    main-app/main-webapp
)

echo "=== 1. Building native binaries (-Pnative) ==="

for mod in "${MODULES[@]}"; do
    echo ""
    echo "--- $mod ---"
    mvn package -Pnative -pl "$mod" -am -DskipTests
done

echo ""
echo "=== 2. Building Docker images for native binaries ==="

for mod in "${MODULES[@]}"; do
    name="$(basename "$mod")"
    native_bin="$mod/target/$name"

    if [ ! -f "$native_bin" ]; then
        echo "  WARNING: $native_bin not found — skipping Docker build for $name"
        continue
    fi

    image="gtapp-${name}-native:latest"
    echo "  $native_bin  ->  $image"
    docker build \
        -f docker/Dockerfile.native \
        --build-arg NATIVE_BINARY="$native_bin" \
        -t "$image" \
        .
done

echo ""
echo "=== Done ==="
echo "Native images:"
docker images | grep -- '-native'
