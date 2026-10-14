package dev.eugheist.ttmodern;
import java.util.function.Consumer;
import net.minecraft.core.*;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.phys.Vec3;
/** Original smoke checks vanilla flammability, not elemental transformation targets. */
final class InfusedFireSmoke {
 static void emit(Level level,BlockPos pos,RandomSource random,Consumer<Vec3> output){
  var below=pos.below();var vanilla=(FireBlock)Blocks.FIRE;
  if(level.hasChunkAt(below)&&(level.getBlockState(below).isFaceSturdy(level,below,Direction.UP)||vanilla.canCatchFire(level,below,Direction.UP))){
   for(int i=0;i<3;i++)output.accept(new Vec3(pos.getX()+random.nextFloat(),pos.getY()+random.nextFloat()*.5f+.5f,pos.getZ()+random.nextFloat()));return;
  }
  for(var direction:new Direction[]{Direction.WEST,Direction.EAST,Direction.NORTH,Direction.SOUTH,Direction.UP}){
   var neighbor=pos.relative(direction);if(!level.hasChunkAt(neighbor)||!vanilla.canCatchFire(level,neighbor,direction.getOpposite()))continue;
   for(int i=0;i<2;i++){
    float rx=random.nextFloat(),ry=random.nextFloat(),rz=random.nextFloat();float x=pos.getX()+rx,y=pos.getY()+ry,z=pos.getZ()+rz;
    switch(direction){case WEST->x=pos.getX()+rx*.1f;case EAST->x=pos.getX()+1-rx*.1f;case NORTH->z=pos.getZ()+rz*.1f;case SOUTH->z=pos.getZ()+1-rz*.1f;case UP->y=pos.getY()+1-ry*.1f;default->{}}
    output.accept(new Vec3(x,y,z));
   }
  }
 }
 private InfusedFireSmoke(){}
}
