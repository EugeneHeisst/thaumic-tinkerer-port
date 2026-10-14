package dev.eugheist.ttmodern;
final class MagnetPeripheralAudit {
 static void run(net.minecraft.server.level.ServerLevel world,net.minecraft.core.BlockPos at){
  var power=at.above();var oldPower=world.getBlockState(power);var powerTile=world.getBlockEntity(power);var powerData=powerTile==null?null:powerTile.saveWithFullMetadata(world.registryAccess());
  try{world.setBlock(power,net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(),3);
   for(var block:java.util.List.of(ThaumicTinkererModern.MAGNET.get(),ThaumicTinkererModern.MOB_MAGNET.get())){
    world.setBlock(at,block.defaultBlockState(),3);var tile=(MagnetEntity)world.getBlockEntity(at);tile.adult=false;var filter=new net.minecraft.world.item.ItemStack(ThaumicTinkererModern.SOUL_MOULD.get());tile.inventory.setStackInSlot(0,filter);var before=tile.saveWithoutMetadata(world.registryAccess());
    var p=(MagnetPeripheral)world.getCapability(dan200.computercraft.api.peripheral.PeripheralCapability.get(),at,net.minecraft.core.Direction.UP);
    if(p==null||!p.getType().equals("tt_magnet")||!p.isPulling()||p.getSignal()!=0||!p.equals(world.getCapability(dan200.computercraft.api.peripheral.PeripheralCapability.get(),at,net.minecraft.core.Direction.DOWN)))throw new IllegalStateException("CC magnet identity/default signal");
    for(var side:net.minecraft.core.Direction.values())if(!p.equals(world.getCapability(dan200.computercraft.api.peripheral.PeripheralCapability.get(),at,side)))throw new IllegalStateException("CC magnet face "+side);
    if(tile.mobs()){if(!(p instanceof MobMagnetPeripheral mob)||mob.getAdultSearch())throw new IllegalStateException("CC mob magnet age query");((MobMagnetPeripheral)p).setAdultSearch(true);if(!tile.adult)throw new IllegalStateException("CC mob magnet age setter");((MobMagnetPeripheral)p).setAdultSearch(false);}else if(p instanceof MobMagnetPeripheral)throw new IllegalStateException("CC item magnet must not expose mob commands");
    p.setPulling(false);if(p.isPulling()||world.getBlockEntity(at)!=tile||!tile.saveWithoutMetadata(world.registryAccess()).equals(before))throw new IllegalStateException("CC magnet pushing preserves entity/filter");
    world.setBlock(power,net.minecraft.world.level.block.Blocks.REDSTONE_BLOCK.defaultBlockState(),3);if(p.getSignal()!=15)throw new IllegalStateException("CC magnet powered signal");p.setPulling(true);if(!p.isPulling()||!tile.saveWithoutMetadata(world.registryAccess()).equals(before))throw new IllegalStateException("CC magnet pulling preserves filter");world.setBlock(power,net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(),3);if(p.getSignal()!=0)throw new IllegalStateException("CC magnet signal update");var saved=tile.saveWithoutMetadata(world.registryAccess());tile.inventory.setStackInSlot(0,net.minecraft.world.item.ItemStack.EMPTY);
    world.setBlock(at,net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(),3);if(world.getCapability(dan200.computercraft.api.peripheral.PeripheralCapability.get(),at,net.minecraft.core.Direction.UP)!=null)throw new IllegalStateException("CC magnet capability remains after removal");
    world.setBlock(at,block.defaultBlockState(),3);var replacement=(MagnetEntity)world.getBlockEntity(at);replacement.loadWithComponents(saved,world.registryAccess());var fresh=(MagnetPeripheral)world.getCapability(dan200.computercraft.api.peripheral.PeripheralCapability.get(),at,net.minecraft.core.Direction.UP);
    if(fresh==null||fresh.equals(p)||!fresh.isPulling()||!replacement.saveWithoutMetadata(world.registryAccess()).equals(saved))throw new IllegalStateException("CC magnet reload identity/filter persistence");
    p.setPulling(false);if(!fresh.isPulling())throw new IllegalStateException("CC removed magnet mutates replacement polarity");if(p instanceof MobMagnetPeripheral stale){stale.setAdultSearch(true);if(replacement.adult||tile.adult)throw new IllegalStateException("CC removed mob magnet mutates age");}
    replacement.inventory.setStackInSlot(0,net.minecraft.world.item.ItemStack.EMPTY);
   }
   System.out.println("TT CC MAGNET PERIPHERAL AUDIT: ALL PASS (both native variants,identity,six-face capability,pull/push,signal0/15/0,entity/filter preservation,removal capability invalidation,NBT reload,new identity,stale command isolation)");
  }finally{world.setBlock(power,oldPower,3);if(powerData!=null&&world.getBlockEntity(power)!=null)world.getBlockEntity(power).loadWithComponents(powerData,world.registryAccess());}
 }
}
