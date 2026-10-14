package dev.eugheist.ttmodern;
public final class IchorPouchScreen extends net.minecraft.client.gui.screens.inventory.AbstractContainerScreen<IchorPouchMenu> {
 static final net.minecraft.resources.ResourceLocation TEXTURE=net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("ttinkerer","textures/gui/ichorpouch.png");
 public IchorPouchScreen(IchorPouchMenu menu,net.minecraft.world.entity.player.Inventory inventory,net.minecraft.network.chat.Component title){super(menu,inventory,title);imageWidth=imageHeight=256;}
 @Override protected void renderBg(net.minecraft.client.gui.GuiGraphics gui,float partial,int mouseX,int mouseY){com.mojang.blaze3d.systems.RenderSystem.enableBlend();gui.blit(TEXTURE,leftPos,topPos,0,0,256,256,256,256);com.mojang.blaze3d.systems.RenderSystem.disableBlend();}
 @Override protected void renderLabels(net.minecraft.client.gui.GuiGraphics gui,int x,int y){}
 @Override public void render(net.minecraft.client.gui.GuiGraphics gui,int x,int y,float partial){super.render(gui,x,y,partial);renderTooltip(gui,x,y);}
}
