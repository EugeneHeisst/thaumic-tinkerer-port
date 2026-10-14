package dev.eugheist.ttmodern;
import net.minecraft.world.item.*;
import thaumcraft.api.aspects.*;

/** Build164 recall: the first attuned hotbar pearl wins, including a wrong-dimension pearl. */
public final class RecallFocus extends TinkererFocus {
 static final net.neoforged.neoforge.registries.DeferredItem<RecallFocus> ITEM=KamiRegistries.ITEMS.register("focus_recall",RecallFocus::new);
 static void init(net.neoforged.bus.api.IEventBus bus){if(!KamiStartupConfig.enabled())return;bus.addListener((net.neoforged.neoforge.event.ModifyDefaultComponentsEvent e)->e.modify(ITEM.get(),b->b.set(net.minecraft.core.component.DataComponents.RARITY,Rarity.EPIC)));}
 @Override public AspectList getVisCost(ItemStack focus){return new AspectList().add(Aspect.AIR,4000).add(Aspect.EARTH,4000).add(Aspect.ORDER,4000);}
 @Override public String getSortingHelperBase(){return "RECALL";}
 @Override public int getFocusColor(ItemStack focus){return 10287359;}
 @Override public int[] getPossibleUpgradesByRank(ItemStack focus,int rank){return new int[0];}
 @Override public String getDepthTexture(ItemStack focus){return "ttinkerer_focus_recall_depth";}
 @Override public void onUsingFocusTick(ItemStack wand,net.minecraft.world.entity.player.Player player,int remaining){
  // Modern wands use 72000 ticks rather than the original Integer.MAX_VALUE.
  if(wand.getUseDuration(player)-remaining<=60)return;
  if(player instanceof net.minecraft.server.level.ServerPlayer server){
   ItemStack pearl=ItemStack.EMPTY;
   for(int i=0;i<9;i++){var candidate=player.getInventory().getItem(i);if(candidate.is(SkyPearl.ITEM.get())&&SkyPearlData.attuned(candidate)){pearl=candidate;break;}}
   if(!pearl.isEmpty()&&SkyPearlData.dimension(pearl).equals(player.level().dimension().location())&&dev.eugheist.tc4modern.FociContent.consumeAllVis(wand,player,getVisCost(wand),false)&&WarpGateEntity.teleport(server,SkyPearlData.position(pearl)))dev.eugheist.tc4modern.FociContent.consumeAllVis(wand,player,getVisCost(wand),true);
  }
  player.stopUsingItem();
 }
 static void research(){
  if(dev.eugheist.tc4modern.WarpConfig.allowMirrors()){
   var r=new KamiResearch("FOCUS_RECALL",new AspectList().add(Aspect.TRAVEL,2).add(Aspect.ELDRITCH,1).add(Aspect.FLIGHT,1).add(Aspect.MAGIC,1),20,8,5,new ItemStack(ITEM.get()));
   r.setParents("WARP_GATE");r.setParentsHidden("ROD_ICHORCLOTH");r.setPages(new thaumcraft.api.research.ResearchPage("ttresearch.page.FOCUS_RECALL.0"),MagnetResearch.page("FOCUS_RECALL",thaumcraft.api.research.ResearchPage.PageType.INFUSION_CRAFTING)).registerResearchItem();
  }
  ThaumicTinkererModern.INFUSION_RECIPES.add(new dev.eugheist.tc4modern.InfusionItemRecipes.Recipe("FOCUS_RECALL","FOCUS_RECALL",ITEM::get,10,new AspectList().add(Aspect.TRAVEL,100).add(Aspect.ELDRITCH,64).add(Aspect.MAGIC,50),SkyPearl.ITEM::get,java.util.List.of(()->KamiResources.item(KamiResources.Kind.ICHOR),()->KamiResources.item(KamiResources.Kind.ICHOR),()->Items.ENDER_PEARL,()->Items.DIAMOND,()->FocusResearch.item("mirror"),()->FocusResearch.item("focus_portable_hole"))));
 }
}
