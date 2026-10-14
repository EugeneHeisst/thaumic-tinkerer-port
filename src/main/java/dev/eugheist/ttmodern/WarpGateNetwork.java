package dev.eugheist.ttmodern;
@net.neoforged.fml.common.EventBusSubscriber(modid="ttinkerer",bus=net.neoforged.fml.common.EventBusSubscriber.Bus.MOD)
public final class WarpGateNetwork {
 public record Teleport(net.minecraft.core.BlockPos gate,int index) implements net.minecraft.network.protocol.common.custom.CustomPacketPayload {
  public static final Type<Teleport> TYPE=new Type<>(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("ttinkerer","warp_gate_teleport"));
  public static final net.minecraft.network.codec.StreamCodec<net.minecraft.network.FriendlyByteBuf,Teleport> CODEC=net.minecraft.network.codec.StreamCodec.composite(net.minecraft.core.BlockPos.STREAM_CODEC,Teleport::gate,net.minecraft.network.codec.ByteBufCodecs.VAR_INT,Teleport::index,Teleport::new);
  @Override public Type<? extends net.minecraft.network.protocol.common.custom.CustomPacketPayload> type(){return TYPE;}
 }
 static boolean handle(net.minecraft.server.level.ServerPlayer player,Teleport request){if(request.index()<0||request.index()>=10||!player.serverLevel().hasChunkAt(request.gate()))return false;if(!(player.serverLevel().getBlockEntity(request.gate()) instanceof WarpGateEntity gate)||!gate.valid(player)||!player.serverLevel().mayInteract(player,request.gate()))return false;return gate.teleport(player,request.index());}
 @net.neoforged.bus.api.SubscribeEvent public static void register(net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent event){event.registrar("1").playToServer(Teleport.TYPE,Teleport.CODEC,(packet,context)->context.enqueueWork(()->{if(context.player() instanceof net.minecraft.server.level.ServerPlayer player)handle(player,packet);}));}
}
