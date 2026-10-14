package dev.eugheist.ttmodern.mixin;
/** LivingIncomingDamageEvent precedes cooldown/shield checks. Original LivingHurt ran here. */
@org.spongepowered.asm.mixin.Mixin(value=net.minecraft.world.entity.player.Player.class,priority=900)
abstract class SoulHeartsDamageMixin extends net.minecraft.world.entity.LivingEntity {
 protected SoulHeartsDamageMixin(net.minecraft.world.entity.EntityType<? extends net.minecraft.world.entity.LivingEntity> type,net.minecraft.world.level.Level level){super(type,level);}
 @org.spongepowered.asm.mixin.injection.Inject(method="actuallyHurt",at=@org.spongepowered.asm.mixin.injection.At("HEAD"))
 private void souls(net.minecraft.world.damagesource.DamageSource source,float amount,org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci){if(!level().isClientSide&&!isInvulnerableTo(source)&&!damageContainers.empty()){var container=damageContainers.peek();var player=(net.minecraft.world.entity.player.Player)(Object)this;float remaining=dev.eugheist.ttmodern.SoulHearts.absorb(player,container.getNewDamage());container.setNewDamage(dev.eugheist.ttmodern.IchorSword.reduce(player,source,remaining));}}
}
