package dev.eugheist.ttmodern;

/** Optional modern equivalent of the original addon IMovableTile callbacks. */
final class AE2Compat {
 static void init(){appeng.api.movable.BlockEntityMoveStrategies.add(new Strategy());if(Boolean.getBoolean("ttinkerer.portAudit"))AE2LocomotiveAudit.register();}
 static PassengerTransfer begin(net.minecraft.world.level.block.entity.BlockEntity entity){
  var level=entity.getLevel();
  if(level==null||entity.getBlockState().is(appeng.api.ids.AETags.SPATIAL_BLACKLIST))return null;
  var strategy=appeng.api.movable.BlockEntityMoveStrategies.get(entity);
  var data=strategy.beginMove(entity,level.registryAccess());
  if(data==null)return null;
  return (destination,pos,state)->{
   destination.setBlock(pos,state,net.minecraft.world.level.block.Block.UPDATE_CLIENTS|net.minecraft.world.level.block.Block.UPDATE_KNOWN_SHAPE);
   if(!strategy.completeMove(entity,state,data,destination,pos))return false;
   var moved=destination.getBlockEntity(pos);
   if(moved==null||moved==entity||!moved.getBlockPos().equals(pos))return false;
   moved.setChanged();destination.invalidateCapabilities(pos);destination.sendBlockUpdated(pos,state,state,3);
   return true;
  };
 }
 static final class Strategy extends appeng.api.movable.DefaultBlockEntityMoveStrategy {
  public boolean canHandle(net.minecraft.world.level.block.entity.BlockEntityType<?> type){
   var types=java.util.Set.of(ThaumicTinkererModern.PLATFORM_ENTITY.get(),ThaumicTinkererModern.TRANSVECTOR_ENTITY.get(),ThaumicTinkererModern.DISLOCATOR_ENTITY.get(),ThaumicTinkererModern.MAGNET_ENTITY.get(),ThaumicTinkererModern.LOCOMOTIVE_RELAY_ENTITY.get(),ThaumicTinkererModern.ANIMATION_TABLET_ENTITY.get(),ThaumicTinkererModern.FUNNEL_ENTITY.get(),ThaumicTinkererModern.REPAIRER_ENTITY.get(),ThaumicTinkererModern.ENCHANTER_ENTITY.get());
   if(types.contains(type))return true;
   return net.neoforged.fml.ModList.get().isLoaded("computercraft")&&(type==AspectAnalyzer.ENTITY.get()||type==GolemConnector.ENTITY.get());
  }
  public net.minecraft.nbt.CompoundTag beginMove(net.minecraft.world.level.block.entity.BlockEntity entity,net.minecraft.core.HolderLookup.Provider registries){
   return entity instanceof MovableTile movable&&movable.prepareToMove()?super.beginMove(entity,registries):null;
  }
  public boolean completeMove(net.minecraft.world.level.block.entity.BlockEntity original,net.minecraft.world.level.block.state.BlockState state,net.minecraft.nbt.CompoundTag data,net.minecraft.world.level.Level level,net.minecraft.core.BlockPos pos){
   if(!super.completeMove(original,state,data,level,pos))return false;
   if(level.getBlockEntity(pos) instanceof MovableTile moved)moved.doneMoving();
   level.invalidateCapabilities(pos);var moved=level.getBlockEntity(pos);if(moved!=null)moved.setChanged();
   return true;
  }
 }
}
