package dev.eugheist.ttmodern;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
/** Crop growth runs every loaded server tick through its entity, never vanilla random growth. */
public final class InfusedGrainBlock extends CropBlock implements EntityBlock {
 public static final IntegerProperty ASPECT=IntegerProperty.create("aspect",0,6);
 public static final MapCodec<InfusedGrainBlock> CODEC=simpleCodec(p->new InfusedGrainBlock());
 public InfusedGrainBlock(){super(Properties.ofFullCopy(Blocks.WHEAT));registerDefaultState(defaultBlockState().setValue(ASPECT,6));}
 @Override public MapCodec<InfusedGrainBlock> codec(){return CODEC;}
 @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){super.createBlockStateDefinition(builder);builder.add(ASPECT);}
 @Override protected void randomTick(BlockState state,ServerLevel level,BlockPos pos,RandomSource random){}
 @Override protected boolean mayPlaceOn(BlockState state,BlockGetter level,BlockPos pos){return state.getBlock() instanceof InfusedFarmlandBlock;}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new InfusedGrainEntity(pos,state);}
 @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return !level.isClientSide&&type==ThaumicTinkererModern.INFUSED_GRAIN_ENTITY.get()?(l,p,s,t)->InfusedGrainEntity.tick((ServerLevel)l,p,s,(InfusedGrainEntity)t):null;}
 @Override protected net.minecraft.world.level.ItemLike getBaseSeedId(){return ThaumicTinkererModern.INFUSED_SEEDS.get();}
 @Override public net.minecraft.world.item.ItemStack getCloneItemStack(LevelReader level,BlockPos pos,BlockState state){var stack=new net.minecraft.world.item.ItemStack(ThaumicTinkererModern.INFUSED_SEEDS.get());if(level.getBlockEntity(pos) instanceof InfusedGrainEntity crop)crop.data().write(stack);return stack;}
 @Override protected java.util.List<net.minecraft.world.item.ItemStack> getDrops(BlockState state,net.minecraft.world.level.storage.loot.LootParams.Builder params){return java.util.List.of();}
 @Override protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState replacement,boolean moving){
  if(!state.is(replacement.getBlock())&&level instanceof ServerLevel server&&level.getBlockEntity(pos) instanceof InfusedGrainEntity crop){
   var soil=level.getBlockEntity(pos.below()) instanceof InfusedFarmlandEntity farmland?farmland:null;
   var loot=InfusedCropLootRuntime.get(server);
   var drops=InfusedCropHarvest.collect(crop.data(),state.getValue(AGE),soil==null?null:soil.aspects(),level.random,a->{if(soil!=null)soil.fertilize(a);},a->loot.roll(a,level.random));
   for(var stack:drops){var entity=new net.minecraft.world.entity.item.ItemEntity(level,pos.getX()+.15+level.random.nextFloat()*.7,pos.getY()+.15+level.random.nextFloat()*.7,pos.getZ()+.15+level.random.nextFloat()*.7,stack);entity.setPickUpDelay(10);level.addFreshEntity(entity);}
  }
  super.onRemove(state,level,pos,replacement,moving);
 }
 static int texture(thaumcraft.api.aspects.Aspect aspect){
  var primals=new thaumcraft.api.aspects.Aspect[]{thaumcraft.api.aspects.Aspect.AIR,thaumcraft.api.aspects.Aspect.FIRE,thaumcraft.api.aspects.Aspect.WATER,thaumcraft.api.aspects.Aspect.EARTH,thaumcraft.api.aspects.Aspect.ORDER,thaumcraft.api.aspects.Aspect.ENTROPY};
  for(int i=0;i<primals.length;i++)if(aspect==primals[i])return i;return 6;
 }
}
