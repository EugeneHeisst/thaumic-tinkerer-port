package dev.eugheist.ttmodern;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
/** Worn ichorcloth robes give ItemIchorclothArmor.getVisDiscount (4% each, boots 3%) through TC4's gear discount. */
@EventBusSubscriber(modid="ttinkerer",value=net.neoforged.api.distmarker.Dist.CLIENT)
final class KamiDiscountAudit {
 static int ticks;
 @SubscribeEvent static void tick(net.neoforged.neoforge.client.event.ClientTickEvent.Post e){if(!Boolean.getBoolean("ttinkerer.kamiDiscountAudit"))return;var mc=net.minecraft.client.Minecraft.getInstance();if(mc.player==null||mc.level==null)return;var server=mc.getSingleplayerServer();var id=mc.player.getUUID();
  if(++ticks!=40)return;
  server.execute(()->{var p=server.getPlayerList().getPlayer(id);
   EquipmentSlot[] slots={EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET};
   for(int i=0;i<4;i++)p.setItemSlot(slots[i],new ItemStack(KamiArmor.ITEMS.get(i).get()));
   float d=thaumcraft.common.lib.vis.VisGearDiscount.armor(p,thaumcraft.api.aspects.Aspect.AIR);
   var lines=p.getItemBySlot(EquipmentSlot.FEET).getTooltipLines(net.minecraft.world.item.Item.TooltipContext.of(p.level()),p,net.minecraft.world.item.TooltipFlag.NORMAL);
   System.out.println("TT_KAMI_DISCOUNT total="+d+" boots tooltip="+lines.stream().map(c->c.getString()).toList());
   System.out.println("TT_KAMI_DISCOUNT "+(Math.abs(d-.15f)<1e-4?"PASS":"FAIL"));
   for(var s:slots)p.setItemSlot(s,ItemStack.EMPTY);
   mc.execute(mc::stop);});
 }
}
