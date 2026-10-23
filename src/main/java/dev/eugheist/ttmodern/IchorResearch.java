package dev.eugheist.ttmodern;
import net.minecraft.world.item.*;
import thaumcraft.api.aspects.*;
import thaumcraft.api.research.*;
public final class IchorResearch {
 static void register(){
  var page=new ResearchPage("");page.type=ResearchPage.PageType.INFUSION_CRAFTING;page.recipeKey="ICHOR";
  new KamiResearch("ICHOR",new AspectList().add(Aspect.MAN,1).add(Aspect.LIGHT,2).add(Aspect.SOUL,1).add(Aspect.TAINT,1),9,8,5,new ItemStack(KamiResources.item(KamiResources.Kind.ICHOR))).setPages(new ResearchPage("ttresearch.page.ICHOR.0"),page).registerResearchItem();
  ThaumicTinkererModern.INFUSION_RECIPES.add(new dev.eugheist.tc4modern.InfusionItemRecipes.Recipe("ICHOR","ICHOR",()->KamiResources.item(KamiResources.Kind.ICHOR),7,new AspectList().add(Aspect.MAN,32).add(Aspect.LIGHT,32).add(Aspect.SOUL,64),()->Items.NETHER_STAR,java.util.List.of(()->Items.DIAMOND,()->KamiResources.item(KamiResources.Kind.ENDER_SHARD),()->Items.ENDER_EYE,()->KamiResources.item(KamiResources.Kind.NETHER_SHARD))));
 }
 public static ItemStack output(String key){return key.equals("SKY_PEARL")?new ItemStack(SkyPearl.ITEM.get(),2):key.equals("ICHOR")?new ItemStack(KamiResources.item(KamiResources.Kind.ICHOR),8):ItemStack.EMPTY;}
}
