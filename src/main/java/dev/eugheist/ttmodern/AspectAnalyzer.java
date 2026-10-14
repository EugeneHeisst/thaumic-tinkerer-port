package dev.eugheist.ttmodern;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredRegister;
/** Original ComputerCraft-only analyzer. It reads, never consumes, the stored item. */
final class AspectAnalyzer {
 static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("ttinkerer");
 static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("ttinkerer");
 static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"ttinkerer");
 static final DeferredRegister<net.minecraft.world.inventory.MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"ttinkerer");
 static final net.neoforged.neoforge.registries.DeferredBlock<AnalyzerBlock> BLOCK=BLOCKS.register("aspect_analyzer",AnalyzerBlock::new);
 static final net.neoforged.neoforge.registries.DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem("aspect_analyzer",BLOCK);
 static final java.util.function.Supplier<BlockEntityType<Entity>> ENTITY=ENTITIES.register("aspect_analyzer",()->BlockEntityType.Builder.of(Entity::new,BLOCK.get()).build(null));
 static final java.util.function.Supplier<net.minecraft.world.inventory.MenuType<AspectAnalyzerMenu>> MENU=MENUS.register("aspect_analyzer",()->net.neoforged.neoforge.common.extensions.IMenuTypeExtension.create((id,inv,buf)->AspectAnalyzerMenu.client(id,inv,buf.readBlockPos())));
 static void register(net.neoforged.bus.api.IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);}
 static final class Entity extends BlockEntity implements MovableTile {
  final net.neoforged.neoforge.items.ItemStackHandler inventory=new net.neoforged.neoforge.items.ItemStackHandler(1){
   @Override protected void onContentsChanged(int slot){setChanged();}
  };
  Entity(BlockPos pos,BlockState state){super(ENTITY.get(),pos,state);}
  java.util.Map<String,Integer> aspects(){return level==null?java.util.Map.of():dev.eugheist.tc4modern.RecipeAspects.get(inventory.getStackInSlot(0),level);}
  @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);tag.put("Inventory",inventory.serializeNBT(lookup));}
  @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);inventory.deserializeNBT(lookup,tag.getCompound("Inventory"));}
 }
 static final class AnalyzerBlock extends BaseEntityBlock {
  static final com.mojang.serialization.MapCodec<AnalyzerBlock> CODEC=simpleCodec(p->new AnalyzerBlock());
  AnalyzerBlock(){super(Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(1.7f,1));}
  @Override protected com.mojang.serialization.MapCodec<? extends BaseEntityBlock> codec(){return CODEC;}
  @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
  @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new Entity(pos,state);}
  @Override protected net.minecraft.world.InteractionResult useWithoutItem(BlockState state,net.minecraft.world.level.Level level,BlockPos pos,net.minecraft.world.entity.player.Player player,net.minecraft.world.phys.BlockHitResult hit){
   if(!level.mayInteract(player,pos))return net.minecraft.world.InteractionResult.FAIL;
   if(player instanceof net.minecraft.server.level.ServerPlayer server&&level.getBlockEntity(pos) instanceof Entity tile)server.openMenu(new net.minecraft.world.SimpleMenuProvider((id,inv,p)->new AspectAnalyzerMenu(id,inv,tile),net.minecraft.network.chat.Component.translatable("block.ttinkerer.aspect_analyzer")),buf->buf.writeBlockPos(pos));
   return net.minecraft.world.InteractionResult.sidedSuccess(level.isClientSide);
  }
  @Override protected void onRemove(BlockState state,net.minecraft.world.level.Level level,BlockPos pos,BlockState next,boolean moving){if(!state.is(next.getBlock())&&!level.isClientSide&&level.getBlockEntity(pos) instanceof Entity tile){var stack=tile.inventory.extractItem(0,64,false);if(!stack.isEmpty())Block.popResource(level,pos,stack);}super.onRemove(state,level,pos,next,moving);}
 }
}

