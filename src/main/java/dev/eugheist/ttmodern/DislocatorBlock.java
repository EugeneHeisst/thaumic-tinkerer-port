package dev.eugheist.ttmodern;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.BlockHitResult;
public final class DislocatorBlock extends BaseEntityBlock{
    static final MapCodec<DislocatorBlock> CODEC=simpleCodec(p->new DislocatorBlock());
    public static final DirectionProperty FACING=BlockStateProperties.FACING;
    public static final BooleanProperty POWERED=BlockStateProperties.POWERED;
    public DislocatorBlock(){super(Properties.ofFullCopy(Blocks.IRON_BLOCK).strength(3,10));registerDefaultState(stateDefinition.any().setValue(FACING,Direction.NORTH).setValue(POWERED,false));}
    @Override protected MapCodec<? extends BaseEntityBlock> codec(){return CODEC;}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(FACING,POWERED);}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){return defaultBlockState().setValue(FACING,context.getNearestLookingDirection().getOpposite());}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.ENTITYBLOCK_ANIMATED;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new DislocatorEntity(pos,state);}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return level.isClientSide?null:createTickerHelper(type,ThaumicTinkererModern.DISLOCATOR_ENTITY.get(),DislocatorEntity::tick);}
    @Override protected void neighborChanged(BlockState state,Level level,BlockPos pos,Block block,BlockPos neighbor,boolean moving){if(level.isClientSide)return;boolean power=level.hasNeighborSignal(pos)||level.hasNeighborSignal(pos.above());if(power!=state.getValue(POWERED)){level.setBlock(pos,state.setValue(POWERED,power),2);if(power)level.scheduleTick(pos,this,1);}}
    @Override protected void tick(BlockState state,ServerLevel level,BlockPos pos,net.minecraft.util.RandomSource random){if(level.getBlockEntity(pos) instanceof DislocatorEntity tile)tile.pulse();}
    @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){
        if(!level.mayInteract(player,pos))return ItemInteractionResult.FAIL;
        if(stack.getItem() instanceof dev.eugheist.tc4modern.BasicWandItem){if(!level.isClientSide)level.setBlock(pos,state.setValue(FACING,hit.getDirection()),3);return ItemInteractionResult.sidedSuccess(level.isClientSide);}
        if(stack.getItem() instanceof BlockItem item&&!(item.getBlock() instanceof DislocatorBlock)&&!(item.getBlock() instanceof TransvectorBlock)){
            var camo=item.getBlock().defaultBlockState();if(camo.getRenderShape()==RenderShape.MODEL&&!camo.hasBlockEntity()&&camo.isCollisionShapeFullBlock(level,pos)){if(!level.isClientSide&&level.getBlockEntity(pos) instanceof DislocatorEntity tile)tile.camouflage(camo);return ItemInteractionResult.sidedSuccess(level.isClientSide);}
        }return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){if(!player.getMainHandItem().isEmpty()||!level.mayInteract(player,pos))return InteractionResult.PASS;if(!level.isClientSide&&level.getBlockEntity(pos) instanceof DislocatorEntity tile)tile.camouflage(null);return InteractionResult.sidedSuccess(level.isClientSide);}
}
