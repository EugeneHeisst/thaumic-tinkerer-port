package dev.eugheist.ttmodern;
/** Keeps optional movement APIs out of the ordinary locomotive class signature. */
@FunctionalInterface
interface PassengerTransfer {
 boolean complete(net.minecraft.server.level.ServerLevel level,net.minecraft.core.BlockPos target,net.minecraft.world.level.block.state.BlockState state);
}
