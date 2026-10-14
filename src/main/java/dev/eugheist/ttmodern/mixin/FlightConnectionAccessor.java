package dev.eugheist.ttmodern.mixin;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
@Mixin(ServerGamePacketListenerImpl.class)
public interface FlightConnectionAccessor {
    @Accessor("aboveGroundTickCount") void ttinkerer$resetFloatingTicks(int count);
}
