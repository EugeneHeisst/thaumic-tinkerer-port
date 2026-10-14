package dev.eugheist.ttmodern;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import thaumic.tinkerer.api.INoRemoveEnchant;

/** Build 164: one cloth + one enchanted item; remove only enchantments, use cloth once. */
public final class SpellClothRecipe extends CustomRecipe {
    public SpellClothRecipe(CraftingBookCategory category) { super(category); }
    private static ItemStack target(CraftingInput input) {
        boolean cloth=false; ItemStack target=ItemStack.EMPTY;
        for(int i=0;i<input.size();i++) {
            var stack=input.getItem(i); if(stack.isEmpty()) continue;
            if(stack.is(ThaumicTinkererModern.SPELL_CLOTH.get()) && !cloth) cloth=true;
            else if(stack.isEnchanted() && !(stack.getItem() instanceof INoRemoveEnchant) && target.isEmpty()) target=stack;
            else return ItemStack.EMPTY;
        }
        return cloth?target:ItemStack.EMPTY;
    }
    @Override public boolean matches(CraftingInput input, Level level) { return !target(input).isEmpty(); }
    @Override public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        var target=target(input); if(target.isEmpty()) return ItemStack.EMPTY;
        var result=target.copyWithCount(1); result.remove(DataComponents.ENCHANTMENTS); return result;
    }
    @Override public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
        var result=NonNullList.withSize(input.size(),ItemStack.EMPTY);
        if(target(input).isEmpty()) return result;
        for(int i=0;i<input.size();i++) if(input.getItem(i).is(ThaumicTinkererModern.SPELL_CLOTH.get())) {
            var cloth=input.getItem(i).copyWithCount(1); int next=cloth.getDamageValue()+1;
            if(next<cloth.getMaxDamage()) { cloth.setDamageValue(next); result.set(i,cloth); }
        }
        return result;
    }
    @Override public boolean canCraftInDimensions(int width,int height) { return width*height>=2; }
    @Override public RecipeSerializer<?> getSerializer() { return ThaumicTinkererModern.SPELL_CLOTH_SERIALIZER.get(); }
}
