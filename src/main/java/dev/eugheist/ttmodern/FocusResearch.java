package dev.eugheist.ttmodern;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import thaumcraft.api.aspects.*;
import thaumcraft.api.research.*;
final class FocusResearch {
    static void research(String key,Item item,AspectList aspects,int x,int y,String parent,boolean secondary,ResearchPage.PageType pageType){
        research(key,item,aspects,x,y,parent,secondary,pageType,2);
    }
    static void research(String key,Item item,AspectList aspects,int x,int y,String parent,boolean secondary,ResearchPage.PageType pageType,int complexity,String... extraPages){
        var recipe=new ResearchPage("");recipe.type=pageType;recipe.recipeKey=key;
        var research=new ResearchItem(key,"THAUMICTINKERER",aspects,x,y,complexity,new ItemStack(item)){
            @Override public String getName(){return Component.translatable("ttresearch.name."+key).getString();}
            @Override public String getText(){return Component.translatable("ttresearch.lore."+key).getString();}
        }.setParents(parent).setConcealed();
        var pages=new java.util.ArrayList<ResearchPage>();pages.add(new ResearchPage("ttresearch.page."+key+".0"));for(var text:extraPages)pages.add(new ResearchPage(text));pages.add(recipe);research.setPages(pages.toArray(ResearchPage[]::new));
        if(pageType==ResearchPage.PageType.INFUSION_CRAFTING)research.setParentsHidden("INFUSION");if(secondary)research.setSecondary();research.registerResearchItem();
    }
    static Item item(String id){return net.minecraft.core.registries.BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("tc4modern",id));}
    static void register(){
        research("FOCUS_SMELT",ThaumicTinkererModern.FOCUS_SMELT.get(),new AspectList().add(Aspect.FIRE,2).add(Aspect.ENERGY,1).add(Aspect.MAGIC,1),-2,-2,"FOCUSEXCAVATION",false,ResearchPage.PageType.ARCANE_CRAFTING);
        var ingredients=java.util.List.of(Ingredient.of(item("focus_fire")),Ingredient.of(dev.eugheist.tc4modern.TC4Modern.NITOR.get()),Ingredient.of(item("focus_excavation")));
        var predicates=new java.util.ArrayList<java.util.function.Predicate<ItemStack>>();for(var ingredient:ingredients)predicates.add(ingredient::test);
        ThaumicTinkererModern.ARCANE_RECIPES.add(new dev.eugheist.tc4modern.ArcaneRecipes.Entry("FOCUS_SMELT","FOCUS_SMELT",3,1,ingredients,new ItemStack(ThaumicTinkererModern.FOCUS_SMELT.get()),new AspectList().add(Aspect.FIRE,10).add(Aspect.ORDER,5).add(Aspect.ENTROPY,6),new thaumcraft.api.crafting.ArcanePattern<ItemStack>("FOCUS_SMELT",3,1,predicates).setMirrored(true)));
        research("FOCUS_FLIGHT",ThaumicTinkererModern.FOCUS_FLIGHT.get(),new AspectList().add(Aspect.MOTION,1).add(Aspect.MAGIC,1).add(Aspect.AIR,2),-3,-4,"FOCUS_SMELT",false,ResearchPage.PageType.INFUSION_CRAFTING);
        ThaumicTinkererModern.INFUSION_RECIPES.add(new dev.eugheist.tc4modern.InfusionItemRecipes.Recipe("FOCUS_FLIGHT","FOCUS_FLIGHT",ThaumicTinkererModern.FOCUS_FLIGHT::get,3,new AspectList().add(Aspect.AIR,15).add(Aspect.MOTION,20).add(Aspect.TRAVEL,10),()->Items.ENDER_PEARL,java.util.List.of(()->Items.QUARTZ,()->Items.QUARTZ,()->Items.QUARTZ,()->Items.QUARTZ,()->Items.FEATHER,()->Items.FEATHER,dev.eugheist.tc4modern.TC4Modern.SHARDS.get(0)::get)));
        if(dev.eugheist.tc4modern.WarpConfig.allowMirrors()){
            research("FOCUS_DEFLECT",ThaumicTinkererModern.FOCUS_DEFLECT.get(),new AspectList().add(Aspect.MOTION,2).add(Aspect.AIR,1).add(Aspect.ORDER,1).add(Aspect.DEATH,1),-4,-3,"FOCUS_SMELT",true,ResearchPage.PageType.INFUSION_CRAFTING,3);
        }
        ThaumicTinkererModern.INFUSION_RECIPES.add(new dev.eugheist.tc4modern.InfusionItemRecipes.Recipe("FOCUS_DEFLECT","FOCUS_DEFLECT",ThaumicTinkererModern.FOCUS_DEFLECT::get,5,new AspectList().add(Aspect.AIR,15).add(Aspect.ARMOR,5).add(Aspect.ORDER,20),ThaumicTinkererModern.FOCUS_FLIGHT::get,java.util.List.of(()->item("mirror_glass"),()->item("mirror_glass"),()->item("paving_stone_warding"),dev.eugheist.tc4modern.TC4Modern.SHARDS.get(4)::get)));
        if(dev.eugheist.tc4modern.WarpConfig.allowMirrors())research("FOCUS_HEAL",ThaumicTinkererModern.FOCUS_HEAL.get(),new AspectList().add(Aspect.HEAL,2).add(Aspect.SOUL,1).add(Aspect.MAGIC,1),-6,-4,"FOCUS_DEFLECT",true,ResearchPage.PageType.INFUSION_CRAFTING);
        ThaumicTinkererModern.INFUSION_RECIPES.add(new dev.eugheist.tc4modern.InfusionItemRecipes.Recipe("FOCUS_HEAL","FOCUS_HEAL",ThaumicTinkererModern.FOCUS_HEAL::get,4,new AspectList().add(Aspect.HEAL,10).add(Aspect.SOUL,10).add(Aspect.LIFE,15),()->item("focus_pech"),java.util.List.of(()->Items.GOLDEN_CARROT,()->Items.GOLD_NUGGET,()->Items.GOLD_NUGGET,()->Items.GOLD_NUGGET)));
        if(dev.eugheist.tc4modern.WarpConfig.allowMirrors()){
            research("FOCUS_ENDER_CHEST",ThaumicTinkererModern.FOCUS_ENDER_CHEST.get(),new AspectList().add(Aspect.ELDRITCH,2).add(Aspect.VOID,1).add(Aspect.MAGIC,1),-6,-2,"FOCUS_DEFLECT",false,ResearchPage.PageType.ARCANE_CRAFTING);
            var enderIngredients=java.util.List.of(Ingredient.of(item("mirror")),Ingredient.of(Items.ENDER_CHEST),Ingredient.of(item("focus_portable_hole")));
            var enderPredicates=new java.util.ArrayList<java.util.function.Predicate<ItemStack>>();for(var ingredient:enderIngredients)enderPredicates.add(ingredient::test);
            ThaumicTinkererModern.ARCANE_RECIPES.add(new dev.eugheist.tc4modern.ArcaneRecipes.Entry("FOCUS_ENDER_CHEST","FOCUS_ENDER_CHEST",1,3,enderIngredients,new ItemStack(ThaumicTinkererModern.FOCUS_ENDER_CHEST.get()),new AspectList().add(Aspect.ORDER,10).add(Aspect.ENTROPY,10),new thaumcraft.api.crafting.ArcanePattern<ItemStack>("FOCUS_ENDER_CHEST",1,3,enderPredicates).setMirrored(true)));
        }
        if(dev.eugheist.tc4modern.WarpConfig.allowMirrors())research("FOCUS_DISLOCATION",ThaumicTinkererModern.FOCUS_DISLOCATION.get(),new AspectList().add(Aspect.ELDRITCH,2).add(Aspect.MAGIC,1).add(Aspect.EXCHANGE,1),-5,-5,"FOCUS_FLIGHT",true,ResearchPage.PageType.INFUSION_CRAFTING,2,"ttresearch.page.FOCUS_DISLOCATION.1");
        ThaumicTinkererModern.INFUSION_RECIPES.add(new dev.eugheist.tc4modern.InfusionItemRecipes.Recipe("FOCUS_DISLOCATION","FOCUS_DISLOCATION",ThaumicTinkererModern.FOCUS_DISLOCATION::get,8,new AspectList().add(Aspect.ELDRITCH,20).add(Aspect.DARKNESS,10).add(Aspect.VOID,25).add(Aspect.MAGIC,20).add(Aspect.TAINT,5),()->Items.ENDER_PEARL,java.util.List.of(()->Items.QUARTZ,()->Items.QUARTZ,()->Items.QUARTZ,()->Items.QUARTZ,()->item("amber"),()->item("amber"),()->item("amber"),()->Items.DIAMOND)));
        research("FOCUS_TELEKINESIS",ThaumicTinkererModern.FOCUS_TELEKINESIS.get(),new AspectList().add(Aspect.ELDRITCH,2).add(Aspect.MAGIC,1).add(Aspect.MOTION,1),-4,-6,"FOCUS_FLIGHT",true,ResearchPage.PageType.INFUSION_CRAFTING);
        ThaumicTinkererModern.INFUSION_RECIPES.add(new dev.eugheist.tc4modern.InfusionItemRecipes.Recipe("FOCUS_TELEKINESIS","FOCUS_TELEKINESIS",ThaumicTinkererModern.FOCUS_TELEKINESIS::get,5,new AspectList().add(Aspect.MOTION,10).add(Aspect.AIR,20).add(Aspect.ENTROPY,20).add(Aspect.MIND,10),()->Items.ENDER_PEARL,java.util.List.of(()->Items.QUARTZ,()->Items.QUARTZ,()->Items.QUARTZ,()->Items.QUARTZ,()->Items.IRON_INGOT,()->Items.GOLD_INGOT,dev.eugheist.tc4modern.TC4Modern.SHARDS.get(0)::get)));
    }
}
