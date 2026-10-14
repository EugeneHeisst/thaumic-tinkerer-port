package dev.eugheist.ttmodern;
@net.neoforged.fml.common.EventBusSubscriber(modid="ttinkerer",value=net.neoforged.api.distmarker.Dist.CLIENT)
public final class PlacementMirrorPreview {
 static int rendered;
 static final net.minecraft.client.renderer.RenderType TYPE=net.minecraft.client.renderer.RenderType.entityTranslucent(net.minecraft.client.renderer.texture.TextureAtlas.LOCATION_BLOCKS);
 static final class Alpha implements com.mojang.blaze3d.vertex.VertexConsumer {
  final com.mojang.blaze3d.vertex.VertexConsumer out;
  Alpha(com.mojang.blaze3d.vertex.VertexConsumer out){this.out=out;}
  public com.mojang.blaze3d.vertex.VertexConsumer addVertex(float x,float y,float z){out.addVertex(x,y,z);return this;}
  public com.mojang.blaze3d.vertex.VertexConsumer setColor(int r,int g,int b,int a){out.setColor(r,g,b,Math.round(a*.6f));return this;}
  public com.mojang.blaze3d.vertex.VertexConsumer setUv(float u,float v){out.setUv(u,v);return this;}
  public com.mojang.blaze3d.vertex.VertexConsumer setUv1(int u,int v){out.setUv1(u,v);return this;}
  public com.mojang.blaze3d.vertex.VertexConsumer setUv2(int u,int v){out.setUv2(u,v);return this;}
  public com.mojang.blaze3d.vertex.VertexConsumer setNormal(float x,float y,float z){out.setNormal(x,y,z);return this;}
 }
 static void block(net.minecraft.client.Minecraft mc,com.mojang.blaze3d.vertex.PoseStack pose,net.minecraft.client.renderer.MultiBufferSource buffers,net.minecraft.world.level.block.state.BlockState state){mc.getBlockRenderer().renderSingleBlock(state,pose,type->new Alpha(buffers.getBuffer(TYPE)),net.minecraft.client.renderer.LightTexture.FULL_BRIGHT,net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY,net.neoforged.neoforge.client.model.data.ModelData.EMPTY,TYPE);}
 @net.neoforged.bus.api.SubscribeEvent public static void render(net.neoforged.neoforge.client.event.RenderLevelStageEvent event){if(!KamiStartupConfig.enabled())return ;if(event.getStage()!=net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS)return;var mc=net.minecraft.client.Minecraft.getInstance();rendered=0;if(mc.level==null)return;var camera=event.getCamera().getPosition();var pose=event.getPoseStack();var buffers=mc.renderBuffers().bufferSource();for(var player:mc.level.players()){var mirror=player.getMainHandItem();if(!mirror.is(PlacementMirror.ITEM.get())||BlockTalisman.block(mirror)==net.minecraft.world.level.block.Blocks.AIR)continue;var positions=PlacementMirror.targets(mirror,player);if(!player.getAbilities().instabuild&&PlacementMirror.available(mirror,player)<positions.size())continue;for(var at:positions){pose.pushPose();try{pose.translate(at.getX()-camera.x,at.getY()-camera.y,at.getZ()-camera.z);block(mc,pose,buffers,BlockTalisman.block(mirror).defaultBlockState());rendered++;}finally{pose.popPose();}}}buffers.endBatch(TYPE);}
}
