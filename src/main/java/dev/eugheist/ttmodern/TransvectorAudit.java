package dev.eugheist.ttmodern;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.capabilities.Capabilities;
final class TransvectorAudit {
    private static void check(boolean ok,String label){if(!ok)throw new IllegalStateException("TT PORT AUDIT FAILED: transvector "+label);}
    // Energy fixture is registered only in the explicitly opted-in isolated audit.
    static void capabilities(net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent event){
        var storage=new net.neoforged.neoforge.energy.EnergyStorage(1000);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,net.minecraft.world.level.block.entity.BlockEntityType.CHEST,(chest,side)->side==Direction.UP?storage:null);
    }
    static void run(ServerLevel level){
        var origin=new BlockPos(8,240,8);var chest=origin.east(4);var other=origin.west();
        for(var pos:new BlockPos[]{origin,chest,other})level.removeBlock(pos,false);
        level.setBlockAndUpdate(origin,ThaumicTinkererModern.INTERFACE.get().defaultBlockState());
        var tile=(TransvectorEntity)level.getBlockEntity(origin);
        check(tile!=null,"block entity exists");
        level.setBlockAndUpdate(chest,Blocks.CHEST.defaultBlockState());
        var inventory=(ChestBlockEntity)level.getBlockEntity(chest);
        check(tile.link(chest),"four-block link");check(!tile.link(origin.east(5)),"range five rejected");check(!tile.link(origin),"self rejected");
        var handler=level.getCapability(Capabilities.ItemHandler.BLOCK,origin,Direction.UP);
        check(handler!=null&&handler.getSlots()==27,"remote chest capability");
        check(handler.insertItem(0,new ItemStack(Items.DIAMOND,3),true).isEmpty()&&inventory.getItem(0).isEmpty(),"simulation does not insert");
        check(handler.insertItem(0,new ItemStack(Items.DIAMOND,3),false).isEmpty()&&inventory.getItem(0).getCount()==3,"real insert");
        check(handler.extractItem(0,2,true).getCount()==2&&inventory.getItem(0).getCount()==3,"simulate extraction");
        check(handler.extractItem(0,2,false).getCount()==2&&inventory.getItem(0).getCount()==1,"extract exactly two");
        var energy=level.getCapability(Capabilities.EnergyStorage.BLOCK,origin,Direction.UP);
        check(energy.receiveEnergy(600,true)==600&&energy.getEnergyStored()==0,"energy simulation");
        check(energy.receiveEnergy(600,false)==600&&energy.extractEnergy(200,false)==200&&energy.getEnergyStored()==400,"energy transfer");
        check(!level.getCapability(Capabilities.EnergyStorage.BLOCK,origin,Direction.DOWN).canReceive(),"energy side restriction");
        tile.camouflage(Blocks.STONE.defaultBlockState());
        var saved=tile.saveWithoutMetadata(level.registryAccess());var copy=new TransvectorEntity(origin,tile.getBlockState());copy.loadWithComponents(saved,level.registryAccess());
        check(chest.equals(copy.target())&&copy.camouflage().is(Blocks.STONE),"link and camouflage persistence");
        level.removeBlock(chest,false);
        check(energy.getEnergyStored()==0&&handler.getSlots()==0&&handler.extractItem(0,64,false).isEmpty(),"cached handler cannot access removed chest");
        level.setBlockAndUpdate(other,Blocks.CHEST.defaultBlockState());check(tile.link(other),"relink");
        check(handler.insertItem(0,new ItemStack(Items.GOLD_INGOT),false).isEmpty()&&((ChestBlockEntity)level.getBlockEntity(other)).getItem(0).is(Items.GOLD_INGOT),"cached handler follows relink");
        level.setBlockAndUpdate(chest,ThaumicTinkererModern.INTERFACE.get().defaultBlockState());check(!tile.link(chest),"interface cycle rejected");
        level.setBlockAndUpdate(other,dev.eugheist.tc4modern.Hygiene.SPA.get().defaultBlockState());check(tile.link(other),"spa link");
        var fluid=level.getCapability(Capabilities.FluidHandler.BLOCK,origin,Direction.NORTH);
        var water=new net.neoforged.neoforge.fluids.FluidStack(net.minecraft.world.level.material.Fluids.WATER,500);
        check(fluid.fill(water,net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction.SIMULATE)==500&&fluid.getFluidInTank(0).isEmpty(),"fluid simulation");
        check(fluid.fill(water,net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE)==500&&fluid.getFluidInTank(0).getAmount()==500,"fluid fill");
        check(fluid.drain(200,net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE).getAmount()==200,"fluid drain");
        check(level.getCapability(Capabilities.FluidHandler.BLOCK,origin,Direction.UP).getTanks()==0,"fluid side restriction");
        level.setBlockAndUpdate(other,dev.eugheist.tc4modern.EssentiaDevices.JAR.get().defaultBlockState());check(tile.link(other),"jar link");
        check(tile.addEssentia(thaumcraft.api.aspects.Aspect.AIR,8,Direction.UP)==8&&tile.getEssentiaAmount(Direction.UP)==8,"essentia insertion");
        check(tile.addEssentia(thaumcraft.api.aspects.Aspect.AIR,1,Direction.DOWN)==0,"essentia side restriction");
        check(tile.takeEssentia(thaumcraft.api.aspects.Aspect.AIR,3,Direction.UP)==3&&tile.getEssentiaAmount(Direction.UP)==5,"essentia extraction");
        check(fluid.getTanks()==0,"cached fluid handler follows new nonfluid target");
        for(var pos:new BlockPos[]{origin,chest,other})level.removeBlock(pos,false);
        check(thaumcraft.api.research.ResearchCategories.getResearch("INTERFACE").parents[0].equals("DARK_QUARTZ"),"research parent");
        for(String key:new String[]{"INTERFACE","INTERFACE1"}){
            var recipe=dev.eugheist.tc4modern.ArcaneRecipes.entries().stream().filter(r->r.key().equals(key)).findFirst().orElseThrow();
            var grid=recipe.ingredients().stream().map(i->i.isEmpty()?ItemStack.EMPTY:i.getItems()[0].copy()).toList();
            check(recipe.matches(grid,java.util.Set.of("INTERFACE")),"arcane recipe "+key);
            check(!recipe.matches(grid,java.util.Set.of()),"research gate "+key);
        }
        System.out.println("TT TRANSVECTOR AUDIT: ALL PASS (range, persistence, cached inventory relink, simulation, cycles, research)");
    }
}
