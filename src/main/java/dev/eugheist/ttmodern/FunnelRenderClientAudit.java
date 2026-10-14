package dev.eugheist.ttmodern;

final class FunnelRenderClientAudit {
    static void run(){
        var renderer=new FunnelRenderer(null);
        var tile=new FunnelEntity(net.minecraft.core.BlockPos.ZERO,ThaumicTinkererModern.FUNNEL.get().defaultBlockState());
        for(int variant=0;variant<4;variant++){
            var stack=variant==0?net.minecraft.world.item.ItemStack.EMPTY:FunnelWorldClientAudit.jar(variant);
            tile.inventory.setStackInSlot(0,stack);var before=stack.copy();
            var vertices=new java.util.ArrayList<org.joml.Vector3f>();
            var type=com.mojang.blaze3d.vertex.VertexConsumer.class;
            var sink=(com.mojang.blaze3d.vertex.VertexConsumer)java.lang.reflect.Proxy.newProxyInstance(type.getClassLoader(),new Class<?>[]{type},(proxy,method,args)->{
                if(method.isDefault())return java.lang.reflect.InvocationHandler.invokeDefault(proxy,method,args);
                if(method.getName().equals("addVertex")&&args.length==3)vertices.add(new org.joml.Vector3f(((Number)args[0]).floatValue(),((Number)args[1]).floatValue(),((Number)args[2]).floatValue()));
                return method.getReturnType()==type?proxy:null;
            });
            var pose=new com.mojang.blaze3d.vertex.PoseStack();var initial=new org.joml.Matrix4f(pose.last().pose());
            renderer.render(tile,0,pose,renderType->sink,15728880,net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY);
            check(pose.last().pose().equals(initial),"pose restored");check(net.minecraft.world.item.ItemStack.isSameItemSameComponents(before,stack),"item components read-only");
            if(variant==0){check(vertices.isEmpty(),"empty funnel draws no jar");continue;}
            float minX=Float.POSITIVE_INFINITY,maxX=Float.NEGATIVE_INFINITY,minY=minX,maxY=maxX;
            for(var vertex:vertices){minX=Math.min(minX,vertex.x);maxX=Math.max(maxX,vertex.x);minY=Math.min(minY,vertex.y);maxY=Math.max(maxY,vertex.y);}
            check(Math.abs(minY-.115f)<.0001f&&Math.abs(maxY-.99f)<.0001f,"original full jar bottom/cap height variant "+variant+" "+minY+".."+maxY);
            check(minX>=.18f&&minX<=.188f&&maxX>=.812f&&maxX<=.82f,"original full jar width including label variant "+variant);
        }
        System.out.println("TT FUNNEL RENDER CLIENT AUDIT: ALL PASS (native vertices,original full-size bounds/height,empty/filled/void,item components and pose unchanged)");
    }
    private static void check(boolean ok,String label){if(!ok)throw new IllegalStateException("Funnel renderer: "+label);}
}
