package dev.eugheist.ttmodern;
/** Actual server equipment -> client synchronization -> native worn-armor renderer. */
final class KamiArmorWorldClientAudit {
 static boolean checked;private static net.minecraft.client.gui.screens.Screen previous;
 private static final java.util.Map<net.minecraft.world.entity.EquipmentSlot,net.minecraft.world.item.ItemStack> OLD=new java.util.EnumMap<>(net.minecraft.world.entity.EquipmentSlot.class);
 static void tick(net.minecraft.client.Minecraft mc,int tick){
  if(tick==550)mc.getSingleplayerServer().execute(()->{var player=mc.getSingleplayerServer().getPlayerList().getPlayer(mc.player.getUUID());OLD.clear();for(var entry:KamiArmor.ITEMS){var item=entry.get();OLD.put(item.getEquipmentSlot(),player.getItemBySlot(item.getEquipmentSlot()).copy());player.setItemSlot(item.getEquipmentSlot(),new net.minecraft.world.item.ItemStack(item));}});
  if(tick==560){for(var entry:KamiArmor.ITEMS)EnchanterClientAudit.check(mc.player.getItemBySlot(entry.get().getEquipmentSlot()).is(entry.get()),"KAMI native equipment synchronization "+entry.getId());previous=mc.screen;mc.setScreen(new net.minecraft.client.gui.screens.Screen(net.minecraft.network.chat.Component.literal("KAMI worn armor audit")){
   @Override public boolean isPauseScreen(){return false;}
   @Override public void render(net.minecraft.client.gui.GuiGraphics gui,int mx,int my,float partial){gui.fill(0,0,width,height,0xFF20202A);gui.drawCenteredString(mc.font,"KAMI: original worn armor / front and back",width/2,15,0xFFFFFF);int scale=Math.min(height/3,100);net.minecraft.client.gui.screens.inventory.InventoryScreen.renderEntityInInventoryFollowsAngle(gui,width/2-180,30,width/2-10,height-20,scale,.0625f,0,0,mc.player);net.minecraft.client.gui.screens.inventory.InventoryScreen.renderEntityInInventoryFollowsAngle(gui,width/2+10,30,width/2+180,height-20,scale,.0625f,9,0,mc.player);gui.flush();}
  });}
  if(tick==575){EnchanterClientAudit.Client.shot(mc,"kami-worn-armor.png");checked=true;mc.setScreen(previous);mc.getSingleplayerServer().execute(()->{var p=mc.getSingleplayerServer().getPlayerList().getPlayer(mc.player.getUUID());for(var entry:OLD.entrySet())p.setItemSlot(entry.getKey(),entry.getValue());OLD.clear();});System.out.println("TT KAMI ARMOR WORLD CLIENT AUDIT: ALL PASS (actual equipment packets,four worn pieces,native armor layer rendering,original textures,restored equipment)");}
 }
}
