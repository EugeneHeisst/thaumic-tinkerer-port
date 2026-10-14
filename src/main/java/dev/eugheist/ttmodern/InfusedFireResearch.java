package dev.eugheist.ttmodern;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import thaumcraft.api.aspects.*;
import thaumcraft.api.research.ResearchPage;
final class InfusedFireResearch {
 static void register(){
  if(!TinkererConfig.fireEnabled())return;
  var kinds=InfusedFireTransformations.Kind.values();
  var aspects=new Aspect[]{Aspect.AIR,Aspect.WATER,Aspect.FIRE,Aspect.EARTH,Aspect.ORDER,Aspect.ENTROPY};
  var keys=new String[]{"AER","AQUA","IGNIS","TERRA","ORDO","PERDITIO"};
  var xs=new int[]{3,2,4,4,3,2};var ys=new int[]{-7,-2,-4,-6,-3,-8};var shard=new int[]{0,2,1,3,4,5};
  for(int i=0;i<kinds.length;i++){
   String key="FIRE_"+keys[i];var block=ThaumicTinkererModern.FIRES.get(kinds[i]).get();
   var research=new AspectList().add(Aspect.FIRE,kinds[i]==InfusedFireTransformations.Kind.FIRE?10:5);if(aspects[i]!=Aspect.FIRE)research.add(aspects[i],5);
   FocusResearch.research(key,block.asItem(),research,xs[i],ys[i],"BRIGHT_NITOR",true,ResearchPage.PageType.CRUCIBLE_CRAFTING,i==5?3:2);
   var cost=kinds[i]==InfusedFireTransformations.Kind.FIRE?new AspectList().add(Aspect.FIRE,10).add(Aspect.AIR,5):new AspectList().add(Aspect.FIRE,5).add(Aspect.MAGIC,5).add(aspects[i],5);
   ThaumicTinkererModern.CRUCIBLE_RECIPES.put(key,new thaumcraft.api.crafting.CrucibleRecipe(key,new ItemStack(block),Ingredient.of(dev.eugheist.tc4modern.TC4Modern.SHARDS.get(shard[i]).get()),cost));
  }
 }
}
