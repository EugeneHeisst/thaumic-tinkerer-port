"""Hidden silent actual first-person held frames, optional isolated CC runtime."""
from pathlib import Path
import os,subprocess,shutil,sys
from PIL import Image,ImageChops,ImageDraw
root=Path(__file__).resolve().parent.parent
runtime=root/'run-enchanter-audit/mods/cc-tweaked-audit.jar'
backup=runtime.read_bytes() if runtime.exists() else None
try:
    if len(sys.argv)>1:
        runtime.parent.mkdir(parents=True,exist_ok=True);shutil.copy2(Path(sys.argv[1]).resolve(),runtime)
    env=os.environ.copy();env['JAVA_HOME']='/usr/lib/jvm/java-21-openjdk';env['ALSOFT_DRIVERS']='null'
    run=subprocess.run(['timeout','180s','xvfb-run','-a','-s','-screen 0 1280x960x24','./gradlew','runHeldBlocksWorldAudit','--console=plain'],cwd=root,env=env,capture_output=True,text=True)
    print(run.stdout,end='');print(run.stderr,end='')
    if run.returncode or 'TT HELD BLOCKS WORLD CLIENT AUDIT: ALL PASS' not in run.stdout:raise SystemExit(run.returncode or 1)
    folder=root/'run-enchanter-audit/enchanter-audit'
    rows=[line.split('\t') for line in (folder/'held-blocks.tsv').read_text().splitlines()]
    names=sorted({row[1] for row in rows if row[1]!='empty'})
    assert len(rows)==2*(len(names)+1) and len(names)>=24
    assert len({row[0] for row in rows})==len(rows)
    base={hand:Image.open(folder/f'held-empty-{hand}.png').convert('RGB') for hand in ['right','left']}
    sheet=Image.new('RGB',(720,len(names)*220),'#202020');draw=ImageDraw.Draw(sheet)
    for n,name in enumerate(names):
        for col,hand in enumerate(['right','left']):
            selected=[r for r in rows if r[1]==name and r[2]==hand];assert len(selected)==1
            frame=Image.open(folder/selected[0][0]).convert('RGB');assert frame.size==(1080,900)
            crop=(580,300,1080,900) if hand=='right' else (0,300,500,900)
            diff=ImageChops.difference(frame.crop(crop),base[hand].crop(crop))
            changed=sum(max(pixel)>24 for pixel in diff.getdata())
            assert changed>200,(name,hand,'invisible held item',changed)
            # Compare to same-row sky on the opposite, empty side. Empty-hand
            # subtraction alone can pass merely because the player arm vanished.
            outer=range(820,1080) if hand=='right' else range(0,260)
            sky_x=40 if hand=='right' else 1040
            foreground=sum(not (b>150 and b>g>r and b-r>40) for y in range(350,900) for x in outer for r,g,b in [frame.getpixel((x,y))])
            assert foreground>200,(name,hand,'no actual held foreground',foreground)
            sheet.paste(frame.crop(crop).resize((200,200)),(col*360,n*220+20));draw.text((col*360,n*220),name+' '+hand,fill='white')
    sheet.save(folder/'held-blocks-contact.png')
    print(f'TT HELD BLOCKS WORLD PIXEL AUDIT: ALL PASS ({len(names)} items, both first-person hands nonempty difference plus non-sky foreground (bright-blue sky excluded); contact sheet for manual review)')
finally:
    if len(sys.argv)>1:
        if backup is None:runtime.unlink(missing_ok=True)
        else:runtime.write_bytes(backup)
