package dev.eugheist.ttmodern;
import thaumcraft.api.aspects.*;
import net.minecraft.nbt.*;
import net.minecraft.world.item.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
final class InfusedSeedDataAudit {
 private static void check(boolean ok,String label){if(!ok)throw new IllegalStateException("TT seed data: "+label);}
 static void run(net.minecraft.server.level.ServerLevel level){
  var seed=new ItemStack(Items.WHEAT_SEEDS,3);var tag=new CompoundTag();tag.putString("unrelated","keep");seed.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));seed.set(DataComponents.CUSTOM_NAME,net.minecraft.network.chat.Component.literal("seed data fixture"));
  var input=new AspectList().add(Aspect.AIR,7).add(Aspect.WATER,11);var data=new InfusedSeedData(Aspect.MAGIC,input);input.add(Aspect.FIRE,50);check(data.amount(Aspect.FIRE)==0,"defensive constructor copy");data.write(seed);var read=InfusedSeedData.read(seed);check(read.mainAspect()==Aspect.MAGIC&&read.amount(Aspect.AIR)==7&&read.amount(Aspect.WATER)==11,"compound seed and primal tendencies roundtrip");read.tendencies().add(Aspect.FIRE,20);check(read.amount(Aspect.FIRE)==0,"defensive accessor copy");
  check(seed.getCount()==3&&seed.get(DataComponents.CUSTOM_NAME).getString().equals("seed data fixture")&&seed.get(DataComponents.CUSTOM_DATA).copyTag().getString("unrelated").equals("keep"),"other stack components preserved");
  var parsed=ItemStack.parse(level.registryAccess(),seed.save(level.registryAccess())).orElseThrow();check(InfusedSeedData.read(parsed).mainAspect()==Aspect.MAGIC&&InfusedSeedData.read(parsed).amount(Aspect.WATER)==11,"native full-stack codec");
  var nested=seed.get(DataComponents.CUSTOM_DATA).copyTag();var legacy=new AspectList();legacy.readFromNBT(nested.getCompound("aspectTendencies"));check(legacy.getAmount(Aspect.AIR)==7&&legacy.getAmount(Aspect.WATER)==11,"original AspectList NBT layout");
  var empty=new ItemStack(Items.WHEAT_SEEDS);check(InfusedSeedData.read(empty).mainAspect()==null&&!empty.has(DataComponents.CUSTOM_DATA),"generic read safe and nonmutating");
  var malformed=new CompoundTag();var badList=new ListTag();for(String key:new String[]{"unknown","aer","aer","aqua"}){var entry=new CompoundTag();entry.putString("key",key);entry.putInt("amount",Integer.MAX_VALUE);badList.add(entry);}var badTag=new CompoundTag();badTag.put("Aspects",badList);malformed.put("aspectTendencies",badTag);var bounded=InfusedSeedData.read(malformed);check(bounded.tendencies().visSize()==50&&bounded.growthBound()>0&&bounded.transferBound()>0,"malformed duplicates bounded without integer overflow");
  var water=new InfusedSeedData(Aspect.WATER,new AspectList().add(Aspect.WATER,50));var air=new InfusedSeedData(Aspect.AIR,new AspectList().add(Aspect.AIR,50));check(water.growthBound()==60&&air.transferBound()==500,"original fastest growth/transfer bounds");
  System.out.println("TT SEED DATA AUDIT: ALL PASS (legacy NBT,compound aspects,defensive copies,native stack codec,components,generic seeds,malformed bounds,original growth formulas)");
 }
}
