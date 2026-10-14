package dev.eugheist.ttmodern;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.*;
final class EnchanterMenuAudit {
    private static void check(boolean ok,String label){if(!ok)throw new IllegalStateException("Enchanter menu: "+label);}
    static void run(ServerLevel level){
        var pos=new BlockPos(8,240,8);level.setBlockAndUpdate(pos,ThaumicTinkererModern.ENCHANTER.get().defaultBlockState());var tile=(EnchanterEntity)level.getBlockEntity(pos);
        var player=net.neoforged.neoforge.common.util.FakePlayerFactory.getMinecraft(level);player.getInventory().clearContent();player.setPos(8.5,240,8.5);
        var menu=new EnchanterMenu(1,player.getInventory(),tile);player.getInventory().setItem(0,new ItemStack(Items.DIAMOND_SWORD));
        check(!menu.quickMoveStack(player,29).isEmpty()&&tile.inventory.getStackInSlot(0).is(Items.DIAMOND_SWORD),"shift inserts tool");
        int sharp=EnchanterMenu.IDS.indexOf("minecraft:sharpness");
        check(menu.clickMenuButton(player,1+sharp*3)&&menu.rank(sharp)==1,"server selects eligible enchant");
        check(menu.clickMenuButton(player,2+sharp*3)&&menu.rank(sharp)==2,"increase level");
        check(!menu.clickMenuButton(player,121)&&!menu.clickMenuButton(player,-1),"invalid buttons refused");
        check(menu.clickMenuButton(player,0)&&menu.working(),"start locks tool");
        check(!menu.slots.get(0).mayPickup(player)&&!menu.slots.get(0).mayPlace(new ItemStack(Items.DIAMOND_SWORD))&&menu.quickMoveStack(player,0).isEmpty(),"working tool cannot move");
        check(!menu.clickMenuButton(player,1+sharp*3),"working choices locked");
        player.setPos(100,240,100);check(!menu.stillValid(player)&&!menu.clickMenuButton(player,0)&&menu.quickMoveStack(player,1).isEmpty(),"distance enforcement");
        player.setPos(8.5,240,8.5);EnchanterEntity.tick(level,pos,tile.getBlockState(),tile);check(!menu.working(),"missing structure stops");
        check(!menu.quickMoveStack(player,0).isEmpty()&&player.getInventory().countItem(Items.DIAMOND_SWORD)==1&&tile.process.selection.entries().isEmpty(),"tool removal conserves item and clears choices");
        level.removeBlock(pos,false);player.getInventory().clearContent();
        System.out.println("TT ENCHANTER MENU AUDIT: ALL PASS (shift conservation, levels, button validation, working lock, distance)");
    }
}
