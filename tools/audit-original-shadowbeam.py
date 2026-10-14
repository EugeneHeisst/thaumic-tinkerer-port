#!/usr/bin/env python3
"""Run extracted original throwable constructor and TT beam heading; print trajectory goldens."""
from pathlib import Path
import subprocess
import tempfile
JAVA = '/usr/lib/jvm/java-21-openjdk/bin/'
REF = Path('/home/eugheist/thaumcraft4-reference')
TT = Path('/home/eugheist/thaumic-tinkerer-reference/build164/thaumic/tinkerer/common/item/kami/foci/ItemFocusShadowbeam.java')
def method(source, marker):
    start = source.index(marker)
    opening = source.index('{', start)
    depth = 1
    end = opening + 1
    while depth:
        depth += (source[end] == '{') - (source[end] == '}')
        end += 1
    return source[start:end]
with tempfile.TemporaryDirectory(prefix='tt-original-shadowbeam-') as temp:
    root = Path(temp)
    subprocess.run([JAVA+'java', '-jar', str(REF/'cfr.jar'), str(REF/'minecraft-1.7.10-client.jar'), '--jarfilter', 'zk', '--outputdir', temp], check=True, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
    source = (root/'zk.java').read_text()
    constructor = method(source, 'public zk(ahb ahb2, sv sv2)')
    heading = method(TT.read_text(), 'public void func_70186_c(')
    # Mechanical MCP aliases only; the original arithmetic is unchanged.
    for old, new in {'func_70186_c':'c', 'MathHelper.func_76133_a':'qh.a', 'field_70159_w':'v', 'field_70181_x':'w', 'field_70179_y':'x'}.items():
        heading = heading.replace(old, new)
    stubs = '''class ahb {} class sa {double s,t,u,v,w,x;float y,z,L;sa(ahb world){} void a(float w,float h){} void b(double x,double y,double z){s=x;t=y;u=z;} void b(double x,double y,double z,float yaw,float pitch){b(x,y,z);this.y=yaw;this.z=pitch;}}
class sv extends sa {sv(){super(new ahb());}float g(){return 1.62f;}}
class qh {static final float[] table=new float[65536];static{for(int i=0;i<65536;i++)table[i]=(float)Math.sin(i*Math.PI*2/65536);}static float a(float v){return table[(int)(v*10430.378f)&65535];}static float b(float v){return table[(int)(v*10430.378f+16384)&65535];}static float a(double v){return (float)Math.sqrt(v);}}
'''
    # Base heading's Gaussian output is overwritten in full by the original override.
    base = 'class zk extends sa {sv g;zk(ahb world){super(world);}' + constructor + 'protected float e(){return 1.5f;}protected float f(){return 0;}public void c(double x,double y,double z,float speed,float noise){} }'
    main = '''public class OriginalShadowbeamOracle extends zk {OriginalShadowbeamOracle(sv p){super(new ahb(),p);v/=10;w/=10;x/=10;}'''+heading+'''public static void main(String[] args){float[][] cases={{0,0},{90,0},{-90,0},{180,0},{45,30},{-135,-60},{359.9f,89.9f}};for(float[] c:cases){sv p=new sv();p.s=12.25;p.t=64;p.u=-7.75;p.y=c[0];p.z=c[1];var b=new OriginalShadowbeamOracle(p);System.out.printf(java.util.Locale.ROOT,"%.9g %.9g %.17g %.17g %.17g %.17g %.17g %.17g%n",c[0],c[1],b.s,b.t,b.u,b.v,b.w,b.x);}}}'''
    (root/'OriginalShadowbeamOracle.java').write_text(stubs+base+main)
    subprocess.run([JAVA+'javac', str(root/'OriginalShadowbeamOracle.java')], check=True)
    subprocess.run([JAVA+'java', '-cp', temp, 'OriginalShadowbeamOracle'], check=True)
