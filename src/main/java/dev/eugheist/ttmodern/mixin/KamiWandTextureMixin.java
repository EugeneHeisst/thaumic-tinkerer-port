package dev.eugheist.ttmodern.mixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(targets="dev.eugheist.tc4modern.WandRenderer",remap=false)
abstract class KamiWandTextureMixin {
 @Inject(method="tex",at=@At("HEAD"),cancellable=true)private static void texture(String path,CallbackInfoReturnable<net.minecraft.resources.ResourceLocation> cir){String asset=switch(path){case "wand_cap_ICHOR"->"capichor";case "wand_rod_ICHORCLOTH"->"rodichorcloth";default->null;};if(asset!=null)cir.setReturnValue(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("ttinkerer","textures/model/"+asset+".png"));}
}
