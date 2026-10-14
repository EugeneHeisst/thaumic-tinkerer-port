package dev.eugheist.ttmodern;
import java.util.*;
import dan200.computercraft.api.lua.*;
import dan200.computercraft.api.peripheral.IPeripheral;
/** Build164 method names and return arity; all native entity access stays on server thread. */
public final class GolemPeripheral implements IPeripheral {
 private final GolemConnector.Entity tile;
 GolemPeripheral(GolemConnector.Entity tile){this.tile=tile;}
 public String getType(){return "tt_golemconnector";}
 public Object getTarget(){return tile;}
 public boolean equals(IPeripheral other){return other instanceof GolemPeripheral p&&p.tile==tile;}
 private dev.eugheist.tc4modern.StrawGolem golem(){var g=tile.golem();return g!=null&&!g.isRemoved()?g:null;}
 private net.minecraft.nbt.ListTag markers(dev.eugheist.tc4modern.StrawGolem g)throws LuaException {int id=dim();g.markers.removeIf(value->value instanceof net.minecraft.nbt.CompoundTag marker&&marker.contains("ttinkerer:legacyDim")&&marker.getInt("ttinkerer:legacyDim")!=id);return g.markers;}
 private int dim()throws LuaException{return GolemMarkerCodec.dimension(tile.getLevel().dimension());}
 @LuaFunction(mainThread=true) public final Object[] getDecorations()throws LuaException {
  var g=golem();if(g==null||g.decoration().isEmpty())return new Object[0];var result=new LinkedHashMap<Double,String>();
  for(int i=0;i<g.decoration().length();i++){char c=g.decoration().charAt(i);int id="HGBFRVPM".indexOf(c);if(id<0)throw new LuaException("Golem is wearing unknown accessory: "+c);result.put((double)i,dev.eugheist.tc4modern.Golemancy.decoration(id).getDescription().getString());}return new Object[]{result};
 }
 @LuaFunction(mainThread=true) public final Object[] getPosition(){var g=golem();return g==null?new Object[0]:new Object[]{(int)g.getX(),(int)g.getY(),(int)g.getZ()};}
 @LuaFunction(value="getType",mainThread=true) public final Object[] getGolemType(){var g=golem();if(g==null)return new Object[0];String id=g.material().id();return new Object[]{Character.toUpperCase(id.charAt(0))+id.substring(1)};}
 @LuaFunction(mainThread=true) public final Object[] getHealth(){var g=golem();return g==null?new Object[0]:new Object[]{g.getHealth()};}
 @LuaFunction(mainThread=true) public final Object[] getCore()throws LuaException {var g=golem();if(g==null)return new Object[0];int id=g.core();var item=id==-1?dev.eugheist.tc4modern.Golemancy.BLANK.get():dev.eugheist.tc4modern.Golemancy.core(id);if(item==net.minecraft.world.item.Items.AIR)throw new LuaException("Golem has Unknown core: "+id);return new Object[]{item.getDescription().getString()};}
 @LuaFunction(mainThread=true) public final Object[] getHome(){var g=golem();return g==null?new Object[0]:new Object[]{g.home.getX(),g.home.getY(),g.home.getZ(),g.homeFace.get3DDataValue()};}
 @LuaFunction(mainThread=true) public final Object[] setHome(double x,double y,double z,double facing)throws LuaException {var g=golem();if(g==null)return new Object[0];if(!Double.isFinite(x)||!Double.isFinite(y)||!Double.isFinite(z)||!Double.isFinite(facing)||(int)facing<0||(int)facing>5)throw new LuaException("Home facing must be 0..5 and coordinates finite");g.home=new net.minecraft.core.BlockPos((int)x,(int)y,(int)z);g.homeFace=net.minecraft.core.Direction.from3DDataValue((int)facing);g.restrictTo(g.home,35);return getHome();}
 @LuaFunction(mainThread=true) public final Object[] getMarkers()throws LuaException {var g=golem();return g==null?new Object[0]:new Object[]{GolemMarkerCodec.encodeAll(markers(g),dim())};}
 @LuaFunction(mainThread=true) public final Object[] setMarkers(Map<?,?> markers)throws LuaException {var g=golem();if(g==null)return new Object[0];g.markers=GolemMarkerCodec.decodeAll(markers);return getMarkers();}
 @LuaFunction(mainThread=true) public final Object[] newMarker()throws LuaException {var pos=tile.getBlockPos();return new Object[]{Map.of("posX",pos.getX(),"posY",pos.getY(),"posZ",pos.getZ(),"dim",dim(),"color",-1,"side",1)};}
 @LuaFunction(mainThread=true) public final Object[] addMarker(Map<?,?> marker)throws LuaException {var g=golem();if(g==null)return new Object[0];markers(g).add(GolemMarkerCodec.decode(marker));return getMarker(g.markers.size()-1);}
 @LuaFunction(mainThread=true) public final Object[] saveMarker(double index,Map<?,?> marker)throws LuaException {var g=golem();if(g==null)return new Object[0];int i=GolemMarkerCodec.index(index,markers(g).size());g.markers.set(i,GolemMarkerCodec.decode(marker));return getMarker(i);}
 @LuaFunction(mainThread=true) public final Object[] deleteMarker(double index)throws LuaException {var g=golem();if(g==null)return new Object[0];g.markers.remove(GolemMarkerCodec.index(index,markers(g).size()));return new Object[0];}
 @LuaFunction(mainThread=true) public final Object[] getMarker(double index)throws LuaException {var g=golem();return g==null?new Object[0]:new Object[]{GolemMarkerCodec.encode(g.markers.getCompound(GolemMarkerCodec.index(index,markers(g).size())),dim())};}
 @LuaFunction(mainThread=true) public final Object[] getMarkerCount()throws LuaException {var g=golem();return g==null?new Object[0]:new Object[]{(double)markers(g).size()};}
}
