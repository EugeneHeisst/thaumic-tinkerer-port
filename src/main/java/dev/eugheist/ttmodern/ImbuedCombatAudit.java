package dev.eugheist.ttmodern;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.effect.MobEffectInstance;
final class ImbuedCombatAudit {
 private static void check(boolean ok,String label){if(!ok)throw new IllegalStateException("TT imbued combat: "+label);}
 static void run(ServerLevel level){
  var sphere=ImbuedPotionEffects.fireSphere(net.minecraft.util.RandomSource.create(42),10,20,30);check(sphere.size()==30,"thirty original wisps");for(var point:sphere)check(Math.abs(point.distanceTo(new net.minecraft.world.phys.Vec3(10,21,30))-2.5)<1e-10,"original spherical radius and center offset");
  var player=net.neoforged.neoforge.common.util.FakePlayerFactory.getMinecraft(level);player.removeAllEffects();var target=EntityType.COW.create(level);target.setNoAi(true);target.setPos(24,290,24);level.addFreshEntity(target);
  try{
   target.hurt(level.damageSources().playerAttack(player),1);check(!ImbuedPotionEffects.tracked(target,false)&&!ImbuedPotionEffects.tracked(target,true),"plain attacker no tracking");
   player.addEffect(new MobEffectInstance(InfusedPotions.AIR,3600));player.addEffect(new MobEffectInstance(InfusedPotions.FIRE,3600));target.invulnerableTime=0;check(target.hurt(level.damageSources().playerAttack(player),1),"native player damage");check(ImbuedPotionEffects.tracked(target,false)&&ImbuedPotionEffects.tracked(target,true),"native event records both imbued attacks");
   long start=level.getGameTime();long next=start+(5-start%5)%5;target.setDeltaMovement(0,0,0);ImbuedPotionEffects.tickHits(level,next);var motion=target.getDeltaMovement();check(motion.x>=-.5&&motion.x<.5&&motion.y>=0&&motion.y<1&&motion.z>=-.5&&motion.z<.5&&motion.lengthSqr()>0,"air original random velocity bounds");check(target.getRemainingFireTicks()==120,"fire six seconds");
   target.setRemainingFireTicks(0);var before=target.getDeltaMovement();ImbuedPotionEffects.tickHits(level,next+1);check(target.getRemainingFireTicks()==0&&target.getDeltaMovement().equals(before),"five-tick cadence");
   player.removeAllEffects();ImbuedPotionEffects.tickHits(level,start+20);check(ImbuedPotionEffects.tracked(target,false),"air survives exact twenty boundary after dose removed");ImbuedPotionEffects.tickHits(level,start+21);check(!ImbuedPotionEffects.tracked(target,false)&&ImbuedPotionEffects.tracked(target,true),"air expires strictly after twenty while fire persists");
   ImbuedPotionEffects.tickHits(level,start+6000);check(ImbuedPotionEffects.tracked(target,true),"original fire duration six thousand not six hundred");ImbuedPotionEffects.tickHits(level,start+6001);check(!ImbuedPotionEffects.tracked(target,true),"fire strict expiration");
   player.addEffect(new MobEffectInstance(InfusedPotions.AIR,3600));target.invulnerableTime=0;target.hurt(level.damageSources().playerAttack(player),1);target.discard();ImbuedPotionEffects.tickHits(level,start);check(!ImbuedPotionEffects.tracked(target,false),"removed targets cleaned immediately");
   System.out.println("TT IMBUED COMBAT AUDIT: ALL PASS (native attack event,air velocity/fire duration,five tick cadence,dose-independent timers,strict20/6000expiry,removed cleanup)");
  }finally{player.removeAllEffects();target.discard();ImbuedPotionEffects.tickHits(level,level.getGameTime());}
 }
}
