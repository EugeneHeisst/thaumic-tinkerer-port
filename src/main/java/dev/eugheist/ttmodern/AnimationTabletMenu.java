package dev.eugheist.ttmodern;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;
public final class AnimationTabletMenu extends AbstractContainerMenu {
    final AnimationTabletEntity tile;
    private final ContainerData data;
    public AnimationTabletMenu(int id,Inventory inventory,AnimationTabletEntity tile){
        super(ThaumicTinkererModern.ANIMATION_TABLET_MENU.get(),id);this.tile=tile;
        data=new ContainerData(){public int get(int index){return index==0?(tile.leftClick?1:0):(tile.redstone?1:0);}public void set(int index,int value){if(index==0)tile.leftClick=value!=0;else tile.redstone=value!=0;}public int getCount(){return 2;}};addDataSlots(data);
        addSlot(new SlotItemHandler(tile.inventory,0,80,15));
        for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(inventory,col+row*9+9,8+col*18,84+row*18));
        for(int col=0;col<9;col++)addSlot(new Slot(inventory,col,8+col*18,142));
    }
    public boolean leftClick(){return data.get(0)!=0;}
    public boolean redstone(){return data.get(1)!=0;}
    @Override public boolean stillValid(Player player){return tile.getLevel()!=null&&tile.getLevel().getBlockEntity(tile.getBlockPos())==tile&&player.distanceToSqr(net.minecraft.world.phys.Vec3.atCenterOf(tile.getBlockPos()))<=64;}
    @Override public boolean clickMenuButton(Player player,int id){if(id<0||id>2||!stillValid(player))return false;if(id==0)tile.redstone=!tile.redstone;else{tile.leftClick=id==1;tile.stopBreaking();}tile.changed();return true;}
    @Override public ItemStack quickMoveStack(Player player,int index){
        if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var copy=stack.copy();
        if(index==0){if(!moveItemStackTo(stack,1,37,true))return ItemStack.EMPTY;}
        else if(!moveItemStackTo(stack,0,1,false))return ItemStack.EMPTY;
        if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();slot.onTake(player,stack);return copy;
    }
}
