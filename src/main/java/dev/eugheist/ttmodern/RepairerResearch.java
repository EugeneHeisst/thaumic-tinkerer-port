package dev.eugheist.ttmodern;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import thaumcraft.api.aspects.*;
import thaumcraft.api.research.*;
final class RepairerResearch {
    static void register(){
        var page=new ResearchPage("");page.type=ResearchPage.PageType.INFUSION_CRAFTING;page.recipeKey="REPAIRER";
        new ResearchItem("REPAIRER","THAUMICTINKERER",new AspectList().add(Aspect.TOOL,2).add(Aspect.CRAFT,1).add(Aspect.ORDER,1).add(Aspect.MAGIC,1),-1,-9,3,new ItemStack(ThaumicTinkererModern.REPAIRER.get())){
            @Override public String getName(){return Component.translatable("ttresearch.name.REPAIRER").getString();}
            @Override public String getText(){return Component.translatable("ttresearch.lore.REPAIRER").getString();}
        }.setConcealed().setParents("FUNNEL").setParentsHidden("THAUMIUM","ENCHFABRIC").setPages(new ResearchPage("ttresearch.page.REPAIRER.0"),page).registerResearchItem();
        ThaumicTinkererModern.INFUSION_RECIPES.add(new dev.eugheist.tc4modern.InfusionItemRecipes.Recipe("REPAIRER","REPAIRER",()->ThaumicTinkererModern.REPAIRER.get().asItem(),8,new AspectList().add(Aspect.TOOL,15).add(Aspect.CRAFT,20).add(Aspect.ORDER,10).add(Aspect.MAGIC,15),()->net.minecraft.core.registries.BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("tc4modern","thaumium_block")),java.util.List.of(()->Items.IRON_INGOT,()->Items.GOLD_INGOT,()->Items.DIAMOND,()->Items.COBBLESTONE,()->Items.OAK_PLANKS,()->Items.LEATHER,dev.eugheist.tc4modern.TC4Modern.ENCHANTED_FABRIC::get,dev.eugheist.tc4modern.TC4Modern.THAUMIUM::get)));
    }
}
