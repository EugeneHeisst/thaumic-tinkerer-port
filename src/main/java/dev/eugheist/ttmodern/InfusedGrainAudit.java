package dev.eugheist.ttmodern;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.*;
import thaumcraft.api.aspects.*;
final class InfusedGrainAudit {
 private static void check(boolean ok,String label){if(!ok)throw new IllegalStateException("TT infused grain: "+label);}
 static void run(ServerLevel level){
  var pos=new BlockPos(14,280,14);var other=pos.east();var block=ThaumicTinkererModern.INFUSED_GRAIN.get();
  for(var p:java.util.List.of(pos,other)){level.setBlockAndUpdate(p.below(),ThaumicTinkererModern.INFUSED_FARMLAND.get().defaultBlockState());level.setBlockAndUpdate(p,block.defaultBlockState());}
  var crop=(InfusedGrainEntity)level.getBlockEntity(pos);var donor=(InfusedGrainEntity)level.getBlockEntity(other);crop.data(new InfusedSeedData(Aspect.MAGIC,new AspectList().add(Aspect.WATER,50)));donor.data(new InfusedSeedData(Aspect.FIRE,new AspectList()));
  check(level.getBlockState(pos).canSurvive(level,pos),"infused farmland supports grain");check(!block.defaultBlockState().canSurvive(level,pos.above()),"ordinary block rejected as soil");
  check(level.getBlockState(pos).getValue(InfusedGrainBlock.ASPECT)==6&&level.getBlockState(other).getValue(InfusedGrainBlock.ASPECT)==1,"compound and primal texture state");
  for(int i=0;i<10000;i++)InfusedGrainEntity.tick(level,pos,level.getBlockState(pos),crop);
  check(level.getBlockState(pos).getValue(CropBlock.AGE)==7,"real world growth reaches maturity");crop.data(new InfusedSeedData(Aspect.MAGIC,new AspectList().add(Aspect.AIR,50)));for(int i=0;i<100000;i++)InfusedGrainEntity.tick(level,pos,level.getBlockState(pos),crop);check(crop.data().amount(Aspect.FIRE)>0&&donor.data().amount(Aspect.FIRE)==0,"real adjacent primal exchange without donor debit");check(crop.data().tendencies().visSize()<=50,"world tendency budget");
  var saved=crop.saveWithFullMetadata(level.registryAccess());var loaded=(InfusedGrainEntity)net.minecraft.world.level.block.entity.BlockEntity.loadStatic(pos,level.getBlockState(pos),saved,level.registryAccess());check(loaded.data().tendencies().aspects.equals(crop.data().tendencies().aspects)&&loaded.data().mainAspect()==Aspect.MAGIC,"native persistence");
  check(block.getTicker(level,block.defaultBlockState(),ThaumicTinkererModern.INFUSED_GRAIN_ENTITY.get())!=null,"registered server ticker");
  check(ThaumicTinkererModern.INFUSED_FARMLAND.get().canSustainPlant(level.getBlockState(pos.below()),level,pos.below(),net.minecraft.core.Direction.UP,block.defaultBlockState())==net.neoforged.neoforge.common.util.TriState.TRUE,"exclusive plant accepted");
  for(var p:java.util.List.of(pos,other)){level.removeBlock(p,false);level.removeBlock(p.below(),false);}
  System.out.println("TT INFUSED GRAIN AUDIT: ALL PASS (native growth to maturity,neighbor transfer,budget,aspect models,soil support,persistence,ticker)");
 }
}
