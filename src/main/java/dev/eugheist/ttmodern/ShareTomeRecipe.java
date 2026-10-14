package dev.eugheist.ttmodern;
import net.minecraft.core.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
public final class ShareTomeRecipe extends CustomRecipe{
    public ShareTomeRecipe(CraftingBookCategory category){super(category);}
    @Override public NonNullList<Ingredient> getIngredients(){return NonNullList.of(Ingredient.EMPTY,Ingredient.EMPTY,Ingredient.of(dev.eugheist.tc4modern.TC4Modern.SCRIBING_TOOLS.get()),Ingredient.EMPTY,Ingredient.of(Items.PAPER),Ingredient.of(dev.eugheist.tc4modern.TC4Modern.THAUMONOMICON.get()),Ingredient.of(Items.PAPER),Ingredient.EMPTY,Ingredient.of(Items.PAPER),Ingredient.EMPTY);}
    @Override public boolean matches(CraftingInput input,Level level){if(!TinkererConfig.survivalShareTome()||input.width()!=3||input.height()!=3)return false;var ingredients=getIngredients();for(int i=0;i<9;i++)if(!ingredients.get(i).test(input.getItem(i)))return false;return true;}
    @Override public ItemStack assemble(CraftingInput input,HolderLookup.Provider registries){return new ItemStack(ThaumicTinkererModern.SHARE_TOME.get());}
    @Override public ItemStack getResultItem(HolderLookup.Provider registries){return new ItemStack(ThaumicTinkererModern.SHARE_TOME.get());}
    @Override public boolean canCraftInDimensions(int width,int height){return width>=3&&height>=3;}
    @Override public RecipeSerializer<?> getSerializer(){return ThaumicTinkererModern.SHARE_TOME_SERIALIZER.get();}
}
