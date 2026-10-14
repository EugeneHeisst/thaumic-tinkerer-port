package dev.eugheist.ttmodern;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
public final class MagnetScreen extends AbstractContainerScreen<MagnetMenu>{
    private static final ResourceLocation TEXTURE=ResourceLocation.fromNamespaceAndPath("ttinkerer","textures/gui/mobmagnet.png");
    public MagnetScreen(MagnetMenu menu,Inventory inventory,Component title){super(menu,inventory,title);}
    @Override protected void init(){super.init();
        addRenderableWidget(Button.builder(Component.translatable("ttmisc.adult"),b->select(0)).bounds(leftPos+98,topPos+24,70,20).build());
        addRenderableWidget(Button.builder(Component.translatable("ttmisc.child"),b->select(1)).bounds(leftPos+98,topPos+46,70,20).build());
    }
    private void select(int id){if(minecraft!=null&&minecraft.gameMode!=null)minecraft.gameMode.handleInventoryButtonClick(menu.containerId,id);}
    @Override protected void renderBg(GuiGraphics graphics,float partial,int mouseX,int mouseY){graphics.blit(TEXTURE,leftPos,topPos,0,0,imageWidth,imageHeight);graphics.drawString(font,menu.adult()?"●":"○",leftPos+87,topPos+30,0x404040,false);graphics.drawString(font,menu.adult()?"○":"●",leftPos+87,topPos+52,0x404040,false);}
    @Override public void render(GuiGraphics graphics,int mouseX,int mouseY,float partial){super.render(graphics,mouseX,mouseY,partial);renderTooltip(graphics,mouseX,mouseY);}
}
