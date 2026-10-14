package dev.eugheist.ttmodern;
import net.minecraft.world.item.*;
import thaumcraft.api.aspects.*;
final class InfusedSeedRecipesAudit {
 private static void check(boolean ok,String label){if(!ok)throw new IllegalStateException("TT seed infusion: "+label);}
 static void run(){
  var aspects=java.util.List.of(Aspect.AIR,Aspect.FIRE,Aspect.EARTH,Aspect.WATER,Aspect.ORDER,Aspect.ENTROPY);int[] indices={0,1,3,2,4,5};
  for(int i=0;i<6;i++){
   var components=new java.util.ArrayList<ItemStack>();for(int j=0;j<4;j++)components.add(new ItemStack(dev.eugheist.tc4modern.TC4Modern.SHARDS.get(indices[i]).get()));var input=new ItemStack(Items.WHEAT_SEEDS);
   var recipe=dev.eugheist.tc4modern.InfusionItemRecipes.match(components,input,java.util.Set.of("INFUSED_POTIONS"));check(recipe!=null&&recipe.key().equals("INFUSED_POTIONS"+i),"native recipe match/shard order "+i);
   check(recipe.instability()==5&&recipe.aspects().size()==2&&recipe.aspects().getAmount(Aspect.CROP)==32&&recipe.aspects().getAmount(Aspect.HARVEST)==32,"original instability and aspect cost");
   check(dev.eugheist.tc4modern.InfusionItemRecipes.match(components,input,java.util.Set.of())==null,"research gate");
   var output=recipe.outputStack(input);check(output.is(ThaumicTinkererModern.INFUSED_SEEDS.get())&&output.getCount()==1&&InfusedSeedData.read(output).mainAspect()==aspects.get(i)&&InfusedSeedData.read(output).tendencies().visSize()==0,"native output keeps seed aspect components");
   new InfusedSeedData(null,new AspectList()).write(output);check(InfusedSeedData.read(recipe.outputStack(input)).mainAspect()==aspects.get(i),"new independent output each craft");
   components.removeLast();check(dev.eugheist.tc4modern.InfusionItemRecipes.match(components,input,java.util.Set.of("INFUSED_POTIONS"))==null,"four separate components required");
  }
  check(InfusedSeedRecipes.output("INFUSED_POTIONS6").isEmpty()&&InfusedSeedRecipes.output("ENCHANTER").isEmpty(),"output patch only exact six keys");
  System.out.println("TT SEED INFUSION AUDIT: ALL PASS (six native recipe matches,original shard order/cost/instability,research gate,component outputs/no alias,four pedestals)");
 }
}
