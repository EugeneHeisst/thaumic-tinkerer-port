package dev.eugheist.ttmodern;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import dev.eugheist.tc4modern.*;
import thaumcraft.api.aspects.Aspect;
final class EnchanterLifecycleAudit {
    private static void check(boolean ok,String label){if(!ok)throw new IllegalStateException("Enchanter lifecycle: "+label);}
    static void run(ServerLevel level){
        var pos=new BlockPos(8,250,8);var state=ThaumicTinkererModern.ENCHANTER.get().defaultBlockState();level.setBlockAndUpdate(pos,state);
        var tile=(EnchanterEntity)level.getBlockEntity(pos);var sword=new ItemStack(Items.DIAMOND_SWORD);sword.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME,net.minecraft.network.chat.Component.literal("Persisted enchantment"));tile.inventory.setStackInSlot(0,sword);
        var wand=new ItemStack(TC4Modern.GOLD_GREATWOOD_WAND.get());WandData.add(wand,Aspect.ORDER,50,true);tile.inventory.setStackInSlot(1,wand);
        var totem=net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("tc4modern","obsidian_totem"));
        for(int x:new int[]{-4,4})for(int z:new int[]{-4,0,4}){var base=pos.offset(x,0,z);level.setBlockAndUpdate(base,totem.defaultBlockState());level.setBlockAndUpdate(base.above(),totem.defaultBlockState());level.setBlockAndUpdate(base.above(2),TC4Modern.NITOR_BLOCK.get().defaultBlockState());}
        check(tile.process.change(level,sword,java.util.Set.of(),"minecraft:sharpness",1)&&tile.process.start(),"start actual entity");
        EnchanterEntity.tick(level,pos,state,tile);check(tile.process.collected(Aspect.ORDER)==1,"entity paid tick");int paid=WandData.get(wand,Aspect.ORDER);
        var saved=tile.saveWithFullMetadata(level.registryAccess());
        var restored=(EnchanterEntity)BlockEntity.loadStatic(pos,state,saved,level.registryAccess());check(restored!=null,"full metadata loads correct type");
        check(restored.saveWithFullMetadata(level.registryAccess()).getCompound("Process").equals(saved.getCompound("Process")),"save before onLoad retains pending progress");
        level.removeBlockEntity(pos);level.setBlockEntity(restored);restored.onLoad();
        check(restored.process.working()&&restored.process.collected(Aspect.ORDER)==1&&WandData.get(restored.inventory.getStackInSlot(1),Aspect.ORDER)==paid,"onLoad restores paid progress without extra charge");
        for(int i=0;i<12;i++)EnchanterEntity.tick(level,pos,state,restored);
        var output=restored.inventory.getStackInSlot(0);check(output.isEnchanted()&&output.getHoverName().getString().equals("Persisted enchantment")&&!restored.process.working(),"resume completes and preserves custom name");
        int cost=(int)(100*WandData.craftingModifier(wand,null,Aspect.ORDER));check(WandData.get(restored.inventory.getStackInSlot(1),Aspect.ORDER)==5000-12*cost,"exact total cost across reload");
        var area=new net.minecraft.world.phys.AABB(pos).inflate(2);int before=level.getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class,area).size();
        level.removeBlock(pos,false);var drops=level.getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class,area);check(drops.size()==before+2,"breaking drops both inventories once");
        check(drops.stream().filter(e->e.getItem().is(Items.DIAMOND_SWORD)).mapToInt(e->e.getItem().getCount()).sum()==1,"single enchanted sword drop");
        for(var e:drops)e.discard();
        for(int x:new int[]{-4,4})for(int z:new int[]{-4,0,4})for(int y=0;y<3;y++)level.removeBlock(pos.offset(x,y,z),false);
        System.out.println("TT ENCHANTER LIFECYCLE AUDIT: ALL PASS (full metadata, deferred load, paid resume, components, two inventory drops)");
    }
}
