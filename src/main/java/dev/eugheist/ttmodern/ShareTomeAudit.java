package dev.eugheist.ttmodern;
import java.util.*;
import net.minecraft.nbt.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.CraftingInput;
import dev.eugheist.tc4modern.*;
final class ShareTomeAudit{
    static void check(boolean ok,String label){if(!ok)throw new IllegalStateException("TT PORT AUDIT FAILED: share tome "+label);}
    static void run(ServerLevel level){
        var author=net.neoforged.neoforge.common.util.FakePlayerFactory.get(level,new com.mojang.authlib.GameProfile(UUID.fromString("c98ab010-9992-4be5-9407-9b300ad75885"),"TomeAuthor"));
        var reader=net.neoforged.neoforge.common.util.FakePlayerFactory.get(level,new com.mojang.authlib.GameProfile(UUID.fromString("ceb7cf51-545b-4e5c-afb9-cf14232965e8"),"TomeReader"));
        author.getPersistentData().remove(ResearchKnowledge.KEY);reader.getPersistentData().remove(ResearchKnowledge.KEY);
        ResearchProgress.add(author.getPersistentData(),"NITOR");var book=new ItemStack(ThaumicTinkererModern.SHARE_TOME.get());check(!ShareTomeItem.assigned(book),"initially unassigned");ShareTomeItem.bind(book,author);check(ShareTomeItem.assigned(book),"author bound");
        var before=ResearchKnowledge.points(reader.getPersistentData(),"aer");var source=ShareTomeItem.source(book,reader);check(source.contains("NITOR"),"offline archive contains author research");check(ShareTomeItem.learn(reader,source)==1&&ResearchProgress.completed(reader.getPersistentData()).contains("NITOR"),"reader learns author knowledge");check(ShareTomeItem.learn(reader,source)==0,"repeat grants nothing");check(ResearchKnowledge.points(reader.getPersistentData(),"aer")==before,"aspect pools unchanged");
        ResearchProgress.add(author.getPersistentData(),"ALUMENTUM");net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent(author));check(ShareTomeItem.source(book,reader).contains("ALUMENTUM"),"logout refreshes author research");
        var archive=ShareKnowledgeArchive.get(level.getServer());var data=archive.save(new CompoundTag(),level.registryAccess());check(ShareKnowledgeArchive.load(data,level.registryAccess()).research(author.getUUID()).contains("ALUMENTUM"),"archive reload retains research");
        int warpBefore=PlayerWarp.load(reader).get(WarpState.Kind.PERMANENT);ShareTomeItem.learn(reader,Set.of("RESEARCHER2"));int afterWarp=PlayerWarp.load(reader).get(WarpState.Kind.PERMANENT);ShareTomeItem.learn(reader,Set.of("RESEARCHER2"));check(PlayerWarp.load(reader).get(WarpState.Kind.PERMANENT)==afterWarp&&afterWarp==warpBefore+(WarpConfig.wussMode()?0:1),"warp granted only once");
        var recipe=new ShareTomeRecipe(net.minecraft.world.item.crafting.CraftingBookCategory.MISC);var grid=new ArrayList<ItemStack>();for(var ingredient:recipe.getIngredients())grid.add(ingredient.isEmpty()?ItemStack.EMPTY:ingredient.getItems()[0].copy());var input=CraftingInput.of(3,3,grid);check(recipe.matches(input,level)&&recipe.assemble(input,level.registryAccess()).is(ThaumicTinkererModern.SHARE_TOME.get()),"original cross recipe");boolean enabled=TinkererConfig.survivalShareTome();try{TinkererConfig.SURVIVAL_SHARE_TOME.set(false);check(!recipe.matches(input,level),"survival config disables crafting");}finally{TinkererConfig.SURVIVAL_SHARE_TOME.set(enabled);}
        check(recipe.getRemainingItems(input).stream().allMatch(ItemStack::isEmpty),"ingredients consumed");grid.set(0,new ItemStack(Items.DIAMOND));check(!recipe.matches(CraftingInput.of(3,3,grid),level),"extra input rejected");
        var forged=new ItemStack(ThaumicTinkererModern.SHARE_TOME.get());net.minecraft.world.item.component.CustomData.update(net.minecraft.core.component.DataComponents.CUSTOM_DATA,forged,tag->{tag.putUUID("Author",UUID.randomUUID());var keys=new ListTag();keys.add(StringTag.valueOf("ALUMENTUM"));tag.put("research",keys);});check(ShareTomeItem.source(forged,reader).isEmpty(),"item snapshot cannot forge server knowledge");
        check(thaumcraft.api.research.ResearchCategories.getResearch("SHARE_TOME").isAutoUnlock(),"initial research auto unlocked");
        System.out.println("TT SHARE TOME AUDIT: ALL PASS (binding, offline archive, logout refresh, one-time research, point conservation, original recipe, forged snapshot rejection)");
    }
}
