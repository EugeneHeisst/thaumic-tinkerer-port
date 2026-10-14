package dev.eugheist.ttmodern;

@net.neoforged.fml.common.EventBusSubscriber(modid="ttinkerer",value=net.neoforged.api.distmarker.Dist.CLIENT)
public final class FunnelWorldClientAudit {
    static final net.minecraft.core.BlockPos AT=new net.minecraft.core.BlockPos(0,302,0);
    static final String[] CASES={"empty","jar-empty","jar-filled","jar-void"};
    static final java.util.Map<net.minecraft.core.BlockPos,net.minecraft.world.level.block.state.BlockState> states=new java.util.LinkedHashMap<>();
    static final java.util.Map<net.minecraft.core.BlockPos,net.minecraft.nbt.CompoundTag> data=new java.util.HashMap<>();
    static int ticks,phase,ready;static boolean initialized,mayfly,flying,hideGui;static float yaw,pitch;static net.minecraft.world.phys.Vec3 position;
    static net.minecraft.world.level.GameType originalMode;
    static net.minecraft.nbt.ListTag inventory;static int selected;
    @net.neoforged.bus.api.SubscribeEvent public static void tick(net.neoforged.neoforge.client.event.ClientTickEvent.Post event){
        if(!Boolean.getBoolean("ttinkerer.funnelWorldAudit"))return;
        var mc=net.minecraft.client.Minecraft.getInstance();if(mc.player==null||mc.level==null||mc.screen instanceof net.minecraft.client.gui.screens.ReceivingLevelScreen)return;
        try{
            int t=++ticks;
            if(t==20){
                hideGui=mc.options.hideGui;mc.options.hideGui=true;mc.setScreen(null);
                mc.getSingleplayerServer().submit(()->{
                    var p=mc.getSingleplayerServer().getPlayerList().getPlayer(mc.player.getUUID());var world=p.serverLevel();position=p.position();yaw=p.getYRot();pitch=p.getXRot();mayfly=p.getAbilities().mayfly;flying=p.getAbilities().flying;
                    for(var pos:java.util.List.of(AT,AT.below())){states.put(pos,world.getBlockState(pos));var tile=world.getBlockEntity(pos);if(tile!=null)data.put(pos,tile.saveWithFullMetadata(world.registryAccess()));}
                    initialized=true;p.getAbilities().mayfly=p.getAbilities().flying=true;p.onUpdateAbilities();
                    world.setBlockAndUpdate(AT.below(),net.minecraft.world.level.block.Blocks.HOPPER.defaultBlockState());world.setBlockAndUpdate(AT,ThaumicTinkererModern.FUNNEL.get().defaultBlockState());
                    p.connection.teleport(2.5,302,2.5,135,26);
                }).join();ready=t+30;
            }
            if(initialized&&t>=ready&&phase>=4){interactions(mc,t);return;}
            if(initialized&&t>=ready){
                var tile=(FunnelEntity)mc.level.getBlockEntity(AT);if(tile==null)throw new IllegalStateException("Funnel fixture missing on client");
                if(phase==0&& !tile.inventory.getStackInSlot(0).isEmpty())throw new IllegalStateException("Empty funnel fixture");
                if(phase>0){var actual=tile.inventory.getStackInSlot(0);if(!net.minecraft.world.item.ItemStack.isSameItemSameComponents(actual,jar(phase)))throw new IllegalStateException("Funnel jar data did not synchronize");}
                EnchanterClientAudit.Client.shot(mc,"funnel-"+CASES[phase]+".png");
                if(++phase==CASES.length){prepareMode(mc,0);ready=t+25;return;}
                mc.getSingleplayerServer().submit(()->{var f=(FunnelEntity)mc.getSingleplayerServer().overworld().getBlockEntity(AT);f.inventory.setStackInSlot(0,jar(phase));}).join();ready=t+25;
            }
            if(t>450)throw new IllegalStateException("Funnel world audit timeout");
        }catch(RuntimeException error){restore(mc);throw error;}
    }
    static void prepareMode(net.minecraft.client.Minecraft mc,int mode){
        mc.getSingleplayerServer().submit(()->{
            var p=mc.getSingleplayerServer().getPlayerList().getPlayer(mc.player.getUUID());
            if(originalMode==null){originalMode=p.gameMode.getGameModeForPlayer();inventory=p.getInventory().save(new net.minecraft.nbt.ListTag());selected=p.getInventory().selected;}
            var tile=(FunnelEntity)p.serverLevel().getBlockEntity(AT);tile.inventory.setStackInSlot(0,net.minecraft.world.item.ItemStack.EMPTY);
            p.setGameMode(mode==0?net.minecraft.world.level.GameType.SURVIVAL:net.minecraft.world.level.GameType.CREATIVE);
            p.getAbilities().mayfly=p.getAbilities().flying=true;p.onUpdateAbilities();
            p.getInventory().clearContent();p.getInventory().selected=0;p.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,jar(2));
            p.connection.send(new net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket(0));p.inventoryMenu.broadcastFullState();
        }).join();
    }
    static void interactions(net.minecraft.client.Minecraft mc,int t){
        int mode=(phase-4)/3,step=(phase-4)%3;
        var expected=mode==0?net.minecraft.world.level.GameType.SURVIVAL:net.minecraft.world.level.GameType.CREATIVE;
        if(mc.gameMode.getPlayerMode()!=expected)throw new IllegalStateException("Funnel client game-mode packet missing "+expected);
        var tile=(FunnelEntity)mc.level.getBlockEntity(AT);
        if(step==0){
            if(!tile.inventory.getStackInSlot(0).isEmpty()||!net.minecraft.world.item.ItemStack.isSameItemSameComponents(mc.player.getMainHandItem(),jar(2)))throw new IllegalStateException("Funnel client insert fixture "+expected);
        }else if(step==1){
            if(!mc.player.getMainHandItem().isEmpty()||!net.minecraft.world.item.ItemStack.isSameItemSameComponents(tile.inventory.getStackInSlot(0),jar(2)))throw new IllegalStateException("Funnel client insert/debit sync "+expected);
        }else{
            long count=mc.player.getInventory().items.stream().filter(s->net.minecraft.world.item.ItemStack.isSameItemSameComponents(s,jar(2))).mapToInt(net.minecraft.world.item.ItemStack::getCount).sum();
            if(!tile.inventory.getStackInSlot(0).isEmpty()||count!=1)throw new IllegalStateException("Funnel client extract conservation/sync "+expected+" count="+count);
            if(mode==0){prepareMode(mc,1);phase++;ready=t+25;return;}
            restore(mc);
            System.out.println("TT FUNNEL CLIENT INTERACTION MODES AUDIT: ALL PASS (actual use-item packets SURVIVAL/CREATIVE,mode/inventory/BE synchronization,one jar debit and return)");
            System.out.println("TT FUNNEL WORLD CLIENT AUDIT: ALL PASS (actual empty/filled/void jar frames,server inventory synchronization,restored fixture/player)");mc.stop();return;
        }
        var result=mc.gameMode.useItemOn(mc.player,net.minecraft.world.InteractionHand.MAIN_HAND,new net.minecraft.world.phys.BlockHitResult(net.minecraft.world.phys.Vec3.atCenterOf(AT),net.minecraft.core.Direction.UP,AT,false));
        if(!result.consumesAction())throw new IllegalStateException("Funnel client click rejected "+expected+" step="+step);
        phase++;ready=t+25;
    }
    static net.minecraft.world.item.ItemStack jar(int phase){
        var stack=new net.minecraft.world.item.ItemStack(phase==3?dev.eugheist.tc4modern.AlchemyMachines.VOID_JAR.get():dev.eugheist.tc4modern.EssentiaDevices.JAR.get());
        if(phase>=2){var tag=new net.minecraft.nbt.CompoundTag();new thaumcraft.api.aspects.AspectList().add(thaumcraft.api.aspects.Aspect.FIRE,32).writeToNBT(tag);tag.putString("AspectFilter",thaumcraft.api.aspects.Aspect.FIRE.getTag());stack.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA,net.minecraft.world.item.component.CustomData.of(tag));}
        return stack;
    }
    static void restore(net.minecraft.client.Minecraft mc){
        if(!initialized||mc.getSingleplayerServer()==null)return;
        mc.getSingleplayerServer().submit(()->{var world=mc.getSingleplayerServer().overworld();if(world.getBlockEntity(AT) instanceof FunnelEntity f)f.inventory.setStackInSlot(0,net.minecraft.world.item.ItemStack.EMPTY);
            for(var entry:states.entrySet()){world.setBlockAndUpdate(entry.getKey(),entry.getValue());if(data.containsKey(entry.getKey())&&world.getBlockEntity(entry.getKey())!=null)world.getBlockEntity(entry.getKey()).loadWithComponents(data.get(entry.getKey()),world.registryAccess());}
            var p=mc.getSingleplayerServer().getPlayerList().getPlayer(mc.player.getUUID());
            if(originalMode!=null){p.setGameMode(originalMode);p.getInventory().clearContent();p.getInventory().load(inventory);p.getInventory().selected=selected;p.connection.send(new net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket(selected));p.inventoryMenu.broadcastFullState();originalMode=null;}
            p.connection.teleport(position.x,position.y,position.z,yaw,pitch);p.getAbilities().mayfly=mayfly;p.getAbilities().flying=flying;p.onUpdateAbilities();
        }).join();mc.options.hideGui=hideGui;initialized=false;
    }
}
