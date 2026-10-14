package dev.eugheist.ttmodern;
final class WarpGateClient {
 static void tick(WarpGateEntity gate){var mc=net.minecraft.client.Minecraft.getInstance();if(mc.screen!=null||mc.player==null||!mc.player.isShiftKeyDown())return;var p=gate.getBlockPos();if(mc.player.getBoundingBox().intersects(new net.minecraft.world.phys.AABB(p.getX(),p.getY()+1,p.getZ(),p.getX()+1,p.getY()+1.5,p.getZ()+1)))mc.setScreen(new WarpGateDestinations(gate));}
}
