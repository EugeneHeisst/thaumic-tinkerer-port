package dev.eugheist.ttmodern.mixin;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.world.item.ItemStack;
@Mixin(value=dev.eugheist.tc4modern.InfusionItemRecipes.Recipe.class,remap=false)
abstract class KamiInfusionMixin {
 @Shadow public abstract String key();
 @Inject(method="outputStack",at=@At("HEAD"),cancellable=true)private void output(ItemStack input,CallbackInfoReturnable<ItemStack> cir){var result=dev.eugheist.ttmodern.IchorResearch.output(key());if(!result.isEmpty())cir.setReturnValue(result);}
 @Inject(method="componentStacks",at=@At("RETURN"),cancellable=true)private void components(CallbackInfoReturnable<java.util.List<ItemStack>> cir){if(key().equals("ICHORCLOTH_LEGS_GEM"))cir.setReturnValue(dev.eugheist.ttmodern.AwakenedLegs.components(cir.getReturnValue()));if(key().equals("ICHORCLOTH_HELM_GEM"))cir.setReturnValue(dev.eugheist.ttmodern.AwakenedHelm.components(cir.getReturnValue()));}
}
