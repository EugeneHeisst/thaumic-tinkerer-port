package dev.eugheist.ttmodern.mixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(value=dev.eugheist.tc4modern.FortressArmor.class,remap=false)
abstract class KamiArmorProtectionMixin {
 @Inject(method="equipped",at=@At("HEAD"),cancellable=true)private static void equipped(net.minecraft.world.entity.LivingEntity entity,CallbackInfoReturnable<Boolean> cir){if(dev.eugheist.ttmodern.KamiArmorProtection.equipped(entity))cir.setReturnValue(true);}
 @Inject(method="absorb",at=@At("HEAD"),cancellable=true)private static void absorb(net.minecraft.world.entity.LivingEntity entity,net.minecraft.world.damagesource.DamageSource source,float amount,CallbackInfoReturnable<Float> cir){if(dev.eugheist.ttmodern.KamiArmorProtection.equipped(entity))cir.setReturnValue(dev.eugheist.ttmodern.KamiArmorProtection.absorb(entity,source,amount));}
}
