package dev.eugheist.ttmodern;
import java.nio.file.*;
import java.util.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Items;
import thaumcraft.api.aspects.Aspect;
final class InfusedCropReloadAudit {
 private static void check(boolean ok,String label){if(!ok)throw new IllegalStateException("TT crop reload: "+label);}
 static void run(ServerLevel level){
  var server=level.getServer();var selected=new ArrayList<>(server.getPackRepository().getSelectedIds());var root=server.getWorldPath(net.minecraft.world.level.storage.LevelResource.DATAPACK_DIR).resolve("tt-crop-audit");
  var before=InfusedCropLootRuntime.get(level);int originalBooks=before.entries(Aspect.MIND).size();int originalWeather=before.entries(Aspect.WEATHER).size();
  check(before==InfusedCropLootRuntime.get(level),"catalog cached before reload");
  try{
   Files.createDirectories(root.resolve("data/ttinkerer/tags/item/crop_ores"));Files.createDirectories(root.resolve("data/ttinkerer/tags/enchantment"));
   Files.writeString(root.resolve("pack.mcmeta"),"{\"pack\":{\"pack_format\":48,\"description\":\"isolated crop reload audit\"}}");
   Files.writeString(root.resolve("data/ttinkerer/tags/item/crop_ores/cloud.json"),"{\"replace\":true,\"values\":[\"minecraft:diamond_block\"]}");
   Files.writeString(root.resolve("data/ttinkerer/tags/enchantment/crop_books.json"),"{\"replace\":true,\"values\":[\"minecraft:unbreaking\"]}");
   server.getPackRepository().reload();var enabled=new ArrayList<>(selected);enabled.add("file/tt-crop-audit");server.reloadResources(enabled).join();
   var changed=InfusedCropLootRuntime.get(level);check(changed!=before,"resource reload invalidates warm catalog");
   check(changed.entries(Aspect.WEATHER).stream().anyMatch(e->e.stack().is(Items.DIAMOND_BLOCK)&&e.stack().getCount()==100&&e.weight()==1),"native optional tag extension after reload");
   check(changed.entries(Aspect.MIND).size()==4,"book tag replacement after reload");
   var book=changed.entries(Aspect.MIND).get(3).stack();var enchants=book.get(net.minecraft.core.component.DataComponents.STORED_ENCHANTMENTS);check(enchants.size()==1&&enchants.entrySet().iterator().next().getKey().is(net.minecraft.world.item.enchantment.Enchantments.UNBREAKING),"reloaded native enchantment holder");
   server.reloadResources(selected).join();var restored=InfusedCropLootRuntime.get(level);check(restored!=changed&&restored.entries(Aspect.MIND).size()==originalBooks&&restored.entries(Aspect.WEATHER).size()==originalWeather,"removal restores original books/weather and invalidates positive cache");
   check(restored.entries(Aspect.WEATHER).stream().noneMatch(e->e.stack().is(Items.DIAMOND_BLOCK)),"removed optional item does not survive cache");
  }catch(java.io.IOException e){throw new IllegalStateException(e);}finally{
   if(!new HashSet<>(server.getPackRepository().getSelectedIds()).equals(new HashSet<>(selected)))server.reloadResources(selected).join();
   try{if(Files.exists(root))try(var paths=Files.walk(root)){for(var path:paths.sorted(Comparator.reverseOrder()).toList())Files.delete(path);}}catch(java.io.IOException e){throw new IllegalStateException(e);}
  }
  System.out.println("TT CROP RELOAD AUDIT: ALL PASS (warm cache,actual datapack optional item/book tags,new native holders,removal restoration,no stale loot)");
 }
}
