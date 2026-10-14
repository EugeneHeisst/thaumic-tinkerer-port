package dev.eugheist.ttmodern;
/** Original pair of purple FXBlockRunes at the pillar and enchanter. */
public final class EnchanterFx {
    static int received;
    public static void handle(dev.eugheist.tc4modern.InfusionContent.FxPayload payload){
        var level=net.minecraft.client.Minecraft.getInstance().level;if(level==null)return;received++;
        var r=level.random;
        dev.eugheist.tc4modern.ScanRunes.blockRunes(level,payload.x1(),payload.y1(),payload.z1(),.3f+r.nextFloat()*.7f,0,.3f+r.nextFloat()*.7f,15,r.nextFloat()/8);
        dev.eugheist.tc4modern.ScanRunes.blockRunes(level,payload.x0(),payload.y0(),payload.z0(),.3f+r.nextFloat()*.7f,0,.3f+r.nextFloat()*.7f,15,r.nextFloat()/8);
    }
}
