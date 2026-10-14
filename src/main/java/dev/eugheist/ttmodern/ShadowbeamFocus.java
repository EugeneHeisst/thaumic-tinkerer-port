package dev.eugheist.ttmodern;
import net.minecraft.world.item.*;
import net.minecraft.world.entity.player.Player;
import thaumcraft.api.aspects.*;
public final class ShadowbeamFocus extends TinkererFocus {
 static final net.neoforged.neoforge.registries.DeferredItem<ShadowbeamFocus> ITEM=KamiRegistries.ITEMS.register("focus_shadowbeam",ShadowbeamFocus::new);
 static void init(net.neoforged.bus.api.IEventBus bus){if(!KamiStartupConfig.enabled())return;bus.addListener((net.neoforged.neoforge.event.ModifyDefaultComponentsEvent e)->e.modify(ITEM.get(),b->b.set(net.minecraft.core.component.DataComponents.RARITY,Rarity.EPIC)));}
 @Override public AspectList getVisCost(ItemStack focus){return new AspectList().add(Aspect.ORDER,25).add(Aspect.ENTROPY,25).add(Aspect.AIR,15);}
 @Override public boolean isVisCostPerTick(ItemStack focus){return true;}
 @Override public String getSortingHelperBase(){return "SHADOWBEAM";}
 @Override public int[] getPossibleUpgradesByRank(ItemStack focus,int rank){return new int[0];}
 @Override public int getFocusColor(ItemStack focus){return 4915283;}
 @Override public void onUsingFocusTick(ItemStack wand,Player player,int ticks){
  if(!(player.level() instanceof net.minecraft.server.level.ServerLevel level)||!dev.eugheist.tc4modern.FociContent.consumeAllVis(wand,player,getVisCost(wand),true))return;
  if(level.random.nextInt(10)==0)level.playSound(null,player.blockPosition(),net.minecraft.sounds.SoundEvent.createVariableRangeEvent(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("tc4modern","brain")),net.minecraft.sounds.SoundSource.PLAYERS,.5f,1);
  var start=ShadowbeamPath.start(player.getX(),player.getY(),player.getZ(),player.getEyeHeight(),player.getYRot(),player.getXRot());
  if(net.neoforged.fml.loading.FMLEnvironment.dist.isClient()){var points=new java.util.ArrayList<net.minecraft.world.phys.Vec3>();ShadowbeamTrace.run(level,player,start,points::add);ShadowbeamFx.emit(level.dimension(),points);}else ShadowbeamTrace.run(level,player,start);
 }
 static void research(){
  var r=new KamiResearch("FOCUS_SHADOWBEAM",new AspectList().add(Aspect.DARKNESS,2).add(Aspect.MAGIC,1).add(Aspect.ELDRITCH,1).add(Aspect.TAINT,1),14,4,5,new ItemStack(ITEM.get()));
  r.setParents("ROD_ICHORCLOTH");r.setPages(new thaumcraft.api.research.ResearchPage("ttresearch.page.FOCUS_SHADOWBEAM.0"),MagnetResearch.page("FOCUS_SHADOWBEAM",thaumcraft.api.research.ResearchPage.PageType.INFUSION_CRAFTING)).registerResearchItem();
  ThaumicTinkererModern.INFUSION_RECIPES.add(new dev.eugheist.tc4modern.InfusionItemRecipes.Recipe("FOCUS_SHADOWBEAM","FOCUS_SHADOWBEAM",ITEM::get,12,new AspectList().add(Aspect.DARKNESS,65).add(Aspect.ELDRITCH,32).add(Aspect.MAGIC,50).add(Aspect.WEAPON,32),()->dev.eugheist.tc4modern.FociContent.SHOCK.get(),java.util.List.of(()->KamiResources.item(KamiResources.Kind.ICHOR),()->Items.ARROW,()->Items.DIAMOND,()->dev.eugheist.tc4modern.FociWorld.EXCAVATION.get(),()->ThaumicTinkererModern.FOCUS_DEFLECT.get(),()->KamiResources.item(KamiResources.Kind.ICHOR))));
 }
}
