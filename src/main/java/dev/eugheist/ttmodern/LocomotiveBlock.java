package dev.eugheist.ttmodern;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
public final class LocomotiveBlock extends BaseEntityBlock{
    private final boolean relay;
    static final MapCodec<LocomotiveBlock> CODEC=simpleCodec(p->new LocomotiveBlock(false));
    public LocomotiveBlock(boolean relay){super(Properties.ofFullCopy(Blocks.IRON_BLOCK).strength(3,50));this.relay=relay;}
    @Override protected MapCodec<? extends BaseEntityBlock> codec(){return CODEC;}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return relay?new LocomotiveRelayEntity(pos,state):new LocomotiveEntity(pos,state);}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return relay?createTickerHelper(type,ThaumicTinkererModern.LOCOMOTIVE_RELAY_ENTITY.get(),LocomotiveRelayEntity::tick):level.isClientSide?null:createTickerHelper(type,ThaumicTinkererModern.LOCOMOTIVE_ENTITY.get(),LocomotiveEntity::tick);}
}
