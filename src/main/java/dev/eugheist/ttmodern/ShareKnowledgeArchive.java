package dev.eugheist.ttmodern;
import java.util.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
/** Original name-indexed server research cache, persisted by UUID for modern player identity. */
final class ShareKnowledgeArchive extends SavedData{
    private final Map<UUID,Set<String>> authors=new HashMap<>();
    static ShareKnowledgeArchive get(MinecraftServer server){return server.overworld().getDataStorage().computeIfAbsent(new SavedData.Factory<>(ShareKnowledgeArchive::new,ShareKnowledgeArchive::load,null),"ttinkerer_share_knowledge");}
    static ShareKnowledgeArchive load(CompoundTag tag,HolderLookup.Provider registries){var archive=new ShareKnowledgeArchive();for(var entry:tag.getList("Authors",10)){var data=(CompoundTag)entry;if(!data.hasUUID("UUID"))continue;var keys=new HashSet<String>();for(var key:data.getList("Research",8))keys.add(key.getAsString());archive.authors.put(data.getUUID("UUID"),keys);}return archive;}
    void remember(ServerPlayer player){authors.put(player.getUUID(),Set.copyOf(dev.eugheist.tc4modern.ResearchProgress.completed(player.getPersistentData())));setDirty();}
    Set<String> research(UUID author){return authors.getOrDefault(author,Set.of());}
    @Override public CompoundTag save(CompoundTag tag,HolderLookup.Provider registries){var list=new ListTag();authors.forEach((id,keys)->{var data=new CompoundTag();data.putUUID("UUID",id);var research=new ListTag();keys.stream().sorted().forEach(key->research.add(StringTag.valueOf(key)));data.put("Research",research);list.add(data);});tag.put("Authors",list);return tag;}
    static void register(){
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener((net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent event)->{if(event.getEntity() instanceof ServerPlayer player){var archive=get(player.server);if(archive.authors.containsKey(player.getUUID()))archive.remember(player);}});
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener((net.neoforged.neoforge.event.server.ServerStoppingEvent event)->{var archive=get(event.getServer());for(var player:event.getServer().getPlayerList().getPlayers())if(archive.authors.containsKey(player.getUUID()))archive.remember(player);});
    }
}
