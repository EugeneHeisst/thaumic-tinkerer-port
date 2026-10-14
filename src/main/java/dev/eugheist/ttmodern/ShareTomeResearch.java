package dev.eugheist.ttmodern;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.*;
final class ShareTomeResearch{
    static void register(){var item=new ResearchItem("SHARE_TOME","THAUMICTINKERER",new AspectList(),0,-1,0,new ItemStack(ThaumicTinkererModern.SHARE_TOME.get())){
        @Override public String getName(){return Component.translatable("ttresearch.name.SHARE_TOME").getString();}
        @Override public String getText(){return Component.translatable("ttresearch.lore.SHARE_TOME").getString();}
    }.setStub().setAutoUnlock().setRound();
        if(TinkererConfig.survivalShareTome())item.setPages(new ResearchPage("ttresearch.page.SHARE_TOME.0"),MagnetResearch.page("ttinkerer:share_tome",ResearchPage.PageType.NORMAL_CRAFTING));else item.setPages(new ResearchPage("ttresearch.page.SHARE_TOME.0"));item.registerResearchItem();
    }
}
