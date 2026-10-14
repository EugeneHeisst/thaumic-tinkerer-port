package dev.eugheist.ttmodern;
import net.minecraft.core.BlockPos;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.*;
public final class GasRemoverItem extends Item {
    public GasRemoverItem(){super(new Properties().stacksTo(1).rarity(Rarity.UNCOMMON));}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand) {
        if(player.isShiftKeyDown()&&!level.isClientSide){
            clear(level,player.blockPosition());
            level.playSound(null,player.blockPosition(),SoundEvents.AMETHYST_BLOCK_CHIME,SoundSource.PLAYERS,.2f,1f);
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand),level.isClientSide);
    }
    static int clear(Level level,BlockPos center) {
        int count=0;
        for(int x=-3;x<3;x++)for(int y=-3;y<3;y++)for(int z=-3;z<3;z++){
            var pos=center.offset(x,y,z);if(!level.hasChunkAt(pos))continue;
            var block=level.getBlockState(pos).getBlock();
            if(block instanceof GasBlock || block instanceof NitorLightBlock){level.removeBlock(pos,false);count++;}
        }
        return count;
    }
}
