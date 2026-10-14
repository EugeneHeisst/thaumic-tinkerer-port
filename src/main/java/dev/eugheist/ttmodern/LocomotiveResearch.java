package dev.eugheist.ttmodern;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import thaumcraft.api.aspects.*;
import thaumcraft.api.research.*;
final class LocomotiveResearch{
    static final String KEY="TT_LEVITATOR"; // Base Thaumcraft already owns LEVITATOR.
    static void register(){
        new ResearchItem(KEY,"THAUMICTINKERER",new AspectList().add(Aspect.MOTION,2).add(Aspect.ORDER,2),-7,5,3,new ItemStack(ThaumicTinkererModern.LOCOMOTIVE.get())){
            @Override public String getName(){return Component.translatable("ttresearch.name.LEVITATOR").getString();}
            @Override public String getText(){return Component.translatable("ttresearch.lore.LEVITATOR").getString();}
        }.setParents("MAGNETS").setParentsHidden("INFUSION").setSecondary().setPages(new ResearchPage("ttresearch.page.LEVITATOR.0"),MagnetResearch.page("TT_LEVITATOR",ResearchPage.PageType.INFUSION_CRAFTING),MagnetResearch.page("LEVITATOR_RELAY",ResearchPage.PageType.ARCANE_CRAFTING)).registerResearchItem();
        ThaumicTinkererModern.INFUSION_RECIPES.add(new dev.eugheist.tc4modern.InfusionItemRecipes.Recipe(KEY,KEY,()->ThaumicTinkererModern.LOCOMOTIVE.get().asItem(),4,new AspectList().add(Aspect.MOTION,15).add(Aspect.ORDER,20).add(Aspect.MAGIC,15),()->FocusResearch.item("levitator"),java.util.List.of(()->Items.IRON_INGOT,()->Items.FEATHER,()->Items.IRON_INGOT,()->FocusResearch.item("obsidian_tile"))));
        var wood=Ingredient.of(FocusResearch.item("greatwood_log"));var fence=Ingredient.of(Items.OAK_FENCE);
        var ingredients=java.util.List.of(wood,fence,wood,Ingredient.of(dev.eugheist.tc4modern.TC4Modern.SHARDS.get(0).get()),Ingredient.of(Items.IRON_INGOT),Ingredient.of(dev.eugheist.tc4modern.TC4Modern.SHARDS.get(3).get()),wood,fence,wood);
        var predicates=new java.util.ArrayList<java.util.function.Predicate<ItemStack>>();for(var ingredient:ingredients)predicates.add(ingredient::test);
        ThaumicTinkererModern.ARCANE_RECIPES.add(new dev.eugheist.tc4modern.ArcaneRecipes.Entry("LEVITATOR_RELAY",KEY,3,3,ingredients,new ItemStack(ThaumicTinkererModern.LOCOMOTIVE_RELAY.get()),new AspectList().add(Aspect.AIR,20).add(Aspect.ORDER,5).add(Aspect.EARTH,15),new thaumcraft.api.crafting.ArcanePattern<ItemStack>(KEY,3,3,predicates).setMirrored(true)));
    }
}
