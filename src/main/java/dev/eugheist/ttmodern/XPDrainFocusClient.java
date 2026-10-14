package dev.eugheist.ttmodern;
@net.neoforged.fml.common.EventBusSubscriber(modid="ttinkerer",bus=net.neoforged.fml.common.EventBusSubscriber.Bus.MOD,value=net.neoforged.api.distmarker.Dist.CLIENT)
public final class XPDrainFocusClient {
 static int color(){var p=net.minecraft.client.Minecraft.getInstance().player;return p==null?0xFFFFFFFF:java.awt.Color.HSBtoRGB((p.tickCount*2%360)/360f,1,1);}
 @net.neoforged.bus.api.SubscribeEvent public static void colors(net.neoforged.neoforge.client.event.RegisterColorHandlersEvent.Item event){if(!KamiStartupConfig.enabled())return;event.register((stack,tint)->tint==0?color():0xFFFFFF,XPDrainFocus.ITEM.get());}
}
