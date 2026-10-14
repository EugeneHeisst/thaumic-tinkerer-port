package dev.eugheist.ttmodern;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.entity.EntityType;
final class CriticalEnchantmentAudit {
 private static void check(boolean ok,String label){if(!ok)throw new IllegalStateException("TT critical enchants: "+label);}
 static void run(ServerLevel level){
  var registry=level.registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT);
  var attacker=EntityType.ZOMBIE.create(level);var victim=EntityType.COW.create(level);attacker.setNoAi(true);victim.setNoAi(true);attacker.setPos(30,290,30);victim.setPos(31,290,30);level.addFreshEntity(attacker);level.addFreshEntity(victim);victim.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH).setBaseValue(200);
  for(var key:java.util.List.of(CriticalEnchantments.VALIANCE,CriticalEnchantments.FINAL)){
   var enchant=registry.getHolderOrThrow(key);check(enchant.value().getMaxLevel()==5&&!enchant.is(net.minecraft.tags.EnchantmentTags.IN_ENCHANTING_TABLE),"rank/acquisition");
   for(var allowed:java.util.List.of(Enchantments.UNBREAKING,Enchantments.SHARPNESS,Enchantments.SMITE))check(Enchantment.areCompatible(enchant,registry.getHolderOrThrow(allowed)),"allowed "+allowed);
   for(var denied:java.util.List.of(Enchantments.BANE_OF_ARTHROPODS,Enchantments.KNOCKBACK,Enchantments.FIRE_ASPECT))check(!Enchantment.areCompatible(enchant,registry.getHolderOrThrow(denied)),"denied "+denied);
  }
  for(int rank=1;rank<=5;rank++){
   final int r=rank;for(int roll=0;roll<20-rank;roll++){final int value=roll;check(CriticalEnchantments.damage(6,r,0,20,20,bound->{check(bound==20-r,"exact RNG bound");return value;})==(roll==0?18:6),"every chance outcome rank"+rank);}
   var sword=new ItemStack(Items.DIAMOND_SWORD);sword.enchant(registry.getHolderOrThrow(CriticalEnchantments.VALIANCE),rank);attacker.setItemSlot(net.minecraft.world.entity.EquipmentSlot.MAINHAND,sword);
   for(float health:new float[]{9,10,11}){attacker.setHealth(health);victim.setHealth(100);victim.invulnerableTime=0;check(victim.hurt(level.damageSources().mobAttack(attacker),6),"native valiance hit");float expected=health<10?(float)(6*(1+.1*rank)):6;check(Math.abs(victim.getHealth()-(100-expected))<1e-5,"strict half-health threshold rank"+rank+" hp"+health);}
   sword=new ItemStack(Items.DIAMOND_SWORD);sword.enchant(registry.getHolderOrThrow(CriticalEnchantments.FINAL),rank);attacker.setItemSlot(net.minecraft.world.entity.EquipmentSlot.MAINHAND,sword);attacker.setHealth(20);
   for(int hit=0;hit<40;hit++){victim.setHealth(100);victim.invulnerableTime=0;victim.hurt(level.damageSources().mobAttack(attacker),6);check(victim.getHealth()==94||victim.getHealth()==82,"native random outcome remains exact rank"+rank);}
  }
  check(CriticalEnchantments.damage(6,5,5,9,20,b->0)==27,"legacy final-before-valiance order");
  var finalResearch=thaumcraft.api.research.ResearchCategories.getResearch("TTENCH_FINAL");check(java.util.Arrays.asList(finalResearch.parents).equals(java.util.List.of("TTENCH_FOCUSED","TTENCH_DISPERSED")),"final research requires both strikes");
  for(var id:java.util.List.of("ttinkerer:valiance","ttinkerer:final_strike"))check(new EnchanterSelection().change(level.registryAccess(),new ItemStack(Items.DIAMOND_SWORD),java.util.Set.of("TTENCH_VAMPIRISM"),id,1),"original machine gate "+id);
  attacker.discard();victim.discard();System.out.println("TT CRITICAL ENCHANT AUDIT: ALL PASS (all ranks/all RNG outcomes, real damage, strict half-health threshold, compatibility, research, legacy order)");
 }
}
