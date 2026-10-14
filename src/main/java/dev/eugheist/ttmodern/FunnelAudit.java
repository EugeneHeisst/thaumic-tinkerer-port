package dev.eugheist.ttmodern;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.*;
import net.minecraft.world.item.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import thaumcraft.api.aspects.*;
final class FunnelAudit {
    private static void check(boolean ok,String label){if(!ok)throw new IllegalStateException("TT PORT AUDIT FAILED: funnel "+label);}
    static void run(ServerLevel level){
        var pos=new BlockPos(8,250,8);var hopper=pos.below();var target=hopper.east();
        for(var p:new BlockPos[]{pos,hopper,target})level.removeBlock(p,false);
        level.setBlockAndUpdate(hopper,Blocks.HOPPER.defaultBlockState().setValue(HopperBlock.FACING,Direction.EAST));
        level.setBlockAndUpdate(target,dev.eugheist.tc4modern.EssentiaDevices.JAR.get().defaultBlockState());
        level.setBlockAndUpdate(pos,ThaumicTinkererModern.FUNNEL.get().defaultBlockState());
        var tile=(FunnelEntity)level.getBlockEntity(pos);var jar=(dev.eugheist.tc4modern.WardedJarEntity)level.getBlockEntity(target);
        var item=new ItemStack(dev.eugheist.tc4modern.EssentiaDevices.JAR.get());var aspects=new AspectList().add(Aspect.AIR,4);
        CustomData.update(DataComponents.CUSTOM_DATA,item,aspects::writeToNBT);tile.inventory.setStackInSlot(0,item);
        FunnelEntity.tick(level,pos,tile.getBlockState(),tile);check(jar.state.amount==1&&jar.state.aspect==Aspect.AIR,"one unit into target jar");
        var left=new AspectList();left.readFromNBT(item.get(DataComponents.CUSTOM_DATA).copyTag());check(left.getAmount(Aspect.AIR)==3,"source debited exactly one");
        jar.state.aspectFilter=Aspect.FIRE;jar.state.aspect=null;jar.state.amount=0;FunnelEntity.tick(level,pos,tile.getBlockState(),tile);check(jar.state.amount==0,"target label respected");
        jar.state.aspectFilter=null;jar.state.aspect=Aspect.AIR;jar.state.amount=64;FunnelEntity.tick(level,pos,tile.getBlockState(),tile);check(jar.state.amount==64,"full jar preserved");
        check(level.getCapability(net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK,pos,Direction.DOWN)==null,"bottom inaccessible");
        check(!tile.inventory.isItemValid(0,new ItemStack(Items.DIAMOND)),"automation only accepts jars");
        var copy=new FunnelEntity(pos,tile.getBlockState());copy.loadWithComponents(tile.saveWithoutMetadata(level.registryAccess()),level.registryAccess());check(ItemStack.isSameItemSameComponents(copy.inventory.getStackInSlot(0),item),"jar state saved");
        // Empty fixture slot before dismantling, so the audit leaves no item entity behind.
        tile.inventory.setStackInSlot(0,ItemStack.EMPTY);for(var p:new BlockPos[]{pos,hopper,target})level.removeBlock(p,false);
        check(thaumcraft.api.research.ResearchCategories.getResearch("FUNNEL").parents[0].equals("BRIGHT_NITOR"),"research parent");
        System.out.println("TT FUNNEL AUDIT: ALL PASS (one-unit conservation, label/full jar, sided inventory, persistence)");
    }
}
