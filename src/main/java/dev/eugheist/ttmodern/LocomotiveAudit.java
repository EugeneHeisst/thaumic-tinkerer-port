package dev.eugheist.ttmodern;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.item.*;
final class LocomotiveAudit{
    static void check(boolean ok,String label){if(!ok)throw new IllegalStateException("TT PORT AUDIT FAILED: locomotive "+label);}
    static void time(ServerLevel level,long value){((net.minecraft.world.level.storage.ServerLevelData)level.getLevelData()).setGameTime(value);}
    static void tick(ServerLevel level,BlockPos pos,long value){time(level,value);var tile=(LocomotiveEntity)level.getBlockEntity(pos);LocomotiveEntity.tick(level,pos,tile.getBlockState(),tile);}
    static void run(ServerLevel level){long old=level.getGameTime();var start=new BlockPos(8,300,24);var left=start.west(5);var right=start.east(5);try{
        level.setBlockAndUpdate(left,ThaumicTinkererModern.LOCOMOTIVE_RELAY.get().defaultBlockState());level.setBlockAndUpdate(right,ThaumicTinkererModern.LOCOMOTIVE_RELAY.get().defaultBlockState());level.setBlockAndUpdate(start,ThaumicTinkererModern.LOCOMOTIVE.get().defaultBlockState());
        time(level,40);var relay=(LocomotiveRelayEntity)level.getBlockEntity(left);LocomotiveRelayEntity.tick(level,left,relay.getBlockState(),relay);var train=(LocomotiveEntity)level.getBlockEntity(start);check(right.equals(relay.partner)&&train.linked&&train.direction==Direction.EAST,"reciprocal relay pair links train");
        level.setBlockAndUpdate(start.above(),Blocks.CHEST.defaultBlockState());var chest=(ChestBlockEntity)level.getBlockEntity(start.above());chest.setItem(0,new ItemStack(Items.DIAMOND,37));
        tick(level,start,99);check(level.getBlockEntity(start)==train,"100tick movement cadence");tick(level,start,101);var moved=start.east();check(level.isEmptyBlock(start)&&level.isEmptyBlock(start.above())&&level.getBlockEntity(moved) instanceof LocomotiveEntity,"locomotive moved one block");var passenger=(ChestBlockEntity)level.getBlockEntity(moved.above());check(passenger!=null&&passenger.getItem(0).getCount()==37&&passenger.getBlockPos().equals(moved.above()),"passenger inventory and tile coordinates preserved");
        tick(level,moved,101);check(level.getBlockEntity(moved) instanceof LocomotiveEntity,"same tick cannot move twice");
        var copy=new LocomotiveEntity(moved,level.getBlockState(moved));copy.loadWithComponents(level.getBlockEntity(moved).saveWithoutMetadata(level.registryAccess()),level.registryAccess());check(copy.linked&&left.equals(copy.first)&&right.equals(copy.second)&&copy.lastMove==101&&copy.direction==Direction.EAST,"movement data persistence");
        level.setBlockAndUpdate(moved.east().above(),Blocks.STONE.defaultBlockState());tick(level,moved,200);check(((LocomotiveEntity)level.getBlockEntity(moved)).direction==Direction.WEST,"occupied headspace reverses");tick(level,moved,201);check(level.getBlockEntity(start) instanceof LocomotiveEntity&&((ChestBlockEntity)level.getBlockEntity(start.above())).getItem(0).getCount()==37,"reverse move conserves inventory");
        level.setBlockAndUpdate(start.north(),Blocks.REDSTONE_BLOCK.defaultBlockState());tick(level,start,301);check(level.getBlockEntity(start) instanceof LocomotiveEntity,"redstone disables movement");level.removeBlock(start.north(),false);
        level.removeBlockEntity(left);level.removeBlock(left,false);tick(level,start,401);check(!((LocomotiveEntity)level.getBlockEntity(start)).linked,"broken relay unlinks train");
        check(level.getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class,new net.minecraft.world.phys.AABB(start).inflate(6)).isEmpty(),"moving inventory creates no spilled drops");
        for(var key:java.util.List.of("FOCUS_FLIGHT","FOCUS_TELEKINESIS","BLOOD_SWORD","SUMMON",LocomotiveResearch.KEY))check(java.util.Arrays.asList(thaumcraft.api.research.ResearchCategories.getResearch(key).parentsHidden).contains("INFUSION"),"original automatic hidden infusion prerequisite "+key);
        level.removeBlockEntity(start.above());level.setBlockAndUpdate(start.above(),Blocks.BEDROCK.defaultBlockState());level.setBlockAndUpdate(left,ThaumicTinkererModern.LOCOMOTIVE_RELAY.get().defaultBlockState());
        relay=(LocomotiveRelayEntity)level.getBlockEntity(left);relay.pair(right);train=(LocomotiveEntity)level.getBlockEntity(start);train.first=left;train.second=right;train.linked=true;train.direction=Direction.WEST;
        tick(level,start,501);check(level.getBlockEntity(start.west()) instanceof LocomotiveEntity&&level.getBlockState(start.above()).is(Blocks.BEDROCK),"unbreakable passenger is left intact");
        check(thaumcraft.api.research.ResearchCategories.getResearch("LEVITATOR").category.equals("ARTIFICE")&&thaumcraft.api.research.ResearchCategories.getResearch(LocomotiveResearch.KEY).category.equals("THAUMICTINKERER"),"base research not overwritten");
        var recipe=dev.eugheist.tc4modern.InfusionItemRecipes.all().stream().filter(r->r.key().equals(LocomotiveResearch.KEY)).findFirst().orElseThrow();check(recipe.central().get()==FocusResearch.item("levitator")&&recipe.components().size()==4&&recipe.instability()==4&&recipe.aspects().getAmount(thaumcraft.api.aspects.Aspect.ORDER)==20,"original infusion costs and central levitator");
        check(recipe.components().get(0).get()==Items.IRON_INGOT&&recipe.components().get(1).get()==Items.FEATHER&&recipe.components().get(2).get()==Items.IRON_INGOT&&recipe.components().get(3).get()==FocusResearch.item("obsidian_tile"),"exact original locomotive component sequence");
        var relayRecipe=dev.eugheist.tc4modern.ArcaneRecipes.entries().stream().filter(r->r.key().equals("LEVITATOR_RELAY")).findFirst().orElseThrow();check(relayRecipe.ingredients().get(4).test(new ItemStack(Items.IRON_INGOT))&&!relayRecipe.ingredients().get(4).test(new ItemStack(Items.DIAMOND)),"original relay iron center");
        LocomotiveMovableAudit.run(level,start,left,right);
        if(net.neoforged.fml.ModList.get().isLoaded("ae2"))AE2LocomotiveAudit.run(level,start,left,right);
        System.out.println("TT LOCOMOTIVE AUDIT: ALL PASS (relay pairing, cadence, passenger inventory/coordinates, same-tick guard, persistence, reversal, redstone, broken relay, research collision, recipe)");
    }finally{for(int x=2;x<=14;x++)for(int y=300;y<=301;y++)for(int z=23;z<=25;z++){var at=new BlockPos(x,y,z);level.removeBlockEntity(at);level.removeBlock(at,false);}time(level,old);}}
}
