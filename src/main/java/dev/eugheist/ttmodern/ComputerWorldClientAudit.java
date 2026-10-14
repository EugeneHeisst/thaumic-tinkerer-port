package dev.eugheist.ttmodern;
/** Isolated real menu/network/render audit; optional CC is required by the run wrapper. */
@net.neoforged.fml.common.EventBusSubscriber(modid="ttinkerer",value=net.neoforged.api.distmarker.Dist.CLIENT)
public final class ComputerWorldClientAudit {
 private static final net.minecraft.core.BlockPos AT=new net.minecraft.core.BlockPos(0,302,0);
 private static int ticks;
 private static net.minecraft.world.level.block.state.BlockState state;
 private static net.minecraft.nbt.CompoundTag tileData,knowledge;
 private static net.minecraft.world.phys.Vec3 position;
 private static float yaw,pitch;
 private static boolean mayfly,flying;
 @net.neoforged.bus.api.SubscribeEvent public static void tick(net.neoforged.neoforge.client.event.ClientTickEvent.Post event){
  if(!Boolean.getBoolean("ttinkerer.computerWorldAudit"))return;
  var mc=net.minecraft.client.Minecraft.getInstance();if(mc.player==null||mc.level==null||mc.screen instanceof net.minecraft.client.gui.screens.ReceivingLevelScreen)return;
  try{step(mc,++ticks);}catch(RuntimeException error){restore(mc);throw error;}
 }
 private static void step(net.minecraft.client.Minecraft mc,int t){
  if(t==20){knowledge=dev.eugheist.tc4modern.ClientKnowledge.knowledge.copy();mc.getSingleplayerServer().submit(()->{var p=mc.getSingleplayerServer().getPlayerList().getPlayer(mc.player.getUUID());var world=p.serverLevel();position=p.position();mayfly=p.getAbilities().mayfly;flying=p.getAbilities().flying;p.getAbilities().mayfly=true;p.getAbilities().flying=true;p.onUpdateAbilities();yaw=p.getYRot();pitch=p.getXRot();state=world.getBlockState(AT);var old=world.getBlockEntity(AT);tileData=old==null?null:old.saveWithFullMetadata(world.registryAccess());world.setBlockAndUpdate(AT,AspectAnalyzer.BLOCK.get().defaultBlockState());var tile=(AspectAnalyzer.Entity)world.getBlockEntity(AT);tile.inventory.setStackInSlot(0,new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.STONE,64));p.connection.teleport(.5,302,2.5,180,0);p.openMenu(new net.minecraft.world.SimpleMenuProvider((id,inv,player)->new AspectAnalyzerMenu(id,inv,tile),net.minecraft.network.chat.Component.literal("Analyzer audit")),buf->buf.writeBlockPos(AT));}).join();}
  if(t==40){var absent=AspectAnalyzerMenu.client(99,mc.player.getInventory(),new net.minecraft.core.BlockPos(240000,302,240000));EnchanterClientAudit.check(absent.slots.size()==37&&absent.slots.get(0).getItem().isEmpty(),"analyzer menu tolerates delayed block entity packet");EnchanterClientAudit.check(mc.screen instanceof AspectAnalyzerScreen&&mc.player.containerMenu instanceof AspectAnalyzerMenu,"analyzer actual registered menu/screen");EnchanterClientAudit.check(mc.player.containerMenu.slots.get(0).getItem().getCount()==64,"analyzer actual inventory packet64");dev.eugheist.tc4modern.ClientKnowledge.knowledge=new net.minecraft.nbt.CompoundTag();}
  if(t==60){var target=dev.eugheist.tc4modern.ScanTarget.item(mc.player.containerMenu.slots.get(0).getItem(),mc.level);EnchanterClientAudit.check(!target.aspects().isEmpty(),"analyzer render fixture has aspects");var scanned=new net.minecraft.nbt.CompoundTag();scanned.putBoolean(target.knowledgeKey(),true);dev.eugheist.tc4modern.ClientKnowledge.knowledge.put("scanned",scanned);}
  if(t==45)EnchanterClientAudit.Client.shot(mc,"analyzer-unscanned.png");
  if(t==65){var target=dev.eugheist.tc4modern.ScanTarget.item(mc.player.containerMenu.slots.get(0).getItem(),mc.level);EnchanterClientAudit.check(dev.eugheist.tc4modern.ResearchKnowledge.isScanned(dev.eugheist.tc4modern.ClientKnowledge.knowledge,target.knowledgeKey()),"analyzer scanned state survives rendered frames");EnchanterClientAudit.Client.shot(mc,"analyzer-scanned.png");}
  if(t==80){mc.player.closeContainer();restore(mc);System.out.println("TT COMPUTER WORLD CLIENT AUDIT: ALL PASS (native analyzer menu/open inventory packets,unscanned/scanned actual screen renders,restored block/player/client knowledge)");mc.stop();}
  if(t>300)throw new IllegalStateException("Computer world audit timeout");
 }
 private static void restore(net.minecraft.client.Minecraft mc){if(knowledge!=null){dev.eugheist.tc4modern.ClientKnowledge.knowledge=knowledge;knowledge=null;}if(state==null||mc.getSingleplayerServer()==null)return;mc.getSingleplayerServer().submit(()->{var p=mc.getSingleplayerServer().getPlayerList().getPlayer(mc.player.getUUID());var world=p.serverLevel();if(world.getBlockEntity(AT) instanceof AspectAnalyzer.Entity tile)tile.inventory.setStackInSlot(0,net.minecraft.world.item.ItemStack.EMPTY);world.setBlockAndUpdate(AT,state);if(tileData!=null&&world.getBlockEntity(AT)!=null)world.getBlockEntity(AT).loadWithComponents(tileData,world.registryAccess());p.connection.teleport(position.x,position.y,position.z,yaw,pitch);p.getAbilities().mayfly=mayfly;p.getAbilities().flying=flying;p.onUpdateAbilities();state=null;}).join();}
}
