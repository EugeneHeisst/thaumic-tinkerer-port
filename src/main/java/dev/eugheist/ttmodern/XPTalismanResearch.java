package dev.eugheist.ttmodern;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import thaumcraft.api.aspects.*;
import thaumcraft.api.research.*;
final class XPTalismanResearch {
    /** Original icon is ItemXPTalisman metadata 1: the enabled talisman. */
    static ItemStack icon(){var stack=new ItemStack(ThaumicTinkererModern.XP_TALISMAN.get());net.minecraft.world.item.component.CustomData.update(net.minecraft.core.component.DataComponents.CUSTOM_DATA,stack,t->t.putBoolean("enabled",true));return stack;}
    static void register(){
        var page=new ResearchPage("");page.type=ResearchPage.PageType.INFUSION_CRAFTING;page.recipeKey="XP_TALISMAN";
        new ResearchItem("XP_TALISMAN","THAUMICTINKERER",new AspectList().add(Aspect.GREED,1).add(Aspect.MAGIC,1).add(Aspect.MAN,1),4,-1,2,icon()){
            @Override public String getName(){return Component.translatable("ttresearch.name.XP_TALISMAN").getString();}
            @Override public String getText(){return Component.translatable("ttresearch.lore.XP_TALISMAN").getString();}
        }.setParents("JARBRAIN","SPELL_CLOTH").setConcealed().setSecondary().setPages(new ResearchPage("ttresearch.page.XP_TALISMAN.0"),page).registerResearchItem();
        ThaumicTinkererModern.INFUSION_RECIPES.add(new dev.eugheist.tc4modern.InfusionItemRecipes.Recipe("XP_TALISMAN","XP_TALISMAN",ThaumicTinkererModern.XP_TALISMAN::get,6,new AspectList().add(Aspect.GREED,20).add(Aspect.EXCHANGE,10).add(Aspect.BEAST,10).add(Aspect.MECHANISM,5),()->Items.GOLD_INGOT,java.util.List.of(()->Items.QUARTZ,()->Items.QUARTZ,()->net.minecraft.core.registries.BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("tc4modern","zombie_brain")),()->Items.DIAMOND)));
    }
}
