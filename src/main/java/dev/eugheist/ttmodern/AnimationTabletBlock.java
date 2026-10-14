package dev.eugheist.ttmodern;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.*;
public final class AnimationTabletBlock extends BaseEntityBlock{
    static final MapCodec<AnimationTabletBlock> CODEC=simpleCodec(p->new AnimationTabletBlock());
    public static final DirectionProperty FACING=BlockStateProperties.HORIZONTAL_FACING;public static final BooleanProperty POWERED=BlockStateProperties.POWERED;
    public AnimationTabletBlock(){super(Properties.ofFullCopy(Blocks.IRON_BLOCK).strength(3,50).noOcclusion());registerDefaultState(stateDefinition.any().setValue(FACING,Direction.NORTH).setValue(POWERED,false));}
    @Override protected MapCodec<? extends BaseEntityBlock> codec(){return CODEC;}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(FACING,POWERED);}
    @Override public BlockState getStateForPlacement(net.minecraft.world.item.context.BlockPlaceContext context){return defaultBlockState().setValue(FACING,context.getHorizontalDirection().getOpposite());}
    @Override protected net.minecraft.world.phys.shapes.VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,net.minecraft.world.phys.shapes.CollisionContext context){return Block.box(0,0,0,16,2,16);}
    @Override protected void neighborChanged(BlockState state,Level level,BlockPos pos,Block block,BlockPos from,boolean moving){if(level.isClientSide)return;boolean power=level.hasNeighborSignal(pos)||level.hasNeighborSignal(pos.above());if(power&&!state.getValue(POWERED))level.scheduleTick(pos,this,1);if(power!=state.getValue(POWERED))level.setBlock(pos,state.setValue(POWERED,power),3);}
    @Override protected void tick(BlockState state,net.minecraft.server.level.ServerLevel level,BlockPos pos,net.minecraft.util.RandomSource random){if(level.getBlockEntity(pos) instanceof AnimationTabletEntity tile&&tile.redstone)tile.trigger();}
    boolean interact(Level level,BlockPos pos,Player player,ItemStack stack){if(!level.mayInteract(player,pos))return false;if(level.isClientSide)return true;if(!(level.getBlockEntity(pos) instanceof AnimationTabletEntity tile))return false;if(stack.getItem() instanceof dev.eugheist.tc4modern.BasicWandItem){if(!level.getBlockState(pos).getValue(POWERED)&&!tile.breaking&&tile.swingProgress==0){var old=level.getBlockState(pos);var next=switch(old.getValue(FACING)){case NORTH->Direction.SOUTH;case SOUTH->Direction.WEST;case WEST->Direction.EAST;default->Direction.NORTH;};level.setBlock(pos,old.setValue(FACING,next),3);}else player.displayClientMessage(net.minecraft.network.chat.Component.translatable("ttmisc.animationTablet.notRotatable"),false);}else if(player instanceof net.minecraft.server.level.ServerPlayer server)server.openMenu(new SimpleMenuProvider((id,inv,p)->new AnimationTabletMenu(id,inv,tile),net.minecraft.network.chat.Component.translatable("block.ttinkerer.animation_tablet")),buf->buf.writeBlockPos(pos));return true;}
    @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){return interact(level,pos,player,stack)?ItemInteractionResult.sidedSuccess(level.isClientSide):ItemInteractionResult.FAIL;}
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){return interact(level,pos,player,ItemStack.EMPTY)?InteractionResult.sidedSuccess(level.isClientSide):InteractionResult.FAIL;}
    @Override protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState next,boolean moving){if(!state.is(next.getBlock())&&level.getBlockEntity(pos) instanceof AnimationTabletEntity tile&&!level.isClientSide){var stack=tile.inventory.extractItem(0,64,false);if(!stack.isEmpty())Block.popResource(level,pos,stack);}super.onRemove(state,level,pos,next,moving);}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new AnimationTabletEntity(pos,state);}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return createTickerHelper(type,ThaumicTinkererModern.ANIMATION_TABLET_ENTITY.get(),AnimationTabletEntity::tick);}
}
