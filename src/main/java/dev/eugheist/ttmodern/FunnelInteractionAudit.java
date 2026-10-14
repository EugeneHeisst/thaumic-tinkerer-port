package dev.eugheist.ttmodern;

import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.*;

/** Native server interaction routing, including creative block-use and cancellation. */
final class FunnelInteractionAudit {
    static void run(ServerLevel world){
        var pos=new BlockPos(80,250,80);var hopper=pos.below();
        var player=new net.neoforged.neoforge.common.util.FakePlayer(world,new com.mojang.authlib.GameProfile(java.util.UUID.randomUUID(),"FunnelModes"));
        player.setPos(80.5,250,82.5);
        var bounds=new AABB(pos).inflate(4);
        var previous=world.getEntitiesOfClass(ItemEntity.class,bounds).stream().map(e->e.getUUID()).collect(java.util.stream.Collectors.toSet());
        boolean[] cancel={false};
        java.util.function.Consumer<net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock> listener=event->{
            if(cancel[0]&&event.getEntity()==player&&event.getPos().equals(pos)){event.setCanceled(true);event.setCancellationResult(InteractionResult.FAIL);}
        };
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(listener);
        try{
            for(var mode:java.util.List.of(GameType.SURVIVAL,GameType.CREATIVE))for(boolean voidJar:new boolean[]{false,true}){
                player.setGameMode(mode);player.getInventory().clearContent();player.setItemInHand(InteractionHand.OFF_HAND,ItemStack.EMPTY);
                world.setBlockAndUpdate(hopper,Blocks.HOPPER.defaultBlockState());world.setBlockAndUpdate(pos,ThaumicTinkererModern.FUNNEL.get().defaultBlockState());
                var tile=(FunnelEntity)world.getBlockEntity(pos);tile.inventory.setStackInSlot(0,ItemStack.EMPTY);
                var jar=new ItemStack(voidJar?dev.eugheist.tc4modern.AlchemyMachines.VOID_JAR.get():dev.eugheist.tc4modern.EssentiaDevices.JAR.get());
                CustomData.update(DataComponents.CUSTOM_DATA,jar,tag->{new thaumcraft.api.aspects.AspectList().add(thaumcraft.api.aspects.Aspect.AIR,4).writeToNBT(tag);tag.putString("AspectFilter","aer");tag.putInt("FunnelAudit",164);});
                player.setItemInHand(InteractionHand.MAIN_HAND,jar.copy());
                cancel[0]=true;
                check(click(player,world,pos,InteractionHand.MAIN_HAND)==InteractionResult.FAIL&&tile.inventory.getStackInSlot(0).isEmpty()&&same(player.getMainHandItem(),jar),"canceled insert unchanged "+mode);
                cancel[0]=false;
                check(click(player,world,pos,InteractionHand.MAIN_HAND).consumesAction(),"native insert accepted "+mode);
                check(player.getMainHandItem().isEmpty()&&same(tile.inventory.getStackInSlot(0),jar),"original block activation debits exactly one even creative "+mode);
                check(click(player,world,pos,InteractionHand.MAIN_HAND).consumesAction()&&tile.inventory.getStackInSlot(0).isEmpty(),"native empty-hand extract "+mode);
                check(count(player,jar)==1,"extract conserves one jar/components "+mode);
                player.setItemInHand(InteractionHand.MAIN_HAND,ItemStack.EMPTY); // returned jar is in selected slot
                player.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(Items.DIAMOND));
                check(!click(player,world,pos,InteractionHand.MAIN_HAND).consumesAction()&&player.getMainHandItem().is(Items.DIAMOND)&&tile.inventory.getStackInSlot(0).isEmpty(),"invalid item rejected "+mode);
                player.getInventory().clearContent();player.setItemInHand(InteractionHand.OFF_HAND,jar.copy());
                check(click(player,world,pos,InteractionHand.OFF_HAND).consumesAction()&&player.getOffhandItem().isEmpty()&&same(tile.inventory.getStackInSlot(0),jar),"offhand insert "+mode);
                cancel[0]=true;
                check(click(player,world,pos,InteractionHand.MAIN_HAND)==InteractionResult.FAIL&&same(tile.inventory.getStackInSlot(0),jar)&&count(player,jar)==0,"canceled extract preserves jar "+mode);
                cancel[0]=false;
                // Full inventory must return exactly one dropped jar, without deleting any held items.
                for(int slot=0;slot<player.getInventory().items.size();slot++)player.getInventory().items.set(slot,new ItemStack(Items.DIAMOND,64));
                var beforeDrops=drops(world,bounds,previous,jar);
                check(click(player,world,pos,InteractionHand.MAIN_HAND).consumesAction()&&tile.inventory.getStackInSlot(0).isEmpty(),"full inventory extract "+mode);
                check(drops(world,bounds,previous,jar)-beforeDrops==1&&count(player,jar)==0,"exactly one jar dropped when full "+mode);
                check(player.getInventory().items.stream().allMatch(s->s.is(Items.DIAMOND)&&s.getCount()==64),"full inventory unchanged "+mode);
                check(!click(player,world,pos,InteractionHand.MAIN_HAND).consumesAction()&&drops(world,bounds,previous,jar)==beforeDrops+1,"repeated empty extraction cannot duplicate "+mode);
                tile.inventory.setStackInSlot(0,ItemStack.EMPTY);
                world.removeBlock(pos,false);world.removeBlock(hopper,false);
            }
            System.out.println("TT FUNNEL INTERACTION MODES AUDIT: ALL PASS (SURVIVAL/CREATIVE,ordinary/void,main/offhand,original debit,metadata conservation,cancel insert/extract,full inventory drop,no duplicate extraction)");
        }finally{
            net.neoforged.neoforge.common.NeoForge.EVENT_BUS.unregister(listener);
            if(world.getBlockEntity(pos) instanceof FunnelEntity tile)tile.inventory.setStackInSlot(0,ItemStack.EMPTY);
            world.removeBlock(pos,false);world.removeBlock(hopper,false);
            for(var entity:world.getEntitiesOfClass(ItemEntity.class,bounds))if(!previous.contains(entity.getUUID()))entity.discard();
        }
    }
    private static InteractionResult click(net.neoforged.neoforge.common.util.FakePlayer player,ServerLevel world,BlockPos pos,InteractionHand hand){return player.gameMode.useItemOn(player,world,player.getItemInHand(hand),hand,new BlockHitResult(Vec3.atCenterOf(pos),Direction.UP,pos,false));}
    private static boolean same(ItemStack a,ItemStack b){return a.getCount()==b.getCount()&&ItemStack.isSameItemSameComponents(a,b);}
    private static int count(net.neoforged.neoforge.common.util.FakePlayer player,ItemStack jar){return player.getInventory().items.stream().filter(s->ItemStack.isSameItemSameComponents(s,jar)).mapToInt(ItemStack::getCount).sum();}
    private static int drops(ServerLevel world,AABB bounds,java.util.Set<java.util.UUID> previous,ItemStack jar){return world.getEntitiesOfClass(ItemEntity.class,bounds).stream().filter(e->!previous.contains(e.getUUID())&&ItemStack.isSameItemSameComponents(e.getItem(),jar)).mapToInt(e->e.getItem().getCount()).sum();}
    private static void check(boolean ok,String label){if(!ok)throw new IllegalStateException("Funnel interaction: "+label);}
}
