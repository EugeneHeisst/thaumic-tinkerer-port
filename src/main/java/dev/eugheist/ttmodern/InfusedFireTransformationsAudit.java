package dev.eugheist.ttmodern;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import static dev.eugheist.ttmodern.InfusedFireTransformations.*;
final class InfusedFireTransformationsAudit {
 private static void check(boolean ok,String label){if(!ok)throw new IllegalStateException("TT infused fire table: "+label);}
 static void run(){
  check(fixed(Kind.AIR,Blocks.WATER.defaultBlockState(),true).is(Blocks.CAKE),"source water cake enabled");
  check(fixed(Kind.AIR,Blocks.WATER.defaultBlockState(),false)==null,"cake config respected");
  check(fixed(Kind.AIR,Blocks.WATER.defaultBlockState().setValue(LiquidBlock.LEVEL,1),true)==null,"flowing water excluded");
  check(fixed(Kind.WATER,Blocks.LAVA.defaultBlockState().setValue(LiquidBlock.LEVEL,5),true).is(Blocks.OBSIDIAN),"flowing lava obsidian");
  check(fixed(Kind.FIRE,Blocks.GOLD_BLOCK.defaultBlockState(),true).is(Blocks.NETHER_QUARTZ_ORE)&&fixed(Kind.FIRE,Blocks.GOLD_ORE.defaultBlockState(),true)==null,"original gold block versus ore distinction");
  check(fixed(Kind.FIRE,Blocks.REDSTONE_ORE.defaultBlockState().setValue(BlockStateProperties.LIT,true),true).is(Blocks.NETHER_QUARTZ_ORE),"lit redstone maps too");
  check(fixed(Kind.FIRE,Blocks.DANDELION.defaultBlockState(),true).is(Blocks.DANDELION),"intentional identity transformation");
  check(fixed(Kind.EARTH,Blocks.SNOW.defaultBlockState(),true).is(Blocks.DEAD_BUSH),"tallgrass metadata zero");
  check(fixed(Kind.EARTH,Blocks.NETHER_BRICK_STAIRS.defaultBlockState(),true).equals(Blocks.OAK_STAIRS.defaultBlockState()),"result resets to metadata zero");
  for(var block:new Block[]{Blocks.OAK_LOG,Blocks.SPRUCE_LOG,Blocks.BIRCH_LOG,Blocks.JUNGLE_LOG,Blocks.ACACIA_LOG,Blocks.DARK_OAK_LOG})check(fixed(Kind.AIR,block.defaultBlockState(),true).is(Blocks.SAND),"legacy logs "+block);
  check(fixed(Kind.EARTH,Blocks.POLISHED_ANDESITE.defaultBlockState(),true).is(Blocks.DIRT)&&fixed(Kind.FIRE,Blocks.PINK_TULIP.defaultBlockState(),true).is(Blocks.BROWN_MUSHROOM),"legacy metadata families expanded");
  check(fixed(Kind.AIR,Blocks.MANGROVE_LOG.defaultBlockState(),true)==null,"no unsolicited new wood conversion");
  check(fixed(Kind.ORDER,Blocks.IRON_ORE.defaultBlockState(),true)==null&&fixed(Kind.ENTROPY,Blocks.STONE.defaultBlockState(),true)==null,"dynamic branches not replaced with guessed maps");
  System.out.println("TT INFUSED FIRE TABLE AUDIT: ALL PASS (legacy variants,water source,cake gate,flowing lava,gold block,lit ore,identity,metadata reset)");
 }
}
