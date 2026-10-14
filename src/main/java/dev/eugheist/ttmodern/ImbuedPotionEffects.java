package dev.eugheist.ttmodern;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
@net.neoforged.fml.common.EventBusSubscriber(modid=ThaumicTinkererModern.ID)
public final class ImbuedPotionEffects {
 private record Hit(ServerLevel level,long time){}
 private static final java.util.Map<net.minecraft.world.entity.LivingEntity,Hit> AIR_HITS=new java.util.WeakHashMap<>(),FIRE_HITS=new java.util.WeakHashMap<>();
 @net.neoforged.bus.api.SubscribeEvent public static void incoming(net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent event){
  if(!(event.getEntity().level() instanceof ServerLevel level)||!(event.getSource().getEntity() instanceof Player attacker))return;
  var target=event.getEntity();if(attacker.hasEffect(InfusedPotions.EARTH))earth(attacker,target);if(attacker.hasEffect(InfusedPotions.AIR))AIR_HITS.put(target,new Hit(level,level.getGameTime()));if(attacker.hasEffect(InfusedPotions.FIRE))FIRE_HITS.put(target,new Hit(level,level.getGameTime()));
 }
 @net.neoforged.bus.api.SubscribeEvent public static void levelTick(net.neoforged.neoforge.event.tick.LevelTickEvent.Post event){if(event.getLevel() instanceof ServerLevel level)tickHits(level,level.getGameTime());}
 static boolean tracked(net.minecraft.world.entity.LivingEntity entity,boolean fire){return (fire?FIRE_HITS:AIR_HITS).containsKey(entity);}
 static void tickHits(ServerLevel level,long now){applyHits(AIR_HITS,level,now,false);applyHits(FIRE_HITS,level,now,true);}
 private static void applyHits(java.util.Map<net.minecraft.world.entity.LivingEntity,Hit> hits,ServerLevel level,long now,boolean fire){
  var iterator=hits.entrySet().iterator();while(iterator.hasNext()){var entry=iterator.next();var target=entry.getKey();var hit=entry.getValue();
   if(target.isRemoved()||!target.isAlive()||target.level()!=hit.level()){iterator.remove();continue;}if(hit.level()!=level)continue;
   if(now%5==0){if(fire){target.igniteForSeconds(6);for(var point:fireSphere(level.random,target.getX(),target.getY(),target.getZ()))net.neoforged.neoforge.network.PacketDistributor.sendToPlayersNear(level,null,target.getX(),target.getY(),target.getZ(),64,new dev.eugheist.tc4modern.InfusionContent.FxPayload(171,point.x,point.y,point.z,0,0,0,0));}else{var random=level.random;target.setDeltaMovement(random.nextFloat()-.5,random.nextFloat(),random.nextFloat()-.5);target.hurtMarked=true;net.neoforged.neoforge.network.PacketDistributor.sendToPlayersNear(level,null,target.getX(),target.getY(),target.getZ(),64,new dev.eugheist.tc4modern.InfusionContent.FxPayload(169,target.getX(),target.getY(),target.getZ(),.5,0,0,0));}}
   // Build 164 checks strict expiration after the final possible application.
   if(now>hit.time()+(fire?6000:20))iterator.remove();
  }
 }
 static java.util.List<net.minecraft.world.phys.Vec3> fireSphere(net.minecraft.util.RandomSource random,double x,double y,double z){var points=new java.util.ArrayList<net.minecraft.world.phys.Vec3>(30);for(int i=0;i<30;i++){double theta=random.nextFloat()*2f*Math.PI,phi=random.nextFloat()*2f*Math.PI;points.add(new net.minecraft.world.phys.Vec3(x+2.5*Math.sin(theta)*Math.cos(phi),y+1+2.5*Math.sin(theta)*Math.sin(phi),z+2.5*Math.cos(theta)));}return points;}
 @net.neoforged.bus.api.SubscribeEvent public static void playerTick(net.neoforged.neoforge.event.tick.PlayerTickEvent.Post event){water(event.getEntity());}
 static int earth(Player player,net.minecraft.world.entity.LivingEntity target){
  if(!(player.level() instanceof ServerLevel level)||target.level()!=level||!player.hasEffect(InfusedPotions.EARTH))return 0;
  boolean xAxis=Math.abs(target.getZ()-player.getZ())<Math.abs(target.getX()-player.getX());int x=(int)((target.getX()+player.getX())/2),y=(int)(player.getY()+2),z=(int)((target.getZ()+player.getZ())/2);int count=0;
  for(int i=-2;i<=2;i++)for(int j=-2;j<=2;j++){var pos=xAxis?new BlockPos(x,y+i,z+j):new BlockPos(x+j,y+i,z);
   if(!level.isInWorldBounds(pos)||!level.hasChunkAt(pos)||(!level.isEmptyBlock(pos)&&!level.getBlockState(pos).is(ThaumicTinkererModern.FORCEFIELD.get())))continue;
   if(level.setBlockAndUpdate(pos,ThaumicTinkererModern.FORCEFIELD.get().defaultBlockState())){count++;net.neoforged.neoforge.network.PacketDistributor.sendToPlayersNear(level,null,pos.getX(),pos.getY(),pos.getZ(),64,new dev.eugheist.tc4modern.InfusionContent.FxPayload(170,pos.getX(),pos.getY(),pos.getZ(),100,100,0,0));}
  }return count;
 }
 static int water(Player player){
  if(!(player.level() instanceof ServerLevel level)||!player.hasEffect(InfusedPotions.WATER))return 0;
  int changed=0;
  // Java truncation and exclusive floating upper bounds intentionally match build 164, including negative coordinates.
  for(int x=(int)(player.getX()-2);x<player.getX()+2;x++)for(int y=(int)(player.getY()-2);y<player.getY()+2;y++)for(int z=(int)(player.getZ()-2);z<player.getZ()+2;z++){
   var pos=new BlockPos(x,y,z);if(!level.isInWorldBounds(pos)||!level.hasChunkAt(pos)||!level.getBlockState(pos).is(Blocks.LAVA))continue;
   if(level.setBlockAndUpdate(pos,Blocks.OBSIDIAN.defaultBlockState())){changed++;net.neoforged.neoforge.network.PacketDistributor.sendToPlayersNear(level,null,x+.5,y+.5,z+.5,64,new dev.eugheist.tc4modern.InfusionContent.FxPayload(169,x+.5,y+.5,z+.5,1.2,0,0,0));}
  }
  return changed;
 }
}
