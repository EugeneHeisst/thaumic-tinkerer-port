package dev.eugheist.ttmodern;
public final class WarpGateScreen extends net.minecraft.client.gui.screens.inventory.AbstractContainerScreen<WarpGateMenu> {
 private static final net.minecraft.resources.ResourceLocation TEXTURE=net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("ttinkerer","textures/gui/warpgate.png");
 public WarpGateScreen(WarpGateMenu menu,net.minecraft.world.entity.player.Inventory inv,net.minecraft.network.chat.Component title){super(menu,inv,title);}
 @Override protected void init(){super.init();addRenderableWidget(new LockButton(leftPos+5,topPos+5,()->{if(minecraft!=null&&minecraft.gameMode!=null)minecraft.gameMode.handleInventoryButtonClick(menu.containerId,0);},()->menu.gate.locked));}
 private static final class LockButton extends net.minecraft.client.gui.components.Button {
  final java.util.function.BooleanSupplier locked;
  LockButton(int x,int y,Runnable action,java.util.function.BooleanSupplier locked){super(x,y,13,13,net.minecraft.network.chat.Component.translatable("ttmisc.lockedGate"),b->action.run(),DEFAULT_NARRATION);this.locked=locked;}
  @Override protected void renderWidget(net.minecraft.client.gui.GuiGraphics g,int mx,int my,float partial){if(locked.getAsBoolean())g.blit(TEXTURE,getX(),getY(),176,0,13,13);}
 }
 @Override protected void renderBg(net.minecraft.client.gui.GuiGraphics g,float partial,int mx,int my){g.blit(TEXTURE,leftPos,topPos,0,0,imageWidth,imageHeight);g.drawString(font,net.minecraft.network.chat.Component.translatable("ttmisc.lockedGate"),leftPos+20,topPos+7,0xFF999999,false);}
 @Override protected void renderLabels(net.minecraft.client.gui.GuiGraphics g,int mx,int my){}
 @Override public void render(net.minecraft.client.gui.GuiGraphics g,int mx,int my,float partial){super.render(g,mx,my,partial);renderTooltip(g,mx,my);}
}
