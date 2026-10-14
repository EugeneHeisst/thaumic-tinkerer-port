package dev.eugheist.ttmodern;
@net.neoforged.fml.common.EventBusSubscriber(modid="ttinkerer",value=net.neoforged.api.distmarker.Dist.CLIENT)
public final class AdvancedToolHud {
 static String text;static int remaining;static long elapsed;
 public static void show(String value){if(!value.equals(text)){text=value;remaining=400;}}
 static int color(int ticks,long time){int opacity=Math.min(160,(int)(ticks*256f/10f));return java.awt.Color.HSBtoRGB((float)Math.cos(time/250.0),.6f,1f)+(opacity<<24);}
 @net.neoforged.bus.api.SubscribeEvent public static void tick(net.neoforged.neoforge.client.event.ClientTickEvent.Post event){elapsed++;if(remaining>0)remaining--;}
 @net.neoforged.bus.api.SubscribeEvent public static void render(net.neoforged.neoforge.client.event.RenderGuiEvent.Post event){var mc=net.minecraft.client.Minecraft.getInstance();if(mc.player==null||mc.options.hideGui)return;draw(event.getGuiGraphics(),mc.font,mc.getWindow().getGuiScaledWidth(),mc.getWindow().getGuiScaledHeight());}
 static void draw(net.minecraft.client.gui.GuiGraphics gui,net.minecraft.client.gui.Font font,int width,int height){if(remaining>0&&text!=null&&!text.isEmpty())gui.drawString(font,text,(width-font.width(text))/2,height-72,color(remaining,elapsed),false);}
}
