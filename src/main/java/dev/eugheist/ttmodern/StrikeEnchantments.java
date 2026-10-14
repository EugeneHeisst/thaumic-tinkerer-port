package dev.eugheist.ttmodern;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
@EventBusSubscriber(modid="ttinkerer")
public final class StrikeEnchantments {
 static final ResourceKey<Enchantment> FOCUSED=key("focused_strike"),DISPERSED=key("dispersed_strikes");
 private static ResourceKey<Enchantment> key(String name){return ResourceKey.create(Registries.ENCHANTMENT,ResourceLocation.fromNamespaceAndPath("ttinkerer",name));}
 @SubscribeEvent public static void incoming(net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent event){
  if(event.getEntity().level().isClientSide||!(event.getSource().getEntity() instanceof LivingEntity attacker))return;
  var stack=attacker.getMainHandItem();var registry=attacker.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
  FallCombatEnchantments.pounce(event,attacker);
  int finalRank=stack.getEnchantmentLevel(registry.getHolderOrThrow(CriticalEnchantments.FINAL)),valianceRank=stack.getEnchantmentLevel(registry.getHolderOrThrow(CriticalEnchantments.VALIANCE));
  if(finalRank>0||valianceRank>0)event.setAmount(CriticalEnchantments.damage(event.getAmount(),finalRank,valianceRank,attacker.getHealth(),attacker.getMaxHealth(),bound->new java.util.Random().nextInt(bound)));
  int focused=stack.getEnchantmentLevel(registry.getHolderOrThrow(FOCUSED)),dispersed=stack.getEnchantmentLevel(registry.getHolderOrThrow(DISPERSED));if(focused<=0&&dispersed<=0)return;
  var data=stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();int target=event.getEntity().getId(),last=data.getInt("TTEnchantLastTarget"),successive=data.getInt("TTEnchantSuccessiveStrike");
  if(last!=target){data.putInt("TTEnchantSuccessiveStrike",0);successive=0;}else{data.putInt("TTEnchantSuccessiveStrike",successive+1);successive=1;} // Exact build164 local-counter behavior.
  float damage=event.getAmount();if(focused>0){damage/=2;damage=(float)(damage+.5*successive*damage*focused);}if(dispersed>0){damage*=1+successive/5;damage/=1+successive*2;}
  data.putInt("TTEnchantLastTarget",target);stack.set(DataComponents.CUSTOM_DATA,CustomData.of(data));event.setAmount(damage);
 }
 private StrikeEnchantments(){}
}
