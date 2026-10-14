package dev.eugheist.ttmodern;
/** Exercises the actual installed AE2 strategy registry and serialization callbacks. */
final class AE2MovableAudit {
 static void check(boolean valid,String label){if(!valid)throw new IllegalStateException("AE2 movable "+label);}
 static void run(net.minecraft.server.MinecraftServer server){
  var world=server.overworld();var source=new net.minecraft.core.BlockPos(14,310,14);var target=source.east();
  var old=new java.util.LinkedHashMap<net.minecraft.core.BlockPos,net.minecraft.world.level.block.state.BlockState>();var tags=new java.util.HashMap<net.minecraft.core.BlockPos,net.minecraft.nbt.CompoundTag>();
  for(var pos:java.util.List.of(source,target,source.below(),target.below())){old.put(pos,world.getBlockState(pos));var tile=world.getBlockEntity(pos);if(tile!=null)tags.put(pos,tile.saveWithFullMetadata(world.registryAccess()));}
  var blocks=new java.util.ArrayList<net.minecraft.world.level.block.Block>(java.util.List.of(ThaumicTinkererModern.PLATFORM.get(),ThaumicTinkererModern.INTERFACE.get(),ThaumicTinkererModern.DISLOCATOR.get(),ThaumicTinkererModern.MAGNET.get(),ThaumicTinkererModern.MOB_MAGNET.get(),ThaumicTinkererModern.LOCOMOTIVE_RELAY.get(),ThaumicTinkererModern.ANIMATION_TABLET.get(),ThaumicTinkererModern.FUNNEL.get(),ThaumicTinkererModern.REPAIRER.get(),ThaumicTinkererModern.ENCHANTER.get()));
  if(net.neoforged.fml.ModList.get().isLoaded("computercraft")){blocks.add(AspectAnalyzer.BLOCK.get());blocks.add(GolemConnector.BLOCK.get());}
  try{
   for(var block:blocks){
    for(var pos:java.util.List.of(source,target)){world.removeBlockEntity(pos);world.setBlock(pos,net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(),3);}
    for(var pos:java.util.List.of(source.below(),target.below()))world.setBlock(pos,net.minecraft.world.level.block.Blocks.HOPPER.defaultBlockState(),3);
    var state=block.defaultBlockState();world.setBlock(source,state,3);var tile=world.getBlockEntity(source);check(tile instanceof MovableTile,"contract "+block);
    var inventory=world.getCapability(net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK,source,net.minecraft.core.Direction.UP);
    if(inventory instanceof net.neoforged.neoforge.items.IItemHandlerModifiable handler){
     var item=tile instanceof FunnelEntity?new net.minecraft.world.item.ItemStack(dev.eugheist.tc4modern.EssentiaDevices.JAR.get()):tile instanceof MagnetEntity?new net.minecraft.world.item.ItemStack(ThaumicTinkererModern.SOUL_MOULD.get()):new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.DIAMOND_PICKAXE);
     if(item.isDamageableItem())item.setDamageValue(43);item.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME,net.minecraft.network.chat.Component.literal("Spatial inventory conservation"));handler.setStackInSlot(0,item);
    }
    if(tile instanceof PlatformEntity platform)platform.camouflage(net.minecraft.world.level.block.Blocks.STONE.defaultBlockState());
    if(tile instanceof AnimationTabletEntity tablet){tablet.breaking=true;tablet.damage=.7f;}
    var expected=tile.saveWithoutMetadata(world.registryAccess());var strategy=appeng.api.movable.BlockEntityMoveStrategies.get(tile);
    check(strategy instanceof AE2Compat.Strategy,"actual registry chooses addon strategy "+block);var data=strategy.beginMove(tile,world.registryAccess());check(data!=null,"beginMove permits "+block);
    if(tile instanceof AnimationTabletEntity tablet)check(!tablet.breaking&&tablet.damage==0,"prepare cancels mining");
    world.removeBlockEntity(source);world.setBlock(source,net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(),net.minecraft.world.level.block.Block.UPDATE_CLIENTS|net.minecraft.world.level.block.Block.UPDATE_KNOWN_SHAPE);world.setBlock(target,state,net.minecraft.world.level.block.Block.UPDATE_CLIENTS|net.minecraft.world.level.block.Block.UPDATE_KNOWN_SHAPE);
    check(strategy.completeMove(tile,state,data,world,target),"completeMove succeeds "+block);var moved=world.getBlockEntity(target);
    check(moved!=null&&moved!=tile&&moved.getBlockPos().equals(target)&&moved.getType()==tile.getType(),"new identity/location "+block);check(moved.saveWithoutMetadata(world.registryAccess()).equals(expected),"all saved state conserved "+block);
    if(inventory!=null)check(world.getCapability(net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK,target,net.minecraft.core.Direction.UP)!=null,"new capability "+block);
    var cell=appeng.core.definitions.AEItems.SPATIAL_CELL2.stack();var cellItem=appeng.core.definitions.AEItems.SPATIAL_CELL2.get();int plotId=-1;
    try{
     var min=target.offset(-1,-2,-1);var max=min.offset(2,3,2);
     check(cellItem.doSpatialTransition(cell,world,min,max,-1),"real cell store "+block);plotId=cellItem.getAllocatedPlotId(cell);var plot=appeng.spatial.SpatialStoragePlotManager.INSTANCE.getPlot(plotId);check(plot!=null,"allocated spatial plot");var spatial=appeng.spatial.SpatialStoragePlotManager.INSTANCE.getLevel();var storedPos=plot.getOrigin().above();var stored=spatial.getBlockEntity(storedPos);
     check(world.isEmptyBlock(target)&&stored!=null&&stored.getType()==tile.getType()&&stored.getLevel()==spatial&&stored.getBlockPos().equals(storedPos),"actual cross-dimension storage "+block);
     check(stored.saveWithoutMetadata(spatial.registryAccess()).equals(expected),"actual spatial inventory/data preserved "+block);
     check(cellItem.doSpatialTransition(cell,world,min,max,-1),"real cell retrieve "+block);var retrieved=world.getBlockEntity(target);
     check(retrieved!=null&&retrieved!=stored&&retrieved.saveWithoutMetadata(world.registryAccess()).equals(expected)&&spatial.isEmptyBlock(storedPos),"actual spatial round trip "+block);
    }finally{if(plotId>=0)appeng.spatial.SpatialStoragePlotManager.INSTANCE.freePlot(plotId,true);}
   }
   System.out.println("TT AE2 SPATIAL CELL AUDIT: ALL PASS ("+blocks.size()+" addon variants,real cell allocation/store/retrieve,actual spatial dimension,hopper support,inventory/NBT/camouflage round trip)");
   check(world.getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class,new net.minecraft.world.phys.AABB(source).inflate(3)).isEmpty(),"no spilled inventory");
   System.out.println("TT AE2 MOVABLE AUDIT: ALL PASS ("+blocks.size()+" native addon variants,actual AE2 strategy registry,prepare/restore callbacks,new identity/location,inventory/NBT/camouflage conservation,capabilities,no drops)");
  }finally{for(var pos:old.keySet()){world.removeBlockEntity(pos);world.setBlock(pos,old.get(pos),net.minecraft.world.level.block.Block.UPDATE_CLIENTS|net.minecraft.world.level.block.Block.UPDATE_KNOWN_SHAPE);}tags.forEach((pos,data)->{var tile=world.getBlockEntity(pos);if(tile!=null)tile.loadWithComponents(data,world.registryAccess());});}
 }
}
