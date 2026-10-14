package dev.eugheist.ttmodern.mixin;
import dev.eugheist.tc4modern.FociContent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
@Mixin(FociContent.class)
public interface FocusUseInvoker {
    @Invoker("startUsing") static void ttinkerer$startUsing(Player player,InteractionHand hand){throw new AssertionError();}
}
