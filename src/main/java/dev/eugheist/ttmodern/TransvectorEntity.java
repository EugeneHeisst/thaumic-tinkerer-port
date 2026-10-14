package dev.eugheist.ttmodern;

import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;

/** Build 164 range is four blocks per axis. Links never load a chunk. */
public final class TransvectorEntity extends BlockEntity implements MovableTile, TransvectorLink, thaumcraft.api.aspects.IEssentiaTransport {
    private BlockPos target;
    private BlockState camouflage;
    public BlockState camouflage(){return camouflage;}
    public void camouflage(BlockState state){camouflage=state;setChanged();if(level!=null)level.sendBlockUpdated(worldPosition,getBlockState(),getBlockState(),3);}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider registries){return saveWithoutMetadata(registries);}
    @Override public net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket getUpdatePacket(){return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);}

    private boolean resolving;
    public TransvectorEntity(BlockPos pos,BlockState state){super(ThaumicTinkererModern.TRANSVECTOR_ENTITY.get(),pos,state);}
    public BlockPos target(){return target;}
    public boolean inRange(BlockPos pos){return !pos.equals(worldPosition)&&Math.abs((long)pos.getX()-worldPosition.getX())<=4&&Math.abs((long)pos.getY()-worldPosition.getY())<=4&&Math.abs((long)pos.getZ()-worldPosition.getZ())<=4;}
    public boolean link(BlockPos pos){
        if(level==null||!inRange(pos)||!level.hasChunkAt(pos))return false;
        var tile=level.getBlockEntity(pos);
        if(tile==null||tile instanceof TransvectorEntity)return false;
        target=pos.immutable();setChanged();level.invalidateCapabilities(worldPosition);return true;
    }
    public <T,C> T forward(BlockCapability<T,C> capability,C context){
        if(resolving||level==null||target==null||!inRange(target)||!level.hasChunkAt(target))return null;
        var tile=level.getBlockEntity(target);
        if(tile==null||tile instanceof TransvectorEntity)return null;
        resolving=true;
        try{return level.getCapability(capability,target,context);}finally{resolving=false;}
    }
    private thaumcraft.api.aspects.IEssentiaTransport essentia(){
        if(level==null||target==null||!inRange(target)||!level.hasChunkAt(target))return null;
        var tile=level.getBlockEntity(target);
        return tile instanceof thaumcraft.api.aspects.IEssentiaTransport transport && !(tile instanceof TransvectorEntity)?transport:null;
    }
    public boolean isConnectable(Direction face){var t=essentia();return t!=null&&t.isConnectable(face);}
    public boolean canInputFrom(Direction face){var t=essentia();return t!=null&&t.canInputFrom(face);}
    public boolean canOutputTo(Direction face){var t=essentia();return t!=null&&t.canOutputTo(face);}
    public void setSuction(thaumcraft.api.aspects.Aspect aspect,int amount){var t=essentia();if(t!=null)t.setSuction(aspect,amount);}
    public thaumcraft.api.aspects.Aspect getSuctionType(Direction face){var t=essentia();return t==null?null:t.getSuctionType(face);}
    public int getSuctionAmount(Direction face){var t=essentia();return t==null?0:t.getSuctionAmount(face);}
    public int takeEssentia(thaumcraft.api.aspects.Aspect aspect,int amount,Direction face){var t=essentia();return t==null?0:t.takeEssentia(aspect,amount,face);}
    public int addEssentia(thaumcraft.api.aspects.Aspect aspect,int amount,Direction face){var t=essentia();return t==null?0:t.addEssentia(aspect,amount,face);}
    public thaumcraft.api.aspects.Aspect getEssentiaType(Direction face){var t=essentia();return t==null?null:t.getEssentiaType(face);}
    public int getEssentiaAmount(Direction face){var t=essentia();return t==null?0:t.getEssentiaAmount(face);}
    public int getMinimumSuction(){var t=essentia();return t==null?0:t.getMinimumSuction();}
    public boolean renderExtendedTube(){return false;}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider registries){super.saveAdditional(tag,registries);if(target!=null)tag.putLong("Target",target.asLong());if(camouflage!=null)tag.put("Camouflage",net.minecraft.nbt.NbtUtils.writeBlockState(camouflage));}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider registries){super.loadAdditional(tag,registries);target=tag.contains("Target")?BlockPos.of(tag.getLong("Target")):null;camouflage=tag.contains("Camouflage")?net.minecraft.nbt.NbtUtils.readBlockState(registries.lookupOrThrow(net.minecraft.core.registries.Registries.BLOCK),tag.getCompound("Camouflage")):null;}
}
