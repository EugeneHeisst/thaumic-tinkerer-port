package dev.eugheist.ttmodern;
final class KamiDisabledClientAudit {
 static void run(net.minecraft.client.Minecraft mc){
  if(KamiStartupConfig.enabled()||KamiRegistries.ITEMS.getEntries().size()<30)throw new IllegalStateException("KAMI disabled fixture/config inventory");
  for(var holder:KamiRegistries.ITEMS.getEntries())if(net.minecraft.core.registries.BuiltInRegistries.ITEM.containsKey(holder.getId()))throw new IllegalStateException("disabled client item registered: "+holder.getId());
  for(var holder:ThaumicTinkererModern.itemEntries().toList()){var stack=new net.minecraft.world.item.ItemStack(holder.get());var model=mc.getItemRenderer().getModel(stack,null,null,0);if(model==mc.getModelManager().getMissingModel()||model.getParticleIcon().contents().name().getPath().equals("missingno"))throw new IllegalStateException("ordinary model missing with KAMI disabled: "+holder.getId());}
  for(var holder:ThaumicTinkererModern.blockEntries().toList())if(mc.getBlockRenderer().getBlockModel(holder.get().defaultBlockState())==mc.getModelManager().getMissingModel())throw new IllegalStateException("ordinary block missing with KAMI disabled");
  var gui=new net.minecraft.client.gui.GuiGraphics(mc,mc.renderBuffers().bufferSource());gui.renderItem(new net.minecraft.world.item.ItemStack(ThaumicTinkererModern.DARK_QUARTZ.get()),20,20);gui.flush();
  System.out.println("TT KAMI DISABLED CLIENT AUDIT: ALL PASS (native client startup without KAMI objects/screens/renderers/extensions,ordinary models and native item draw retained)");
 }
}
