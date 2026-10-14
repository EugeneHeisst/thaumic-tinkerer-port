package dev.eugheist.ttmodern;
import java.util.*;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import thaumcraft.api.aspects.*;
final class InfusedCropProcessAudit {
 private static void check(boolean ok,String label){if(!ok)throw new IllegalStateException("TT crop process: "+label);}
 private static InfusedSeedData seed(Aspect main,AspectList tendencies){return new InfusedSeedData(main,tendencies);}
 static void run(){
  var zero=new LegacyRandomSource(0){@Override public int nextInt(int bound){if(bound<=0)throw new IllegalStateException("nonpositive crop RNG");return 0;}};
  var fail=new LegacyRandomSource(0){@Override public int nextInt(int bound){return bound-1;}};
  var compound=seed(Aspect.MAGIC,new AspectList());var primal=seed(Aspect.AIR,new AspectList());
  check(InfusedCropProcess.grow(compound,0,8,zero)==0&&InfusedCropProcess.grow(compound,0,9,zero)==1&&InfusedCropProcess.grow(compound,7,15,zero)==7&&InfusedCropProcess.grow(compound,3,15,fail)==3,"light nine/one stage/maturity/chance");
  var transfer=InfusedCropProcess.transfer(compound,List.of(primal),zero);check(transfer.recipient().amount(Aspect.AIR)==1&&transfer.donor()==primal,"primal neighbor gives one without debit");
  check(InfusedCropProcess.transfer(primal,List.of(primal),zero)==null&&InfusedCropProcess.transfer(compound,List.of(primal),fail)==null,"primal recipient and failed trial do not exchange");
  var capped=seed(Aspect.MAGIC,new AspectList().add(Aspect.AIR,5));transfer=InfusedCropProcess.transfer(capped,List.of(primal,seed(Aspect.FIRE,new AspectList())),zero);check(transfer.neighbor()==1&&transfer.recipient().amount(Aspect.AIR)==5&&transfer.recipient().amount(Aspect.FIRE)==1,"skip capped primal and continue direction order");
  var donor=seed(Aspect.LIGHT,new AspectList().add(Aspect.WATER,1));transfer=InfusedCropProcess.transfer(compound,List.of(donor),zero);check(transfer.recipient().amount(Aspect.WATER)==1&&transfer.donor().amount(Aspect.WATER)==0&&transfer.donor().tendencies().aspects.containsKey(Aspect.WATER),"compound transfer debits and retains zero entry");
  var tag=new net.minecraft.nbt.CompoundTag();transfer.donor().write(tag);var depleted=InfusedSeedData.read(tag);check(depleted.tendencies().aspects.containsKey(Aspect.WATER),"zero entry persists through original NBT");transfer=InfusedCropProcess.transfer(compound,List.of(depleted),zero);check(transfer.recipient().amount(Aspect.WATER)==1&&transfer.donor().amount(Aspect.WATER)==0,"legacy zero donor edge case preserved");
  check(InfusedCropProcess.transfer(compound,List.of(seed(Aspect.LIGHT,new AspectList()),primal),zero)==null,"empty compound neighbor ends search");
  check(InfusedCropProcess.transfer(seed(Aspect.MAGIC,new AspectList().add(Aspect.WATER,2)),List.of(donor,primal),zero)==null,"weaker compound donor ends search");
  var full=seed(Aspect.MAGIC,new AspectList().add(Aspect.WATER,50));transfer=InfusedCropProcess.transfer(full,List.of(primal),zero);check(transfer.recipient().tendencies().visSize()==50,"random saturation retains total fifty");
  var soil=new AspectList().add(Aspect.WATER,1);check(InfusedCropProcess.mutate(seed(Aspect.EARTH,new AspectList()),7,soil,zero)==thaumcraft.common.lib.research.ResearchManager.getCombinationResult(Aspect.WATER,Aspect.EARTH),"original mature soil combination");check(InfusedCropProcess.mutate(seed(Aspect.EARTH,new AspectList()),6,soil,zero)==Aspect.EARTH&&InfusedCropProcess.mutate(seed(Aspect.EARTH,new AspectList()),7,soil,fail)==Aspect.EARTH,"immature and failed mutation retain main aspect");
  System.out.println("TT CROP PROCESS AUDIT: ALL PASS (growth/light,primal/compound transfer,zero donor/NBT,direction/early exits,cap fifty,mature soil combinations)");
 }
}
