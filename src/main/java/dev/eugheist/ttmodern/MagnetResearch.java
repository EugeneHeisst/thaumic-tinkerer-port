package dev.eugheist.ttmodern;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import thaumcraft.api.aspects.*;
import thaumcraft.api.research.*;
final class MagnetResearch{
    static ResearchPage page(String key,ResearchPage.PageType type){var page=new ResearchPage("");page.type=type;page.recipeKey=key;return page;}
    static void register(){
        new ResearchItem("MAGNETS","THAUMICTINKERER",new AspectList().add(Aspect.MECHANISM,2).add(Aspect.MOTION,1).add(Aspect.SENSES,1),-6,3,3,new ItemStack(ThaumicTinkererModern.MAGNET.get())){
            @Override public String getName(){return Component.translatable("ttresearch.name.MAGNETS").getString();}
            @Override public String getText(){return Component.translatable("ttresearch.lore.MAGNETS").getString();}
        }.setParents("INTERFACE").setParentsHidden("FOCUS_TELEKINESIS").setConcealed().setPages(new ResearchPage("ttresearch.page.MAGNETS.0"),new ResearchPage("ttresearch.page.MAGNETS.1"),page("MAGNET",ResearchPage.PageType.ARCANE_CRAFTING),page("MOB_MAGNET",ResearchPage.PageType.ARCANE_CRAFTING),page("MAGNETS",ResearchPage.PageType.CRUCIBLE_CRAFTING)).registerResearchItem();
        arcane("MAGNET",Items.IRON_INGOT,ThaumicTinkererModern.MAGNET.get().asItem());
        arcane("MOB_MAGNET",Items.COPPER_INGOT,ThaumicTinkererModern.MOB_MAGNET.get().asItem());
        ThaumicTinkererModern.CRUCIBLE_RECIPES.put("MAGNETS",new thaumcraft.api.crafting.CrucibleRecipe("MAGNETS",new ItemStack(ThaumicTinkererModern.SOUL_MOULD.get()),Ingredient.of(Items.ENDER_PEARL),new AspectList().add(Aspect.BEAST,4).add(Aspect.MIND,8).add(Aspect.SENSES,8)));
    }
    static void arcane(String key,Item metal,Item output){
        var wood=Ingredient.of(FocusResearch.item("greatwood_log"));
        var ingredients=java.util.List.of(Ingredient.EMPTY,Ingredient.of(metal),Ingredient.EMPTY,Ingredient.of(dev.eugheist.tc4modern.TC4Modern.SHARDS.get(0).get()),Ingredient.of(metal),Ingredient.of(dev.eugheist.tc4modern.TC4Modern.SHARDS.get(3).get()),wood,Ingredient.of(ThaumicTinkererModern.FOCUS_TELEKINESIS.get()),wood);
        var predicates=new java.util.ArrayList<java.util.function.Predicate<ItemStack>>();for(var ingredient:ingredients)predicates.add(ingredient.isEmpty()?null:ingredient::test);
        ThaumicTinkererModern.ARCANE_RECIPES.add(new dev.eugheist.tc4modern.ArcaneRecipes.Entry(key,"MAGNETS",3,3,ingredients,new ItemStack(output),new AspectList().add(Aspect.AIR,20).add(Aspect.ORDER,5).add(Aspect.EARTH,15).add(Aspect.ENTROPY,5),new thaumcraft.api.crafting.ArcanePattern<ItemStack>("MAGNETS",3,3,predicates).setMirrored(true)));
    }
}
