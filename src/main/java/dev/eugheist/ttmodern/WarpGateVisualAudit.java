package dev.eugheist.ttmodern;

/** Checks the original twelve cubes and five alpha trails through the native vertex API. */
final class WarpGateVisualAudit {
 static void run(net.minecraft.client.Minecraft mc) {
  var type=com.mojang.blaze3d.vertex.VertexConsumer.class;
  int[] counts=new int[3];
  var sink=(com.mojang.blaze3d.vertex.VertexConsumer)java.lang.reflect.Proxy.newProxyInstance(type.getClassLoader(),new Class<?>[]{type},(proxy,method,args)->{
   if(method.getName().equals("addVertex") && args.length==3){counts[0]++;for(Object value:args)if(!Float.isFinite(((Number)value).floatValue()))throw new IllegalStateException("nonfinite warp cube");}
   if(method.getName().equals("setColor") && args[0] instanceof Float){float alpha=((Number)args[3]).floatValue();if(alpha==1)counts[1]++;else if(alpha==.2f)counts[2]++;else throw new IllegalStateException("warp trail alpha "+alpha);}
   if(method.isDefault())return java.lang.reflect.InvocationHandler.invokeDefault(proxy,method,args);
   if(method.getReturnType()==type)return proxy;return null;
  });
  var pose=new com.mojang.blaze3d.vertex.PoseStack();var before=new org.joml.Matrix4f(pose.last().pose());
  WarpGateRenderer.draw(pose,renderType->{if(renderType.mode()!=com.mojang.blaze3d.vertex.VertexFormat.Mode.QUADS)throw new IllegalStateException("warp cube render topology");return sink;},137);
  if(counts[0]!=1728||counts[1]!=288||counts[2]!=1440||!before.equals(pose.last().pose()))throw new IllegalStateException("warp cube count/alpha/pose "+java.util.Arrays.toString(counts));
  var state=WarpGateBlock.BLOCK.get().defaultBlockState();var model=mc.getBlockRenderer().getBlockModel(state);
  var overlay=model.getQuads(state,null,net.minecraft.util.RandomSource.create(0));
  if(overlay.size()!=1||!overlay.getFirst().getSprite().contents().name().getPath().equals("blocks/warpgate2")||overlay.getFirst().isShade())throw new IllegalStateException("warp fullbright top overlay");
  var item=mc.getItemRenderer().getModel(new net.minecraft.world.item.ItemStack(WarpGateBlock.ITEM.get()),null,null,0);
  int quads=item.getQuads(null,null,net.minecraft.util.RandomSource.create(0)).size();for(var side:net.minecraft.core.Direction.values())quads+=item.getQuads(null,side,net.minecraft.util.RandomSource.create(0)).size();
  if(quads!=1)throw new IllegalStateException("original warp inventory top plane: "+quads);
  WarpGateRenderer.draw(new com.mojang.blaze3d.vertex.PoseStack(),mc.renderBuffers().bufferSource(),137);mc.renderBuffers().bufferSource().endBatch();
  System.out.println("TT WARP GATE VISUAL CLIENT AUDIT: ALL PASS (72 cubes,quad topology,original alpha trails,pose restoration,top overlay,inventory plane,native draw)");
 }
}
