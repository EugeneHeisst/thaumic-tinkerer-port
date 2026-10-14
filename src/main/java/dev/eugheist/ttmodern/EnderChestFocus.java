package dev.eugheist.ttmodern;
import dev.eugheist.tc4modern.FociContent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import thaumcraft.api.aspects.*;
public final class EnderChestFocus extends TinkererFocus {
    @Override public AspectList getVisCost(ItemStack focus){return new AspectList().add(Aspect.ORDER,100).add(Aspect.ENTROPY,100);}
    @Override public String getSortingHelperBase(){return "ENDERCHEST";}
    @Override public int getFocusColor(ItemStack focus){return 0x132223;}
    @Override public String getDepthTexture(ItemStack focus){return "ttinkerer_focus_ender_chest_depth";}
    @Override public int[] getPossibleUpgradesByRank(ItemStack focus,int rank){return new int[]{2};}
    @Override public boolean onFocusRightClick(ItemStack wand,Level level,Player player){
        if(!level.isClientSide&&FociContent.consumeAllVis(wand,player,getVisCost(wand),true)){
            player.openMenu(new net.minecraft.world.SimpleMenuProvider((id,inventory,p)->net.minecraft.world.inventory.ChestMenu.threeRows(id,inventory,p.getEnderChestInventory()),net.minecraft.network.chat.Component.translatable("container.enderchest")));
            level.playSound(null,player.blockPosition(),net.minecraft.sounds.SoundEvents.ENDERMAN_TELEPORT,net.minecraft.sounds.SoundSource.PLAYERS,1,1);
        }
        return true;
    }
}
