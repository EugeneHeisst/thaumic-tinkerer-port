package dev.eugheist.ttmodern;
import net.minecraft.world.entity.player.Player;
@net.neoforged.fml.common.EventBusSubscriber(modid="ttinkerer",bus=net.neoforged.fml.common.EventBusSubscriber.Bus.MOD)
public final class AwakenedArmorStatus {
 public static boolean enabled(Player player){var data=player.getPersistentData().getCompound("ThaumicTinkerer");return !data.contains("GemArmor")||data.getBoolean("GemArmor");}
 public static void set(Player player,boolean value){var data=player.getPersistentData();var tag=data.getCompound("ThaumicTinkerer");tag.putBoolean("GemArmor",value);data.put("ThaumicTinkerer",tag);}
 @net.neoforged.fml.common.EventBusSubscriber(modid="ttinkerer")public static final class Login {@net.neoforged.bus.api.SubscribeEvent public static void login(net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent event){if(event.getEntity() instanceof net.minecraft.server.level.ServerPlayer player)net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(player,new Payload(enabled(player)));}}
 public record Payload(boolean enabled) implements net.minecraft.network.protocol.common.custom.CustomPacketPayload {
  public static final Type<Payload> TYPE=new Type<>(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("ttinkerer","armor_status"));
  public static final net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf,Payload> CODEC=net.minecraft.network.codec.StreamCodec.composite(net.minecraft.network.codec.ByteBufCodecs.BOOL,Payload::enabled,Payload::new);
  public Type<Payload> type(){return TYPE;}
 }
 @net.neoforged.bus.api.SubscribeEvent public static void register(net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent event){event.registrar("1").playBidirectional(Payload.TYPE,Payload.CODEC,(packet,context)->context.enqueueWork(()->set(context.player(),packet.enabled())));}
}
