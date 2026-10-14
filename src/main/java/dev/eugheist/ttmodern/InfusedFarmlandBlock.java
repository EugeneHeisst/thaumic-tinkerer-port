package dev.eugheist.ttmodern;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.*;
import net.neoforged.neoforge.common.util.TriState;
/** Original permanent, full-height farmland; hydrated appearance does not require nearby water. */
public final class InfusedFarmlandBlock extends FarmBlock implements EntityBlock {
 public static final MapCodec<FarmBlock> CODEC=simpleCodec(p->new InfusedFarmlandBlock());
 public InfusedFarmlandBlock(){super(Properties.ofFullCopy(Blocks.FARMLAND).strength(.6f));}
 @Override public MapCodec<FarmBlock> codec(){return CODEC;}
 @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return Shapes.block();}
 @Override protected void randomTick(BlockState state,ServerLevel level,BlockPos pos,RandomSource random){}
 @Override public void fallOn(Level level,BlockState state,BlockPos pos,Entity entity,float distance){}
 @Override public TriState canSustainPlant(BlockState state,BlockGetter level,BlockPos pos,Direction direction,BlockState plant){
  return (net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(plant.getBlock()).equals(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(ThaumicTinkererModern.ID,"infused_grain")))?TriState.TRUE:TriState.FALSE;
 }
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new InfusedFarmlandEntity(pos,state);}
}
