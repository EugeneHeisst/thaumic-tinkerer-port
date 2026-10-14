package dev.eugheist.ttmodern;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.*;
import thaumcraft.api.aspects.Aspect;
final class InfusedFarmlandAudit {
 private static void check(boolean ok,String label){if(!ok)throw new IllegalStateException("TT infused farmland: "+label);}
 static void run(ServerLevel level){
  var pos=new BlockPos(14,280,14);var block=ThaumicTinkererModern.INFUSED_FARMLAND.get();var state=block.defaultBlockState();level.setBlockAndUpdate(pos,state);
  var soil=(InfusedFarmlandEntity)level.getBlockEntity(pos);check(soil!=null,"native registered block entity");
  for(int i=0;i<20;i++)soil.fertilize(Aspect.MAGIC);soil.fertilize(Aspect.WATER);check(soil.aspects().visSize()==20,"world fertilization cap");
  var saved=soil.saveWithFullMetadata(level.registryAccess());var restored=(InfusedFarmlandEntity)net.minecraft.world.level.block.entity.BlockEntity.loadStatic(pos,state,saved,level.registryAccess());check(restored!=null&&restored.aspects().aspects.equals(soil.aspects().aspects),"native block entity save/load");
  var clientCopy=new InfusedFarmlandEntity(pos,state);clientCopy.handleUpdateTag(soil.getUpdateTag(level.registryAccess()),level.registryAccess());check(clientCopy.aspects().aspects.equals(soil.aspects().aspects),"client update tag");
  check(state.getShape(level,pos).max(net.minecraft.core.Direction.Axis.Y)==1,"original full block height");
  block.randomTick(state,level,pos,level.random);check(level.getBlockState(pos).is(block),"dry soil persists without water");
  var player=net.neoforged.neoforge.common.util.FakePlayerFactory.getMinecraft(level);block.fallOn(level,state,pos,player,100);check(level.getBlockState(pos).is(block),"trampling disabled");
  check(block.canSustainPlant(state,level,pos,net.minecraft.core.Direction.UP,Blocks.WHEAT.defaultBlockState())==net.neoforged.neoforge.common.util.TriState.FALSE,"ordinary crops rejected");
  check(block.asItem()==net.minecraft.world.item.Items.AIR,"hidden block has no block item");level.removeBlock(pos,false);
  System.out.println("TT INFUSED FARMLAND AUDIT: ALL PASS (registered entity,world fertilization,persistence,sync,full height,no drying/trampling,exclusive crop soil,no item)");
 }
}
