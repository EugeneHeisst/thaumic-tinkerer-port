"""Hidden client resource audit with optional CC in the isolated run directory."""
from pathlib import Path
import shutil, subprocess, sys, zipfile
jar = Path(sys.argv[1]).resolve()
with zipfile.ZipFile(jar) as archive:
    if archive.testzip() is not None:
        raise SystemExit('Corrupt CC runtime JAR')
target = Path('run-client-audit/mods/cc-tweaked-audit.jar')
target.parent.mkdir(parents=True, exist_ok=True)
backup = target.read_bytes() if target.exists() else None
try:
    shutil.copy2(jar, target)
    result = subprocess.run(['./audit-client.sh'], capture_output=True, text=True)
    print(result.stdout, end='')
    print(result.stderr, end='', file=sys.stderr)
    if result.returncode or 'TT CLIENT AUDIT: ALL PASS' not in result.stdout:
        raise SystemExit(result.returncode or 1)
finally:
    if backup is None:
        target.unlink(missing_ok=True)
    else:
        target.write_bytes(backup)
