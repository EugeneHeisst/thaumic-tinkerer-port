package dev.eugheist.ttmodern;
/** Atlas sprite UVs retain the original animated depth texture on the mounted focus. */
public final class HealFocusDepth {
    public static void render(com.mojang.blaze3d.vertex.PoseStack pose,net.minecraft.client.renderer.MultiBufferSource buffers,int light,int overlay){
        render(pose,buffers,light,overlay,"focushealdepth");
    }
    public static void render(com.mojang.blaze3d.vertex.PoseStack pose,net.minecraft.client.renderer.MultiBufferSource buffers,int light,int overlay,String texture){
        var sprite=net.minecraft.client.Minecraft.getInstance().getTextureAtlas(net.minecraft.world.inventory.InventoryMenu.BLOCK_ATLAS).apply(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("ttinkerer","items/"+texture));
        var out=buffers.getBuffer(net.minecraft.client.renderer.RenderType.entityTranslucent(net.minecraft.world.inventory.InventoryMenu.BLOCK_ATLAS));
        for(var direction:net.minecraft.core.Direction.values()){
            pose.pushPose();pose.mulPose(direction.getRotation());var last=pose.last();
            float[][] corners={{-.5f,.5f,-.5f,0,0},{-.5f,.5f,.5f,0,1},{.5f,.5f,.5f,1,1},{.5f,.5f,-.5f,1,0}};
            for(var v:corners)out.addVertex(last,v[0],v[1],v[2]).setColor(-1).setUv(sprite.getU(v[3]),sprite.getV(v[4])).setOverlay(overlay).setLight(light).setNormal(last,0,1,0);
            pose.popPose();
        }
    }
}
