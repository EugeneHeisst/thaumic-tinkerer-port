package dev.eugheist.ttmodern;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
final class PlatformClientAudit {
 static final BlockPos POS=new BlockPos(6,202,6);static volatile boolean landed,descended;
 static void tick(Minecraft mc,int tick){
  if(tick==190){mc.player.closeContainer();var uuid=mc.player.getUUID();mc.getSingleplayerServer().execute(()->{var p=mc.getSingleplayerServer().getPlayerList().getPlayer(uuid);p.serverLevel().setBlockAndUpdate(POS,ThaumicTinkererModern.PLATFORM.get().defaultBlockState());p.getAbilities().flying=false;p.getAbilities().mayfly=false;p.onUpdateAbilities();p.setDeltaMovement(0,0,0);p.connection.teleport(6.5,206,6.5,180,0);});}
  if(tick==225){EnchanterClientAudit.check(Math.abs(mc.player.getY()-203)<.01,"native client falling player lands on platform");var uuid=mc.player.getUUID();mc.getSingleplayerServer().execute(()->{var p=mc.getSingleplayerServer().getPlayerList().getPlayer(uuid);EnchanterClientAudit.check(Math.abs(p.getY()-203)<.01,"native server agrees player is supported on platform");landed=true;});}
  if(tick==230)mc.options.keyShift.setDown(true);
  if(tick==250){EnchanterClientAudit.check(landed&&mc.player.getY()<202.5,"native crouch input drops player through platform");mc.options.keyShift.setDown(false);var uuid=mc.player.getUUID();mc.getSingleplayerServer().execute(()->{var p=mc.getSingleplayerServer().getPlayerList().getPlayer(uuid);EnchanterClientAudit.check(p.getY()<202.5,"server agrees crouching player descended");descended=true;var tile=(PlatformEntity)p.level().getBlockEntity(POS);tile.camouflage(net.minecraft.world.level.block.Blocks.STONE.defaultBlockState());p.getAbilities().mayfly=true;p.getAbilities().flying=true;p.onUpdateAbilities();p.connection.teleport(6.5,205,9.5,180,35);});}
  if(tick==270){var tile=(PlatformEntity)mc.level.getBlockEntity(POS);EnchanterClientAudit.check(descended&&tile.camouflage()!=null&&tile.camouflage().is(net.minecraft.world.level.block.Blocks.STONE),"platform camouflage synchronizes to client");var renderer=new PlatformRenderer(null);var buffers=mc.renderBuffers().bufferSource();renderer.render(tile,0,new com.mojang.blaze3d.vertex.PoseStack(),buffers,15728880,net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY);buffers.endBatch();EnchanterClientAudit.Client.shot(mc,"platform-camouflage.png");}
 }
 private PlatformClientAudit(){}
}
