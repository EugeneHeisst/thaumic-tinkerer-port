package dev.eugheist.ttmodern;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.AABB;
final class DislocatorAudit{
    static void check(boolean ok,String label){if(!ok)throw new IllegalStateException("TT PORT AUDIT FAILED: dislocator "+label);}
    static void run(ServerLevel level){
        var pos=new BlockPos(8,280,8);var near=pos.east();var far=pos.east(6);
        for(var at:new BlockPos[]{pos,near,far}){level.removeBlockEntity(at);level.removeBlock(at,false);}
        level.setBlockAndUpdate(pos,ThaumicTinkererModern.DISLOCATOR.get().defaultBlockState().setValue(DislocatorBlock.FACING,Direction.EAST));var tile=(DislocatorEntity)level.getBlockEntity(pos);
        check(tile.inRange(pos.east(16))&&!tile.inRange(pos.east(17))&&!tile.inRange(pos),"sixteen-axis bounds");check(tile.link(far),"ordinary blocks link");
        level.setBlockAndUpdate(near,Blocks.CHEST.defaultBlockState());level.setBlockAndUpdate(far,Blocks.CHEST.defaultBlockState());
        var diamonds=new ItemStack(Items.DIAMOND,23);diamonds.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME,net.minecraft.network.chat.Component.literal("Retain components"));
        ((ChestBlockEntity)level.getBlockEntity(near)).setItem(0,diamonds);((ChestBlockEntity)level.getBlockEntity(far)).setItem(5,new ItemStack(Items.GOLD_INGOT,17));
        var dropped=new ItemEntity(level,far.getX()+.5,far.getY()+.5,far.getZ()+.5,new ItemStack(Items.APPLE));level.addFreshEntity(dropped);
        tile.pulse();
        var nearChest=(ChestBlockEntity)level.getBlockEntity(near);var farChest=(ChestBlockEntity)level.getBlockEntity(far);
        check(nearChest.getItem(5).is(Items.GOLD_INGOT)&&nearChest.getItem(5).getCount()==17&&nearChest.getItem(0).isEmpty(),"same-block tile inventory swap");
        check(ItemStack.isSameItemSameComponents(farChest.getItem(0),diamonds)&&farChest.getItem(0).getCount()==23&&farChest.getItem(5).isEmpty(),"components and count retained");
        check(farChest.getBlockPos().equals(far)&&nearChest.getBlockPos().equals(near),"tile coordinates rewritten");check(dropped.position().x==near.getX()+.5,"entity translated");
        check(level.getEntitiesOfClass(ItemEntity.class,new AABB(near).inflate(1)).size()==1,"no inventory spill");dropped.discard();
        tile.pulse();tile.pulse();check(tile.pulseStored&&tile.cooldown==10,"pulses coalesce while cooling down");
        for(int i=0;i<10;i++)DislocatorEntity.tick(level,pos,tile.getBlockState(),tile);
        check(!tile.pulseStored&&tile.cooldown==10&&((ChestBlockEntity)level.getBlockEntity(near)).getItem(0).getCount()==23,"one deferred pulse after ten ticks");
        var saved=tile.saveWithoutMetadata(level.registryAccess());var copy=new DislocatorEntity(pos,tile.getBlockState());copy.loadWithComponents(saved,level.registryAccess());check(copy.target().equals(far)&&copy.cooldown==10,"link and cooldown persisted");
        level.removeBlockEntity(near);level.setBlockAndUpdate(near,Blocks.BEDROCK.defaultBlockState());tile.cooldown=0;tile.pulse();check(level.getBlockState(near).is(Blocks.BEDROCK),"unbreakable blocks never swapped");
        check(DislocatorEntity.movable(level,pos.above()),"air is movable");
        level.setBlockAndUpdate(near,Blocks.STONE.defaultBlockState());tile.cooldown=0;tile.pulse();
        check(level.getBlockEntity(near) instanceof ChestBlockEntity&&((ChestBlockEntity)level.getBlockEntity(near)).getItem(5).getCount()==17&&level.getBlockState(far).is(Blocks.STONE),"different-block swap preserves chest");
        level.removeBlock(far,false);tile.cooldown=0;tile.pulse();
        check(level.getBlockState(near).isAir()&&level.getBlockEntity(far) instanceof ChestBlockEntity&&((ChestBlockEntity)level.getBlockEntity(far)).getItem(5).getCount()==17,"swap with empty space preserves chest");
        tile.cooldown=0;level.setBlockAndUpdate(pos.west(),Blocks.REDSTONE_BLOCK.defaultBlockState());check(tile.getBlockState().getValue(DislocatorBlock.POWERED),"redstone rising edge latched");
        var block=(DislocatorBlock)tile.getBlockState().getBlock();block.tick(tile.getBlockState(),level,pos,level.random);check(level.getBlockEntity(near) instanceof ChestBlockEntity&&tile.cooldown==10,"scheduled pulse exchanges blocks");
        block.neighborChanged(tile.getBlockState(),level,pos,Blocks.REDSTONE_BLOCK,pos.west(),false);check(!tile.pulseStored,"steady power generates no second pulse");
        level.removeBlock(pos.west(),false);check(!tile.getBlockState().getValue(DislocatorBlock.POWERED),"falling edge resets latch");
        check(!tile.link(pos),"self-link rejected");
        var recipe=dev.eugheist.tc4modern.ArcaneRecipes.entries().stream().filter(r->r.key().equals("DISLOCATOR")).findFirst().orElseThrow();check(recipe.ingredients().stream().allMatch(i->!i.isEmpty()&&i.getItems().length>0&&!i.getItems()[0].is(Items.AIR)),"recipe resolves");
        for(var at:new BlockPos[]{pos,near,far}){level.removeBlockEntity(at);level.removeBlock(at,false);}
        System.out.println("TT DISLOCATOR AUDIT: ALL PASS (range, same-block inventory swap, components, entity translation, cooldown, persistence, immovable blocks, recipes)");
    }
}
