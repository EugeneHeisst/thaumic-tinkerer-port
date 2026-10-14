package dev.eugheist.ttmodern;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.*;
import net.minecraft.world.item.enchantment.Enchantment;
final class CriticalEnchantments {
 static final ResourceKey<Enchantment> VALIANCE=key("valiance"),FINAL=key("final_strike");
 private static ResourceKey<Enchantment> key(String name){return ResourceKey.create(Registries.ENCHANTMENT,ResourceLocation.fromNamespaceAndPath("ttinkerer",name));}
 static float damage(float input,int finalRank,int valianceRank,float health,float maxHealth,java.util.function.IntUnaryOperator random){
  if(finalRank>0&&random.applyAsInt(20-finalRank)==0)input*=3;
  if(valianceRank>0&&health/maxHealth<.5f)input=(float)(input*(1+.1*valianceRank));
  return input;
 }
 private CriticalEnchantments(){}
}
