package dev.eugheist.ttmodern;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import thaumcraft.api.aspects.*;
import thaumcraft.api.research.*;
final class SummonResearch{
    static void register(){
        var pages=new java.util.ArrayList<ResearchPage>();pages.add(new ResearchPage("ttresearch.page.SUMMON.0"));pages.add(MagnetResearch.page("SUMMON0",ResearchPage.PageType.ARCANE_CRAFTING));
        for(var aspect:MobAspects.ASPECTS){pages.add(MagnetResearch.page("ttinkerer:mob_aspect_"+aspect.getTag()+"_condensed",ResearchPage.PageType.NORMAL_CRAFTING));pages.add(MagnetResearch.page("MOB_ASPECT_INFUSE_"+aspect.getTag(),ResearchPage.PageType.INFUSION_CRAFTING));var condensed=MobAspects.ITEMS.get(aspect)[1];ThaumicTinkererModern.INFUSION_RECIPES.add(new dev.eugheist.tc4modern.InfusionItemRecipes.Recipe("MOB_ASPECT_INFUSE_"+aspect.getTag(),"SUMMON",MobAspects.ITEMS.get(aspect)[2]::get,4,new AspectList().add(aspect,10),condensed::get,java.util.Collections.nCopies(8,condensed::get)));}
        pages.add(new ResearchPage("ttresearch.page.SUMMON.1"));
        new ResearchItem("SUMMON","THAUMICTINKERER",new AspectList().add(Aspect.WEAPON,1).add(Aspect.BEAST,3).add(Aspect.MAGIC,3),-5,8,3,new ItemStack(ThaumicTinkererModern.SUMMON.get())){
            @Override public String getName(){return Component.translatable("ttresearch.name.SUMMON").getString();}
            @Override public String getText(){return Component.translatable("ttresearch.lore.SUMMON").getString();}
        }.setParents("BLOOD_SWORD").setParentsHidden("INFUSION").setPages(pages.toArray(ResearchPage[]::new)).registerResearchItem();
        var wood=Ingredient.of(FocusResearch.item("obsidian_tile"));var stone=Ingredient.of(Items.STONE);var ingredients=java.util.List.of(wood,wood,wood,stone,stone,stone);var predicates=new java.util.ArrayList<java.util.function.Predicate<ItemStack>>();for(var ingredient:ingredients)predicates.add(ingredient::test);
        ThaumicTinkererModern.ARCANE_RECIPES.add(new dev.eugheist.tc4modern.ArcaneRecipes.Entry("SUMMON0","SUMMON",3,2,ingredients,new ItemStack(ThaumicTinkererModern.SUMMON.get()),new AspectList().add(Aspect.ORDER,50).add(Aspect.ENTROPY,50),new thaumcraft.api.crafting.ArcanePattern<ItemStack>("SUMMON",3,2,predicates).setMirrored(true)));
    }
}
