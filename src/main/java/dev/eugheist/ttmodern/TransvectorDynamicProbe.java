package dev.eugheist.ttmodern;
/** Audit-only third-party-style dynamic peripheral, never registered in ordinary startup. */
final class TransvectorDynamicProbe {
 static final java.util.Map<net.minecraft.world.level.block.entity.BlockEntity,Probe> PROBES=new java.util.WeakHashMap<>();
 static void capabilities(net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent event){event.registerBlockEntity(dan200.computercraft.api.peripheral.PeripheralCapability.get(),net.minecraft.world.level.block.entity.BlockEntityType.CHEST,(tile,side)->PROBES.computeIfAbsent(tile,Probe::new));}
 static void tick(){for(var probe:PROBES.values()){if(probe.pending!=null&&probe.tile.getLevel().getGameTime()>=probe.due){probe.pending.queueEvent("tt_probe_ready",probe.token,"resumed");probe.pending=null;}}}
 static final class Probe implements dan200.computercraft.api.peripheral.IDynamicPeripheral {
  final net.minecraft.world.level.block.entity.BlockEntity tile;int attached,detached;long due;Object token;dan200.computercraft.api.peripheral.IComputerAccess pending;
  Probe(net.minecraft.world.level.block.entity.BlockEntity tile){this.tile=tile;}
  public String getType(){return "tt_dynamic_probe";}
  public String[] getMethodNames(){return new String[]{"echo","wait","getLifecycle"};}
  public Object getTarget(){return tile;}
  public boolean equals(dan200.computercraft.api.peripheral.IPeripheral other){return other instanceof Probe p&&p.tile==tile;}
  public void attach(dan200.computercraft.api.peripheral.IComputerAccess computer){attached++;}
  public void detach(dan200.computercraft.api.peripheral.IComputerAccess computer){detached++;}
  public dan200.computercraft.api.lua.MethodResult callMethod(dan200.computercraft.api.peripheral.IComputerAccess computer,dan200.computercraft.api.lua.ILuaContext context,int method,dan200.computercraft.api.lua.IArguments args)throws dan200.computercraft.api.lua.LuaException {
   return switch(method){case 0->dan200.computercraft.api.lua.MethodResult.of(args.getAll());case 1->{pending=computer;token=args.get(0);due=tile.getLevel().getGameTime()+10;yield dan200.computercraft.api.lua.MethodResult.pullEvent("tt_probe_ready",event->dan200.computercraft.api.lua.MethodResult.of(event[1],event[2]));}case 2->dan200.computercraft.api.lua.MethodResult.of(attached,detached);default->throw new dan200.computercraft.api.lua.LuaException("Unknown probe method");};
  }
 }
}
