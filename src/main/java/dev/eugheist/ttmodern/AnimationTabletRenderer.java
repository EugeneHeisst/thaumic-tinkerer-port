package dev.eugheist.ttmodern;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.*;
final class AnimationTabletRenderer implements BlockEntityRenderer<AnimationTabletEntity>{
    AnimationTabletRenderer(BlockEntityRendererProvider.Context context){}
    public void render(AnimationTabletEntity tile,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){
        int index=switch(tile.facing()){case NORTH->0;case SOUTH->1;case WEST->2;default->3;};int rotation=new int[]{270,90,0,180}[index];
        overlay(tile,pose,buffers,"atcenter",-1,.65f,.13f,0,partial);overlay(tile,pose,buffers,tile.leftClick?"atleft":"atright",1,1,tile.leftClick?.13f:.131f,0,partial);overlay(tile,pose,buffers,"atfacingindent",0,.5f,.13f,rotation+90,partial);
        var stack=tile.inventory.getStackInSlot(0);if(stack.isEmpty())return;pose.pushPose();pose.mulPose(Axis.YP.rotationDegrees(rotation));pose.translate(.1,.2+Math.cos(System.currentTimeMillis()/600.0)/18,.5);float[][] shifts={{0,0,-1},{-1,0,0},{0,0,0},{-1,0,-1}};pose.translate(shifts[index][0],0,shifts[index][2]);pose.scale(.8f,.8f,.8f);pose.translate(.5,0,.5);pose.mulPose(Axis.ZP.rotationDegrees(tile.swingProgress));pose.translate(-.5,0,-.5);pose.translate(-tile.swingProgress/250f,tile.swingProgress/1000f,0);pose.mulPose(new org.joml.Quaternionf().rotationAxis((float)Math.toRadians(Math.cos(System.currentTimeMillis()/400.0)*5),new org.joml.Vector3f(1,0,1).normalize()));pose.translate(.5,.55,0);
        // Ground item transforms include the old entity-item scale; show one item even for a full stack.
        // The floating tool occupies the space above the opaque tablet, whose own light sample is zero.
        int itemLight=tile.getLevel()==null?light:LevelRenderer.getLightColor(tile.getLevel(),tile.getBlockPos().above());
        float scale=stack.getItem() instanceof BlockItem?2.5f:1.5f;pose.scale(scale,scale,scale);Minecraft.getInstance().getItemRenderer().renderStatic(stack.copyWithCount(1),ItemDisplayContext.GROUND,itemLight,overlay,pose,buffers,tile.getLevel(),0);pose.popPose();
    }
    private static void overlay(AnimationTabletEntity tile,PoseStack pose,MultiBufferSource buffers,String texture,int rotation,float size,float height,float force,float partial){pose.pushPose();pose.translate(.5,height,.5);pose.mulPose(Axis.YP.rotationDegrees(rotation==0?force:(tile.ticks+partial)*rotation%360));var out=buffers.getBuffer(RenderType.entityTranslucent(ResourceLocation.fromNamespaceAndPath("ttinkerer","textures/misc/"+texture+".png")));float h=size/2;float[][] corners={{-h,h,0,1},{h,h,1,1},{h,-h,1,0},{-h,-h,0,0}};for(var c:corners)out.addVertex(pose.last().pose(),c[0],0,c[1]).setColor(255,255,255,255).setUv(c[2],c[3]).setOverlay(OverlayTexture.NO_OVERLAY).setLight(0xF000F0).setNormal(pose.last(),0,1,0);pose.popPose();}
}
