from pathlib import Path
import subprocess

root = Path(__file__).resolve().parent.parent
config = root / 'run-client-audit/config/ttinkerer-startup.toml'
config.parent.mkdir(parents=True, exist_ok=True)
backup = config.read_bytes() if config.exists() else None
try:
    config.write_text('[general.kami]\nforceenabled = false\n')
    result = subprocess.run(['./audit-client.sh'], cwd=root, text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
    print(result.stdout)
    if result.returncode or 'TT KAMI DISABLED CLIENT AUDIT: ALL PASS' not in result.stdout:
        raise SystemExit(1)
finally:
    if backup is None:
        config.unlink(missing_ok=True)
    else:
        config.write_bytes(backup)
