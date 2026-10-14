package dev.eugheist.ttmodern;
import net.minecraft.world.item.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import thaumcraft.api.aspects.*;
@net.neoforged.fml.common.EventBusSubscriber(modid="ttinkerer")
public final class AwakenedChest extends AwakenedArmorItem {
 static final net.neoforged.neoforge.registries.DeferredItem<AwakenedChest> ITEM=KamiRegistries.ITEMS.register("ichorcloth_chest_gem",AwakenedChest::new);
 private static final java.util.Set<Player> FLIGHT=java.util.Collections.synchronizedSet(java.util.Collections.newSetFromMap(new java.util.WeakHashMap<>()));
 public AwakenedChest(){super(Type.CHESTPLATE);}
 static void init(){}
 static void update(Player player){update(player,TinkererConfig.flightEnabled());}
 static void update(Player player,boolean enableFlight){var stack=player.getItemBySlot(EquipmentSlot.CHEST);boolean active=stack.getItem() instanceof AwakenedChest&&!disabled(stack)&&AwakenedArmorStatus.enabled(player);if(active&&!player.level().isClientSide){DeflectFocus.protect(player);DeflectFocus.protect(player);}boolean wants=active&&enableFlight;var abilities=player.getAbilities();boolean changed=false;if(FLIGHT.contains(player)){if(wants){changed=!abilities.mayfly;abilities.mayfly=true;}else{if(!abilities.instabuild){changed=abilities.mayfly||abilities.flying||abilities.invulnerable;abilities.mayfly=false;abilities.flying=false;abilities.invulnerable=false;}FLIGHT.remove(player);}}else if(wants){FLIGHT.add(player);changed=!abilities.mayfly;abilities.mayfly=true;}if(changed&&player instanceof net.minecraft.server.level.ServerPlayer)player.onUpdateAbilities();}
 @net.neoforged.bus.api.SubscribeEvent public static void tick(net.neoforged.neoforge.event.tick.EntityTickEvent.Post event){if(event.getEntity() instanceof Player player)update(player);}
 static void research(){var r=new KamiResearch("ICHORCLOTH_CHEST_GEM",new AspectList().add(Aspect.AIR,2).add(Aspect.MOTION,1).add(Aspect.FLIGHT,1).add(Aspect.ELDRITCH,1),17,7,5,new ItemStack(ITEM.get()));r.setParents("ICHORCLOTH_ARMOR");r.setPages(new thaumcraft.api.research.ResearchPage("ttresearch.page.ICHORCLOTH_CHEST_GEM.0"),MagnetResearch.page("ICHORCLOTH_CHEST_GEM",thaumcraft.api.research.ResearchPage.PageType.INFUSION_CRAFTING)).registerResearchItem();ThaumicTinkererModern.INFUSION_RECIPES.add(new dev.eugheist.tc4modern.InfusionItemRecipes.Recipe("ICHORCLOTH_CHEST_GEM","ICHORCLOTH_CHEST_GEM",ITEM::get,13,new AspectList().add(Aspect.AIR,50).add(Aspect.ARMOR,32).add(Aspect.FLIGHT,32).add(Aspect.ORDER,32).add(Aspect.LIGHT,64).add(Aspect.ELDRITCH,16).add(Aspect.SENSES,16),()->KamiArmor.ITEMS.get(1).get(),java.util.List.of(()->Items.DIAMOND,()->KamiResources.item(KamiResources.Kind.ICHOR),()->KamiResources.item(KamiResources.Kind.ICHOR),()->FocusResearch.item("focus_primal"),()->FocusResearch.item("thaumonomicon"),()->Items.GOLDEN_CHESTPLATE,()->ThaumicTinkererModern.FOCUS_FLIGHT.get(),()->FocusResearch.item("hover_harness"),()->ThaumicTinkererModern.FOCUS_DEFLECT.get(),()->Items.FEATHER,()->Items.GHAST_TEAR,()->Items.ARROW)));}
}
