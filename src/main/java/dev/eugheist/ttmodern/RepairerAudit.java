package dev.eugheist.ttmodern;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.*;
import thaumcraft.api.aspects.*;
final class RepairerAudit {
    private static void check(boolean ok,String label){if(!ok)throw new IllegalStateException("TT PORT AUDIT FAILED: repairer "+label);}
    static void run(ServerLevel level){
        var pos=new BlockPos(8,260,8);var source=pos.north();level.removeBlock(pos,false);level.removeBlock(source,false);
        level.setBlockAndUpdate(pos,ThaumicTinkererModern.REPAIRER.get().defaultBlockState());
        level.setBlockAndUpdate(source,dev.eugheist.tc4modern.EssentiaTubes.TUBE.get().defaultBlockState());
        var tile=(RepairerEntity)level.getBlockEntity(pos);var tube=(dev.eugheist.tc4modern.EssentiaTubeEntity)level.getBlockEntity(source);
        var tool=new ItemStack(Items.DIAMOND_PICKAXE);tool.setDamageValue(30);tool.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME,net.minecraft.network.chat.Component.literal("Restorer audit"));tile.inventory.setStackInSlot(0,tool);
        check(!tile.inventory.isItemValid(0,new ItemStack(Items.DIAMOND)),"reject nonrepairable item");
        for(var aspect:new Aspect[]{Aspect.TOOL,Aspect.CRAFT,Aspect.ORDER}){
            tube.state.suctionType=aspect;tube.state.suction=127;tube.state.essentiaType=aspect;tube.state.essentiaAmount=1;
            int old=tool.getDamageValue();tile.ticks=9;RepairerEntity.tick(level,pos,tile.getBlockState(),tile);
            check(tool.getDamageValue()==old-(aspect==Aspect.TOOL?8:aspect==Aspect.CRAFT?5:3)&&tube.state.essentiaAmount==0,"one unit repair cost "+aspect.getTag());
        }
        tube.state.suctionType=Aspect.TOOL;tube.state.suction=128;tube.state.essentiaType=Aspect.TOOL;tube.state.essentiaAmount=1;int damage=tool.getDamageValue();tile.ticks=9;RepairerEntity.tick(level,pos,tile.getBlockState(),tile);check(tool.getDamageValue()==damage&&tube.state.essentiaAmount==1,"higher suction preserved");
        tube.state.suction=127;tool.setDamageValue(2);tile.ticks=9;RepairerEntity.tick(level,pos,tile.getBlockState(),tile);check(tool.getDamageValue()==0,"repair clamps zero");
        tube.state.essentiaAmount=1;tile.ticks=9;RepairerEntity.tick(level,pos,tile.getBlockState(),tile);check(tube.state.essentiaAmount==1,"undamaged consumes no essentia");
        check(tool.getHoverName().getString().equals("Restorer audit"),"custom components retained");
        var saved=tile.saveWithoutMetadata(level.registryAccess());var copy=new RepairerEntity(pos,tile.getBlockState());copy.loadWithComponents(saved,level.registryAccess());check(ItemStack.isSameItemSameComponents(copy.inventory.getStackInSlot(0),tool),"item persistence");
        tool.setDamageValue(1);check(tile.getBlockState().getDirectSignal(level,pos,Direction.UP)==15,"damaged signal 15");tool.setDamageValue(0);check(tile.getBlockState().getDirectSignal(level,pos,Direction.UP)==0,"repaired signal zero");
        tile.inventory.setStackInSlot(0,ItemStack.EMPTY);level.removeBlock(pos,false);level.removeBlock(source,false);
        var recipe=dev.eugheist.tc4modern.InfusionItemRecipes.all().stream().filter(r->r.key().equals("REPAIRER")).findFirst().orElseThrow();check(recipe.central().get()!=Items.AIR&&recipe.components().stream().allMatch(s->s.get()!=Items.AIR),"infusion ingredients resolve");
        System.out.println("TT REPAIRER AUDIT: ALL PASS (three repair rates, suction, conservation, components, persistence, signal)");
    }
}
