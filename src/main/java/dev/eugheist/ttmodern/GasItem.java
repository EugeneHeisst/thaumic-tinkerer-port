package dev.eugheist.ttmodern;
import java.util.function.Supplier;
import net.minecraft.world.*;
import net.minecraft.world.item.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.*;
public final class GasItem extends Item {
    private final Supplier<GasBlock> gas;
    public GasItem(Supplier<GasBlock> gas){super(new Properties());this.gas=gas;}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){
        var stack=player.getItemInHand(hand);var pos=player.blockPosition().above();
        if(!player.mayUseItemAt(pos,net.minecraft.core.Direction.UP,stack))return InteractionResultHolder.fail(stack);
        if(!level.isClientSide) {
            // Build 164 consumes the bottle even if the player's head position is occupied.
            if(level.getBlockState(pos).isAir() || level.getBlockState(pos).getBlock() instanceof GasBlock || level.getBlockState(pos).getBlock() instanceof NitorLightBlock)level.setBlock(pos,gas.get().defaultBlockState().setValue(GasBlock.POWER,4),2);
            if(!player.getAbilities().instabuild)stack.shrink(1);
            level.playSound(null,player.blockPosition(),SoundEvents.BOTTLE_EMPTY,SoundSource.PLAYERS,.5f,.5f);
        }
        return InteractionResultHolder.sidedSuccess(stack,level.isClientSide);
    }
}
