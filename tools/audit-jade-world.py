"""Hidden, silent integrated-world audit with temporary optional Jade runtime."""
from PIL import Image, ImageChops
from pathlib import Path
import os, shutil, subprocess, sys, zipfile
jar=Path(sys.argv[1]).resolve()
with zipfile.ZipFile(jar) as archive:
    if archive.testzip() is not None: raise SystemExit('Corrupt Jade runtime JAR')
target=Path('run-enchanter-audit/mods/jade-audit.jar')
target.parent.mkdir(parents=True,exist_ok=True)
backup=target.read_bytes() if target.exists() else None
try:
    shutil.copy2(jar,target)
    env=os.environ.copy();env['JAVA_HOME']='/usr/lib/jvm/java-21-openjdk';env['ALSOFT_DRIVERS']='null'
    result=subprocess.run(['timeout','180s','xvfb-run','-a','-s','-screen 0 1280x960x24','./gradlew','runJadeWorldAudit','--console=plain'],env=env,capture_output=True,text=True)
    print(result.stdout,end='');print(result.stderr,end='',file=sys.stderr)
    if result.returncode or 'TT JADE WORLD CLIENT AUDIT: ALL PASS' not in result.stdout: raise SystemExit(result.returncode or 1)
    folder=Path('run-enchanter-audit/enchanter-audit')
    with Image.open(folder/'jade-left-redstone.png') as a, Image.open(folder/'jade-right-autonomous.png') as b:
        if a.size!=(1080,900) or b.size!=a.size: raise SystemExit('Unexpected Jade framebuffer size')
        # Compare only the actual HUD mode lines, excluding world/animations.
        before=a.convert('RGB').crop((340,73,790,135));after=b.convert('RGB').crop((340,73,790,135))
        changed=sum(max(pixel)>30 for pixel in ImageChops.difference(before,after).get_flattened_data())
        if changed<500: raise SystemExit(f'Jade HUD mode text did not visibly update: {changed}')
        print(f'TT JADE HUD PIXEL AUDIT: ALL PASS ({changed} changed mode-line pixels)')
    for facing in ('north','south','west','east'):
        with Image.open(folder/f'tablet-diagonal-{facing}.png') as frame:
            pixels=frame.convert('RGB').crop((250,210,850,440)).get_flattened_data()
            colored=sum(g>70 and b>70 and g>1.8*r and b>1.8*r for r,g,b in pixels)
            if colored<150: raise SystemExit(f'Tablet tool lost visible diamond texture ({facing}): {colored}')
    print('TT TABLET TOOL LIGHT PIXEL AUDIT: ALL PASS (four facings)')
finally:
    if backup is None: target.unlink(missing_ok=True)
    else: target.write_bytes(backup)
