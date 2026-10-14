package dev.eugheist.ttmodern;
/** MC1.7.10 RenderGlobal skips sky/clouds when WorldProvider.isSurfaceWorld is false. */
@net.neoforged.fml.common.EventBusSubscriber(modid="ttinkerer",bus=net.neoforged.fml.common.EventBusSubscriber.Bus.MOD,value=net.neoforged.api.distmarker.Dist.CLIENT)
public final class BedrockDimensionClient extends net.minecraft.client.renderer.DimensionSpecialEffects {
 public BedrockDimensionClient(){super(Float.NaN,false,SkyType.NONE,false,false);}
 @Override public net.minecraft.world.phys.Vec3 getBrightnessDependentFogColor(net.minecraft.world.phys.Vec3 color,float brightness){return new net.minecraft.world.phys.Vec3((160/255f)*.15f,(128/255f)*.15f,(160/255f)*.15f);}
 @Override public boolean isFoggyAt(int x,int z){return false;}
 @net.neoforged.bus.api.SubscribeEvent public static void register(net.neoforged.neoforge.client.event.RegisterDimensionSpecialEffectsEvent event){event.register(BedrockPortal.DIMENSION.location(),new BedrockDimensionClient());}
}
