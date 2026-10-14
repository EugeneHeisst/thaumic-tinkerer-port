package dev.eugheist.ttmodern;
import java.util.List;
import net.minecraft.util.RandomSource;
import thaumcraft.api.aspects.*;
/** Build-164 growth and tendency exchange; world mutation is performed by the crop entity. */
final class InfusedCropProcess {
 record Transfer(int neighbor,InfusedSeedData recipient,InfusedSeedData donor,Aspect aspect){}
 static int grow(InfusedSeedData data,int age,int light,RandomSource random){return light>=9&&age<7&&random.nextInt(data.growthBound())==0?age+1:age;}
 static Transfer transfer(InfusedSeedData data,List<InfusedSeedData> neighbors,RandomSource random){
  if(random.nextInt(data.transferBound())!=0||data.mainAspect()==null||data.mainAspect().isPrimal())return null;
  for(int i=0;i<neighbors.size();i++){
   var neighbor=neighbors.get(i);if(neighbor==null||neighbor.mainAspect()==null)continue;
   var own=data.tendencies();var other=neighbor.tendencies();var aspect=neighbor.mainAspect();
   if(aspect.isPrimal()){
    if(own.getAmount(aspect)>=5)continue;
    own.add(aspect,1);saturate(own,50,random);return new Transfer(i,new InfusedSeedData(data.mainAspect(),own),neighbor,aspect);
   }
   var choices=other.getAspects();if(choices.length==0)return null;
   aspect=choices[random.nextInt(choices.length)];
   if(other.getAmount(aspect)<own.getAmount(aspect))return null;
   // reduce deliberately retains zero entries; even a zero donor is eligible in build 164.
   own.add(aspect,1);other.reduce(aspect,1);saturate(own,50,random);
   return new Transfer(i,new InfusedSeedData(data.mainAspect(),own),new InfusedSeedData(neighbor.mainAspect(),other),aspect);
  }
  return null;
 }
 static void saturate(AspectList list,int maximum,RandomSource random){
  while(list.visSize()>maximum){var candidates=java.util.Arrays.stream(list.getAspects()).filter(a->list.getAmount(a)>0).toList();if(candidates.isEmpty())break;list.remove(candidates.get(random.nextInt(candidates.size())),1);}
 }
 static Aspect mutate(InfusedSeedData data,int age,AspectList soil,RandomSource random){
  if(age<7||data.mainAspect()==null||soil==null)return data.mainAspect();
  for(var aspect:soil.getAspects()){
   int amount=Math.min(20,Math.max(0,soil.getAmount(aspect)));
   if(random.nextInt(10)>=(data.amount(Aspect.FIRE)+1)*amount*amount)continue;
   var result=thaumcraft.common.lib.research.ResearchManager.getCombinationResult(aspect,data.mainAspect());if(result!=null)return result;
  }
  return data.mainAspect();
 }
 private InfusedCropProcess(){}
}
