package dev.eugheist.ttmodern.mixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(value=dev.eugheist.tc4modern.AspectTable.class,remap=false)
abstract class MobAspectTagsMixin{
 @Inject(method="get(Lnet/minecraft/world/item/ItemStack;)Ljava/util/Map;",at=@At("HEAD"),cancellable=true)
 private static void addon(net.minecraft.world.item.ItemStack stack,CallbackInfoReturnable<java.util.Map<String,Integer>> cir){if(stack.getItem() instanceof dev.eugheist.ttmodern.InfusedGrainItem grain){cir.setReturnValue(grain.aspects());return;}if(stack.getItem() instanceof dev.eugheist.ttmodern.MobAspectItem item&&item.form==0)cir.setReturnValue(java.util.Map.of(item.aspect.getTag(),8));}
}
