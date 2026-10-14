package dev.eugheist.ttmodern;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
@EventBusSubscriber(modid="ttinkerer")
public final class HarvestSpeedEnchantments {
 static final ResourceKey<Enchantment> TUNNEL=key("tunnel"),SHATTER=key("shatter");
 private static ResourceKey<Enchantment> key(String name){return ResourceKey.create(Registries.ENCHANTMENT,ResourceLocation.fromNamespaceAndPath("ttinkerer",name));}
 static void speed(net.neoforged.neoforge.event.entity.player.PlayerEvent.BreakSpeed event){
  var player=event.getEntity();var stack=player.getMainHandItem();var registry=player.registryAccess().registryOrThrow(Registries.ENCHANTMENT);int shatter=stack.getEnchantmentLevel(registry.getHolderOrThrow(SHATTER)),tunnel=stack.getEnchantmentLevel(registry.getHolderOrThrow(TUNNEL));
  float speed=event.getNewSpeed();if(shatter>0)speed=event.getState().getDestroySpeed(player.level(),event.getPosition().orElse(player.blockPosition()))>20?speed*(3*shatter):(float)(speed*.8);
  var data=stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();if(tunnel>0&&data.contains("TTEnchantTunnelDir")){float difference=Math.abs(data.getFloat("TTEnchantTunnelDir")-player.getYRot());speed=(float)(speed*(difference<50?1+.2*tunnel:.3));}
  event.setNewSpeed(speed);
 }
 @SubscribeEvent public static void broken(net.neoforged.neoforge.event.level.BlockEvent.BreakEvent event){
  var player=event.getPlayer();if(player.level().isClientSide)return;var stack=player.getMainHandItem();var holder=player.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(TUNNEL);if(stack.getEnchantmentLevel(holder)>0)CustomData.update(DataComponents.CUSTOM_DATA,stack,t->t.putFloat("TTEnchantTunnelDir",player.getYRot()));
 }
 private HarvestSpeedEnchantments(){}
}
