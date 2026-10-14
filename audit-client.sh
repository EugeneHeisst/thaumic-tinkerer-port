#!/usr/bin/env bash
set -euo pipefail
cd -- "$(dirname -- "${BASH_SOURCE[0]}")"
python3 - <<'PY'
from pathlib import Path
p=Path('run-client-audit/options.txt');p.parent.mkdir(exist_ok=True)
s=p.read_text() if p.exists() else ''
lines=[l for l in s.splitlines() if not l.startswith('soundCategory_')]
lines.extend('soundCategory_'+k+':0.0' for k in ['master','music','record','weather','block','hostile','neutral','player','ambient','voice'])
p.write_text('\n'.join(lines)+'\n')
PY
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk
# OpenAL null backend keeps hidden checks silent.
export ALSOFT_DRIVERS=null
exec xvfb-run -a -s '-screen 0 1280x960x24' ./gradlew runPortClientAudit --console=plain "$@"
