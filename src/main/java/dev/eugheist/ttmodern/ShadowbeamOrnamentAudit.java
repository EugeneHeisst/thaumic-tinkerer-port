package dev.eugheist.ttmodern;
final class ShadowbeamOrnamentAudit {
 static void run(net.minecraft.client.Minecraft mc){
  if(mc.getModelManager().getModel(ShadowbeamOrnament.MODEL)==mc.getModelManager().getMissingModel())throw new IllegalStateException("missing shadowbeam ornament model");
  var vertices=new java.util.concurrent.atomic.AtomicInteger();var type=com.mojang.blaze3d.vertex.VertexConsumer.class;
  var sink=(com.mojang.blaze3d.vertex.VertexConsumer)java.lang.reflect.Proxy.newProxyInstance(type.getClassLoader(),new Class<?>[]{type},(proxy,method,args)->{if(method.getName().equals("addVertex"))vertices.incrementAndGet();if(method.isDefault())return java.lang.reflect.InvocationHandler.invokeDefault(proxy,method,args);if(method.getReturnType()==type)return proxy;return null;});
  var pose=new com.mojang.blaze3d.vertex.PoseStack();var original=new org.joml.Matrix4f(pose.last().pose());
  ShadowbeamOrnament.draw(pose,renderType->sink,15728880,net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY);
  if(vertices.get()<48||!original.equals(pose.last().pose()))throw new IllegalStateException("shadowbeam crossed extrusion vertices/pose restore: "+vertices);
  System.out.println("TT SHADOWBEAM ORNAMENT CLIENT AUDIT: ALL PASS (native additional baked model,crossed extruded sprites,vertices,pose restoration)");
 }
}
