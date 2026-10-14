package dev.eugheist.ttmodern;
import net.minecraft.world.phys.Vec3;
/** The original virtual throwable is advanced synchronously; world entity ticks never run it. */
final class ShadowbeamPath {
 static final int STEPS=300;
 record Start(Vec3 position,Vec3 velocity){}
 static Start start(double x,double y,double z,float eyeHeight,float yaw,float pitch){float yr=yaw/180f*(float)Math.PI,pr=pitch/180f*(float)Math.PI;double vx=-net.minecraft.util.Mth.sin(yr)*net.minecraft.util.Mth.cos(pr)*.4f,vy=-net.minecraft.util.Mth.sin(pr)*.4f,vz=net.minecraft.util.Mth.cos(yr)*net.minecraft.util.Mth.cos(pr)*.4f;float length=(float)Math.sqrt(vx*vx+vy*vy+vz*vz);double spread=(double).0075f;var velocity=new Vec3((vx/length+spread)*1.5f/10d,(vy/length+spread)*1.5f/10d,(vz/length+spread)*1.5f/10d);return new Start(new Vec3(x-(double)(net.minecraft.util.Mth.cos(yr)*.16f),y+(double)eyeHeight-(double).1f,z-(double)(net.minecraft.util.Mth.sin(yr)*.16f)),velocity);}
 static Vec3 reflect(Vec3 velocity,net.minecraft.core.Direction side){var n=new Vec3(side.getStepX(),side.getStepY(),side.getStepZ());return n.scale(-2*velocity.dot(n)).add(velocity);}
 /** Legacy AABB intercept tests all six planes, including exits from inside a target.
  * Modern AABB.clip tests entry planes only and has a different parallel tolerance. */
 static java.util.Optional<Vec3> intercept(net.minecraft.world.phys.AABB box,Vec3 from,Vec3 to){
  double[] low={box.minX,box.minY,box.minZ},high={box.maxX,box.maxY,box.maxZ};
  double[] start={from.x,from.y,from.z},delta={to.x-from.x,to.y-from.y,to.z-from.z};
  Vec3 nearest=null;double distance=Double.POSITIVE_INFINITY;
  for(int axis=0;axis<3;axis++){
   if(delta[axis]*delta[axis]<(double)1.0E-7f)continue;
   for(double plane:new double[]{low[axis],high[axis]}){
    double fraction=(plane-start[axis])/delta[axis];if(fraction<0||fraction>1)continue;
    var point=new Vec3(start[0]+delta[0]*fraction,start[1]+delta[1]*fraction,start[2]+delta[2]*fraction);
    double[] coords={point.x,point.y,point.z};boolean inside=true;
    for(int other=0;other<3;other++)if(other!=axis&&(coords[other]<low[other]||coords[other]>high[other]))inside=false;
    double candidate=from.distanceToSqr(point);if(inside&&candidate<distance){nearest=point;distance=candidate;}
   }
  }
  return java.util.Optional.ofNullable(nearest);
 }
 static boolean sparkle(int iteration){return iteration>2;}
}
