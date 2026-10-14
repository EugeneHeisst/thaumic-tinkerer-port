package dev.eugheist.ttmodern;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import dan200.computercraft.api.lua.LuaException;
/** Original marker fields; native TC4Modern uses packed pos and no dimension field. */
final class GolemMarkerCodec {
 static int dimension(ResourceKey<Level> dimension)throws LuaException {
  if(dimension.equals(Level.OVERWORLD))return 0;if(dimension.equals(Level.NETHER))return -1;if(dimension.equals(Level.END))return 1;
  throw new LuaException("No legacy numeric dimension ID for "+dimension.location());
 }
 static CompoundTag decode(Map<?,?> marker)throws LuaException {
  int x=number(marker,"posX").intValue(),y=number(marker,"posY").intValue(),z=number(marker,"posZ").intValue(),dim=number(marker,"dim").intValue();
  byte side=number(marker,"side").byteValue(),color=number(marker,"color").byteValue();
  var result=new CompoundTag();result.putLong("pos",new BlockPos(x,y,z).asLong());result.putByte("side",side);result.putByte("color",color);result.putInt("ttinkerer:legacyDim",dim);return result;
 }
 static Map<String,Object> encode(CompoundTag marker,int currentDimension){
  var pos=BlockPos.of(marker.getLong("pos"));var result=new LinkedHashMap<String,Object>();result.put("posX",pos.getX());result.put("posY",pos.getY());result.put("posZ",pos.getZ());result.put("dim",marker.contains("ttinkerer:legacyDim")?marker.getInt("ttinkerer:legacyDim"):currentDimension);result.put("side",marker.getByte("side"));result.put("color",marker.getByte("color"));return result;
 }
 static Map<Integer,Map<String,Object>> encodeAll(ListTag markers,int currentDimension){var result=new LinkedHashMap<Integer,Map<String,Object>>();for(int i=0;i<markers.size();i++)result.put(i+1,encode(markers.getCompound(i),currentDimension));return result;}
 static ListTag decodeAll(Map<?,?> markers)throws LuaException {var result=new ListTag();for(var value:markers.values()){if(!(value instanceof Map<?,?> marker))throw new LuaException("Each marker must be a table");result.add(decode(marker));}return result;}
 static int index(double value,int size)throws LuaException {int index=(int)value;if(!Double.isFinite(value)||value<0||value>=size)throw new LuaException("marker "+index+" does not exist");return index;}
 private static Number number(Map<?,?> marker,String field)throws LuaException {var value=marker.get(field);if(!(value instanceof Number number)||!Double.isFinite(number.doubleValue()))throw new LuaException("Marker field "+field+" must be a finite number");return number;}
 static void audit()throws LuaException {
  var marker=new LinkedHashMap<String,Object>();marker.put("posX",-12.9);marker.put("posY",72d);marker.put("posZ",19d);marker.put("dim",-1d);marker.put("side",258d);marker.put("color",255d);
  var encoded=encode(decode(marker),0);if(!encoded.equals(Map.of("posX",-12,"posY",72,"posZ",19,"dim",-1,"side",(byte)2,"color",(byte)-1)))throw new IllegalStateException("Golem original marker numeric casts/fields");
  var entries=new LinkedHashMap<Double,Object>();entries.put(1d,marker);entries.put(2d,marker);var nativeList=decodeAll(entries);var output=encodeAll(nativeList,0);if(output.size()!=2||!output.containsKey(1)||!output.containsKey(2)||output.containsKey(0))throw new IllegalStateException("Golem marker one based returned list");
  if(index(0,2)!=0||index(1.9,2)!=1||dimension(Level.OVERWORLD)!=0||dimension(Level.NETHER)!=-1||dimension(Level.END)!=1)throw new IllegalStateException("Golem marker zero based operations/dimensions");
  for(double invalid:new double[]{-1,2,Double.NaN}){boolean rejected=false;try{index(invalid,2);}catch(LuaException expected){rejected=true;}if(!rejected)throw new IllegalStateException("Golem marker invalid index accepted");}
  var invalid=new LinkedHashMap<>(marker);invalid.remove("posX");boolean rejected=false;try{decode(invalid);}catch(LuaException expected){rejected=true;}if(!rejected)throw new IllegalStateException("Golem incomplete marker accepted");
  var vanilla=new CompoundTag();vanilla.putLong("pos",new BlockPos(1,2,3).asLong());if(!encode(vanilla,1).get("dim").equals(1))throw new IllegalStateException("Golem native marker dimension fallback");
  System.out.println("TT GOLEM MARKER CODEC AUDIT: ALL PASS (original fields,integer/byte casts,dimension preservation,one based lists,zero based operations,invalid input)");
 }
}
