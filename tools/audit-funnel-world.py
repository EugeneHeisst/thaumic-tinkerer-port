"""Isolated hidden silent integrated-world funnel render audit."""
import os,subprocess
from pathlib import Path
root=Path(__file__).resolve().parent.parent
env=os.environ.copy();env['JAVA_HOME']='/usr/lib/jvm/java-21-openjdk';env['ALSOFT_DRIVERS']='null'
result=subprocess.run(['timeout','180s','xvfb-run','-a','-s','-screen 0 1280x960x24','./gradlew','runFunnelWorldAudit','--console=plain'],cwd=root,env=env,capture_output=True,text=True)
print(result.stdout,end='');print(result.stderr,end='')
if result.returncode or 'TT FUNNEL WORLD CLIENT AUDIT: ALL PASS' not in result.stdout or 'TT FUNNEL CLIENT INTERACTION MODES AUDIT: ALL PASS' not in result.stdout:raise SystemExit(result.returncode or 1)

from PIL import Image,ImageChops
folder=root/'run-enchanter-audit/enchanter-audit'
base=Image.open(folder/'funnel-empty.png').convert('RGB')
for name in ('jar-empty','jar-filled','jar-void'):
    with Image.open(folder/f'funnel-{name}.png') as frame:
        assert frame.size==(1080,900),'Unexpected funnel framebuffer'
        crop=(420,270,660,440)
        delta=ImageChops.difference(base.crop(crop),frame.convert('RGB').crop(crop))
        points=[(i%240,i//240) for i,pixel in enumerate(delta.get_flattened_data()) if max(pixel)>35]
        assert points,('Missing jar',name)
        width=max(x for x,y in points)-min(x for x,y in points)+1
        top=min(y for x,y in points)
        assert 150<=width<=190 and 20<=top<=60,('Jar scale/height regression',name,width,top)
with Image.open(folder/'funnel-jar-empty.png') as empty,Image.open(folder/'funnel-jar-filled.png') as full,Image.open(folder/'funnel-jar-void.png') as void:
    crop=(420,270,660,490)
    diff=lambda a,b:sum(max(pixel)>35 for pixel in ImageChops.difference(a.convert('RGB').crop(crop),b.convert('RGB').crop(crop)).get_flattened_data())
    assert diff(empty,full)>5000,'Filled contents/label did not visibly render'
    assert diff(full,void)>500,'Void jar texture not visibly distinct'
print('TT FUNNEL WORLD PIXEL AUDIT: ALL PASS (three full-sized jars,original height,visible contents/label,void texture)')
