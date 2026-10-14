package dev.eugheist.ttmodern;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import thaumcraft.api.aspects.Aspect;
final class CropOreTagsAudit {
 private static void check(boolean ok,String message){if(!ok)throw new IllegalStateException("TT crop ore tags: "+message);}
 static void run(){
  var sources=java.util.List.of(new CropOreTags.Source("ingotIron",java.util.List.of(new ItemStack(Items.IRON_INGOT))),new CropOreTags.Source("ingotCopper",java.util.List.of(new ItemStack(Items.COPPER_INGOT))),new CropOreTags.Source("blockIron",java.util.List.of(new ItemStack(Items.IRON_BLOCK))),new CropOreTags.Source("cloud",java.util.List.of(new ItemStack(Items.WHITE_WOOL))));var loot=new InfusedCropLoot();
  CropOreTags.expand(loot,Aspect.METAL,"iron",1,2,sources);check(loot.entries(Aspect.METAL).size()==4,"substring family and original double expansion");
  CropOreTags.expand(loot,Aspect.EXCHANGE,"ingotCopper",4,1,sources);check(loot.entries(Aspect.EXCHANGE).size()==1&&loot.entries(Aspect.EXCHANGE).getFirst().stack().getCount()==4&&loot.entries(Aspect.EXCHANGE).getFirst().weight()==1,"copper count four not weight four");
  CropOreTags.expand(loot,Aspect.WEATHER,"cloud",100,1,sources);check(loot.entries(Aspect.WEATHER).getFirst().stack().getCount()==100,"cloud original oversized count");check(sources.getFirst().items().getFirst().getCount()==1,"source stacks not mutated");
  check("ingotCopper".equals(CropOreTags.legacyName(ResourceLocation.parse("c:ingots/copper")))&&"stickWood".equals(CropOreTags.legacyName(ResourceLocation.parse("c:rods/wooden")))&&"shard".equals(CropOreTags.legacyName(ResourceLocation.parse("tc4modern:shards"))),"modern bridge mappings");
  check("rodBlizz".equals(CropOreTags.legacyName(ResourceLocation.parse("ttinkerer:crop_ores/rod_blizz")))&&"bucketEnder".equals(CropOreTags.legacyName(ResourceLocation.parse("ttinkerer:crop_ores/bucket_ender"))),"legal lowercase extension names map camel legacy keys");
  var runtime=CropOreTags.sources();check(runtime.stream().anyMatch(s->s.name().equals("sapling")&&!s.items().isEmpty())&&runtime.stream().anyMatch(s->s.name().equals("shard")&&!s.items().isEmpty()),"actual loaded vanilla and thaumcraft tags");var actual=InfusedCropLoot.fixed();CropOreTags.addAll(actual);check(actual.entries(Aspect.MAGIC).size()>0,"native expansion produces shard loot");
  System.out.println("TT CROP ORE TAG AUDIT: ALL PASS (legacy substring/double expansion,count vs weight,oversized cloud,source copies,native loaded tags/shards)");
 }
}
