package dev.eugheist.ttmodern;
import java.util.*;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ItemLike;
import thaumcraft.api.aspects.Aspect;
/** Build-164 item entries. Optional OreDictionary expansions and TC-specific variants are added separately. */
final class InfusedCropLoot {
 record Entry(ItemStack stack,int weight){
  Entry{stack=stack.copy();if(weight<=0)throw new IllegalArgumentException("nonpositive crop loot weight");}
  @Override public ItemStack stack(){return stack.copy();}
 }
 private final Map<Aspect,List<Entry>> table=new LinkedHashMap<>();
 void add(Aspect aspect,ItemStack stack,int weight){table.computeIfAbsent(aspect,a->new ArrayList<>()).add(new Entry(stack,weight));}
 void add(Aspect aspect,ItemLike item,int count,int weight){add(aspect,new ItemStack(item,count),weight);}
 List<Entry> entries(Aspect aspect){return List.copyOf(table.getOrDefault(aspect,List.of()));}
 ItemStack roll(Aspect aspect,RandomSource random){
  var entries=table.get(aspect);if(entries==null||entries.isEmpty())return ItemStack.EMPTY;
  int total=0;for(var entry:entries)total=Math.addExact(total,entry.weight());int value=random.nextInt(total);
  // Preserve the original <= 0 boundary. HashMap iteration was unstable; modern insertion order is explicit.
  for(var entry:entries){value-=entry.weight();if(value<=0)return entry.stack();}return ItemStack.EMPTY;
 }
 static InfusedCropLoot fixed(){
  var loot=new InfusedCropLoot();
  loot.add(Aspect.AIR,ThaumicTinkererModern.INFUSED_GRAIN_AIR.get(),1,1);loot.add(Aspect.FIRE,ThaumicTinkererModern.INFUSED_GRAIN_FIRE.get(),1,1);loot.add(Aspect.EARTH,ThaumicTinkererModern.INFUSED_GRAIN_EARTH.get(),1,1);loot.add(Aspect.WATER,ThaumicTinkererModern.INFUSED_GRAIN_WATER.get(),1,1);
  loot.add(Aspect.ORDER,Items.GLASS,64,1);loot.add(Aspect.ENTROPY,Items.SAND,64,1);
  loot.add(Aspect.ELDRITCH,Items.ENDER_PEARL,4,10);loot.add(Aspect.ELDRITCH,Items.ENDER_EYE,4,5);
  loot.add(Aspect.TREE,Items.OAK_LOG,1,1);
  loot.add(Aspect.MIND,Items.PAPER,64,15);loot.add(Aspect.MIND,Items.BOOK,32,10);loot.add(Aspect.MIND,Items.BOOKSHELF,16,5);
  loot.add(Aspect.UNDEAD,Items.ROTTEN_FLESH,32,1);loot.add(Aspect.CRAFT,ThaumicTinkererModern.QUARTZ.get(),32,1);
  loot.add(Aspect.HUNGER,Items.NETHER_WART,16,1);loot.add(Aspect.COLD,Items.SNOWBALL,16,1);
  for(var sapling:List.of(Items.OAK_SAPLING,Items.SPRUCE_SAPLING,Items.BIRCH_SAPLING,Items.JUNGLE_SAPLING,Items.ACACIA_SAPLING,Items.DARK_OAK_SAPLING))loot.add(Aspect.PLANT,sapling,1,1);
  for(var armor:List.of(Items.DIAMOND_BOOTS,Items.DIAMOND_LEGGINGS,Items.DIAMOND_CHESTPLATE,Items.DIAMOND_HELMET))loot.add(Aspect.ARMOR,armor,1,1);
  loot.add(Aspect.SLIME,Items.SLIME_BALL,16,1);loot.add(Aspect.GREED,Items.GOLD_INGOT,4,1);
  loot.add(Aspect.LIGHT,Items.GLOWSTONE_DUST,16,5);loot.add(Aspect.LIGHT,ThaumicTinkererModern.BRIGHT_NITOR.get(),1,1);
  loot.add(Aspect.MECHANISM,Items.PISTON,8,1);loot.add(Aspect.CROP,Items.WHEAT,32,1);loot.add(Aspect.METAL,Items.IRON_INGOT,4,100);
  loot.add(Aspect.DEATH,Items.BONE,32,1);loot.add(Aspect.MOTION,Items.RAIL,1,10);loot.add(Aspect.MOTION,Items.ACTIVATOR_RAIL,1,1);
  loot.add(Aspect.CLOTH,Items.WHITE_WOOL,16,30);loot.add(Aspect.CLOTH,Items.STRING,15,10);
  for(var wool:List.of(Items.WHITE_WOOL,Items.ORANGE_WOOL,Items.MAGENTA_WOOL,Items.LIGHT_BLUE_WOOL,Items.YELLOW_WOOL,Items.LIME_WOOL,Items.PINK_WOOL,Items.GRAY_WOOL,Items.LIGHT_GRAY_WOOL,Items.CYAN_WOOL,Items.PURPLE_WOOL,Items.BLUE_WOOL,Items.BROWN_WOOL,Items.GREEN_WOOL,Items.RED_WOOL,Items.BLACK_WOOL))loot.add(Aspect.CLOTH,wool,4,1);
  loot.add(Aspect.HEAL,Items.GOLDEN_APPLE,1,1);loot.add(Aspect.HEAL,Items.CAKE,1,1);loot.add(Aspect.SENSES,Items.LAPIS_LAZULI,20,1);
  loot.add(Aspect.SOUL,Items.SOUL_SAND,64,2);loot.add(Aspect.SOUL,Items.NETHERRACK,64,2);loot.add(Aspect.SOUL,Items.NETHER_BRICKS,1,1);
  // Original WEATHER includes an AIR item: intentionally an empty result, not a replacement prize.
  loot.add(Aspect.WEATHER,ItemStack.EMPTY,1);loot.add(Aspect.DARKNESS,Items.OBSIDIAN,10,1);loot.add(Aspect.VOID,Items.BUCKET,1,1);
  loot.add(Aspect.LIFE,Items.EGG,8,1);loot.add(Aspect.TRAP,Items.COBWEB,4,1);loot.add(Aspect.CRYSTAL,Items.DIAMOND,1,1);
  for(int id=0;id<24;id++)loot.add(Aspect.BEAST,LegacyCropEggItem.stack(id),1);
  addThaumcraft(loot);
  return loot;
 }
 static InfusedCropLoot complete(net.minecraft.core.HolderLookup.Provider registries){var loot=fixed();loot.addBooks(registries);CropOreTags.addAll(loot);return loot;}
 static final net.minecraft.tags.TagKey<net.minecraft.world.item.enchantment.Enchantment> BOOKS=net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ENCHANTMENT,net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(ThaumicTinkererModern.ID,"crop_books"));
 void addBooks(net.minecraft.core.HolderLookup.Provider registries){
  var enchantments=registries.lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT);
  for(var enchantment:enchantments.getOrThrow(BOOKS))add(Aspect.MIND,EnchantedBookItem.createForEnchantment(new net.minecraft.world.item.enchantment.EnchantmentInstance(enchantment,1)),1);
 }
 private static Item tc(String name){var item=FocusResearch.item(name);if(item==Items.AIR)throw new IllegalStateException("Missing crop loot item tc4modern:"+name);return item;}
 private static void addThaumcraft(InfusedCropLoot loot){
  loot.add(Aspect.FLESH,tc("zombie_brain"),16,4);loot.add(Aspect.FLESH,tc("zombie_brain"),16,1);
  loot.add(Aspect.CRAFT,tc("alchemical_furnace"),16,1);
  loot.add(Aspect.MINE,tc("thaumium_pickaxe"),1,1);loot.add(Aspect.HARVEST,tc("thaumium_hoe"),1,1);loot.add(Aspect.WEAPON,tc("thaumium_sword"),1,1);
  loot.add(Aspect.TOOL,tc("thaumium_shovel"),1,1);loot.add(Aspect.TOOL,tc("thaumium_axe"),1,1);
  loot.add(Aspect.TRAVEL,tc("arcane_stone_bricks"),8,1);loot.table.get(Aspect.LIGHT).add(1,new Entry(new ItemStack(tc("nitor"),4),1));
  loot.add(Aspect.EXCHANGE,tc("cinnabar_ore"),4,1);loot.add(Aspect.ENERGY,tc("alumentum"),12,1);
  loot.add(Aspect.POISON,tc("quicksilver"),16,1);loot.add(Aspect.TAINT,tc("tainted_goo"),4,1);
  for(var core:List.of("fill","empty","gather","harvest","guard","liquid","essentia","lumber","use","butcher","sorting","fishing"))loot.add(Aspect.MAN,tc("golem_core_"+core),1,1);
  for(var aspect:Aspect.aspects.values())loot.add(Aspect.AURA,dev.eugheist.tc4modern.WispEssenceItem.of(aspect,2),1);
 }
}
