package dev.eugheist.ttmodern;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;
public final class EnchanterMenu extends AbstractContainerMenu {
    static final java.util.List<String> IDS=java.util.List.copyOf(EnchanterCosts.ENTRIES.keySet());
    final EnchanterEntity tile;
    private final ContainerData data;
    public EnchanterMenu(int id,Inventory inventory,EnchanterEntity tile){
        super(ThaumicTinkererModern.ENCHANTER_MENU.get(),id);this.tile=tile;
        data=inventory.player.level().isClientSide?new SimpleContainerData(47):new ContainerData(){
            public int getCount(){return 47;}
            public int get(int index){if(index==0)return tile.process.working()?1:0;if(index<=40)return tile.process.selection.entries().getOrDefault(IDS.get(index-1),0);return tile.process.collected(thaumcraft.api.aspects.Aspect.getPrimalAspects().get(index-41));}
            public void set(int index,int value){}
        };addDataSlots(data);
        addSlot(new SlotItemHandler(tile.inventory,0,6,6){@Override public boolean mayPlace(ItemStack stack){return !working()&&super.mayPlace(stack);}@Override public boolean mayPickup(Player player){return !working();}});
        addSlot(new SlotItemHandler(tile.inventory,1,6,31));
        for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(inventory,col+row*9+9,8+col*18,84+row*18));
        for(int col=0;col<9;col++)addSlot(new Slot(inventory,col,8+col*18,142));
    }
    public boolean working(){return data.get(0)!=0;}
    public int rank(int index){return index>=0&&index<40?data.get(index+1):0;}
    public int progress(int index){return index>=0&&index<6?data.get(index+41):0;}
    @Override public boolean stillValid(Player player){return tile.getLevel()!=null&&tile.getLevel().getBlockEntity(tile.getBlockPos())==tile&&player.distanceToSqr(net.minecraft.world.phys.Vec3.atCenterOf(tile.getBlockPos()))<=64;}
    @Override public boolean clickMenuButton(Player player,int button){
        if(!stillValid(player)||!(player.level() instanceof net.minecraft.server.level.ServerLevel server)||working()||button<0||button>120)return false;
        boolean changed;
        if(button==0)changed=!tile.inventory.getStackInSlot(0).isEmpty()&&tile.process.start();
        else{
            int index=(button-1)/3,action=(button-1)%3;String key=IDS.get(index);int current=rank(index);
            int requested=action==0?(current>0?-1:1):action==1?current+1:current<=1?-1:current-1;
            changed=tile.process.change(server,tile.inventory.getStackInSlot(0),dev.eugheist.tc4modern.ResearchProgress.completed(player.getPersistentData()),key,requested);
        }
        if(changed){tile.setChanged();server.sendBlockUpdated(tile.getBlockPos(),tile.getBlockState(),tile.getBlockState(),3);broadcastChanges();}return changed;
    }
    @Override public ItemStack quickMoveStack(Player player,int index){
        if(!stillValid(player)||index<0||index>=slots.size())return ItemStack.EMPTY;var slot=slots.get(index);if(!slot.hasItem()||!slot.mayPickup(player))return ItemStack.EMPTY;
        var stack=slot.getItem();var copy=stack.copy();
        if(index<2){if(!moveItemStackTo(stack,2,38,true))return ItemStack.EMPTY;}
        else{int target=tile.inventory.isItemValid(1,stack)?1:0;if(!moveItemStackTo(stack,target,target+1,false))return ItemStack.EMPTY;}
        if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();slot.onTake(player,stack);return copy;
    }
}
