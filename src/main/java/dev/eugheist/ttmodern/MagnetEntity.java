package dev.eugheist.ttmodern;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.items.ItemStackHandler;
public final class MagnetEntity extends BlockEntity implements MovableTile {
    public boolean adult=true;
    public final ItemStackHandler inventory=new ItemStackHandler(1){
        @Override public int getSlotLimit(int slot){return 1;}
        @Override public boolean isItemValid(int slot,ItemStack stack){return stack.getItem() instanceof SoulMouldItem;}
        @Override protected void onContentsChanged(int slot){changed();}
    };
    public MagnetEntity(BlockPos pos,BlockState state){super(ThaumicTinkererModern.MAGNET_ENTITY.get(),pos,state);}
    public boolean mobs(){return ((MagnetBlock)getBlockState().getBlock()).mobs;}
    void changed(){setChanged();if(level!=null)level.sendBlockUpdated(worldPosition,getBlockState(),getBlockState(),3);}
    boolean matches(Entity entity){
        if(!entity.isAlive())return false;
        if(!mobs())return entity instanceof ItemEntity;
        if(!(entity instanceof LivingEntity)||entity instanceof Player)return false;
        if(entity instanceof AgeableMob ageable&&adult==ageable.isBaby())return false;
        var filter=inventory.getStackInSlot(0);
        return filter.isEmpty()||SoulMouldItem.pattern(filter).equals(net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString());
    }
    public static void tick(Level level,BlockPos pos,BlockState state,MagnetEntity tile){
        if(level.isClientSide)return;int signal=level.getBestNeighborSignal(pos);if(signal==0)return;
        int range=signal/2;var center=Vec3.atCenterOf(pos);boolean pull=state.getValue(MagnetBlock.PULLING);
        var bounds=new AABB(center.x-range,pos.getY(),center.z-range,center.x+range,center.y+range,center.z+range);
        for(var entity:level.getEntities((Entity)null,bounds,tile::matches)){
            if(pull&&entity.position().distanceToSqr(center)<=1)continue;
            var delta=center.subtract(entity.position().add(0,entity.getBbHeight()/2,0));if(delta.length()>1)delta=delta.normalize();
            entity.setDeltaMovement(delta.scale(pull?.25:-.25));entity.hurtMarked=true;
        }
    }
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider registries){super.saveAdditional(tag,registries);tag.putBoolean("Adult",adult);tag.put("Inventory",inventory.serializeNBT(registries));}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider registries){super.loadAdditional(tag,registries);adult=!tag.contains("Adult")||tag.getBoolean("Adult");inventory.deserializeNBT(registries,tag.getCompound("Inventory"));}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider registries){return saveWithoutMetadata(registries);}
    @Override public net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket getUpdatePacket(){return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);}
}
