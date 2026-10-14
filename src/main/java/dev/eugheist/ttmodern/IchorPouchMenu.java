package dev.eugheist.ttmodern;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
public final class IchorPouchMenu extends AbstractContainerMenu {
 static final java.util.function.Supplier<MenuType<IchorPouchMenu>> TYPE=KamiRegistries.MENUS.register("ichor_pouch",()->net.neoforged.neoforge.common.extensions.IMenuTypeExtension.create((id,inv,buf)->new IchorPouchMenu(id,inv,buf.readBoolean())));
 public final net.minecraft.world.SimpleContainer contents=new net.minecraft.world.SimpleContainer(117);
 private final Inventory inventory;private final ItemStack pouch;private final boolean offhand;private final int held;
 static void init(){}
 public IchorPouchMenu(int id,Inventory inventory,boolean offhand){super(TYPE.get(),id);this.inventory=inventory;this.offhand=offhand;held=inventory.selected;pouch=offhand?inventory.player.getOffhandItem():inventory.getItem(held);var stored=IchorPouch.contents(pouch);for(int i=0;i<117;i++)contents.setItem(i,stored.get(i));contents.addListener(c->persist());for(int row=0;row<9;row++)for(int col=0;col<13;col++)addSlot(new Slot(contents,row*13+col,12+col*18,8+row*18){@Override public boolean mayPlace(ItemStack stack){return IchorPouch.allowed(stack);}});for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(inventory,9+row*9+col,48+18*col,177+18*row));for(int col=0;col<9;col++){int index=col;addSlot(new Slot(inventory,col,48+18*col,235){@Override public boolean mayPickup(Player player){return offhand||index!=held;}@Override public boolean mayPlace(ItemStack stack){return offhand||index!=held;}});}}
 private void persist(){if(!inventory.player.level().isClientSide&&stillValid(inventory.player)){var stored=new java.util.ArrayList<ItemStack>();for(int i=0;i<117;i++)stored.add(contents.getItem(i));IchorPouch.store(pouch,stored);inventory.setChanged();}}
 @Override public boolean stillValid(Player player){return pouch.getItem() instanceof IchorPouch&&(offhand?player.getOffhandItem():inventory.getItem(held))==pouch;}
 @Override public void clicked(int index,int button,ClickType type,Player player){if(!stillValid(player)||!offhand&&index==144+held||type==ClickType.SWAP&&(!offhand&&button==held||offhand&&button==40))return;super.clicked(index,button,type,player);}
 @Override public ItemStack quickMoveStack(Player player,int index){if(!stillValid(player)||index<0||index>=slots.size())return ItemStack.EMPTY;var slot=slots.get(index);if(!slot.hasItem()||!slot.mayPickup(player))return ItemStack.EMPTY;var stack=slot.getItem();var copy=stack.copy();if(index<117){if(!moveItemStackTo(stack,117,153,true))return ItemStack.EMPTY;}else if(!IchorPouch.allowed(stack)||!moveItemStackTo(stack,0,117,false))return ItemStack.EMPTY;if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();slot.onTake(player,stack);persist();return copy;}
 @Override public void removed(Player player){persist();super.removed(player);}
}
