package dev.eugheist.ttmodern;
import net.minecraft.world.item.*;
import thaumcraft.api.aspects.*;
public final class InfusedSeedRecipes {
 private static final Aspect[] ASPECTS={Aspect.AIR,Aspect.FIRE,Aspect.EARTH,Aspect.WATER,Aspect.ORDER,Aspect.ENTROPY};
 private static final int[] SHARDS={0,1,3,2,4,5};
 static void register(){
  for(int i=0;i<ASPECTS.length;i++){final int shard=SHARDS[i];java.util.function.Supplier<Item> component=()->dev.eugheist.tc4modern.TC4Modern.SHARDS.get(shard).get();
   ThaumicTinkererModern.INFUSION_RECIPES.add(new dev.eugheist.tc4modern.InfusionItemRecipes.Recipe("INFUSED_POTIONS"+i,"INFUSED_POTIONS",ThaumicTinkererModern.INFUSED_SEEDS::get,5,new AspectList().add(Aspect.CROP,32).add(Aspect.HARVEST,32),()->Items.WHEAT_SEEDS,java.util.Collections.nCopies(4,component)));
  }
 }
 public static ItemStack output(String key){for(int i=0;i<ASPECTS.length;i++)if(key.equals("INFUSED_POTIONS"+i))return InfusedSeedsItem.stack(ASPECTS[i]);return ItemStack.EMPTY;}
}
