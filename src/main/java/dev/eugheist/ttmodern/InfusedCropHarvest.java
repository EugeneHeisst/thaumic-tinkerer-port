package dev.eugheist.ttmodern;
import java.util.*;
import java.util.function.*;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.aspects.*;
/** Build-164 harvest order: breed seed, entropy copies, fertilize, then parent-aspect loot. */
final class InfusedCropHarvest {
 static List<ItemStack> collect(InfusedSeedData data,int age,AspectList soil,RandomSource random,Consumer<Aspect> fertilize,Function<Aspect,ItemStack> loot){
  var result=new ArrayList<ItemStack>();
  var seed=InfusedSeedsItem.stack(InfusedCropProcess.mutate(data,age,soil,random));
  new InfusedSeedData(InfusedSeedData.read(seed).mainAspect(),data.tendencies()).write(seed);
  int count=1;int entropy=data.amount(Aspect.ENTROPY);
  while(random.nextInt(10000)<entropy*entropy)count++;
  // Vanilla stack limits are respected when spawning a rare long entropy sequence.
  while(count>0){int amount=Math.min(count,seed.getMaxStackSize());var split=seed.copy();split.setCount(amount);result.add(split);count-=amount;}
  if(age>=7){
   do{fertilize.accept(data.mainAspect());}while(random.nextInt(55)<data.amount(Aspect.EARTH));
   do{var drop=loot.apply(data.mainAspect());if(drop!=null&&!drop.isEmpty())result.add(drop.copy());}while(random.nextInt(75)<data.amount(Aspect.ORDER));
  }
  return result;
 }
 private InfusedCropHarvest(){}
}
