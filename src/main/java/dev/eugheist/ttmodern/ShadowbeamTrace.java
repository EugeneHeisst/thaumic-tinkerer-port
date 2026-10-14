package dev.eugheist.ttmodern;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.*;
/** Unspawned, synchronous legacy throwable. Neither gravity nor drag changes its cached movement. */
final class ShadowbeamTrace {
 record Result(Vec3 position,Vec3 velocity,int blockHits,int entityHits){}
 static Result run(ServerLevel level,Player shooter,ShadowbeamPath.Start start){
  return run(level,shooter,start,null);
 }
 static Result run(ServerLevel level,Player shooter,ShadowbeamPath.Start start,java.util.function.Consumer<Vec3> sparkle){
  Vec3 position=start.position(),velocity=start.velocity();int blockHits=0,entityHits=0;
  for(int step=0;step<ShadowbeamPath.STEPS;step++){
   var end=position.add(velocity);
   var block=level.clip(new ClipContext(position,end,ClipContext.Block.OUTLINE,ClipContext.Fluid.NONE,shooter));
   boolean blocked=block.getType()==HitResult.Type.BLOCK;
   var entityEnd=blocked?block.getLocation():end;
   var bounds=new AABB(position.x-.125,position.y,position.z-.125,position.x+.125,position.y+.25,position.z+.125).expandTowards(velocity).inflate(1);
   Entity target=null;double nearest=0;
   for(var candidate:level.getEntities((Entity)null,bounds,e->e.isPickable())){
    if(candidate==shooter&&step+1<5)continue;
    var intercept=ShadowbeamPath.intercept(candidate.getBoundingBox().inflate((double).3f),position,entityEnd);
    if(intercept.isEmpty())continue;
    double distance=position.distanceTo(intercept.get());
    if(distance<nearest||nearest==0){nearest=distance;target=candidate;}
   }
   if(target!=null){
    entityHits++;
    if(target!=shooter&&(!(target instanceof Player)||level.getServer().isPvpAllowed()))target.hurt(level.damageSources().playerAttack(shooter),8);
   }else if(blocked&&!level.getBlockState(block.getBlockPos()).is(Blocks.NETHER_PORTAL)){
    blockHits++;velocity=ShadowbeamPath.reflect(velocity,block.getDirection());
   }
   // Original impact changes motion before parent moves, from the old position.
   position=position.add(velocity);
   if(sparkle!=null&&ShadowbeamPath.sparkle(step))sparkle.accept(new Vec3((float)position.x,(float)position.y,(float)position.z));
  }
  return new Result(position,velocity,blockHits,entityHits);
 }
}
