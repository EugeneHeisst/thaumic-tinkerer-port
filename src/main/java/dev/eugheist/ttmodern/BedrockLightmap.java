package dev.eugheist.ttmodern;
/** Original provider brightness table and MC1.7.10 lightmap arithmetic, including night vision. */
@net.neoforged.fml.common.EventBusSubscriber(modid="ttinkerer",value=net.neoforged.api.distmarker.Dist.CLIENT)
public final class BedrockLightmap {
 static float flicker,unused;
 @net.neoforged.bus.api.SubscribeEvent public static void tick(net.neoforged.neoforge.client.event.ClientTickEvent.Post event){flicker=(float)((double)flicker+(Math.random()-Math.random())*Math.random()*Math.random());unused=(float)((double)unused+(Math.random()-Math.random())*Math.random()*Math.random());flicker=(float)((double)flicker*.9);unused=(float)((double)unused*.9);}
 static float brightness(int light){float f=12,f1=12-light/15f;return (1-f1)/(f1*3+1)*(1-f)+f;}
 public static int color(int block,int sky,float sunlight,float torch,float darken,float nightVision,float gamma,boolean lightning){
  float s=brightness(sky)*(sunlight*.95f+.05f);if(lightning)s=brightness(sky);float t=brightness(block)*(torch*.1f+1.5f);
  float r=s*(sunlight*.65f+.35f)+t,g=s*(sunlight*.65f+.35f)+t*((t*.6f+.4f)*.6f+.4f),b=s+t*(t*t*.6f+.4f);
  r=r*.96f+.03f;g=g*.96f+.03f;b=b*.96f+.03f;
  if(darken>0){r=r*(1-darken)+r*.7f*darken;g=g*(1-darken)+g*.6f*darken;b=b*(1-darken)+b*.6f*darken;}
  if(nightVision>0){float scale=1/r;if(scale>1/g)scale=1/g;if(scale>1/b)scale=1/b;r=r*(1-nightVision)+r*scale*nightVision;g=g*(1-nightVision)+g*scale*nightVision;b=b*(1-nightVision)+b*scale*nightVision;}
  r=Math.min(1,r);g=Math.min(1,g);b=Math.min(1,b);
  float ir=1-r,ig=1-g,ib=1-b;ir=1-ir*ir*ir*ir;ig=1-ig*ig*ig*ig;ib=1-ib*ib*ib*ib;
  r=r*(1-gamma)+ir*gamma;g=g*(1-gamma)+ig*gamma;b=b*(1-gamma)+ib*gamma;
  int red=(int)(net.minecraft.util.Mth.clamp(r*.96f+.03f,0,1)*255),green=(int)(net.minecraft.util.Mth.clamp(g*.96f+.03f,0,1)*255),blue=(int)(net.minecraft.util.Mth.clamp(b*.96f+.03f,0,1)*255);
  return 0xFF000000|blue<<16|green<<8|red;
 }
 public static int nativeColor(net.minecraft.client.Minecraft mc,int block,int sky,float partial){float vision=mc.player.hasEffect(net.minecraft.world.effect.MobEffects.NIGHT_VISION)?net.minecraft.client.renderer.GameRenderer.getNightVisionScale(mc.player,partial):0;return color(block,sky,mc.level.getSkyDarken(1),flicker,mc.gameRenderer.getDarkenWorldAmount(partial),vision,mc.options.gamma().get().floatValue(),mc.level.getSkyFlashTime()>0);}
}
