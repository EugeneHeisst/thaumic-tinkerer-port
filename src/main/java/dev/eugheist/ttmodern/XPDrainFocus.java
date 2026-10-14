package dev.eugheist.ttmodern;
import net.minecraft.world.item.*;
import net.minecraft.world.entity.player.Player;
import thaumcraft.api.aspects.*;
/** Original shared focus cursor: each server use tick buys five vis for fifteen XP points. */
public final class XPDrainFocus extends TinkererFocus {
 static final net.neoforged.neoforge.registries.DeferredItem<XPDrainFocus> ITEM=KamiRegistries.ITEMS.register("focus_xp_drain",XPDrainFocus::new);
 private int lastGiven;
 static void init(net.neoforged.bus.api.IEventBus bus){if(!KamiStartupConfig.enabled())return;bus.addListener((net.neoforged.neoforge.event.ModifyDefaultComponentsEvent e)->e.modify(ITEM.get(),b->b.set(net.minecraft.core.component.DataComponents.RARITY,Rarity.EPIC)));}
 @Override public AspectList getVisCost(ItemStack focus){return new AspectList();}
 @Override public boolean isVisCostPerTick(ItemStack focus){return true;}
 @Override public String getSortingHelperBase(){return "XPDRAIN";}
 @Override public int[] getPossibleUpgradesByRank(ItemStack focus,int rank){return new int[0];}
 @Override public int getFocusColor(ItemStack focus){return net.neoforged.fml.loading.FMLEnvironment.dist==net.neoforged.api.distmarker.Dist.CLIENT?XPDrainFocusClient.color():0xFFFFFF;}
 @Override public void onUsingFocusTick(ItemStack wand,Player player,int ticks){if(player.level().isClientSide)return;var primals=Aspect.getPrimalAspects();Aspect selected=null;for(int attempts=0;selected==null&&attempts<7;attempts++){lastGiven=lastGiven==5?0:lastGiven+1;var aspect=primals.get(lastGiven);if(dev.eugheist.tc4modern.WandData.get(wand,aspect)<dev.eugheist.tc4modern.WandData.capacity(wand))selected=aspect;}if(selected!=null&&player.totalExperience>=15){player.giveExperiencePoints(-15);dev.eugheist.tc4modern.WandData.add(wand,selected,5,true);}}
 @Override public void appendHoverText(ItemStack focus,TooltipContext context,java.util.List<net.minecraft.network.chat.Component> lines,TooltipFlag flag){super.appendHoverText(focus,context,lines,flag);lines.add(net.minecraft.network.chat.Component.translatable("ttmisc.experience").append(" x 15").withStyle(net.minecraft.ChatFormatting.GREEN));}
 static void research(){var r=new KamiResearch("FOCUS_XP_DRAIN",new AspectList().add(Aspect.MIND,2).add(Aspect.MAGIC,1).add(Aspect.AURA,1).add(Aspect.MAN,1),12,3,5,new ItemStack(ITEM.get()));r.setParents("ROD_ICHORCLOTH");r.setPages(new thaumcraft.api.research.ResearchPage("ttresearch.page.FOCUS_XP_DRAIN.0"),MagnetResearch.page("FOCUS_XP_DRAIN",thaumcraft.api.research.ResearchPage.PageType.INFUSION_CRAFTING)).registerResearchItem();ThaumicTinkererModern.INFUSION_RECIPES.add(new dev.eugheist.tc4modern.InfusionItemRecipes.Recipe("FOCUS_XP_DRAIN","FOCUS_XP_DRAIN",ITEM::get,12,new AspectList().add(Aspect.MIND,65).add(Aspect.TAINT,16).add(Aspect.MAGIC,50).add(Aspect.AURA,32),()->Items.ENDER_PEARL,java.util.List.of(()->KamiResources.item(KamiResources.Kind.ICHOR),()->Items.EXPERIENCE_BOTTLE,()->Items.DIAMOND,()->ThaumicTinkererModern.XP_TALISMAN.get(),()->Items.ENCHANTING_TABLE,()->KamiResources.item(KamiResources.Kind.ICHOR))));}
}
