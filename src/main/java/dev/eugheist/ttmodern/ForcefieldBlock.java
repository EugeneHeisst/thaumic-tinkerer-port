package dev.eugheist.ttmodern;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
public final class ForcefieldBlock extends BaseEntityBlock {
 public static final MapCodec<ForcefieldBlock> CODEC=simpleCodec(p->new ForcefieldBlock());
 // Do not mark as air: modern empty chunk sections discard air states, and collision skips empty sections.
 public ForcefieldBlock(){super(Properties.of().noOcclusion().noLootTable().isSuffocating((s,l,p)->false).isViewBlocking((s,l,p)->false));}
 @Override protected MapCodec<ForcefieldBlock> codec(){return CODEC;}
 @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.INVISIBLE;}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new ForcefieldEntity(pos,state);}
 @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return type==ThaumicTinkererModern.FORCEFIELD_ENTITY.get()?(l,p,s,t)->ForcefieldEntity.tick(l,p,(ForcefieldEntity)t):null;}
}
