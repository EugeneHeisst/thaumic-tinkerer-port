package dev.eugheist.ttmodern;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
public final class WarpGateMenu extends AbstractContainerMenu {
 static final java.util.function.Supplier<MenuType<WarpGateMenu>> TYPE=KamiRegistries.MENUS.register("warp_gate",()->net.neoforged.neoforge.common.extensions.IMenuTypeExtension.create((id,inv,buf)->new WarpGateMenu(id,inv,(WarpGateEntity)inv.player.level().getBlockEntity(buf.readBlockPos()))));
 final WarpGateEntity gate;
 WarpGateMenu(int id,Inventory inventory,WarpGateEntity gate){super(TYPE.get(),id);this.gate=gate;
  addDataSlots(new ContainerData(){public int get(int i){return gate.locked?1:0;}public void set(int i,int value){gate.locked=value!=0;}public int getCount(){return 1;}});
  for(int row=0;row<2;row++)for(int col=0;col<5;col++)addSlot(new net.neoforged.neoforge.items.SlotItemHandler(gate.inventory,row*5+col,30+col*25,27+row*25));
  for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(inventory,9+row*9+col,8+col*18,84+row*18));for(int col=0;col<9;col++)addSlot(new Slot(inventory,col,8+col*18,142));
 }
 static void init(){}
 @Override public boolean stillValid(Player player){return gate.valid(player);}
 @Override public boolean clickMenuButton(Player player,int id){if(id!=0||!stillValid(player))return false;gate.locked=!gate.locked;gate.changed();return true;}
 @Override public ItemStack quickMoveStack(Player player,int index){if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var original=stack.copy();if(index<10){if(!moveItemStackTo(stack,10,46,true))return ItemStack.EMPTY;}else if(!moveItemStackTo(stack,0,10,false))return ItemStack.EMPTY;if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();slot.onTake(player,stack);return original;}
}
