package dev.eugheist.ttmodern.mixin;
import dev.eugheist.ttmodern.IchorPouch;
import dev.eugheist.tc4modern.FocusPouchItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
@Mixin(targets="dev.eugheist.tc4modern.FociRadial",remap=false)
public abstract class IchorPouchRadialMixin {
 @Redirect(method="gather",at=@At("dev.eugheist.ttmodern.mixin.FocusPouchInstanceof"))
 private static boolean pouch(Object item,Class<?> type){return item instanceof FocusPouchItem||item instanceof IchorPouch;}
 @Redirect(method="gather",at=@At(value="INVOKE",target="Ldev/eugheist/tc4modern/FocusPouchItem;contents(Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;"))
 private static java.util.List<ItemStack> contents(ItemStack stack){return stack.getItem() instanceof IchorPouch?IchorPouch.contents(stack):FocusPouchItem.contents(stack);}
}
