package dev.eugheist.ttmodern;
/** Original ClientProxy-only sparkles: local integrated world only, never broadcast on dedicated servers. */
final class ShadowbeamFx {
 static int emitted;
 static void emit(net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level> dimension,java.util.List<net.minecraft.world.phys.Vec3> points){
  var mc=net.minecraft.client.Minecraft.getInstance();mc.execute(()->{if(mc.getSingleplayerServer()==null||mc.level==null||mc.level.dimension()!=dimension)return;for(var p:points){mc.particleEngine.add(new Sparkle(mc.level,p));emitted++;}});
 }
 static void audit(net.minecraft.client.multiplayer.ClientLevel level){Sparkle.audit(level);}
 static final class Sparkle extends SummonFx.Homing {
 static void audit(net.minecraft.client.multiplayer.ClientLevel level){
  var p=new Sparkle(level,new net.minecraft.world.phys.Vec3(1,2,3));
  if(p.hasPhysics||p.lifetime!=5||p.quadSize<.4f||p.quadSize>.8f)throw new IllegalStateException("original shadow sparkle properties");
  p.tick();if(Math.abs(p.y-(2+.04*(double).7f))>1e-12||p.x!=1||p.z!=3)throw new IllegalStateException("original shadow sparkle gravity/no drag");
  for(int i=0;i<4;i++)p.tick();if(!p.isAlive())throw new IllegalStateException("original sparkle remains alive through fifth update");p.tick();if(p.isAlive())throw new IllegalStateException("original sparkle expires on sixth update");
 }
  Sparkle(net.minecraft.client.multiplayer.ClientLevel level,net.minecraft.world.phys.Vec3 p){super(level,p,p);xd=yd=zd=0;quadSize*=4;rCol=gCol=bCol=.001f;lifetime=5;hasPhysics=false;setSize(.01f,.01f);}
  @Override public void tick(){xo=x;yo=y;zo=z;if(age++>=lifetime)remove();yd-=.04*(double)-.7f;x+=xd;y+=yd;z+=zd;}
  @Override public net.minecraft.client.particle.ParticleRenderType getRenderType(){return SummonFx.TRANSLUCENT;}
  @Override public void render(com.mojang.blaze3d.vertex.VertexConsumer out,net.minecraft.client.Camera camera,float partial){float u=((1+age)%4)/16f;quad(out,camera,partial,quadSize,u,u+.0624375f,.25f,.3124375f,1);}
 }
}
