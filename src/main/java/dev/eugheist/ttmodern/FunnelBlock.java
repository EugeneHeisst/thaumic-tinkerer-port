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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.*;
public final class FunnelBlock extends BaseEntityBlock {
    public static final MapCodec<FunnelBlock> CODEC=simpleCodec(p->new FunnelBlock());
    public FunnelBlock(){super(Properties.ofFullCopy(Blocks.STONE).strength(3,8).noOcclusion());}
    @Override protected MapCodec<? extends BaseEntityBlock> codec(){return CODEC;}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
    @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return Block.box(0,0,0,16,2,16);}
    @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return Shapes.block();}
    @Override public boolean canSurvive(BlockState state,LevelReader level,BlockPos pos){return level.getBlockState(pos.below()).is(Blocks.HOPPER);}
    @Override protected BlockState updateShape(BlockState state,Direction side,BlockState other,LevelAccessor level,BlockPos pos,BlockPos otherPos){
        if(side==Direction.DOWN&&!canSurvive(state,level,pos)){if(level instanceof Level world&&!world.isClientSide)world.destroyBlock(pos,true);return Blocks.AIR.defaultBlockState();}
        return state;
    }
    @Override protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState next,boolean moving){
        if(!state.is(next.getBlock())&&level.getBlockEntity(pos) instanceof FunnelEntity tile){var stack=tile.inventory.extractItem(0,1,false);if(!level.isClientSide&&!stack.isEmpty())Block.popResource(level,pos,stack);}
        super.onRemove(state,level,pos,next,moving);
    }
    private static boolean interact(Level level,BlockPos pos,Player player,ItemStack held){
        if(!level.mayInteract(player,pos)||!(level.getBlockEntity(pos) instanceof FunnelEntity tile))return false;
        var stored=tile.inventory.getStackInSlot(0);
        if(stored.isEmpty()){
            if(!tile.inventory.isItemValid(0,held))return false;
            if(!level.isClientSide)tile.inventory.setStackInSlot(0,held.split(1));
        }else if(!level.isClientSide){var jar=tile.inventory.extractItem(0,1,false);player.getInventory().placeItemBackInInventory(jar);}
        return true;
    }
    @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){return interact(level,pos,player,stack)?ItemInteractionResult.sidedSuccess(level.isClientSide):ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;}
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){return interact(level,pos,player,ItemStack.EMPTY)?InteractionResult.sidedSuccess(level.isClientSide):InteractionResult.PASS;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new FunnelEntity(pos,state);}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return level.isClientSide?null:createTickerHelper(type,ThaumicTinkererModern.FUNNEL_ENTITY.get(),FunnelEntity::tick);}
}
