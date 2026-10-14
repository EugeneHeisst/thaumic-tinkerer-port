package dev.eugheist.ttmodern;
import net.minecraft.world.entity.player.Player;
/** Original separate 0..20 soul-health reserve, never vanilla absorption. */
@net.neoforged.fml.common.EventBusSubscriber(modid="ttinkerer")
public final class SoulHearts {
 public static int get(Player player){return player.getPersistentData().getCompound("ThaumicTinkerer").getInt("soulHearts");}
 public static void set(Player player,int value){var data=player.getPersistentData();var tag=data.getCompound("ThaumicTinkerer");tag.putInt("soulHearts",Math.clamp(value,0,20));data.put("ThaumicTinkerer",tag);}
 public static void add(Player player){set(player,get(player)+1);sync(player);}
 public static int remove(Player player,int amount){int next=get(player)-amount;set(player,Math.max(0,next));return Math.max(0,-next);}
 static void sync(Player player){if(player instanceof net.minecraft.server.level.ServerPlayer server&&!(server instanceof net.neoforged.neoforge.common.util.FakePlayer))net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(server,new Payload(get(player)));}
 public static float absorb(Player player,float amount){if(!KamiStartupConfig.enabled()||amount<=0)return amount;int result=remove(player,(int)amount);sync(player);return result;}
 @net.neoforged.bus.api.SubscribeEvent public static void login(net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent event){if(KamiStartupConfig.enabled())sync(event.getEntity());}
 public record Payload(int hearts) implements net.minecraft.network.protocol.common.custom.CustomPacketPayload {
  public static final Type<Payload> TYPE=new Type<>(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("ttinkerer","soul_hearts"));
  public static final net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf,Payload> CODEC=net.minecraft.network.codec.StreamCodec.composite(net.minecraft.network.codec.ByteBufCodecs.INT,Payload::hearts,Payload::new);
  public Type<Payload> type(){return TYPE;}
 }
 @net.neoforged.fml.common.EventBusSubscriber(modid="ttinkerer",bus=net.neoforged.fml.common.EventBusSubscriber.Bus.MOD)
 public static class Network {
  @net.neoforged.bus.api.SubscribeEvent public static void register(net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent event){event.registrar("1").playToClient(Payload.TYPE,Payload.CODEC,(packet,context)->context.enqueueWork(()->set(context.player(),packet.hearts())));}
 }
}
