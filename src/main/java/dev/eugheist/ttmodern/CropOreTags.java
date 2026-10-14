package dev.eugheist.ttmodern;
import java.util.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import thaumcraft.api.aspects.Aspect;
/** Compatibility names for the removed OreDictionary. Individual tag matches remain distinct. */
final class CropOreTags {
 record Source(String name,List<ItemStack> items){}
 static String legacyName(ResourceLocation id){
  var path=id.getPath();
  if(id.getNamespace().equals("ttinkerer")&&path.startsWith("crop_ores/")){var words=path.substring("crop_ores/".length()).split("_");var name=new StringBuilder(words[0]);for(int i=1;i<words.length;i++)if(!words[i].isEmpty())name.append(Character.toUpperCase(words[i].charAt(0))).append(words[i].substring(1));return name.toString();}
  if(id.getNamespace().equals("tc4modern")&&path.equals("shards"))return "shard";
  if(id.getNamespace().equals("minecraft"))return switch(path){case "logs"->"logWood";case "planks"->"plankWood";case "saplings"->"sapling";default->null;};
  if(!id.getNamespace().equals("c")&&!id.getNamespace().equals("forge"))return null;
  var parts=path.split("/");if(parts.length==2){String material=parts[1];String suffix=Character.toUpperCase(material.charAt(0))+material.substring(1);return switch(parts[0]){case "ingots"->"ingot"+suffix;case "nuggets"->"nugget"+suffix;case "ores"->"ore"+suffix;case "storage_blocks"->"block"+suffix;case "dusts"->"dust"+suffix;case "gears"->"gear"+suffix;case "buckets"->"bucket"+suffix;case "rods"->material.equals("wooden")?"stickWood":"rod"+suffix;default->null;};}
  return switch(path){case "slime_balls"->"slimeball";case "buckets"->"bucket";case "gears"->"gear";case "saplings"->"sapling";default->null;};
 }
 static List<Source> sources(){
  var result=new ArrayList<Source>();net.minecraft.core.registries.BuiltInRegistries.ITEM.getTags().sorted(Comparator.comparing(p->p.getFirst().location().toString())).forEach(pair->{var name=legacyName(pair.getFirst().location());if(name!=null){var stacks=new ArrayList<ItemStack>();for(var item:pair.getSecond())if(item.is(pair.getFirst()))stacks.add(new ItemStack(item));result.add(new Source(name,stacks));}});return result;
 }
 static void expand(InfusedCropLoot loot,Aspect aspect,String target,int count,int repeats,List<Source> sources){
  String capital=target.isEmpty()?target:Character.toUpperCase(target.charAt(0))+target.substring(1).toLowerCase(java.util.Locale.ROOT);
  for(int pass=0;pass<repeats;pass++)for(var source:sources)if(source.name().contains(target)||source.name().contains(capital))for(var item:source.items()){var stack=item.copy();stack.setCount(count);loot.add(aspect,stack,1);}
 }
 static void addAll(InfusedCropLoot loot){var sources=sources();
  expand(loot,Aspect.ELDRITCH,"bucketEnder",1,2,sources);expand(loot,Aspect.TREE,"wood",1,2,sources);expand(loot,Aspect.COLD,"rodBlizz",1,2,sources);expand(loot,Aspect.PLANT,"sapling",1,2,sources);expand(loot,Aspect.SLIME,"slime",1,2,sources);expand(loot,Aspect.MECHANISM,"gear",1,2,sources);expand(loot,Aspect.METAL,"iron",1,2,sources);expand(loot,Aspect.EXCHANGE,"ingotCopper",4,1,sources);expand(loot,Aspect.MAGIC,"shard",1,2,sources);expand(loot,Aspect.WEATHER,"cloud",100,1,sources);expand(loot,Aspect.VOID,"bucket",1,2,sources);
 }
}
