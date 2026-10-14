package dev.eugheist.ttmodern;
import net.minecraft.world.item.*;
import net.minecraft.world.entity.*;
import thaumcraft.api.aspects.*;
public final class CatAmulet extends Item implements baubles.api.IBauble {
 static final net.neoforged.neoforge.registries.DeferredItem<CatAmulet> ITEM=KamiRegistries.ITEMS.register("cat_amulet",CatAmulet::new);
 public CatAmulet(){super(new Properties().stacksTo(1).rarity(Rarity.EPIC));}
 static void init(){}
 public interface TargetGoal {Class<?> ttinkerer$targetType();void ttinkerer$targetType(Class<?> type);}
 @Override public baubles.api.BaubleType getBaubleType(ItemStack stack){return baubles.api.BaubleType.AMULET;}
 @Override public void onWornTick(ItemStack stack,LivingEntity wearer){
  if(wearer.level().isClientSide)return;
  for(var mob:wearer.level().getEntitiesOfClass(Mob.class,new net.minecraft.world.phys.AABB(wearer.getX()-10,wearer.getY()-4,wearer.getZ()-10,wearer.getX()+10,wearer.getY()+4,wearer.getZ()+10))){
   var access=(dev.eugheist.ttmodern.mixin.CatMobAccessor)mob;
   var goals=new java.util.ArrayList<>(access.ttinkerer$goals().getAvailableGoals());goals.addAll(access.ttinkerer$targets().getAvailableGoals());
   for(var wrapped:goals){var goal=wrapped.getGoal();if(goal instanceof dev.eugheist.ttmodern.mixin.CatAvoidGoalAccessor avoid&&(avoid.ttinkerer$avoidClass()==net.minecraft.world.entity.animal.Ocelot.class||avoid.ttinkerer$avoidClass()==net.minecraft.world.entity.animal.Cat.class))avoid.ttinkerer$avoidClass(net.minecraft.world.entity.player.Player.class);if(goal instanceof TargetGoal target&&target.ttinkerer$targetType()==net.minecraft.world.entity.player.Player.class)target.ttinkerer$targetType(net.minecraft.world.entity.boss.enderdragon.EndCrystal.class);}
   if(mob instanceof net.minecraft.world.entity.monster.Creeper creeper){((dev.eugheist.ttmodern.mixin.CatCreeperAccessor)creeper).ttinkerer$fuse(2);creeper.setTarget(null);}
  }
 }
 static void research(){var r=new KamiResearch("CAT_AMULET",new AspectList().add(Aspect.MIND,2).add(Aspect.ORDER,1).add(Aspect.DARKNESS,1).add(Aspect.DEATH,1),13,10,5,new ItemStack(ITEM.get()));r.setParents("ICHORIUM");r.setPages(new thaumcraft.api.research.ResearchPage("ttresearch.page.CAT_AMULET.0"),MagnetResearch.page("CAT_AMULET",thaumcraft.api.research.ResearchPage.PageType.INFUSION_CRAFTING)).registerResearchItem();ThaumicTinkererModern.INFUSION_RECIPES.add(new dev.eugheist.tc4modern.InfusionItemRecipes.Recipe("CAT_AMULET","CAT_AMULET",ITEM::get,8,new AspectList().add(Aspect.DARKNESS,16).add(Aspect.ORDER,32).add(Aspect.MIND,16),()->Items.QUARTZ_BLOCK,java.util.List.of(()->KamiResources.item(KamiResources.Kind.ICHOR),()->Items.GOLD_INGOT,()->Items.GOLD_INGOT,()->Items.COCOA_BEANS,()->Items.JUNGLE_LEAVES,()->Items.COD)));}
}
