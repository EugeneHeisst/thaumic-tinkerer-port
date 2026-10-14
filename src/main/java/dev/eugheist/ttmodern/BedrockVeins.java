package dev.eugheist.ttmodern;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
/** Original 1.7.10 WorldGenMinable ellipsoids (including size-zero RNG consumption). */
final class BedrockVeins {
 static void generate(LevelAccessor world,Random random,int x,int y,int z,int size,BlockState ore){
  float angle=random.nextFloat()*(float)Math.PI;
  double x0=(float)(x+8)+Mth.sin(angle)*size/8f,x1=(float)(x+8)-Mth.sin(angle)*size/8f;
  double z0=(float)(z+8)+Mth.cos(angle)*size/8f,z1=(float)(z+8)-Mth.cos(angle)*size/8f;
  double y0=y+random.nextInt(3)-2,y1=y+random.nextInt(3)-2;
  for(int i=0;i<=size;i++){
   double cx=x0+(x1-x0)*i/size,cy=y0+(y1-y0)*i/size,cz=z0+(z1-z0)*i/size;
   double radius=random.nextDouble()*size/16d;
   double width=(Mth.sin(i*(float)Math.PI/size)+1f)*radius+1d,height=(Mth.sin(i*(float)Math.PI/size)+1f)*radius+1d;
   for(int bx=Mth.floor(cx-width/2);bx<=Mth.floor(cx+width/2);bx++){
    double dx=(bx+.5-cx)/(width/2);if(!(dx*dx<1))continue;
    for(int by=Mth.floor(cy-height/2);by<=Mth.floor(cy+height/2);by++){
     double dy=(by+.5-cy)/(height/2);if(!(dx*dx+dy*dy<1))continue;
     for(int bz=Mth.floor(cz-width/2);bz<=Mth.floor(cz+width/2);bz++){
      double dz=(bz+.5-cz)/(width/2);if(!(dx*dx+dy*dy+dz*dz<1))continue;
      var pos=new BlockPos(bx,by,bz);if(world.getBlockState(pos).is(Blocks.BEDROCK))world.setBlock(pos,ore,2);
     }
    }
   }
  }
 }
}
