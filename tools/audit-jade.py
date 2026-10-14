"""Run native Jade provider checks in the isolated server and restore its mod."""
from pathlib import Path
import shutil, subprocess, sys, zipfile
source = Path(sys.argv[1])
with zipfile.ZipFile(source) as archive:
    if archive.testzip() is not None:
        raise SystemExit('Corrupt Jade runtime')
target = Path('/tmp/tt-modern-native-audit/mods/jade-audit.jar')
target.parent.mkdir(parents=True, exist_ok=True)
backup = target.read_bytes() if target.exists() else None
try:
    shutil.copy2(source, target)
    result = subprocess.run([sys.executable, 'tools/audit-server.py'], capture_output=True, text=True)
    print(result.stdout, end='')
    print(result.stderr, end='', file=sys.stderr)
    log = Path('/tmp/tt-modern-native-audit/audit.log').read_text()
    for line in log.splitlines():
        if 'TT JADE PAYLOAD AUDIT:' in line:
            print(line)
    if result.returncode or 'TT JADE PAYLOAD AUDIT: ALL PASS' not in log:
        raise SystemExit(result.returncode or 1)
finally:
    if backup is None:
        target.unlink(missing_ok=True)
    else:
        target.write_bytes(backup)
