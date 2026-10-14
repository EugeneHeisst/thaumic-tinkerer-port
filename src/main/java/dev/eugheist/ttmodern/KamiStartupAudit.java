package dev.eugheist.ttmodern;
/** Actual disabled startup must omit KAMI registrations and damage behavior. */
final class KamiStartupAudit {
 static void run(net.neoforged.neoforge.event.server.ServerStartedEvent event){
  if(KamiRegistries.ITEMS.getEntries().size()<30||KamiRegistries.BLOCKS.getEntries().isEmpty()||KamiRegistries.ENTITIES.getEntries().isEmpty()||KamiRegistries.MENUS.getEntries().isEmpty())throw new IllegalStateException("empty KAMI audit coverage");
  var player=net.neoforged.neoforge.common.util.FakePlayerFactory.getMinecraft(event.getServer().overworld());
  if(SoulHearts.absorb(player,1.75f)!=1.75f)throw new IllegalStateException("disabled KAMI changes fractional damage");
  if(KamiStartupConfig.enabled())throw new IllegalStateException("actual KAMI startup configuration was not disabled");
  if(event.getServer().getLevel(BedrockPortal.DIMENSION)!=null)throw new IllegalStateException("KAMI-disabled dimension still loaded");
  for(String key:new String[]{"ICHOR","ICHORIUM","ICHOR_TOOLS","WARP_GATE","FOCUS_RECALL"})if(thaumcraft.api.research.ResearchCategories.getResearch(key)!=null)throw new IllegalStateException("KAMI-disabled research remains: "+key);
  if(thaumcraft.api.research.ResearchCategories.getResearch("DARK_QUARTZ")==null)throw new IllegalStateException("ordinary addon research removed");
  for(var holder:KamiRegistries.ITEMS.getEntries())if(net.minecraft.core.registries.BuiltInRegistries.ITEM.containsKey(holder.getId()))throw new IllegalStateException("disabled KAMI item registered: "+holder.getId());
  for(var holder:KamiRegistries.BLOCKS.getEntries())if(net.minecraft.core.registries.BuiltInRegistries.BLOCK.containsKey(holder.getId()))throw new IllegalStateException("disabled KAMI block registered: "+holder.getId());
  for(var holder:KamiRegistries.ENTITIES.getEntries())if(net.minecraft.core.registries.BuiltInRegistries.BLOCK_ENTITY_TYPE.containsKey(holder.getId()))throw new IllegalStateException("disabled KAMI entity registered: "+holder.getId());
  for(var holder:KamiRegistries.MENUS.getEntries())if(net.minecraft.core.registries.BuiltInRegistries.MENU.containsKey(holder.getId()))throw new IllegalStateException("disabled KAMI menu registered: "+holder.getId());
  System.out.println("TT KAMI STARTUP AUDIT: ALL PASS (actual STARTUP forceenabled=false,config loaded before registrations,items/blocks/entities/menus/research/dimension absent,ordinary research retained)");
 }
}
