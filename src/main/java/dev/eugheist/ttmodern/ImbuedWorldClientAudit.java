package dev.eugheist.ttmodern;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
/** Real player ticks and server-to-client updates, without calling the effect handler directly. */
final class ImbuedWorldClientAudit {
 private static final BlockPos LAVA=new BlockPos(18,203,15);
 private static final java.util.Map<BlockPos,net.minecraft.world.level.block.state.BlockState> OLD=new java.util.LinkedHashMap<>();
 static volatile boolean waterChecked;
 private static void check(boolean ok,String label){EnchanterClientAudit.check(ok,"imbued world: "+label);}
 static void tick(Minecraft mc,int tick){
  if(tick==400)mc.getSingleplayerServer().execute(()->{var p=mc.getSingleplayerServer().getPlayerList().getPlayer(mc.player.getUUID());var level=p.serverLevel();p.removeEffect(InfusedPotions.WATER);p.connection.teleport(17.5,205,15.5,180,20);OLD.clear();for(var direction:net.minecraft.core.Direction.values()){var pos=LAVA.relative(direction);OLD.put(pos,level.getBlockState(pos));level.setBlockAndUpdate(pos,Blocks.BEDROCK.defaultBlockState());}OLD.put(LAVA,level.getBlockState(LAVA));level.setBlockAndUpdate(LAVA,Blocks.LAVA.defaultBlockState());});
  if(tick==410){check(mc.level.getBlockState(LAVA).is(Blocks.LAVA),"ordinary real player ticks preserve lava without imbued dose");mc.getSingleplayerServer().execute(()->check(mc.getSingleplayerServer().overworld().getBlockState(LAVA).is(Blocks.LAVA),"server agrees no-dose protection"));}
  if(tick==420)mc.getSingleplayerServer().execute(()->mc.getSingleplayerServer().getPlayerList().getPlayer(mc.player.getUUID()).addEffect(new net.minecraft.world.effect.MobEffectInstance(InfusedPotions.WATER,3600)));
  if(tick==430){check(mc.player.hasEffect(InfusedPotions.WATER),"native effect synchronization");check(mc.level.getBlockState(LAVA).is(Blocks.OBSIDIAN),"real player tick conversion synchronizes obsidian");int[] count={0};mc.particleEngine.iterateParticles(p->{if(p instanceof dev.eugheist.tc4modern.NodeConversionBurst&&p.getBoundingBox().getCenter().distanceToSqr(net.minecraft.world.phys.Vec3.atCenterOf(LAVA))<.01)count[0]++;});check(count[0]==1,"actual network packet creates one conversion burst at lava (count="+count[0]+")");mc.getSingleplayerServer().execute(()->{check(mc.getSingleplayerServer().overworld().getBlockState(LAVA).is(Blocks.OBSIDIAN),"server authoritative conversion");waterChecked=true;});}
  if(tick==440)mc.getSingleplayerServer().execute(()->{var p=mc.getSingleplayerServer().getPlayerList().getPlayer(mc.player.getUUID());p.removeEffect(InfusedPotions.WATER);for(var entry:OLD.entrySet())p.serverLevel().setBlockAndUpdate(entry.getKey(),entry.getValue());OLD.clear();check(waterChecked,"water end-to-end checks completed");System.out.println("TT IMBUED WATER WORLD AUDIT: ALL PASS (real player ticks,no-dose protection,effect/block synchronization,actual network conversion burst)");});
 }
}
