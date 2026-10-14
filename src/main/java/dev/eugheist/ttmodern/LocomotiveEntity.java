package dev.eugheist.ttmodern;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
public final class LocomotiveEntity extends BlockEntity{
    boolean linked;BlockPos first,second;Direction direction=Direction.NORTH;long lastMove=Long.MIN_VALUE;
    public LocomotiveEntity(BlockPos pos,BlockState state){super(ThaumicTinkererModern.LOCOMOTIVE_ENTITY.get(),pos,state);}
    boolean verify(){if(!linked||first==null||second==null)return false;if(!level.hasChunkAt(first)||!level.hasChunkAt(second))return false;if(!(level.getBlockEntity(first) instanceof LocomotiveRelayEntity relay)||!relay.verify()||!second.equals(relay.partner)){linked=false;setChanged();return false;}return true;}
    public static void tick(Level level,BlockPos pos,BlockState state,LocomotiveEntity tile){if(!(level instanceof ServerLevel server)||!tile.verify()||level.hasNeighborSignal(pos))return;long time=level.getGameTime();var target=pos.relative(tile.direction);if(!level.hasChunkAt(target)||level.isOutsideBuildHeight(target.above()))return;
        if(time%100==0&&(!level.isEmptyBlock(target)||!level.isEmptyBlock(target.above()))){tile.direction=tile.direction.getOpposite();tile.setChanged();return;}
        if(time%100!=1||tile.lastMove==time||!level.isEmptyBlock(target)||!level.isEmptyBlock(target.above())||!state.is(ThaumicTinkererModern.LOCOMOTIVE.get()))return;
        var above=pos.above();var passenger=level.getBlockState(above);var passengerTile=level.getBlockEntity(above);
        boolean ae2=net.neoforged.fml.ModList.get().isLoaded("ae2");
        boolean movePassenger=!passenger.isAir()&&passenger.getDestroySpeed(level,above)>=0&&!passenger.is(Blocks.PISTON_HEAD)&&passenger.canSurvive(level,target.above())&&(passengerTile==null||ae2||net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(passenger.getBlock()).getNamespace().equals("minecraft")||passengerTile instanceof MovableTile);
        PassengerTransfer transfer=null;
        if(movePassenger&&passengerTile!=null){
            if(ae2){transfer=AE2Compat.begin(passengerTile);movePassenger=transfer!=null;}
            else if(passengerTile instanceof MovableTile movable)movable.prepareToMove();
        }
        CompoundTag passengerData=movePassenger&&passengerTile!=null?passengerTile.saveWithFullMetadata(level.registryAccess()):null;
        tile.lastMove=time;var data=tile.saveWithFullMetadata(level.registryAccess());
        // Detach before state replacement: inventory blocks must never spill or duplicate contents.
        level.removeBlockEntity(pos);if(movePassenger)level.removeBlockEntity(above);
        level.setBlock(pos,Blocks.AIR.defaultBlockState(),Block.UPDATE_CLIENTS|Block.UPDATE_KNOWN_SHAPE);
        install(server,target,state,data);
        if(movePassenger){
            level.setBlock(above,Blocks.AIR.defaultBlockState(),Block.UPDATE_CLIENTS|Block.UPDATE_KNOWN_SHAPE);
            boolean completed=false;
            try{
                if(transfer!=null)completed=transfer.complete(server,target.above(),passenger);
                else{install(server,target.above(),passenger,passengerData);if(level.getBlockEntity(target.above()) instanceof MovableTile moved)moved.doneMoving();completed=true;}
            }catch(RuntimeException error){org.slf4j.LoggerFactory.getLogger(LocomotiveEntity.class).warn("Locomotive passenger transfer failed at {}; restoring source",above,error);}
            if(!completed){
                // A failed foreign handler may already have installed a partial destination entity.
                for(var clear:java.util.List.of(target.above(),target)){level.removeBlockEntity(clear);level.setBlock(clear,Blocks.AIR.defaultBlockState(),Block.UPDATE_CLIENTS|Block.UPDATE_KNOWN_SHAPE);level.invalidateCapabilities(clear);}
                install(server,pos,state,data);install(server,above,passenger,passengerData);
                for(var at:java.util.List.of(pos,above,target,target.above()))level.updateNeighborsAt(at,level.getBlockState(at).getBlock());
                return;
            }
        }
        for(var at:java.util.List.of(pos,above,target,target.above()))level.updateNeighborsAt(at,level.getBlockState(at).getBlock());
    }
    static void install(ServerLevel level,BlockPos pos,BlockState state,CompoundTag data){level.setBlock(pos,state,Block.UPDATE_CLIENTS|Block.UPDATE_KNOWN_SHAPE);if(data!=null){data=data.copy();data.putInt("x",pos.getX());data.putInt("y",pos.getY());data.putInt("z",pos.getZ());var tile=BlockEntity.loadStatic(pos,state,data,level.registryAccess());if(tile==null)throw new IllegalStateException("Cannot restore locomotive passenger at "+pos);level.setBlockEntity(tile);tile.setChanged();}level.invalidateCapabilities(pos);level.sendBlockUpdated(pos,state,state,3);}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider registries){super.saveAdditional(tag,registries);tag.putBoolean("Linked",linked);if(first!=null)tag.putLong("First",first.asLong());if(second!=null)tag.putLong("Second",second.asLong());tag.putString("Direction",direction.getName());tag.putLong("LastMove",lastMove);}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider registries){super.loadAdditional(tag,registries);linked=tag.getBoolean("Linked");first=tag.contains("First")?BlockPos.of(tag.getLong("First")):null;second=tag.contains("Second")?BlockPos.of(tag.getLong("Second")):null;var d=Direction.byName(tag.getString("Direction"));direction=d!=null&&d.getAxis().isHorizontal()?d:Direction.NORTH;lastMove=tag.contains("LastMove")?tag.getLong("LastMove"):Long.MIN_VALUE;}
}
