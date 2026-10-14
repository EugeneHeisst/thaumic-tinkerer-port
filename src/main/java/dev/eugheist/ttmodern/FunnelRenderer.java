package dev.eugheist.ttmodern;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.component.CustomData;
import dev.eugheist.tc4modern.WardedJarBlock;
import dev.eugheist.tc4modern.WardedJarEntity;
import dev.eugheist.tc4modern.EssentiaRenderers;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

final class FunnelRenderer implements BlockEntityRenderer<FunnelEntity> {
    private final EssentiaRenderers.JarRenderer contents=new EssentiaRenderers.JarRenderer(null);
    FunnelRenderer(BlockEntityRendererProvider.Context context){}
    public void render(FunnelEntity tile,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){
        var stack=tile.inventory.getStackInSlot(0);if(!(stack.getItem() instanceof WardedJarBlock.JarItem))return;
        var state=((BlockItem)stack.getItem()).getBlock().defaultBlockState();
        var jar=new WardedJarEntity(BlockPos.ZERO,state);
        var tag=stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();
        var aspects=new AspectList();aspects.readFromNBT(tag);
        jar.state.aspect=aspects.size()>0?aspects.getAspects()[0]:null;
        jar.state.amount=jar.state.aspect==null?0:aspects.getAmount(jar.state.aspect);
        jar.state.aspectFilter=Aspect.getAspect(tag.getString("AspectFilter"));
        jar.state.facing=5; // Original ItemJarFilledRenderer's east-facing label.
        int jarLight=tile.getLevel()==null?light:LevelRenderer.getLightColor(tile.getLevel(),tile.getBlockPos().above());
        pose.pushPose();
        // RenderTileFunnel (.5,.365,.5) + ENTITY jar offset (-.5,-.25,-.5).
        // Draw native full-size geometry directly, without the FIXED item's half scale.
        pose.translate(0,.115,0);
        contents.render(jar,partial,pose,buffers,jarLight,overlay);
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state,pose,buffers,jarLight,overlay,net.neoforged.neoforge.client.model.data.ModelData.EMPTY,null);
        pose.popPose();
    }
}
