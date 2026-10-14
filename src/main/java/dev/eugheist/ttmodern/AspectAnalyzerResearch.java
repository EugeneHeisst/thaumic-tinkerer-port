package dev.eugheist.ttmodern;
final class AspectAnalyzerResearch {
 static void register(){
  var t=net.minecraft.world.item.crafting.Ingredient.of(dev.eugheist.tc4modern.TC4Modern.THAUMIUM.get());var w=net.minecraft.world.item.crafting.Ingredient.of(dev.eugheist.tc4modern.AlchemyContent.GREATWOOD_PLANKS.get());var m=net.minecraft.world.item.crafting.Ingredient.of(dev.eugheist.tc4modern.TC4Modern.THAUMOMETER.get());
  var ingredients=java.util.List.of(t,w,t,w,m,w,t,w,t);var predicates=new java.util.ArrayList<java.util.function.Predicate<net.minecraft.world.item.ItemStack>>();for(var ingredient:ingredients)predicates.add(ingredient::test);
  ThaumicTinkererModern.ARCANE_RECIPES.add(new dev.eugheist.tc4modern.ArcaneRecipes.Entry("ASPECT_ANALYZER","ASPECT_ANALYZER",3,3,ingredients,new net.minecraft.world.item.ItemStack(AspectAnalyzer.ITEM.get()),new thaumcraft.api.aspects.AspectList().add(thaumcraft.api.aspects.Aspect.ORDER,1).add(thaumcraft.api.aspects.Aspect.ENTROPY,1),new thaumcraft.api.crafting.ArcanePattern<net.minecraft.world.item.ItemStack>("ASPECT_ANALYZER",3,3,predicates).setMirrored(true)));
  var page=new thaumcraft.api.research.ResearchPage("");page.type=thaumcraft.api.research.ResearchPage.PageType.ARCANE_CRAFTING;page.recipeKey="ASPECT_ANALYZER";
  new thaumcraft.api.research.ResearchItem("ASPECT_ANALYZER","THAUMICTINKERER",new thaumcraft.api.aspects.AspectList().add(thaumcraft.api.aspects.Aspect.MECHANISM,2).add(thaumcraft.api.aspects.Aspect.SENSES,1).add(thaumcraft.api.aspects.Aspect.MIND,1),0,1,2,new net.minecraft.world.item.ItemStack(AspectAnalyzer.ITEM.get())){
   @Override public String getName(){return net.minecraft.network.chat.Component.translatable("ttresearch.name.ASPECT_ANALYZER").getString();}
   @Override public String getText(){return net.minecraft.network.chat.Component.translatable("ttresearch.lore.ASPECT_ANALYZER").getString();}
  }.setParents("PERIPHERALS").setParentsHidden("GOGGLES","THAUMIUM").setConcealed().setRound().setPages(new thaumcraft.api.research.ResearchPage("ttresearch.page.ASPECT_ANALYZER.0"),page).registerResearchItem();
 }
}
