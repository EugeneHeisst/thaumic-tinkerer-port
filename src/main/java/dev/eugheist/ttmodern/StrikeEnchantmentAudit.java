package dev.eugheist.ttmodern;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
final class StrikeEnchantmentAudit {
 private static void check(boolean ok,String label){if(!ok)throw new IllegalStateException("TT strike enchants: "+label);}
 static void run(ServerLevel level){
  var registry=level.registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT);
  var attacker=EntityType.ZOMBIE.create(level);var victim=EntityType.COW.create(level);var other=EntityType.COW.create(level);attacker.setNoAi(true);victim.setNoAi(true);other.setNoAi(true);attacker.setPos(25,290,25);victim.setPos(26,290,25);other.setPos(27,290,25);level.addFreshEntity(attacker);level.addFreshEntity(victim);level.addFreshEntity(other);victim.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH).setBaseValue(200);other.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH).setBaseValue(200);
  for(var key:java.util.List.of(StrikeEnchantments.FOCUSED,StrikeEnchantments.DISPERSED)){
   var enchant=registry.getHolderOrThrow(key);check(enchant.value().getMaxLevel()==5,"rank max");
   for(var allowed:java.util.List.of(Enchantments.UNBREAKING,Enchantments.KNOCKBACK,Enchantments.LOOTING))check(Enchantment.areCompatible(enchant,registry.getHolderOrThrow(allowed)),"allowed enchant "+allowed);
   check(!Enchantment.areCompatible(enchant,registry.getHolderOrThrow(Enchantments.SHARPNESS))&&!Enchantment.areCompatible(enchant,registry.getHolderOrThrow(VampirismEnchantment.KEY)),"forbidden enchants");
   for(int rank=1;rank<=5;rank++){
    var sword=new ItemStack(Items.DIAMOND_SWORD);sword.enchant(enchant,rank);CustomData.update(DataComponents.CUSTOM_DATA,sword,t->t.putString("unrelated","preserved"));attacker.setItemSlot(net.minecraft.world.entity.EquipmentSlot.MAINHAND,sword);
    for(int hit=0;hit<4;hit++){
     victim.setHealth(100);victim.invulnerableTime=0;check(victim.hurt(level.damageSources().mobAttack(attacker),6),"real hit accepted");float expected=key==StrikeEnchantments.FOCUSED?(hit==0?3:3+1.5f*rank):(hit==0?6:2);check(Math.abs(victim.getHealth()-(100-expected))<1e-5,"exact damage rank"+rank+" hit"+hit+" "+key);
     var data=sword.get(DataComponents.CUSTOM_DATA).copyTag();check(data.getInt("TTEnchantSuccessiveStrike")==hit&&data.getInt("TTEnchantLastTarget")==victim.getId()&&data.getString("unrelated").equals("preserved"),"counter and unrelated data");
    }
    other.setHealth(100);other.invulnerableTime=0;other.hurt(level.damageSources().mobAttack(attacker),6);check(other.getHealth()==(key==StrikeEnchantments.FOCUSED?97:94)&&sword.get(DataComponents.CUSTOM_DATA).copyTag().getInt("TTEnchantSuccessiveStrike")==0,"target switch resets sequence");
    var saved=ItemStack.parseOptional(level.registryAccess(),(net.minecraft.nbt.CompoundTag)sword.save(level.registryAccess()));check(saved.get(DataComponents.CUSTOM_DATA).equals(sword.get(DataComponents.CUSTOM_DATA)),"sequence persists in actual stack codec");
   }
  }
  for(var id:java.util.List.of("ttinkerer:focused_strike","ttinkerer:dispersed_strikes")){
   check(new EnchanterSelection().change(level.registryAccess(),new ItemStack(Items.DIAMOND_SWORD),java.util.Set.of("TTENCH_VAMPIRISM"),id,1),"original machine vampirism gate "+id);
   check(!new EnchanterSelection().change(level.registryAccess(),new ItemStack(Items.DIAMOND_SWORD),java.util.Set.of("TTENCH_FOCUSED","TTENCH_DISPERSED"),id,1),"original catalog gate preserved "+id);
  }
  var category=thaumcraft.api.research.ResearchCategories.getResearch("TTENCH_FOCUSED");check(category!=null&&java.util.Arrays.asList(category.parents).contains("TTENCH_VAMPIRISM")&&category.isSecondary(),"original book progression");
  check(!Enchantment.areCompatible(registry.getHolderOrThrow(StrikeEnchantments.FOCUSED),registry.getHolderOrThrow(StrikeEnchantments.DISPERSED)),"mutual exclusion");attacker.discard();victim.discard();other.discard();System.out.println("TT STRIKE ENCHANT AUDIT: ALL PASS (both enchants/all ranks, real repeated hits, exact legacy local-counter behavior, target switch, persistence, compatibility)");
 }
}
