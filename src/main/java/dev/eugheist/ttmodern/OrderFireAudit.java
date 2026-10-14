package dev.eugheist.ttmodern;
import java.util.*;
import java.nio.file.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
final class OrderFireAudit {
 private static void check(boolean ok,String label){if(!ok)throw new IllegalStateException("TT Ordo: "+label);}
 static void run(ServerLevel level){
  var pos=new BlockPos(7,278,7);
  try {
   for(var pair:new Block[][]{{Blocks.COAL_ORE,Blocks.COAL_BLOCK},{Blocks.IRON_ORE,Blocks.IRON_BLOCK},{Blocks.GOLD_ORE,Blocks.GOLD_BLOCK},{Blocks.DIAMOND_ORE,Blocks.DIAMOND_BLOCK},{Blocks.EMERALD_ORE,Blocks.EMERALD_BLOCK},{Blocks.LAPIS_ORE,Blocks.LAPIS_BLOCK},{Blocks.REDSTONE_ORE,Blocks.REDSTONE_BLOCK}}){
    level.setBlockAndUpdate(pos,pair[0].defaultBlockState());var before=level.getBlockState(pos);
    var result=OrderFireResolver.result(level,pos);check(result!=null&&result.is(pair[1]),"real loot/recipes "+pair[0]+" -> "+result);check(level.getBlockState(pos)==before,"lookup does not mutate world");
   }
   for(var block:new Block[]{Blocks.STONE,Blocks.POLISHED_ANDESITE,Blocks.GLASS,Blocks.SAND,Blocks.RED_SAND,Blocks.GRAVEL}){level.setBlockAndUpdate(pos,block.defaultBlockState());check(OrderFireResolver.result(level,pos)==null,"excluded "+block);}
   check(OrderFireResolver.decayChance(0)==3&&OrderFireResolver.decayChance(1)==3&&OrderFireResolver.decayChance(2)==2&&OrderFireResolver.decayChance(3)==1&&OrderFireResolver.decayChance(8)==1,"entry-count decay");
   check(OrderFireResolver.resolve(level,Blocks.COAL_ORE.defaultBlockState(),List.of(new ItemStack(Items.COAL),new ItemStack(Items.DIAMOND)))==null,"mixed drops rejected");
   check(OrderFireResolver.resolve(level,Blocks.COAL_ORE.defaultBlockState(),List.of(new ItemStack(Items.COAL,4),new ItemStack(Items.COAL,2))).is(Blocks.COAL_BLOCK),"equal drops accept different counts");
   var namedCoal=new ItemStack(Items.COAL);namedCoal.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME,net.minecraft.network.chat.Component.literal("audit"));check(OrderFireResolver.resolve(level,Blocks.COAL_ORE.defaultBlockState(),List.of(new ItemStack(Items.COAL),namedCoal)).is(Blocks.COAL_BLOCK),"legacy equality ignores names/NBT");
   check(OrderFireResolver.resolve(level,Blocks.COAL_ORE.defaultBlockState(),List.of())==null,"empty drops safe");
   check(OrderFireResolver.craftNine(level,ItemStack.EMPTY).isEmpty(),"missing smelting input safe");
   reload(level,pos);
  }finally{level.removeBlock(pos,false);OrderFireResolver.clear();}
  System.out.println("TT ORDO AUDIT: ALL PASS (native ore loot/crafting/smelting,legacy raw-ore adaptation,exclusions,mixed/empty drops,decay,reload invalidation)");
 }
 private static void reload(ServerLevel level,BlockPos pos){
  var server=level.getServer();var selected=new ArrayList<>(server.getPackRepository().getSelectedIds());
  var root=server.getWorldPath(net.minecraft.world.level.storage.LevelResource.DATAPACK_DIR).resolve("tt-ordo-audit");
  try{
   level.setBlockAndUpdate(pos,Blocks.OBSIDIAN.defaultBlockState());check(OrderFireResolver.result(level,pos)==null,"negative cache before recipe");
   Files.createDirectories(root.resolve("data/ttordo/recipe"));Files.writeString(root.resolve("pack.mcmeta"),"{\"pack\":{\"pack_format\":48,\"description\":\"isolated Ordo audit\"}}");
   Files.writeString(root.resolve("data/ttordo/recipe/compress.json"),"{\"type\":\"minecraft:crafting_shaped\",\"category\":\"misc\",\"pattern\":[\"OOO\",\"OOO\",\"OOO\"],\"key\":{\"O\":{\"item\":\"minecraft:obsidian\"}},\"result\":{\"id\":\"minecraft:diamond_block\",\"count\":1}}");
   server.getPackRepository().reload();var enabled=new ArrayList<>(selected);enabled.add("file/tt-ordo-audit");server.reloadResources(enabled).join();
   var result=OrderFireResolver.result(level,pos);check(result!=null&&result.is(Blocks.DIAMOND_BLOCK),"actual datapack reload replaces negative cache");
   server.reloadResources(selected).join();check(OrderFireResolver.result(level,pos)==null,"actual datapack removal replaces positive cache");
  }catch(java.io.IOException e){throw new IllegalStateException(e);}finally{
   if(!new HashSet<>(server.getPackRepository().getSelectedIds()).equals(new HashSet<>(selected)))server.reloadResources(selected).join();
   try {if(Files.exists(root)){try(var paths=Files.walk(root)){for(var path:paths.sorted(Comparator.reverseOrder()).toList())Files.delete(path);}}}catch(java.io.IOException e){throw new IllegalStateException(e);}
  }
 }
}
