package dev.eugheist.ttmodern;
import net.minecraft.world.item.*;
final class KamiResources {
 enum Kind { ICHOR("ichor","ichor"),CLOTH("ichor_cloth","ichorCloth"),ICHORIUM("ichorium","ichorium"),NUGGET("ichor_nugget","ichorNugget"),CAP("ichor_cap","ichorCap"),ROD("ichorcloth_rod","ichorclothRod"),NETHER_SHARD("nether_shard","netherShard"),ENDER_SHARD("ender_shard","enderShard");
  final String id,original;Kind(String id,String original){this.id=id;this.original=original;}
 }
 static final java.util.Map<Kind,net.neoforged.neoforge.registries.DeferredItem<Item>> ITEMS=new java.util.EnumMap<>(Kind.class);
 static{for(var kind:Kind.values())ITEMS.put(kind,KamiRegistries.ITEMS.register(kind.id,()->new Item(new Item.Properties().rarity(kind.ordinal()<6?Rarity.EPIC:Rarity.COMMON))));}
 static void init(){}
 static Item item(Kind kind){return ITEMS.get(kind).get();}
 static void research(){
  new thaumcraft.api.research.ResearchItem("DIMENSION_SHARDS","THAUMICTINKERER",new thaumcraft.api.aspects.AspectList(),7,8,0,new ItemStack(item(Kind.ENDER_SHARD))){
   @Override public String getName(){return net.minecraft.network.chat.Component.translatable("ttresearch.name.DIMENSION_SHARDS").getString();}
   @Override public String getText(){return net.minecraft.network.chat.Component.translatable("ttresearch.lore.DIMENSION_SHARDS").getString();}
  }.setConcealed().setStub().setAutoUnlock().setRound().setPages(new thaumcraft.api.research.ResearchPage("ttresearch.page.DIMENSION_SHARDS.0")).registerResearchItem();
 }
}
