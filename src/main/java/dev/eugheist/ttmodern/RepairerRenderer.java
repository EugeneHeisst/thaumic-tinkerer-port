package dev.eugheist.ttmodern;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.world.item.ItemDisplayContext;
final class RepairerRenderer implements BlockEntityRenderer<RepairerEntity> {
    RepairerRenderer(BlockEntityRendererProvider.Context context){}
    public void render(RepairerEntity tile,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){
        var stack=tile.inventory.getStackInSlot(0);if(stack.isEmpty())return;
        pose.pushPose();pose.translate(.5,.75+Math.sin((tile.ticks+partial)/10)*.05,.5);pose.scale(.5f,.5f,.5f);pose.mulPose(Axis.YP.rotationDegrees((tile.ticks+partial)*.75f));
        Minecraft.getInstance().getItemRenderer().renderStatic(stack,ItemDisplayContext.FIXED,light,overlay,pose,buffers,tile.getLevel(),0);
        pose.popPose();
    }
}
