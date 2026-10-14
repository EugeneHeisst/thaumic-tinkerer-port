package dev.eugheist.ttmodern;
final class AspectAnalyzerScreen extends net.minecraft.client.gui.screens.inventory.AbstractContainerScreen<AspectAnalyzerMenu> {
 private static final net.minecraft.resources.ResourceLocation TEXTURE=net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("ttinkerer","textures/gui/aspectanalyzer.png");
 private thaumcraft.api.aspects.Aspect hovered;
 AspectAnalyzerScreen(AspectAnalyzerMenu menu,net.minecraft.world.entity.player.Inventory inventory,net.minecraft.network.chat.Component title){super(menu,inventory,title);}
 @Override protected void renderBg(net.minecraft.client.gui.GuiGraphics gui,float partial,int mx,int my){
  hovered=null;gui.blit(TEXTURE,leftPos,topPos,0,0,imageWidth,imageHeight);
  var stack=menu.slots.get(0).getItem();if(stack.isEmpty()||minecraft.level==null)return;
  var target=dev.eugheist.tc4modern.ScanTarget.item(stack,minecraft.level);
  if(!dev.eugheist.tc4modern.ResearchKnowledge.isScanned(dev.eugheist.tc4modern.ClientKnowledge.knowledge,target.knowledgeKey()))return;
  var aspects=new thaumcraft.api.aspects.AspectList();target.aspects().forEach((tag,n)->{var a=thaumcraft.api.aspects.Aspect.getAspect(tag);if(a!=null)aspects.add(a,n);});int i=0;
  for(var aspect:aspects.getAspectsSortedAmount()){int x=leftPos+20+i++*18,y=topPos+58,c=aspect.getColor();com.mojang.blaze3d.systems.RenderSystem.setShaderColor((c>>16&255)/255f,(c>>8&255)/255f,(c&255)/255f,1);gui.blit(aspect.getImage(),x,y,0,0,16,16,16,16);com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1,1,1,1);gui.pose().pushPose();gui.pose().translate(x+11,y+11,0);gui.pose().scale(.5f,.5f,1);gui.drawString(font,Integer.toString(aspects.getAmount(aspect)),0,0,0xFFFFFF);gui.pose().popPose();if(mx>=x&&mx<x+16&&my>=y&&my<y+16)hovered=aspect;}
 }
 @Override public void render(net.minecraft.client.gui.GuiGraphics gui,int mx,int my,float partial){super.render(gui,mx,my,partial);renderTooltip(gui,mx,my);if(hovered!=null)gui.renderComponentTooltip(font,java.util.List.of(net.minecraft.network.chat.Component.literal(hovered.getName()),net.minecraft.network.chat.Component.literal(hovered.getLocalizedDescription())),mx,my);}
}
