package dev.eugheist.ttmodern;
/** Reads the real TC configuration at startup, not a mocked configuration predicate. */
final class MirrorsDisabledAudit {
 static void run(net.neoforged.neoforge.event.server.ServerStartedEvent event){
  if(dev.eugheist.tc4modern.WarpConfig.allowMirrors())throw new IllegalStateException("mirror config was not loaded false");
  for(String key:new String[]{"MIRROR","DISLOCATOR","WARP_GATE","FOCUS_RECALL"})if(thaumcraft.api.research.ResearchCategories.getResearch(key)!=null)throw new IllegalStateException("disabled mirror research retained: "+key);
  if(dev.eugheist.tc4modern.InfusionItemRecipes.all().stream().anyMatch(r->r.key().equals("WARP_GATE")))throw new IllegalStateException("disabled warp recipe retained");
  // These recipes have no separate allowMirrors check in build164; their research remains unavailable.
  for(String key:new String[]{"SKY_PEARL","FOCUS_RECALL"})if(dev.eugheist.tc4modern.InfusionItemRecipes.all().stream().noneMatch(r->r.key().equals(key)))throw new IllegalStateException("original research-locked recipe removed: "+key);
  if(WarpGateBlock.ITEM.get()==null||SkyPearl.ITEM.get()==null||RecallFocus.ITEM.get()==null)throw new IllegalStateException("existing item registrations removed");
  System.out.println("TT MIRRORS CONFIG AUDIT: ALL PASS (actual allow_mirrors=false,no gated research or gate recipe,original research-locked pearl/recall recipes,item registrations retained)");
 }
}
