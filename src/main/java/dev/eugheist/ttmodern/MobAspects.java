package dev.eugheist.ttmodern;
import thaumcraft.api.aspects.Aspect;
import net.minecraft.world.item.*;
/** Exact build-164 NumericAspectHelper and EnumMobAspect mappings (17 real aspects, not the unused 21-slot stride). */
final class MobAspects{
 static final java.util.List<Aspect> ASPECTS=java.util.List.of(Aspect.WATER,Aspect.MAN,Aspect.AIR,Aspect.FLIGHT,Aspect.FIRE,Aspect.MAGIC,Aspect.UNDEAD,Aspect.FLESH,Aspect.BEAST,Aspect.POISON,Aspect.EARTH,Aspect.ELDRITCH,Aspect.TRAVEL,Aspect.METAL,Aspect.SLIME,Aspect.CLOTH,Aspect.CROP);
 static final java.util.Map<Aspect,net.neoforged.neoforge.registries.DeferredItem<MobAspectItem>[]> ITEMS=new java.util.LinkedHashMap<>();
 static void register(){for(var aspect:ASPECTS){@SuppressWarnings("unchecked") var variants=(net.neoforged.neoforge.registries.DeferredItem<MobAspectItem>[])new net.neoforged.neoforge.registries.DeferredItem<?>[3];for(int i=0;i<3;i++){final int form=i;variants[i]=ThaumicTinkererModern.ITEMS.register("mob_aspect_"+aspect.getTag()+(i==0?"":i==1?"_condensed":"_infused"),()->new MobAspectItem(aspect,form));}ITEMS.put(aspect,variants);}}
 static ItemStack stack(Aspect aspect,int form){return new ItemStack(ITEMS.get(aspect)[form].get());}
 static final java.util.List<String> SUMMON_ORDER=java.util.List.of("minecraft:snow_golem","minecraft:bat","minecraft:blaze","tc4modern:brainy_zombie","tc4modern:firebat","minecraft:cave_spider","minecraft:chicken","minecraft:cow","minecraft:creeper","minecraft:enderman","minecraft:ghast","minecraft:horse","minecraft:iron_golem","minecraft:magma_cube","minecraft:mooshroom","minecraft:ocelot","minecraft:pig","minecraft:zombified_piglin","minecraft:sheep","minecraft:silverfish","minecraft:skeleton","minecraft:slime","minecraft:spider","minecraft:squid","minecraft:villager","tc4modern:wisp","minecraft:witch","minecraft:wolf","minecraft:zombie");
 static final java.util.Map<String,java.util.List<Aspect>> MOBS=mobTable();
 static java.util.Map<String,java.util.List<Aspect>> mobTable(){var table=new java.util.HashMap<String,java.util.List<Aspect>>(java.util.Map.ofEntries(
java.util.Map.entry("minecraft:snow_golem",java.util.List.of(Aspect.WATER,Aspect.WATER,Aspect.MAN)),
java.util.Map.entry("minecraft:bat",java.util.List.of(Aspect.AIR,Aspect.AIR,Aspect.FLIGHT)),
java.util.Map.entry("minecraft:blaze",java.util.List.of(Aspect.FIRE,Aspect.FIRE,Aspect.FIRE)),
java.util.Map.entry("tc4modern:brainy_zombie",java.util.List.of(Aspect.MAGIC,Aspect.UNDEAD,Aspect.FLESH)),
java.util.Map.entry("tc4modern:firebat",java.util.List.of(Aspect.FLIGHT,Aspect.FIRE,Aspect.MAGIC)),
java.util.Map.entry("minecraft:cave_spider",java.util.List.of(Aspect.BEAST,Aspect.POISON,Aspect.POISON)),
java.util.Map.entry("minecraft:chicken",java.util.List.of(Aspect.CROP,Aspect.FLIGHT,Aspect.BEAST)),
java.util.Map.entry("minecraft:cow",java.util.List.of(Aspect.BEAST,Aspect.EARTH,Aspect.BEAST)),
java.util.Map.entry("minecraft:creeper",java.util.List.of(Aspect.MAGIC,Aspect.BEAST,Aspect.ELDRITCH)),
java.util.Map.entry("minecraft:enderman",java.util.List.of(Aspect.ELDRITCH,Aspect.ELDRITCH,Aspect.MAN)),
java.util.Map.entry("minecraft:ghast",java.util.List.of(Aspect.FIRE,Aspect.FLIGHT,Aspect.FLIGHT)),
java.util.Map.entry("minecraft:horse",java.util.List.of(Aspect.BEAST,Aspect.BEAST,Aspect.TRAVEL)),
java.util.Map.entry("minecraft:iron_golem",java.util.List.of(Aspect.METAL,Aspect.METAL,Aspect.MAN)),
java.util.Map.entry("minecraft:magma_cube",java.util.List.of(Aspect.FIRE,Aspect.SLIME,Aspect.SLIME)),
java.util.Map.entry("minecraft:mooshroom",java.util.List.of(Aspect.BEAST,Aspect.EARTH,Aspect.CROP)),
java.util.Map.entry("minecraft:ocelot",java.util.List.of(Aspect.BEAST,Aspect.EARTH,Aspect.ELDRITCH)),
java.util.Map.entry("minecraft:pig",java.util.List.of(Aspect.BEAST,Aspect.EARTH,Aspect.TRAVEL)),
java.util.Map.entry("minecraft:zombified_piglin",java.util.List.of(Aspect.UNDEAD,Aspect.FLESH,Aspect.FIRE)),
java.util.Map.entry("minecraft:sheep",java.util.List.of(Aspect.EARTH,Aspect.CLOTH,Aspect.BEAST)),
java.util.Map.entry("minecraft:silverfish",java.util.List.of(Aspect.METAL,Aspect.METAL,Aspect.EARTH)),
java.util.Map.entry("minecraft:skeleton",java.util.List.of(Aspect.UNDEAD,Aspect.MAN,Aspect.UNDEAD)),
java.util.Map.entry("minecraft:slime",java.util.List.of(Aspect.SLIME,Aspect.SLIME,Aspect.BEAST)),
java.util.Map.entry("minecraft:spider",java.util.List.of(Aspect.BEAST,Aspect.UNDEAD,Aspect.UNDEAD)),
java.util.Map.entry("minecraft:squid",java.util.List.of(Aspect.WATER,Aspect.WATER,Aspect.WATER)),
java.util.Map.entry("minecraft:villager",java.util.List.of(Aspect.MAN,Aspect.MAN,Aspect.MAN)),
java.util.Map.entry("tc4modern:wisp",java.util.List.of(Aspect.AIR,Aspect.MAGIC,Aspect.MAGIC)),
java.util.Map.entry("minecraft:witch",java.util.List.of(Aspect.MAGIC,Aspect.UNDEAD,Aspect.ELDRITCH)),
java.util.Map.entry("minecraft:wolf",java.util.List.of(Aspect.BEAST,Aspect.BEAST,Aspect.BEAST)),
java.util.Map.entry("minecraft:zombie",java.util.List.of(Aspect.FLESH,Aspect.FLESH,Aspect.UNDEAD))));
 // These modern entity types were metadata/tame variants of the same original entity class.
 table.put("tc4modern:giant_brainy_zombie",table.get("tc4modern:brainy_zombie"));
 table.put("minecraft:wither_skeleton",table.get("minecraft:skeleton"));
 table.put("minecraft:zombie_villager",table.get("minecraft:zombie"));
 table.put("minecraft:cat",table.get("minecraft:ocelot"));
 for(var horse:java.util.List.of("donkey","mule","skeleton_horse","zombie_horse"))table.put("minecraft:"+horse,table.get("minecraft:horse"));
 return java.util.Map.copyOf(table);}
}
