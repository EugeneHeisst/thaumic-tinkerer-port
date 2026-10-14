package dev.eugheist.ttmodern;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.phys.*;
import thaumcraft.api.aspects.*;
final class InfusedSeedsAudit {
 private static void check(boolean ok,String label){if(!ok)throw new IllegalStateException("TT infused seeds: "+label);}
 static void run(ServerLevel level){
  var pos=new BlockPos(14,280,14);var player=net.neoforged.neoforge.common.util.FakePlayerFactory.getMinecraft(level);player.getInventory().clearContent();player.setPos(14.5,281,14.5);
  var seed=InfusedSeedsItem.stack(Aspect.MAGIC);seed.setCount(3);new InfusedSeedData(Aspect.MAGIC,new AspectList().add(Aspect.WATER,12)).write(seed);player.setItemInHand(InteractionHand.MAIN_HAND,seed);
  var context=new UseOnContext(player,InteractionHand.MAIN_HAND,new BlockHitResult(Vec3.atCenterOf(pos),Direction.UP,pos,false));
  level.setBlockAndUpdate(pos,Blocks.FARMLAND.defaultBlockState());level.removeBlock(pos.above(),false);
  check(seed.getItem().useOn(context).consumesAction(),"native use on farmland");check(seed.getCount()==2&&level.getBlockState(pos).is(ThaumicTinkererModern.INFUSED_FARMLAND.get()),"soil conversion and exactly one seed debit");
  var crop=(InfusedGrainEntity)level.getBlockEntity(pos.above());check(crop.data().mainAspect()==Aspect.MAGIC&&crop.data().amount(Aspect.WATER)==12,"seed traits copied to crop");var cloned=level.getBlockState(pos.above()).getCloneItemStack(new BlockHitResult(Vec3.atCenterOf(pos.above()),Direction.UP,pos.above(),false),level,pos.above(),player);check(cloned.is(ThaumicTinkererModern.INFUSED_SEEDS.get())&&InfusedSeedData.read(cloned).amount(Aspect.WATER)==12,"pick block traits");
  check(seed.getItem().useOn(context)==InteractionResult.FAIL&&seed.getCount()==2&&level.getBlockEntity(pos.above())==crop,"occupied crop never overwritten");
  level.removeBlock(pos.above(),false);var soil=(InfusedFarmlandEntity)level.getBlockEntity(pos);soil.fertilize(Aspect.LIGHT);check(seed.getItem().useOn(context).consumesAction()&&level.getBlockEntity(pos)==soil&&soil.aspects().getAmount(Aspect.LIGHT)==1,"replant keeps soil reservoir");
  level.removeBlock(pos.above(),false);level.setBlockAndUpdate(pos.above(),Blocks.CHEST.defaultBlockState());check(seed.getItem().useOn(context)==InteractionResult.FAIL&&level.getBlockState(pos.above()).is(Blocks.CHEST)&&seed.getCount()==1,"occupied tile safe");
  var tooltip=new java.util.ArrayList<net.minecraft.network.chat.Component>();seed.getItem().appendHoverText(new ItemStack(seed.getItem()),Item.TooltipContext.EMPTY,tooltip,TooltipFlag.NORMAL);check(seed.getItem().isFoil(seed),"generic tooltip safe and original foil");
  player.getInventory().clearContent();level.removeBlock(pos.above(),false);level.removeBlock(pos,false);System.out.println("TT INFUSED SEEDS AUDIT: ALL PASS (native planting,soil conversion,one debit,traits/pick block,occupied blocks safe,replant reservoir,generic tooltip)");
 }
}
