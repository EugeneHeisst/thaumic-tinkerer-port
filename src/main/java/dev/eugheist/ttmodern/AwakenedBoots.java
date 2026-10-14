package dev.eugheist.ttmodern;
import net.minecraft.world.item.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import thaumcraft.api.aspects.*;
@net.neoforged.fml.common.EventBusSubscriber(modid="ttinkerer")
public final class AwakenedBoots extends AwakenedArmorItem {
 static final net.neoforged.neoforge.registries.DeferredItem<AwakenedBoots> ITEM=KamiRegistries.ITEMS.register("ichorcloth_boots_gem",AwakenedBoots::new);
 private static final java.util.Map<Player,Double> STEPS=new java.util.WeakHashMap<>();
 public AwakenedBoots(){super(Type.BOOTS);}static void init(){}
 public static boolean worn(Player p){return p.getItemBySlot(EquipmentSlot.FEET).getItem() instanceof AwakenedBoots;}
 public static boolean active(Player p){return worn(p)&&!disabled(p.getItemBySlot(EquipmentSlot.FEET))&&AwakenedArmorStatus.enabled(p);}
 public static float airSpeed(Player p){return p.isSprinting()?.05f:.04f;}
 static void update(Player p){boolean active=active(p);if(p.level().isClientSide){var step=p.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.STEP_HEIGHT);if(step!=null){if(active){STEPS.putIfAbsent(p,step.getBaseValue());step.setBaseValue(p.isShiftKeyDown()?.5:1);}else if(STEPS.containsKey(p))step.setBaseValue(STEPS.remove(p));}}if(!active)return;p.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.DIG_SPEED,2,1,true,true));if((p.onGround()||p.getAbilities().flying)&&p.zza>0)p.moveRelative(p.getAbilities().flying?.075f:.15f,new net.minecraft.world.phys.Vec3(0,0,1));p.fallDistance=0;var pos=new net.minecraft.core.BlockPos((int)p.getX(),(int)p.getY()-1,(int)p.getZ());if(p.level().getBlockState(pos).is(net.minecraft.world.level.block.Blocks.DIRT))p.level().setBlock(pos,net.minecraft.world.level.block.Blocks.GRASS_BLOCK.defaultBlockState(),2);}
 @net.neoforged.bus.api.SubscribeEvent static void tick(net.neoforged.neoforge.event.tick.EntityTickEvent.Post e){if(e.getEntity() instanceof Player p)update(p);}
 @net.neoforged.bus.api.SubscribeEvent static void jump(net.neoforged.neoforge.event.entity.living.LivingEvent.LivingJumpEvent e){if(e.getEntity() instanceof Player p&&active(p))p.setDeltaMovement(p.getDeltaMovement().add(0,.3,0));}
 @net.neoforged.bus.api.SubscribeEvent static void fall(net.neoforged.neoforge.event.entity.living.LivingFallEvent e){if(e.getEntity() instanceof Player p&&worn(p))e.setDistance(0);}
 static void research(){var r=new KamiResearch("ICHORCLOTH_BOOTS_GEM",new AspectList().add(Aspect.EARTH,2).add(Aspect.TRAVEL,1).add(Aspect.MINE,1).add(Aspect.PLANT,1),15,10,5,new ItemStack(ITEM.get()));r.setParents("ICHORCLOTH_ARMOR");r.setPages(new thaumcraft.api.research.ResearchPage("ttresearch.page.ICHORCLOTH_BOOTS_GEM.0"),MagnetResearch.page("ICHORCLOTH_BOOTS_GEM",thaumcraft.api.research.ResearchPage.PageType.INFUSION_CRAFTING)).registerResearchItem();ThaumicTinkererModern.INFUSION_RECIPES.add(new dev.eugheist.tc4modern.InfusionItemRecipes.Recipe("ICHORCLOTH_BOOTS_GEM","ICHORCLOTH_BOOTS_GEM",ITEM::get,13,new AspectList().add(Aspect.EARTH,50).add(Aspect.ARMOR,32).add(Aspect.MINE,32).add(Aspect.MOTION,32).add(Aspect.LIGHT,64).add(Aspect.PLANT,16).add(Aspect.TRAVEL,16),()->KamiArmor.ITEMS.get(3).get(),java.util.List.of(()->Items.DIAMOND,()->KamiResources.item(KamiResources.Kind.ICHOR),()->KamiResources.item(KamiResources.Kind.ICHOR),()->FocusResearch.item("thaumonomicon"),()->FocusResearch.item("focus_primal"),()->Items.GOLDEN_BOOTS,()->Items.GRASS_BLOCK,()->FocusResearch.item("arcane_bore"),()->FocusResearch.item("lamp_growth"),()->Items.WHEAT_SEEDS,()->Items.WHITE_WOOL,()->Items.LEAD)));}
}
