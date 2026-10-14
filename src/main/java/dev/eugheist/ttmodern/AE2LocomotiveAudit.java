package dev.eugheist.ttmodern;
/** Real AE2 handler dispatch, using a foreign TC4 jar rather than an addon movable tile. */
final class AE2LocomotiveAudit {
 static final Probe PROBE=new Probe();
 static int mode=-1,begins,completes;
 static void register(){appeng.api.movable.BlockEntityMoveStrategies.add(PROBE);}
 static final class Probe extends appeng.api.movable.DefaultBlockEntityMoveStrategy {
  public boolean canHandle(net.minecraft.world.level.block.entity.BlockEntityType<?> type){return type==dev.eugheist.tc4modern.EssentiaDevices.JAR_ENTITY.get();}
  public net.minecraft.nbt.CompoundTag beginMove(net.minecraft.world.level.block.entity.BlockEntity tile,net.minecraft.core.HolderLookup.Provider registries){if(mode>=0)begins++;return mode==1?null:super.beginMove(tile,registries);}
  public boolean completeMove(net.minecraft.world.level.block.entity.BlockEntity tile,net.minecraft.world.level.block.state.BlockState state,net.minecraft.nbt.CompoundTag tag,net.minecraft.world.level.Level level,net.minecraft.core.BlockPos pos){
   if(mode>=0)completes++;boolean success=super.completeMove(tile,state,tag,level,pos);
   if(mode>=2){((dev.eugheist.tc4modern.WardedJarEntity)level.getBlockEntity(pos)).state.amount=0;if(mode==3)throw new IllegalStateException("Expected audit handler exception after partial restore");return false;}
   return success;
  }
 }
 static void run(net.minecraft.server.level.ServerLevel level,net.minecraft.core.BlockPos start,net.minecraft.core.BlockPos left,net.minecraft.core.BlockPos right){
  try{
   for(int scenario=0;scenario<5;scenario++){
    for(var pos:java.util.List.of(start,start.above(),start.east(),start.east().above())){level.removeBlockEntity(pos);level.setBlock(pos,net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(),3);}
    level.setBlock(start,ThaumicTinkererModern.LOCOMOTIVE.get().defaultBlockState(),3);var train=(LocomotiveEntity)level.getBlockEntity(start);train.first=left;train.second=right;train.linked=true;train.direction=net.minecraft.core.Direction.EAST;
    level.setBlock(start.above(),(scenario==4?dev.eugheist.tc4modern.AlchemyMachines.VOID_JAR.get():dev.eugheist.tc4modern.EssentiaDevices.JAR.get()).defaultBlockState(),3);var jar=(dev.eugheist.tc4modern.WardedJarEntity)level.getBlockEntity(start.above());jar.state.aspect=thaumcraft.api.aspects.Aspect.AIR;jar.state.amount=17;jar.state.aspectFilter=thaumcraft.api.aspects.Aspect.AIR;var expected=jar.saveWithoutMetadata(level.registryAccess());
    LocomotiveAudit.check(!((Object)jar instanceof MovableTile),"foreign fixture is not addon movable");LocomotiveAudit.check(appeng.api.movable.BlockEntityMoveStrategies.get(jar)==PROBE,"foreign real AE2 strategy registry");
    LocomotiveAudit.check(jar.getBlockState().is(appeng.api.ids.AETags.SPATIAL_BLACKLIST)==(scenario==4),"actual nested relocation blacklist tag "+scenario);
    mode=scenario;begins=completes=0;long time=3001+100L*scenario;LocomotiveAudit.tick(level,start,time);
    if(scenario==0){LocomotiveAudit.check(level.isEmptyBlock(start.above())&&level.getBlockEntity(start.east()) instanceof LocomotiveEntity,"foreign passenger accepted");var moved=level.getBlockEntity(start.east().above());LocomotiveAudit.check(moved!=null&&moved!=jar&&moved.saveWithoutMetadata(level.registryAccess()).equals(expected),"foreign handler state conservation");}
    else if(scenario==1||scenario==4){LocomotiveAudit.check(level.getBlockEntity(start.above())==jar&&level.getBlockEntity(start.east()) instanceof LocomotiveEntity&&level.isEmptyBlock(start.east().above())&&jar.saveWithoutMetadata(level.registryAccess()).equals(expected),"foreign refusal leaves passenger intact and moves train");}
    else{LocomotiveAudit.check(level.getBlockEntity(start) instanceof LocomotiveEntity&&level.isEmptyBlock(start.east())&&level.isEmptyBlock(start.east().above())&&level.getBlockEntity(start.above()).saveWithoutMetadata(level.registryAccess()).equals(expected),"failed foreign handler rolls back partial state "+scenario);LocomotiveAudit.tick(level,start,time);}
    LocomotiveAudit.check(begins==(scenario==4?0:1)&&completes==(scenario==1||scenario==4?0:1),"one begin/complete and no same-tick retry "+scenario);
   }
   LocomotiveAudit.check(level.getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class,new net.minecraft.world.phys.AABB(start).inflate(6)).isEmpty(),"foreign movement no item drops");
   System.out.println("TT AE2 LOCOMOTIVE AUDIT: ALL PASS (foreign TC4 jar,real custom registry dispatch,accept/refuse,nested blacklist skips handler,partial failure/exception rollback,aspect/filter conservation,same-tick guard,no drops)");
  }finally{mode=-1;}
 }
}
