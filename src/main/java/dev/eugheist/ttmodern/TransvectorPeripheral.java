package dev.eugheist.ttmodern;
import java.util.*;
import dan200.computercraft.api.lua.*;
import dan200.computercraft.api.peripheral.*;
/** Original peripheral forwarding, with CC:T's own annotation/dynamic dispatch adapter. */
public final class TransvectorPeripheral implements IDynamicPeripheral {
 static final String UNCONNECTED="Transvector Interface Unconnected Peripherad";
 private static final dan200.computercraft.core.methods.MethodSupplier<dan200.computercraft.core.methods.PeripheralMethod> METHODS=dan200.computercraft.core.asm.PeripheralMethodSupplier.create(List.of());
 private static final List<java.lang.ref.WeakReference<TransvectorPeripheral>> WATCHED=new ArrayList<>();
 static void register(){net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(TransvectorPeripheral::tick);}
 private static void tick(net.neoforged.neoforge.event.tick.ServerTickEvent.Post event){var changed=new HashSet<TransvectorEntity>();WATCHED.removeIf(reference->{var p=reference.get();if(p==null||p.tile.isRemoved())return true;var level=p.tile.getLevel();if(level==null||level.isClientSide)return false;if(!p.current()){changed.add(p.tile);return true;}return false;});for(var tile:changed)tile.getLevel().invalidateCapabilities(tile.getBlockPos());}
 private final TransvectorEntity tile;
 private final net.minecraft.core.Direction side;
 private final IPeripheral initial;
 private final String type;
 private final Set<String> additional;
 private final String[] names;
 private final Map<IComputerAccess,IPeripheral> attached=new IdentityHashMap<>();
 TransvectorPeripheral(TransvectorEntity tile,net.minecraft.core.Direction side){this.tile=tile;this.side=side;initial=target();type=initial==null?UNCONNECTED:initial.getType();additional=initial==null?Set.of():Set.copyOf(initial.getAdditionalTypes());names=initial==null?new String[0]:METHODS.getSelfMethods(initial).keySet().stream().sorted().toArray(String[]::new);WATCHED.add(new java.lang.ref.WeakReference<>(this));}
 private IPeripheral target(){var level=tile.getLevel();return level==null||tile.isRemoved()||level.getBlockEntity(tile.getBlockPos())!=tile?null:tile.forward(PeripheralCapability.get(),side);}
 public String getType(){return type;}
 public Set<String> getAdditionalTypes(){return additional;}
 public String[] getMethodNames(){return names.clone();}
 public Object getTarget(){return tile;}
 public boolean equals(IPeripheral other){return other instanceof TransvectorPeripheral p&&p.tile==tile&&p.side==side&&type.equals(p.type)&&Arrays.equals(names,p.names)&&same(initial,p.initial);}
 static boolean same(IPeripheral a,IPeripheral b){return a==b||a!=null&&b!=null&&a.equals(b);}
 /** The snapshot is replaced by capability invalidation when the linked peripheral changes. */
 boolean current(){var current=target();return same(initial,current)&&Objects.equals(type,current==null?UNCONNECTED:current.getType())&&Arrays.equals(names,current==null?new String[0]:METHODS.getSelfMethods(current).keySet().stream().sorted().toArray(String[]::new));}
 public void attach(IComputerAccess computer){var current=initial;if(current!=null){attached.put(computer,current);current.attach(computer);}}
 public void detach(IComputerAccess computer){var previous=attached.remove(computer);if(previous!=null)previous.detach(computer);}
 public MethodResult callMethod(IComputerAccess computer,ILuaContext context,int method,IArguments arguments)throws LuaException {
  if(method<0||method>=names.length)throw new LuaException("No such transvector method");arguments.escapes();
  var result=new java.util.concurrent.atomic.AtomicReference<MethodResult>();
  var pending=context.executeMainThreadTask(()->{
   var current=target();if(current==null){result.set(MethodResult.of());return new Object[0];}
   var delegate=METHODS.getSelfMethods(current).get(names[method]);if(delegate==null)throw new LuaException("Linked peripheral no longer exposes "+names[method]);
   result.set(delegate.apply(current,context,computer,arguments));return new Object[0];
  });
  return then(pending,result);
 }
 /** Preserve nested yields and target callbacks; don't flatten MethodResult to its first values. */
 private static MethodResult then(MethodResult pending,java.util.concurrent.atomic.AtomicReference<MethodResult> result){var callback=pending.getCallback();if(callback==null)return Objects.requireNonNullElseGet(result.get(),MethodResult::of);return MethodResult.yield(pending.getResult(),event->then(callback.resume(event),result));}
}
