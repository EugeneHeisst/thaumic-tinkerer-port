package dev.eugheist.ttmodern;
/** Build164 uses the local Thaumcraft client proxy, not a server particle broadcast. */
final class WarpGateFx {
 static int attempted,emitted;
 static int threshold(net.minecraft.client.ParticleStatus setting){return switch(setting){case ALL->4;case DECREASED->2;case MINIMAL->0;};}
 static void emit(net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level> dimension,java.util.List<net.minecraft.world.phys.Vec3> points){var mc=net.minecraft.client.Minecraft.getInstance();mc.execute(()->{if(mc.getSingleplayerServer()==null||mc.level==null||mc.level.dimension()!=dimension)return;for(var point:points){attempted++;if(mc.level.random.nextInt(6)<threshold(mc.options.particles().get())){mc.particleEngine.add(new Sparkle(mc.level,point));emitted++;}}});}
 static final class Sparkle extends SummonFx.Homing {
  Sparkle(net.minecraft.client.multiplayer.ClientLevel level,net.minecraft.world.phys.Vec3 point){super(level,point,point);xd=yd=zd=0;quadSize*=1.5f;rCol=.8f+level.random.nextFloat()*.2f;gCol=.8f+level.random.nextFloat()*.2f;bCol=.8f+level.random.nextFloat()*.2f;lifetime=18;hasPhysics=false;setSize(.01f,.01f);}
  @Override public void tick(){xo=x;yo=y;zo=z;if(age++>=lifetime)remove();}
  @Override public net.minecraft.client.particle.ParticleRenderType getRenderType(){return ADDITIVE;}
  @Override public void render(com.mojang.blaze3d.vertex.VertexConsumer out,net.minecraft.client.Camera camera,float partial){float u=((16+age/6)%4)/16f;quad(out,camera,partial,quadSize*(lifetime-age+1)/(float)lifetime,u,u+.0624375f,.25f,.3124375f,1);}
  static void audit(net.minecraft.client.multiplayer.ClientLevel level){var p=new Sparkle(level,new net.minecraft.world.phys.Vec3(1,2,3));if(p.hasPhysics||p.lifetime!=18||p.quadSize<.15f||p.quadSize>.3f||p.rCol<.8f||p.rCol>1||p.gCol<.8f||p.bCol<.8f)throw new IllegalStateException("original warp sparkle properties");for(int i=0;i<18;i++)p.tick();if(!p.isAlive()||p.x!=1||p.y!=2||p.z!=3)throw new IllegalStateException("warp sparkle static lifetime");p.tick();if(p.isAlive())throw new IllegalStateException("warp sparkle expiry");if(threshold(net.minecraft.client.ParticleStatus.ALL)!=4||threshold(net.minecraft.client.ParticleStatus.DECREASED)!=2||threshold(net.minecraft.client.ParticleStatus.MINIMAL)!=0)throw new IllegalStateException("warp particle setting probability");}
 }
 static final net.minecraft.client.particle.ParticleRenderType ADDITIVE=new net.minecraft.client.particle.ParticleRenderType(){
  @Override public com.mojang.blaze3d.vertex.BufferBuilder begin(com.mojang.blaze3d.vertex.Tesselator tessellator,net.minecraft.client.renderer.texture.TextureManager textures){com.mojang.blaze3d.systems.RenderSystem.setShader(net.minecraft.client.renderer.GameRenderer::getParticleShader);com.mojang.blaze3d.systems.RenderSystem.setShaderTexture(0,net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("tc4modern","textures/misc/particles.png"));com.mojang.blaze3d.systems.RenderSystem.enableBlend();com.mojang.blaze3d.systems.RenderSystem.blendFunc(770,1);com.mojang.blaze3d.systems.RenderSystem.depthMask(false);return tessellator.begin(com.mojang.blaze3d.vertex.VertexFormat.Mode.QUADS,com.mojang.blaze3d.vertex.DefaultVertexFormat.PARTICLE);}
 };
}
