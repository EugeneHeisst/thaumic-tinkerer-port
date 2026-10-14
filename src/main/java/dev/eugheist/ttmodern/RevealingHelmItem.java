package dev.eugheist.ttmodern;
import net.minecraft.world.item.*;
import net.minecraft.world.entity.*;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.api.aspects.Aspect;
public final class RevealingHelmItem extends ArmorItem implements thaumcraft.api.IRepairable,thaumcraft.api.IGoggles,thaumcraft.api.nodes.IRevealer,thaumcraft.api.IVisDiscountGear {
 public RevealingHelmItem(){super(dev.eugheist.tc4modern.ThaumiumContent.ARMOR,Type.HELMET,new Properties().durability(500).rarity(Rarity.UNCOMMON));}
 @Override public boolean showNodes(ItemStack stack,LivingEntity entity){return true;}
 @Override public boolean showIngamePopups(ItemStack stack,LivingEntity entity){return true;}
 @Override public int getVisDiscount(ItemStack stack,net.minecraft.world.entity.player.Player player,Aspect aspect){return 5;}
 @Override public ResourceLocation getArmorTexture(ItemStack stack,Entity entity,EquipmentSlot slot,ArmorMaterial.Layer layer,boolean inner){return ResourceLocation.parse("ttinkerer:textures/model/revealing.png");}
 @Override public boolean isValidRepairItem(ItemStack stack,ItemStack material){return material.is(dev.eugheist.tc4modern.TC4Modern.THAUMIUM.get())||super.isValidRepairItem(stack,material);}
 @Override public void appendHoverText(ItemStack stack,Item.TooltipContext context,java.util.List<net.minecraft.network.chat.Component> lines,TooltipFlag flag){super.appendHoverText(stack,context,lines,flag);lines.add(net.minecraft.network.chat.Component.translatable("tc.visdiscount").append(": 5%"));}
}
