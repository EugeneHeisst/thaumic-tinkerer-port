package dev.eugheist.ttmodern;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import thaumcraft.api.aspects.*;
import thaumcraft.api.research.*;
final class DislocatorResearch{
    static void register(){
        if(!dev.eugheist.tc4modern.WarpConfig.allowMirrors())return;
        new ResearchItem("DISLOCATOR","THAUMICTINKERER",new AspectList().add(Aspect.TRAVEL,2).add(Aspect.MECHANISM,1).add(Aspect.ELDRITCH,1),-6,1,3,new ItemStack(ThaumicTinkererModern.DISLOCATOR.get())){
            @Override public String getName(){return Component.translatable("ttresearch.name.DISLOCATOR").getString();}
            @Override public String getText(){return Component.translatable("ttresearch.lore.DISLOCATOR").getString();}
        }.setConcealed().setParents("INTERFACE").setParentsHidden("MIRROR").setSecondary().setPages(new ResearchPage("ttresearch.page.DISLOCATOR.0"),MagnetResearch.page("DISLOCATOR",ResearchPage.PageType.ARCANE_CRAFTING)).registerResearchItem();
        var ingredients=java.util.List.of(Ingredient.of(FocusResearch.item("mirror_glass")),Ingredient.of(ThaumicTinkererModern.INTERFACE.get()),Ingredient.of(Items.COMPARATOR));
        var predicates=new java.util.ArrayList<java.util.function.Predicate<ItemStack>>();for(var ingredient:ingredients)predicates.add(ingredient::test);
        ThaumicTinkererModern.ARCANE_RECIPES.add(new dev.eugheist.tc4modern.ArcaneRecipes.Entry("DISLOCATOR","DISLOCATOR",1,3,ingredients,new ItemStack(ThaumicTinkererModern.DISLOCATOR.get()),new AspectList().add(Aspect.EARTH,5).add(Aspect.ENTROPY,5),new thaumcraft.api.crafting.ArcanePattern<ItemStack>("DISLOCATOR",1,3,predicates).setMirrored(true)));
    }
}
