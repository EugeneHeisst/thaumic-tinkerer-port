package dev.eugheist.ttmodern;
import dev.eugheist.tc4modern.FociContent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
abstract class TinkererFocus extends FociContent.Focus {
    @Override public boolean onFocusRightClick(ItemStack wand,Level level,Player player){
        var hand=player.getOffhandItem()==wand?net.minecraft.world.InteractionHand.OFF_HAND:net.minecraft.world.InteractionHand.MAIN_HAND;
        dev.eugheist.ttmodern.mixin.FocusUseInvoker.ttinkerer$startUsing(player,hand);return true;
    }
    @Override public int[] getPossibleUpgradesByRank(ItemStack focus,int rank){return new int[]{2,0};}
}
