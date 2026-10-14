package dev.eugheist.ttmodern.mixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(targets="dev.eugheist.tc4modern.InfusionFx",remap=false)
public abstract class SummonFxMixin {
    @Inject(method="handle",at=@At("HEAD"),cancellable=true)
    private static void summon(dev.eugheist.tc4modern.InfusionContent.FxPayload payload,CallbackInfo ci){
        if(payload.kind()==172||payload.kind()==173){dev.eugheist.ttmodern.XPTalismanFx.handle(payload);ci.cancel();}
        if(payload.kind()==171){dev.eugheist.ttmodern.ImbuedFireWisp.spawn(payload.x0(),payload.y0(),payload.z0());ci.cancel();}
        if(payload.kind()==170){dev.eugheist.tc4modern.FociFx.blockSparkle(net.minecraft.core.BlockPos.containing(payload.x0(),payload.y0(),payload.z0()),(int)payload.x1(),(int)payload.y1());ci.cancel();}
        if(payload.kind()==169){var mc=net.minecraft.client.Minecraft.getInstance();if(mc.level!=null)mc.particleEngine.add(new dev.eugheist.tc4modern.NodeConversionBurst(mc.level,payload.x0(),payload.y0(),payload.z0(),(float)payload.x1()));ci.cancel();}
        if(payload.kind()==168){dev.eugheist.ttmodern.EnchanterFx.handle(payload);ci.cancel();}
        if(payload.kind()==167){var mc=net.minecraft.client.Minecraft.getInstance();if(mc.level!=null)mc.particleEngine.add(new dev.eugheist.tc4modern.NodeConversionBurst(mc.level,payload.x0(),payload.y0(),payload.z0(),.2f));ci.cancel();}
        if(payload.kind()==166){dev.eugheist.tc4modern.FociFx.sparkle(payload.x0(),payload.y0(),payload.z0(),0);ci.cancel();}
        if(payload.kind()==165){for(int i=0;i<6;i++)dev.eugheist.tc4modern.FociFx.sparkle(payload.x0(),payload.y0(),payload.z0(),6);ci.cancel();}
        if(payload.kind()==164){dev.eugheist.ttmodern.SummonFx.handle(payload);ci.cancel();}
    }
}
