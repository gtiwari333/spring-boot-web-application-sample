#!/usr/bin/env bash
# =============================================================================
# build-native-images.sh — Build GraalVM native images + Docker images.
#
# Per module:
#   1. one `mvn -Pnative package` invocation runs tests (agent captures
#      reachability metadata), then process-aot, metadata-copy, native compile.
#      Merged from the old two-call (test / package -DskipTests) flow to drop
#      ~30-60 s of repeated dependency + plugin resolution per module.
#   2. docker build wraps the binary in a distroless image. Docker builds run
#      in parallel after all native compiles finish (cheap, daemon-bound).
#
# Each step writes a per-module log under dist/logs/ for easier triage.
#
# USAGE:
#   ./build-native-images.sh                                # all modules
#   ./build-native-images.sh main-app/report-service        # specific modules
#   DOCKER_ONLY=1 ./build-native-images.sh                  # skip Phase A,
#                                                           # docker-package
#                                                           # binaries already
#                                                           # in dist/
# =============================================================================

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
OUTPUT_DIR="${ROOT_DIR}/dist"
LOG_DIR="${OUTPUT_DIR}/logs"
MVN="${ROOT_DIR}/mvnw"
DOCKERFILE="${ROOT_DIR}/docker/Dockerfile.native"
IMAGE_PREFIX="gtapp"
DOCKER_ONLY="${DOCKER_ONLY:-0}"

ALL_MODULES=(
    content-checker/content-checker-service
    email/email-service
    trend/trend-service
    main-app/report-service
    main-app/main-webapp
)

BUILD_MODULES=("${@:-${ALL_MODULES[@]}}")

mkdir -p "${OUTPUT_DIR}" "${LOG_DIR}"

elapsed() {
    # Usage: elapsed <start_seconds> -> prints e.g. "1m 23s" or "45s"
    local start=$1 end secs
    end=$(date +%s)
    secs=$(( end - start ))
    if (( secs >= 60 )); then
        printf "%dm %02ds" $(( secs / 60 )) $(( secs % 60 ))
    else
        printf "%ds" "${secs}"
    fi
}

# ── Build one module: mvn -Pnative package + copy binary to dist/ ──────────────
# Writes "<module_name> <SUCCESS|FAILED|NO_BINARY> <elapsed>" to ${RESULTS_FILE}.
build_module() {
    local module="$1"
    local module_name="${module##*/}"
    local module_dir="${ROOT_DIR}/${module}"
    local log_file="${LOG_DIR}/${module_name}-build.log"
    local t0
    t0=$(date +%s)

    echo "[${module_name}] Building native image (log: dist/logs/${module_name}-build.log)..."

    # Full output -> log file via tee. Console gets only goal-header lines
    # (`[INFO] --- plugin:goal (execution) @ project ---`), [ERROR] lines, and
    # the final BUILD SUCCESS/FAILURE line — enough to see progress without the
    # 10k-line firehose. PIPESTATUS[0] captures mvn's exit code despite the pipe.
    local rc=0
    set +e
    "${MVN}" --batch-mode --no-transfer-progress \
            --file "${module_dir}/pom.xml" \
            -Pnative \
            package 2>&1 \
        | tee "${log_file}" \
        | awk -v p="[${module_name}] " '
            /^\[INFO\] --- / || /^\[ERROR\]/ || /^\[INFO\] BUILD / { print p $0; fflush() }
          '
    rc=${PIPESTATUS[0]}
    set -e

    if (( rc != 0 )); then
        echo "[${module_name}] BUILD FAILED in $(elapsed "${t0}") — see ${log_file}"
        printf '%s\t%s\t%s\n' "${module_name}" "FAILED" "$(elapsed "${t0}")" >> "${RESULTS_FILE}"
        return 1
    fi

    local binary
    binary=$(find "${module_dir}/target" -maxdepth 1 -type f \
        ! -name "*.jar" ! -name "*.xml" ! -name "*.txt" -perm -u+x | head -1)

    if [[ -z "${binary}" ]]; then
        echo "[${module_name}] ERROR: no executable found in target/ — see ${log_file}"
        printf '%s\t%s\t%s\n' "${module_name}" "NO_BINARY" "$(elapsed "${t0}")" >> "${RESULTS_FILE}"
        return 1
    fi

    cp "${binary}" "${OUTPUT_DIR}/${module_name}"
    local sz
    sz=$(du -sh "${OUTPUT_DIR}/${module_name}" | cut -f1)
    echo "[${module_name}] Built in $(elapsed "${t0}") -> dist/${module_name} (${sz})"
    printf '%s\t%s\t%s\n' "${module_name}" "SUCCESS" "$(elapsed "${t0}")" >> "${RESULTS_FILE}"
}

# ── Build docker image for one module ──────────────────────────────────────────
build_docker() {
    local module_name="$1"
    local image="${IMAGE_PREFIX}-${module_name}-native:latest"
    local log_file="${LOG_DIR}/${module_name}-docker.log"
    local t0
    t0=$(date +%s)

    if ! docker build \
            -f "${DOCKERFILE}" \
            --build-arg NATIVE_BINARY="dist/${module_name}" \
            -t "${image}" \
            "${ROOT_DIR}" > "${log_file}" 2>&1; then
        echo "[${module_name}] DOCKER BUILD FAILED in $(elapsed "${t0}") — see ${log_file}"
        printf '%s\t%s\t%s\n' "${module_name}" "FAILED" "$(elapsed "${t0}")" >> "${DOCKER_RESULTS_FILE}"
        return 1
    fi
    echo "[${module_name}] Image -> ${image} (built in $(elapsed "${t0}"))"
    printf '%s\t%s\t%s\n' "${module_name}" "SUCCESS" "$(elapsed "${t0}")" >> "${DOCKER_RESULTS_FILE}"
}

# Look up a tab-separated field for a module in a results file.
# Usage: lookup <file> <module_name> <column 1-indexed>; prints empty if not found.
lookup() {
    awk -F'\t' -v m="$2" -v c="$3" '$1==m {print $c; exit}' "$1" 2>/dev/null
}

# ── Phase A: native builds (sequential) ────────────────────────────────────────
RESULTS_FILE=$(mktemp)
DOCKER_RESULTS_FILE=$(mktemp)
trap 'rm -f "${RESULTS_FILE}" "${DOCKER_RESULTS_FILE}"' EXIT

OVERALL_T0=$(date +%s)

if [[ "${DOCKER_ONLY}" == "1" || "${DOCKER_ONLY}" == "true" ]]; then
    echo ""
    echo "══════════════════════════════════════════"
    echo "  Native build phase — SKIPPED (DOCKER_ONLY=${DOCKER_ONLY})"
    echo "══════════════════════════════════════════"
    # Pre-populate the results file so Phase B sees modules whose binary already
    # exists in dist/ as "SUCCESS". The build time is reported as "-" since we
    # didn't actually compile.
    for module in "${BUILD_MODULES[@]}"; do
        module_name="${module##*/}"
        if [[ -x "${OUTPUT_DIR}/${module_name}" ]]; then
            printf '%s\t%s\t%s\n' "${module_name}" "SUCCESS" "-" >> "${RESULTS_FILE}"
            echo "[${module_name}] Found existing binary at dist/${module_name}"
        else
            printf '%s\t%s\t%s\n' "${module_name}" "NO_BINARY" "-" >> "${RESULTS_FILE}"
            echo "[${module_name}] No binary at dist/${module_name} — docker build will be skipped"
        fi
    done
else
    echo ""
    echo "══════════════════════════════════════════"
    echo "  Native build phase"
    echo "══════════════════════════════════════════"

    for module in "${BUILD_MODULES[@]}"; do
        build_module "${module}" || true   # don't let one failure abort the loop
    done
fi

# ── Phase B: docker builds (parallel, cheap, only for modules whose build succeeded)
echo ""
echo "══════════════════════════════════════════"
echo "  Docker build phase"
echo "══════════════════════════════════════════"

for module in "${BUILD_MODULES[@]}"; do
    module_name="${module##*/}"
    if [[ "$(lookup "${RESULTS_FILE}" "${module_name}" 2)" != "SUCCESS" ]]; then
        echo "[${module_name}] Skipping docker — native build did not succeed"
        continue
    fi
    build_docker "${module_name}" &
done
wait

# ── Timing report ──────────────────────────────────────────────────────────────
FAILED=()
echo ""
echo "══════════════════════════════════════════════════════════════════════════"
echo "  Build Timing Report"
echo "══════════════════════════════════════════════════════════════════════════"
printf "%-35s  %10s  %10s  %s\n" "Module" "Build" "Docker" "Status"
printf "%-35s  %10s  %10s  %s\n"  "------" "-----" "------" "------"

for module in "${BUILD_MODULES[@]}"; do
    module_name="${module##*/}"
    bs="$(lookup "${RESULTS_FILE}"        "${module_name}" 2)"; : "${bs:=MISSING}"
    bt="$(lookup "${RESULTS_FILE}"        "${module_name}" 3)"; : "${bt:=-}"
    ds="$(lookup "${DOCKER_RESULTS_FILE}" "${module_name}" 2)"; : "${ds:=SKIPPED}"
    dt="$(lookup "${DOCKER_RESULTS_FILE}" "${module_name}" 3)"; : "${dt:=-}"

    case "${bs}" in
        SUCCESS)
            if [[ "${ds}" == "SUCCESS" ]]; then
                status="SUCCESS"
            elif [[ "${ds}" == "FAILED" ]]; then
                status="FAILED(docker)"; FAILED+=("${module_name}")
            else
                status="FAILED(no-docker)"; FAILED+=("${module_name}")
            fi
            ;;
        FAILED)    status="FAILED(build)";     FAILED+=("${module_name}") ;;
        NO_BINARY) status="FAILED(no-binary)"; FAILED+=("${module_name}") ;;
        *)         status="${bs}";             FAILED+=("${module_name}") ;;
    esac

    printf "%-35s  %10s  %10s  %s\n" "${module_name}" "${bt}" "${dt}" "${status}"
done
echo "══════════════════════════════════════════════════════════════════════════"
echo "Total wall-clock: $(elapsed "${OVERALL_T0}")"

echo ""
echo "══════════════════════════════════════════"
echo "  Build complete"
echo "══════════════════════════════════════════"

if (( ${#FAILED[@]} > 0 )); then
    echo "FAILED: ${FAILED[*]}"
    echo "Logs:   ${LOG_DIR}/"
    exit 1
fi

echo ""
echo "Native binaries (dist/):"
ls -lhp "${OUTPUT_DIR}/" | grep -v '/$' | grep -v '^total'

echo ""
echo "Docker images:"
docker images "${IMAGE_PREFIX}-*-native" --format "table {{.Repository}}:{{.Tag}}\t{{.Size}}"
