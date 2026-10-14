package dev.eugheist.ttmodern;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
public final class SummonBlock extends BaseEntityBlock{
    static final MapCodec<SummonBlock> CODEC=simpleCodec(p->new SummonBlock());
    public SummonBlock(){super(Properties.ofFullCopy(Blocks.IRON_BLOCK).strength(3,50).sound(SoundType.METAL).noOcclusion());}
    @Override protected MapCodec<? extends BaseEntityBlock> codec(){return CODEC;}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
    @Override protected net.minecraft.world.phys.shapes.VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,net.minecraft.world.phys.shapes.CollisionContext context){return Block.box(0,0,0,16,2,16);}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new SummonEntity(pos,state);}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return level.isClientSide?null:createTickerHelper(type,ThaumicTinkererModern.SUMMON_ENTITY.get(),SummonEntity::tick);}
}
