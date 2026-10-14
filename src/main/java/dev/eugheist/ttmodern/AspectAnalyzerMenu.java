package dev.eugheist.ttmodern;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
final class AspectAnalyzerMenu extends AbstractContainerMenu {
 final AspectAnalyzer.Entity tile;
 static AspectAnalyzerMenu client(int id,Inventory inventory,net.minecraft.core.BlockPos pos){var level=inventory.player.level();var existing=level.getBlockEntity(pos);var tile=existing instanceof AspectAnalyzer.Entity analyzer?analyzer:new AspectAnalyzer.Entity(pos,AspectAnalyzer.BLOCK.get().defaultBlockState());if(tile.getLevel()==null)tile.setLevel(level);return new AspectAnalyzerMenu(id,inventory,tile);}
 AspectAnalyzerMenu(int id,Inventory inventory,AspectAnalyzer.Entity tile){super(AspectAnalyzer.MENU.get(),id);this.tile=tile;
  addSlot(new net.neoforged.neoforge.items.SlotItemHandler(tile.inventory,0,20,30){@Override public boolean mayPlace(ItemStack stack){return !dev.eugheist.tc4modern.RecipeAspects.get(stack,inventory.player.level()).isEmpty();}});
  for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(inventory,col+row*9+9,8+col*18,84+row*18));
  for(int col=0;col<9;col++)addSlot(new Slot(inventory,col,8+col*18,142));
 }
 @Override public boolean stillValid(Player player){return tile.getLevel()!=null&&tile.getLevel().getBlockEntity(tile.getBlockPos())==tile&&player.distanceToSqr(net.minecraft.world.phys.Vec3.atCenterOf(tile.getBlockPos()))<=64;}
 @Override public ItemStack quickMoveStack(Player player,int index){if(!stillValid(player)||index<0||index>=slots.size())return ItemStack.EMPTY;var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var original=stack.copy();if(index==0){if(!moveItemStackTo(stack,1,37,true))return ItemStack.EMPTY;}else if(!slots.get(0).mayPlace(stack)||!moveItemStackTo(stack,0,1,false))return ItemStack.EMPTY;if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();slot.onTake(player,stack);return original;}
}
