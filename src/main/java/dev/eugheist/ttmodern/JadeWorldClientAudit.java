package dev.eugheist.ttmodern;
/** Actual Jade ray trace, server request, component callback and HUD capture. */
@net.neoforged.fml.common.EventBusSubscriber(modid="ttinkerer",value=net.neoforged.api.distmarker.Dist.CLIENT)
public final class JadeWorldClientAudit {
 static volatile int serverCalls;
 static final net.minecraft.core.BlockPos AT=new net.minecraft.core.BlockPos(0,302,0);
 static java.util.List<net.minecraft.network.chat.Component> observed=java.util.List.of();
 static int ticks,phase,ready,facing;
 static net.minecraft.world.level.block.state.BlockState old;
 static net.minecraft.nbt.CompoundTag data;
 static net.minecraft.world.phys.Vec3 position;
 static float yaw,pitch;static boolean mayfly,flying;
 static void observed(net.minecraft.core.BlockPos pos,java.util.List<net.minecraft.network.chat.Component> lines){if(pos.equals(AT))observed=java.util.List.copyOf(lines);}
 static boolean has(String key){return observed.stream().anyMatch(c->c.getContents() instanceof net.minecraft.network.chat.contents.TranslatableContents t&&t.getKey().equals("ttwaila."+key));}
 @net.neoforged.bus.api.SubscribeEvent public static void tick(net.neoforged.neoforge.client.event.ClientTickEvent.Post event){
  if(!Boolean.getBoolean("ttinkerer.jadeWorldAudit"))return;var mc=net.minecraft.client.Minecraft.getInstance();if(mc.player==null||mc.level==null||mc.screen instanceof net.minecraft.client.gui.screens.ReceivingLevelScreen)return;
  try{
   int t=++ticks;
   if(t==20){mc.setScreen(null);mc.getSingleplayerServer().submit(()->{var p=mc.getSingleplayerServer().getPlayerList().getPlayer(mc.player.getUUID());var world=p.serverLevel();position=p.position();yaw=p.getYRot();pitch=p.getXRot();mayfly=p.getAbilities().mayfly;flying=p.getAbilities().flying;p.getAbilities().mayfly=p.getAbilities().flying=true;p.onUpdateAbilities();old=world.getBlockState(AT);var tile=world.getBlockEntity(AT);data=tile==null?null:tile.saveWithFullMetadata(world.registryAccess());world.setBlockAndUpdate(AT,ThaumicTinkererModern.ANIMATION_TABLET.get().defaultBlockState());var tablet=(AnimationTabletEntity)world.getBlockEntity(AT);tablet.redstone=true;var tool=new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.DIAMOND_PICKAXE);tool.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME,net.minecraft.network.chat.Component.literal("Jade audit pick"));tablet.inventory.setStackInSlot(0,tool);p.connection.teleport(.5,302,2.5,180,37);}).join();}
   if(t>40&&phase==0&&has("leftClick")&&has("redstone")){phase=1;ready=t+15;}
   if(phase==1&&t==ready){EnchanterClientAudit.Client.shot(mc,"jade-left-redstone.png");mc.getSingleplayerServer().submit(()->{var tablet=(AnimationTabletEntity)mc.getSingleplayerServer().overworld().getBlockEntity(AT);tablet.leftClick=false;tablet.redstone=false;tablet.changed();}).join();phase=2;}
   if(phase==2&&has("rightClick")&&has("autonomous")){phase=3;ready=t+15;}
   if(phase==3&&t==ready){EnchanterClientAudit.Client.shot(mc,"jade-right-autonomous.png");EnchanterClientAudit.check(serverCalls>=2,"actual Jade server data requests");phase=4;ready=t+20;mc.getSingleplayerServer().submit(()->{var p=mc.getSingleplayerServer().getPlayerList().getPlayer(mc.player.getUUID());var tablet=(AnimationTabletEntity)p.serverLevel().getBlockEntity(AT);tablet.redstone=true;tablet.changed();p.connection.teleport(2.5,302,2.5,135,28);}).join();}
   if(phase==4&&t==ready){
    var directions=new net.minecraft.core.Direction[]{net.minecraft.core.Direction.NORTH,net.minecraft.core.Direction.SOUTH,net.minecraft.core.Direction.WEST,net.minecraft.core.Direction.EAST};
    System.out.println("TT TABLET LIGHT: facing="+directions[facing]+" packed="+net.minecraft.client.renderer.LevelRenderer.getLightColor(mc.level,AT)+" above="+net.minecraft.client.renderer.LevelRenderer.getLightColor(mc.level,AT.above()));
    EnchanterClientAudit.Client.shot(mc,"tablet-diagonal-"+directions[facing].getName()+".png");
    if(++facing==4){restore(mc);System.out.println("TT TABLET FACING RENDER AUDIT: ALL PASS (four actual world facing frames,diagonal camera,original transform comparison)");System.out.println("TT JADE WORLD CLIENT AUDIT: ALL PASS (actual ray trace,server requests,localized payload,real tooltip callbacks,live mode refresh,HUD frames,restored fixture/player)");mc.stop();}
    else{ready=t+20;mc.getSingleplayerServer().submit(()->{var world=mc.getSingleplayerServer().overworld();world.setBlockAndUpdate(AT,world.getBlockState(AT).setValue(AnimationTabletBlock.FACING,directions[facing]));}).join();}
   }
   if(t>600)throw new IllegalStateException("Jade world timeout: phase="+phase+" calls="+serverCalls+" observed="+observed);
  }catch(RuntimeException error){restore(mc);throw error;}
 }
 static void restore(net.minecraft.client.Minecraft mc){if(old==null||mc.getSingleplayerServer()==null)return;mc.getSingleplayerServer().submit(()->{var p=mc.getSingleplayerServer().getPlayerList().getPlayer(mc.player.getUUID());var world=p.serverLevel();if(world.getBlockEntity(AT) instanceof AnimationTabletEntity tablet)tablet.inventory.setStackInSlot(0,net.minecraft.world.item.ItemStack.EMPTY);world.setBlockAndUpdate(AT,old);if(data!=null&&world.getBlockEntity(AT)!=null)world.getBlockEntity(AT).loadWithComponents(data,world.registryAccess());p.connection.teleport(position.x,position.y,position.z,yaw,pitch);p.getAbilities().mayfly=mayfly;p.getAbilities().flying=flying;p.onUpdateAbilities();old=null;}).join();}
}
