package dev.eugheist.ttmodern;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.*;
public final class PlatformBlock extends BaseEntityBlock {
 public static final MapCodec<PlatformBlock> CODEC=simpleCodec(p->new PlatformBlock());
 public PlatformBlock(){super(Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(2,5).noOcclusion().isValidSpawn((s,l,p,t)->false).isRedstoneConductor((s,l,p)->false).isSuffocating((s,l,p)->false).isViewBlocking((s,l,p)->false));}
 @Override protected MapCodec<? extends BaseEntityBlock> codec(){return CODEC;}
 @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.ENTITYBLOCK_ANIMATED;}
 @Override protected VoxelShape getCollisionShape(BlockState state,net.minecraft.world.level.BlockGetter level,BlockPos pos,CollisionContext context){
  if(!(context instanceof EntityCollisionContext entityContext)||entityContext.getEntity()==null)return Shapes.empty();
  var entity=entityContext.getEntity();if(entity instanceof net.minecraft.world.entity.player.Player&&entity.isShiftKeyDown())return Shapes.empty();
  // Modern coordinates are feet-based on both sides; use the top face for stable one-way support.
  return entity.getY()>=pos.getY()+1-1e-5?Shapes.block():Shapes.empty();
 }
 @Override protected net.minecraft.world.ItemInteractionResult useItemOn(net.minecraft.world.item.ItemStack stack,BlockState state,net.minecraft.world.level.Level level,BlockPos pos,net.minecraft.world.entity.player.Player player,net.minecraft.world.InteractionHand hand,net.minecraft.world.phys.BlockHitResult hit){
  if(!(stack.getItem() instanceof net.minecraft.world.item.BlockItem item)||(item.getBlock() instanceof PlatformBlock||item.getBlock() instanceof TransvectorBlock||item.getBlock() instanceof DislocatorBlock))return net.minecraft.world.ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
  var camo=item.getBlock().defaultBlockState();if(camo.getRenderShape()!=RenderShape.MODEL||!camo.isCollisionShapeFullBlock(level,pos))return net.minecraft.world.ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
  if(!level.mayInteract(player,pos))return net.minecraft.world.ItemInteractionResult.FAIL;
  if(!level.isClientSide&&level.getBlockEntity(pos) instanceof PlatformEntity tile)tile.camouflage(camo);return net.minecraft.world.ItemInteractionResult.sidedSuccess(level.isClientSide);
 }
 @Override protected net.minecraft.world.InteractionResult useWithoutItem(BlockState state,net.minecraft.world.level.Level level,BlockPos pos,net.minecraft.world.entity.player.Player player,net.minecraft.world.phys.BlockHitResult hit){
  if(!player.getMainHandItem().isEmpty()||!level.mayInteract(player,pos))return net.minecraft.world.InteractionResult.PASS;
  if(!level.isClientSide&&level.getBlockEntity(pos) instanceof PlatformEntity tile)tile.camouflage(null);return net.minecraft.world.InteractionResult.sidedSuccess(level.isClientSide);
 }
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new PlatformEntity(pos,state);}
}
