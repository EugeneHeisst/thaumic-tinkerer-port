package dev.eugheist.ttmodern;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import thaumcraft.api.aspects.*;
/** Build-164 farmland reservoir; separate from the seed's primal-only tendencies. */
final class InfusedSoilData {
 static final int MAX_ASPECTS=20;
 private AspectList aspects=new AspectList();
 AspectList aspects(){return aspects.copy();}
 void fertilize(Aspect aspect,RandomSource random){
  if(aspect==null)return;
  aspects.add(aspect,1);InfusedCropProcess.saturate(aspects,MAX_ASPECTS,random);
 }
 void write(CompoundTag tag){var data=new CompoundTag();aspects.writeToNBT(data);tag.put("aspectList",data);}
 void read(CompoundTag tag){
  // Validate before summing: arbitrary saved values must not overflow or produce invalid RNG bounds.
  var raw=new AspectList();raw.readFromNBT(tag.getCompound("aspectList"));var clean=new AspectList();int remaining=MAX_ASPECTS;
  for(var entry:raw.aspects.entrySet()){
   if(entry.getKey()==null||entry.getValue()==null||entry.getValue()<0)continue;
   int amount=Math.min(remaining,entry.getValue());clean.add(entry.getKey(),amount);remaining-=amount;
  }
  aspects=clean;
 }
}
