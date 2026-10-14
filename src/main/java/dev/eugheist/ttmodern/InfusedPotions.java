package dev.eugheist.ttmodern;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionContents;
import thaumcraft.api.aspects.Aspect;
public final class InfusedPotions {
 static final net.neoforged.neoforge.registries.DeferredRegister<MobEffect> EFFECTS=net.neoforged.neoforge.registries.DeferredRegister.create(Registries.MOB_EFFECT,ThaumicTinkererModern.ID);
 public static final net.neoforged.neoforge.registries.DeferredHolder<MobEffect,MobEffect> AIR=effect("air"),FIRE=effect("fire"),EARTH=effect("earth"),WATER=effect("water");
 private static net.neoforged.neoforge.registries.DeferredHolder<MobEffect,MobEffect> effect(String name){return EFFECTS.register(name+"_imbued",()->new ImbuedEffect());}
 private static final class ImbuedEffect extends MobEffect {ImbuedEffect(){super(MobEffectCategory.HARMFUL,0);}}
 public static final class ImbuedPotionItem extends PotionItem {
  final Aspect aspect;
  ImbuedPotionItem(Aspect aspect,net.minecraft.core.Holder<MobEffect> effect){super(new Properties().stacksTo(1).component(net.minecraft.core.component.DataComponents.POTION_CONTENTS,new PotionContents(java.util.Optional.empty(),java.util.Optional.of(0),java.util.List.of(new MobEffectInstance(effect,3600,0)))));this.aspect=aspect;}
  @Override public String getDescriptionId(ItemStack stack){return getDescriptionId();}
  @Override public boolean isFoil(ItemStack stack){return true;}
  @Override public void appendHoverText(ItemStack stack,TooltipContext context,java.util.List<net.minecraft.network.chat.Component> lines,TooltipFlag flag){lines.add(net.minecraft.network.chat.Component.literal(aspect.getName()));}
 }
 static final net.neoforged.neoforge.registries.DeferredItem<ImbuedPotionItem> POTION_AIR=item("air",Aspect.AIR,AIR),POTION_FIRE=item("fire",Aspect.FIRE,FIRE),POTION_EARTH=item("earth",Aspect.EARTH,EARTH),POTION_WATER=item("water",Aspect.WATER,WATER);
 private static net.neoforged.neoforge.registries.DeferredItem<ImbuedPotionItem> item(String name,Aspect aspect,net.minecraft.core.Holder<MobEffect> effect){return ThaumicTinkererModern.ITEMS.register("infused_potion_"+name,()->new ImbuedPotionItem(aspect,effect));}
 static void register(net.neoforged.bus.api.IEventBus bus){EFFECTS.register(bus);}
}
