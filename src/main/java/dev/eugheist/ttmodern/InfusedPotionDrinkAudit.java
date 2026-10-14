package dev.eugheist.ttmodern;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.*;
final class InfusedPotionDrinkAudit {
 private static void check(boolean ok,String label){if(!ok)throw new IllegalStateException("TT infused potion: "+label);}
 static void run(ServerLevel level){
  var player=net.neoforged.neoforge.common.util.FakePlayerFactory.getMinecraft(level);player.getInventory().clearContent();player.getAbilities().instabuild=false;
  var items=java.util.List.of(InfusedPotions.POTION_AIR.get(),InfusedPotions.POTION_FIRE.get(),InfusedPotions.POTION_EARTH.get(),InfusedPotions.POTION_WATER.get());var effects=java.util.List.of(InfusedPotions.AIR,InfusedPotions.FIRE,InfusedPotions.EARTH,InfusedPotions.WATER);
  for(int i=0;i<4;i++){
   player.removeAllEffects();var stack=new ItemStack(items.get(i));check(items.get(i).getDescriptionId(stack).equals(items.get(i).getDescriptionId()),"custom potion name does not append empty effect suffix");var roundTrip=ItemStack.parse(level.registryAccess(),stack.save(level.registryAccess())).orElseThrow();check(roundTrip.is(items.get(i))&&roundTrip.get(net.minecraft.core.component.DataComponents.POTION_CONTENTS).getAllEffects().iterator().next().getDuration()==3600,"native potion component save/load");var contents=stack.get(net.minecraft.core.component.DataComponents.POTION_CONTENTS);check(contents!=null&&contents.getAllEffects().iterator().next().getDuration()==3600,"original duration component");check(stack.getMaxStackSize()==1&&stack.getItem().getUseDuration(stack,player)==32&&stack.getItem().isFoil(stack),"single potion/original drink duration/foil");
   var bottle=stack.getItem().finishUsingItem(stack,level,player);var effect=player.getEffect(effects.get(i));check(effect!=null&&effect.getDuration()==3600&&effect.getAmplifier()==0&&effects.get(i).value().getCategory()==net.minecraft.world.effect.MobEffectCategory.HARMFUL,"native drink applies original rank/category/duration");check(bottle.is(Items.GLASS_BOTTLE)&&bottle.getCount()==1&&stack.isEmpty(),"survival returns exactly one glass bottle");
  }
  player.removeAllEffects();player.getInventory().clearContent();System.out.println("TT INFUSED POTION DRINK AUDIT: ALL PASS (four native potion components,drink32ticks,duration3600/rank0/harmful,foil,stack1,bottle conservation)");
 }
}
