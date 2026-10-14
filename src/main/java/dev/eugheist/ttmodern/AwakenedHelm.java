package dev.eugheist.ttmodern;
import net.minecraft.world.item.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.*;
import thaumcraft.api.aspects.*;
@net.neoforged.fml.common.EventBusSubscriber(modid="ttinkerer")
public final class AwakenedHelm extends AwakenedArmorItem implements thaumcraft.api.IGoggles,thaumcraft.api.nodes.IRevealer {
 static final net.neoforged.neoforge.registries.DeferredItem<AwakenedHelm> ITEM=KamiRegistries.ITEMS.register("ichorcloth_helm_gem",AwakenedHelm::new);
 public AwakenedHelm(){super(Type.HELMET);}
 static void init(){}
 public boolean showNodes(ItemStack stack,LivingEntity entity){return true;}
 public boolean showIngamePopups(ItemStack stack,LivingEntity entity){return true;}
 static void update(Player player){var stack=player.getItemBySlot(EquipmentSlot.HEAD);if(!(stack.getItem() instanceof AwakenedHelm))return;if(!disabled(stack)&&AwakenedArmorStatus.enabled(player)){if(player.isEyeInFluid(net.minecraft.tags.FluidTags.WATER)){player.setAirSupply(300);refresh(player,MobEffects.NIGHT_VISION);}if(player.isEyeInFluid(net.minecraft.tags.FluidTags.LAVA)){player.setAirSupply(300);refresh(player,MobEffects.BLINDNESS);}}int food=player.getFoodData().getFoodLevel();if(food>0&&food<18&&player.getHealth()>0&&player.getHealth()<player.getMaxHealth()&&player.tickCount%80==0)player.heal(1);}
 private static void refresh(Player player,net.minecraft.core.Holder<MobEffect> effect){var old=player.getEffect(effect);player.addEffect(new MobEffectInstance(effect,202,old!=null&&old.getDuration()<=202?old.getAmplifier():0,true,true));}
 @net.neoforged.bus.api.SubscribeEvent public static void tick(net.neoforged.neoforge.event.tick.EntityTickEvent.Post event){if(event.getEntity() instanceof Player player&&!player.level().isClientSide)update(player);}
 static void research(){var r=new KamiResearch("ICHORCLOTH_HELM_GEM",new AspectList().add(Aspect.WATER,2).add(Aspect.HEAL,1).add(Aspect.HUNGER,1).add(Aspect.AURA,1),18,3,5,new ItemStack(ITEM.get()));r.setParents("ICHORCLOTH_ARMOR");r.setPages(new thaumcraft.api.research.ResearchPage("ttresearch.page.ICHORCLOTH_HELM_GEM.0"),MagnetResearch.page("ICHORCLOTH_HELM_GEM",thaumcraft.api.research.ResearchPage.PageType.INFUSION_CRAFTING)).registerResearchItem();ThaumicTinkererModern.INFUSION_RECIPES.add(new dev.eugheist.tc4modern.InfusionItemRecipes.Recipe("ICHORCLOTH_HELM_GEM","ICHORCLOTH_HELM_GEM",ITEM::get,13,new AspectList().add(Aspect.WATER,50).add(Aspect.ARMOR,32).add(Aspect.HUNGER,32).add(Aspect.AURA,32).add(Aspect.LIGHT,64).add(Aspect.FLESH,16).add(Aspect.MIND,16),()->KamiArmor.ITEMS.getFirst().get(),java.util.List.of(()->Items.DIAMOND,()->KamiResources.item(KamiResources.Kind.ICHOR),()->KamiResources.item(KamiResources.Kind.ICHOR),()->FocusResearch.item("thaumonomicon"),()->FocusResearch.item("focus_primal"),()->Items.GOLDEN_HELMET,()->Items.POTION,()->FocusResearch.item("goggles"),()->Items.GHAST_TEAR,()->Items.COD,()->Items.CAKE,()->Items.ENDER_EYE)));}
 public static java.util.List<ItemStack> components(java.util.List<ItemStack> original){var result=new java.util.ArrayList<>(original);result.set(6,net.minecraft.world.item.alchemy.PotionContents.createItemStack(Items.POTION,net.minecraft.world.item.alchemy.Potions.NIGHT_VISION));return result;}
}
