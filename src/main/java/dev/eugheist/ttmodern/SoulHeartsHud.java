package dev.eugheist.ttmodern;
@net.neoforged.fml.common.EventBusSubscriber(modid="ttinkerer",value=net.neoforged.api.distmarker.Dist.CLIENT)
public final class SoulHeartsHud {
 static final net.minecraft.resources.ResourceLocation TEXTURE=net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("ttinkerer","textures/gui/soulhearts.png");
 @net.neoforged.bus.api.SubscribeEvent public static void render(net.neoforged.neoforge.client.event.RenderGuiEvent.Post event){if(!KamiStartupConfig.enabled())return;var mc=net.minecraft.client.Minecraft.getInstance();if(mc.player==null||mc.options.hideGui||!mc.gameMode.canHurtPlayer())return;draw(event.getGuiGraphics(),SoulHearts.get(mc.player),mc.getWindow().getGuiScaledWidth(),mc.getWindow().getGuiScaledHeight());}
 static void draw(net.minecraft.client.gui.GuiGraphics gui,int hp,int width,int height){int x=width/2+10,y=height-29;for(int i=0,it=0;i<hp;i++){boolean half=i==hp-1&&hp%2!=0;if(!half&&i%2!=0)continue;gui.blit(TEXTURE,x+it*8,y,half?9:0,0,half?7:9,9,16,16);it++;}}
}
