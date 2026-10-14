package dev.eugheist.ttmodern;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
public final class WarpGateBlock extends BaseEntityBlock {
 static final net.neoforged.neoforge.registries.DeferredBlock<WarpGateBlock> BLOCK=KamiRegistries.BLOCKS.register("warp_gate",WarpGateBlock::new);
 static final net.neoforged.neoforge.registries.DeferredItem<net.minecraft.world.item.BlockItem> ITEM=KamiRegistries.ITEMS.register("warp_gate",()->new net.minecraft.world.item.BlockItem(BLOCK.get(),new net.minecraft.world.item.Item.Properties().rarity(net.minecraft.world.item.Rarity.EPIC)));
 static final java.util.function.Supplier<BlockEntityType<WarpGateEntity>> ENTITY=KamiRegistries.ENTITIES.register("warp_gate",()->BlockEntityType.Builder.of(WarpGateEntity::new,BLOCK.get()).build(null));
 static final com.mojang.serialization.MapCodec<WarpGateBlock> CODEC=simpleCodec(p->new WarpGateBlock());
 WarpGateBlock(){super(Properties.ofFullCopy(Blocks.STONE).strength(5,2000).noOcclusion());}
 static void init(){WarpGateMenu.init();}
 @Override protected com.mojang.serialization.MapCodec<? extends BaseEntityBlock> codec(){return CODEC;}
 @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new WarpGateEntity(pos,state);}
 @Override protected net.minecraft.world.InteractionResult useWithoutItem(BlockState state,net.minecraft.world.level.Level level,BlockPos pos,net.minecraft.world.entity.player.Player player,net.minecraft.world.phys.BlockHitResult hit){if(!level.isClientSide&&level.getBlockEntity(pos) instanceof WarpGateEntity gate&&level.mayInteract(player,pos))player.openMenu(new net.minecraft.world.SimpleMenuProvider((id,inv,p)->new WarpGateMenu(id,inv,gate),net.minecraft.network.chat.Component.translatable("block.ttinkerer.warp_gate")),buf->buf.writeBlockPos(pos));return net.minecraft.world.InteractionResult.sidedSuccess(level.isClientSide);}
 @Override protected net.minecraft.world.ItemInteractionResult useItemOn(net.minecraft.world.item.ItemStack stack,BlockState state,net.minecraft.world.level.Level level,BlockPos pos,net.minecraft.world.entity.player.Player player,net.minecraft.world.InteractionHand hand,net.minecraft.world.phys.BlockHitResult hit){if(stack.is(SkyPearl.ITEM.get()))return net.minecraft.world.ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;useWithoutItem(state,level,pos,player,hit);return net.minecraft.world.ItemInteractionResult.sidedSuccess(level.isClientSide);}
 @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(net.minecraft.world.level.Level level,BlockState state,BlockEntityType<T> type){return level.isClientSide?createTickerHelper(type,ENTITY.get(),(world,pos,block,gate)->WarpGateClient.tick(gate)):null;}
 @Override protected void onRemove(BlockState state,net.minecraft.world.level.Level level,BlockPos pos,BlockState next,boolean moving){if(!state.is(next.getBlock())&&!level.isClientSide&&level.getBlockEntity(pos) instanceof WarpGateEntity gate)for(int i=0;i<10;i++){var pearl=gate.inventory.extractItem(i,1,false);if(!pearl.isEmpty())Block.popResource(level,pos,pearl);}super.onRemove(state,level,pos,next,moving);}
 static void research(){
  if(!dev.eugheist.tc4modern.WarpConfig.allowMirrors())return;
  var r=new KamiResearch("WARP_GATE",new thaumcraft.api.aspects.AspectList().add(thaumcraft.api.aspects.Aspect.TRAVEL,2).add(thaumcraft.api.aspects.Aspect.ELDRITCH,1).add(thaumcraft.api.aspects.Aspect.FLIGHT,1).add(thaumcraft.api.aspects.Aspect.MECHANISM,1),19,6,5,new net.minecraft.world.item.ItemStack(ITEM.get()));
  r.setParents("ICHORCLOTH_CHEST_GEM");r.setParentsHidden("ICHORCLOTH_BOOTS_GEM");r.setPages(new thaumcraft.api.research.ResearchPage("ttresearch.page.WARP_GATE.0"),MagnetResearch.page("WARP_GATE",thaumcraft.api.research.ResearchPage.PageType.INFUSION_CRAFTING),new thaumcraft.api.research.ResearchPage("ttresearch.page.WARP_GATE.1"),MagnetResearch.page("SKY_PEARL",thaumcraft.api.research.ResearchPage.PageType.INFUSION_CRAFTING)).registerResearchItem();
  ThaumicTinkererModern.INFUSION_RECIPES.add(new dev.eugheist.tc4modern.InfusionItemRecipes.Recipe("WARP_GATE","WARP_GATE",ITEM::get,8,new thaumcraft.api.aspects.AspectList().add(thaumcraft.api.aspects.Aspect.TRAVEL,64).add(thaumcraft.api.aspects.Aspect.ELDRITCH,50).add(thaumcraft.api.aspects.Aspect.FLIGHT,50),()->dev.eugheist.tc4modern.ThaumiumContent.BLOCK.get().asItem(),java.util.List.of(()->KamiResources.item(KamiResources.Kind.ICHOR),()->KamiResources.item(KamiResources.Kind.ENDER_SHARD),()->ThaumicTinkererModern.DISLOCATOR.get().asItem(),()->KamiResources.item(KamiResources.Kind.NETHER_SHARD),()->net.minecraft.world.item.Items.DIAMOND,()->net.minecraft.world.item.Items.FEATHER)));
 }
}
