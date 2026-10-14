package dev.eugheist.ttmodern;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.*;
final class ImbuedWaterAudit {
 private static void check(boolean ok,String label){if(!ok)throw new IllegalStateException("TT imbued water: "+label);}
 static void run(ServerLevel level){
  var player=net.neoforged.neoforge.common.util.FakePlayerFactory.getMinecraft(level);player.removeAllEffects();var positions=new java.util.ArrayList<BlockPos>();
  try{
   player.setPos(24.25,280.25,24.25);var source=new BlockPos(22,278,22);var flow=new BlockPos(26,282,26);var outside=new BlockPos(27,280,24);var water=new BlockPos(24,280,24);positions.addAll(java.util.List.of(source,flow,outside,water));
   level.setBlockAndUpdate(source,Blocks.LAVA.defaultBlockState());level.setBlockAndUpdate(flow,Blocks.LAVA.defaultBlockState().setValue(LiquidBlock.LEVEL,3));level.setBlockAndUpdate(outside,Blocks.LAVA.defaultBlockState());level.setBlockAndUpdate(water,Blocks.WATER.defaultBlockState());
   check(ImbuedPotionEffects.water(player)==0&&level.getBlockState(source).is(Blocks.LAVA),"no effect no conversion");player.addEffect(new net.minecraft.world.effect.MobEffectInstance(InfusedPotions.WATER,3600));
   check(ImbuedPotionEffects.water(player)==2&&level.getBlockState(source).is(Blocks.OBSIDIAN)&&level.getBlockState(flow).is(Blocks.OBSIDIAN),"source and flowing lava original positive bounds");check(level.getBlockState(outside).is(Blocks.LAVA)&&level.getBlockState(water).is(Blocks.WATER),"outside radius and water untouched");check(ImbuedPotionEffects.water(player)==0,"repeat tick idempotent");
   player.setPos(-24.25,280.25,-24.25);var inside=new BlockPos(-26,280,-26);var excluded=new BlockPos(-27,280,-25);positions.add(inside);positions.add(excluded);level.setBlockAndUpdate(inside,Blocks.LAVA.defaultBlockState());level.setBlockAndUpdate(excluded,Blocks.LAVA.defaultBlockState());
   check(ImbuedPotionEffects.water(player)==1&&level.getBlockState(inside).is(Blocks.OBSIDIAN)&&level.getBlockState(excluded).is(Blocks.LAVA),"negative truncation preserved rather than floor expansion");
   System.out.println("TT IMBUED WATER AUDIT: ALL PASS (native source/flowing lava,original positive/negative bounds,no effect/water/outside protection,idempotence)");
  }finally{for(var pos:positions)level.setBlockAndUpdate(pos,Blocks.AIR.defaultBlockState());player.removeAllEffects();player.setPos(0,200,0);}
 }
}
