package dev.eugheist.ttmodern;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import thaumcraft.api.aspects.*;
final class InfusedSoilDataAudit {
 private static void check(boolean ok,String label){if(!ok)throw new IllegalStateException("TT infused soil: "+label);}
 static void run(){
  var soil=new InfusedSoilData();var random=new LegacyRandomSource(42);
  for(int i=0;i<20;i++)soil.fertilize(Aspect.MAGIC,random);
  check(soil.aspects().getAmount(Aspect.MAGIC)==20,"compound aspects retained unlike seed tendencies");
  soil.fertilize(Aspect.AIR,random);check(soil.aspects().visSize()==20,"fertilization randomly saturates at twenty");
  var tag=new CompoundTag();tag.putString("unrelated","kept");soil.write(tag);
  var restored=new InfusedSoilData();restored.read(tag);
  check(restored.aspects().aspects.equals(soil.aspects().aspects)&&tag.getString("unrelated").equals("kept"),"legacy aspectList round trip preserves other fields");
  var copy=restored.aspects();copy.add(Aspect.FIRE,100);check(restored.aspects().visSize()==20,"defensive read");
  var hostile=new AspectList().add(Aspect.MAGIC,Integer.MAX_VALUE).add(Aspect.AIR,Integer.MAX_VALUE).add(Aspect.WATER,-1);var payload=new CompoundTag();hostile.writeToNBT(payload);tag.put("aspectList",payload);restored.read(tag);
  check(restored.aspects().visSize()==20&&restored.aspects().getAmount(Aspect.WATER)==0,"overflow and negative saved weights bounded");
  restored.read(new CompoundTag());restored.fertilize(null,random);check(restored.aspects().visSize()==0,"empty save and generic seed safe");
  System.out.println("TT INFUSED SOIL DATA AUDIT: ALL PASS (compound reservoir,cap twenty,legacy NBT,defensive copies,malformed saves)");
 }
}
