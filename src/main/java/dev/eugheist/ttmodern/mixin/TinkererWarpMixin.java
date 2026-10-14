package dev.eugheist.ttmodern.mixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(value=dev.eugheist.tc4modern.ResearchWarp.class,remap=false)
abstract class TinkererWarpMixin{
    @Inject(method="amount",at=@At("HEAD"),cancellable=true)
    private static void addon(String key,CallbackInfoReturnable<Integer> cir){if(key.equals("ICHORCLOTH_ARMOR"))cir.setReturnValue(4);if(key.equals("ICHOR_TOOLS"))cir.setReturnValue(2);if(key.equals("CAP_ICHOR"))cir.setReturnValue(2);if(key.equals("ROD_ICHORCLOTH"))cir.setReturnValue(1);if(key.equals("ICHOR"))cir.setReturnValue(5);if(key.equals("FOCUS_ENDER_CHEST"))cir.setReturnValue(1);if(key.equals("ANIMATION_TABLET"))cir.setReturnValue(1);if(key.equals("SUMMON"))cir.setReturnValue(3);if(key.equals("BLOOD_SWORD"))cir.setReturnValue(1);}
}
