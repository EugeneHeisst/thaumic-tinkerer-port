package dev.eugheist.ttmodern;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import thaumcraft.api.aspects.*;
import thaumcraft.api.research.*;
final class FunnelResearch {
    static void register(){
        var page=new ResearchPage("");page.type=ResearchPage.PageType.ARCANE_CRAFTING;page.recipeKey="FUNNEL";
        new ResearchItem("FUNNEL","THAUMICTINKERER",new AspectList().add(Aspect.TOOL,1).add(Aspect.TRAVEL,2),0,-7,1,new ItemStack(ThaumicTinkererModern.FUNNEL.get())){
            @Override public String getName(){return Component.translatable("ttresearch.name.FUNNEL").getString();}
            @Override public String getText(){return Component.translatable("ttresearch.lore.FUNNEL").getString();}
        }.setParentsHidden("DISTILESSENTIA").setParents("BRIGHT_NITOR").setConcealed().setSecondary().setPages(new ResearchPage("ttresearch.page.FUNNEL.0"),page).registerResearchItem();
        var ingredients=java.util.List.of(Ingredient.of(Items.STONE),Ingredient.of(dev.eugheist.tc4modern.TC4Modern.THAUMIUM.get()),Ingredient.of(Items.STONE));
        var predicates=new java.util.ArrayList<java.util.function.Predicate<ItemStack>>();for(var ingredient:ingredients)predicates.add(ingredient::test);
        ThaumicTinkererModern.ARCANE_RECIPES.add(new dev.eugheist.tc4modern.ArcaneRecipes.Entry("FUNNEL","FUNNEL",3,1,ingredients,new ItemStack(ThaumicTinkererModern.FUNNEL.get()),new AspectList().add(Aspect.ORDER,1).add(Aspect.ENTROPY,1),new thaumcraft.api.crafting.ArcanePattern<ItemStack>("FUNNEL",3,1,predicates).setMirrored(true)));
    }
}
