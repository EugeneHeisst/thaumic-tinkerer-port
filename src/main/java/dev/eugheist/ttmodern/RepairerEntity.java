package dev.eugheist.ttmodern;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import thaumcraft.api.aspects.*;
public final class RepairerEntity extends BlockEntity implements IEssentiaTransport,MovableTile {
    public int ticks;
    public boolean repairing;
    public final ItemStackHandler inventory=new ItemStackHandler(1){
        @Override public int getSlotLimit(int slot){return 1;}
        @Override public boolean isItemValid(int slot,ItemStack stack){return stack.isDamageableItem()&&stack.isRepairable();}
        @Override protected void onContentsChanged(int slot){changed();}
    };
    public RepairerEntity(BlockPos pos,BlockState state){super(ThaumicTinkererModern.REPAIRER_ENTITY.get(),pos,state);}
    void changed(){setChanged();if(level!=null){level.sendBlockUpdated(worldPosition,getBlockState(),getBlockState(),3);level.updateNeighborsAt(worldPosition,getBlockState().getBlock());}}
    public Direction orientation(){return getBlockState().getValue(RepairerBlock.FACING);}
    public static void tick(Level level,BlockPos pos,BlockState state,RepairerEntity tile){
        tile.ticks++;if(level.isClientSide||tile.ticks%10!=0)return;
        var stack=tile.inventory.getStackInSlot(0);boolean was=tile.repairing;tile.repairing=false;
        if(!stack.isEmpty()&&stack.getDamageValue()>0){int repair=tile.drawEssentia();if(repair>0){stack.setDamageValue(Math.max(0,stack.getDamageValue()-repair));tile.repairing=true;tile.changed();}}
        if(was!=tile.repairing)tile.changed();
    }
    int drawEssentia(){
        var face=orientation();var pos=worldPosition.relative(face);if(level==null||!level.hasChunkAt(pos))return 0;
        if(!(level.getBlockEntity(pos) instanceof IEssentiaTransport source)||!source.isConnectable(face.getOpposite())||!source.canOutputTo(face.getOpposite()))return 0;
        for(var aspect:new Aspect[]{Aspect.TOOL,Aspect.CRAFT,Aspect.ORDER}){
            if(source.getSuctionType(face.getOpposite())!=aspect||source.getSuctionAmount(face.getOpposite())>=getSuctionAmount(face))continue;
            if(source.takeEssentia(aspect,1,face.getOpposite())==1)return aspect==Aspect.TOOL?8:aspect==Aspect.CRAFT?5:3;
        }
        return 0;
    }
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider registries){super.saveAdditional(tag,registries);tag.put("Inventory",inventory.serializeNBT(registries));tag.putBoolean("Repairing",repairing);}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider registries){super.loadAdditional(tag,registries);inventory.deserializeNBT(registries,tag.getCompound("Inventory"));repairing=tag.getBoolean("Repairing");}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider registries){return saveWithoutMetadata(registries);}
    @Override public net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket getUpdatePacket(){return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);}
    public boolean isConnectable(Direction face){return face==orientation();}
    public boolean canInputFrom(Direction face){return false;}
    public boolean canOutputTo(Direction face){return isConnectable(face);}
    public void setSuction(Aspect aspect,int amount){}
    public Aspect getSuctionType(Direction face){return isConnectable(face)?Aspect.TOOL:null;}
    public int getSuctionAmount(Direction face){return isConnectable(face)?128:0;}
    public int takeEssentia(Aspect aspect,int amount,Direction face){return 0;}
    public int addEssentia(Aspect aspect,int amount,Direction face){return 0;}
    public Aspect getEssentiaType(Direction face){return null;}
    public int getEssentiaAmount(Direction face){return 0;}
    public int getMinimumSuction(){return 0;}
    public boolean renderExtendedTube(){return false;}
}
