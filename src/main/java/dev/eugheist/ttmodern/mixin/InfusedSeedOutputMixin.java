package dev.eugheist.ttmodern.mixin;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.world.item.ItemStack;
@Mixin(value=dev.eugheist.tc4modern.InfusionItemRecipes.Recipe.class,remap=false)
abstract class InfusedSeedOutputMixin {
 @Shadow public abstract String key();
 @Inject(method="outputStack",at=@At("HEAD"),cancellable=true)
 private void seed(ItemStack input,CallbackInfoReturnable<ItemStack> cir){var result=dev.eugheist.ttmodern.InfusedSeedRecipes.output(key());if(!result.isEmpty())cir.setReturnValue(result);}
}
