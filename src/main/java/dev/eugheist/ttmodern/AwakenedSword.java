package dev.eugheist.ttmodern;
import net.minecraft.world.item.*;
import thaumcraft.api.aspects.*;
public final class AwakenedSword extends IchorSword implements AdvancedTool {
 static final net.neoforged.neoforge.registries.DeferredItem<AwakenedSword> ITEM=KamiRegistries.ITEMS.register("ichor_sword_gem",AwakenedSword::new);
 public AwakenedSword(){super();}static void init(){}
 @Override public net.minecraft.world.InteractionResult useOn(net.minecraft.world.item.context.UseOnContext context){return net.minecraft.world.InteractionResult.PASS;}
 @Override public String toolType(){return "sword";}
 @Override public boolean isFoil(ItemStack stack){return true;}
 @Override public net.minecraft.world.InteractionResultHolder<ItemStack> use(net.minecraft.world.level.Level level,net.minecraft.world.entity.player.Player p,net.minecraft.world.InteractionHand hand){var stack=p.getItemInHand(hand);if(p.isShiftKeyDown()){AdvancedToolModes.change(stack);if(level.isClientSide)AdvancedToolHud.show(AdvancedToolModes.description("sword",stack).getString());return net.minecraft.world.InteractionResultHolder.sidedSuccess(stack,level.isClientSide);}return super.use(level,p,hand);}
 @Override public void appendHoverText(ItemStack stack,Item.TooltipContext context,java.util.List<net.minecraft.network.chat.Component> lines,TooltipFlag flags){super.appendHoverText(stack,context,lines,flags);lines.add(AdvancedToolModes.description("sword",stack));}
 private boolean attackingGroup;
 @Override public boolean onLeftClickEntity(ItemStack stack,net.minecraft.world.entity.player.Player player,net.minecraft.world.entity.Entity target){
  if(!attackingGroup&&target instanceof net.minecraft.world.entity.LivingEntity living&&living.hurtTime==0&&living.isAlive()){
   int mode=AdvancedToolModes.get(stack);
   if(mode==1){
    var box=new net.minecraft.world.phys.AABB(target.getX()-3,target.getY()-3,target.getZ()-3,target.getX()+3,target.getY()+3,target.getZ()+3);
    var targets=player.level().getEntitiesOfClass(target.getClass(),box);
    attackingGroup=true;try{for(var nearby:targets)player.attack(nearby);}finally{attackingGroup=false;}
   }else if(mode==2){living.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.DAMAGE_RESISTANCE,1,1));SoulHearts.add(player);}
  }
  return super.onLeftClickEntity(stack,player,target);
 }
 static void research(){var r=new KamiResearch("ICHOR_SWORD_GEM",new AspectList().add(Aspect.AIR,2).add(Aspect.WEAPON,1).add(Aspect.SOUL,1).add(Aspect.HUNGER,1),16,12,5,new ItemStack(ITEM.get()));r.setParents("ICHOR_TOOLS");r.setPages(new thaumcraft.api.research.ResearchPage("ttresearch.page.ICHOR_SWORD_GEM.0"),MagnetResearch.page("ICHOR_SWORD_GEM",thaumcraft.api.research.ResearchPage.PageType.INFUSION_CRAFTING),new thaumcraft.api.research.ResearchPage("ttresearch.page.ICHOR_SWORD_GEM.1")).registerResearchItem();ThaumicTinkererModern.INFUSION_RECIPES.add(new dev.eugheist.tc4modern.InfusionItemRecipes.Recipe("ICHOR_SWORD_GEM","ICHOR_SWORD_GEM",ITEM::get,15,new AspectList().add(Aspect.AIR,50).add(Aspect.HUNGER,64).add(Aspect.SOUL,32).add(Aspect.WEAPON,32).add(Aspect.ENERGY,32).add(Aspect.ORDER,16).add(Aspect.CRYSTAL,16),()->KamiTools.SWORD.get(),java.util.List.of(()->KamiResources.item(KamiResources.Kind.ICHORIUM),()->KamiResources.item(KamiResources.Kind.ICHOR),()->FocusResearch.item("elemental_sword"),()->FocusResearch.item("focus_frost"),()->Items.CACTUS,()->FocusResearch.item("cinnabar_cluster"),()->FocusResearch.item("iron_cluster"),()->FocusResearch.item("gold_cluster"),()->Items.DIAMOND,()->FocusResearch.item("focus_frost"),()->FocusResearch.item("elemental_sword"),()->KamiResources.item(KamiResources.Kind.CLOTH))));}
}
