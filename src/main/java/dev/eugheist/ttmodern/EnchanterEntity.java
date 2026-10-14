package dev.eugheist.ttmodern;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import dev.eugheist.tc4modern.WandData;
import dev.eugheist.tc4modern.WandParts;
public final class EnchanterEntity extends BlockEntity implements MovableTile {
    final EnchanterProcess process=new EnchanterProcess();
    private CompoundTag pending;
    public final ItemStackHandler inventory=new ItemStackHandler(2){
        @Override public int getSlotLimit(int slot){return 1;}
        @Override public boolean isItemValid(int slot,ItemStack stack){return slot==1?WandData.isWand(stack)&&!WandParts.isStaff(stack):!stack.isEmpty()&&stack.isEnchantable();}
        @Override protected void onContentsChanged(int slot){if(slot==0&&!process.working())process.selection.clear();setChanged();}
    };
    public EnchanterEntity(BlockPos pos,BlockState state){super(ThaumicTinkererModern.ENCHANTER_ENTITY.get(),pos,state);}
    @Override public void onLoad(){super.onLoad();restore();}
    private void restore(){if(pending!=null&&level instanceof ServerLevel server){process.load(server,inventory.getStackInSlot(0),pending);pending=null;}}
    public static void tick(Level level,BlockPos pos,BlockState state,EnchanterEntity tile){
        if(!(level instanceof ServerLevel server))return;tile.restore();
        boolean wasWorking=tile.process.working();int before=0;for(var a:thaumcraft.api.aspects.Aspect.getPrimalAspects())before+=tile.process.collected(a);
        var pillars=wasWorking?EnchanterStructure.find(level,pos):java.util.List.<BlockPos>of();
        if(tile.process.tick(server,tile.inventory.getStackInSlot(0),tile.inventory.getStackInSlot(1),!wasWorking||!pillars.isEmpty())){
            int after=0;for(var a:thaumcraft.api.aspects.Aspect.getPrimalAspects())after+=tile.process.collected(a);
            if(after>before&&level.random.nextBoolean()){
                var cap=pillars.get(tile.process.lastDrainIndex);
                net.neoforged.neoforge.network.PacketDistributor.sendToPlayersNear(server,null,pos.getX(),pos.getY(),pos.getZ(),64,new dev.eugheist.tc4modern.InfusionContent.FxPayload(168,pos.getX(),pos.getY()+.25,pos.getZ(),cap.getX(),cap.getY()-.75,cap.getZ(),0));
                if(level.random.nextInt(5)==0)level.playSound(null,new BlockPos(cap.getX(),pos.getY(),cap.getZ()),net.minecraft.sounds.SoundEvent.createVariableRangeEvent(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("tc4modern","brain")),net.minecraft.sounds.SoundSource.BLOCKS,.5f,1);
            }
            if(wasWorking&&!tile.process.working()&&tile.inventory.getStackInSlot(0).isEnchanted())level.playSound(null,pos,net.minecraft.sounds.SoundEvent.createVariableRangeEvent(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("tc4modern","wand")),net.minecraft.sounds.SoundSource.BLOCKS,1,1);
            tile.setChanged();level.sendBlockUpdated(pos,state,state,3);
        }
    }
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider access){super.saveAdditional(tag,access);tag.put("Inventory",inventory.serializeNBT(access));tag.put("Process",pending==null?process.save():pending.copy());}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider access){super.loadAdditional(tag,access);inventory.deserializeNBT(access,tag.getCompound("Inventory"));pending=tag.getCompound("Process").copy();}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider access){return saveWithoutMetadata(access);}
    @Override public net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket getUpdatePacket(){return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);}
}
