package dev.eugheist.ttmodern;

import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

/** Recipe-driven Ordo conversion. No items are removed/created while resolving. */
final class OrderFireResolver {
 private static final Map<ServerLevel,Map<BlockState,Optional<BlockState>>> CACHE=new WeakHashMap<>();
 static void register(){NeoForge.EVENT_BUS.addListener(OrderFireResolver::reloadListeners);}
 private static void reloadListeners(AddReloadListenerEvent event){
  event.addListener((net.minecraft.server.packs.resources.ResourceManagerReloadListener) resources->clear());
 }
 static void clear(){CACHE.clear();}
 static boolean isResult(ServerLevel level,BlockState state){return CACHE.getOrDefault(level,Map.of()).values().stream().anyMatch(value->value.isPresent()&&value.get().getBlock()==state.getBlock());}
 static BlockState result(ServerLevel level,BlockPos pos){
  if(!level.hasChunkAt(pos))return null;
  var state=level.getBlockState(pos);
  // Build 164 seeds these entries with null so they can never become targets.
  if(excluded(state))return null;
  var cache=CACHE.computeIfAbsent(level,ignored->new HashMap<>());
  return cache.computeIfAbsent(state,ignored->Optional.ofNullable(resolve(level,state,drops(level,pos,state)))).orElse(null);
 }
 private static boolean excluded(BlockState state){
  return state.is(Blocks.STONE)||state.is(Blocks.GRANITE)||state.is(Blocks.POLISHED_GRANITE)||state.is(Blocks.DIORITE)||state.is(Blocks.POLISHED_DIORITE)||state.is(Blocks.ANDESITE)||state.is(Blocks.POLISHED_ANDESITE)||state.is(Blocks.GLASS)||state.is(Blocks.SAND)||state.is(Blocks.RED_SAND)||state.is(Blocks.GRAVEL);
 }
 private static List<ItemStack> drops(ServerLevel level,BlockPos pos,BlockState state){
  return Block.getDrops(state,level,pos,level.getBlockEntity(pos));
 }
 static int decayChance(ServerLevel level,BlockPos pos){return decayChance(drops(level,pos,level.getBlockState(pos)).size());}
 static int decayChance(int dropEntries){return dropEntries==2?2:dropEntries>=3?1:3;}
 static BlockState resolve(ServerLevel level,BlockState state,List<ItemStack> drops){
  if(excluded(state)||drops.isEmpty())return null;
  var first=drops.getFirst();
  if(first.isEmpty()||drops.stream().anyMatch(s->!ItemStack.isSameItem(first,s)))return null;
  // Before raw ores existed, vanilla iron/gold ore dropped the ore block itself.
  // Preserve that source conversion instead of inventing a raw-metal-block result.
  var input=first.copy();
  if(state.is(Blocks.IRON_ORE)&&input.is(Items.RAW_IRON))input=new ItemStack(Items.IRON_ORE);
  if(state.is(Blocks.GOLD_ORE)&&input.is(Items.RAW_GOLD))input=new ItemStack(Items.GOLD_ORE);
  var output=craftNine(level,input);
  if(output.isEmpty()){
   var single=new SingleRecipeInput(input);
   var smelt=level.getRecipeManager().getRecipeFor(RecipeType.SMELTING,single,level);
   output=smelt.map(r->craftNine(level,r.value().assemble(single,level.registryAccess()))).orElse(ItemStack.EMPTY);
  }
  return output.getItem() instanceof BlockItem block?block.getBlock().defaultBlockState():null;
 }
 static ItemStack craftNine(ServerLevel level,ItemStack stack){
  if(stack.isEmpty())return ItemStack.EMPTY;
  var slots=new ArrayList<ItemStack>(9);for(int i=0;i<9;i++)slots.add(stack.copy());
  var input=CraftingInput.of(3,3,slots);
  return level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING,input,level).map(r->r.value().assemble(input,level.registryAccess())).orElse(ItemStack.EMPTY);
 }
 private OrderFireResolver(){}
}
