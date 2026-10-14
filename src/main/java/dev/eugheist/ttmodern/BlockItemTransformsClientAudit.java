package dev.eugheist.ttmodern;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

final class BlockItemTransformsClientAudit {
    static void run(Minecraft mc){
        for(var item:java.util.List.of(ThaumicTinkererModern.FUNNEL.get().asItem(),ThaumicTinkererModern.REPAIRER.get().asItem(),ThaumicTinkererModern.DISLOCATOR.get().asItem(),ThaumicTinkererModern.MAGNET.get().asItem(),ThaumicTinkererModern.MOB_MAGNET.get().asItem())){
            var model=mc.getItemRenderer().getModel(new ItemStack(item),null,null,0);
            var gui=model.getTransforms().getTransform(ItemDisplayContext.GUI);
            check(gui.scale.equals(new org.joml.Vector3f(.625f))&&gui.rotation.equals(new org.joml.Vector3f(30,225,0)),"isometric GUI "+item);
            for(var hand:java.util.List.of(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND,ItemDisplayContext.FIRST_PERSON_LEFT_HAND)){
                check(model.getTransforms().getTransform(hand).scale.equals(new org.joml.Vector3f(.4f)),"first person block scale "+item+" "+hand);
            }
            check(model.getTransforms().getTransform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND).scale.equals(new org.joml.Vector3f(.375f)),"third person block scale "+item);
        }
        allBlockItems(mc);
        System.out.println("TT BLOCK ITEM TRANSFORMS CLIENT AUDIT: ALL PASS (five repaired block items,GUI/both first-person hands/third-person native baked transforms)");
    }
    private static void allBlockItems(Minecraft mc){
        int blocks=0,renders=0;
        for(var entry:ThaumicTinkererModern.itemEntries().toList()){
            if(!(entry.get() instanceof net.minecraft.world.item.BlockItem))continue;
            blocks++;var stack=new ItemStack(entry.get());var saved=stack.copy();
            var model=mc.getItemRenderer().getModel(stack,null,null,0);
            for(var context:java.util.List.of(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND,ItemDisplayContext.FIRST_PERSON_LEFT_HAND,ItemDisplayContext.THIRD_PERSON_RIGHT_HAND,ItemDisplayContext.THIRD_PERSON_LEFT_HAND)){
                if(model.isGui3d()){
                    float expected=context.firstPerson()?.4f:.375f;
                    check(model.getTransforms().getTransform(context).scale.equals(new org.joml.Vector3f(expected)),"all 3D block items native hand scale "+entry.getId()+" "+context);
                }
                var vertices=new java.util.ArrayList<org.joml.Vector3f>();
                var type=com.mojang.blaze3d.vertex.VertexConsumer.class;
                var sink=(com.mojang.blaze3d.vertex.VertexConsumer)java.lang.reflect.Proxy.newProxyInstance(type.getClassLoader(),new Class<?>[]{type},(proxy,method,args)->{
                    if(method.isDefault())return java.lang.reflect.InvocationHandler.invokeDefault(proxy,method,args);
                    if(method.getName().equals("addVertex")&&args.length==3)vertices.add(new org.joml.Vector3f(((Number)args[0]).floatValue(),((Number)args[1]).floatValue(),((Number)args[2]).floatValue()));
                    return method.getReturnType()==type?proxy:null;
                });
                var pose=new com.mojang.blaze3d.vertex.PoseStack();var before=new org.joml.Matrix4f(pose.last().pose());
                boolean left=context==ItemDisplayContext.FIRST_PERSON_LEFT_HAND||context==ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
                mc.getItemRenderer().render(stack,context,left,pose,t->sink,15728880,net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY,model);
                check(pose.last().pose().equals(before)&&ItemStack.isSameItemSameComponents(saved,stack),"held render preserves pose/item "+entry.getId());
                check(!vertices.isEmpty(),"held block has visible geometry "+entry.getId()+" "+context);
                var min=new org.joml.Vector3f(Float.POSITIVE_INFINITY);var max=new org.joml.Vector3f(Float.NEGATIVE_INFINITY);
                for(var vertex:vertices){check(vertex.isFinite(),"finite held vertices "+entry.getId());min.min(vertex);max.max(vertex);}
                check(new org.joml.Vector3f(max).sub(min).length()<2,"no oversized held block geometry "+entry.getId()+" "+context+" "+min+".."+max);
                renders++;
            }
        }
        check(blocks>15,"whole registered block item coverage");
        System.out.println("TT ALL BLOCK ITEMS HELD GEOMETRY AUDIT: ALL PASS ("+blocks+" registered block items, "+renders+" native first/third-person left/right renders,finite/bounded geometry,pose/components unchanged)");
    }
    private static void check(boolean pass,String label){if(!pass)throw new IllegalStateException("Block item transforms: "+label);}
}
