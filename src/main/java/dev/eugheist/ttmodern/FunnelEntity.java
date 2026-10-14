package dev.eugheist.ttmodern;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import thaumcraft.api.aspects.AspectList;
public final class FunnelEntity extends BlockEntity implements MovableTile {
    public final ItemStackHandler inventory=new ItemStackHandler(1){
        @Override public int getSlotLimit(int slot){return 1;}
        @Override public boolean isItemValid(int slot,ItemStack stack){return stack.getItem() instanceof dev.eugheist.tc4modern.WardedJarBlock.JarItem;}
        @Override protected void onContentsChanged(int slot){changed();}
    };
    public FunnelEntity(BlockPos pos,BlockState state){super(ThaumicTinkererModern.FUNNEL_ENTITY.get(),pos,state);}
    void changed(){setChanged();if(level!=null)level.sendBlockUpdated(worldPosition,getBlockState(),getBlockState(),3);}
    public static void tick(Level level,BlockPos pos,BlockState state,FunnelEntity tile){
        if(level.isClientSide)return;
        var stack=tile.inventory.getStackInSlot(0);if(!tile.inventory.isItemValid(0,stack))return;
        var aspects=new AspectList();aspects.readFromNBT(stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag());
        if(aspects.size()!=1)return;var aspect=aspects.getAspects()[0];if(aspects.getAmount(aspect)<=0)return;
        var hopper=pos.below();if(!level.hasChunkAt(hopper)||!level.getBlockState(hopper).hasProperty(HopperBlock.FACING)||!(level.getBlockEntity(hopper) instanceof HopperBlockEntity))return;
        var target=hopper.relative(level.getBlockState(hopper).getValue(HopperBlock.FACING));if(!level.hasChunkAt(target))return;
        if(!(level.getBlockEntity(target) instanceof dev.eugheist.tc4modern.WardedJarEntity jar))return;
        if(jar.state.aspectFilter!=null&&jar.state.aspectFilter!=aspect)return;
        if(jar.state.amount>0&&jar.state.aspect!=aspect)return;
        // Original funnel transfers one unit per tick directly into the jar, including void jars.
        int accepted=1-jar.state.addToContainer(aspect,1);if(accepted!=1)return;
        aspects.remove(aspect,1);CustomData.update(DataComponents.CUSTOM_DATA,stack,aspects::writeToNBT);jar.changed();tile.changed();
    }
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider registries){super.saveAdditional(tag,registries);tag.put("Inventory",inventory.serializeNBT(registries));}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider registries){super.loadAdditional(tag,registries);inventory.deserializeNBT(registries,tag.getCompound("Inventory"));}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider registries){return saveWithoutMetadata(registries);}
    @Override public net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket getUpdatePacket(){return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);}
}
