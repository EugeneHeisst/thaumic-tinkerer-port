package dev.eugheist.ttmodern;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.*;
import net.minecraft.world.phys.AABB;
import thaumcraft.api.aspects.*;
final class InfusedHarvestWorldAudit {
 private static void check(boolean ok,String label){if(!ok)throw new IllegalStateException("TT crop world harvest: "+label);}
 static void run(ServerLevel level){
  var pos=new BlockPos(24,280,24);var area=new AABB(pos).inflate(1);var block=ThaumicTinkererModern.INFUSED_GRAIN.get();level.setBlockAndUpdate(pos.below(),ThaumicTinkererModern.INFUSED_FARMLAND.get().defaultBlockState());level.removeBlock(pos,false);level.getEntitiesOfClass(ItemEntity.class,area).forEach(net.minecraft.world.entity.Entity::discard);
  level.setBlockAndUpdate(pos,block.defaultBlockState());var crop=(InfusedGrainEntity)level.getBlockEntity(pos);crop.data(new InfusedSeedData(Aspect.AIR,new AspectList()));var soil=(InfusedFarmlandEntity)level.getBlockEntity(pos.below());
  check(Block.getDrops(level.getBlockState(pos),level,pos,crop).isEmpty()&&soil.aspects().visSize()==0,"loot queries never harvest or fertilize");level.setBlockAndUpdate(pos,level.getBlockState(pos).setValue(CropBlock.AGE,7));check(level.getBlockEntity(pos)==crop&&level.getEntitiesOfClass(ItemEntity.class,area).isEmpty(),"age update preserves crop without harvest");
  level.removeBlock(pos,false);var drops=level.getEntitiesOfClass(ItemEntity.class,area);check(drops.size()==2&&drops.stream().filter(e->e.getItem().is(ThaumicTinkererModern.INFUSED_SEEDS.get())).count()==1&&drops.stream().anyMatch(e->e.getItem().is(ThaumicTinkererModern.INFUSED_GRAIN_AIR.get())),"mature removal one seed plus parent grain exactly once");check(soil.aspects().getAmount(Aspect.AIR)==1,"mature removal fertilizes native soil");drops.forEach(net.minecraft.world.entity.Entity::discard);
  level.setBlockAndUpdate(pos,block.defaultBlockState());((InfusedGrainEntity)level.getBlockEntity(pos)).data(new InfusedSeedData(Aspect.FIRE,new AspectList()));level.destroyBlock(pos,true);drops=level.getEntitiesOfClass(ItemEntity.class,area);check(drops.size()==1&&drops.getFirst().getItem().is(ThaumicTinkererModern.INFUSED_SEEDS.get())&&InfusedSeedData.read(drops.getFirst().getItem()).mainAspect()==Aspect.FIRE,"immature destroyBlock no duplicated seed or grain");check(soil.aspects().visSize()==1,"immature harvest does not fertilize");drops.forEach(net.minecraft.world.entity.Entity::discard);level.removeBlock(pos.below(),false);
  System.out.println("TT CROP WORLD HARVEST AUDIT: ALL PASS (query purity,age preservation,mature seed/grain once,fertility,immature destruction without duplication)");
 }
}
