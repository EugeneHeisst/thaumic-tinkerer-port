package dev.eugheist.ttmodern.mixin;
@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.entity.player.Player.class)
abstract class AwakenedBootsAirMixin {
 @org.spongepowered.asm.mixin.injection.Inject(method="getFlyingSpeed",at=@org.spongepowered.asm.mixin.injection.At("RETURN"),cancellable=true)
 private void air(org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable<Float> cir){var p=(net.minecraft.world.entity.player.Player)(Object)this;if(dev.eugheist.ttmodern.AwakenedBoots.active(p)&&(!p.getAbilities().flying||p.isPassenger()))cir.setReturnValue(dev.eugheist.ttmodern.AwakenedBoots.airSpeed(p));}
}
