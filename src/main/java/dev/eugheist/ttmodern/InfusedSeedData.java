package dev.eugheist.ttmodern;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import thaumcraft.api.aspects.*;
/** Shared seed/plant state. Legacy nested AspectList keys survive modern stack components. */
record InfusedSeedData(Aspect mainAspect,AspectList tendencies) {
 static final int MAX_TENDENCIES=50;
 InfusedSeedData {
  var clean=new AspectList();int remaining=MAX_TENDENCIES;
  if(tendencies!=null)for(var e:tendencies.aspects.entrySet()){
   var aspect=e.getKey();if(aspect==null||!aspect.isPrimal()||e.getValue()==null||e.getValue()<0)continue;
   int amount=Math.min(remaining,e.getValue());clean.add(aspect,amount);remaining-=amount;
  }
  tendencies=clean;
 }
 @Override public AspectList tendencies(){return tendencies.copy();}
 static InfusedSeedData read(ItemStack stack){return read(stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag());}
 static InfusedSeedData read(CompoundTag tag){
  Aspect main=null;
  var list=tag.getCompound("mainAspect").getList("Aspects",Tag.TAG_COMPOUND);
  for(var entry:list){var value=(CompoundTag)entry;var aspect=Aspect.getAspect(value.getString("key"));if(aspect!=null&&value.getInt("amount")>0){main=aspect;break;}}
  var tendencies=new AspectList();int remaining=MAX_TENDENCIES;
  for(var entry:tag.getCompound("aspectTendencies").getList("Aspects",Tag.TAG_COMPOUND)){
   var value=(CompoundTag)entry;var aspect=Aspect.getAspect(value.getString("key"));if(aspect==null||!aspect.isPrimal())continue;
   if(value.getInt("amount")<0)continue;int amount=Math.min(remaining,value.getInt("amount"));tendencies.add(aspect,amount);remaining-=amount;
  }
  return new InfusedSeedData(main,tendencies);
 }
 void write(ItemStack stack){var tag=stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();write(tag);stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));}
 void write(CompoundTag tag){var main=new CompoundTag();var list=new AspectList();if(mainAspect!=null)list.add(mainAspect,1);list.writeToNBT(main);tag.put("mainAspect",main);var tendencyTag=new CompoundTag();tendencies.writeToNBT(tendencyTag);tag.put("aspectTendencies",tendencyTag);}
 int amount(Aspect aspect){return tendencies.getAmount(aspect);}
 int growthBound(){return (2510-amount(Aspect.WATER)*amount(Aspect.WATER))*6;}
 int transferBound(){return (2550-amount(Aspect.AIR)*amount(Aspect.AIR))*10;}
}
