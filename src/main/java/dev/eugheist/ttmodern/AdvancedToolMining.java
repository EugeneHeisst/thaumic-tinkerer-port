package dev.eugheist.ttmodern;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
/** Original ToolHandler extra-block harvest, delegated to native permission/drop hooks. */
public final class AdvancedToolMining {
 private AdvancedToolMining(){}
 private static final java.util.Set<ServerPlayer> BREAKING=java.util.Collections.newSetFromMap(new java.util.WeakHashMap<>());
 /** Legacy material.isToolNotRequired plus each tool's material family. */
 public static boolean material(BlockState state,String type){if(!state.requiresCorrectToolForDrops())return true;return switch(type){case "pick"->state.is(net.minecraft.tags.BlockTags.MINEABLE_WITH_PICKAXE);case "shovel"->state.is(net.minecraft.tags.BlockTags.MINEABLE_WITH_SHOVEL);case "axe"->state.is(net.minecraft.tags.BlockTags.MINEABLE_WITH_AXE);default->false;};}
 public static boolean breaking(ServerPlayer player){return BREAKING.contains(player);}
 public static void harvest(ServerPlayer player,BlockPos origin,AdvancedToolModes.Bounds bounds,java.util.function.Predicate<BlockState> material){
  if(!BREAKING.add(player))return;
  var level=player.serverLevel();
  try{var reference=level.getBlockState(origin);float referenceStrength=reference.getDestroyProgress(player,level,origin);
   for(var pos:bounds.positions(origin)){
    if(!level.hasChunkAt(pos))continue;var state=level.getBlockState(pos);if(state.isAir()||!material.test(state)||!state.canHarvestBlock(level,pos,player))continue;
    float strength=state.getDestroyProgress(player,level,pos);if(referenceStrength/strength>10)continue;
    player.gameMode.destroyBlock(pos);
   }
   var box=new net.minecraft.world.phys.AABB(origin.getX()+bounds.xs(),origin.getY()+bounds.ys(),origin.getZ()+bounds.zs(),origin.getX()+bounds.xe(),origin.getY()+bounds.ye(),origin.getZ()+bounds.ze());
   for(var item:level.getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class,box))item.setPos(player.getX(),player.getY()+1,player.getZ());
  }finally{BREAKING.remove(player);}
 }
 static BlockPos furthest(net.minecraft.world.level.Level level,BlockPos origin,net.minecraft.world.level.block.Block block){var last=origin;double distance=0;search:while(true){for(int x=-2;x<=2;x++)for(int y=2;y>=-2;y--)for(int z=-2;z<=2;z++){var next=last.offset(x,y,z);int dx=next.getX()-origin.getX(),dy=next.getY()-origin.getY(),dz=next.getZ()-origin.getZ();if(Math.abs(dx)>24||Math.abs(dy)>48||Math.abs(dz)>24)return last;if(!level.hasChunkAt(next))continue;var state=level.getBlockState(next);double d=(double)dx*dx+(double)dy*dy+(double)dz*dz;if(state.is(block)&&state.is(net.minecraft.tags.BlockTags.LOGS)&&state.getDestroySpeed(level,next)>=0&&d>distance){distance=d;last=next;continue search;}}return last;}}
 public static void fell(ServerPlayer player,BlockPos origin){if(!BREAKING.add(player))return;var level=player.serverLevel();try{if(!level.getBlockState(origin).is(net.minecraft.tags.BlockTags.LOGS))return;while(!level.getBlockState(origin).isAir()){var block=level.getBlockState(origin).getBlock();var target=furthest(level,origin,block);var before=level.getBlockState(target);if(!player.gameMode.destroyBlock(target)||level.getBlockState(target).equals(before))break;for(int x=-3;x<=3;x++)for(int y=-3;y<=3;y++)for(int z=-3;z<=3;z++){var at=target.offset(x,y,z);if(level.hasChunkAt(at))level.scheduleTick(at,level.getBlockState(at).getBlock(),150+level.random.nextInt(150));}}for(var item:level.getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class,new net.minecraft.world.phys.AABB(origin.getX()-5,origin.getY()-1,origin.getZ()-5,origin.getX()+5,origin.getY()+64,origin.getZ()+5))){item.setPos(net.minecraft.world.phys.Vec3.atCenterOf(origin));item.tickCount+=20;}}finally{BREAKING.remove(player);}}

}
