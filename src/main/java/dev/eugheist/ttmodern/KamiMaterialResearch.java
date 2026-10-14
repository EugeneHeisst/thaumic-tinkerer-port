package dev.eugheist.ttmodern;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import thaumcraft.api.aspects.*;
import thaumcraft.api.research.*;
final class KamiMaterialResearch {
 static void register(){
  research("ICHOR_CLOTH",KamiResources.Kind.CLOTH,new AspectList().add(Aspect.CLOTH,2).add(Aspect.LIGHT,1).add(Aspect.CRAFT,1).add(Aspect.SENSES,1),11,7,"ICHOR",null,false);
  research("ICHORIUM",KamiResources.Kind.ICHORIUM,new AspectList().add(Aspect.METAL,2).add(Aspect.LIGHT,1).add(Aspect.CRAFT,1).add(Aspect.TOOL,1),11,9,"ICHOR","ICHOR_CLOTH",false);
  research("CAP_ICHOR",KamiResources.Kind.CAP,new AspectList().add(Aspect.TOOL,2).add(Aspect.METAL,1).add(Aspect.LIGHT,1).add(Aspect.MAGIC,1),11,11,"ICHORIUM",null,false);
  research("ROD_ICHORCLOTH",KamiResources.Kind.ROD,new AspectList().add(Aspect.TOOL,2).add(Aspect.CLOTH,1).add(Aspect.LIGHT,1).add(Aspect.MAGIC,1),14,2,"ICHOR_CLOTH","CAP_ICHOR",true);
  var ichor=KamiResources.item(KamiResources.Kind.ICHOR);
  arcane("ICHOR_CLOTH",new ItemStack(KamiResources.item(KamiResources.Kind.CLOTH),3),125,"CCCIIIDDD",java.util.Map.of('C',dev.eugheist.tc4modern.TC4Modern.ENCHANTED_FABRIC.get(),'I',ichor,'D',Items.DIAMOND));
  arcane("ICHORIUM",new ItemStack(KamiResources.item(KamiResources.Kind.ICHORIUM)),100," T IDI I ",java.util.Map.of('T',dev.eugheist.tc4modern.TC4Modern.THAUMIUM.get(),'I',ichor,'D',Items.DIAMOND));
  arcane("CAP_ICHOR",new ItemStack(KamiResources.item(KamiResources.Kind.CAP),2),100,"ICI M ICI",java.util.Map.of('M',KamiResources.item(KamiResources.Kind.ICHORIUM),'I',ichor,'C',dev.eugheist.tc4modern.WandParts.THAUMIUM_CAP.get()));
  ThaumicTinkererModern.INFUSION_RECIPES.add(new dev.eugheist.tc4modern.InfusionItemRecipes.Recipe("ROD_ICHORCLOTH","ROD_ICHORCLOTH",()->KamiResources.item(KamiResources.Kind.ROD),9,new AspectList().add(Aspect.MAGIC,100).add(Aspect.LIGHT,32).add(Aspect.TOOL,32),()->dev.eugheist.tc4modern.WandParts.RODS.get("silverwood").get(),java.util.List.of(()->ichor,()->KamiResources.item(KamiResources.Kind.CLOTH),()->FocusResearch.item("salis_mundus"),()->Items.GHAST_TEAR,()->FocusResearch.item("salis_mundus"),()->KamiResources.item(KamiResources.Kind.CLOTH))));
 }
 private static void research(String key,KamiResources.Kind kind,AspectList aspects,int x,int y,String parent,String hidden,boolean infusion){var r=new KamiResearch(key,aspects,x,y,5,new ItemStack(KamiResources.item(kind)));r.setParents(parent);if(hidden!=null)r.setParentsHidden(hidden);r.setPages(new ResearchPage("ttresearch.page."+key+".0"),MagnetResearch.page(key,infusion?ResearchPage.PageType.INFUSION_CRAFTING:ResearchPage.PageType.ARCANE_CRAFTING)).registerResearchItem();}
 private static void arcane(String key,ItemStack output,int amount,String pattern,java.util.Map<Character,Item> keys){var ingredients=new java.util.ArrayList<Ingredient>();var predicates=new java.util.ArrayList<java.util.function.Predicate<ItemStack>>();for(char c:pattern.toCharArray()){var ingredient=c==' '?Ingredient.EMPTY:Ingredient.of(keys.get(c));ingredients.add(ingredient);predicates.add(c==' '?null:ingredient::test);}var cost=new AspectList();for(var aspect:Aspect.getPrimalAspects())cost.add(aspect,amount);ThaumicTinkererModern.ARCANE_RECIPES.add(new dev.eugheist.tc4modern.ArcaneRecipes.Entry(key,key,3,3,ingredients,output,cost,new thaumcraft.api.crafting.ArcanePattern<ItemStack>(key,3,3,predicates).setMirrored(true)));}
}
