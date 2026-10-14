package dev.eugheist.ttmodern;
import net.minecraft.world.item.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.ItemContainerContents;
import thaumcraft.api.aspects.*;
/** Original 117-slot general inventory, not the ordinary 18-focus pouch. */
public final class IchorPouch extends Item implements baubles.api.IBauble {
 static final net.neoforged.neoforge.registries.DeferredItem<IchorPouch> ITEM=KamiRegistries.ITEMS.register("ichor_pouch",IchorPouch::new);
 public IchorPouch(){super(new Properties().stacksTo(1).rarity(Rarity.EPIC));}
 static void init(){IchorPouchMenu.init();}
 @Override public net.minecraft.world.InteractionResultHolder<ItemStack> use(net.minecraft.world.level.Level level,net.minecraft.world.entity.player.Player player,net.minecraft.world.InteractionHand hand){if(player instanceof net.minecraft.server.level.ServerPlayer server)server.openMenu(new net.minecraft.world.SimpleMenuProvider((id,inv,p)->new IchorPouchMenu(id,inv,hand==net.minecraft.world.InteractionHand.OFF_HAND),getName(player.getItemInHand(hand))),buf->buf.writeBoolean(hand==net.minecraft.world.InteractionHand.OFF_HAND));return net.minecraft.world.InteractionResultHolder.sidedSuccess(player.getItemInHand(hand),level.isClientSide);}
 @Override public baubles.api.BaubleType getBaubleType(ItemStack stack){return baubles.api.BaubleType.BELT;}
 public static java.util.List<ItemStack> contents(ItemStack pouch){var result=new java.util.ArrayList<ItemStack>(java.util.Collections.nCopies(117,ItemStack.EMPTY));var stored=pouch.getOrDefault(DataComponents.CONTAINER,ItemContainerContents.EMPTY);for(int i=0;i<Math.min(117,stored.getSlots());i++)result.set(i,stored.getStackInSlot(i).copy());return result;}
 public static boolean allowed(ItemStack stack){return !stack.isEmpty()&&!(stack.getItem() instanceof IchorPouch)&&!(stack.getItem() instanceof dev.eugheist.tc4modern.FocusPouchItem);}
 public static void store(ItemStack pouch,java.util.List<ItemStack> items){if(items.size()!=117)throw new IllegalArgumentException("Ichor pouch requires 117 slots");for(var stack:items)if(!stack.isEmpty()&&!allowed(stack))throw new IllegalArgumentException("Nested focus pouches are not allowed");pouch.set(DataComponents.CONTAINER,ItemContainerContents.fromItems(items));}
 static void research(){var r=new KamiResearch("ICHOR_POUCH",new AspectList().add(Aspect.VOID,2).add(Aspect.CLOTH,1).add(Aspect.ELDRITCH,1).add(Aspect.MAN,1),13,6,5,new ItemStack(ITEM.get()));r.setParents("ICHOR_CLOTH");r.setPages(new thaumcraft.api.research.ResearchPage("ttresearch.page.ICHOR_POUCH.0"),MagnetResearch.page("ICHOR_POUCH",thaumcraft.api.research.ResearchPage.PageType.INFUSION_CRAFTING)).registerResearchItem();ThaumicTinkererModern.INFUSION_RECIPES.add(new dev.eugheist.tc4modern.InfusionItemRecipes.Recipe("ICHOR_POUCH","ICHOR_POUCH",ITEM::get,9,new AspectList().add(Aspect.VOID,64).add(Aspect.MAN,32).add(Aspect.CLOTH,32).add(Aspect.ELDRITCH,32).add(Aspect.AIR,64),()->FocusResearch.item("focus_pouch"),java.util.List.of(()->KamiResources.item(KamiResources.Kind.CLOTH),()->FocusResearch.item("focus_portable_hole"),()->Items.DIAMOND,()->KamiResources.item(KamiResources.Kind.CLOTH),()->FocusResearch.item("hungry_chest"),()->FocusResearch.item("void_jar"))));}
}
