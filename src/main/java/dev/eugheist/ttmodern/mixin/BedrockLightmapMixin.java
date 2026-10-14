package dev.eugheist.ttmodern.mixin;
@org.spongepowered.asm.mixin.Mixin(net.minecraft.client.renderer.LightTexture.class)
abstract class BedrockLightmapMixin {
 @org.spongepowered.asm.mixin.Unique private float tt$partial;
 @org.spongepowered.asm.mixin.injection.Inject(method="updateLightTexture",at=@org.spongepowered.asm.mixin.injection.At("HEAD"))
 private void partial(float partial,org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci){tt$partial=partial;}
 @org.spongepowered.asm.mixin.injection.Redirect(method="updateLightTexture",at=@org.spongepowered.asm.mixin.injection.At(value="INVOKE",target="Lcom/mojang/blaze3d/platform/NativeImage;setPixelRGBA(III)V"))
 private void pixel(com.mojang.blaze3d.platform.NativeImage image,int x,int y,int original){var mc=net.minecraft.client.Minecraft.getInstance();image.setPixelRGBA(x,y,mc.level!=null&&mc.player!=null&&mc.level.dimension().equals(dev.eugheist.ttmodern.BedrockPortal.DIMENSION)?dev.eugheist.ttmodern.BedrockLightmap.nativeColor(mc,x,y,tt$partial):original);}
}
