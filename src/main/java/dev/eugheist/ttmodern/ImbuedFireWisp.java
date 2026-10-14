package dev.eugheist.ttmodern;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.particle.*;
/** The original FXWisp type4, scale .1, shrink=true, noclip=true, gravity=1. */
public final class ImbuedFireWisp extends Particle {
 private final float size;
 static final ParticleRenderType ADDITIVE=new ParticleRenderType(){public com.mojang.blaze3d.vertex.BufferBuilder begin(com.mojang.blaze3d.vertex.Tesselator t,net.minecraft.client.renderer.texture.TextureManager textures){com.mojang.blaze3d.systems.RenderSystem.setShader(net.minecraft.client.renderer.GameRenderer::getParticleShader);com.mojang.blaze3d.systems.RenderSystem.setShaderTexture(0,net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("tc4modern","textures/misc/particles.png"));com.mojang.blaze3d.systems.RenderSystem.enableBlend();com.mojang.blaze3d.systems.RenderSystem.blendFunc(770,1);com.mojang.blaze3d.systems.RenderSystem.depthMask(false);return t.begin(com.mojang.blaze3d.vertex.VertexFormat.Mode.QUADS,com.mojang.blaze3d.vertex.DefaultVertexFormat.PARTICLE);}};

 public ImbuedFireWisp(net.minecraft.client.multiplayer.ClientLevel level,double x,double y,double z){
  super(level,x,y,z);xd=yd=zd=0;hasPhysics=false;size=(random.nextFloat()*.5f+.5f)*2*.1f;lifetime=(int)(36/(Math.random()*.3+.7));rCol=.7f+level.random.nextFloat()*.3f;gCol=bCol=.2f;setSize(.1f,.1f);
  var mc=net.minecraft.client.Minecraft.getInstance();var eye=mc.getCameraEntity();if(eye!=null&&eye.distanceToSqr(x,y,z)>Math.pow(mc.options.graphicsMode().get()==net.minecraft.client.GraphicsStatus.FAST?25:50,2))lifetime=0;
 }
 @Override public ParticleRenderType getRenderType(){return ADDITIVE;}
 @Override public void tick(){xo=x;yo=y;zo=z;if(age++>=lifetime)remove();yd-=.04;move(xd,yd,zd);xd*=.98f;yd*=.98f;zd*=.98f;}
 @Override public void render(VertexConsumer out,net.minecraft.client.Camera camera,float partial){float radius=size*(lifetime-age)/(float)Math.max(1,lifetime)*.5f;var eye=camera.getPosition();float cx=(float)(xo+(x-xo)*partial-eye.x),cy=(float)(yo+(y-yo)*partial-eye.y),cz=(float)(zo+(z-zo)*partial-eye.z);float[][] corners={{-1,-1,.125f,1},{1,-1,0,1},{1,1,0,.875f},{-1,1,.125f,.875f}};for(var c:corners){var o=new org.joml.Vector3f(c[0]*radius,c[1]*radius,0).rotate(camera.rotation());out.addVertex(cx+o.x,cy+o.y,cz+o.z).setUv(c[2],c[3]).setColor(rCol,gCol,bCol,.5f).setLight(240);}}
 public static void spawn(double x,double y,double z){var mc=net.minecraft.client.Minecraft.getInstance();if(mc.level!=null)mc.particleEngine.add(new ImbuedFireWisp(mc.level,x,y,z));}
 static void audit(net.minecraft.client.Minecraft mc){
  var before=java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<Particle,Boolean>());mc.particleEngine.tick();mc.particleEngine.iterateParticles(before::add);var pos=mc.player.position();dev.eugheist.ttmodern.mixin.InfusionFxInvoker.handle(new dev.eugheist.tc4modern.InfusionContent.FxPayload(171,pos.x,pos.y+1,pos.z,0,0,0,0));mc.particleEngine.tick();var added=new java.util.ArrayList<ImbuedFireWisp>();mc.particleEngine.iterateParticles(p->{if(!before.contains(p)&&p instanceof ImbuedFireWisp w)added.add(w);});if(added.size()!=1)throw new IllegalStateException("TT fire wisp packet did not create exactly one native particle");var w=added.getFirst();if(w.lifetime<36||w.lifetime>51||w.hasPhysics||w.rCol<.7||w.rCol>1||w.gCol!=.2f||w.bCol!=.2f||w.size<.1f||w.size>.2f)throw new IllegalStateException("TT fire wisp original properties");double y=w.y,velocity=w.yd;w.tick();if(Math.abs((w.y-y)-(velocity-.04))>1e-7||Math.abs(w.yd-(velocity-.04)*.98f)>1e-7)throw new IllegalStateException("TT fire wisp gravity/friction");for(int t=0;t<60;t++)w.tick();if(w.isAlive())throw new IllegalStateException("TT fire wisp lifetime");System.out.println("TT IMBUED FIRE CLIENT AUDIT: ALL PASS (native packet particle,count,color/scale,lifetime,gravity/friction,noclip)");
 }
}
