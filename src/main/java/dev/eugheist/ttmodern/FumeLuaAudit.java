package dev.eugheist.ttmodern;
/** Real turtle program, sharing only the isolated parent audit's forced chunk. */
final class FumeLuaAudit {
 static final net.minecraft.core.BlockPos AT=new net.minecraft.core.BlockPos(6,300,6);
 static dan200.computercraft.shared.turtle.blocks.TurtleBlockEntity turtle;
 static dan200.computercraft.api.filesystem.WritableMount mount;
 static net.minecraft.server.level.ServerLevel world;
 static boolean complete;
 static int remoteStage;
 static final java.util.Map<net.minecraft.core.BlockPos,net.minecraft.world.level.block.state.BlockState> blocks=new java.util.LinkedHashMap<>();
 static final java.util.Map<net.minecraft.core.BlockPos,net.minecraft.nbt.CompoundTag> tags=new java.util.HashMap<>();
 static void start(net.minecraft.server.level.ServerLevel level)throws java.io.IOException {
  world=level;complete=false;remoteStage=0;
  for(var p:java.util.List.of(AT,AT.west(3),AT.above(),AT.east(3),AT.north(),AT.south(),AT.south(2),AT.east(),AT.east(2),AT.west(),AT.west(2),AT.below())){blocks.put(p,world.getBlockState(p));var tile=world.getBlockEntity(p);if(tile!=null)tags.put(p,tile.saveWithFullMetadata(world.registryAccess()));}
  var block=net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(net.minecraft.resources.ResourceLocation.parse("computercraft:turtle_normal"));world.setBlock(AT,block.defaultBlockState(),3);turtle=(dan200.computercraft.shared.turtle.blocks.TurtleBlockEntity)world.getBlockEntity(AT);turtle.setDirection(net.minecraft.core.Direction.NORTH);
  var access=turtle.getAccess();access.setFuelLevel(37);access.getInventory().setItem(0,new net.minecraft.world.item.ItemStack(ThaumicTinkererModern.GAS_REMOVER.get()));access.getInventory().setItem(1,new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.DIAMOND,3));
  world.setBlock(AT.west(3),ThaumicTinkererModern.LIGHT_GAS.get().defaultBlockState(),3);world.setBlock(AT.above(),ThaumicTinkererModern.NITOR_LIGHT.get().defaultBlockState(),3);world.setBlock(AT.east(3),ThaumicTinkererModern.SHADOW_GAS.get().defaultBlockState(),3);world.setBlock(AT.north(),net.minecraft.world.level.block.Blocks.STONE.defaultBlockState(),3);
  world.setBlock(AT.south(),ThaumicTinkererModern.MAGNET.get().defaultBlockState(),3);world.setBlock(AT.east(),ThaumicTinkererModern.MOB_MAGNET.get().defaultBlockState(),3);world.setBlock(AT.south(2),net.minecraft.world.level.block.Blocks.REDSTONE_BLOCK.defaultBlockState(),3);world.setBlock(AT.east(2),net.minecraft.world.level.block.Blocks.REDSTONE_BLOCK.defaultBlockState(),3);
  world.setBlock(AT.west(),ThaumicTinkererModern.INTERFACE.get().defaultBlockState(),3);if(!((TransvectorEntity)world.getBlockEntity(AT.west())).link(AT.south()))throw new IllegalStateException("Lua transvector link");
  world.setBlock(AT.below(),ThaumicTinkererModern.ANIMATION_TABLET.get().defaultBlockState(),3);var tablet=(AnimationTabletEntity)world.getBlockEntity(AT.below());tablet.redstone=true;tablet.inventory.setStackInSlot(0,new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.DIAMOND_PICKAXE));
  var computer=turtle.createServerComputer();mount=computer.createRootMount();String program="""
   local ok, err = pcall(function()
     local remote = assert(peripheral.wrap('left'), 'transvector peripheral discovery')
     assert(peripheral.getType('left') == 'tt_magnet', 'forwarded peripheral type')
     assert(remote.isPulling() and remote.getSignal() == 15, 'forwarded annotated queries')
     assert(select('#', remote.setPulling(false)) == 0, 'forwarded setter arity')
     assert(not remote.isPulling(), 'forwarded setter native effect')
     remote.setPulling(true)
     local a,b = assert(peripheral.wrap('back')), assert(peripheral.wrap('right'))
     assert(a and b, 'both magnet peripherals discovered')
     local age_count = 0
     for _, magnet in ipairs({a,b}) do
       if magnet.getAdultSearch then
         age_count = age_count + 1
         assert(magnet.getAdultSearch(), 'mob age defaults adult')
         assert(select('#', magnet.setAdultSearch(false)) == 0, 'age setter no returns')
         assert(not magnet.getAdultSearch(), 'mob child search')
         assert(not pcall(magnet.setAdultSearch, 1), 'age setter boolean validation')
         assert(not magnet.getAdultSearch(), 'invalid age setter conservation')
       end
       assert(magnet.isPulling() and magnet.getSignal() == 15, 'magnet initial state/signal')
       assert(select('#', magnet.setPulling(false)) == 0, 'setPulling has no return values')
       assert(not magnet.isPulling(), 'magnet actual pushing')
       assert(not pcall(magnet.setPulling, 'false'), 'magnet rejects nonboolean without changing state')
       assert(not magnet.isPulling(), 'invalid setter preserves state')
       magnet.setPulling(true)
       assert(magnet.isPulling(), 'magnet actual pulling')
       magnet.setPulling(false)
     end
     assert(age_count == 1, 'only mob magnet exposes age methods')
     local function stage(n, ready)
       local request = assert(fs.open('remote-request', 'w'))
       request.write(tostring(n)); request.close()
       for i=1,200 do
         if fs.exists('remote-ready-'..n) and ready() then return end
         sleep(0.05)
       end
       error('remote lifecycle stage timeout '..n)
     end
     stage(1, function() local p=peripheral.wrap('left'); return p and p.getAdultSearch ~= nil end)
     local mob = assert(peripheral.wrap('left'))
     assert(not mob.getAdultSearch(), 'rebound mob retains child search')
     mob.setAdultSearch(true); assert(mob.getAdultSearch(), 'rebound additional command')
     mob.setAdultSearch(false)
     remote.setPulling(true)
     assert(mob.isPulling(), 'cached Lua wrapper follows new target')
     mob.setPulling(false)
     stage(2, function() return peripheral.getType('left') == 'Transvector Interface Unconnected Peripherad' end)
     assert(#peripheral.getMethods('left') == 0, 'removed target exposes no methods')
     stage(3, function() local p=peripheral.wrap('left'); return p and p.getAdultSearch ~= nil end)
     local restored = assert(peripheral.wrap('left'))
     assert(restored.isPulling() and restored.getAdultSearch(), 'replacement native default state')
     restored.setPulling(false); restored.setAdultSearch(false)
     stage(4, function() return peripheral.getType('left') == 'tt_dynamic_probe' end)
     local probe = assert(peripheral.wrap('left'))
     local text,number,table = probe.echo('hello', 42, {value=7})
     assert(text == 'hello' and number == 42 and table.value == 7, 'dynamic multiple values/table forwarding')
     local attached,detached = probe.getLifecycle()
     assert(attached == 1 and detached == 0, 'target receives one attach callback')
     local token,message = probe.wait(73)
     assert(token == 73 and message == 'resumed', 'dynamic target yielded event callback preserved')
     stage(5, function() local p=peripheral.wrap('left'); return peripheral.getType('left') == 'tt_magnet' and p and not p.getAdultSearch end)
     local tablet = assert(peripheral.wrap('bottom'), 'tablet discovery')
     assert(peripheral.getType('bottom') == 'tt_animationTablet', 'original tablet type')
     assert(#peripheral.getMethods('bottom') == 8, 'eight original commands')
     assert(tablet.hasItem() and tablet.getLeftClick() and tablet.getRedstone(), 'native tablet initial state')
     assert(select('#', tablet.setRedstone(false)) == 0 and not tablet.getRedstone(), 'redstone setter arity/state')
     tablet.setRedstone(true)
     assert(select('#', tablet.setLeftClick(false)) == 0 and not tablet.getLeftClick(), 'left click setter arity/state')
     assert(not pcall(tablet.setLeftClick, 1) and not pcall(tablet.setRedstone, 'true'), 'boolean validation')
     for rotation=0,3 do
       assert(select('#', tablet.setRotation(rotation + 0.9)) == 0, 'rotation setter arity')
       assert(tablet.getRotation() == rotation, 'original rotation mapping/truncation')
     end
     assert(not pcall(tablet.setRotation, 4) and not pcall(tablet.setRotation, -1), 'invalid rotation rejected')
     assert(tablet.getRotation() == 3, 'invalid rotation conserves state')
     assert(tablet.trigger() and not tablet.trigger(), 'trigger starts actual swing and refuses busy')
     assert(turtle.select(1))
     assert(turtle.equipLeft(), 'equip gas remover from inventory')
     assert(turtle.getItemCount(1) == 0, 'equip consumes one upgrade item')
     assert(not turtle.attack(), 'fume tool attack must fail')
     assert(turtle.dig(), 'actual DIG dispatch')
     assert(turtle.digUp(), 'empty DIG UP succeeds')
     assert(turtle.digDown(), 'empty DIG DOWN succeeds')
     assert(turtle.getFuelLevel() == 37, 'no fuel consumed')
     assert(turtle.getItemCount(2) == 3, 'inventory conserved')
     assert(turtle.equipLeft(), 'unequip to inventory')
     assert(turtle.getItemDetail(1).name == 'ttinkerer:gas_remover', 'original upgrade item returned')
     assert(turtle.getItemCount(1) == 1, 'one upgrade returned')
   end)
   local f = assert(fs.open('fume-result', 'w'))
   f.write(ok and 'PASS' or ('FAIL: '..tostring(err)))
   f.close()
   """;
  try(var output=mount.openFile("startup.lua",java.util.Set.of(java.nio.file.StandardOpenOption.CREATE,java.nio.file.StandardOpenOption.WRITE,java.nio.file.StandardOpenOption.TRUNCATE_EXISTING))){output.write(java.nio.ByteBuffer.wrap(program.getBytes(java.nio.charset.StandardCharsets.UTF_8)));}turtle.updateInputsImmediately();computer.turnOn();
 }
 static void tick()throws java.io.IOException {
  if(complete||turtle==null)return;turtle.createServerComputer().keepAlive();remoteLifecycle();if(!mount.exists("fume-result"))return;String result;try(var input=mount.openFile("fume-result",java.util.Set.of(java.nio.file.StandardOpenOption.READ))){result=new String(java.nio.channels.Channels.newInputStream(input).readAllBytes(),java.nio.charset.StandardCharsets.UTF_8);}if(result.startsWith("FAIL"))throw new IllegalStateException(result);if(!result.equals("PASS"))return;
  if(remoteStage!=5)throw new IllegalStateException("Lua remote lifecycle incomplete");var access=turtle.getAccess();if(!world.getBlockState(AT.west(3)).isAir()||!world.getBlockState(AT.above()).isAir()||!world.getBlockState(AT.east(3)).is(ThaumicTinkererModern.SHADOW_GAS.get())||!world.getBlockState(AT.north()).is(net.minecraft.world.level.block.Blocks.STONE)||access.getFuelLevel()!=37||access.getInventory().getItem(1).getCount()!=3||!access.getInventory().getItem(0).is(ThaumicTinkererModern.GAS_REMOVER.get())||access.getUpgrade(dan200.computercraft.api.turtle.TurtleSide.LEFT)!=null)throw new IllegalStateException("Fume Lua native bounds/conservation/unequip");if(((MagnetEntity)world.getBlockEntity(AT.east())).adult)throw new IllegalStateException("Lua mob age authoritative state");if(world.getBlockState(AT.south()).getValue(MagnetBlock.PULLING)||world.getBlockState(AT.east()).getValue(MagnetBlock.PULLING))throw new IllegalStateException("Lua magnet authoritative block state");var tablet=(AnimationTabletEntity)world.getBlockEntity(AT.below());if(!tablet.redstone||tablet.leftClick||tablet.facing()!=net.minecraft.core.Direction.EAST||!tablet.inventory.getStackInSlot(0).is(net.minecraft.world.item.Items.DIAMOND_PICKAXE)||tablet.inventory.getStackInSlot(0).getDamageValue()!=0)throw new IllegalStateException("Lua tablet native state/tool conservation");System.out.println("TT TABLET LUA AUDIT: ALL PASS (eight original commands,actual turtle discovery,boolean validation,void arity,fractional rotation,rejected invalid rotation,trigger/busy,native mode/tool conservation)");System.out.println("TT TRANSVECTOR LUA AUDIT: ALL PASS (actual turtle remote discovery,forwarded type,annotated queries/setter,void arity,nested main-thread tasks,rebind method refresh,cached Lua wrapper,removed target empty shape,replacement discovery,dynamic multi-return/table,event-yield callback,attach/detach exactly once)");System.out.println("TT MAGNET LUA AUDIT: ALL PASS (both real adjacent peripherals,boolean validation,no-return arity,pull/push,signal15,native state)");complete=true;System.out.println("TT FUME LUA AUDIT: ALL PASS (actual turtle equipLeft,dig/digUp/digDown,attack failure,gas bounds,stone retention,fuel/inventory conservation,unequip item)");
 }
 static void remoteLifecycle()throws java.io.IOException {
  TransvectorDynamicProbe.tick();
  if(!mount.exists("remote-request"))return;String value;try(var input=mount.openFile("remote-request",java.util.Set.of(java.nio.file.StandardOpenOption.READ))){value=new String(java.nio.channels.Channels.newInputStream(input).readAllBytes(),java.nio.charset.StandardCharsets.UTF_8);}
  int next=remoteStage+1;if(!value.equals(Integer.toString(next)))return;
  if(next==1){if(!((TransvectorEntity)world.getBlockEntity(AT.west())).link(AT.east()))throw new IllegalStateException("Lua remote mob rebind");}
  else if(next==2)world.setBlock(AT.east(),net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(),3);
  else if(next==3)world.setBlock(AT.east(),ThaumicTinkererModern.MOB_MAGNET.get().defaultBlockState(),3);
  else if(next==4){world.setBlock(AT.west(2),net.minecraft.world.level.block.Blocks.CHEST.defaultBlockState(),3);if(!((TransvectorEntity)world.getBlockEntity(AT.west())).link(AT.west(2)))throw new IllegalStateException("Lua dynamic probe rebind");}
  else if(next==5){var link=(TransvectorEntity)world.getBlockEntity(AT.west());if(!link.target().equals(AT.south())&&!link.link(AT.south()))throw new IllegalStateException("Lua probe detach rebind");var probe=TransvectorDynamicProbe.PROBES.get(world.getBlockEntity(AT.west(2)));if(probe==null||probe.attached!=1)throw new IllegalStateException("Lua probe attach count");if(probe.detached==0)return;if(probe.detached!=1)throw new IllegalStateException("Lua probe detach count");}
  else throw new IllegalStateException("Unexpected remote lifecycle stage");
  remoteStage=next;try(var output=mount.openFile("remote-ready-"+next,java.util.Set.of(java.nio.file.StandardOpenOption.CREATE,java.nio.file.StandardOpenOption.WRITE))){output.write(java.nio.ByteBuffer.wrap(new byte[]{1}));}
 }
 static void restore(){
  try{if(turtle!=null){if(turtle.getServerComputer()!=null)turtle.getServerComputer().shutdown();turtle.getAccess().getInventory().clearContent();}if(mount!=null){mount.delete("startup.lua");if(mount.exists("remote-request"))mount.delete("remote-request");for(int i=1;i<=5;i++)if(mount.exists("remote-ready-"+i))mount.delete("remote-ready-"+i);if(mount.exists("fume-result"))mount.delete("fume-result");}}
  catch(java.io.IOException error){throw new IllegalStateException(error);}finally{if(world!=null){if(world.getBlockEntity(AT.below()) instanceof AnimationTabletEntity tablet)tablet.inventory.setStackInSlot(0,net.minecraft.world.item.ItemStack.EMPTY);blocks.forEach((p,state)->world.setBlock(p,state,3));tags.forEach((p,tag)->{var tile=world.getBlockEntity(p);if(tile!=null)tile.loadWithComponents(tag,world.registryAccess());});}blocks.clear();tags.clear();turtle=null;mount=null;}
 }
}
