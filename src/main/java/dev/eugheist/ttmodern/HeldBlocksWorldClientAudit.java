package dev.eugheist.ttmodern;

/** Actual first-person camera frames after server inventory and main-arm synchronization. */
@net.neoforged.fml.common.EventBusSubscriber(modid="ttinkerer",value=net.neoforged.api.distmarker.Dist.CLIENT)
public final class HeldBlocksWorldClientAudit {
    static int ticks,phase,ready;
    static boolean initialized,hideGui,mayfly,flying,bob;
    static int selected,fov;
    static long day;
    static float yaw,pitch;
    static net.minecraft.world.phys.Vec3 position;
    static net.minecraft.world.entity.HumanoidArm arm;
    static net.minecraft.client.CameraType camera;
    static net.minecraft.world.level.GameType mode;
    static net.minecraft.nbt.ListTag inventory;
    static java.util.List<net.minecraft.world.item.Item> items;
    static final java.util.List<String> manifest=new java.util.ArrayList<>();
    @net.neoforged.bus.api.SubscribeEvent public static void tick(net.neoforged.neoforge.client.event.ClientTickEvent.Post event){
        if(!Boolean.getBoolean("ttinkerer.heldBlocksWorldAudit"))return;
        var mc=net.minecraft.client.Minecraft.getInstance();
        if(mc.player==null||mc.level==null||mc.screen instanceof net.minecraft.client.gui.screens.ReceivingLevelScreen)return;
        try{
            int t=++ticks;
            if(t==20){
                items=ThaumicTinkererModern.itemEntries().map(e->(net.minecraft.world.item.Item)e.get()).filter(i->i instanceof net.minecraft.world.item.BlockItem).sorted(java.util.Comparator.comparing(i->net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(i).toString())).toList();
                hideGui=mc.options.hideGui;camera=mc.options.getCameraType();bob=mc.options.bobView().get();fov=mc.options.fov().get();
                mc.options.hideGui=false;mc.options.setCameraType(net.minecraft.client.CameraType.FIRST_PERSON);mc.options.bobView().set(false);mc.options.fov().set(70);mc.setScreen(null);
                mc.getSingleplayerServer().submit(()->{
                    var p=mc.getSingleplayerServer().getPlayerList().getPlayer(mc.player.getUUID());position=p.position();yaw=p.getYRot();pitch=p.getXRot();arm=p.getMainArm();mode=p.gameMode.getGameModeForPlayer();mayfly=p.getAbilities().mayfly;flying=p.getAbilities().flying;selected=p.getInventory().selected;inventory=p.getInventory().save(new net.minecraft.nbt.ListTag());day=p.serverLevel().getDayTime();initialized=true;
                    p.setGameMode(net.minecraft.world.level.GameType.CREATIVE);p.getAbilities().mayfly=p.getAbilities().flying=true;p.onUpdateAbilities();p.serverLevel().setDayTime(6000);p.connection.teleport(.5,302,.5,180,-15);
                }).join();prepare(mc);ready=t+25;
            }
            if(initialized&&t>=ready){
                int index=phase/2-1;var expectedArm=phase%2==0?net.minecraft.world.entity.HumanoidArm.RIGHT:net.minecraft.world.entity.HumanoidArm.LEFT;
                var expected=index<0?net.minecraft.world.item.ItemStack.EMPTY:new net.minecraft.world.item.ItemStack(items.get(index));
                if(mc.player.getMainArm()!=expectedArm||!net.minecraft.world.item.ItemStack.isSameItemSameComponents(mc.player.getMainHandItem(),expected))throw new IllegalStateException("Held block inventory/main-arm packet mismatch phase "+phase);
                String name=index<0?"empty":net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(items.get(index)).getPath();
                String file="held-"+name+"-"+expectedArm.name().toLowerCase(java.util.Locale.ROOT)+".png";
                EnchanterClientAudit.Client.shot(mc,file);manifest.add(file+"\t"+(index<0?"empty":net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(items.get(index)))+"\t"+expectedArm.name().toLowerCase(java.util.Locale.ROOT));
                if(++phase==(items.size()+1)*2){
                    java.nio.file.Files.write(java.nio.file.Path.of("enchanter-audit","held-blocks.tsv"),manifest);restore(mc);
                    System.out.println("TT HELD BLOCKS WORLD CLIENT AUDIT: ALL PASS ("+items.size()+" registered block items, both actual first-person hands, native server inventory/main-arm sync, frames, restored state)");mc.stop();
                }else{prepare(mc);ready=t+20;}
            }
        }catch(Throwable error){try{restore(mc);}catch(Throwable cleanup){error.addSuppressed(cleanup);}throw new IllegalStateException("Held blocks world audit failed",error);}
    }
    static void prepare(net.minecraft.client.Minecraft mc){
        mc.getSingleplayerServer().submit(()->{
            var p=mc.getSingleplayerServer().getPlayerList().getPlayer(mc.player.getUUID());p.getInventory().clearContent();p.getInventory().selected=0;p.setMainArm(phase%2==0?net.minecraft.world.entity.HumanoidArm.RIGHT:net.minecraft.world.entity.HumanoidArm.LEFT);
            if(phase>=2)p.getInventory().setItem(0,new net.minecraft.world.item.ItemStack(items.get(phase/2-1)));
            p.connection.send(new net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket(0));p.inventoryMenu.broadcastFullState();
        }).join();
    }
    static void restore(net.minecraft.client.Minecraft mc){
        if(!initialized)return;
        mc.getSingleplayerServer().submit(()->{
            var p=mc.getSingleplayerServer().getPlayerList().getPlayer(mc.player.getUUID());p.getInventory().clearContent();p.getInventory().load(inventory);p.getInventory().selected=selected;p.setMainArm(arm);p.setGameMode(mode);p.getAbilities().mayfly=mayfly;p.getAbilities().flying=flying;p.onUpdateAbilities();p.serverLevel().setDayTime(day);p.connection.teleport(position.x,position.y,position.z,yaw,pitch);p.connection.send(new net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket(selected));p.inventoryMenu.broadcastFullState();
        }).join();mc.options.hideGui=hideGui;mc.options.setCameraType(camera);mc.options.bobView().set(bob);mc.options.fov().set(fov);initialized=false;
    }
}
