package dev.eugheist.ttmodern;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.api.aspects.AspectList;
public final class InfusedGrainEntity extends BlockEntity {
 private InfusedSeedData data=new InfusedSeedData(null,new AspectList());
 private static final Direction[] DIRECTIONS={Direction.DOWN,Direction.UP,Direction.NORTH,Direction.SOUTH,Direction.WEST,Direction.EAST};
 public InfusedGrainEntity(BlockPos pos,BlockState state){super(ThaumicTinkererModern.INFUSED_GRAIN_ENTITY.get(),pos,state);}
 InfusedSeedData data(){return new InfusedSeedData(data.mainAspect(),data.tendencies());}
 void data(InfusedSeedData value){data=new InfusedSeedData(value.mainAspect(),value.tendencies());setChanged();if(level!=null&&!level.isClientSide){var state=getBlockState();if(state.getBlock() instanceof InfusedGrainBlock)level.setBlock(worldPosition,state.setValue(InfusedGrainBlock.ASPECT,InfusedGrainBlock.texture(data.mainAspect())),2);level.sendBlockUpdated(worldPosition,getBlockState(),getBlockState(),3);}}
 static void tick(ServerLevel level,BlockPos pos,BlockState state,InfusedGrainEntity crop){
  int age=state.getValue(CropBlock.AGE);int next=InfusedCropProcess.grow(crop.data,age,level.getMaxLocalRawBrightness(pos.above()),level.random);
  if(next!=age)level.setBlock(pos,state.setValue(CropBlock.AGE,next),2);
  var neighbors=new java.util.ArrayList<InfusedSeedData>();var tiles=new java.util.ArrayList<InfusedGrainEntity>();
  for(var direction:DIRECTIONS){var neighborPos=pos.relative(direction);var tile=level.hasChunkAt(neighborPos)&&level.getBlockEntity(neighborPos) instanceof InfusedGrainEntity c?c:null;tiles.add(tile);neighbors.add(tile==null?null:tile.data);}
  var transfer=InfusedCropProcess.transfer(crop.data,neighbors,level.random);
  if(transfer!=null){crop.data(transfer.recipient());tiles.get(transfer.neighbor()).data(transfer.donor());}
 }
 @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider registries){super.saveAdditional(tag,registries);data.write(tag);}
 @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider registries){super.loadAdditional(tag,registries);data=InfusedSeedData.read(tag);}
 @Override public CompoundTag getUpdateTag(HolderLookup.Provider registries){return saveWithoutMetadata(registries);}
 @Override public net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket getUpdatePacket(){return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);}
}
