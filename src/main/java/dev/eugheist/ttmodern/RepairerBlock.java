package dev.eugheist.ttmodern;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.world.*;
import net.minecraft.world.item.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.BlockHitResult;
public final class RepairerBlock extends BaseEntityBlock {
    public static final MapCodec<RepairerBlock> CODEC=simpleCodec(p->new RepairerBlock());
    public static final DirectionProperty FACING=BlockStateProperties.HORIZONTAL_FACING;
    public RepairerBlock(){super(Properties.ofFullCopy(Blocks.IRON_BLOCK).strength(5,10).noOcclusion());registerDefaultState(stateDefinition.any().setValue(FACING,Direction.NORTH));}
    @Override protected MapCodec<? extends BaseEntityBlock> codec(){return CODEC;}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(FACING);}
    @Override public BlockState getStateForPlacement(net.minecraft.world.item.context.BlockPlaceContext context){return defaultBlockState().setValue(FACING,context.getHorizontalDirection().getOpposite());}
    @Override protected BlockState rotate(BlockState state,Rotation rotation){return state.setValue(FACING,rotation.rotate(state.getValue(FACING)));}
    @Override protected BlockState mirror(BlockState state,Mirror mirror){return state.rotate(mirror.getRotation(state.getValue(FACING)));}
    @Override protected boolean isSignalSource(BlockState state){return true;}
    @Override protected int getDirectSignal(BlockState state,BlockGetter level,BlockPos pos,Direction side){return level.getBlockEntity(pos) instanceof RepairerEntity tile&&tile.inventory.getStackInSlot(0).isDamaged()?15:0;}
    @Override protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState next,boolean moving){
        if(!state.is(next.getBlock())&&level.getBlockEntity(pos) instanceof RepairerEntity tile){var stack=tile.inventory.extractItem(0,1,false);if(!level.isClientSide&&!stack.isEmpty())Block.popResource(level,pos,stack);}
        super.onRemove(state,level,pos,next,moving);
    }
    private static boolean interact(Level level,BlockPos pos,Player player,ItemStack held){
        if(!level.mayInteract(player,pos)||!(level.getBlockEntity(pos) instanceof RepairerEntity tile))return false;
        var stored=tile.inventory.getStackInSlot(0);
        if(stored.isEmpty()){
            if(!tile.inventory.isItemValid(0,held))return false;
            if(!level.isClientSide)tile.inventory.setStackInSlot(0,held.split(1));
        }else if(!level.isClientSide){var stack=tile.inventory.extractItem(0,1,false);player.getInventory().placeItemBackInInventory(stack);}
        return true;
    }
    @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){return interact(level,pos,player,stack)?ItemInteractionResult.sidedSuccess(level.isClientSide):ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;}
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){return interact(level,pos,player,ItemStack.EMPTY)?InteractionResult.sidedSuccess(level.isClientSide):InteractionResult.PASS;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new RepairerEntity(pos,state);}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return createTickerHelper(type,ThaumicTinkererModern.REPAIRER_ENTITY.get(),RepairerEntity::tick);}
}
