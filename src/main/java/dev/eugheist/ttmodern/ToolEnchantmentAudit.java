package dev.eugheist.ttmodern;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.enchantment.*;
final class ToolEnchantmentAudit {
 private static void check(boolean ok,String label){if(!ok)throw new IllegalStateException("TT tool enchants: "+label);}
 static void run(ServerLevel level){
  var registry=level.registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT);var flame=registry.getHolderOrThrow(ToolEnchantments.FLAME);var soft=registry.getHolderOrThrow(ToolEnchantments.SOFT);
  var player=net.neoforged.neoforge.common.util.FakePlayerFactory.getMinecraft(level);player.getInventory().clearContent();player.setGameMode(net.minecraft.world.level.GameType.SURVIVAL);
  check(flame.value().getMaxLevel()==1&&soft.value().getMaxLevel()==1,"rank bounds");
  check(Enchantment.areCompatible(flame,registry.getHolderOrThrow(Enchantments.UNBREAKING)),"unbreaking allowed");
  check(!Enchantment.areCompatible(flame,registry.getHolderOrThrow(Enchantments.EFFICIENCY))&&!Enchantment.areCompatible(flame,soft)&&!Enchantment.areCompatible(soft,registry.getHolderOrThrow(Enchantments.MENDING)),"exclusive enchants");
  check(!flame.is(net.minecraft.tags.EnchantmentTags.IN_ENCHANTING_TABLE),"not ordinary table loot");
  for(var entry:java.util.List.of(flame,soft)){
   var stack=new ItemStack(Items.DIAMOND_AXE);stack.enchant(entry,1);player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,stack);
   var allowed=new net.neoforged.neoforge.event.entity.player.PlayerEvent.BreakSpeed(player,(entry==flame?Blocks.OAK_LOG:Blocks.STONE).defaultBlockState(),2,new net.minecraft.core.BlockPos(0,200,0));
   net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(allowed);check(!allowed.isCanceled()&&allowed.getNewSpeed()==Float.MAX_VALUE&&stack.getDamageValue()==1,"allowed material speed and paid durability "+entry);
   var denied=new net.neoforged.neoforge.event.entity.player.PlayerEvent.BreakSpeed(player,(entry==flame?Blocks.STONE:Blocks.OBSIDIAN).defaultBlockState(),2,new net.minecraft.core.BlockPos(0,200,0));
   net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(denied);check(denied.isCanceled()&&stack.getDamageValue()==1,"wrong material rejected without wear "+entry);
   var bedrock=new net.neoforged.neoforge.event.entity.player.PlayerEvent.BreakSpeed(player,Blocks.BEDROCK.defaultBlockState(),2,new net.minecraft.core.BlockPos(0,200,0));net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(bedrock);check(bedrock.isCanceled(),"unbreakable excluded");
  }
  var research=java.util.Set.of("TTENCH_AUTO_SMELT");check(new EnchanterSelection().change(level.registryAccess(),new ItemStack(Items.DIAMOND_AXE),research,"ttinkerer:auto_smelt",1),"axe selection");
  check(!new EnchanterSelection().change(level.registryAccess(),new ItemStack(Items.DIAMOND_HOE),research,"ttinkerer:auto_smelt",1),"hoe excluded");
  var elemental=net.minecraft.core.registries.BuiltInRegistries.ITEM.get(net.minecraft.resources.ResourceLocation.parse("tc4modern:elemental_axe"));check(!flame.value().isSupportedItem(new ItemStack(elemental)),"elemental axe registry exclusion including anvil");check(elemental!=Items.AIR&&!new EnchanterSelection().change(level.registryAccess(),new ItemStack(elemental),research,"ttinkerer:auto_smelt",1),"elemental axe excluded");
  player.getInventory().clearContent();player.setGameMode(net.minecraft.world.level.GameType.CREATIVE);
  System.out.println("TT TOOL ENCHANT AUDIT: ALL PASS (registry, exclusivity, native break speed, durability, forbidden blocks, research/item gates)");
 }
}
