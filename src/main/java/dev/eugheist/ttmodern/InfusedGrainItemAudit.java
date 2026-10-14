package dev.eugheist.ttmodern;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.component.DataComponents;
import thaumcraft.api.aspects.Aspect;
final class InfusedGrainItemAudit {
 static void run(){
  var items=java.util.List.of(ThaumicTinkererModern.INFUSED_GRAIN_AIR.get(),ThaumicTinkererModern.INFUSED_GRAIN_FIRE.get(),ThaumicTinkererModern.INFUSED_GRAIN_EARTH.get(),ThaumicTinkererModern.INFUSED_GRAIN_WATER.get());
  var expected=java.util.List.of(Aspect.AIR,Aspect.FIRE,Aspect.EARTH,Aspect.WATER);
  for(int i=0;i<items.size();i++){
   var stack=new ItemStack(items.get(i));var aspects=dev.eugheist.tc4modern.AspectTable.get(stack);
   if(!aspects.equals(java.util.Map.of(expected.get(i).getTag(),1,Aspect.CROP.getTag(),1)))throw new IllegalStateException("TT infused grain aspect integration "+i+": "+aspects);
   if(stack.has(DataComponents.FOOD)||stack.getMaxStackSize()!=64)throw new IllegalStateException("TT grain is non-food stackable ingredient");
  }
  System.out.println("TT INFUSED GRAIN ITEM AUDIT: ALL PASS (four original variants,actual AspectTable primal1/crop1,non-food,stack64)");
 }
}
