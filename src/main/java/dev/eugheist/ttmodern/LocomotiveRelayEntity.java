package dev.eugheist.ttmodern;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
public final class LocomotiveRelayEntity extends BlockEntity implements MovableTile{
    BlockPos partner;
    public LocomotiveRelayEntity(BlockPos pos,BlockState state){super(ThaumicTinkererModern.LOCOMOTIVE_RELAY_ENTITY.get(),pos,state);}
    boolean verify(){if(partner==null)return false;if(!level.hasChunkAt(partner))return false;var other=level.getBlockEntity(partner);if(!(other instanceof LocomotiveRelayEntity relay)||!worldPosition.equals(relay.partner)){partner=null;changed();return false;}return true;}
    void search(){if(partner!=null)return;for(int i=-32;i<32;i++){if(i==0)continue;pair(worldPosition.offset(0,0,i));pair(worldPosition.offset(i,0,0));}}
    void pair(BlockPos at){if(!level.hasChunkAt(at))return;if(level.getBlockEntity(at) instanceof LocomotiveRelayEntity other){partner=at;other.partner=worldPosition;changed();other.changed();}}
    public static void tick(Level level,BlockPos pos,BlockState state,LocomotiveRelayEntity tile){if(level.isClientSide){LocomotiveRelayFx.tick(level,pos,tile.partner);return;}tile.verify();if(level.getGameTime()%40!=0)return;tile.search();if(!tile.verify())return;var end=tile.partner;int dx=Integer.signum(end.getX()-pos.getX()),dz=Integer.signum(end.getZ()-pos.getZ());int distance=Math.max(Math.abs(end.getX()-pos.getX()),Math.abs(end.getZ()-pos.getZ()));for(int i=1;i<distance;i++){var at=pos.offset(dx*i,0,dz*i);if(level.hasChunkAt(at)&&level.getBlockEntity(at) instanceof LocomotiveEntity train&&!train.linked){train.first=pos;train.second=end;train.linked=true;train.direction=dx!=0?Direction.EAST:Direction.NORTH;train.setChanged();}}}
    void changed(){setChanged();if(level!=null&&!level.isClientSide)level.sendBlockUpdated(worldPosition,getBlockState(),getBlockState(),3);}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider registries){return saveWithoutMetadata(registries);}
    @Override public net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket getUpdatePacket(){return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider registries){super.saveAdditional(tag,registries);if(partner!=null)tag.putLong("Partner",partner.asLong());}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider registries){super.loadAdditional(tag,registries);partner=tag.contains("Partner")?BlockPos.of(tag.getLong("Partner")):null;}
}
