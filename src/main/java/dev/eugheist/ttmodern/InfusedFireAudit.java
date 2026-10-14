package dev.eugheist.ttmodern;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import static dev.eugheist.ttmodern.InfusedFireTransformations.Kind;
final class InfusedFireAudit {
 private static void check(boolean ok,String label){if(!ok)throw new IllegalStateException("TT fire world: "+label);}
 static void run(ServerLevel level){
  var pos=new BlockPos(7,276,7);var random=new LegacyRandomSource(0){@Override public int nextInt(int bound){return 0;}};
  float oldRain=level.getRainLevel(1);
  boolean gamerule=level.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK),enabled=TinkererConfig.fireEnabled();
  try{
   level.getGameRules().getRule(GameRules.RULE_DOFIRETICK).set(true,level.getServer());
   for(var kind:Kind.values()){
    var fire=ThaumicTinkererModern.FIRES.get(kind).get();
    check(fire.defaultBlockState().is(net.minecraft.tags.BlockTags.FIRE),"native fire tag "+kind);
    check(fire.delay()==(kind==Kind.ENTROPY?1:200),"original delay "+kind);
    check(Block.getDrops(fire.defaultBlockState(),level,pos,null).isEmpty(),"zero drops "+kind);
    check(fire.defaultBlockState().getCollisionShape(level,pos).isEmpty(),"no collision "+kind);
   }
   for(var pair:new Object[][]{{Kind.AIR,Blocks.DIRT,Blocks.SAND},{Kind.WATER,Blocks.SAND,Blocks.ICE},{Kind.FIRE,Blocks.CLAY,Blocks.GLOWSTONE},{Kind.EARTH,Blocks.GRAVEL,Blocks.CLAY},{Kind.ORDER,Blocks.DIAMOND_ORE,Blocks.DIAMOND_BLOCK}}){
    clear(level,pos);var fire=ThaumicTinkererModern.FIRES.get((Kind)pair[0]).get();level.setBlockAndUpdate(pos.below(),Blocks.BEDROCK.defaultBlockState());level.setBlockAndUpdate(pos.east(),((Block)pair[1]).defaultBlockState());level.setBlockAndUpdate(pos,fire.defaultBlockState());
    fire.tick(fire.defaultBlockState(),level,pos,random);check(level.getBlockState(pos.east()).is((Block)pair[2]),"native tick transformation "+pair[0]);
   }
   clear(level,pos);var entropy=ThaumicTinkererModern.FIRES.get(Kind.ENTROPY).get();var air=ThaumicTinkererModern.FIRES.get(Kind.AIR).get();
   level.setBlockAndUpdate(pos.east().below(),Blocks.BEDROCK.defaultBlockState());level.setBlockAndUpdate(pos.east(),air.defaultBlockState());entropy.convertOrPlace(level,pos.east(),0,entropy,random);check(level.getBlockState(pos.east()).is(Blocks.FIRE),"entropy transforms elemental fire into vanilla fire");
   clear(level,pos);level.setBlockAndUpdate(pos.below(),Blocks.BEDROCK.defaultBlockState());level.setBlockAndUpdate(pos.east(),Blocks.DIRT.defaultBlockState());level.setBlockAndUpdate(pos,air.defaultBlockState());
   level.getGameRules().getRule(GameRules.RULE_DOFIRETICK).set(false,level.getServer());air.tick(air.defaultBlockState(),level,pos,random);check(level.getBlockState(pos.east()).is(Blocks.DIRT)&&level.getBlockState(pos).is(air),"gamerule freezes fire");level.getGameRules().getRule(GameRules.RULE_DOFIRETICK).set(true,level.getServer());
   TinkererConfig.ENABLE_FIRE.set(false);air.tick(air.defaultBlockState(),level,pos,random);check(level.getBlockState(pos.east()).is(Blocks.DIRT),"config freezes fire");TinkererConfig.ENABLE_FIRE.set(true);
   var target=pos.east();var decoy=new BlockPos(target.getX(),target.getZ(),target.getY());level.setBlockAndUpdate(decoy,Blocks.DIRT.defaultBlockState());air.convertOrPlace(level,target,0,air,random);check(level.getBlockState(target).is(Blocks.SAND)&&level.getBlockState(decoy).is(Blocks.DIRT),"writes actual target, swapped-axis decoy preserved");level.removeBlock(decoy,false);
   clear(level,pos);for(int x=-2;x<=2;x++)for(int z=-2;z<=2;z++)level.setBlockAndUpdate(pos.offset(x,-1,z),Blocks.BEDROCK.defaultBlockState());level.setBlockAndUpdate(pos.west(),Blocks.DIRT.defaultBlockState());level.setBlockAndUpdate(pos.east(2),Blocks.DIRT.defaultBlockState());level.setBlockAndUpdate(pos,air.defaultBlockState());
   air.tick(air.defaultBlockState(),level,pos,new LegacyRandomSource(0){@Override public int nextInt(int bound){return bound==20?1:0;}});check(level.getBlockState(pos.east()).is(air),"area spread reaches adjacent target beyond direct neighbors");
   clear(level,pos);level.setBlockAndUpdate(pos.below(),Blocks.BEDROCK.defaultBlockState());level.setBlockAndUpdate(pos,air.defaultBlockState());air.tick(air.defaultBlockState(),level,pos,random);check(level.isEmptyBlock(pos),"no transformation neighbors extinguishes");
   clear(level,pos);level.setBlockAndUpdate(pos.below(),Blocks.BEDROCK.defaultBlockState());level.setBlockAndUpdate(pos.east(),Blocks.DIRT.defaultBlockState());level.setBlockAndUpdate(pos,air.defaultBlockState());check(level.getBlockTicks().hasScheduledTick(pos,air),"placement schedules native tick");level.setRainLevel(1);check(level.isRainingAt(pos),"native rain fixture");air.tick(air.defaultBlockState(),level,pos,random);check(!level.getBlockState(pos).is(air),"rain extinguishes origin");level.setRainLevel(oldRain);
   clear(level,pos);var order=ThaumicTinkererModern.FIRES.get(Kind.ORDER).get();level.setBlockAndUpdate(pos.below(),Blocks.BEDROCK.defaultBlockState());level.setBlockAndUpdate(pos.east(),Blocks.DIAMOND_ORE.defaultBlockState());level.setBlockAndUpdate(pos,order.defaultBlockState());order.tick(order.defaultBlockState(),level,pos,new LegacyRandomSource(0){@Override public int nextInt(int bound){return bound==3?2:0;}});check(level.isEmptyBlock(pos.east()),"Ordo direct decay failure erases target");
   clear(level,pos);level.setBlockAndUpdate(pos.below(),Blocks.BEDROCK.defaultBlockState());level.setBlockAndUpdate(pos.west(),Blocks.DIRT.defaultBlockState());level.setBlockAndUpdate(pos.east(),Blocks.TNT.defaultBlockState());level.setBlockAndUpdate(pos,air.defaultBlockState());air.tick(air.defaultBlockState(),level,pos,new LegacyRandomSource(0){@Override public int nextInt(int bound){return bound==20?1:0;}});var primed=level.getEntitiesOfClass(net.minecraft.world.entity.item.PrimedTnt.class,new net.minecraft.world.phys.AABB(pos).inflate(4));check(primed.size()==1,"ordinary flammable TNT is primed once");primed.forEach(net.minecraft.world.entity.Entity::discard);
   var unloaded=new BlockPos(10000000,276,10000000);check(!level.hasChunkAt(unloaded),"unloaded fixture");air.convertOrPlace(level,unloaded,0,air,random);check(!level.hasChunkAt(unloaded)&&!air.adjacentTarget(level,unloaded),"no forced chunk loading");
   for(String key:new String[]{"FIRE_AER","FIRE_AQUA","FIRE_IGNIS","FIRE_TERRA","FIRE_ORDO","FIRE_PERDITIO"}){check(thaumcraft.api.research.ResearchCategories.getResearch(key).parents[0].equals("BRIGHT_NITOR"),"parent "+key);check(dev.eugheist.tc4modern.CrucibleRecipes.byKey().containsKey(key),"crucible recipe "+key);}
   TinkererConfig.ENABLE_FIRE.set(false);check(!dev.eugheist.tc4modern.CrucibleRecipes.byKey().containsKey("FIRE_AER"),"recipe config gate");
  }finally{level.setRainLevel(oldRain);TinkererConfig.ENABLE_FIRE.set(enabled);level.getGameRules().getRule(GameRules.RULE_DOFIRETICK).set(gamerule,level.getServer());clear(level,pos);}
  System.out.println("TT FIRE WORLD AUDIT: ALL PASS (five native tick conversions,entropy,delay,no drops/collision,gamerule/config,axis decoy,area spread,extinguish,rain,Ordo decay,TNT,unloaded chunks,research/recipes)");
 }
 private static void clear(ServerLevel level,BlockPos pos){for(int x=-3;x<=3;x++)for(int z=-3;z<=3;z++)for(int y=-1;y<=3;y++)level.removeBlock(pos.offset(x,y,z),false);}
}
