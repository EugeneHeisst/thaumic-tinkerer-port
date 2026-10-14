package dev.eugheist.ttmodern;
/** Exercise the actual client collector rather than an equivalent addon helper. */
final class IchorPouchRadialAudit {
 static void run(net.minecraft.client.Minecraft mc){
  var inventory=mc.player.getInventory();var worn=dev.eugheist.tc4modern.BaublesInventory.of(mc.player);
  var saved=inventory.getItem(9);var belt=worn.getItem(3);
  try{
   var type=Class.forName("dev.eugheist.tc4modern.FociRadial");var gather=type.getDeclaredMethod("gather",net.minecraft.world.entity.player.Player.class);gather.setAccessible(true);var field=type.getDeclaredField("ITEMS");field.setAccessible(true);
   var focus=new net.minecraft.world.item.ItemStack(FocusResearch.item("focus_portable_hole"));focus.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME,net.minecraft.network.chat.Component.literal("Ichor radial slot117 fixture"));var key=((dev.eugheist.tc4modern.FociContent.Focus)focus.getItem()).getSortingHelper(focus);
   var pouch=new net.minecraft.world.item.ItemStack(IchorPouch.ITEM.get());var contents=IchorPouch.contents(pouch);contents.set(116,focus);IchorPouch.store(pouch,contents);
   for(boolean equipped:new boolean[]{false,true}){inventory.setItem(9,equipped?net.minecraft.world.item.ItemStack.EMPTY:pouch);worn.setItem(3,equipped?pouch:net.minecraft.world.item.ItemStack.EMPTY);gather.invoke(null,mc.player);var collected=(java.util.Map<?,?>)field.get(null);EnchanterClientAudit.check(collected.get(key) instanceof net.minecraft.world.item.ItemStack stack&&net.minecraft.world.item.ItemStack.isSameItemSameComponents(stack,focus),"actual radial collector sees117th focus from "+(equipped?"belt":"inventory"));}
   System.out.println("TT ICHOR POUCH RADIAL AUDIT: ALL PASS (native collector,117th slot,inventory and Baubles belt)");
  }catch(ReflectiveOperationException e){throw new IllegalStateException("Native pouch radial audit",e);}finally{inventory.setItem(9,saved);worn.setItem(3,belt);}
 }
}
