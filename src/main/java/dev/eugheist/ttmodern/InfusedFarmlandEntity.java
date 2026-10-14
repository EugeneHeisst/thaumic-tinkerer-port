package dev.eugheist.ttmodern;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.api.aspects.*;
public final class InfusedFarmlandEntity extends BlockEntity {
 private final InfusedSoilData soil=new InfusedSoilData();
 public InfusedFarmlandEntity(BlockPos pos,BlockState state){super(ThaumicTinkererModern.INFUSED_FARMLAND_ENTITY.get(),pos,state);}
 public AspectList aspects(){return soil.aspects();}
 public void fertilize(Aspect aspect){if(level==null||level.isClientSide||aspect==null)return;soil.fertilize(aspect,level.random);setChanged();level.sendBlockUpdated(worldPosition,getBlockState(),getBlockState(),3);}
 @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider registries){super.saveAdditional(tag,registries);soil.write(tag);}
 @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider registries){super.loadAdditional(tag,registries);soil.read(tag);}
 @Override public CompoundTag getUpdateTag(HolderLookup.Provider registries){return saveWithoutMetadata(registries);}
 @Override public net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket getUpdatePacket(){return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);}
}
