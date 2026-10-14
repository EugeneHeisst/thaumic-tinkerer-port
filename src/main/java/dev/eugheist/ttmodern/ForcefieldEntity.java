package dev.eugheist.ttmodern;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
public final class ForcefieldEntity extends BlockEntity {
 private int ticks=60;
 public ForcefieldEntity(BlockPos pos,BlockState state){super(ThaumicTinkererModern.FORCEFIELD_ENTITY.get(),pos,state);}
 static void tick(Level level,BlockPos pos,ForcefieldEntity tile){
  if(level.isClientSide){dev.eugheist.tc4modern.FociFx.blockSparkle(pos,255,1);return;}
  if(tile.ticks<0)level.removeBlock(pos,false);tile.ticks--;
 }
}
