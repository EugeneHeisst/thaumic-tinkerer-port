"""Exercise real AE2 movement API only in the isolated native audit server."""
from pathlib import Path
import json
import shutil
import subprocess
import sys
import zipfile

if len(sys.argv) not in (3, 4):
    raise SystemExit('Usage: audit-ae2.py AE2.jar GuideME.jar [CC.jar]')
mods = Path('/tmp/tt-modern-native-audit/mods')
mods.mkdir(parents=True, exist_ok=True)
targets = [mods / name for name in ('ae2-audit.jar', 'guideme-audit.jar', 'cc-tweaked-audit.jar')][:len(sys.argv)-1]
backups = {p: p.read_bytes() if p.exists() else None for p in targets}
pack = mods.parent / 'audit-world/datapacks/tt-ae2-blacklist-audit.zip'
pack.parent.mkdir(parents=True, exist_ok=True)
pack_backup = pack.read_bytes() if pack.exists() else None
try:
    with zipfile.ZipFile(pack, 'w') as archive:
        archive.writestr('pack.mcmeta', json.dumps({'pack': {'pack_format': 48, 'description': 'Isolated AE2 relocation blacklist audit'}}))
        archive.writestr('data/c/tags/block/relocation_not_supported.json', json.dumps({'values': ['tc4modern:void_jar']}))
    for source, target in zip(sys.argv[1:], targets):
        with zipfile.ZipFile(source) as archive:
            if archive.testzip() is not None:
                raise SystemExit('Corrupt optional runtime JAR')
        shutil.copy2(source, target)
    result = subprocess.run([sys.executable, 'tools/audit-server.py'], capture_output=True, text=True)
    print(result.stdout, end='')
    print(result.stderr, end='', file=sys.stderr)
    log = Path('/tmp/tt-modern-native-audit/audit.log').read_text()
    for line in log.splitlines():
        if 'TT AE2 SPATIAL CELL AUDIT:' in line or 'TT AE2 MOVABLE AUDIT:' in line or 'TT AE2 LOCOMOTIVE AUDIT:' in line:
            print(line)
    if result.returncode or 'TT AE2 SPATIAL CELL AUDIT: ALL PASS' not in log or 'TT AE2 MOVABLE AUDIT: ALL PASS' not in log or 'TT AE2 LOCOMOTIVE AUDIT: ALL PASS' not in log:
        raise SystemExit(result.returncode or 1)
finally:
    if pack_backup is None:
        pack.unlink(missing_ok=True)
    else:
        pack.write_bytes(pack_backup)
    for target, backup in backups.items():
        if backup is None:
            target.unlink(missing_ok=True)
        else:
            target.write_bytes(backup)
