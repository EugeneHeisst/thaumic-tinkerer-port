package dev.eugheist.ttmodern;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import thaumcraft.api.aspects.*;
import thaumcraft.api.research.*;
final class RevealingHelmResearch {
 static void register(){
  var page=new ResearchPage("");page.type=ResearchPage.PageType.ARCANE_CRAFTING;page.recipeKey="REVEALING_HELM";
  new ResearchItem("REVEALING_HELM","THAUMICTINKERER",new AspectList().add(Aspect.AURA,2).add(Aspect.ARMOR,1),0,0,1,new ItemStack(ThaumicTinkererModern.REVEALING_HELM.get())){
   @Override public String getName(){return Component.translatable("ttresearch.name.REVEALING_HELM").getString();}
   @Override public String getText(){return Component.translatable("ttresearch.lore.REVEALING_HELM").getString();}
  }.setParents("GOGGLES").setParentsHidden("THAUMIUM").setPages(new ResearchPage("ttresearch.page.REVEALING_HELM.0"),page).registerResearchItem();
  var ingredients=java.util.List.of(Ingredient.of(FocusResearch.item("goggles")),Ingredient.of(FocusResearch.item("thaumium_helmet")));var predicates=new java.util.ArrayList<java.util.function.Predicate<ItemStack>>();for(var ingredient:ingredients)predicates.add(ingredient::test);var cost=new AspectList();for(var aspect:Aspect.getPrimalAspects())cost.add(aspect,5);
  ThaumicTinkererModern.ARCANE_RECIPES.add(new dev.eugheist.tc4modern.ArcaneRecipes.Entry("REVEALING_HELM","REVEALING_HELM",2,1,ingredients,new ItemStack(ThaumicTinkererModern.REVEALING_HELM.get()),cost,new thaumcraft.api.crafting.ArcanePattern<ItemStack>("REVEALING_HELM",2,1,predicates).setMirrored(true)));
 }
}
