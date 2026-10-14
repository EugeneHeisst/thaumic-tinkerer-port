package dev.eugheist.ttmodern;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
public final class PlatformEntity extends BlockEntity implements MovableTile {
 private BlockState camouflage;
 public PlatformEntity(BlockPos pos,BlockState state){super(ThaumicTinkererModern.PLATFORM_ENTITY.get(),pos,state);}
 public BlockState camouflage(){return camouflage;}
 public void camouflage(BlockState state){camouflage=state;setChanged();if(level!=null)level.sendBlockUpdated(worldPosition,getBlockState(),getBlockState(),3);}
 @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider registries){super.saveAdditional(tag,registries);if(camouflage!=null)tag.put("Camouflage",net.minecraft.nbt.NbtUtils.writeBlockState(camouflage));}
 @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider registries){super.loadAdditional(tag,registries);camouflage=tag.contains("Camouflage")?net.minecraft.nbt.NbtUtils.readBlockState(registries.lookupOrThrow(net.minecraft.core.registries.Registries.BLOCK),tag.getCompound("Camouflage")):null;}
 @Override public CompoundTag getUpdateTag(HolderLookup.Provider registries){return saveWithoutMetadata(registries);}
 @Override public net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket getUpdatePacket(){return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);}
}
