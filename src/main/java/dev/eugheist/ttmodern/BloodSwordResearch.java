package dev.eugheist.ttmodern;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import thaumcraft.api.aspects.*;
import thaumcraft.api.research.*;
final class BloodSwordResearch{
    static void register(){new ResearchItem("BLOOD_SWORD","THAUMICTINKERER",new AspectList().add(Aspect.HUNGER,2).add(Aspect.WEAPON,1).add(Aspect.FLESH,1).add(Aspect.SOUL,1),-4,6,3,new ItemStack(ThaumicTinkererModern.BLOOD_SWORD.get())){
        @Override public String getName(){return Component.translatable("ttresearch.name.BLOOD_SWORD").getString();}
        @Override public String getText(){return Component.translatable("ttresearch.lore.BLOOD_SWORD").getString();}
    }.setParents("CLEANSING_TALISMAN").setParentsHidden("INFUSION").setSecondary().setPages(new ResearchPage("ttresearch.page.BLOOD_SWORD.0"),MagnetResearch.page("BLOOD_SWORD",ResearchPage.PageType.INFUSION_CRAFTING),new ResearchPage("ttresearch.page.BLOOD_SWORD.1")).registerResearchItem();
        ThaumicTinkererModern.INFUSION_RECIPES.add(new dev.eugheist.tc4modern.InfusionItemRecipes.Recipe("BLOOD_SWORD","BLOOD_SWORD",ThaumicTinkererModern.BLOOD_SWORD::get,6,new AspectList().add(Aspect.HUNGER,20).add(Aspect.DARKNESS,5).add(Aspect.SOUL,10).add(Aspect.MAN,6),()->FocusResearch.item("thaumium_sword"),java.util.List.of(()->Items.ROTTEN_FLESH,()->Items.PORKCHOP,()->Items.BEEF,()->Items.BONE,()->Items.DIAMOND,()->Items.GHAST_TEAR)));
    }
}
