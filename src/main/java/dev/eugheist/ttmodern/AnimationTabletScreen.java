package dev.eugheist.ttmodern;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
public final class AnimationTabletScreen extends AbstractContainerScreen<AnimationTabletMenu>{
    private static final ResourceLocation TEXTURE=ResourceLocation.fromNamespaceAndPath("ttinkerer","textures/gui/animationtablet.png");
    public AnimationTabletScreen(AnimationTabletMenu menu,Inventory inventory,Component title){super(menu,inventory,title);}
    @Override protected void init(){super.init();addRenderableWidget(new ModeButton(leftPos+52,topPos+15,1));addRenderableWidget(new ModeButton(leftPos+111,topPos+15,2));addRenderableWidget(new ModeButton(leftPos+81,topPos+60,0));}
    private final class ModeButton extends Button{
        final int choice;
        ModeButton(int x,int y,int choice){super(x,y,13,13,Component.translatable(choice==0?"ttmisc.redstoneControl":choice==1?"ttmisc.leftClick":"ttmisc.rightClick"),b->select(choice),DEFAULT_NARRATION);this.choice=choice;}
        @Override protected void renderWidget(GuiGraphics g,int mouseX,int mouseY,float partial){boolean selected=choice==0?menu.redstone():choice==1?menu.leftClick():!menu.leftClick();g.blit(TEXTURE,getX(),getY(),176,selected?13:0,13,13);}
    }
    void select(int id){if(minecraft!=null&&minecraft.gameMode!=null)minecraft.gameMode.handleInventoryButtonClick(menu.containerId,id);}
    @Override protected void renderBg(GuiGraphics g,float partial,int mouseX,int mouseY){g.blit(TEXTURE,leftPos,topPos,0,0,imageWidth,imageHeight);var left=Component.translatable("ttmisc.leftClick");g.drawString(font,left,leftPos+48-font.width(left),topPos+18,0x999999,false);g.drawString(font,Component.translatable("ttmisc.rightClick"),leftPos+128,topPos+18,0x999999,false);var redstone=Component.translatable("ttmisc.redstoneControl");g.drawString(font,redstone,leftPos+88-font.width(redstone)/2,topPos+50,0x999999,false);}
    @Override public void render(GuiGraphics g,int mouseX,int mouseY,float partial){super.render(g,mouseX,mouseY,partial);renderTooltip(g,mouseX,mouseY);}
}
