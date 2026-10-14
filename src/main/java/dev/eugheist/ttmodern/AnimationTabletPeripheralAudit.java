package dev.eugheist.ttmodern;
final class AnimationTabletPeripheralAudit {
 static void check(boolean valid,String message){if(!valid)throw new IllegalStateException("CC tablet "+message);}
 static void run(net.minecraft.server.level.ServerLevel world,net.minecraft.core.BlockPos at){
  world.setBlock(at,ThaumicTinkererModern.ANIMATION_TABLET.get().defaultBlockState(),3);
  var tile=(AnimationTabletEntity)world.getBlockEntity(at);
  var p=(AnimationTabletPeripheral)world.getCapability(dan200.computercraft.api.peripheral.PeripheralCapability.get(),at,net.minecraft.core.Direction.UP);
  check(p!=null&&p.getType().equals("tt_animationTablet")&&p.getTarget()==tile,"native capability/type");
  for(var side:net.minecraft.core.Direction.values())check(p.equals(world.getCapability(dan200.computercraft.api.peripheral.PeripheralCapability.get(),at,side)),"six faces");
  check(!p.getRedstone()&&p.getLeftClick()&&p.getRotation()==0&&!p.hasItem(),"defaults");
  p.setRedstone(true);p.setLeftClick(false);
  check(p.getRedstone()&&!p.getLeftClick(),"mode setters");
  check(p.trigger()&&!p.trigger()&&tile.swingProgress==1,"original empty trigger succeeds and busy refuses");
  AnimationTabletEntity.tick(world,at,tile.getBlockState(),tile);
  check(tile.swingProgress==0,"empty swing resets on tick");
  var stack=new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.DIAMOND_PICKAXE);stack.setDamageValue(17);
  stack.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME,net.minecraft.network.chat.Component.literal("Tablet CC tool"));
  tile.inventory.setStackInSlot(0,stack.copy());check(p.hasItem(),"occupied query");
  try{
   for(int rotation=0;rotation<4;rotation++){p.setRotation(rotation+.9);check(p.getRotation()==rotation&&world.getBlockEntity(at)==tile,"original fractional rotation "+rotation);}
   for(double invalid:new double[]{-1,4,Double.NaN,Double.POSITIVE_INFINITY}){int before=p.getRotation();boolean rejected=false;try{p.setRotation(invalid);}catch(dan200.computercraft.api.lua.LuaException expected){rejected=true;}check(rejected&&p.getRotation()==before,"invalid rotation conserves state");}
  }catch(dan200.computercraft.api.lua.LuaException e){throw new IllegalStateException(e);}
  check(net.minecraft.world.item.ItemStack.matches(stack,tile.inventory.getStackInSlot(0)),"rotation preserves named damaged tool");
  check(p.trigger()&&!p.trigger(),"equipped trigger/busy");
  var saved=tile.saveWithoutMetadata(world.registryAccess());tile.inventory.setStackInSlot(0,net.minecraft.world.item.ItemStack.EMPTY);
  world.setBlock(at,net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(),3);
  check(world.getCapability(dan200.computercraft.api.peripheral.PeripheralCapability.get(),at,net.minecraft.core.Direction.UP)==null,"removed capability");
  world.setBlock(at,ThaumicTinkererModern.ANIMATION_TABLET.get().defaultBlockState().setValue(AnimationTabletBlock.FACING,net.minecraft.core.Direction.EAST),3);
  var replacement=(AnimationTabletEntity)world.getBlockEntity(at);replacement.loadWithComponents(saved,world.registryAccess());
  var fresh=(AnimationTabletPeripheral)world.getCapability(dan200.computercraft.api.peripheral.PeripheralCapability.get(),at,net.minecraft.core.Direction.UP);
  check(fresh!=null&&!fresh.equals(p)&&replacement.saveWithoutMetadata(world.registryAccess()).equals(saved),"reload/new identity");
  p.setLeftClick(true);p.setRedstone(false);try{p.setRotation(0);}catch(dan200.computercraft.api.lua.LuaException e){throw new IllegalStateException(e);}
  check(!p.trigger()&&!fresh.getLeftClick()&&fresh.getRedstone()&&fresh.getRotation()==3&&replacement.saveWithoutMetadata(world.registryAccess()).equals(saved),"stale commands isolated");
  replacement.inventory.setStackInSlot(0,net.minecraft.world.item.ItemStack.EMPTY);
  System.out.println("TT CC TABLET PERIPHERAL AUDIT: ALL PASS (eight methods,six faces,modes,fractional rotation,invalid input,empty/equipped trigger,busy refusal,named tool/NBT conservation,removal/replacement/stale isolation)");
 }
}
