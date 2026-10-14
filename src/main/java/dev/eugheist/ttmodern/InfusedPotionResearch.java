package dev.eugheist.ttmodern;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import thaumcraft.api.aspects.*;
import thaumcraft.api.research.*;
final class InfusedPotionResearch {
 static void register(){
  var research=new ResearchItem("INFUSED_POTIONS","THAUMICTINKERER",new AspectList().add(Aspect.WATER,5).add(Aspect.ENTROPY,5),7,-5,2,InfusedSeedsItem.stack(Aspect.FIRE)){
   @Override public String getName(){return Component.translatable("ttresearch.name.INFUSED_POTIONS").getString();}
   @Override public String getText(){return Component.translatable("ttresearch.lore.INFUSED_POTIONS").getString();}
  }.setParentsHidden("INFUSION").setConcealed();
  if(TinkererConfig.fireEnabled())research.setParents("FIRE_PERDITIO","FIRE_ORDO","FIRE_IGNIS","FIRE_TERRA","FIRE_AER","FIRE_AQUA");
  var pages=new java.util.ArrayList<ResearchPage>();for(int i=0;i<6;i++)pages.add(new ResearchPage("ttresearch.page.INFUSED_POTIONS."+i));
  pages.add(page("INFUSED_POTIONS",ResearchPage.PageType.INFUSION_CRAFTING));
  Item[] grains={ThaumicTinkererModern.INFUSED_GRAIN_AIR.get(),ThaumicTinkererModern.INFUSED_GRAIN_FIRE.get(),ThaumicTinkererModern.INFUSED_GRAIN_EARTH.get(),ThaumicTinkererModern.INFUSED_GRAIN_WATER.get()};
  Item[] potions={InfusedPotions.POTION_AIR.get(),InfusedPotions.POTION_FIRE.get(),InfusedPotions.POTION_EARTH.get(),InfusedPotions.POTION_WATER.get()};
  Aspect[] aspects={Aspect.AIR,Aspect.FIRE,Aspect.EARTH,Aspect.WATER};
  for(int i=0;i<4;i++){String key="INFUSED_POTIONSPOT"+i;ThaumicTinkererModern.CRUCIBLE_RECIPES.put(key,new thaumcraft.api.crafting.CrucibleRecipe("INFUSED_POTIONS",new ItemStack(potions[i]),Ingredient.of(grains[i]),new AspectList().add(Aspect.AURA,5).add(aspects[i],5)));pages.add(page(key,ResearchPage.PageType.CRUCIBLE_CRAFTING));}
  research.setPages(pages.toArray(ResearchPage[]::new)).registerResearchItem();
 }
 private static ResearchPage page(String key,ResearchPage.PageType type){var page=new ResearchPage("");page.recipeKey=key;page.type=type;return page;}
}
