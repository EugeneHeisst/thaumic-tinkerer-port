package dev.eugheist.ttmodern;
/** Loaded only when CC:T is present. Keep API references out of ordinary startup. */
final class ComputerCraftCompat {
 static void init(net.neoforged.bus.api.IEventBus bus){GolemConnector.init();AspectAnalyzer.register(bus);FumeTool.init(bus);if(net.neoforged.fml.loading.FMLEnvironment.dist==net.neoforged.api.distmarker.Dist.CLIENT)ComputerCraftClientCompat.init(bus);bus.addListener(ComputerCraftCompat::capabilities);TransvectorPeripheral.register();}
 private static void capabilities(net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent event){
  if(Boolean.getBoolean("ttinkerer.ccLuaAudit"))TransvectorDynamicProbe.capabilities(event);
  event.registerBlockEntity(dan200.computercraft.api.peripheral.PeripheralCapability.get(),ThaumicTinkererModern.ANIMATION_TABLET_ENTITY.get(),(tile,side)->new AnimationTabletPeripheral(tile));
  event.registerBlockEntity(dan200.computercraft.api.peripheral.PeripheralCapability.get(),ThaumicTinkererModern.TRANSVECTOR_ENTITY.get(),TransvectorPeripheral::new);
  event.registerBlockEntity(dan200.computercraft.api.peripheral.PeripheralCapability.get(),ThaumicTinkererModern.MAGNET_ENTITY.get(),(tile,side)->tile.mobs()?new MobMagnetPeripheral(tile):new MagnetPeripheral(tile));
  event.registerBlockEntity(dan200.computercraft.api.peripheral.PeripheralCapability.get(),GolemConnector.ENTITY.get(),(tile,side)->new GolemPeripheral(tile));
  event.registerBlockEntity(dan200.computercraft.api.peripheral.PeripheralCapability.get(),AspectAnalyzer.ENTITY.get(),(tile,side)->new AnalyzerPeripheral(tile));
  event.registerBlockEntity(net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK,AspectAnalyzer.ENTITY.get(),(tile,side)->tile.inventory);
  event.registerBlockEntity(dan200.computercraft.api.peripheral.PeripheralCapability.get(),dev.eugheist.tc4modern.EssentiaDevices.ALEMBIC_ENTITY.get(),(tile,side)->new AspectPeripheral(tile,()->tile.state.aspect==null?new thaumcraft.api.aspects.AspectList():new thaumcraft.api.aspects.AspectList().add(tile.state.aspect,tile.state.amount)));
  event.registerBlockEntity(dan200.computercraft.api.peripheral.PeripheralCapability.get(),dev.eugheist.tc4modern.ArcaneBore.BORE_ENTITY.get(),(bore,side)->new BorePeripheral(bore));
  event.registerBlockEntity(dan200.computercraft.api.peripheral.PeripheralCapability.get(),dev.eugheist.tc4modern.EssentiaTubes.TUBE_ENTITY.get(),(tile,side)->new TransportPeripheral(tile,tile));
  event.registerBlockEntity(dan200.computercraft.api.peripheral.PeripheralCapability.get(),dev.eugheist.tc4modern.EssentiaTubes.VARIANT_ENTITY.get(),(tile,side)->new TransportPeripheral(tile,tile));
  event.registerBlockEntity(dan200.computercraft.api.peripheral.PeripheralCapability.get(),dev.eugheist.tc4modern.EssentiaTubes.BUFFER_ENTITY.get(),(tile,side)->new TransportPeripheral(tile,tile));
  event.registerBlockEntity(dan200.computercraft.api.peripheral.PeripheralCapability.get(),dev.eugheist.tc4modern.ArcaneEar.ENTITY.get(),(ear,side)->new EarPeripheral(ear));
  event.registerBlockEntity(dan200.computercraft.api.peripheral.PeripheralCapability.get(),dev.eugheist.tc4modern.BasicsContent.DECONSTRUCTOR_ENTITY.get(),(table,side)->new DeconstructorPeripheral(table));
  event.registerBlockEntity(dan200.computercraft.api.peripheral.PeripheralCapability.get(),dev.eugheist.tc4modern.BrainJarContent.ENTITY.get(),(jar,side)->new BrainPeripheral(jar));
  event.registerBlockEntity(dan200.computercraft.api.peripheral.PeripheralCapability.get(),dev.eugheist.tc4modern.EssentiaDevices.JAR_ENTITY.get(),(jar,side)->new JarPeripheral(jar));
  var explicit=java.util.Set.of(dev.eugheist.tc4modern.EssentiaDevices.ALEMBIC_ENTITY.get(),dev.eugheist.tc4modern.EssentiaDevices.JAR_ENTITY.get(),dev.eugheist.tc4modern.ArcaneBore.BORE_ENTITY.get(),dev.eugheist.tc4modern.EssentiaTubes.TUBE_ENTITY.get(),dev.eugheist.tc4modern.EssentiaTubes.VARIANT_ENTITY.get(),dev.eugheist.tc4modern.EssentiaTubes.BUFFER_ENTITY.get(),dev.eugheist.tc4modern.ArcaneEar.ENTITY.get(),dev.eugheist.tc4modern.BasicsContent.DECONSTRUCTOR_ENTITY.get(),dev.eugheist.tc4modern.BrainJarContent.ENTITY.get());
  for(var type:net.minecraft.core.registries.BuiltInRegistries.BLOCK_ENTITY_TYPE){var id=net.minecraft.core.registries.BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(type);if(id.getNamespace().equals("tc4modern")&&!explicit.contains(type))event.registerBlockEntity(dan200.computercraft.api.peripheral.PeripheralCapability.get(),type,(tile,side)->fallback(tile));}

 }
 static dan200.computercraft.api.peripheral.IPeripheral fallback(net.minecraft.world.level.block.entity.BlockEntity tile){
  if(tile instanceof dev.eugheist.tc4modern.EssentiaReservoir.ReservoirEntity reservoir)return new AspectPeripheral(tile,()->reservoir.essentia);
  if(tile instanceof dev.eugheist.tc4modern.ThaumatoriumEntity machine)return new AspectPeripheral(tile,()->machine.state.essentia);
  if(tile instanceof dev.eugheist.tc4modern.AlchemyMachineEntity machine)return new AspectPeripheral(tile,()->{var aspect=machine.state.crystalizer?machine.state.input:machine.state.output;return aspect==null?new thaumcraft.api.aspects.AspectList():new thaumcraft.api.aspects.AspectList().add(aspect,1);});
  return tile instanceof thaumcraft.api.aspects.IEssentiaTransport pipe?new TransportPeripheral(tile,pipe):null;
 }
 static void research(){
  AspectAnalyzerResearch.register();GolemConnectorResearch.register();
  new thaumcraft.api.research.ResearchItem("PERIPHERALS","THAUMICTINKERER",new thaumcraft.api.aspects.AspectList(),-1,0,0,new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.REDSTONE)){
   @Override public String getName(){return net.minecraft.network.chat.Component.translatable("ttresearch.name.PERIPHERALS").getString();}
   @Override public String getText(){return net.minecraft.network.chat.Component.translatable("ttresearch.lore.PERIPHERALS").getString();}
  }.setAutoUnlock().setRound().setPages(new thaumcraft.api.research.ResearchPage("ttresearch.page.PERIPHERALS.0")).registerResearchItem();
 }
 static void audit(net.minecraft.server.MinecraftServer server){
  try{GolemMarkerCodec.audit();}catch(dan200.computercraft.api.lua.LuaException error){throw new IllegalStateException(error);}
  var research=thaumcraft.api.research.ResearchCategories.getResearch("PERIPHERALS");if(research==null||!research.isAutoUnlock()||!research.isRound()||research.displayColumn!=-1||research.displayRow!=0)throw new IllegalStateException("CC original peripherals root research");
  var world=server.overworld();var at=new net.minecraft.core.BlockPos(12,310,12);var support=at.below();var oldSupport=world.getBlockState(support);if(world.getBlockEntity(support)!=null)throw new IllegalStateException("CC bore fixture requires empty support tile");var old=world.getBlockState(at);var oldTile=world.getBlockEntity(at);var saved=oldTile==null?null:oldTile.saveWithFullMetadata(world.registryAccess());
  try{
   TransvectorPeripheralAudit.run(world,at);
   MagnetPeripheralAudit.run(world,at);
   AnimationTabletPeripheralAudit.run(world,at);
   FumeToolAudit.run(world,at);
   GolemConnectorAudit.run(world,at);
   AspectAnalyzerAudit.run(world,at);
   for(var block:new net.minecraft.world.level.block.Block[]{dev.eugheist.tc4modern.EssentiaDevices.JAR.get(),dev.eugheist.tc4modern.AlchemyMachines.VOID_JAR.get()}){
    world.setBlock(at,block.defaultBlockState(),3);var jar=(dev.eugheist.tc4modern.WardedJarEntity)world.getBlockEntity(at);
    var peripheral=world.getCapability(dan200.computercraft.api.peripheral.PeripheralCapability.get(),at,net.minecraft.core.Direction.UP);
    if(!(peripheral instanceof JarPeripheral p)||!p.getType().equals("tt_aspectContainer")||p.getTarget()!=jar)throw new IllegalStateException("CC native jar peripheral capability");
    if(!p.getAspects().isEmpty()||p.getAspectCount("aer")!=0||p.getAspectCount("unknown")!=0)throw new IllegalStateException("CC empty/unknown jar aspect");
    jar.state.aspect=thaumcraft.api.aspects.Aspect.AIR;jar.state.amount=17;
    if(!p.getAspects().equals(java.util.Map.of(1.0,"aer"))||p.getAspectCount("aer")!=17)throw new IllegalStateException("CC actual jar amount");
    jar.state.aspectFilter=thaumcraft.api.aspects.Aspect.FIRE;
    if(!p.getAspects().equals(java.util.Map.of(1.0,"ignis"))||p.getAspectCount("aer")!=17||p.getAspectCount("ignis")!=0)throw new IllegalStateException("CC original filter precedence");
    jar.state.amount=0;jar.state.aspect=null;
    if(!p.getAspects().equals(java.util.Map.of(1.0,"ignis")))throw new IllegalStateException("CC empty filtered jar");
    if(!p.equals(new JarPeripheral(jar)))throw new IllegalStateException("CC peripheral stable identity");
   }
   world.setBlock(at,dev.eugheist.tc4modern.BasicsContent.DECONSTRUCTOR.get().defaultBlockState(),3);
   var table=(dev.eugheist.tc4modern.DeconstructorEntity)world.getBlockEntity(at);
   var tablePeripheral=(DeconstructorPeripheral)world.getCapability(dan200.computercraft.api.peripheral.PeripheralCapability.get(),at,net.minecraft.core.Direction.NORTH);
   if(tablePeripheral==null||tablePeripheral.hasItem()||tablePeripheral.hasAspect()||tablePeripheral.getAspect()!=null)throw new IllegalStateException("CC empty deconstructor");
   table.setItem(0,new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.STONE,3));table.state.aspect=thaumcraft.api.aspects.Aspect.EARTH;
   if(!tablePeripheral.hasItem()||!tablePeripheral.hasAspect()||!"terra".equals(tablePeripheral.getAspect())||table.getItem(0).getCount()!=3||table.state.aspect!=thaumcraft.api.aspects.Aspect.EARTH)throw new IllegalStateException("CC deconstructor read-only queries");
   world.setBlock(at,dev.eugheist.tc4modern.BrainJarContent.BLOCK.get().defaultBlockState(),3);
   var brain=(dev.eugheist.tc4modern.BrainJarEntity)world.getBlockEntity(at);
   var brainPeripheral=(BrainPeripheral)world.getCapability(dan200.computercraft.api.peripheral.PeripheralCapability.get(),at,net.minecraft.core.Direction.DOWN);
   if(brainPeripheral==null||brainPeripheral.getXP()!=0)throw new IllegalStateException("CC empty brain jar");
   var xp=new net.minecraft.nbt.CompoundTag();xp.putInt("XP",731);brain.storage.load(xp);
   if(brainPeripheral.getXP()!=731||brainPeripheral.getXP()!=731)throw new IllegalStateException("CC brain jar read-only experience");
   world.setBlock(at,dev.eugheist.tc4modern.ArcaneEar.BLOCK.get().defaultBlockState(),3);
   var ear=(EarPeripheral)world.getCapability(dan200.computercraft.api.peripheral.PeripheralCapability.get(),at,net.minecraft.core.Direction.EAST);
   if(ear==null||!ear.getType().equals("tt_arcaneear")||ear.getNote()!=0)throw new IllegalStateException("CC actual ear capability");
   for(double value:new double[]{0,24,7.9,-2.9,130,256}){ear.setNote(value);if(ear.getNote()!=(byte)value)throw new IllegalStateException("CC original byte note cast");}
   ear.setNote(12);if(world.getBlockEntity(at).saveWithoutMetadata(world.registryAccess()).getByte("note")!=12)throw new IllegalStateException("CC ear saved note");
   for(var block:java.util.List.of(dev.eugheist.tc4modern.EssentiaTubes.TUBE.get(),dev.eugheist.tc4modern.EssentiaTubes.VALVE.get(),dev.eugheist.tc4modern.EssentiaTubes.FILTER.get(),dev.eugheist.tc4modern.EssentiaTubes.BUFFER.get(),dev.eugheist.tc4modern.EssentiaTubes.RESTRICT.get(),dev.eugheist.tc4modern.EssentiaTubes.ONEWAY.get())){
    world.setBlock(at,block.defaultBlockState(),3);var tile=world.getBlockEntity(at);var pipe=(thaumcraft.api.aspects.IEssentiaTransport)tile;
    var peripheral=(TransportPeripheral)world.getCapability(dan200.computercraft.api.peripheral.PeripheralCapability.get(),at,net.minecraft.core.Direction.UP);
    if(peripheral==null||!peripheral.getType().equals("tt_aspectTransport"))throw new IllegalStateException("CC tube capability");
    if(tile instanceof dev.eugheist.tc4modern.EssentiaTubeEntity tube){tube.state.essentiaType=thaumcraft.api.aspects.Aspect.FIRE;tube.state.essentiaAmount=1;tube.state.suctionType=thaumcraft.api.aspects.Aspect.AIR;tube.state.suction=19;}
    var before=tile.saveWithoutMetadata(world.registryAccess());
    try{for(var direction:net.minecraft.core.Direction.values()){double side=direction.get3DDataValue();if(peripheral.isConnectable(side)!=pipe.isConnectable(direction)||peripheral.canInputFrom(side)!=pipe.canInputFrom(direction)||peripheral.canOutputTo(side)!=pipe.canOutputTo(direction)||!java.util.Objects.equals(peripheral.getSuctionType(side),TransportPeripheral.tag(pipe.getSuctionType(direction)))||peripheral.getSuctionAmount(side)!=pipe.getSuctionAmount(direction)||!java.util.Objects.equals(peripheral.getEssentiaType(side),TransportPeripheral.tag(pipe.getEssentiaType(direction)))||peripheral.getEssentiaAmount(side)!=pipe.getEssentiaAmount(direction)||peripheral.getMinimumSuction()!=pipe.getMinimumSuction())throw new IllegalStateException("CC tube directional query");}
    if(TransportPeripheral.direction(2.9)!=net.minecraft.core.Direction.NORTH)throw new IllegalStateException("CC fractional direction");
    try{TransportPeripheral.direction(6);throw new IllegalStateException("CC invalid direction wrapped");}catch(dan200.computercraft.api.lua.LuaException expected){}
    }catch(dan200.computercraft.api.lua.LuaException failure){throw new IllegalStateException(failure);}
    if(!before.equals(tile.saveWithoutMetadata(world.registryAccess())))throw new IllegalStateException("CC query mutated tube");
   }
   world.setBlock(support,dev.eugheist.tc4modern.AlchemyContent.GREATWOOD_PLANKS.get().defaultBlockState(),3);
   world.setBlock(at,dev.eugheist.tc4modern.ArcaneBore.BORE.get().defaultBlockState(),3);
   var bore=(dev.eugheist.tc4modern.ArcaneBore.BoreEntity)world.getBlockEntity(at);
   var bp=(BorePeripheral)world.getCapability(dan200.computercraft.api.peripheral.PeripheralCapability.get(),at,net.minecraft.core.Direction.UP);
   if(bp==null||bp.hasPickaxe()||bp.hasFocus()||bp.isWorking()||bp.getRadius()!=5||bp.getSpeed()!=0||bp.getFortune()!=0||bp.hasSilkTouch())throw new IllegalStateException("CC empty bore queries");
   var pick=new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.DIAMOND_PICKAXE);pick.enchant(world.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).getOrThrow(net.minecraft.world.item.enchantment.Enchantments.FORTUNE),3);bore.setItem(1,pick);
   if(!bp.hasPickaxe()||bp.hasFocus()||bp.getFortune()!=3||bp.hasNativeClusters()||bp.isPickaxeBroken())throw new IllegalStateException("CC equipped bore queries");
   pick.setDamageValue(pick.getMaxDamage()-1);if(!bp.isPickaxeBroken()||bp.isWorking())throw new IllegalStateException("CC near-broken bore");
   pick.setDamageValue(0);pick.enchant(world.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).getOrThrow(net.minecraft.world.item.enchantment.Enchantments.EFFICIENCY),2);
   var focus=new net.minecraft.world.item.ItemStack(dev.eugheist.tc4modern.FociWorld.EXCAVATION.get());var focusItem=(dev.eugheist.tc4modern.FociContent.Focus)focus.getItem();
   focusItem.applyUpgrade(focus,3,1);focusItem.applyUpgrade(focus,0,2);focusItem.applyUpgrade(focus,2,3);focusItem.applyUpgrade(focus,8,4);focusItem.applyUpgrade(focus,20,5);bore.setItem(0,focus);bore.setChanged();
   if(!bp.hasFocus()||bp.getRadius()!=7||bp.getSpeed()!=3||bp.getFortune()!=3||bp.hasSilkTouch()||bp.hasNativeClusters())throw new IllegalStateException("CC focus/pick property distinction");
   var powerPos=at.east();var oldPower=world.getBlockState(powerPos);if(world.getBlockEntity(powerPos)!=null)throw new IllegalStateException("CC powered fixture needs no existing tile");
   try{world.setBlock(powerPos,net.minecraft.world.level.block.Blocks.REDSTONE_BLOCK.defaultBlockState(),3);if(!bp.isWorking())throw new IllegalStateException("CC powered working bore");pick.setDamageValue(pick.getMaxDamage()-1);if(bp.isWorking())throw new IllegalStateException("CC powered near-broken stop");
   var silk=new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.DIAMOND_PICKAXE);silk.enchant(world.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).getOrThrow(net.minecraft.world.item.enchantment.Enchantments.SILK_TOUCH),1);bore.setItem(1,silk);if(!bp.hasSilkTouch()||bp.getFortune()!=0||!bp.isWorking())throw new IllegalStateException("CC silk pick vs focus treasure");
   bore.setItem(1,new net.minecraft.world.item.ItemStack(dev.eugheist.tc4modern.ElementalTools.item("pickaxe")));if(!bp.hasNativeClusters()||!bp.isWorking())throw new IllegalStateException("CC elemental pick");
   var before=bore.saveWithoutMetadata(world.registryAccess());bp.getRadius();bp.getSpeed();bp.getFortune();bp.hasSilkTouch();bp.isWorking();if(!before.equals(bore.saveWithoutMetadata(world.registryAccess())))throw new IllegalStateException("CC bore query mutates state");
   }finally{bore.clearContent();world.setBlock(powerPos,oldPower,3);}
   world.setBlock(at,dev.eugheist.tc4modern.EssentiaDevices.ALEMBIC.get().defaultBlockState(),3);
   var alembic=(dev.eugheist.tc4modern.AlembicEntity)world.getBlockEntity(at);
   var ap=(AspectPeripheral)world.getCapability(dan200.computercraft.api.peripheral.PeripheralCapability.get(),at,net.minecraft.core.Direction.SOUTH);
   if(ap==null||!ap.getType().equals("tt_aspectContainer")||!ap.getAspects().isEmpty()||ap.getAspectCount("unknown")!=0)throw new IllegalStateException("CC alembic container precedence");
   alembic.state.aspect=thaumcraft.api.aspects.Aspect.AIR;alembic.state.amount=23;alembic.state.aspectFilter=thaumcraft.api.aspects.Aspect.FIRE;
   var beforeAlembic=alembic.saveWithoutMetadata(world.registryAccess());if(!ap.getAspects().equals(java.util.Map.of(1.0,"aer"))||ap.getAspectCount("aer")!=23||ap.getAspectCount("ignis")!=0||!beforeAlembic.equals(alembic.saveWithoutMetadata(world.registryAccess())))throw new IllegalStateException("CC alembic contents not jar filter");
   for(var block:java.util.List.of(dev.eugheist.tc4modern.EssentiaReservoir.BLOCK.get(),dev.eugheist.tc4modern.Thaumatorium.BODY.get(),dev.eugheist.tc4modern.AlchemyMachines.CENTRIFUGE.get(),dev.eugheist.tc4modern.AlchemyMachines.CRYSTALIZER.get())){
    world.setBlock(at,block.defaultBlockState(),3);var tile=world.getBlockEntity(at);var peripheral=world.getCapability(dan200.computercraft.api.peripheral.PeripheralCapability.get(),at,net.minecraft.core.Direction.UP);
    if(!(peripheral instanceof AspectPeripheral container))throw new IllegalStateException("CC aspect container fallback precedence "+block);
    if(tile instanceof dev.eugheist.tc4modern.EssentiaReservoir.ReservoirEntity reservoir)reservoir.essentia.add(thaumcraft.api.aspects.Aspect.AIR,17).add(thaumcraft.api.aspects.Aspect.FIRE,9);
    if(tile instanceof dev.eugheist.tc4modern.ThaumatoriumEntity machine)machine.state.essentia.add(thaumcraft.api.aspects.Aspect.AIR,17).add(thaumcraft.api.aspects.Aspect.FIRE,9);
    if(tile instanceof dev.eugheist.tc4modern.AlchemyMachineEntity machine){machine.state.input=thaumcraft.api.aspects.Aspect.FIRE;machine.state.output=thaumcraft.api.aspects.Aspect.AIR;if(container.getAspectCount(machine.state.crystalizer?"ignis":"aer")!=1||container.getAspectCount(machine.state.crystalizer?"aer":"ignis")!=0)throw new IllegalStateException("CC centrifuge output/crystalizer input");}
    else if(container.getAspectCount("aer")!=17||container.getAspectCount("ignis")!=9||container.getAspects().size()!=2)throw new IllegalStateException("CC multi-aspect contents");
   }
   world.setBlock(at,dev.eugheist.tc4modern.ArcaneBore.BASE.get().defaultBlockState(),3);
   var base=world.getCapability(dan200.computercraft.api.peripheral.PeripheralCapability.get(),at,net.minecraft.core.Direction.UP);
   if(!(base instanceof TransportPeripheral transport))throw new IllegalStateException("CC generic transport fallback");
   try{if(transport.getSuctionAmount(2)!=128||!"perditio".equals(transport.getSuctionType(2))||transport.getEssentiaAmount(2)!=0)throw new IllegalStateException("CC bore base transport state");}catch(dan200.computercraft.api.lua.LuaException e){throw new IllegalStateException(e);}
   System.out.println("TT CC FALLBACK PERIPHERAL AUDIT: ALL PASS (reservoir/thaumatorium/centrifuge/crystalizer container precedence,multi-aspect counts,bore base transport)");
   System.out.println("TT CC ASPECT CONTAINER AUDIT: ALL PASS (native alembic capability,container before transport,contents not jar filter,read-only)");
   System.out.println("TT CC BORE PERIPHERAL AUDIT: ALL PASS (native capability,empty/powered/upgraded,original radius/speed,pick vs focus enchants,elemental,silk,near-broken,read-only)");
   System.out.println("TT CC TRANSPORT PERIPHERAL AUDIT: ALL PASS (six native tube kinds,six faces,eight read-only methods,fractional/invalid direction)");
   System.out.println("TT CC EAR PERIPHERAL AUDIT: ALL PASS (actual capability,original fractional/signed/wrapping byte conversion,saved note)");
   System.out.println("TT CC DEVICE PERIPHERAL AUDIT: ALL PASS (native deconstructor/brain capabilities,empty/filled,read-only item/aspect/XP queries)");
   System.out.println("TT CC JAR PERIPHERAL AUDIT: ALL PASS (actual CC capability,normal/void jars,empty/filter precedence,amounts,unknown tags,stable identity)");
  }finally{world.setBlock(at,old,3);world.setBlock(support,oldSupport,3);if(saved!=null&&world.getBlockEntity(at)!=null)world.getBlockEntity(at).loadWithComponents(saved,world.registryAccess());}
 }
 public static final class JarPeripheral implements dan200.computercraft.api.peripheral.IPeripheral {
  private final dev.eugheist.tc4modern.WardedJarEntity jar;
  public JarPeripheral(dev.eugheist.tc4modern.WardedJarEntity jar){this.jar=jar;}
  @Override public String getType(){return "tt_aspectContainer";}
  @Override public boolean equals(dan200.computercraft.api.peripheral.IPeripheral other){return other instanceof JarPeripheral p&&p.jar==jar;}
  @Override public Object getTarget(){return jar;}
  @dan200.computercraft.api.lua.LuaFunction(mainThread=true)
  public final java.util.Map<Double,String> getAspects(){
   var result=new java.util.LinkedHashMap<Double,String>();
   var aspect=jar.state.aspectFilter!=null?jar.state.aspectFilter:jar.state.amount>0?jar.state.aspect:null;
   if(aspect!=null)result.put(1.0,aspect.getTag());return result;
  }
  @dan200.computercraft.api.lua.LuaFunction(mainThread=true)
  public final int getAspectCount(String name){var aspect=thaumcraft.api.aspects.Aspect.getAspect(name);return aspect!=null&&aspect==jar.state.aspect?jar.state.amount:0;}
 }
 public static final class DeconstructorPeripheral implements dan200.computercraft.api.peripheral.IPeripheral {
  private final dev.eugheist.tc4modern.DeconstructorEntity table;
  DeconstructorPeripheral(dev.eugheist.tc4modern.DeconstructorEntity table){this.table=table;}
  public String getType(){return "tt_deconstructor";}
  public Object getTarget(){return table;}
  public boolean equals(dan200.computercraft.api.peripheral.IPeripheral other){return other instanceof DeconstructorPeripheral p&&p.table==table;}
  @dan200.computercraft.api.lua.LuaFunction(mainThread=true) public final boolean hasAspect(){return table.state.aspect!=null;}
  @dan200.computercraft.api.lua.LuaFunction(mainThread=true) public final boolean hasItem(){return !table.getItem(0).isEmpty();}
  @dan200.computercraft.api.lua.LuaFunction(mainThread=true) public final String getAspect(){return table.state.aspect==null?null:table.state.aspect.getTag();}
 }
 public static final class BrainPeripheral implements dan200.computercraft.api.peripheral.IPeripheral {
  private final dev.eugheist.tc4modern.BrainJarEntity jar;
  BrainPeripheral(dev.eugheist.tc4modern.BrainJarEntity jar){this.jar=jar;}
  public String getType(){return "tt_braininajar";}
  public Object getTarget(){return jar;}
  public boolean equals(dan200.computercraft.api.peripheral.IPeripheral other){return other instanceof BrainPeripheral p&&p.jar==jar;}
  @dan200.computercraft.api.lua.LuaFunction(mainThread=true) public final int getXP(){return jar.storage.experience();}
 }
 public static final class EarPeripheral implements dan200.computercraft.api.peripheral.IPeripheral {
  private final dev.eugheist.tc4modern.ArcaneEar.EarEntity ear;
  EarPeripheral(dev.eugheist.tc4modern.ArcaneEar.EarEntity ear){this.ear=ear;}
  public String getType(){return "tt_arcaneear";}
  public Object getTarget(){return ear;}
  public boolean equals(dan200.computercraft.api.peripheral.IPeripheral other){return other instanceof EarPeripheral p&&p.ear==ear;}
  @dan200.computercraft.api.lua.LuaFunction(mainThread=true) public final int getNote(){return ((dev.eugheist.ttmodern.mixin.ArcaneEarAccessor)(Object)ear).ttinkerer$getNote();}
  @dan200.computercraft.api.lua.LuaFunction(mainThread=true) public final void setNote(double note){((dev.eugheist.ttmodern.mixin.ArcaneEarAccessor)(Object)ear).ttinkerer$setNote((byte)note);ear.setChanged();}
 }
 public static final class TransportPeripheral implements dan200.computercraft.api.peripheral.IPeripheral {
  private final net.minecraft.world.level.block.entity.BlockEntity tile;
  private final thaumcraft.api.aspects.IEssentiaTransport pipe;
  TransportPeripheral(net.minecraft.world.level.block.entity.BlockEntity tile,thaumcraft.api.aspects.IEssentiaTransport pipe){this.tile=tile;this.pipe=pipe;}
  public String getType(){return "tt_aspectTransport";}
  public Object getTarget(){return tile;}
  public boolean equals(dan200.computercraft.api.peripheral.IPeripheral other){return other instanceof TransportPeripheral p&&p.tile==tile;}
  static net.minecraft.core.Direction direction(double value)throws dan200.computercraft.api.lua.LuaException{int side=(int)value;if(side<0||side>5)throw new dan200.computercraft.api.lua.LuaException("Direction must be 0..5");return net.minecraft.core.Direction.from3DDataValue(side);}
  static String tag(thaumcraft.api.aspects.Aspect aspect){return aspect==null?null:aspect.getTag();}
  @dan200.computercraft.api.lua.LuaFunction(mainThread=true) public final boolean isConnectable(double side)throws dan200.computercraft.api.lua.LuaException{return pipe.isConnectable(direction(side));}
  @dan200.computercraft.api.lua.LuaFunction(mainThread=true) public final boolean canInputFrom(double side)throws dan200.computercraft.api.lua.LuaException{return pipe.canInputFrom(direction(side));}
  @dan200.computercraft.api.lua.LuaFunction(mainThread=true) public final boolean canOutputTo(double side)throws dan200.computercraft.api.lua.LuaException{return pipe.canOutputTo(direction(side));}
  @dan200.computercraft.api.lua.LuaFunction(mainThread=true) public final String getSuctionType(double side)throws dan200.computercraft.api.lua.LuaException{return tag(pipe.getSuctionType(direction(side)));}
  @dan200.computercraft.api.lua.LuaFunction(mainThread=true) public final int getSuctionAmount(double side)throws dan200.computercraft.api.lua.LuaException{return pipe.getSuctionAmount(direction(side));}
  @dan200.computercraft.api.lua.LuaFunction(mainThread=true) public final String getEssentiaType(double side)throws dan200.computercraft.api.lua.LuaException{return tag(pipe.getEssentiaType(direction(side)));}
  @dan200.computercraft.api.lua.LuaFunction(mainThread=true) public final int getEssentiaAmount(double side)throws dan200.computercraft.api.lua.LuaException{return pipe.getEssentiaAmount(direction(side));}
  @dan200.computercraft.api.lua.LuaFunction(mainThread=true) public final int getMinimumSuction(){return pipe.getMinimumSuction();}
 }
 public static final class BorePeripheral implements dan200.computercraft.api.peripheral.IPeripheral {
  private final dev.eugheist.tc4modern.ArcaneBore.BoreEntity bore;
  BorePeripheral(dev.eugheist.tc4modern.ArcaneBore.BoreEntity bore){this.bore=bore;}
  private dev.eugheist.ttmodern.mixin.ArcaneBoreAccessor access(){return (dev.eugheist.ttmodern.mixin.ArcaneBoreAccessor)(Object)bore;}
  public String getType(){return "tt_arcanebore";}
  public Object getTarget(){return bore;}
  public boolean equals(dan200.computercraft.api.peripheral.IPeripheral other){return other instanceof BorePeripheral p&&p.bore==bore;}
  @dan200.computercraft.api.lua.LuaFunction(mainThread=true) public final boolean hasPickaxe(){return bore.getItem(1).getItem() instanceof net.minecraft.world.item.PickaxeItem;}
  @dan200.computercraft.api.lua.LuaFunction(mainThread=true) public final boolean hasFocus(){return bore.getItem(0).getItem() instanceof dev.eugheist.tc4modern.FociWorld.ExcavationFocus;}
  @dan200.computercraft.api.lua.LuaFunction(mainThread=true) public final boolean isPickaxeBroken(){var pick=bore.getItem(1);return !pick.isEmpty()&&pick.getDamageValue()+1==pick.getMaxDamage();}
  @dan200.computercraft.api.lua.LuaFunction(mainThread=true) public final boolean isWorking(){return access().ttinkerer$gettingPower()&&hasFocus()&&hasPickaxe()&&bore.getItem(1).isDamageableItem()&&!isPickaxeBroken();}
  @dan200.computercraft.api.lua.LuaFunction(mainThread=true) public final int getRadius(){return 1+(access().ttinkerer$getArea()+2)*2;}
  @dan200.computercraft.api.lua.LuaFunction(mainThread=true) public final int getSpeed(){return access().ttinkerer$getSpeed();}
  @dan200.computercraft.api.lua.LuaFunction(mainThread=true) public final boolean hasNativeClusters(){return bore.getItem(1).getItem() instanceof dev.eugheist.tc4modern.ElementalTools.Pick;}
  private int enchant(net.minecraft.resources.ResourceKey<net.minecraft.world.item.enchantment.Enchantment> key){return bore.getItem(1).getEnchantmentLevel(bore.getLevel().registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).getOrThrow(key));}
  @dan200.computercraft.api.lua.LuaFunction(mainThread=true) public final int getFortune(){return enchant(net.minecraft.world.item.enchantment.Enchantments.FORTUNE);}
  @dan200.computercraft.api.lua.LuaFunction(mainThread=true) public final boolean hasSilkTouch(){return enchant(net.minecraft.world.item.enchantment.Enchantments.SILK_TOUCH)>0;}
 }
 public static final class AspectPeripheral implements dan200.computercraft.api.peripheral.IPeripheral {
  private final net.minecraft.world.level.block.entity.BlockEntity tile;
  private final java.util.function.Supplier<thaumcraft.api.aspects.AspectList> aspects;
  AspectPeripheral(net.minecraft.world.level.block.entity.BlockEntity tile,java.util.function.Supplier<thaumcraft.api.aspects.AspectList> aspects){this.tile=tile;this.aspects=aspects;}
  public String getType(){return "tt_aspectContainer";}
  public Object getTarget(){return tile;}
  public boolean equals(dan200.computercraft.api.peripheral.IPeripheral other){return other instanceof AspectPeripheral p&&p.tile==tile;}
  @dan200.computercraft.api.lua.LuaFunction(mainThread=true) public final java.util.Map<Double,String> getAspects(){var result=new java.util.LinkedHashMap<Double,String>();var list=aspects.get();double i=1;if(list!=null)for(var aspect:list.getAspectsSorted())result.put(i++,aspect.getTag());return result;}
  @dan200.computercraft.api.lua.LuaFunction(mainThread=true) public final int getAspectCount(String name){var list=aspects.get();var aspect=thaumcraft.api.aspects.Aspect.getAspect(name);return list==null||aspect==null?0:list.getAmount(aspect);}
 }
 public static final class AnalyzerPeripheral implements dan200.computercraft.api.peripheral.IPeripheral {
  private final AspectAnalyzer.Entity tile;
  AnalyzerPeripheral(AspectAnalyzer.Entity tile){this.tile=tile;}
  public String getType(){return "tt_aspectanalyzer";}
  public Object getTarget(){return tile;}
  public boolean equals(dan200.computercraft.api.peripheral.IPeripheral other){return other instanceof AnalyzerPeripheral p&&p.tile==tile;}
  @dan200.computercraft.api.lua.LuaFunction(mainThread=true) public final boolean hasItem(){return !tile.inventory.getStackInSlot(0).isEmpty();}
  @dan200.computercraft.api.lua.LuaFunction(mainThread=true) public final boolean itemHasAspects(){return !tile.aspects().isEmpty();}
  @dan200.computercraft.api.lua.LuaFunction(mainThread=true) public final java.util.Map<Double,String> getAspects(){var list=new thaumcraft.api.aspects.AspectList();tile.aspects().forEach((tag,n)->{var a=thaumcraft.api.aspects.Aspect.getAspect(tag);if(a!=null)list.add(a,n);});var result=new java.util.LinkedHashMap<Double,String>();double i=1;for(var a:list.getAspectsSorted())result.put(i++,a.getTag());return result;}
  @dan200.computercraft.api.lua.LuaFunction(mainThread=true) public final java.util.Map<String,Double> getAspectCount(){var result=new java.util.LinkedHashMap<String,Double>();tile.aspects().forEach((tag,n)->result.put(tag,n.doubleValue()));return result;}
 }
}
