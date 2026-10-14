package dev.eugheist.ttmodern;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
/** ComputerCraft-only connector. A UUID is persisted, never a transient entity ID. */
final class GolemConnector {
 static final net.neoforged.neoforge.registries.DeferredBlock<ConnectorBlock> BLOCK=AspectAnalyzer.BLOCKS.register("golem_connector",ConnectorBlock::new);
 static final net.neoforged.neoforge.registries.DeferredItem<net.minecraft.world.item.BlockItem> ITEM=AspectAnalyzer.ITEMS.registerSimpleBlockItem("golem_connector",BLOCK);
 static final java.util.function.Supplier<BlockEntityType<Entity>> ENTITY=AspectAnalyzer.ENTITIES.register("golem_connector",()->BlockEntityType.Builder.of(Entity::new,BLOCK.get()).build(null));
 static void init(){}
 static final class Entity extends BlockEntity implements MovableTile {
  private java.util.UUID connected;
  private dev.eugheist.tc4modern.StrawGolem golem;
  private BlockState camouflage;
  Entity(BlockPos pos,BlockState state){super(ENTITY.get(),pos,state);}
  java.util.UUID connected(){return connected;}
  dev.eugheist.tc4modern.StrawGolem golem(){return golem;}
  BlockState camouflage(){return camouflage;}
  void camouflage(BlockState state){camouflage=state;changed();}
  void changed(){setChanged();if(level!=null)level.sendBlockUpdated(worldPosition,getBlockState(),getBlockState(),3);}
  void connect(java.util.UUID id){connected=id;golem=null;verify();changed();}
  void verify(){
   // A removed/unloaded entity must not remain a writable stale object.
   golem=null;if(connected==null||level==null)return;
   var pos=worldPosition;var bounds=new net.minecraft.world.phys.AABB(pos.getX()-30,pos.getY()-30,pos.getZ()-30,pos.getX()+30,pos.getY()+30,pos.getZ()+30);
   for(var candidate:level.getEntitiesOfClass(dev.eugheist.tc4modern.StrawGolem.class,bounds))if(candidate.getUUID().equals(connected)){golem=candidate;break;}
  }
  static void tick(Level level,BlockPos pos,BlockState state,Entity tile){if(!level.isClientSide&&level.getGameTime()%100==1)tile.verify();}
  @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);if(connected!=null){tag.putLong("UUIDMost",connected.getMostSignificantBits());tag.putLong("UUIDLeast",connected.getLeastSignificantBits());}if(camouflage!=null)tag.put("Camouflage",NbtUtils.writeBlockState(camouflage));}
  @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);connected=tag.contains("UUIDMost")&&tag.contains("UUIDLeast")?new java.util.UUID(tag.getLong("UUIDMost"),tag.getLong("UUIDLeast")):null;golem=null;camouflage=tag.contains("Camouflage")?NbtUtils.readBlockState(lookup.lookupOrThrow(net.minecraft.core.registries.Registries.BLOCK),tag.getCompound("Camouflage")):null;}
  @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){return saveWithoutMetadata(lookup);}
  @Override public net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket getUpdatePacket(){return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);}
 }
 static final class ConnectorBlock extends BaseEntityBlock {
  static final com.mojang.serialization.MapCodec<ConnectorBlock> CODEC=simpleCodec(p->new ConnectorBlock());
  ConnectorBlock(){super(Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(0,0).sound(SoundType.STONE));}
  @Override protected com.mojang.serialization.MapCodec<? extends BaseEntityBlock> codec(){return CODEC;}
  @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.ENTITYBLOCK_ANIMATED;}
  @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new Entity(pos,state);}
  @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return level.isClientSide?null:createTickerHelper(type,ENTITY.get(),Entity::tick);}
  @Override protected net.minecraft.world.ItemInteractionResult useItemOn(net.minecraft.world.item.ItemStack stack,BlockState state,Level level,BlockPos pos,net.minecraft.world.entity.player.Player player,net.minecraft.world.InteractionHand hand,net.minecraft.world.phys.BlockHitResult hit){
   if(stack.getItem() instanceof ConnectorItem||!(stack.getItem() instanceof net.minecraft.world.item.BlockItem item)||item.getBlock() instanceof ConnectorBlock)return net.minecraft.world.ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
   var camo=item.getBlock().defaultBlockState();if(camo.getRenderShape()!=RenderShape.MODEL||camo.hasBlockEntity()||!camo.isCollisionShapeFullBlock(level,pos))return net.minecraft.world.ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
   if(!level.mayInteract(player,pos))return net.minecraft.world.ItemInteractionResult.FAIL;
   if(!level.isClientSide&&level.getBlockEntity(pos) instanceof Entity tile)tile.camouflage(camo);return net.minecraft.world.ItemInteractionResult.sidedSuccess(level.isClientSide);
  }
  @Override protected net.minecraft.world.InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,net.minecraft.world.entity.player.Player player,net.minecraft.world.phys.BlockHitResult hit){if(!player.getMainHandItem().isEmpty()||!level.mayInteract(player,pos))return net.minecraft.world.InteractionResult.PASS;if(!level.isClientSide&&level.getBlockEntity(pos) instanceof Entity tile)tile.camouflage(null);return net.minecraft.world.InteractionResult.sidedSuccess(level.isClientSide);}
 }
}
