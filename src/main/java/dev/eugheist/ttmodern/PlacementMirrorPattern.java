package dev.eugheist.ttmodern;
import net.minecraft.core.*;
/** Original mirror builds upward on walls; looking almost straight down changes it to a floor square. */
final class PlacementMirrorPattern {
 static java.util.List<BlockPos> positions(BlockPos hit,Direction side,int size,float yaw,float pitch){
  int range=((size|1)^1)/2,rotation=net.minecraft.util.Mth.floor(yaw*4f/360f+.5)&3;
  boolean horizontal=side.getAxis()==Direction.Axis.Y,down=pitch>75f;
  int x=side.getAxis()==Direction.Axis.X?0:horizontal?(down||(rotation&1)==0?range:0):range;
  int y=horizontal?(down?0:range):range;
  int z=side.getAxis()==Direction.Axis.Z?0:horizontal?(down||(rotation&1)==1?range:0):range;
  var result=new java.util.ArrayList<BlockPos>();for(int dx=-x;dx<=x;dx++)for(int dy=0;dy<=2*y;dy++)for(int dz=-z;dz<=z;dz++)result.add(hit.offset(dx,dy,dz).relative(side));return result;
 }
}
