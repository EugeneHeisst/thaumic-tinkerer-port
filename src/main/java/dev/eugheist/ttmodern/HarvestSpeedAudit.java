package dev.eugheist.ttmodern;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
final class HarvestSpeedAudit {
 private static void check(boolean ok,String label){if(!ok)throw new IllegalStateException("TT harvest speed: "+label);}
 static void run(ServerLevel level){
  var registry=level.registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT);var tunnel=registry.getHolderOrThrow(HarvestSpeedEnchantments.TUNNEL);var shatter=registry.getHolderOrThrow(HarvestSpeedEnchantments.SHATTER);var player=net.neoforged.neoforge.common.util.FakePlayerFactory.getMinecraft(level);player.getInventory().clearContent();var pos=new BlockPos(40,290,40);
  check(tunnel.value().getMaxLevel()==5&&shatter.value().getMaxLevel()==5,"max ranks");check(!Enchantment.areCompatible(tunnel,shatter)&&!Enchantment.areCompatible(shatter,registry.getHolderOrThrow(ToolEnchantments.SOFT)),"mutual/source exclusions");for(var enchant:java.util.List.of(tunnel,shatter)){check(!Enchantment.areCompatible(enchant,registry.getHolderOrThrow(Enchantments.EFFICIENCY))&&Enchantment.areCompatible(enchant,registry.getHolderOrThrow(Enchantments.FORTUNE)),"efficiency excluded fortune allowed");}
  for(int rank=1;rank<=5;rank++){
   var stack=new ItemStack(Items.DIAMOND_PICKAXE);stack.enchant(shatter,rank);player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,stack);
   for(var block:java.util.List.of(Blocks.OBSIDIAN,Blocks.STONE)){var event=new net.neoforged.neoforge.event.entity.player.PlayerEvent.BreakSpeed(player,block.defaultBlockState(),10,pos);net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(event);check(Math.abs(event.getNewSpeed()-(block==Blocks.OBSIDIAN?30*rank:8))<1e-5&&!event.isCanceled(),"hardness speed rank"+rank);check(stack.getDamageValue()==0,"no extra wear");}
   stack=new ItemStack(Items.DIAMOND_PICKAXE);stack.enchant(tunnel,rank);player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,stack);player.setYRot(0);
   var untouched=new net.neoforged.neoforge.event.entity.player.PlayerEvent.BreakSpeed(player,Blocks.STONE.defaultBlockState(),10,pos);net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(untouched);check(untouched.getNewSpeed()==10,"no previous break unaffected");
   net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.level.BlockEvent.BreakEvent(level,pos,Blocks.STONE.defaultBlockState(),player));
   for(float yaw:new float[]{0,49.9f,50,50.1f,359}){player.setYRot(yaw);var speed=new net.neoforged.neoforge.event.entity.player.PlayerEvent.BreakSpeed(player,Blocks.STONE.defaultBlockState(),10,pos);net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(speed);float expected=yaw<50?(float)(10*(1+.2*rank)):3;check(Math.abs(speed.getNewSpeed()-expected)<1e-5,"yaw boundary rank"+rank+" yaw"+yaw);}
   var saved=ItemStack.parseOptional(level.registryAccess(),(net.minecraft.nbt.CompoundTag)stack.save(level.registryAccess()));check(saved.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA).copyTag().getFloat("TTEnchantTunnelDir")==0,"direction persisted");
  }
  player.setYRot(0);player.getInventory().clearContent();System.out.println("TT HARVEST SPEED AUDIT: ALL PASS (all ranks, hardness, native speed/break events, strict 50-degree bound, unwrapped yaw, persistence, compatibility)");
 }
}
