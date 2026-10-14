package dev.eugheist.ttmodern;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import thaumcraft.api.aspects.Aspect;
import dev.eugheist.tc4modern.InfusionContent;
final class SummonAudit{
    static void check(boolean ok,String label){if(!ok)throw new IllegalStateException("TT PORT AUDIT FAILED: summon "+label);}
    static InfusionContent.PedestalEntity[] pedestals(ServerLevel level,BlockPos pos){var result=new InfusionContent.PedestalEntity[3];int i=0;for(var offset:new BlockPos[]{new BlockPos(-1,0,-1),new BlockPos(-1,0,0),new BlockPos(0,0,-1)}){var at=pos.offset(offset);level.setBlockAndUpdate(at,InfusionContent.PEDESTAL.get().defaultBlockState());result[i++]=(InfusionContent.PedestalEntity)level.getBlockEntity(at);}return result;}
    static void fill(InfusionContent.PedestalEntity[] ped,int form,Aspect... aspects){for(int i=0;i<3;i++)ped[i].setItem(MobAspects.stack(aspects[i],form));}
    static void tick(ServerLevel level,BlockPos pos,long time){((net.minecraft.world.level.storage.ServerLevelData)level.getLevelData()).setGameTime(time);var tile=(SummonEntity)level.getBlockEntity(pos);SummonEntity.tick(level,pos,tile.getBlockState(),tile);}
    static long cows(ServerLevel level,BlockPos pos){return level.getEntitiesOfClass(net.minecraft.world.entity.animal.Cow.class,new AABB(pos).inflate(2)).size();}
    static void run(ServerLevel level){long old=level.getGameTime();var pos=new BlockPos(8,300,8);var second=pos.east(5);try{
        level.setBlockAndUpdate(pos,ThaumicTinkererModern.SUMMON.get().defaultBlockState());var ped=pedestals(level,pos);fill(ped,0,Aspect.BEAST,Aspect.EARTH,Aspect.BEAST);tick(level,pos,299);check(cows(level,pos)==0&&!ped[0].item().isEmpty(),"300-tick cadence");tick(level,pos,300);check(cows(level,pos)==1&&java.util.Arrays.stream(ped).allMatch(p->p.item().isEmpty()),"three raw essences consumed for cow");
        fill(ped,0,Aspect.BEAST,Aspect.EARTH,Aspect.EARTH);tick(level,pos,600);check(cows(level,pos)==1&&!ped[0].item().isEmpty(),"duplicate count must match");
        fill(ped,1,Aspect.BEAST,Aspect.EARTH,Aspect.BEAST);level.setBlockAndUpdate(pos.east(),Blocks.REDSTONE_BLOCK.defaultBlockState());tick(level,pos,900);check(cows(level,pos)==1&&!ped[0].item().isEmpty(),"redstone disables summoning");level.removeBlock(pos.east(),false);tick(level,pos,1200);check(cows(level,pos)==2&&ped[0].item().isEmpty(),"condensed essences consumed");
        fill(ped,2,Aspect.BEAST,Aspect.EARTH,Aspect.BEAST);tick(level,pos,1500);check(cows(level,pos)==2,"infused waits1200 ticks");tick(level,pos,2400);check(cows(level,pos)==3&&java.util.Arrays.stream(ped).allMatch(p->!p.item().isEmpty()),"infused spawns without consumption");tick(level,pos,2400);check(cows(level,pos)==3,"same tick cannot duplicate spawn");
        var tile=(SummonEntity)level.getBlockEntity(pos);var saved=tile.saveWithoutMetadata(level.registryAccess());var copy=new SummonEntity(pos,tile.getBlockState());copy.loadWithComponents(saved,level.registryAccess());check(copy.lastCheck==2400,"cadence persistence");
        level.setBlockAndUpdate(second,ThaumicTinkererModern.SUMMON.get().defaultBlockState());var next=pedestals(level,second);for(int i=0;i<3;i++){next[i].setItem(ped[i].item());ped[i].setItem(ItemStack.EMPTY);}tick(level,second,3600);check(cows(level,second)==0&&SummonEntity.matchesLast(next[0].item(),level,second),"moved infused essences skip one cycle and rebind");tick(level,second,4800);check(cows(level,second)==1,"next bound cycle spawns");
        // Legacy consumes complete pedestal stacks, including mixed raw/condensed/infused triples.
        for(int i=0;i<3;i++){var stack=MobAspects.stack(i==1?Aspect.EARTH:Aspect.BEAST,i);stack.setCount(7);ped[i].setItem(stack);}
        tick(level,pos,5100);check(cows(level,pos)==4&&java.util.Arrays.stream(ped).allMatch(p->p.item().isEmpty()),"mixed forms consume complete stacks at ordinary cadence");
        fill(ped,0,Aspect.BEAST,Aspect.EARTH,Aspect.BEAST);
        java.util.function.Consumer<net.neoforged.neoforge.event.entity.EntityJoinLevelEvent> cancel=event->{if(event.getLevel()==level&&event.getEntity() instanceof net.minecraft.world.entity.animal.Cow&&event.getEntity().blockPosition().equals(pos.above()))event.setCanceled(true);};
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(cancel);
        try{tick(level,pos,5400);check(cows(level,pos)==4&&java.util.Arrays.stream(ped).allMatch(p->!p.item().isEmpty()),"cancelled spawn conserves all essences");}
        finally{net.neoforged.neoforge.common.NeoForge.EVENT_BUS.unregister(cancel);}
        tick(level,pos,5700);check(cows(level,pos)==5&&java.util.Arrays.stream(ped).allMatch(p->p.item().isEmpty()),"summoning recovers after cancellation");
        check(level.getEntitiesOfClass(net.minecraft.world.entity.animal.Cow.class,new AABB(pos).inflate(2)).stream().allMatch(Mob::isPersistenceRequired),"summoned mobs persist");
        for(var aspect:MobAspects.ASPECTS){var raw=MobAspects.stack(aspect,0);check(dev.eugheist.tc4modern.AspectTable.get(raw).equals(java.util.Map.of(aspect.getTag(),8)),"direct tags usable by recipe resolver");var condensed=MobAspects.stack(aspect,1);check(!dev.eugheist.tc4modern.RecipeAspects.get(condensed,level).isEmpty(),"condensed recipe generates tags");var recipe=dev.eugheist.tc4modern.InfusionItemRecipes.all().stream().filter(r->r.key().equals("MOB_ASPECT_INFUSE_"+aspect.getTag())).findFirst().orElseThrow();check(recipe.components().size()==8&&recipe.central().get()==condensed.getItem()&&recipe.aspects().getAmount(aspect)==10&&recipe.instability()==4,"infusion exact nine condensed plus10aspect");}
        check(SummonEntity.RECIPES.size()==29&&dev.eugheist.tc4modern.ResearchWarp.amount("SUMMON")==3,"all original mob recipes and warp");
        var arcane=dev.eugheist.tc4modern.ArcaneRecipes.entries().stream().filter(r->r.key().equals("SUMMON0")).findFirst().orElseThrow();check(arcane.ingredients().getFirst().test(new ItemStack(FocusResearch.item("obsidian_tile")))&&arcane.cost().getAmount(Aspect.ORDER)==50&&arcane.cost().getAmount(Aspect.ENTROPY)==50,"tablet recipe resolved original cosmeticSolid1 as obsidian tile");
        System.out.println("TT SUMMON AUDIT: ALL PASS (cadence, raw/condensed costs, duplicates, redstone, infused reuse/rebinding, persistence,29 recipes, recursive tags, infusion/arcane costs)");
    }finally{for(var at:new BlockPos[]{pos,second}){for(var mob:level.getEntitiesOfClass(Mob.class,new AABB(at).inflate(2)))mob.discard();for(var offset:new BlockPos[]{BlockPos.ZERO,new BlockPos(-1,0,-1),new BlockPos(-1,0,0),new BlockPos(0,0,-1)}){level.removeBlockEntity(at.offset(offset));level.removeBlock(at.offset(offset),false);}}((net.minecraft.world.level.storage.ServerLevelData)level.getLevelData()).setGameTime(old);}}
}
