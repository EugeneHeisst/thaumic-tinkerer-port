package dev.eugheist.ttmodern;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.*;
public final class MagnetBlock extends BaseEntityBlock {
    public static final BooleanProperty PULLING=BooleanProperty.create("pulling");
    public static final MapCodec<MagnetBlock> CODEC=com.mojang.serialization.codecs.RecordCodecBuilder.mapCodec(instance->instance.group(com.mojang.serialization.Codec.BOOL.fieldOf("mobs").forGetter(block->block.mobs),propertiesCodec()).apply(instance,MagnetBlock::new));
    public final boolean mobs;
    public MagnetBlock(boolean mobs){this(mobs,Properties.of().strength(1.7f,1).sound(SoundType.WOOD).noOcclusion());}
    private MagnetBlock(boolean mobs,Properties properties){super(properties);this.mobs=mobs;registerDefaultState(stateDefinition.any().setValue(PULLING,true));}
    @Override protected MapCodec<? extends BaseEntityBlock> codec(){return CODEC;}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(PULLING);}
    @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return Block.box(1,0,1,15,2,15);}
    private boolean interact(Level level,BlockPos pos,Player player,ItemStack held){
        if(!level.mayInteract(player,pos))return false;
        if(held.getItem() instanceof dev.eugheist.tc4modern.BasicWandItem){
            if(mobs&&!level.isClientSide&&player instanceof net.minecraft.server.level.ServerPlayer server&&level.getBlockEntity(pos) instanceof MagnetEntity tile)
                server.openMenu(new SimpleMenuProvider((id,inv,p)->new MagnetMenu(id,inv,tile),net.minecraft.network.chat.Component.translatable("block.ttinkerer.mob_magnet")),buf->buf.writeBlockPos(pos));
        }else if(!level.isClientSide){level.setBlock(pos,level.getBlockState(pos).cycle(PULLING),3);level.playSound(null,pos,net.minecraft.sounds.SoundEvents.UI_BUTTON_CLICK.value(),net.minecraft.sounds.SoundSource.BLOCKS,1,.5f);}
        return true;
    }
    @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){return interact(level,pos,player,stack)?ItemInteractionResult.sidedSuccess(level.isClientSide):ItemInteractionResult.FAIL;}
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){return interact(level,pos,player,ItemStack.EMPTY)?InteractionResult.sidedSuccess(level.isClientSide):InteractionResult.FAIL;}
    @Override protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState next,boolean moving){if(!state.is(next.getBlock())&&level.getBlockEntity(pos) instanceof MagnetEntity tile){var stack=tile.inventory.extractItem(0,1,false);if(!level.isClientSide&&!stack.isEmpty())Block.popResource(level,pos,stack);}super.onRemove(state,level,pos,next,moving);}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new MagnetEntity(pos,state);}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return level.isClientSide?null:createTickerHelper(type,ThaumicTinkererModern.MAGNET_ENTITY.get(),MagnetEntity::tick);}
}
