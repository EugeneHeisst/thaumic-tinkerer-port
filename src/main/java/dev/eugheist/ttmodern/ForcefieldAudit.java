package dev.eugheist.ttmodern;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.AABB;
final class ForcefieldAudit {
 private static void check(boolean ok,String label){if(!ok)throw new IllegalStateException("TT forcefield: "+label);}
 static void run(ServerLevel level){
  var player=net.neoforged.neoforge.common.util.FakePlayerFactory.getMinecraft(level);player.removeAllEffects();player.setPos(24.25,280.25,24.25);var target=EntityType.COW.create(level);target.setPos(30.25,280.25,24.25);var stone=new BlockPos(27,282,24);var positions=new java.util.ArrayList<BlockPos>();
  for(int y=280;y<=284;y++)for(int z=22;z<=26;z++)positions.add(new BlockPos(27,y,z));
  try{
   for(var pos:positions)level.setBlockAndUpdate(pos,Blocks.AIR.defaultBlockState());level.setBlockAndUpdate(stone,Blocks.STONE.defaultBlockState());check(ImbuedPotionEffects.earth(player,target)==0,"no effect no wall");player.addEffect(new net.minecraft.world.effect.MobEffectInstance(InfusedPotions.EARTH,3600));int placed=ImbuedPotionEffects.earth(player,target);check(placed==24,"five by five X plane occupied block excluded: placed="+placed+", effect="+player.hasEffect(InfusedPotions.EARTH)+", stone="+level.getBlockState(stone));check(level.getBlockState(stone).is(Blocks.STONE),"solid block preserved");
   var pos=new BlockPos(27,280,22);var state=level.getBlockState(pos);check(!state.isAir()&&state.getRenderShape()==RenderShape.INVISIBLE&&!state.getCollisionShape(level,pos).isEmpty(),"modern stored block/invisible/full collision");check(!level.noCollision(player,new AABB(pos).deflate(.1)),"native collision engine blocks entities");check(Block.getDrops(state,level,pos,level.getBlockEntity(pos)).isEmpty()&&state.getBlock().asItem()==net.minecraft.world.item.Items.AIR,"no drops or block item");
   var tile=(ForcefieldEntity)level.getBlockEntity(pos);for(int tick=0;tick<61;tick++)ForcefieldEntity.tick(level,pos,tile);check(level.getBlockState(pos).is(ThaumicTinkererModern.FORCEFIELD.get()),"still exists after sixty-one ticks");ForcefieldEntity.tick(level,pos,tile);check(!level.getBlockState(pos).is(ThaumicTinkererModern.FORCEFIELD.get()),"expires on sixty-second tick");
   for(var p:positions)level.removeBlock(p,false);positions.clear();player.setPos(-24.25,280.25,-24.25);target.setPos(-24.25,280.25,-30.25);for(int y=280;y<=284;y++)for(int x=-26;x<=-22;x++)positions.add(new BlockPos(x,y,-27));for(var p:positions)level.setBlockAndUpdate(p,Blocks.AIR.defaultBlockState());check(ImbuedPotionEffects.earth(player,target)==25,"Z plane and negative midpoint truncation");for(var p:positions)check(level.getBlockState(p).is(ThaumicTinkererModern.FORCEFIELD.get()),"exact original Z wall coordinates");
   for(var p:positions)level.removeBlock(p,false);positions.clear();player.setPos(100.25,304.25,100.25);target.setPos(106.25,304.25,100.25);var emptyChunk=level.getChunk(6,6);check(emptyChunk.getSection(emptyChunk.getSectionIndex(306)).hasOnlyAir(),"fixture is truly empty chunk section");for(int yy=304;yy<=308;yy++)for(int zz=98;zz<=102;zz++)positions.add(new BlockPos(103,yy,zz));check(ImbuedPotionEffects.earth(player,target)==25,"stores all field blocks in initially empty section");check(!level.noCollision(player,new AABB(new BlockPos(103,306,100)).deflate(.1)),"empty section field has native collision");
   System.out.println("TT FORCEFIELD AUDIT: ALL PASS (5x5 X/Z planes,negative midpoint,occupied protection,stored/invisible/native collision,no drops/item,strict62tick expiry)");
  }finally{for(var pos:positions)level.setBlockAndUpdate(pos,Blocks.AIR.defaultBlockState());level.removeBlock(stone,false);player.removeAllEffects();player.setPos(0,200,0);target.discard();}
 }
}
