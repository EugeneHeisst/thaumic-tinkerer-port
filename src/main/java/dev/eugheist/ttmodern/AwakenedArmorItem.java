package dev.eugheist.ttmodern;
import net.minecraft.world.item.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.*;
import net.minecraft.world.level.Level;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
public abstract class AwakenedArmorItem extends KamiArmor {
 AwakenedArmorItem(Type type){super(type);}
 public static boolean disabled(ItemStack stack){return stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag().getBoolean("AwakenedDisabled");}
 public static void toggle(ItemStack stack){boolean next=!disabled(stack);CustomData.update(DataComponents.CUSTOM_DATA,stack,t->t.putBoolean("AwakenedDisabled",next));}
 @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){var stack=player.getItemInHand(hand);if(player.isShiftKeyDown()){if(!level.isClientSide){toggle(stack);level.playSound(null,player.blockPosition(),net.minecraft.sounds.SoundEvents.EXPERIENCE_ORB_PICKUP,net.minecraft.sounds.SoundSource.PLAYERS,.3f,.1f);player.displayClientMessage(net.minecraft.network.chat.Component.translatable("ttmisc.awakenedArmor"+(disabled(stack)?1:0)),true);}return InteractionResultHolder.sidedSuccess(stack,level.isClientSide);}return super.use(level,player,hand);}
 @Override public net.minecraft.resources.ResourceLocation getArmorTexture(ItemStack stack,Entity entity,EquipmentSlot slot,ArmorMaterial.Layer layer,boolean inner){return net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("ttinkerer","textures/model/ichorgem"+(slot==EquipmentSlot.LEGS?2:1)+".png");}
 @Override public void appendHoverText(ItemStack stack,Item.TooltipContext context,java.util.List<net.minecraft.network.chat.Component> lines,TooltipFlag flag){super.appendHoverText(stack,context,lines,flag);if(disabled(stack))lines.add(net.minecraft.network.chat.Component.translatable("ttmisc.awakenedArmor1"));}
}
