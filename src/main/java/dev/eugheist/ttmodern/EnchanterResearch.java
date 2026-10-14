package dev.eugheist.ttmodern;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import thaumcraft.api.aspects.*;
import thaumcraft.api.research.*;
final class EnchanterResearch {
    static void register(){
        var page=new ResearchPage("");page.type=ResearchPage.PageType.INFUSION_CRAFTING;page.recipeKey="ENCHANTER";
        new ResearchItem("ENCHANTER","THAUMICTINKERER",new AspectList().add(Aspect.MAGIC,2).add(Aspect.AURA,1).add(Aspect.ELDRITCH,1).add(Aspect.DARKNESS,1).add(Aspect.MIND,1),5,4,5,new ItemStack(ThaumicTinkererModern.ENCHANTER.get())){
            @Override public String getName(){return Component.translatable("ttresearch.name.ENCHANTER").getString();}
            @Override public String getText(){return Component.translatable("ttresearch.lore.ENCHANTER").getString();}
        }.setParents("SPELL_CLOTH").setParentsHidden("INFUSION").setPages(new ResearchPage("ttresearch.page.ENCHANTER.0"),new ResearchPage("ttresearch.page.ENCHANTER.1"),new ResearchPage("ttresearch.page.ENCHANTER.2"),page).registerResearchItem();
        var tile=(java.util.function.Supplier<Item>)()->FocusResearch.item("obsidian_tile");
        ThaumicTinkererModern.INFUSION_RECIPES.add(new dev.eugheist.tc4modern.InfusionItemRecipes.Recipe("ENCHANTER","ENCHANTER",()->ThaumicTinkererModern.ENCHANTER.get().asItem(),15,new AspectList().add(Aspect.MAGIC,50).add(Aspect.ENERGY,20).add(Aspect.ELDRITCH,20).add(Aspect.VOID,20).add(Aspect.MIND,10),()->Items.ENCHANTING_TABLE,java.util.List.of(tile,tile,tile,tile,tile,dev.eugheist.tc4modern.TC4Modern.THAUMIUM::get,dev.eugheist.tc4modern.TC4Modern.THAUMIUM::get,()->ThaumicTinkererModern.SPELL_CLOTH.get())));
    }
}
