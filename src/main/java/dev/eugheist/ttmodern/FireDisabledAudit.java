package dev.eugheist.ttmodern;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
final class FireDisabledAudit {
 static void run(net.neoforged.neoforge.event.server.ServerStartedEvent event){
  if(TinkererConfig.fireEnabled())throw new IllegalStateException("Fire config audit did not load disabled common config");
  for(String key:new String[]{"FIRE_AER","FIRE_AQUA","FIRE_IGNIS","FIRE_TERRA","FIRE_ORDO","FIRE_PERDITIO"})if(thaumcraft.api.research.ResearchCategories.getResearch(key)!=null||dev.eugheist.tc4modern.CrucibleRecipes.byKey().containsKey(key))throw new IllegalStateException("Disabled fire progression remains: "+key);
  if(thaumcraft.api.research.ResearchCategories.getResearch("BRIGHT_NITOR")==null||ThaumicTinkererModern.FIRES.size()!=6)throw new IllegalStateException("Disabled config removed unrelated progression/registries");
  InfusedPotionResearchAudit.run();
  var level=event.getServer().overworld();var pos=new BlockPos(7,278,7);var fire=ThaumicTinkererModern.FIRES.get(InfusedFireTransformations.Kind.AIR).get();
  try{level.setBlockAndUpdate(pos.below(),Blocks.BEDROCK.defaultBlockState());level.setBlockAndUpdate(pos.east(),Blocks.DIRT.defaultBlockState());level.setBlockAndUpdate(pos,fire.defaultBlockState());fire.tick(fire.defaultBlockState(),level,pos,net.minecraft.util.RandomSource.create(0));if(!level.getBlockState(pos.east()).is(Blocks.DIRT)||!level.getBlockState(pos).is(fire))throw new IllegalStateException("Disabled fire changed world");}finally{level.removeBlock(pos,false);level.removeBlock(pos.below(),false);level.removeBlock(pos.east(),false);}
  System.out.println("TT FIRE CONFIG AUDIT: ALL PASS (disabled at startup,no research/recipes,registries/base progression retained,world tick frozen)");
 }
}
