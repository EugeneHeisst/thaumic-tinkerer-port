package dev.eugheist.ttmodern;

import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.*;
import net.minecraft.world.phys.*;

/** Item and XP conservation through real native interaction routing. */
final class ReturnedItemModesAudit {
    static void run(ServerLevel world){
        var failures=new java.util.ArrayList<String>();
        var pos=new BlockPos(84,250,80);var bounds=new AABB(pos).inflate(4);
        var player=new net.neoforged.neoforge.common.util.FakePlayer(world,new com.mojang.authlib.GameProfile(java.util.UUID.randomUUID(),"ReturnModes"));player.setPos(84.5,250,82);
        var previous=world.getEntitiesOfClass(ItemEntity.class,bounds).stream().map(e->e.getUUID()).collect(java.util.stream.Collectors.toSet());
        try{
            for(var mode:java.util.List.of(GameType.SURVIVAL,GameType.CREATIVE)){
                player.setGameMode(mode);player.getInventory().clearContent();
                world.setBlockAndUpdate(pos,ThaumicTinkererModern.REPAIRER.get().defaultBlockState());
                var tile=(RepairerEntity)world.getBlockEntity(pos);
                var tool=new ItemStack(Items.DIAMOND_PICKAXE);tool.setDamageValue(30);tool.set(DataComponents.CUSTOM_NAME,net.minecraft.network.chat.Component.literal("Preserve repaired tool"));CustomData.update(DataComponents.CUSTOM_DATA,tool,t->t.putInt("ReturnAudit",164));
                player.setItemInHand(InteractionHand.MAIN_HAND,tool.copy());
                check(click(player,world,pos,InteractionHand.MAIN_HAND).consumesAction()&&player.getMainHandItem().isEmpty()&&same(tile.inventory.getStackInSlot(0),tool),failures,"repairer insert/debit "+mode);
                full(player);int dropped=drops(world,bounds,previous,tool);
                check(click(player,world,pos,InteractionHand.MAIN_HAND).consumesAction()&&tile.inventory.getStackInSlot(0).isEmpty(),failures,"repairer full extract "+mode);
                check(drops(world,bounds,previous,tool)==dropped+1&&inventory(player,tool)==0,failures,"repairer full inventory must drop one tool with damage/name/data "+mode);
                check(player.getInventory().items.stream().allMatch(s->s.is(Items.DIAMOND)&&s.getCount()==64),failures,"repairer full inventory unchanged "+mode);
                check(!click(player,world,pos,InteractionHand.MAIN_HAND).consumesAction()&&drops(world,bounds,previous,tool)==dropped+1,failures,"repairer repeat no duplication "+mode);
                player.getInventory().clearContent();player.setItemInHand(InteractionHand.OFF_HAND,tool.copy());
                check(click(player,world,pos,InteractionHand.OFF_HAND).consumesAction()&&player.getOffhandItem().isEmpty(),failures,"repairer offhand debit "+mode);
                check(click(player,world,pos,InteractionHand.MAIN_HAND).consumesAction()&&tile.inventory.getStackInSlot(0).isEmpty()&&inventory(player,tool)==1,failures,"repairer normal extract exact tool "+mode);
                for(int scenario=0;scenario<6;scenario++){
                    full(player);
                    var talisman=new ItemStack(ThaumicTinkererModern.XP_TALISMAN.get());XPTalismanItem.xp(talisman,scenario==4?9:20);CustomData.update(DataComponents.CUSTOM_DATA,talisman,t->t.putInt("ReturnAudit",164));
                    player.setItemInHand(InteractionHand.MAIN_HAND,talisman);
                    if(scenario!=5)player.getInventory().setItem(1,new ItemStack(Items.GLASS_BOTTLE,scenario==2?1:2));
                    if(scenario==0)player.getInventory().setItem(2,ItemStack.EMPTY);
                    if(scenario==3)player.getInventory().setItem(2,new ItemStack(Items.EXPERIENCE_BOTTLE,63));
                    var output=new ItemStack(Items.EXPERIENCE_BOTTLE);int oldDrops=drops(world,bounds,previous,output),oldOutput=inventory(player,output);
                    int oldEmpty=player.getInventory().items.stream().filter(s->s.is(Items.GLASS_BOTTLE)).mapToInt(ItemStack::getCount).sum();
                    player.gameMode.useItem(player,world,player.getMainHandItem(),InteractionHand.MAIN_HAND);
                    int created=inventory(player,output)-oldOutput+drops(world,bounds,previous,output)-oldDrops;
                    int leftEmpty=player.getInventory().items.stream().filter(s->s.is(Items.GLASS_BOTTLE)).mapToInt(ItemStack::getCount).sum();boolean allowed=scenario<4;
                    check(created==(allowed?1:0)&&leftEmpty==oldEmpty-(allowed?1:0)&&XPTalismanItem.xp(player.getMainHandItem())==(scenario==4?9:allowed?10:20),failures,"XP bottle/XP conservation scenario="+scenario+" "+mode+" created="+created);
                    check(player.getMainHandItem().getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag().getInt("ReturnAudit")==164,failures,"talisman unrelated data "+mode);
                    if(scenario==1)check(drops(world,bounds,previous,output)==oldDrops+1,failures,"XP full inventory output must drop "+mode);
                }
            }
            if(!failures.isEmpty())throw new IllegalStateException("Returned-item mode failures: "+failures);
            System.out.println("TT RETURNED ITEM MODES AUDIT: ALL PASS (repairer damaged named tool/main+offhand/full inventory/no duplicate; XP free/full/freed/merge/lowXP/no bottle; SURVIVAL/CREATIVE resource conservation)");
        }finally{
            if(world.getBlockEntity(pos) instanceof RepairerEntity tile)tile.inventory.setStackInSlot(0,ItemStack.EMPTY);world.removeBlock(pos,false);
            for(var entity:world.getEntitiesOfClass(ItemEntity.class,bounds))if(!previous.contains(entity.getUUID()))entity.discard();
        }
    }
    private static void full(net.neoforged.neoforge.common.util.FakePlayer player){player.getInventory().clearContent();for(int i=0;i<player.getInventory().items.size();i++)player.getInventory().items.set(i,new ItemStack(Items.DIAMOND,64));}
    private static InteractionResult click(net.neoforged.neoforge.common.util.FakePlayer player,ServerLevel world,BlockPos pos,InteractionHand hand){return player.gameMode.useItemOn(player,world,player.getItemInHand(hand),hand,new BlockHitResult(Vec3.atCenterOf(pos),Direction.UP,pos,false));}
    private static boolean same(ItemStack a,ItemStack b){return a.getCount()==b.getCount()&&ItemStack.isSameItemSameComponents(a,b);}
    private static int inventory(net.neoforged.neoforge.common.util.FakePlayer player,ItemStack item){return player.getInventory().items.stream().filter(s->ItemStack.isSameItemSameComponents(s,item)).mapToInt(ItemStack::getCount).sum();}
    private static int drops(ServerLevel world,AABB bounds,java.util.Set<java.util.UUID> previous,ItemStack item){return world.getEntitiesOfClass(ItemEntity.class,bounds).stream().filter(e->!previous.contains(e.getUUID())&&ItemStack.isSameItemSameComponents(e.getItem(),item)).mapToInt(e->e.getItem().getCount()).sum();}
    private static void check(boolean ok,java.util.List<String> failures,String label){if(!ok)failures.add(label);}
}
