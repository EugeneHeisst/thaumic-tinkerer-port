package dev.eugheist.ttmodern;
import net.minecraft.world.item.*;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import thaumcraft.api.aspects.Aspect;
final class InfusedCropLootAudit {
 private static void check(boolean ok,String label){if(!ok)throw new IllegalStateException("TT crop loot: "+label);}
 static void run(net.minecraft.server.level.ServerLevel level){
  var loot=InfusedCropLoot.fixed();
  for(var aspect:java.util.List.of(Aspect.AIR,Aspect.FIRE,Aspect.EARTH,Aspect.WATER)){var entry=loot.entries(aspect).getFirst();check(entry.stack().getItem() instanceof InfusedGrainItem item&&item.aspect==aspect&&entry.weight()==1&&entry.stack().getCount()==1,"primal grain "+aspect.getTag());}
  check(loot.entries(Aspect.ORDER).getFirst().stack().is(Items.GLASS)&&loot.entries(Aspect.ORDER).getFirst().stack().getCount()==64&&loot.entries(Aspect.ENTROPY).getFirst().stack().is(Items.SAND),"order/entropy bulk blocks");
  check(loot.entries(Aspect.METAL).getFirst().weight()==100&&loot.entries(Aspect.METAL).getFirst().stack().getCount()==4,"iron weight not count");
  check(loot.entries(Aspect.PLANT).size()==6&&loot.entries(Aspect.ARMOR).size()==4&&loot.entries(Aspect.CLOTH).size()==18,"original sapling/armor/wool variants");
  check(loot.entries(Aspect.CLOTH).get(0).weight()==30&&loot.entries(Aspect.CLOTH).get(1).stack().getCount()==15&&loot.entries(Aspect.CLOTH).get(2).stack().getCount()==4,"cloth duplicate white wool entries retained");
  check(loot.entries(Aspect.MAN).size()==12&&loot.entries(Aspect.MAN).stream().noneMatch(e->e.stack().is(FocusResearch.item("golem_core_blank"))),"original metadata zero through eleven cores exclude blank100");
  check(loot.entries(Aspect.FLESH).size()==2&&loot.entries(Aspect.FLESH).get(0).weight()==4&&loot.entries(Aspect.FLESH).get(1).weight()==1,"duplicate brain entries weights four and one");
  check(loot.entries(Aspect.AURA).size()==Aspect.aspects.size(),"wisp essence for every registered aspect");
  for(var entry:loot.entries(Aspect.AURA)){var stored=dev.eugheist.tc4modern.EssentiaItems.getAspects(entry.stack());check(stored.size()==1&&stored.visSize()==2,"real wisp essence aspect contents");}
  check(loot.entries(Aspect.CRAFT).get(1).stack().is(FocusResearch.item("alchemical_furnace"))&&loot.entries(Aspect.CRAFT).get(1).stack().getCount()==16,"stone device zero furnace mapping");
  var books=InfusedCropLoot.fixed();books.addBooks(level.registryAccess());var mind=books.entries(Aspect.MIND);check(mind.size()==29,"three fixed entries plus twenty-six original books");
  var keys=new java.util.HashSet<String>();for(var entry:mind.subList(3,mind.size())){check(entry.stack().is(Items.ENCHANTED_BOOK)&&entry.weight()==1&&entry.stack().getCount()==1,"book type/count/weight");var enchantments=entry.stack().get(net.minecraft.core.component.DataComponents.STORED_ENCHANTMENTS);check(enchantments!=null&&enchantments.size()==1,"stored enchantment component");for(var enchantment:enchantments.entrySet()){check(enchantment.getIntValue()==1,"original rank one");keys.add(enchantment.getKey().unwrapKey().orElseThrow().location().toString());}}
  check(keys.size()==26&&keys.contains("tc4modern:haste")&&keys.contains("tc4modern:repair")&&!keys.contains("minecraft:mending")&&keys.stream().noneMatch(k->k.startsWith("ttinkerer:")),"legacy book membership excludes modern and tinkerer additions");
  var eggs=loot.entries(Aspect.BEAST);check(eggs.size()==24,"twenty-four original egg metadata entries");for(int i=0;i<24;i++){var egg=eggs.get(i).stack();check(egg.getItem() instanceof LegacyCropEggItem&&egg.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA).copyTag().getInt("legacyEggId")==i,"inert legacy egg ID "+i);check(!(egg.getItem() instanceof SpawnEggItem),"invalid old IDs must not invent modern mobs");}
  var complete=InfusedCropLoot.complete(level.registryAccess());for(var aspect:java.util.List.of(Aspect.BEAST,Aspect.AURA,Aspect.MIND,Aspect.MAN,Aspect.MAGIC,Aspect.EXCHANGE,Aspect.WEATHER))check(!complete.entries(aspect).isEmpty(),"assembled catalog for "+aspect.getTag());check(complete.entries(Aspect.FLIGHT).isEmpty()&&complete.roll(Aspect.FLIGHT,new LegacyRandomSource(0)).isEmpty(),"original unpopulated aspect stays empty safely");
  int pearls=0,eyes=0;for(int i=0;i<15;i++){final int value=i;var random=new LegacyRandomSource(0){@Override public int nextInt(int bound){check(bound==15,"eldritch weight sum");return value;}};var drop=loot.roll(Aspect.ELDRITCH,random);check(drop.getCount()==4,"eldritch stack size");if(drop.is(Items.ENDER_PEARL))pearls++;else if(drop.is(Items.ENDER_EYE))eyes++;else throw new IllegalStateException("unexpected eldritch result");}
  check(pearls==11&&eyes==4,"original inclusive boundary retained with explicit order");
  var copy=loot.entries(Aspect.METAL).getFirst().stack();copy.setCount(1);check(loot.entries(Aspect.METAL).getFirst().stack().getCount()==4,"catalog stack defensive copy");
  var same=new InfusedCropLoot();same.add(Aspect.AIR,new ItemStack(Items.PAPER),1);same.add(Aspect.AIR,new ItemStack(Items.PAPER),1);check(same.entries(Aspect.AIR).size()==2,"identity-distinct duplicates remain separate");
  check(loot.roll(null,new LegacyRandomSource(0)).isEmpty()&&loot.roll(Aspect.WEATHER,new LegacyRandomSource(0)).isEmpty(),"missing aspect and original weather empty drop");
  System.out.println("TT CROP FIXED LOOT AUDIT: ALL PASS (original primal/fixed quantities,weights,legacy boundary,duplicate entries,variant families,defensive copies,empty weather)");
 }
}
