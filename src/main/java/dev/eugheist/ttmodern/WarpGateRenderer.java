package dev.eugheist.ttmodern;
public final class WarpGateRenderer implements net.minecraft.client.renderer.blockentity.BlockEntityRenderer<WarpGateEntity> {
 public WarpGateRenderer(net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context context){}
 @Override public void render(WarpGateEntity gate,float partial,com.mojang.blaze3d.vertex.PoseStack pose,net.minecraft.client.renderer.MultiBufferSource buffers,int light,int overlay){var player=net.minecraft.client.Minecraft.getInstance().player;draw(pose,buffers,player==null?0:player.tickCount);}
 static void draw(com.mojang.blaze3d.vertex.PoseStack pose,net.minecraft.client.renderer.MultiBufferSource buffers,int playerTicks){
  var out=buffers.getBuffer(net.minecraft.client.renderer.RenderType.debugQuads());pose.pushPose();pose.translate(.5,2.5,.5);pose.mulPose(new org.joml.Quaternionf().rotationAxis((float)Math.PI,new org.joml.Vector3f(1,0,1).normalize()));
  for(int repeat=5;repeat>=0;repeat--){double ticks=playerTicks-.75*(5-repeat);pose.pushPose();pose.translate(-.025f,.85f,-.025f);
   for(int i=0;i<12;i++){float deg=(int)(ticks/.25%360+30*i),rad=deg*(float)Math.PI/180f;float rx=(float)((double).7f+(double).1f*Math.sin(ticks/6)),rz=(float)((double).7f+(double).1f*Math.cos(ticks/6));float x=(float)(rx*Math.cos(rad)),z=(float)(rz*Math.sin(rad)),y=(float)Math.cos((ticks+50*i)/5)/10f;
    pose.pushPose();pose.translate(x,y,z);var axis=new org.joml.Vector3f((float)Math.sin(ticks*.25)/2f,(float)Math.max((double).6f,Math.sin(ticks*(double).1f)/2+.5),(float)Math.cos(ticks*.25)/2f).normalize();pose.mulPose(new org.joml.Quaternionf().rotationAxis(deg*(float)Math.PI/180f,axis));cube(pose.last(),out,repeat==5?1:.2f);pose.popPose();
   }pose.popPose();
  }pose.popPose();
 }
 private static void cube(com.mojang.blaze3d.vertex.PoseStack.Pose pose,com.mojang.blaze3d.vertex.VertexConsumer out,float alpha){float s=.0625f;float[][] corners={{0,0,0},{s,0,0},{s,s,0},{0,s,0},{0,0,s},{s,0,s},{s,s,s},{0,s,s}};int[][] faces={{0,3,2,1},{4,5,6,7},{0,4,7,3},{1,2,6,5},{0,1,5,4},{3,7,6,2}};for(var face:faces)for(int index:face){var v=corners[index];out.addVertex(pose,v[0],v[1],v[2]).setColor(1f,1f,1f,alpha);}}
}
