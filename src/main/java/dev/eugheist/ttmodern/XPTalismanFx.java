package dev.eugheist.ttmodern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.world.phys.Vec3;
/** build164 bottle sparkles and ClientProxy.wispFX RGB constructor. */
public final class XPTalismanFx {
    public static void handle(dev.eugheist.tc4modern.InfusionContent.FxPayload payload){
        var mc=Minecraft.getInstance();if(mc.level==null)return;
        if(payload.kind()==172){
            for(int i=0;i<6;i++)dev.eugheist.tc4modern.FociFx.sparkle(payload.x0()+mc.level.random.nextDouble()-.5,payload.y0()+mc.level.random.nextDouble()-.5,payload.z0()+mc.level.random.nextDouble()-.5,3);
        }else if(payload.kind()==173&&Double.isFinite(payload.x1())&&payload.x1()>0){
            mc.particleEngine.add(new Wisp(mc.level,new Vec3(payload.x0(),payload.y0(),payload.z0()),(float)payload.x1()));
        }
    }
    static final class Wisp extends SummonFx.Homing {
        final float size;
        Wisp(net.minecraft.client.multiplayer.ClientLevel level,Vec3 pos,float scale){
            super(level,pos,pos);xd=yd=zd=0;size=(random.nextFloat()*.5f+.5f)*2*scale;
            lifetime=(int)(36/(random.nextDouble()*.3+.7));setColor(.1f,.9f,.1f);setSize(.1f,.1f);
            var mc=Minecraft.getInstance();var eye=mc.getCameraEntity();int range=mc.options.graphicsMode().get()==net.minecraft.client.GraphicsStatus.FAST?25:50;
            if(eye!=null&&eye.distanceToSqr(pos)>range*range)lifetime=0;
        }
        boolean originalParameters(){return hasPhysics&&lifetime>=36&&lifetime<=51&&size>=1.4f&&size<=2.8f&&rCol==.1f&&gCol==.9f&&bCol==.1f;}
        @Override public ParticleRenderType getRenderType(){return ImbuedFireWisp.ADDITIVE;}
        @Override public void tick(){
            xo=x;yo=y;zo=z;if(age++>=lifetime)remove();yd-=.04*(double).02f;
            pushOut(true);move(xd,yd,zd);xd*=.98f;yd*=.98f;zd*=.98f;
            if(onGround){xd*=.7f;zd*=.7f;}
        }
        @Override public void render(com.mojang.blaze3d.vertex.VertexConsumer out,net.minecraft.client.Camera camera,float partial){
            float pulse=age/(float)Math.max(1,lifetime/2);if(pulse>1)pulse=2-pulse;
            quad(out,camera,partial,size*pulse*.5f,0,.125f,.875f,1,.5f);
        }
    }
    static void audit(Minecraft mc){
        var pos=mc.player.position().add(0,1,0);
        var before=java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<net.minecraft.client.particle.Particle,Boolean>());
        mc.particleEngine.tick();mc.particleEngine.iterateParticles(before::add);
        dev.eugheist.ttmodern.mixin.InfusionFxInvoker.handle(new dev.eugheist.tc4modern.InfusionContent.FxPayload(173,pos.x,pos.y,pos.z,1.4,0,0,0));
        mc.particleEngine.tick();var added=new java.util.ArrayList<Wisp>();
        mc.particleEngine.iterateParticles(p->{if(!before.contains(p)&&p instanceof Wisp w)added.add(w);});
        if(added.size()!=1)throw new IllegalStateException("XP talisman packet particle count");
        var w=added.getFirst();if(!w.originalParameters())throw new IllegalStateException("XP talisman original RGB wisp parameters");
        for(int i=0;i<60;i++)w.tick();if(w.isAlive())throw new IllegalStateException("XP talisman wisp expiry");
        System.out.println("TT XP TALISMAN CLIENT FX AUDIT: ALL PASS (packet dispatch, exact count, denomination scale, RGB, collision, lifetime)");
    }
}
