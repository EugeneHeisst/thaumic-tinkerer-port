package dev.eugheist.ttmodern;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import thaumcraft.api.aspects.*;
import thaumcraft.api.research.*;
import java.util.*;
final class TransvectorResearch {
    private static ResearchPage arcane(String key){var p=new ResearchPage("");p.type=ResearchPage.PageType.ARCANE_CRAFTING;p.recipeKey=key;return p;}
    static void register(){
        new ResearchItem("INTERFACE","THAUMICTINKERER",new AspectList().add(Aspect.ENTROPY,4).add(Aspect.ORDER,4),-4,2,1,new ItemStack(ThaumicTinkererModern.INTERFACE.get())){
            @Override public String getName(){return Component.translatable("ttresearch.name.INTERFACE").getString();}
            @Override public String getText(){return Component.translatable("ttresearch.lore.INTERFACE").getString();}
        }.setParents("DARK_QUARTZ").setPages(new ResearchPage("ttresearch.page.INTERFACE.0"),arcane("INTERFACE"),new ResearchPage("ttresearch.page.INTERFACE.1"),arcane("INTERFACE1"),new ResearchPage("ttresearch.page.INTERFACE.2")).registerResearchItem();
        var stone=dev.eugheist.tc4modern.TC4Modern.ARCANE_STONE.get().asItem();
        recipe("INTERFACE",new ItemStack(ThaumicTinkererModern.INTERFACE.get()),new AspectList().add(Aspect.ORDER,12).add(Aspect.ENTROPY,16),stone,Items.REDSTONE,stone,Items.LAPIS_LAZULI,Items.ENDER_PEARL,Items.LAPIS_LAZULI,stone,Items.REDSTONE,stone);
        recipe("INTERFACE1",new ItemStack(ThaumicTinkererModern.CONNECTOR.get()),new AspectList().add(Aspect.ORDER,2),null,Items.IRON_INGOT,null,null,Items.STICK,Items.IRON_INGOT,dev.eugheist.tc4modern.TC4Modern.SHARDS.get(4).get(),null,null);
    }
    private static void recipe(String key,ItemStack result,AspectList cost,Item... items){
        var ingredients=new ArrayList<Ingredient>();var predicates=new ArrayList<java.util.function.Predicate<ItemStack>>();
        for(var item:items){var ingredient=item==null?Ingredient.EMPTY:Ingredient.of(item);ingredients.add(ingredient);predicates.add(item==null?null:ingredient::test);}
        ThaumicTinkererModern.ARCANE_RECIPES.add(new dev.eugheist.tc4modern.ArcaneRecipes.Entry(key,"INTERFACE",3,3,ingredients,result,cost,new thaumcraft.api.crafting.ArcanePattern<ItemStack>("INTERFACE",3,3,predicates).setMirrored(true)));
    }
}
