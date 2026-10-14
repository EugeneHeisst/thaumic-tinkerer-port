package dev.eugheist.ttmodern;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import thaumcraft.api.aspects.*;
import thaumcraft.api.research.*;
final class PlatformResearch {
 static void register(){
  var page=new ResearchPage("");page.type=ResearchPage.PageType.ARCANE_CRAFTING;page.recipeKey="PLATFORM";
  new ResearchItem("PLATFORM","THAUMICTINKERER",new AspectList().add(Aspect.SENSES,2).add(Aspect.TREE,1).add(Aspect.MOTION,1),-2,6,3,new ItemStack(ThaumicTinkererModern.PLATFORM.get())){
   @Override public String getName(){return Component.translatable("ttresearch.name.PLATFORM").getString();}
   @Override public String getText(){return Component.translatable("ttresearch.lore.PLATFORM").getString();}
  }.setParents("CLEANSING_TALISMAN").setConcealed().setSecondary().setPages(new ResearchPage("ttresearch.page.PLATFORM.0"),page).registerResearchItem();
  var ingredients=java.util.List.of(Ingredient.EMPTY,Ingredient.of(FocusResearch.item("silverwood_planks")),Ingredient.EMPTY,Ingredient.of(FocusResearch.item("greatwood_planks")),Ingredient.EMPTY,Ingredient.of(FocusResearch.item("greatwood_planks")));var predicates=new java.util.ArrayList<java.util.function.Predicate<ItemStack>>();for(var ingredient:ingredients)predicates.add(ingredient.isEmpty()?null:ingredient::test);
  ThaumicTinkererModern.ARCANE_RECIPES.add(new dev.eugheist.tc4modern.ArcaneRecipes.Entry("PLATFORM","PLATFORM",3,2,ingredients,new ItemStack(ThaumicTinkererModern.PLATFORM.get(),2),new AspectList().add(Aspect.AIR,2).add(Aspect.ENTROPY,4),new thaumcraft.api.crafting.ArcanePattern<ItemStack>("PLATFORM",3,2,predicates).setMirrored(true)));
 }
}
