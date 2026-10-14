package dev.eugheist.ttmodern;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Transfer payload for the dislocation focus. Permissions and vis belong to the focus caller. */
final class DislocationStorage {
    static boolean available(CompoundTag payload){return payload.getBoolean("available");}
    static boolean movable(ServerLevel level,BlockPos pos){
        if(level.isOutsideBuildHeight(pos)||!level.hasChunkAt(pos))return false;
        var state=level.getBlockState(pos);
        return !state.isAir()&&!state.is(Blocks.MOVING_PISTON)&&!state.is(Blocks.PISTON_HEAD)
            &&state.getDestroySpeed(level,pos)>=0&&!dev.eugheist.tc4modern.PortableHoleConfig.blocked(state)
            &&!(level.getBlockEntity(pos) instanceof dev.eugheist.tc4modern.AuraNodeEntity);
    }
    static boolean pickup(ServerLevel level,BlockPos pos,CompoundTag payload){
        if(available(payload)||!movable(level,pos))return false;
        var state=level.getBlockState(pos);var tile=level.getBlockEntity(pos);
        var saved=new CompoundTag();saved.put("state",NbtUtils.writeBlockState(state));
        if(tile!=null)saved.put("tileCmp",tile.saveWithFullMetadata(level.registryAccess()));
        // Detach before changing state: container onRemove must never spill its inventory.
        level.removeBlockEntity(pos);
        if(!level.setBlock(pos,Blocks.AIR.defaultBlockState(),Block.UPDATE_CLIENTS|Block.UPDATE_KNOWN_SHAPE)){
            if(tile!=null)level.setBlockEntity(tile);return false;
        }
        payload.put("dislocation",saved);payload.putBoolean("available",true);
        level.invalidateCapabilities(pos);Blocks.AIR.defaultBlockState().updateNeighbourShapes(level,pos,Block.UPDATE_ALL);level.updateNeighborsAt(pos,state.getBlock());return true;
    }
    static boolean place(ServerLevel level,BlockPos pos,CompoundTag payload){return place(level,pos,payload,()->true);}
    static boolean place(ServerLevel level,BlockPos pos,CompoundTag payload,java.util.function.BooleanSupplier accept){return place(level,pos,payload,accept,java.util.function.UnaryOperator.identity(),state->{});}
    static boolean place(ServerLevel level,BlockPos pos,CompoundTag payload,java.util.function.BooleanSupplier accept,java.util.function.UnaryOperator<BlockState> orientation,java.util.function.Consumer<BlockState> placed){
        if(!available(payload)||level.isOutsideBuildHeight(pos)||!level.hasChunkAt(pos)||!level.getBlockState(pos).canBeReplaced()||level.getBlockEntity(pos)!=null)return false;
        var saved=payload.getCompound("dislocation");
        if(!saved.contains("state",10))return false;
        BlockState state=NbtUtils.readBlockState(level.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.BLOCK),saved.getCompound("state"));
        if(state.isAir())return false;state=orientation.apply(state);
        BlockEntity restored=null;
        if(saved.contains("tileCmp",10)){
            var data=saved.getCompound("tileCmp").copy();data.putInt("x",pos.getX());data.putInt("y",pos.getY());data.putInt("z",pos.getZ());
            restored=BlockEntity.loadStatic(pos,state,data,level.registryAccess());
            if(restored==null||!restored.getType().isValid(state))return false;
        }
        var previous=level.getBlockState(pos);
        if(!level.setBlock(pos,state,Block.UPDATE_CLIENTS|Block.UPDATE_KNOWN_SHAPE))return false;
        placed.accept(state);
        if(restored!=null){level.removeBlockEntity(pos);level.setBlockEntity(restored);restored.setChanged();}
        if(!accept.getAsBoolean()){
            level.removeBlockEntity(pos);level.setBlock(pos,previous,Block.UPDATE_CLIENTS|Block.UPDATE_KNOWN_SHAPE);level.invalidateCapabilities(pos);return false;
        }
        // Consume before neighbour callbacks: callbacks must not see a second usable payload.
        payload.remove("dislocation");payload.putBoolean("available",false);
        level.invalidateCapabilities(pos);level.sendBlockUpdated(pos,state,state,3);state.updateNeighbourShapes(level,pos,Block.UPDATE_ALL);level.updateNeighborsAt(pos,state.getBlock());return true;
    }
}
