package dev.eugheist.ttmodern;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
final class MagnetAudit{
    static void check(boolean ok,String label){if(!ok)throw new IllegalStateException("TT PORT AUDIT FAILED: magnet "+label);}
    static void run(ServerLevel level){
        var pos=new BlockPos(8,270,8);level.setBlockAndUpdate(pos,ThaumicTinkererModern.MAGNET.get().defaultBlockState());
        var tile=(MagnetEntity)level.getBlockEntity(pos);
        var item=new ItemEntity(level,12.5,270.5,8.5,new ItemStack(Items.DIAMOND));item.setNoGravity(true);item.setDeltaMovement(Vec3.ZERO);level.addFreshEntity(item);
        MagnetEntity.tick(level,pos,tile.getBlockState(),tile);check(item.getDeltaMovement().equals(Vec3.ZERO),"unpowered stationary");
        level.setBlockAndUpdate(pos.west(),Blocks.REDSTONE_BLOCK.defaultBlockState());MagnetEntity.tick(level,pos,tile.getBlockState(),tile);check(item.getDeltaMovement().x<0,"powered item attraction");
        level.setBlockAndUpdate(pos,tile.getBlockState().setValue(MagnetBlock.PULLING,false));MagnetEntity.tick(level,pos,tile.getBlockState(),tile);check(item.getDeltaMovement().x>0,"polarity repulsion");
        item.setPos(20,270,8);item.setDeltaMovement(Vec3.ZERO);MagnetEntity.tick(level,pos,tile.getBlockState(),tile);check(item.getDeltaMovement().equals(Vec3.ZERO),"outside seven block range");
        item.setPos(12,268,8);MagnetEntity.tick(level,pos,tile.getBlockState(),tile);check(item.getDeltaMovement().equals(Vec3.ZERO),"no attraction below plate");item.discard();
        level.setBlockAndUpdate(pos,ThaumicTinkererModern.MOB_MAGNET.get().defaultBlockState());tile=(MagnetEntity)level.getBlockEntity(pos);
        var cow=EntityType.COW.create(level);var pig=EntityType.PIG.create(level);check(cow!=null&&pig!=null,"entities created");
        check(tile.matches(cow),"adult without filter");cow.setBaby(true);check(!tile.matches(cow),"child excluded by adult mode");tile.adult=false;check(tile.matches(cow),"child mode");
        var mould=new ItemStack(ThaumicTinkererModern.SOUL_MOULD.get());tile.inventory.setStackInSlot(0,mould);check(!tile.matches(cow),"unassigned mould matches none");
        net.minecraft.world.item.component.CustomData.update(net.minecraft.core.component.DataComponents.CUSTOM_DATA,mould,t->t.putString("pattern","minecraft:cow"));check(tile.matches(cow)&&!tile.matches(pig),"exact species filter");
        check(!tile.inventory.isItemValid(0,new ItemStack(Items.DIAMOND)),"invalid filter rejected");
        var player=net.neoforged.neoforge.common.util.FakePlayerFactory.getMinecraft(level);player.setPos(8.5,270.5,8.5);check(!tile.matches(player),"players never attracted");
        var menu=new MagnetMenu(42,player.getInventory(),tile);check(menu.stillValid(player),"menu near tile valid");check(menu.clickMenuButton(player,0)&&tile.adult,"adult selection");check(menu.clickMenuButton(player,1)&&!tile.adult,"child selection");check(!menu.clickMenuButton(player,2),"invalid selection rejected");check(menu.quickMoveStack(player,-1).isEmpty(),"invalid slot rejected");
        player.setPos(80,270,80);check(!menu.stillValid(player)&&!menu.clickMenuButton(player,0)&&!tile.adult,"remote menu mutation rejected");
        var saved=tile.saveWithoutMetadata(level.registryAccess());var copy=new MagnetEntity(pos,tile.getBlockState());copy.loadWithComponents(saved,level.registryAccess());check(!copy.adult&&SoulMouldItem.pattern(copy.inventory.getStackInSlot(0)).equals("minecraft:cow"),"filter and age survive save");
        for(String key:new String[]{"MAGNET","MOB_MAGNET"}){var recipe=dev.eugheist.tc4modern.ArcaneRecipes.entries().stream().filter(r->r.key().equals(key)).findFirst().orElseThrow();check(recipe.ingredients().stream().filter(i->!i.isEmpty()).allMatch(i->i.getItems().length>0&&!i.getItems()[0].is(Items.AIR)),"recipe ingredients "+key);}
        tile.inventory.setStackInSlot(0,ItemStack.EMPTY);level.removeBlock(pos,false);level.removeBlock(pos.west(),false);
        System.out.println("TT MAGNET AUDIT: ALL PASS (power, polarity, bounds, species, age, filter, persistence, recipes)");
    }
}
