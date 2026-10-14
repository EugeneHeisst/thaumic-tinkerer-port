package dev.eugheist.ttmodern;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
final class InfusedFireClientAudit {
 private static final BlockPos ORIGIN=new BlockPos(12,202,6),NORMAL=new BlockPos(28,202,6),FAST=new BlockPos(40,202,6);
 private static final java.util.List<net.minecraft.world.entity.animal.Cow> COWS=new java.util.ArrayList<>();
 private static long cadenceStart=-1;private static boolean oldRule,normalTickObserved;
 static volatile boolean damageChecked,cadenceChecked;
 private static void check(boolean ok,String label){EnchanterClientAudit.check(ok,"infused fire: "+label);}
 static void tick(Minecraft mc,int tick){
  InfusedBookClientAudit.tick(mc,tick);
  ImbuedWorldClientAudit.tick(mc,tick);
  ImbuedCombatWorldAudit.tick(mc,tick);
  if(tick==302){ImbuedFireWisp.audit(mc);XPTalismanFx.audit(mc);}
  if(tick==300){var server=mc.getSingleplayerServer();var uuid=mc.player.getUUID();NeoForge.EVENT_BUS.addListener(InfusedFireClientAudit::serverTick);server.execute(()->{
   var p=server.getPlayerList().getPlayer(uuid);var level=p.serverLevel();oldRule=level.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK);level.getGameRules().getRule(GameRules.RULE_DOFIRETICK).set(false,server);
   level.getEntitiesOfClass(net.minecraft.world.entity.animal.Cow.class,new net.minecraft.world.phys.AABB(net.minecraft.world.phys.Vec3.atLowerCornerOf(ORIGIN),net.minecraft.world.phys.Vec3.atLowerCornerOf(ORIGIN.offset(12,3,2))).inflate(2)).forEach(net.minecraft.world.entity.Entity::discard);
   for(int x=-1;x<=13;x++)for(int z=-1;z<=1;z++)level.setBlockAndUpdate(ORIGIN.offset(x,-1,z),Blocks.BEDROCK.defaultBlockState());
   int i=0;for(var kind:InfusedFireTransformations.Kind.values()){var pos=ORIGIN.east(i++*2);level.setBlockAndUpdate(pos,ThaumicTinkererModern.FIRES.get(kind).get().defaultBlockState());}
   p.getAbilities().mayfly=true;p.getAbilities().flying=true;p.onUpdateAbilities();p.connection.teleport(17.5,205,15.5,180,20);
  });}
  if(tick==325){int i=0;for(var kind:InfusedFireTransformations.Kind.values()){var pos=ORIGIN.east(i++*2);check(mc.level.getBlockState(pos).is(ThaumicTinkererModern.FIRES.get(kind).get()),"client receives "+kind);var sprite=mc.getBlockRenderer().getBlockModel(mc.level.getBlockState(pos)).getParticleIcon();check(!sprite.contents().name().getPath().contains("missing"),"animated texture "+kind);}EnchanterClientAudit.Client.shot(mc,"infused-fire-gallery.png");}
  if(tick==326)smoke(mc);
  if(tick==330)mc.getSingleplayerServer().execute(()->{var level=mc.getSingleplayerServer().overworld();int i=0;for(var kind:InfusedFireTransformations.Kind.values()){var cow=net.minecraft.world.entity.EntityType.COW.create(level);var pos=ORIGIN.east(i++*2);cow.setNoAi(true);cow.setNoGravity(false);cow.setPos(pos.getX()+.5,pos.getY()+.1,pos.getZ()+.5);cow.setDeltaMovement(0,-.05,0);level.addFreshEntity(cow);COWS.add(cow);}var control=net.minecraft.world.entity.EntityType.COW.create(level);var cp=ORIGIN.east(12);level.setBlockAndUpdate(cp,Blocks.FIRE.defaultBlockState());control.setNoAi(true);control.setPos(cp.getX()+.5,cp.getY()+.1,cp.getZ()+.5);control.setDeltaMovement(0,-.05,0);level.addFreshEntity(control);COWS.add(control);});
  if(tick==375)mc.getSingleplayerServer().execute(()->{check(COWS.size()==7,"six native collision fixtures plus vanilla control");for(int i=0;i<COWS.size();i++){var cow=COWS.get(i);System.out.println("TT FIRE CONTACT: "+i+" HP="+cow.getHealth()+" fireTicks="+cow.getRemainingFireTicks());check(cow.getHealth()<cow.getMaxHealth()&&cow.getRemainingFireTicks()>0,"native entity collision damages and ignites "+(i==6?"vanilla control":InfusedFireTransformations.Kind.values()[i]));cow.discard();}COWS.clear();damageChecked=true;});
  if(tick==380)mc.getSingleplayerServer().execute(()->{var level=mc.getSingleplayerServer().overworld();for(int i=0;i<7;i++)level.removeBlock(ORIGIN.east(i*2),false);for(var pos:java.util.List.of(NORMAL,FAST)){level.setBlockAndUpdate(pos.below(),Blocks.BEDROCK.defaultBlockState());level.removeBlock(pos,false);}level.getGameRules().getRule(GameRules.RULE_DOFIRETICK).set(true,level.getServer());level.setBlockAndUpdate(NORMAL,ThaumicTinkererModern.FIRES.get(InfusedFireTransformations.Kind.AIR).get().defaultBlockState());level.setBlockAndUpdate(FAST,ThaumicTinkererModern.FIRES.get(InfusedFireTransformations.Kind.ENTROPY).get().defaultBlockState());cadenceStart=level.getGameTime();});
 }
 private static void smoke(Minecraft mc){
  var pos=ORIGIN.above(4);var wall=pos.west();var old=mc.level.getBlockState(wall);
  try{mc.level.setBlock(wall,Blocks.OAK_PLANKS.defaultBlockState(),3);mc.particleEngine.tick();var existing=java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<net.minecraft.client.particle.Particle,Boolean>());mc.particleEngine.iterateParticles(existing::add);
   var fire=ThaumicTinkererModern.FIRES.get(InfusedFireTransformations.Kind.AIR).get();fire.animateTick(fire.defaultBlockState(),mc.level,pos,net.minecraft.util.RandomSource.create(7));mc.particleEngine.tick();int[] added={0};mc.particleEngine.iterateParticles(p->{if(!existing.contains(p)&&p instanceof net.minecraft.client.particle.LargeSmokeParticle)added[0]++;});check(added[0]==2,"wall animateTick creates two native large-smoke particles (actual="+added[0]+")");
  }finally{mc.level.setBlock(wall,old,3);}
 }
 private static void serverTick(LevelTickEvent.Post event){
  if(!(event.getLevel() instanceof ServerLevel level)||level.dimension()!=Level.OVERWORLD)return;
  for(var cow:COWS)if(cow.isAlive())cow.move(net.minecraft.world.entity.MoverType.SELF,new net.minecraft.world.phys.Vec3(0,-.01,0));
  if(cadenceStart<0||cadenceChecked)return;
  long elapsed=level.getGameTime()-cadenceStart;
  if(elapsed==11)check(level.isEmptyBlock(FAST),"entropy naturally extinguishes by tick 11");
  if(elapsed>0&&elapsed<200&&!level.getBlockState(NORMAL).is(ThaumicTinkererModern.FIRES.get(InfusedFireTransformations.Kind.AIR).get()))throw new IllegalStateException("ordinary fire changed before native tick 200: "+elapsed);
  if(elapsed>=200&&elapsed<=209&&level.isEmptyBlock(NORMAL)&&!normalTickObserved){normalTickObserved=true;check(true,"first ordinary fire tick observed at elapsed="+elapsed);}
  if(elapsed==50)check(level.getBlockState(NORMAL).is(ThaumicTinkererModern.FIRES.get(InfusedFireTransformations.Kind.AIR).get()),"ordinary fire remains before scheduled tick 200");
  if(elapsed==220){check(normalTickObserved&&level.isEmptyBlock(NORMAL),"ordinary fire naturally extinguishes after first 200..209 tick");level.getGameRules().getRule(GameRules.RULE_DOFIRETICK).set(oldRule,level.getServer());for(var pos:java.util.List.of(NORMAL,FAST)){level.removeBlock(pos,false);level.removeBlock(pos.below(),false);}cadenceChecked=true;cadenceStart=-1;}
 }
}
