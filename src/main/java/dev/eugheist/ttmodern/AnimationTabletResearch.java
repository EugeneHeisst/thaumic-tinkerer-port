package dev.eugheist.ttmodern;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import thaumcraft.api.aspects.*;
import thaumcraft.api.research.*;
final class AnimationTabletResearch{
    static void register(){
        new ResearchItem("ANIMATION_TABLET","THAUMICTINKERER",new AspectList().add(Aspect.MECHANISM,2).add(Aspect.METAL,1).add(Aspect.MOTION,1).add(Aspect.ENERGY,1),-8,2,4,new ItemStack(ThaumicTinkererModern.ANIMATION_TABLET.get())){
            @Override public String getName(){return Component.translatable("ttresearch.name.ANIMATION_TABLET").getString();}
            @Override public String getText(){return Component.translatable("ttresearch.lore.ANIMATION_TABLET").getString();}
        }.setParents("MAGNETS").setPages(new ResearchPage("ttresearch.page.ANIMATION_TABLET.0"),MagnetResearch.page("ANIMATION_TABLET",ResearchPage.PageType.ARCANE_CRAFTING)).registerResearchItem();
        var gold=Ingredient.of(Items.GOLD_INGOT);var iron=Ingredient.of(Items.IRON_INGOT);var core=Ingredient.of(FocusResearch.item("golem_core_blank"));var ingredients=java.util.List.of(gold,iron,gold,iron,core,iron);
        var predicates=new java.util.ArrayList<java.util.function.Predicate<ItemStack>>();for(var ingredient:ingredients)predicates.add(ingredient::test);
        ThaumicTinkererModern.ARCANE_RECIPES.add(new dev.eugheist.tc4modern.ArcaneRecipes.Entry("ANIMATION_TABLET","ANIMATION_TABLET",3,2,ingredients,new ItemStack(ThaumicTinkererModern.ANIMATION_TABLET.get()),new AspectList().add(Aspect.AIR,25).add(Aspect.ORDER,15).add(Aspect.FIRE,10),new thaumcraft.api.crafting.ArcanePattern<ItemStack>("ANIMATION_TABLET",3,2,predicates).setMirrored(true)));
    }
}
