package dev.eugheist.ttmodern;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
final class InfusedFireSmokeAudit {
 private static void check(boolean ok,String label){if(!ok)throw new IllegalStateException("TT fire smoke: "+label);}
 static void run(ServerLevel level){var pos=new BlockPos(6,280,6);try{
  for(var d:new Direction[]{Direction.WEST,Direction.EAST,Direction.NORTH,Direction.SOUTH,Direction.UP}){
   clear(level,pos);level.setBlockAndUpdate(pos.relative(d),Blocks.OAK_PLANKS.defaultBlockState());var points=new java.util.ArrayList<net.minecraft.world.phys.Vec3>();InfusedFireSmoke.emit(level,pos,net.minecraft.util.RandomSource.create(7),points::add);check(points.size()==2,"two wall particles "+d);
   for(var p:points){double edge=switch(d){case WEST->p.x-pos.getX();case EAST->pos.getX()+1-p.x;case NORTH->p.z-pos.getZ();case SOUTH->pos.getZ()+1-p.z;default->pos.getY()+1-p.y;};check(edge>=0&&edge<=.10001,"correct face band "+d);}
  }
  for(var block:new net.minecraft.world.level.block.Block[]{Blocks.STONE,Blocks.OAK_LEAVES}){clear(level,pos);level.setBlockAndUpdate(pos.below(),block.defaultBlockState());var points=new java.util.ArrayList<net.minecraft.world.phys.Vec3>();InfusedFireSmoke.emit(level,pos,net.minecraft.util.RandomSource.create(7),points::add);check(points.size()==3&&points.stream().allMatch(p->p.y>=pos.getY()+.5&&p.y<=pos.getY()+1),"three upper particles on solid/flammable floor");}
  clear(level,pos);level.setBlockAndUpdate(pos.west(),Blocks.DIRT.defaultBlockState());var points=new java.util.ArrayList<net.minecraft.world.phys.Vec3>();InfusedFireSmoke.emit(level,pos,net.minecraft.util.RandomSource.create(7),points::add);check(points.isEmpty(),"transmutation target alone does not emit vanilla smoke");
 }finally{clear(level,pos);}System.out.println("TT FIRE SMOKE AUDIT: ALL PASS (five wall directions,face bands,solid/flammable floor,transmutation versus flammability)");}
 private static void clear(ServerLevel level,BlockPos pos){level.removeBlock(pos,false);for(var d:Direction.values())level.removeBlock(pos.relative(d),false);}
}
