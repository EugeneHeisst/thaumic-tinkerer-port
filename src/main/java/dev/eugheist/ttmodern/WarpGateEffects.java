package dev.eugheist.ttmodern;
/** Original proxy calls consume world RNG even when the dedicated proxy has no renderer. */
final class WarpGateEffects {
 static void sparkle(net.minecraft.server.level.ServerPlayer player){
  var points=new java.util.ArrayList<net.minecraft.world.phys.Vec3>(20);var random=player.level().random;
  for(int i=0;i<20;i++)points.add(new net.minecraft.world.phys.Vec3((float)player.getX()+random.nextFloat()-.5f,(float)player.getY()+random.nextFloat(),(float)player.getZ()+random.nextFloat()-.5f));
  if(net.neoforged.fml.loading.FMLEnvironment.dist.isClient())WarpGateFx.emit(player.level().dimension(),points);
 }
}
