package dev.eugheist.ttmodern;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * ItemInfusedInkwell (build 164, named "(NYI)" by the original): 800-use scribing tools with the TC4 inkwell icon and a glint,
 * accepted wherever tc4modern takes IScribeTools; hidden from the creative tab and without its own research, like the original.
 */
public final class InfusedInkwellItem extends Item implements thaumcraft.api.IScribeTools {
    InfusedInkwellItem(){super(new Properties().durability(800));}
    @Override public boolean isFoil(ItemStack stack){return true;}
}
