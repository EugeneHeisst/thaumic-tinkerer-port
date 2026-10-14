package dev.eugheist.ttmodern;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.*;
public final class EnchanterBlock extends BaseEntityBlock {
    public static final MapCodec<EnchanterBlock> CODEC=simpleCodec(p->new EnchanterBlock());
    public EnchanterBlock(){super(Properties.ofFullCopy(Blocks.STONE).strength(5,2000).noOcclusion());}
    @Override protected MapCodec<? extends BaseEntityBlock> codec(){return CODEC;}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
    @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return Block.box(0,0,0,16,12,16);}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new EnchanterEntity(pos,state);}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return createTickerHelper(type,ThaumicTinkererModern.ENCHANTER_ENTITY.get(),EnchanterEntity::tick);}
    @Override protected net.minecraft.world.InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,net.minecraft.world.entity.player.Player player,net.minecraft.world.phys.BlockHitResult hit){
        if(!level.mayInteract(player,pos))return net.minecraft.world.InteractionResult.FAIL;
        if(player instanceof net.minecraft.server.level.ServerPlayer server&&level.getBlockEntity(pos) instanceof EnchanterEntity tile)
            server.openMenu(new net.minecraft.world.SimpleMenuProvider((id,inv,p)->new EnchanterMenu(id,inv,tile),net.minecraft.network.chat.Component.translatable("block.ttinkerer.enchanter")),buf->buf.writeBlockPos(pos));
        return net.minecraft.world.InteractionResult.sidedSuccess(level.isClientSide);
    }
    @Override protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState next,boolean moving){
        if(!state.is(next.getBlock())&&!level.isClientSide&&level.getBlockEntity(pos) instanceof EnchanterEntity tile)
            for(int slot=0;slot<2;slot++){var stack=tile.inventory.extractItem(slot,1,false);if(!stack.isEmpty())Block.popResource(level,pos,stack);}
        super.onRemove(state,level,pos,next,moving);
    }
}
