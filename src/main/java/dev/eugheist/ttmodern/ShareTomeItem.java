package dev.eugheist.ttmodern;
import java.util.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import dev.eugheist.tc4modern.*;
public final class ShareTomeItem extends Item{
    public ShareTomeItem(){super(new Properties().stacksTo(1).rarity(Rarity.EPIC));}
    @Override public boolean isFoil(ItemStack stack){return true;}
    static boolean assigned(ItemStack stack){return stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag().hasUUID("Author");}
    static void bind(ItemStack stack,ServerPlayer player){
        var archive=ShareKnowledgeArchive.get(player.server);archive.remember(player);
        CustomData.update(DataComponents.CUSTOM_DATA,stack,tag->{tag.putUUID("Author",player.getUUID());tag.putString("player",player.getGameProfile().getName());var keys=new ListTag();ResearchProgress.completed(player.getPersistentData()).stream().sorted().forEach(key->keys.add(StringTag.valueOf(key)));tag.put("research",keys);});
    }
    static Set<String> source(ItemStack stack,ServerPlayer reader){
        var tag=stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();if(!tag.hasUUID("Author"))return Set.of();
        var author=reader.server.getPlayerList().getPlayer(tag.getUUID("Author"));var archive=ShareKnowledgeArchive.get(reader.server);
        if(author!=null){archive.remember(author);return ResearchProgress.completed(author.getPersistentData());}
        return archive.research(tag.getUUID("Author")); // Never trust item-supplied research as server authority.
    }
    static int learn(ServerPlayer reader,Set<String> keys){
        int added=0;
        for(var key:new TreeSet<>(keys))if(ResearchProgress.add(reader.getPersistentData(),key)){ResearchWarp.award(reader,key);added++;if(!(reader instanceof net.neoforged.neoforge.common.util.FakePlayer))net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(reader,new ResearchCompletedPayload(key));}
        return added;
    }
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){var stack=player.getItemInHand(hand);if(player instanceof ServerPlayer server){if(!assigned(stack)){bind(stack,server);player.displayClientMessage(Component.translatable("ttmisc.shareTome.write"),false);}else{learn(server,source(stack,server));player.displayClientMessage(Component.translatable("ttmisc.shareTome.sync"),false);if(!(server instanceof net.neoforged.neoforge.common.util.FakePlayer))ThaumometerItem.sync(server,"research_complete");}}return InteractionResultHolder.sidedSuccess(stack,level.isClientSide);}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){var data=stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();lines.add(assigned(stack)?Component.translatable("ttmisc.shareTome.playerName",data.getString("player")):Component.translatable("ttmisc.shareTome.noAssign"));}
}
