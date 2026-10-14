package dev.eugheist.ttmodern;
import java.util.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.item.*;
/** Build 164 enum order, weights, first OreDictionary entry and blacklist semantics. */
enum BedrockOres {
 ALUMINUM("oreAluminum","aluminum",617),
 AMBER("oreAmber","amber",161),
 APATITE("oreApatite","apatite",269),
 BLUE_TOPAZ("oreBlueTopaz","blue_topaz",238),
 CERTUS_QUARTZ("oreCertusQuartz","certus_quartz",234),
 CHIMERITE("oreChimerite","chimerite",270),
 CINNABAR("oreCinnabar","cinnabar",172),
 COAL("oreCoal","coal",2648),
 COPPER("oreCopper","copper",603),
 DIAMOND("oreDiamond","diamond",67),
 EMERALD("oreEmerald","emerald",48),
 DARK_IRON("oreFzDarkIron","fz_dark_iron",61),
 GOLD("oreGold","gold",164),
 AIR("oreInfusedAir","infused_air",94),
 EARTH("oreInfusedEarth","infused_earth",35),
 ENTROPY("oreInfusedEntropy","infused_entropy",53),
 FIRE("oreInfusedFire","infused_fire",42),
 ORDER("oreInfusedOrder","infused_order",31),
 WATER("oreInfusedWater","infused_water",27),
 IRON("oreIron","iron",1503),
 LAPIS("oreLapis","lapis",57),
 LEAD("oreLead","lead",335),
 NICKEL("oreNickel","nickel",72),
 PERIDOT("orePeridot","peridot",79),
 REDSTONE("oreRedstone","redstone",364),
 RUBY("oreRuby","ruby",57),
 SALT("oreSaltpeter","saltpeter",768),
 SAPPHIRE("oreSapphire","sapphire",70),
 SILVER("oreSilver","silver",416),
 SULFUR("oreSulfur","sulfur",105),
 TIN("oreTin","tin",507),
 URANIUM("oreUranium","uranium",112),
 VINETUM("oreVinteum","vinteum",392);
 final String legacy,path;final int weight;
 BedrockOres(String legacy,String path,int weight){this.legacy=legacy;this.path=path;this.weight=weight;}
 record Entry(BedrockOres kind,Block block){}
 static List<Entry> entries(Set<String> blacklist){var result=new ArrayList<Entry>();for(var kind:values()){if(blacklist.contains(kind.legacy))continue;var tag=net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM,net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("c","ores/"+kind.path));var items=net.minecraft.core.registries.BuiltInRegistries.ITEM.getTag(tag);if(items.isEmpty()||items.get().size()==0)continue;var first=items.get().get(0).value();if(first instanceof BlockItem block)result.add(new Entry(kind,block.getBlock()));}return List.copyOf(result);}
 static int sum(List<Entry> entries){return entries.stream().mapToInt(e->e.kind.weight).sum();}
 static Block choose(Random random,List<Entry> entries){int total=sum(entries);if(total<=0)return Blocks.GOLD_ORE;int value=random.nextInt(total);for(var entry:entries)if((value-=entry.kind.weight)<0)return entry.block;return Blocks.GOLD_ORE;}
}
