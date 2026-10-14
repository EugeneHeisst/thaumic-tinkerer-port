package dev.eugheist.ttmodern;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
@EventBusSubscriber(modid="ttinkerer")
public final class QuickDrawEnchantment {
 static final ResourceKey<Enchantment> KEY=ResourceKey.create(Registries.ENCHANTMENT,ResourceLocation.parse("ttinkerer:quick_draw"));
 @SubscribeEvent(priority=net.neoforged.bus.api.EventPriority.HIGHEST)
 public static void tick(net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent.Tick event){
  if(!(event.getEntity() instanceof Player player)||!(event.getItem().getItem() instanceof BowItem)||event.getDuration()<=0)return;
  var holder=player.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolder(KEY).orElse(null);if(holder==null)return;
  int rank=player.getMainHandItem().getEnchantmentLevel(holder);if(rank<1||rank>2)return;
  int elapsed=event.getItem().getUseDuration(player)-event.getDuration();
  if(elapsed>=0&&elapsed%(6-rank)==0)event.setDuration(event.getDuration()-1);
 }
 private QuickDrawEnchantment(){}
}
