package dev.eugheist.ttmodern;

@snownee.jade.api.WailaPlugin("ttinkerer")
public final class JadePlugin implements snownee.jade.api.IWailaPlugin {
 static boolean registered,clientRegistered;
 static final String DATA="ttinkerer:device_tooltip";
 public void register(snownee.jade.api.IWailaCommonRegistration registration){
  for(var type:java.util.List.of(AnimationTabletEntity.class,TransvectorEntity.class,RepairerEntity.class,WarpGateEntity.class,MagnetEntity.class))registration.registerBlockDataProvider(Provider.INSTANCE,type);
  registered=true;
 }
 public void registerClient(snownee.jade.api.IWailaClientRegistration registration){
  clientRegistered=true;
  for(var type:java.util.List.of(AnimationTabletBlock.class,TransvectorBlock.class,RepairerBlock.class,WarpGateBlock.class,MagnetBlock.class))registration.registerBlockComponent(Provider.INSTANCE,type);
 }
 enum Provider implements snownee.jade.api.IServerDataProvider<snownee.jade.api.BlockAccessor>,snownee.jade.api.IBlockComponentProvider {
  INSTANCE;
  public net.minecraft.resources.ResourceLocation getUid(){return net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("ttinkerer","devices");}
  public void appendServerData(net.minecraft.nbt.CompoundTag tag,snownee.jade.api.BlockAccessor accessor){
   var lines=DeviceTooltip.lines(accessor.getBlockEntity());
   if(Boolean.getBoolean("ttinkerer.jadeWorldAudit"))JadeWorldClientAudit.serverCalls++;
   tag.put(DATA,net.minecraft.network.chat.ComponentSerialization.CODEC.listOf().encodeStart(accessor.nbtOps(),lines).getOrThrow());
  }
  public void appendTooltip(snownee.jade.api.ITooltip tooltip,snownee.jade.api.BlockAccessor accessor,snownee.jade.api.config.IPluginConfig config){
   var data=accessor.getServerData().get(DATA);if(data==null)return;
   net.minecraft.network.chat.ComponentSerialization.CODEC.listOf().parse(accessor.nbtOps(),data).result().ifPresent(lines->{lines.forEach(tooltip::add);if(Boolean.getBoolean("ttinkerer.jadeWorldAudit"))JadeWorldClientAudit.observed(accessor.getPosition(),lines);});
  }
 }
 static void audit(net.minecraft.server.level.ServerLevel world,net.minecraft.world.level.block.entity.BlockEntity tile){
  if(!registered)throw new IllegalStateException("Jade did not discover addon plugin");
  var tag=new net.minecraft.nbt.CompoundTag();tag.putInt("OtherProvider",31);var ops=world.registryAccess().createSerializationContext(net.minecraft.nbt.NbtOps.INSTANCE);
  var accessor=(snownee.jade.api.BlockAccessor)java.lang.reflect.Proxy.newProxyInstance(JadePlugin.class.getClassLoader(),new Class<?>[]{snownee.jade.api.BlockAccessor.class},(proxy,method,args)->switch(method.getName()){case "getBlockEntity"->tile;case "nbtOps"->ops;default->throw new UnsupportedOperationException(method.getName());});
  Provider.INSTANCE.appendServerData(tag,accessor);var decoded=net.minecraft.network.chat.ComponentSerialization.CODEC.listOf().parse(ops,tag.get(DATA)).getOrThrow();
  if(tag.getInt("OtherProvider")!=31||!decoded.equals(DeviceTooltip.lines(tile)))throw new IllegalStateException("Jade localized component payload round trip");
 }
}
