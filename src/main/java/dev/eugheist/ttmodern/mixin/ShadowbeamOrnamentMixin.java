package dev.eugheist.ttmodern.mixin;
@org.spongepowered.asm.mixin.Mixin(targets="dev.eugheist.tc4modern.WandRenderer",remap=false)
abstract class ShadowbeamOrnamentMixin {
 @org.spongepowered.asm.mixin.injection.Inject(method="renderModel",at=@org.spongepowered.asm.mixin.injection.At(value="INVOKE",target="Ldev/eugheist/tc4modern/FociContent$Focus;getDepthTexture(Lnet/minecraft/world/item/ItemStack;)Ljava/lang/String;"))
 private static void ornament(net.minecraft.world.item.ItemStack wand,com.mojang.blaze3d.vertex.PoseStack pose,net.minecraft.client.renderer.MultiBufferSource buffers,int light,int overlay,org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci){dev.eugheist.ttmodern.ShadowbeamOrnament.render(wand,pose,buffers,light,overlay);}
}
