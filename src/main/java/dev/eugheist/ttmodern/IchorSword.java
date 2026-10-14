package dev.eugheist.ttmodern;
import net.minecraft.world.item.*;
/** 1.7 swords reduce positive blockable damage immediately, before armor, from every direction. */
public class IchorSword extends SwordItem {
 public IchorSword(){super(KamiTools.TIER,KamiTools.properties().attributes(SwordItem.createAttributes(KamiTools.TIER,4,-2.4f)));}
 @Override public boolean isFoil(ItemStack stack){return true;}
 @Override public int getUseDuration(ItemStack stack,net.minecraft.world.entity.LivingEntity entity){return 72000;}
 @Override public UseAnim getUseAnimation(ItemStack stack){return UseAnim.BLOCK;}
 @Override public boolean canPerformAction(ItemStack stack,net.neoforged.neoforge.common.ItemAbility ability){return ability!=net.neoforged.neoforge.common.ItemAbilities.SHIELD_BLOCK&&super.canPerformAction(stack,ability);}
 @Override public net.minecraft.world.InteractionResultHolder<ItemStack> use(net.minecraft.world.level.Level level,net.minecraft.world.entity.player.Player player,net.minecraft.world.InteractionHand hand){player.startUsingItem(hand);return net.minecraft.world.InteractionResultHolder.consume(player.getItemInHand(hand));}
 public static float reduce(net.minecraft.world.entity.player.Player player,net.minecraft.world.damagesource.DamageSource source,float damage){return damage>0&&!source.is(net.minecraft.tags.DamageTypeTags.BYPASSES_ARMOR)&&player.isUsingItem()&&player.getUseItem().getItem() instanceof IchorSword?(1+damage)*.5f:damage;}
}
