#!/usr/bin/env python3
"""Regenerate lightmap goldens from the unchanged MC1.7.10 renderer method.
Requires the local reference Minecraft client JAR and CFR; no network or server.
The stubs only supply inputs/output storage to the original method, not its math.
"""
from pathlib import Path
import subprocess
import tempfile
JAVA = "/usr/lib/jvm/java-21-openjdk/bin/java"
JAVAC = "/usr/lib/jvm/java-21-openjdk/bin/javac"
REFERENCE = Path("/home/eugheist/thaumcraft4-reference")
HEAD = 'import java.util.*;\nclass rv {static Object r=new Object();}\nclass yz {boolean effect;boolean a(Object x){return effect;}}\nclass Provider {int i=-19;float[] h=new float[16];Provider(){float f=12;for(int n=0;n<=15;n++){float f1=12-n/15f;h[n]=(1-f1)/(f1*3+1)*(1-f)+f;}}}\nclass bjf {Provider t=new Provider();int q;float sun;float b(float partial){return sun;}}\nclass Options {float aG;}\nclass Game {bjf f=new bjf();yz h=new yz();Options u=new Options();}\nclass Texture {void a(){}}\npublic class OriginalLightOracle {Game t=new Game();float f,Z,aa,nv;int[] U=new int[256];Texture T=new Texture();boolean ak;float a(yz player,float partial){return nv;}\n'
TAIL = 'public static void main(String[] args)throws Exception{float[][] cases={{.2f,0,0,0,0},{1,.5f,1,1,0},{.2f,-.5f,.4f,.7f,1},{1,0,0,1,1}};for(var c:cases){var o=new OriginalLightOracle();o.t.f.sun=c[0];o.f=c[1];o.Z=o.aa=c[2];o.nv=c[3];o.t.h.effect=c[3]>0;o.t.u.aG=c[4];o.i(.5f);var bytes=java.nio.ByteBuffer.allocate(1024);for(int pixel:o.U){int nativePixel=0xff000000|(pixel&255)<<16|(pixel&0xff00)|(pixel>>16&255);bytes.putInt(nativePixel);}System.out.println(java.util.HexFormat.of().formatHex(java.security.MessageDigest.getInstance("SHA-256").digest(bytes.array()))+" "+Integer.toHexString(o.U[0])+" "+Integer.toHexString(o.U[255]));}}}\n'
with tempfile.TemporaryDirectory(prefix="tt-original-light-") as temp:
    root = Path(temp)
    subprocess.run([JAVA, "-jar", str(REFERENCE / "cfr.jar"), str(REFERENCE / "minecraft-1.7.10-client.jar"), "--jarfilter", "blt", "--outputdir", temp], check=True, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
    source = (root / "blt.java").read_text()
    start = source.index("    private void i(float f2) {")
    end = source.index("    private float a(yz ", start)
    (root / "OriginalLightOracle.java").write_text(HEAD + source[start:end] + TAIL)
    subprocess.run([JAVAC, str(root / "OriginalLightOracle.java")], check=True)
    subprocess.run([JAVA, "-cp", temp, "OriginalLightOracle"], check=True)
