package dev.eugheist.ttmodern;
import java.util.*;
import net.minecraft.server.level.ServerLevel;
final class InfusedCropLootRuntime {
 private static final Map<ServerLevel,InfusedCropLoot> CACHE=new WeakHashMap<>();
 static InfusedCropLoot get(ServerLevel level){return CACHE.computeIfAbsent(level,l->InfusedCropLoot.complete(l.registryAccess()));}
 static void register(){net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(InfusedCropLootRuntime::reload);}
 private static void reload(net.neoforged.neoforge.event.AddReloadListenerEvent event){event.addListener((net.minecraft.server.packs.resources.ResourceManagerReloadListener)resources->CACHE.clear());}
}
