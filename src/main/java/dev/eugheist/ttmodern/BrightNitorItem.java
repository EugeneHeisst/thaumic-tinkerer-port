package dev.eugheist.ttmodern;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
public final class BrightNitorItem extends Item {
    public BrightNitorItem(){super(new Properties().stacksTo(1));}
    @Override public void inventoryTick(ItemStack stack,Level level,Entity entity,int slot,boolean selected) {
        if(level.isClientSide)return;
        place(level,new BlockPos(net.minecraft.util.Mth.floor(entity.getX()),(int)entity.getY()+1,net.minecraft.util.Mth.floor(entity.getZ())),false);
    }
    static void place(Level level,BlockPos pos,boolean awakened){if(level.isClientSide||!level.hasChunkAt(pos))return;var state=level.getBlockState(pos);if(state.isAir()||state.is(ThaumicTinkererModern.NITOR_LIGHT.get()))level.setBlock(pos,ThaumicTinkererModern.NITOR_LIGHT.get().defaultBlockState().setValue(NitorLightBlock.AWAKENED,awakened),2);}
}
