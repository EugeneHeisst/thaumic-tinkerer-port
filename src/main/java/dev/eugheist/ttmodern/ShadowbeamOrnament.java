package dev.eugheist.ttmodern;
@net.neoforged.fml.common.EventBusSubscriber(modid="ttinkerer",bus=net.neoforged.fml.common.EventBusSubscriber.Bus.MOD,value=net.neoforged.api.distmarker.Dist.CLIENT)
public final class ShadowbeamOrnament {
 static final net.minecraft.client.resources.model.ModelResourceLocation MODEL=net.minecraft.client.resources.model.ModelResourceLocation.standalone(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("ttinkerer","item/shadowbeam_ornament"));
 @net.neoforged.bus.api.SubscribeEvent public static void models(net.neoforged.neoforge.client.event.ModelEvent.RegisterAdditional event){event.register(MODEL);}
 public static void render(net.minecraft.world.item.ItemStack wand,com.mojang.blaze3d.vertex.PoseStack pose,net.minecraft.client.renderer.MultiBufferSource buffers,int light,int overlay){
  if(!KamiStartupConfig.enabled())return ;if(!dev.eugheist.tc4modern.FociContent.getFocusItem(wand).is(ShadowbeamFocus.ITEM.get()))return;
  draw(pose,buffers,light,overlay);
 }
 static void draw(com.mojang.blaze3d.vertex.PoseStack pose,net.minecraft.client.renderer.MultiBufferSource buffers,int light,int overlay){
  var mc=net.minecraft.client.Minecraft.getInstance();var model=mc.getModelManager().getModel(MODEL);
  pose.pushPose();pose.mulPose(com.mojang.math.Axis.XP.rotationDegrees(180));
  for(int turn=0;turn<2;turn++){
   pose.pushPose();pose.mulPose(com.mojang.math.Axis.YP.rotationDegrees(turn*90));pose.translate(-.25,-.1,.0275);pose.scale(.5f,.5f,.5f);
   // Generated extrusion has z=[.46875,.53125]. Original ItemRenderer uses [-.1,0].
   pose.translate(0,0,-.85);pose.scale(1,1,1.6f);
   mc.getItemRenderer().renderModelLists(model,net.minecraft.world.item.ItemStack.EMPTY,light,overlay,pose,buffers.getBuffer(net.minecraft.client.renderer.RenderType.entityCutout(net.minecraft.world.inventory.InventoryMenu.BLOCK_ATLAS)));
   pose.popPose();
  }
  pose.popPose();
 }
}
