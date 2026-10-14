package dev.eugheist.ttmodern;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.*;
final class QuickDrawAudit {
 private static void check(boolean ok,String label){if(!ok)throw new IllegalStateException("TT quick draw: "+label);}
 static void run(ServerLevel level){
  var registry=level.registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT);var enchant=registry.getHolderOrThrow(QuickDrawEnchantment.KEY);
  check(enchant.value().getMaxLevel()==2&&!enchant.is(net.minecraft.tags.EnchantmentTags.IN_ENCHANTING_TABLE),"rank and acquisition");
  check(!Enchantment.areCompatible(enchant,registry.getHolderOrThrow(Enchantments.PUNCH))&&Enchantment.areCompatible(enchant,registry.getHolderOrThrow(Enchantments.POWER)),"punch excluded, power allowed");
  var player=net.neoforged.neoforge.common.util.FakePlayerFactory.getMinecraft(level);player.getInventory().clearContent();
  for(int rank=1;rank<=2;rank++){
   var bow=new ItemStack(Items.BOW);bow.enchant(enchant,rank);player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,bow);int maximum=bow.getUseDuration(player),remaining=maximum;
   for(int tick=0;tick<20;tick++){int before=remaining;var event=new net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent.Tick(player,bow,remaining);net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(event);int expected=before-((maximum-before)%(6-rank)==0?1:0);check(event.getDuration()==expected,"original cadence rank"+rank+" tick"+tick);remaining=event.getDuration()-1;}
   check(maximum-remaining>20&&bow.getDamageValue()==0,"draw accelerated without wear");
  }
  var plain=new ItemStack(Items.BOW);player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,plain);var event=new net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent.Tick(player,plain,72000);net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(event);check(event.getDuration()==72000,"plain bow unaffected");
  var sword=new ItemStack(Items.DIAMOND_SWORD);sword.enchant(enchant,2);player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,sword);event=new net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent.Tick(player,sword,72000);net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(event);check(event.getDuration()==72000,"non-bow unaffected");
  var selection=new EnchanterSelection();check(selection.change(level.registryAccess(),plain,java.util.Set.of("TTENCH_QUICK_DRAW"),"ttinkerer:quick_draw",1),"researched bow selection");check(!new EnchanterSelection().change(level.registryAccess(),plain,java.util.Set.of(),"ttinkerer:quick_draw",1),"research gate");
  player.getInventory().clearContent();System.out.println("TT QUICK DRAW AUDIT: ALL PASS (rank, exclusivity, native use events, exact cadence, research, bow-only)");
 }
}
