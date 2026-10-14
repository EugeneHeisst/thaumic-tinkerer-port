"""Run CC jar-capability checks only in the isolated audit server; restore its mods."""
from pathlib import Path
import shutil, subprocess, sys, zipfile
jar = Path(sys.argv[1])
with zipfile.ZipFile(jar) as archive:
    if archive.testzip() is not None:
        raise SystemExit('Corrupt CC:T runtime JAR')
target = Path('/tmp/tt-modern-native-audit/mods/cc-tweaked-audit.jar')
target.parent.mkdir(parents=True, exist_ok=True)
backup = target.read_bytes() if target.exists() else None
try:
    shutil.copy2(jar, target)
    result = subprocess.run([sys.executable, 'tools/audit-server.py', *(['--cc-lua'] if '--cc-lua' in sys.argv else [])], capture_output=True, text=True)
    print(result.stdout, end='')
    print(result.stderr, end='', file=sys.stderr)
    log = Path('/tmp/tt-modern-native-audit/'+('audit-cc-lua.log' if '--cc-lua' in sys.argv else 'audit.log')).read_text()
    if '--cc-lua' in sys.argv:
        if result.returncode or 'TT CC LUA AUDIT: ALL PASS' not in log or 'TT FUME LUA AUDIT: ALL PASS' not in log or 'TT MAGNET LUA AUDIT: ALL PASS' not in log or 'TT TRANSVECTOR LUA AUDIT: ALL PASS' not in log or 'TT TABLET LUA AUDIT: ALL PASS' not in log: raise SystemExit(result.returncode or 1)
    elif result.returncode or any(marker not in log for marker in ('TT CC TABLET PERIPHERAL AUDIT: ALL PASS', 'TT CC TRANSVECTOR PERIPHERAL AUDIT: ALL PASS', 'TT CC MAGNET PERIPHERAL AUDIT: ALL PASS', 'TT FUME TOOL AUDIT: ALL PASS', 'TT GOLEM BINDER AUDIT: ALL PASS', 'TT GOLEM CONNECTOR RESEARCH AUDIT: ALL PASS', 'TT GOLEM CONNECTOR ENTITY AUDIT: ALL PASS', 'TT GOLEM MARKER CODEC AUDIT: ALL PASS', 'TT CC ANALYZER AUDIT: ALL PASS', 'TT CC JAR PERIPHERAL AUDIT: ALL PASS', 'TT CC DEVICE PERIPHERAL AUDIT: ALL PASS', 'TT CC EAR PERIPHERAL AUDIT: ALL PASS', 'TT CC TRANSPORT PERIPHERAL AUDIT: ALL PASS', 'TT CC BORE PERIPHERAL AUDIT: ALL PASS', 'TT CC ASPECT CONTAINER AUDIT: ALL PASS', 'TT CC FALLBACK PERIPHERAL AUDIT: ALL PASS')):
        raise SystemExit(result.returncode or 1)
finally:
    if backup is None:
        target.unlink(missing_ok=True)
    else:
        target.write_bytes(backup)
