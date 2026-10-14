package dev.eugheist.ttmodern;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.entity.*;
import net.minecraft.core.BlockPos;
final class FallCombatAudit {
 private static void check(boolean ok,String label){if(!ok)throw new IllegalStateException("TT fall combat: "+label);}
 static void run(ServerLevel level){
  var registry=level.registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT);var pounce=registry.getHolderOrThrow(FallCombatEnchantments.POUNCE);var shock=registry.getHolderOrThrow(FallCombatEnchantments.SHOCKWAVE);var player=net.neoforged.neoforge.common.util.FakePlayerFactory.getMinecraft(level);player.getInventory().clearContent();player.setPos(0,290,0);var floor=player.blockPosition().below();level.setBlockAndUpdate(floor,net.minecraft.world.level.block.Blocks.AIR.defaultBlockState());
  check(pounce.value().getMaxLevel()==5&&shock.value().getMaxLevel()==5,"max ranks");check(!Enchantment.areCompatible(shock,registry.getHolderOrThrow(Enchantments.FEATHER_FALLING))&&!Enchantment.areCompatible(shock,registry.getHolderOrThrow(MovementEnchantments.SLOW_FALL)),"shockwave exclusions");
  var near=EntityType.COW.create(level);var corner=EntityType.COW.create(level);var outside=EntityType.COW.create(level);near.setPos(1,290,0);corner.setPos(9,299,9);outside.setPos(11,290,0);for(var target:java.util.List.of(near,corner,outside)){target.setNoAi(true);level.addFreshEntity(target);}
  player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,new ItemStack(Items.DIAMOND_SWORD));
  for(int rank=1;rank<=5;rank++){
   var legs=new ItemStack(Items.DIAMOND_LEGGINGS);legs.enchant(pounce,rank);player.setItemSlot(EquipmentSlot.LEGS,legs);near.setHealth(10);near.invulnerableTime=0;near.hurt(level.damageSources().playerAttack(player),2);check(Math.abs(near.getHealth()-(10-(float)(2*(1+.25*rank))))<1e-5,"airborne native hit rank"+rank);
   level.setBlockAndUpdate(floor,net.minecraft.world.level.block.Blocks.STONE.defaultBlockState());near.setHealth(10);near.invulnerableTime=0;near.hurt(level.damageSources().playerAttack(player),2);check(near.getHealth()==8,"solid floor suppresses pounce");level.setBlockAndUpdate(floor,net.minecraft.world.level.block.Blocks.AIR.defaultBlockState());
   player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,ItemStack.EMPTY);near.setHealth(10);near.invulnerableTime=0;near.hurt(level.damageSources().playerAttack(player),2);check(near.getHealth()==8,"empty hand suppresses original pounce");player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,new ItemStack(Items.DIAMOND_SWORD));
   var boots=new ItemStack(Items.DIAMOND_BOOTS);boots.enchant(shock,rank);player.setItemSlot(EquipmentSlot.FEET,boots);
   for(float distance:new float[]{3,3.1f,6}){for(var target:java.util.List.of(near,corner,outside)){target.setHealth(10);target.invulnerableTime=0;}float self=player.getHealth();var event=new net.neoforged.neoforge.event.entity.living.LivingFallEvent(player,distance,1);net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(event);float damage=distance>3?.1f*rank*distance:0;check(Math.abs(near.getHealth()-(10-damage))<1e-5&&Math.abs(corner.getHealth()-(10-damage))<1e-5,"cube damage and strict threshold rank"+rank+" distance"+distance);check(outside.getHealth()==10&&player.getHealth()==self&&event.getDistance()==distance&&event.getDamageMultiplier()==1,"outside/self/fall event preserved");}
  }
  for(var target:java.util.List.of(near,corner,outside))target.discard();player.getInventory().clearContent();player.setPos(0,200,0);System.out.println("TT FALL COMBAT AUDIT: ALL PASS (all ranks, real airborne/grounded/empty-hand hits, fall threshold, cube corners/outside, self health, compatibility)");
 }
}
