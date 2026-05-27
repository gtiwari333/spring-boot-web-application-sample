#!/usr/bin/env bash
# =============================================================================
# build-native.sh  —  Build GraalVM native images + Docker images for all modules
#
# Each module is built in three phases:
#   1. mvn test        (tracing agent — captures reachability metadata)
#   2. mvn package     (native compile using that metadata)
#   3. docker build    (distroless image wrapping the binary)
#
# USAGE:
#   ./build-native.sh                                 # build all modules
#   ./build-native.sh a/b/service-1 c/service-2       # specific modules
#   ./build-native-images.sh main-app/report-service
# =============================================================================

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
OUTPUT_DIR="${ROOT_DIR}/dist"
MVN="${ROOT_DIR}/mvnw"
DOCKERFILE="${ROOT_DIR}/docker/Dockerfile.native"
IMAGE_PREFIX="gtapp"

ALL_MODULES=(
    content-checker/content-checker-service
    email/email-service
    trend/trend-service
    main-app/report-service
    main-app/main-webapp
)

BUILD_MODULES=("${@:-${ALL_MODULES[@]}}")

mkdir -p "${OUTPUT_DIR}"

FAILED=()

# Timing storage: parallel arrays keyed by index
TIMING_MODULES=()
TIMING_AGENT=()
TIMING_NATIVE=()
TIMING_DOCKER=()
TIMING_STATUS=()

elapsed() {
    # Usage: elapsed <start_seconds> -> prints e.g. "1m 23s" or "45s"
    local start=$1
    local end
    end=$(date +%s)
    local secs=$(( end - start ))
    if (( secs >= 60 )); then
        printf "%dm %02ds" $(( secs / 60 )) $(( secs % 60 ))
    else
        printf "%ds" "${secs}"
    fi
}

for module in "${BUILD_MODULES[@]}"; do
    module_name="${module##*/}"
    module_dir="${ROOT_DIR}/${module}"

    echo ""
    echo "══════════════════════════════════════════"
    echo "  ${module}"
    echo "══════════════════════════════════════════"

    TIMING_MODULES+=("${module_name}")
    t_agent="-"
    t_native="-"
    t_docker="-"
    status="SUCCESS"

    # ── Phase 1: capture reachability metadata via tests ─────────────────────
    echo "[${module_name}] Phase 1: collecting reachability metadata via tests..."
    t0=$(date +%s)
    if "${MVN}" --batch-mode --no-transfer-progress \
        --file "${module_dir}/pom.xml" \
        -Pnative \
        test; then
        t_agent=$(elapsed "${t0}")
    else
        t_agent=$(elapsed "${t0}")
        TIMING_AGENT+=("${t_agent}")
        TIMING_NATIVE+=("${t_native}")
        TIMING_DOCKER+=("${t_docker}")
        TIMING_STATUS+=("FAILED(agent)")
        FAILED+=("${module_name}")
        continue
    fi

    # ── Phase 2: compile native image ─────────────────────────────────────────
    echo "[${module_name}] Phase 2: compiling native image..."
    t0=$(date +%s)
    if "${MVN}" --batch-mode --no-transfer-progress \
        --file "${module_dir}/pom.xml" \
        -Pnative \
        package -DskipTests; then
        t_native=$(elapsed "${t0}")
    else
        t_native=$(elapsed "${t0}")
        TIMING_AGENT+=("${t_agent}")
        TIMING_NATIVE+=("${t_native}")
        TIMING_DOCKER+=("${t_docker}")
        TIMING_STATUS+=("FAILED(native)")
        FAILED+=("${module_name}")
        continue
    fi

    # ── Copy binary to dist/ ───────────────────────────────────────────────────
    binary=$(find "${module_dir}/target" -maxdepth 1 -type f \
        ! -name "*.jar" ! -name "*.xml" ! -name "*.txt" -perm -u+x | head -1)

    if [[ -z "${binary}" ]]; then
        echo "[${module_name}] ERROR: no executable found in target/ — skipping Docker build"
        TIMING_AGENT+=("${t_agent}")
        TIMING_NATIVE+=("${t_native}")
        TIMING_DOCKER+=("${t_docker}")
        TIMING_STATUS+=("FAILED(no-binary)")
        FAILED+=("${module_name}")
        continue
    fi

    binary_dest="${OUTPUT_DIR}/${module_name}"
    cp "${binary}" "${binary_dest}"
    binary_size=$(du -sh "${binary_dest}" | cut -f1)
    echo "[${module_name}] Binary -> dist/${module_name}  (${binary_size})"

    # ── Phase 3: build Docker image ────────────────────────────────────────────
    image="${IMAGE_PREFIX}-${module_name}-native:latest"
    echo "[${module_name}] Phase 3: building Docker image ${image}..."
    t0=$(date +%s)
    if docker build \
        -f "${DOCKERFILE}" \
        --build-arg NATIVE_BINARY="dist/${module_name}" \
        -t "${image}" \
        "${ROOT_DIR}"; then
        t_docker=$(elapsed "${t0}")
    else
        t_docker=$(elapsed "${t0}")
        TIMING_AGENT+=("${t_agent}")
        TIMING_NATIVE+=("${t_native}")
        TIMING_DOCKER+=("${t_docker}")
        TIMING_STATUS+=("FAILED(docker)")
        FAILED+=("${module_name}")
        continue
    fi

    echo "[${module_name}] Image -> ${image}"
    TIMING_AGENT+=("${t_agent}")
    TIMING_NATIVE+=("${t_native}")
    TIMING_DOCKER+=("${t_docker}")
    TIMING_STATUS+=("${status}")
done

# ── Timing Report ──────────────────────────────────────────────────────────────
echo ""
echo "══════════════════════════════════════════════════════════════════════════"
echo "  Build Timing Report"
echo "══════════════════════════════════════════════════════════════════════════"
printf "%-35s  %10s  %10s  %10s  %s\n" "Module" "Agent" "Native" "Docker" "Status"
printf "%-35s  %10s  %10s  %10s  %s\n" "------" "-----" "------" "------" "------"
for i in "${!TIMING_MODULES[@]}"; do
    printf "%-35s  %10s  %10s  %10s  %s\n" \
        "${TIMING_MODULES[$i]}" \
        "${TIMING_AGENT[$i]}" \
        "${TIMING_NATIVE[$i]}" \
        "${TIMING_DOCKER[$i]}" \
        "${TIMING_STATUS[$i]}"
done
echo "══════════════════════════════════════════════════════════════════════════"

# ── Summary ────────────────────────────────────────────────────────────────────
echo ""
echo "══════════════════════════════════════════"
echo "  Build complete"
echo "══════════════════════════════════════════"

if [[ ${#FAILED[@]} -gt 0 ]]; then
    echo "FAILED: ${FAILED[*]}"
    # Report printed above — exit with error
    exit 1
fi

echo ""
echo "Native binaries (dist/):"
ls -lh "${OUTPUT_DIR}/"

echo ""
echo "Docker images:"
docker images "${IMAGE_PREFIX}-*-native" --format "table {{.Repository}}:{{.Tag}}\t{{.Size}}"
