package dev.eugheist.ttmodern;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
@EventBusSubscriber(modid="ttinkerer")
public final class VampirismEnchantment {
 static final ResourceKey<Enchantment> KEY=ResourceKey.create(Registries.ENCHANTMENT,ResourceLocation.parse("ttinkerer:vampirism"));
 @SubscribeEvent public static void incoming(net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent event){
  if(event.getEntity().level().isClientSide||!(event.getSource().getEntity() instanceof LivingEntity attacker))return;
  var holder=attacker.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolder(KEY).orElse(null);if(holder==null)return;
  int rank=attacker.getMainHandItem().getEnchantmentLevel(holder);if(rank<=0)return;
  attacker.heal(rank);var victim=event.getEntity();victim.level().playSound(null,victim.getX(),victim.getY(),victim.getZ(),net.minecraft.sounds.SoundEvent.createVariableRangeEvent(ResourceLocation.parse("tc4modern:zap")),net.minecraft.sounds.SoundSource.PLAYERS,.6f,1);
 }
 private VampirismEnchantment(){}
}
