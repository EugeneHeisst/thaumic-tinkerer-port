#!/usr/bin/env bash
set -euo pipefail
cd -- "$(dirname -- "${BASH_SOURCE[0]}")"
# Gradle selects Java 21; JAVA_HOME may be supplied by the caller.
exec ./gradlew build --console=plain "$@"
