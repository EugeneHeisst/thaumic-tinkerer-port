"""Hidden, silent integrated-world audit with temporary optional CC runtime."""
from PIL import Image
from pathlib import Path
import os, shutil, subprocess, sys, zipfile
jar=Path(sys.argv[1]).resolve()
with zipfile.ZipFile(jar) as archive:
    if archive.testzip() is not None: raise SystemExit('Corrupt CC runtime JAR')
target=Path('run-enchanter-audit/mods/cc-tweaked-audit.jar')
target.parent.mkdir(parents=True,exist_ok=True)
backup=target.read_bytes() if target.exists() else None
try:
    shutil.copy2(jar,target)
    env=os.environ.copy();env['JAVA_HOME']='/usr/lib/jvm/java-21-openjdk';env['ALSOFT_DRIVERS']='null'
    result=subprocess.run(['timeout','180s','xvfb-run','-a','-s','-screen 0 1280x960x24','./gradlew','runComputerWorldAudit','--console=plain'],env=env,capture_output=True,text=True)
    print(result.stdout,end='');print(result.stderr,end='',file=sys.stderr)
    if result.returncode or 'TT COMPUTER WORLD CLIENT AUDIT: ALL PASS' not in result.stdout: raise SystemExit(result.returncode or 1)
    folder=Path('run-enchanter-audit/enchanter-audit')
    def earth_pixels(name):
        with Image.open(folder/name) as screenshot:
            if screenshot.size!=(1080,900): raise SystemExit('Unexpected audit framebuffer size')
            # GUI scale 3, left92/top67, first aspect at +20/+58; actual rendered green terra icon.
            pixels=screenshot.convert('RGB').crop((336,375,384,423)).get_flattened_data()
            return sum(g>r+30 and g>b+30 for r,g,b in pixels)
    before=earth_pixels('analyzer-unscanned.png');after=earth_pixels('analyzer-scanned.png')
    if before>20 or after<100: raise SystemExit(f'Analyzer scan visibility pixel failure: {before} -> {after}')
    print(f'TT ANALYZER PIXEL AUDIT: ALL PASS (green terra icon pixels {before} -> {after})')
finally:
    if backup is None: target.unlink(missing_ok=True)
    else: target.write_bytes(backup)
