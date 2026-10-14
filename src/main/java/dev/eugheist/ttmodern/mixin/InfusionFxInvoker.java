package dev.eugheist.ttmodern.mixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
@Mixin(targets="dev.eugheist.tc4modern.InfusionFx",remap=false)
public interface InfusionFxInvoker {
    @Invoker("handle") static void handle(dev.eugheist.tc4modern.InfusionContent.FxPayload payload){throw new AssertionError();}
}
