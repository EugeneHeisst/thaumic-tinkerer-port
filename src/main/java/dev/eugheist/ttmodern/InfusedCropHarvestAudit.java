package dev.eugheist.ttmodern;
import java.util.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import thaumcraft.api.aspects.*;
final class InfusedCropHarvestAudit {
 private static void check(boolean ok,String label){if(!ok)throw new IllegalStateException("TT crop harvest: "+label);}
 private static final class Script extends LegacyRandomSource {
  private final int[] values;private int index;
  Script(int...values){super(0);this.values=values;}
  @Override public int nextInt(int bound){if(index>=values.length)throw new IllegalStateException("unexpected harvest RNG "+bound);int value=values[index++];if(value<0||value>=bound)throw new IllegalStateException("invalid scripted value for "+bound);return value;}
  void exhausted(){check(index==values.length,"all expected random trials consumed");}
 }
 static void run(){
  var calls=new ArrayList<String>();var soil=new AspectList().add(Aspect.WATER,4);var tendencies=new AspectList().add(Aspect.ENTROPY,2).add(Aspect.EARTH,2).add(Aspect.ORDER,2);
  var data=new InfusedSeedData(Aspect.EARTH,tendencies);var random=new Script(0,0,3,4,0,2,0,2);
  var drops=InfusedCropHarvest.collect(data,7,soil,random,a->calls.add("soil:"+a.getTag()),a->{calls.add("loot:"+a.getTag());return new ItemStack(Items.DIAMOND);});random.exhausted();
  check(drops.size()==3&&drops.getFirst().getCount()==3,"entropy copies and order extra loot");
  var seedData=InfusedSeedData.read(drops.getFirst());check(seedData.mainAspect()==thaumcraft.common.lib.research.ResearchManager.getCombinationResult(Aspect.WATER,Aspect.EARTH)&&seedData.tendencies().aspects.equals(tendencies.aspects),"offspring mutation and exact inherited tendencies");
  check(calls.equals(List.of("soil:terra","soil:terra","loot:terra","loot:terra")),"fertility before loot and both use parent aspect");
  var immature=new Script(9999);drops=InfusedCropHarvest.collect(data,6,soil,immature,a->{throw new IllegalStateException("immature fertilization");},a->{throw new IllegalStateException("immature loot");});immature.exhausted();check(drops.size()==1&&InfusedSeedData.read(drops.getFirst()).mainAspect()==Aspect.EARTH,"immature one original seed/no mutation/fertility/loot");
  var generic=new Script(0,54,74);drops=InfusedCropHarvest.collect(new InfusedSeedData(null,new AspectList()),7,null,generic,a->check(a==null,"generic soil callback"),a->ItemStack.EMPTY);generic.exhausted();check(drops.size()==1&&InfusedSeedData.read(drops.getFirst()).mainAspect()==null,"generic missing loot is safe");
  var sequence=new int[68];Arrays.fill(sequence,0);sequence[65]=9999;sequence[66]=54;sequence[67]=74;var longRun=new Script(sequence);drops=InfusedCropHarvest.collect(new InfusedSeedData(Aspect.AIR,new AspectList().add(Aspect.ENTROPY,50)),7,null,longRun,a->{},a->ItemStack.EMPTY);longRun.exhausted();check(drops.size()==2&&drops.get(0).getCount()==64&&drops.get(1).getCount()==2,"entropy stacks split without loss");
  check(data.tendencies().aspects.equals(tendencies.aspects)&&soil.getAmount(Aspect.WATER)==4,"harvest does not alias seed/soil input");
  System.out.println("TT CROP HARVEST AUDIT: ALL PASS (mutation/inheritance,entropy copies,earth fertility,order loot,parent aspect,operation order,immature/generic,stack splitting)");
 }
}
