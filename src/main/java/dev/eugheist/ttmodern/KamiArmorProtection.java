package dev.eugheist.ttmodern;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.minecraft.world.damagesource.*;
/** Shares the base port's exact Forge 1.7.10 priority/cap normalization, including mixed armor. */
public final class KamiArmorProtection {
 public static boolean equipped(LivingEntity entity){for(var stack:entity.getArmorSlots())if(stack.getItem() instanceof KamiArmor)return true;return false;}
 public static float absorb(LivingEntity entity,DamageSource source,float amount){try{
  var property=Class.forName("dev.eugheist.tc4modern.LegacyFortressProtection$Property");var constructor=property.getDeclaredConstructor(int.class,double.class,int.class,ItemStack.class,EquipmentSlot.class);constructor.setAccessible(true);var helper=Class.forName("dev.eugheist.tc4modern.LegacyFortressProtection");var apply=helper.getDeclaredMethod("apply",LivingEntity.class,DamageSource.class,float.class,java.util.List.class);apply.setAccessible(true);var multiplier=dev.eugheist.tc4modern.FortressArmor.class.getDeclaredMethod("multiplier",LivingEntity.class);multiplier.setAccessible(true);double mult=(double)multiplier.invoke(null,entity);var magic=dev.eugheist.tc4modern.FortressArmor.class.getDeclaredMethod("magic",DamageSource.class);magic.setAccessible(true);boolean magical=(boolean)magic.invoke(null,source);var props=new java.util.ArrayList<Object>();
  for(var slot:java.util.List.of(EquipmentSlot.FEET,EquipmentSlot.LEGS,EquipmentSlot.CHEST,EquipmentSlot.HEAD)){var stack=entity.getItemBySlot(slot);if(!(stack.getItem() instanceof ArmorItem armor))continue;int priority=0,max=stack.getMaxDamage()+1-stack.getDamageValue();double ratio=armor.getDefense()/25d;
   if(armor instanceof KamiArmor){ratio=armor.getDefense()*.0425;max=Integer.MAX_VALUE;}
   else if(armor instanceof dev.eugheist.tc4modern.FortressArmor.Piece){if(magical){priority=1;ratio=armor.getDefense()/35d;}else if(source.is(net.minecraft.tags.DamageTypeTags.IS_FIRE)||source.is(net.minecraft.tags.DamageTypeTags.IS_EXPLOSION)){priority=1;ratio=armor.getDefense()/20d;}else if(source.is(net.minecraft.tags.DamageTypeTags.BYPASSES_ARMOR))ratio=0;ratio*=mult;}
   else if(armor instanceof dev.eugheist.tc4modern.VoidRobe.Piece){if(magical){priority=1;ratio=armor.getDefense()/35d;}else if(source.is(net.minecraft.tags.DamageTypeTags.BYPASSES_ARMOR))ratio=0;}
   else if(source.is(net.minecraft.tags.DamageTypeTags.BYPASSES_ARMOR))continue;
   props.add(constructor.newInstance(priority,ratio,max,stack,slot));
  }return (float)apply.invoke(null,entity,source,amount,props);
 }catch(ReflectiveOperationException e){throw new IllegalStateException("KAMI legacy armor bridge",e);}}
}
