package dev.eugheist.ttmodern;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.*;
import thaumcraft.api.aspects.Aspect;
import dev.eugheist.tc4modern.WandData;
final class EnchanterAddonCatalogAudit {
 private static void check(boolean ok,String label){if(!ok)throw new IllegalStateException("TT addon catalog: "+label);}
 static void run(ServerLevel level){
  var registry=level.registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT);int count=0;
  for(var entry:EnchanterCosts.ENTRIES.entrySet()){
   if(!entry.getKey().startsWith("ttinkerer:"))continue;count++;var holder=registry.getHolder(net.minecraft.resources.ResourceLocation.parse(entry.getKey())).orElseThrow();
   var item=switch(entry.getKey()){case "ttinkerer:ascent_boost","ttinkerer:pounce"->Items.DIAMOND_LEGGINGS;case "ttinkerer:slow_fall","ttinkerer:shockwave"->Items.DIAMOND_BOOTS;case "ttinkerer:auto_smelt","ttinkerer:desintegrate","ttinkerer:tunnel","ttinkerer:shatter"->Items.DIAMOND_PICKAXE;case "ttinkerer:quick_draw"->Items.BOW;default->Items.DIAMOND_SWORD;};
   var tool=new ItemStack(item);tool.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME,net.minecraft.network.chat.Component.literal("Catalog "+count));var process=new EnchanterProcess();var research=java.util.Set.of(entry.getValue().research());check(process.change(level,tool,research,entry.getKey(),1)&&process.change(level,tool,research,entry.getKey(),holder.value().getMaxLevel())&&process.start(),"select/start "+entry.getKey());
   var total=process.selection.total();var wand=new ItemStack(dev.eugheist.tc4modern.TC4Modern.GOLD_GREATWOOD_WAND.get());var primals=Aspect.getPrimalAspects().toArray(Aspect[]::new);int[] spent=new int[primals.length];int ticks=0;
   while(process.working()&&ticks++<5000){int[] before=new int[primals.length];for(int i=0;i<primals.length;i++){WandData.add(wand,primals[i],50,true);before[i]=WandData.get(wand,primals[i]);}process.tick(level,tool,wand,true);for(int i=0;i<primals.length;i++)spent[i]+=before[i]-WandData.get(wand,primals[i]);}
   check(!process.working()&&tool.getEnchantmentLevel(holder)==holder.value().getMaxLevel(),"applied max rank "+entry.getKey());check(tool.getHoverName().getString().equals("Catalog "+count),"name preserved");
   for(int i=0;i<primals.length;i++)check(spent[i]==total.getAmount(primals[i])*(int)(100*WandData.craftingModifier(wand,null,primals[i])),"exact paid primal cost "+entry.getKey()+" "+primals[i]);
   var snapshot=tool.copy();process.tick(level,tool,wand,true);check(ItemStack.isSameItemSameComponents(tool,snapshot),"one-time completion "+entry.getKey());
  }
  check(count==14,"all fourteen original addon enchantments covered");System.out.println("TT ADDON CATALOG AUDIT: ALL PASS (all14 registered, research/item selection, max ranks, actual paid process, exact primal costs, component preservation, completion once)");
 }
}
