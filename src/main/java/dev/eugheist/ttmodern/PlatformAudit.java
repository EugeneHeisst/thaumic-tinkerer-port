package dev.eugheist.ttmodern;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.*;
final class PlatformAudit {
 private static void check(boolean ok,String label){if(!ok)throw new IllegalStateException("TT platform: "+label);}
 static void run(ServerLevel level){
  var pos=new BlockPos(8,280,8);var block=ThaumicTinkererModern.PLATFORM.get();var state=block.defaultBlockState();level.setBlockAndUpdate(pos,state);var tile=(PlatformEntity)level.getBlockEntity(pos);var player=net.neoforged.neoforge.common.util.FakePlayerFactory.getMinecraft(level);player.getInventory().clearContent();player.setShiftKeyDown(false);player.setPos(8.5,283,8.5);player.setDeltaMovement(0,0,0);
  player.move(MoverType.SELF,new Vec3(0,-4,0));check(Math.abs(player.getY()-281)<1e-5,"real fall lands on top");player.move(MoverType.SELF,new Vec3(0,-.1,0));check(Math.abs(player.getY()-281)<1e-5,"standing support stable");
  player.setShiftKeyDown(true);player.move(MoverType.SELF,new Vec3(0,-1,0));check(player.getY()<281,"crouch descends through");player.setShiftKeyDown(false);player.setPos(8.5,278,8.5);player.move(MoverType.SELF,new Vec3(0,4,0));check(Math.abs(player.getY()-282)<1e-5,"real ascent passes from below");
  var cow=EntityType.COW.create(level);cow.setPos(8.5,283,8.5);cow.move(MoverType.SELF,new Vec3(0,-4,0));check(Math.abs(cow.getY()-281)<1e-5,"nonplayer lands from above");cow.setPos(8.5,278,8.5);cow.move(MoverType.SELF,new Vec3(0,4,0));check(Math.abs(cow.getY()-282)<1e-5,"nonplayer ascends from below");cow.discard();
  check(state.getCollisionShape(level,pos,CollisionContext.empty()).isEmpty()&&!state.isFaceSturdy(level,pos,Direction.UP),"no solid support face");
  var stack=new ItemStack(Items.STONE,3);player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,stack);var hit=new BlockHitResult(Vec3.atCenterOf(pos),Direction.UP,pos,false);block.useItemOn(stack,state,level,pos,player,net.minecraft.world.InteractionHand.MAIN_HAND,hit);check(tile.camouflage().is(Blocks.STONE)&&stack.getCount()==3,"camo uses no item");
  var restored=new PlatformEntity(pos,state);restored.loadWithComponents(tile.saveWithoutMetadata(level.registryAccess()),level.registryAccess());check(restored.camouflage().is(Blocks.STONE),"camo persisted");check(tile.getUpdateTag(level.registryAccess()).contains("Camouflage"),"camo network update");
  var furnace=new ItemStack(Items.FURNACE);block.useItemOn(furnace,state,level,pos,player,net.minecraft.world.InteractionHand.MAIN_HAND,hit);check(tile.camouflage().is(Blocks.FURNACE)&&level.getBlockEntity(pos)==tile,"ordinary model tile block allowed as appearance only");
  player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,ItemStack.EMPTY);block.useWithoutItem(state,level,pos,player,hit);check(tile.camouflage()==null,"empty hand clears camo");
  var recipe=dev.eugheist.tc4modern.ArcaneRecipes.entries().stream().filter(r->r.key().equals("PLATFORM")).findFirst().orElseThrow();check(recipe.output().getCount()==2&&recipe.cost().getAmount(thaumcraft.api.aspects.Aspect.AIR)==2&&recipe.cost().getAmount(thaumcraft.api.aspects.Aspect.ENTROPY)==4,"two platforms exact vis");var grid=new java.util.ArrayList<ItemStack>(java.util.Collections.nCopies(9,ItemStack.EMPTY));grid.set(1,new ItemStack(FocusResearch.item("silverwood_planks")));grid.set(3,new ItemStack(FocusResearch.item("greatwood_planks")));grid.set(5,new ItemStack(FocusResearch.item("greatwood_planks")));check(recipe.matches(grid,java.util.Set.of("PLATFORM"))&&!recipe.matches(grid,java.util.Set.of()),"original SGS pattern and research gate");
  var flameTool=new ItemStack(Items.DIAMOND_AXE);flameTool.enchant(level.registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).getHolderOrThrow(ToolEnchantments.FLAME),1);player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,flameTool);var speed=new net.neoforged.neoforge.event.entity.player.PlayerEvent.BreakSpeed(player,state,2,pos);net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(speed);check(!speed.isCanceled()&&speed.getNewSpeed()==Float.MAX_VALUE,"wooden platform works with Flaming Touch");
  check(thaumcraft.api.research.ResearchCategories.getResearch("PLATFORM").parents[0].equals("CLEANSING_TALISMAN"),"progression parent");player.setPos(0,200,0);player.getInventory().clearContent();level.removeBlock(pos,false);System.out.println("TT PLATFORM AUDIT: ALL PASS (real player/mob landing and ascent,stable support,crouch descent,no solid face,camo conservation/persistence/clear,recipe/research)");
 }
}
