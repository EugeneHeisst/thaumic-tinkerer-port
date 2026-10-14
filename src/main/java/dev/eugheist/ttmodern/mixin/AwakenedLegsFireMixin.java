package dev.eugheist.ttmodern.mixin;
@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.entity.player.Player.class)
abstract class AwakenedLegsFireMixin {
 @org.spongepowered.asm.mixin.injection.Inject(method="actuallyHurt",at=@org.spongepowered.asm.mixin.injection.At("HEAD"),cancellable=true)
 private void fire(net.minecraft.world.damagesource.DamageSource source,float amount,org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci){if(dev.eugheist.ttmodern.AwakenedLegs.healFire((net.minecraft.world.entity.LivingEntity)(Object)this,source,amount))ci.cancel();}
}
