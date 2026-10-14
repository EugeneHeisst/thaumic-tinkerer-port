package dev.eugheist.ttmodern;
final class GolemConnectorAudit {
 static void run(net.minecraft.server.level.ServerLevel world,net.minecraft.core.BlockPos at){
  GolemConnectorResearch.audit();
  world.setBlock(at,GolemConnector.BLOCK.get().defaultBlockState(),3);var tile=(GolemConnector.Entity)world.getBlockEntity(at);var golem=dev.eugheist.tc4modern.Golemancy.ENTITY.get().create(world);if(golem==null)throw new IllegalStateException("Golem fixture creation");
  golem.setPos(at.getX()+2,at.getY(),at.getZ());golem.setNoAi(true);world.addFreshEntity(golem);
  try{
   var peripheral=(GolemPeripheral)world.getCapability(dan200.computercraft.api.peripheral.PeripheralCapability.get(),at,net.minecraft.core.Direction.NORTH);
   if(peripheral==null||!peripheral.getType().equals("tt_golemconnector")||peripheral.getPosition().length!=0||peripheral.getHealth().length!=0||peripheral.getMarkerCount().length!=0||peripheral.getDecorations().length!=0)throw new IllegalStateException("Golem empty peripheral return arity");
   GolemBinderAudit.run(world,at,tile,golem);
   tile.connect(golem.getUUID());if(tile.golem()!=golem)throw new IllegalStateException("Golem UUID resolution");
   if(!java.util.Arrays.equals(peripheral.getPosition(),new Object[]{at.getX()+2,at.getY(),at.getZ()})||!java.util.Arrays.equals(peripheral.getGolemType(),new Object[]{"Straw"})||((Number)peripheral.getHealth()[0]).floatValue()!=golem.getHealth()||!peripheral.getCore()[0].equals(dev.eugheist.tc4modern.Golemancy.BLANK.get().getDescription().getString()))throw new IllegalStateException("Golem native queries");
   var original=new net.minecraft.nbt.CompoundTag();golem.addAdditionalSaveData(original);
   String[] materials={"Straw","Wood","Tallow","Clay","Flesh","Stone","Iron","Thaumium"};
   for(int i=0;i<materials.length;i++){var data=original.copy();data.putByte("Material",(byte)i);golem.readAdditionalSaveData(data);if(!peripheral.getGolemType()[0].equals(materials[i]))throw new IllegalStateException("Golem material query "+i);}
   for(int i=-1;i<=11;i++){var data=original.copy();if(i>=0)data.getCompound("Golem").putByte("core",(byte)i);golem.readAdditionalSaveData(data);var expected=i<0?dev.eugheist.tc4modern.Golemancy.BLANK.get():dev.eugheist.tc4modern.Golemancy.core(i);if(!peripheral.getCore()[0].equals(expected.getDescription().getString()))throw new IllegalStateException("Golem core query "+i);}
   String decorations="HGBFRVPM";
   for(int i=0;i<decorations.length();i++){var data=original.copy();data.getCompound("Golem").putString("deco",decorations.substring(i,i+1));golem.readAdditionalSaveData(data);var actual=(java.util.Map<?,?>)peripheral.getDecorations()[0];if(actual.size()!=1||!dev.eugheist.tc4modern.Golemancy.decoration(i).getDescription().getString().equals(actual.get(0d)))throw new IllegalStateException("Golem decoration query "+decorations.charAt(i));}
   golem.readAdditionalSaveData(original);if(peripheral.getDecorations().length!=0)throw new IllegalStateException("Golem empty decorations arity");
   var home=peripheral.setHome(-4.9,73.8,12.1,2.9);if(!java.util.Arrays.equals(home,new Object[]{-4,73,12,2})||!golem.getRestrictCenter().equals(new net.minecraft.core.BlockPos(-4,73,12))||golem.getRestrictRadius()!=35)throw new IllegalStateException("Golem original home casts/radius");
   var marker=(java.util.Map<?,?>)peripheral.newMarker()[0];if(!marker.get("posX").equals(at.getX())||!marker.get("side").equals(1)||!marker.get("color").equals(-1))throw new IllegalStateException("Golem marker defaults");
   peripheral.addMarker(marker);peripheral.addMarker(marker);if(!peripheral.getMarkerCount()[0].equals(2d)||((java.util.Map<?,?>)peripheral.getMarkers()[0]).size()!=2||!peripheral.getMarker(0)[0].equals(peripheral.getMarker(1)[0]))throw new IllegalStateException("Golem native marker append/query");
   var replacement=new java.util.LinkedHashMap<Object,Object>(marker);replacement.put("posX",-22d);peripheral.saveMarker(1,replacement);if(!((java.util.Map<?,?>)peripheral.getMarker(1)[0]).get("posX").equals(-22))throw new IllegalStateException("Golem marker save zero based");peripheral.deleteMarker(0);if(!peripheral.getMarkerCount()[0].equals(1d))throw new IllegalStateException("Golem marker delete");peripheral.setMarkers(java.util.Map.of(1d,marker));if(!peripheral.getMarkerCount()[0].equals(1d))throw new IllegalStateException("Golem marker replace");
   boolean invalid=false;try{peripheral.deleteMarker(-1);}catch(dan200.computercraft.api.lua.LuaException expected){invalid=true;}if(!invalid||golem.markers.size()!=1)throw new IllegalStateException("Golem marker invalid index conservation");
   var foreign=new java.util.LinkedHashMap<Object,Object>(marker);foreign.put("dim",-1d);peripheral.setMarkers(java.util.Map.of(1d,marker,2d,foreign));if(!peripheral.getMarkerCount()[0].equals(1d)||golem.markers.size()!=1)throw new IllegalStateException("Golem foreign dimension marker validation");
   invalid=false;try{peripheral.addMarker(foreign);}catch(dan200.computercraft.api.lua.LuaException expected){invalid=true;}if(!invalid||golem.markers.size()!=1)throw new IllegalStateException("Golem foreign append must be discarded before native AI");
   tile.camouflage(net.minecraft.world.level.block.Blocks.STONE.defaultBlockState());var saved=tile.saveWithoutMetadata(world.registryAccess());
   tile.connect(null);tile.loadWithComponents(saved,world.registryAccess());if(!golem.getUUID().equals(tile.connected())||tile.golem()!=null||!tile.camouflage().is(net.minecraft.world.level.block.Blocks.STONE))throw new IllegalStateException("Golem UUID/camouflage save and cleared entity cache");tile.verify();if(tile.golem()!=golem)throw new IllegalStateException("Golem reload resolves UUID");
   golem.setPos(at.getX()+40,at.getY(),at.getZ());tile.verify();if(tile.golem()!=null||!golem.getUUID().equals(tile.connected()))throw new IllegalStateException("Golem out of range invalidation/persistent binding");golem.setPos(at.getX()+2,at.getY(),at.getZ());tile.verify();if(tile.golem()!=golem)throw new IllegalStateException("Golem returns to range");
   golem.discard();tile.verify();if(tile.golem()!=null)throw new IllegalStateException("Golem removed entity cache");tile.connect(null);if(tile.connected()!=null||tile.golem()!=null)throw new IllegalStateException("Golem disconnect");
   System.out.println("TT GOLEM CONNECTOR ENTITY AUDIT: ALL PASS (UUID link,save/reload,camouflage,range exit/reentry,removed entity,disconnect,queries,home radius35,marker CRUD/zero based indexes)");
  }catch(dan200.computercraft.api.lua.LuaException error){throw new IllegalStateException(error);}finally{golem.discard();}
 }
}
