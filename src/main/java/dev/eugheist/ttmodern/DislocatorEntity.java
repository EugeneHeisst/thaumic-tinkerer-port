package dev.eugheist.ttmodern;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
public final class DislocatorEntity extends BlockEntity implements MovableTile, TransvectorLink{
    private BlockPos target;
    private BlockState camouflage;
    int cooldown;boolean pulseStored;
    public DislocatorEntity(BlockPos pos,BlockState state){super(ThaumicTinkererModern.DISLOCATOR_ENTITY.get(),pos,state);}
    public BlockPos target(){return target;}
    public BlockState camouflage(){return camouflage;}
    public void camouflage(BlockState state){camouflage=state;changed();}
    void changed(){setChanged();if(level!=null)level.sendBlockUpdated(worldPosition,getBlockState(),getBlockState(),3);}
    public boolean inRange(BlockPos pos){return !pos.equals(worldPosition)&&Math.abs((long)pos.getX()-worldPosition.getX())<=16&&Math.abs((long)pos.getY()-worldPosition.getY())<=16&&Math.abs((long)pos.getZ()-worldPosition.getZ())<=16;}
    public boolean link(BlockPos pos){if(level==null||!inRange(pos)||!level.hasChunkAt(pos)||level.getBlockEntity(pos) instanceof TransvectorLink)return false;target=pos.immutable();changed();return true;}
    static boolean movable(Level level,BlockPos pos){var state=level.getBlockState(pos);return !(level.getBlockEntity(pos) instanceof dev.eugheist.tc4modern.AuraNodeEntity)&&!dev.eugheist.tc4modern.PortableHoleConfig.blocked(state)&&state.getDestroySpeed(level,pos)>=0&&!state.is(net.minecraft.tags.BlockTags.create(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("ttinkerer","dislocator_immovable")));}
    public void pulse(){
        if(!(level instanceof ServerLevel server)||target==null||!inRange(target)||!server.hasChunkAt(target))return;
        var adjacent=worldPosition.relative(getBlockState().getValue(DislocatorBlock.FACING));
        if(target.equals(adjacent)||server.isOutsideBuildHeight(target)||server.isOutsideBuildHeight(adjacent)||!server.hasChunkAt(adjacent))return;
        if(cooldown>0){pulseStored=true;setChanged();return;}
        var atTarget=server.getEntities((Entity)null,new AABB(target));var atAdjacent=server.getEntities((Entity)null,new AABB(adjacent));
        if(movable(server,target)&&movable(server,adjacent)){
            var first=Snapshot.capture(server,target);var second=Snapshot.capture(server,adjacent);
            // Remove tiles before changing states so vanilla onRemove cannot spill inventories.
            server.removeBlockEntity(target);server.removeBlockEntity(adjacent);
            second.install(server,target);first.install(server,adjacent);
            server.updateNeighborsAt(target,second.state.getBlock());server.updateNeighborsAt(adjacent,first.state.getBlock());
        }
        var delta=net.minecraft.world.phys.Vec3.atLowerCornerOf(adjacent.subtract(target));
        for(var entity:atTarget)move(entity,delta);
        for(var entity:atAdjacent)if(!atTarget.contains(entity))move(entity,delta.scale(-1));
        cooldown=10;setChanged();
    }
    private static void move(Entity entity,net.minecraft.world.phys.Vec3 delta){var end=entity.position().add(delta);if(entity instanceof ServerPlayer player)player.connection.teleport(end.x,end.y,end.z,player.getYRot(),player.getXRot());else entity.teleportTo(end.x,end.y,end.z);}
    private record Snapshot(BlockState state,CompoundTag tile){
        static Snapshot capture(ServerLevel level,BlockPos pos){var tile=level.getBlockEntity(pos);return new Snapshot(level.getBlockState(pos),tile==null?null:tile.saveWithFullMetadata(level.registryAccess()));}
        void install(ServerLevel level,BlockPos pos){
            level.setBlock(pos,state,Block.UPDATE_CLIENTS|Block.UPDATE_KNOWN_SHAPE);
            if(tile!=null){var data=tile.copy();data.putInt("x",pos.getX());data.putInt("y",pos.getY());data.putInt("z",pos.getZ());var restored=BlockEntity.loadStatic(pos,state,data,level.registryAccess());if(restored==null)throw new IllegalStateException("Unable to restore dislocated tile at "+pos);level.setBlockEntity(restored);restored.setChanged();}
            level.invalidateCapabilities(pos);level.sendBlockUpdated(pos,state,state,3);
        }
    }
    public static void tick(Level level,BlockPos pos,BlockState state,DislocatorEntity tile){if(level.isClientSide)return;if(tile.cooldown>0){tile.cooldown--;tile.setChanged();}if(tile.cooldown==0&&tile.pulseStored){tile.pulseStored=false;tile.pulse();}}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider registries){super.saveAdditional(tag,registries);if(target!=null)tag.putLong("Target",target.asLong());if(camouflage!=null)tag.put("Camouflage",net.minecraft.nbt.NbtUtils.writeBlockState(camouflage));tag.putInt("Cooldown",cooldown);tag.putBoolean("PulseStored",pulseStored);}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider registries){super.loadAdditional(tag,registries);target=tag.contains("Target")?BlockPos.of(tag.getLong("Target")):null;camouflage=tag.contains("Camouflage")?net.minecraft.nbt.NbtUtils.readBlockState(registries.lookupOrThrow(net.minecraft.core.registries.Registries.BLOCK),tag.getCompound("Camouflage")):null;cooldown=Math.max(0,tag.getInt("Cooldown"));pulseStored=tag.getBoolean("PulseStored");}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider registries){return saveWithoutMetadata(registries);}
    @Override public net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket getUpdatePacket(){return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);}
}
