package dev.eugheist.ttmodern;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.*;
final class TransvectorRenderer implements BlockEntityRenderer<TransvectorEntity> {
    TransvectorRenderer(BlockEntityRendererProvider.Context context){}
    public void render(TransvectorEntity tile,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){
        var state=tile.camouflage()==null?tile.getBlockState():tile.camouflage();
        var mc=Minecraft.getInstance();var renderer=mc.getBlockRenderer();var model=renderer.getBlockModel(state);
        int color=mc.getBlockColors().getColor(state,tile.getLevel(),tile.getBlockPos(),0);
        for(var layer:model.getRenderTypes(state,net.minecraft.util.RandomSource.create(42),net.neoforged.neoforge.client.model.data.ModelData.EMPTY))
            renderer.getModelRenderer().renderModel(pose.last(),buffers.getBuffer(net.neoforged.neoforge.client.RenderTypeHelper.getEntityRenderType(layer,false)),state,model,((color>>16)&255)/255f,((color>>8)&255)/255f,(color&255)/255f,light,overlay,net.neoforged.neoforge.client.model.data.ModelData.EMPTY,layer);
    }
}
