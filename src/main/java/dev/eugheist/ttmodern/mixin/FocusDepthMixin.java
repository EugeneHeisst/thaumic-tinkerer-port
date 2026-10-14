package dev.eugheist.ttmodern.mixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(targets="dev.eugheist.tc4modern.WandRenderer",remap=false)
public abstract class FocusDepthMixin {
    @Inject(method="depthCube",at=@At("HEAD"),cancellable=true)
    private static void animatedDepth(com.mojang.blaze3d.vertex.PoseStack pose,net.minecraft.client.renderer.MultiBufferSource buffers,net.minecraft.resources.ResourceLocation texture,int light,int overlay,CallbackInfo ci){
        String sprite=switch(texture.getPath()){case "textures/items/ttinkerer_focus_heal_depth.png"->"focushealdepth";case "textures/items/ttinkerer_focus_ender_chest_depth.png"->"focusenderchestdepth";case "textures/items/ttinkerer_focus_recall_depth.png"->"focusrecalldepth";default->null;};if(sprite==null)return;
        dev.eugheist.ttmodern.HealFocusDepth.render(pose,buffers,light,overlay,sprite);ci.cancel();
    }
}
