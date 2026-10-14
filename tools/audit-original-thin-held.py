"""Trace unmodified MC1.7.10 held transforms and compare their plate projection.
No original source is copied into the repository; local reference JAR/CFR required.
This proves geometry/projection, not lighting or complete texture raster fidelity.
"""
from pathlib import Path
import math,subprocess,tempfile,json
from PIL import Image,ImageDraw,ImageChops,ImageFilter
root=Path(__file__).resolve().parent.parent
reference=Path('/home/eugheist/thaumcraft4-reference')
java='/usr/lib/jvm/java-21-openjdk/bin/java'
with tempfile.TemporaryDirectory(prefix='tt-original-thin-held-') as temp:
    subprocess.run([java,'-jar',str(reference/'cfr.jar'),str(reference/'minecraft-1.7.10-client.jar'),'--jarfilter','blm|bly','--outputdir',temp],check=True,stdout=subprocess.DEVNULL,stderr=subprocess.DEVNULL)
    item=(Path(temp)/'bly.java').read_text();block=(Path(temp)/'blm.java').read_text()
    # Original resting (fully equipped/no swing) branch, before block renderer.
    for snippet in ['float f19 = 0.8f;', '(0.7f * f19)', '(-0.65f * f19 - (1.0f - f6) * 0.6f)', '(-0.9f * f19)', 'glRotatef((float)45.0f', 'f18 = 0.4f;', 'glScalef((float)f18']:
        assert snippet in item,('Original held transform changed',snippet)
    start=block.index('    public void a(aji aji2, int n2, float f2) {')
    normal=block[start:block.index('bmh2.b();',start)]
    assert 'aji2.g();' in normal and 'glRotatef((float)90.0f' in normal
    assert 'glTranslatef((float)-0.5f, (float)-0.5f, (float)-0.5f)' in normal
    bytecode=subprocess.check_output(['/usr/lib/jvm/java-21-openjdk/bin/javap','-c','-classpath',str(reference/'minecraft-1.7.10-client.jar'),'aji'],text=True)
    assert 'public void g();\n    Code:\n       0: return' in bytecode,'Original item-bounds hook is no longer no-op'
    ttjar='/home/eugheist/Downloads/ThaumicTinkerer_2.5_1.7.10_164.jar'
    for name in ['BlockFunnel','BlockAnimationTablet','BlockSummon']:
        code=subprocess.check_output(['/usr/lib/jvm/java-21-openjdk/bin/javap','-c','-classpath',ttjar,'thaumic.tinkerer.common.block.'+name],text=True)
        assert 'float 0.125f' in code and 'func_149683_g' not in code,(name,'original item bounds override')
# Original model-to-view: T(.56,-.52,-.72) RY45 S(.4) RY90 T(-.5).
# FOV70 and1080x900 match captured native camera. At rest all view-arm deltas are0.
polygon=[]
for x,z in [(0,0),(1,0),(1,1),(0,1)]:
    x-=.5;z-=.5;angle=math.radians(135)
    vx=.56+.4*(math.cos(angle)*x+math.sin(angle)*z)
    vz=-.72+.4*(-math.sin(angle)*x+math.cos(angle)*z);vy=-.52+.4*(.125-.5)
    polygon.append((540*(1+vx/(-vz*math.tan(math.radians(35))*1.2)),450*(1-vy/(-vz*math.tan(math.radians(35))))))
oracle=Image.new('L',(1080,900));ImageDraw.Draw(oracle).polygon(polygon,fill=255)
folder=root/'audits/held-blocks-firstperson-cc';report=[]
for name in ['animation_tablet','funnel','summon_tablet']:
    for hand in ['right','left']:
        frame=Image.open(folder/f'held-{name}-{hand}.png').convert('RGB')
        mask=Image.new('L',frame.size);pixels=mask.load();outer=range(820,1080) if hand=='right' else range(0,260);sx=40 if hand=='right' else 1040
        for y in range(350,900):
            for x in outer:
                r,g,b=frame.getpixel((x,y))
                # The sky has a horizontal gradient, so the far opposite sample
                # alone is insufficient. These three opaque plate textures are
                # distinguishable from the bright-blue sky by their RGB palette.
                if not (b>150 and b>g>r and b-r>40):pixels[x,y]=255
        expected=oracle if hand=='right' else oracle.transpose(Image.Transpose.FLIP_LEFT_RIGHT)
        # GL triangle rasterization vs Pillow integer polygon rounding: allow1pixel edge.
        assert ImageChops.subtract(mask,expected.filter(ImageFilter.MaxFilter(3))).getbbox() is None,(name,hand,'outside original silhouette')
        assert ImageChops.subtract(expected,mask.filter(ImageFilter.MaxFilter(3))).getbbox() is None,(name,hand,'missing original silhouette')
        report.append({'item':name,'hand':hand,'actual_bounds':mask.getbbox(),'original_bounds':expected.getbbox(),'actual_pixels':sum(v>0 for v in mask.getdata())})
(folder/'original-thin-held-projection.png').parent.mkdir(exist_ok=True)
oracle.save(folder/'original-thin-held-projection.png')
(folder/'original-thin-held-projection.json').write_text(json.dumps(report,indent=2)+'\n')
print('TT ORIGINAL THIN HELD PROJECTION AUDIT: ALL PASS (original JAR transforms/no-op bounds hook,3 original item bounds,6 actual first-person silhouettes within1pixel; low placement matches original geometry)')
