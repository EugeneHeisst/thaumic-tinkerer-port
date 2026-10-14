package dev.eugheist.ttmodern;
/** Actual CC computer/Lua runtime. Isolated audit only, never ordinary startup. */
final class ComputerCraftLuaAudit {
 static dan200.computercraft.shared.computer.blocks.AbstractComputerBlockEntity computer;
 static dan200.computercraft.api.filesystem.WritableMount mount;
 static net.minecraft.server.level.ServerLevel world;
 static dev.eugheist.tc4modern.StrawGolem golem;
 static int ticks;static boolean forced,started;
 static final net.minecraft.core.BlockPos AT=new net.minecraft.core.BlockPos(12,310,12);
 static final java.util.Map<net.minecraft.core.BlockPos,net.minecraft.nbt.CompoundTag> tags=new java.util.HashMap<>();
 static final java.util.Map<net.minecraft.core.BlockPos,net.minecraft.world.level.block.state.BlockState> blocks=new java.util.LinkedHashMap<>();
 static void register(){net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(ComputerCraftLuaAudit::start);net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(ComputerCraftLuaAudit::tick);}
 static void start(net.neoforged.neoforge.event.server.ServerStartedEvent event){
  world=event.getServer().overworld();forced=world.getForcedChunks().contains(new net.minecraft.world.level.ChunkPos(AT).toLong());world.setChunkForced(AT.getX()>>4,AT.getZ()>>4,true);started=true;
  try{
   for(var pos:java.util.List.of(AT,AT.north(),AT.east(),AT.west(),AT.south(),AT.above(),AT.below())){blocks.put(pos,world.getBlockState(pos));var tile=world.getBlockEntity(pos);if(tile!=null)tags.put(pos,tile.saveWithFullMetadata(world.registryAccess()));}
   var cc=net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("computercraft","computer_normal"));
   if(cc==net.minecraft.world.level.block.Blocks.AIR)throw new IllegalStateException("CC actual computer missing");
   world.setBlock(AT,cc.defaultBlockState(),3);world.setBlock(AT.north(),dev.eugheist.tc4modern.EssentiaDevices.JAR.get().defaultBlockState(),3);world.setBlock(AT.east(),dev.eugheist.tc4modern.ArcaneEar.BLOCK.get().defaultBlockState(),3);
   var jar=(dev.eugheist.tc4modern.WardedJarEntity)world.getBlockEntity(AT.north());jar.state.aspect=thaumcraft.api.aspects.Aspect.AIR;jar.state.amount=13;
   world.setBlock(AT.west(),dev.eugheist.tc4modern.BasicsContent.DECONSTRUCTOR.get().defaultBlockState(),3);
   var table=(dev.eugheist.tc4modern.DeconstructorEntity)world.getBlockEntity(AT.west());
   table.setItem(0,new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.STONE,3));table.state.aspect=thaumcraft.api.aspects.Aspect.EARTH;
   world.setBlock(AT.south(),dev.eugheist.tc4modern.BrainJarContent.BLOCK.get().defaultBlockState(),3);
   var brain=(dev.eugheist.tc4modern.BrainJarEntity)world.getBlockEntity(AT.south());
   var xp=new net.minecraft.nbt.CompoundTag();xp.putInt("XP",2000);brain.storage.load(xp);
   world.setBlock(AT.above(),AspectAnalyzer.BLOCK.get().defaultBlockState(),3);
   var analyzer=(AspectAnalyzer.Entity)world.getBlockEntity(AT.above());analyzer.inventory.setStackInSlot(0,new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.STONE,64));
   world.setBlock(AT.below(),GolemConnector.BLOCK.get().defaultBlockState(),3);
   golem=dev.eugheist.tc4modern.Golemancy.ENTITY.get().create(world);if(golem==null)throw new IllegalStateException("Lua golem creation");golem.setNoAi(true);golem.setNoGravity(true);golem.home=AT;golem.setPos(AT.getX()+3,AT.getY(),AT.getZ());world.addFreshEntity(golem);((GolemConnector.Entity)world.getBlockEntity(AT.below())).connect(golem.getUUID());
   computer=(dan200.computercraft.shared.computer.blocks.AbstractComputerBlockEntity)world.getBlockEntity(AT);var serverComputer=computer.createServerComputer();mount=serverComputer.createRootMount();
   String program="""
    local ok, err = pcall(function()
      local jar = assert(peripheral.find('tt_aspectContainer'), 'missing jar peripheral')
      local ear = assert(peripheral.find('tt_arcaneear'), 'missing ear peripheral')
      local table = assert(peripheral.find('tt_deconstructor'), 'missing deconstructor peripheral')
      local brain = assert(peripheral.find('tt_braininajar'), 'missing brain peripheral')
      local analyzer = assert(peripheral.find('tt_aspectanalyzer'), 'missing analyzer peripheral')
      assert(analyzer.hasItem() and analyzer.itemHasAspects(), 'analyzer flags')
      local tags, counts = analyzer.getAspects(), analyzer.getAspectCount()
      assert(type(tags[1]) == 'string' and type(counts) == 'table', 'analyzer original tables')
      local input = assert(fs.open('analyzer-expected', 'r'))
      local expected = textutils.unserializeJSON(input.readAll())
      input.close()
      local size = 0
      for tag, amount in pairs(expected) do assert(counts[tag] == amount, 'analyzer count ' .. tag); size = size + 1 end
      assert(#tags == size, 'analyzer tag count')
      for _, tag in ipairs(tags) do assert(expected[tag] ~= nil, 'analyzer unexpected tag') end
      local golem = assert(peripheral.find('tt_golemconnector'), 'missing golem connector')
      assert(golem.getType() == 'Straw', 'golem getType alias')
      assert(type(golem.getHealth()) == 'number' and type(golem.getCore()) == 'string', 'golem health/core')
      local x,y,z = golem.getPosition()
      assert(x == 15 and y == 310 and z == 12, 'golem multiple return position')
      x,y,z,facing = golem.setHome(-4.9, 310.8, 12.1, 2.9)
      assert(x == -4 and y == 310 and z == 12 and facing == 2, 'golem multiple return home')
      local marker = golem.newMarker()
      assert(marker.side == 1 and marker.color == -1, 'golem marker defaults')
      golem.addMarker(marker)
      assert(golem.getMarkerCount() == 1 and golem.getMarkers()[1].posX == 12, 'golem marker append')
      marker.posX = -22
      golem.saveMarker(0, marker)
      assert(golem.getMarker(0).posX == -22, 'golem zero based marker save')
      assert(not pcall(golem.deleteMarker, -1), 'golem invalid index error')
      golem.deleteMarker(0)
      assert(golem.getMarkerCount() == 0, 'golem marker delete')
      golem.setMarkers({marker})
      assert(golem.getMarkerCount() == 1, 'golem replace markers')
      assert(table.hasItem() and table.hasAspect(), 'ready deconstructor flags')
      assert(table.getAspect() == 'terra', 'deconstructor aspect')
      assert(brain.getXP() == 2000 and brain.getXP() == 2000, 'brain XP read conservation: ' .. tostring(brain.getXP()))
      assert(jar.getAspects()[1] == 'aer', 'numeric aspect table')
      assert(jar.getAspectCount('aer') == 13, 'actual amount')
      assert(jar.getAspectCount('unknown') == 0, 'unknown aspect')
      ear.setNote(7.9)
      assert(ear.getNote() == 7, 'main thread note cast')
      ear.setNote(130)
      assert(ear.getNote() == -126, 'signed byte cast')
      ear.setNote(12)
      assert(jar.getAspectCount('aer') == 13, 'read conservation')
    end)
    local f = assert(fs.open('audit-result', 'w'))
    f.write(ok and 'PASS' or ('FAIL: ' .. tostring(err)))
    f.close()
    """;
   try(var output=mount.openFile("startup.lua",java.util.Set.of(java.nio.file.StandardOpenOption.CREATE,java.nio.file.StandardOpenOption.WRITE,java.nio.file.StandardOpenOption.TRUNCATE_EXISTING))){output.write(java.nio.ByteBuffer.wrap(program.getBytes(java.nio.charset.StandardCharsets.UTF_8)));}
   var expectedJson=new com.google.gson.Gson().toJson(analyzer.aspects());
   try(var output=mount.openFile("analyzer-expected",java.util.Set.of(java.nio.file.StandardOpenOption.CREATE,java.nio.file.StandardOpenOption.WRITE,java.nio.file.StandardOpenOption.TRUNCATE_EXISTING))){output.write(java.nio.ByteBuffer.wrap(expectedJson.getBytes(java.nio.charset.StandardCharsets.UTF_8)));}
   FumeLuaAudit.start(world);computer.updateInputsImmediately();serverComputer.turnOn();
  }catch(Exception error){restore();throw new IllegalStateException("CC Lua setup",error);}
 }
 static void tick(net.neoforged.neoforge.event.tick.ServerTickEvent.Post event){if(!started)return;
  try{
   if(++ticks>1000)throw new IllegalStateException("CC Lua timeout: actual computer never completed startup program");
   FumeLuaAudit.tick();computer.createServerComputer().keepAlive();
   if(mount.exists("audit-result")){
    String result;try(var input=mount.openFile("audit-result",java.util.Set.of(java.nio.file.StandardOpenOption.READ))){result=new String(java.nio.channels.Channels.newInputStream(input).readAllBytes(),java.nio.charset.StandardCharsets.UTF_8);}
    if(result.startsWith("FAIL"))throw new IllegalStateException(result);
    if(result.equals("PASS")&&FumeLuaAudit.complete){var jar=(dev.eugheist.tc4modern.WardedJarEntity)world.getBlockEntity(AT.north());var ear=(dev.eugheist.tc4modern.ArcaneEar.EarEntity)world.getBlockEntity(AT.east());if(jar.state.amount!=13||((dev.eugheist.ttmodern.mixin.ArcaneEarAccessor)(Object)ear).ttinkerer$getNote()!=12)throw new IllegalStateException("Lua mutation did not reach native world");var table=(dev.eugheist.tc4modern.DeconstructorEntity)world.getBlockEntity(AT.west());var brain=(dev.eugheist.tc4modern.BrainJarEntity)world.getBlockEntity(AT.south());if(table.getItem(0).getCount()!=3||table.state.aspect!=thaumcraft.api.aspects.Aspect.EARTH||brain.storage.experience()!=2000)throw new IllegalStateException("Lua device reads consumed native contents");if(((AspectAnalyzer.Entity)world.getBlockEntity(AT.above())).inventory.getStackInSlot(0).getCount()!=64)throw new IllegalStateException("Lua analyzer consumed items");restore();System.out.println("TT CC LUA AUDIT: ALL PASS (actual computer boot,peripheral discovery,Lua method dispatch,numeric tables,main-thread note changes,native world conservation,deconstructor flags/aspect,brain XP,analyzer original tables/conservation,golem Lua alias/multiple returns/marker CRUD)");}
   }
  }catch(Exception error){restore();System.out.println("TT PORT AUDIT FAILED: CC LUA "+error);throw new IllegalStateException(error);}
 }
 static void restore(){if(!started)return;started=false;FumeLuaAudit.restore();if(golem!=null){golem.discard();golem=null;}if(computer!=null&&computer.getServerComputer()!=null)computer.getServerComputer().shutdown();
  try{if(mount!=null){mount.delete("startup.lua");if(mount.exists("analyzer-expected"))mount.delete("analyzer-expected");if(mount.exists("audit-result"))mount.delete("audit-result");}}catch(java.io.IOException e){throw new IllegalStateException(e);}finally{if(world.getBlockEntity(AT.west()) instanceof dev.eugheist.tc4modern.DeconstructorEntity table)table.clearContent();if(world.getBlockEntity(AT.south()) instanceof dev.eugheist.tc4modern.BrainJarEntity brain)brain.storage.load(new net.minecraft.nbt.CompoundTag());if(world.getBlockEntity(AT.above()) instanceof AspectAnalyzer.Entity analyzer)analyzer.inventory.setStackInSlot(0,net.minecraft.world.item.ItemStack.EMPTY);blocks.forEach((pos,state)->world.setBlock(pos,state,3));tags.forEach((pos,tag)->{var tile=world.getBlockEntity(pos);if(tile!=null)tile.loadWithComponents(tag,world.registryAccess());});world.setChunkForced(AT.getX()>>4,AT.getZ()>>4,forced);blocks.clear();tags.clear();}
 }
}
