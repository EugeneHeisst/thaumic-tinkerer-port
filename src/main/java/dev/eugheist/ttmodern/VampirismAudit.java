package dev.eugheist.ttmodern;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.entity.EntityType;
final class VampirismAudit {
 private static void check(boolean ok,String label){if(!ok)throw new IllegalStateException("TT vampirism: "+label);}
 static void run(ServerLevel level){
  var registry=level.registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT);var enchant=registry.getHolderOrThrow(VampirismEnchantment.KEY);
  check(enchant.value().getMaxLevel()==2&&!enchant.is(net.minecraft.tags.EnchantmentTags.IN_ENCHANTING_TABLE),"rank and acquisition");
  for(var key:java.util.List.of(Enchantments.KNOCKBACK,Enchantments.FIRE_ASPECT))check(!Enchantment.areCompatible(enchant,registry.getHolderOrThrow(key)),"original exclusion "+key);
  check(Enchantment.areCompatible(enchant,registry.getHolderOrThrow(Enchantments.SHARPNESS))&&Enchantment.areCompatible(enchant,registry.getHolderOrThrow(Enchantments.UNBREAKING)),"ordinary compatible enchants");
  check(!enchant.value().isSupportedItem(new ItemStack(ThaumicTinkererModern.BLOOD_SWORD.get())),"blood sword excluded including anvil");
  var attacker=EntityType.ZOMBIE.create(level);var victim=EntityType.COW.create(level);attacker.setNoAi(true);victim.setNoAi(true);attacker.setPos(20,290,20);victim.setPos(21,290,20);level.addFreshEntity(attacker);level.addFreshEntity(victim);
  for(int rank=1;rank<=2;rank++){
   var sword=new ItemStack(Items.DIAMOND_SWORD);sword.enchant(enchant,rank);attacker.setItemSlot(net.minecraft.world.entity.EquipmentSlot.MAINHAND,sword);attacker.setHealth(10);victim.setHealth(10);victim.invulnerableTime=0;
   check(victim.hurt(level.damageSources().mobAttack(attacker),2),"real mob hit accepted");check(attacker.getHealth()==10+rank&&victim.getHealth()==8,"real hit heals exact rank, damage retained "+rank);
   attacker.setHealth(19);victim.invulnerableTime=0;victim.hurt(level.damageSources().mobAttack(attacker),2);check(attacker.getHealth()==20,"max-health clamp "+rank);
  }
  var arrow=new net.minecraft.world.entity.projectile.Arrow(EntityType.ARROW,level);arrow.setOwner(attacker);attacker.setHealth(10);victim.setHealth(10);victim.invulnerableTime=0;victim.hurt(level.damageSources().arrow(arrow,attacker),1);check(attacker.getHealth()==12,"indirect source uses living owner held sword like original");arrow.discard();
  attacker.setHealth(10);victim.invulnerableTime=0;victim.hurt(level.damageSources().magic(),1);check(attacker.getHealth()==10,"unattributed damage no heal");
  attacker.setItemSlot(net.minecraft.world.entity.EquipmentSlot.MAINHAND,new ItemStack(Items.DIAMOND_SWORD));victim.invulnerableTime=0;victim.hurt(level.damageSources().mobAttack(attacker),1);check(attacker.getHealth()==10,"plain sword no heal");
  var choice=new EnchanterSelection();check(choice.change(level.registryAccess(),new ItemStack(Items.DIAMOND_SWORD),java.util.Set.of("TTENCH_VAMPIRISM"),"ttinkerer:vampirism",1),"research selection");check(!new EnchanterSelection().change(level.registryAccess(),new ItemStack(Items.DIAMOND_SWORD),java.util.Set.of(),"ttinkerer:vampirism",1),"research required");
  attacker.discard();victim.discard();System.out.println("TT VAMPIRISM AUDIT: ALL PASS (real damage/heal, both ranks, health clamp, source/item gates, original exclusivity, blood sword exclusion)");
 }
}
