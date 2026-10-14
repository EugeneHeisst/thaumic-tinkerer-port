package dev.eugheist.ttmodern;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import dev.eugheist.tc4modern.TC4Modern;
final class EnchanterStructureAudit {
    private static void check(boolean ok,String label){if(!ok)throw new IllegalStateException("Enchanter structure: "+label);}
    static void run(ServerLevel level){
        var center=new BlockPos(8,230,8);
        var tile=net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("tc4modern","obsidian_totem"));
        check(tile!=Blocks.AIR,"registered obsidian totem");
        for(int x=-4;x<=4;x++)for(int z=-4;z<=4;z++)for(int y=0;y<=13;y++)level.removeBlock(center.offset(x,y,z),false);
        var bases=java.util.List.of(center.offset(-4,0,-4),center.offset(-4,0,0),center.offset(-4,0,4),center.offset(4,0,-4),center.offset(4,0,0),center.offset(4,0,4));
        for(var base:bases){level.setBlockAndUpdate(base,tile.defaultBlockState());level.setBlockAndUpdate(base.above(),tile.defaultBlockState());level.setBlockAndUpdate(base.above(2),TC4Modern.NITOR_BLOCK.get().defaultBlockState());}
        check(EnchanterStructure.find(level,center).size()==6,"six pillars in inclusive radius");
        var base=bases.get(0);level.removeBlock(base.above(2),false);
        check(EnchanterStructure.find(level,center).isEmpty(),"five pillars rejected");
        for(int y=0;y<12;y++)level.setBlockAndUpdate(base.above(y),tile.defaultBlockState());
        level.setBlockAndUpdate(base.above(12),TC4Modern.NITOR_BLOCK.get().defaultBlockState());
        check(base.above(12).equals(EnchanterStructure.cap(level,base)),"12 tiles accepted");
        level.setBlockAndUpdate(base.above(12),tile.defaultBlockState());level.setBlockAndUpdate(base.above(13),TC4Modern.NITOR_BLOCK.get().defaultBlockState());
        check(EnchanterStructure.cap(level,base)==null,"13 tiles rejected");
        for(int y=2;y<=13;y++)level.removeBlock(base.above(y),false);
        level.setBlockAndUpdate(base.above(2),TC4Modern.NITOR_BLOCK.get().defaultBlockState());
        check(EnchanterStructure.cap(level,base)!=null,"valid control before wrong material");
        level.setBlockAndUpdate(base,net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("tc4modern","obsidian_tile")).defaultBlockState());check(EnchanterStructure.cap(level,base)==null,"decorative tile rejected");
        level.setBlockAndUpdate(base,Blocks.OBSIDIAN.defaultBlockState());check(EnchanterStructure.cap(level,base)==null,"ordinary obsidian rejected");
        for(int x=-4;x<=4;x++)for(int z=-4;z<=4;z++)for(int y=0;y<=13;y++)level.removeBlock(center.offset(x,y,z),false);
        var recipe=dev.eugheist.tc4modern.InfusionItemRecipes.all().stream().filter(r->r.key().equals("ENCHANTER")).findFirst().orElseThrow();
        check(recipe.central().get()==net.minecraft.world.item.Items.ENCHANTING_TABLE&&recipe.instability()==15&&recipe.components().size()==8,"original central and eight ingredients");
        for(int i=0;i<5;i++)check(recipe.components().get(i).get()==FocusResearch.item("obsidian_tile"),"recipe uses tiles not totems");
        check(recipe.components().get(5).get()==TC4Modern.THAUMIUM.get()&&recipe.components().get(6).get()==TC4Modern.THAUMIUM.get()&&recipe.components().get(7).get()==ThaumicTinkererModern.SPELL_CLOTH.get(),"thaumium and spell cloth components");
        check(thaumcraft.api.research.ResearchCategories.getResearch("ENCHANTER").getPages().length==4,"three text pages and recipe");
        System.out.println("TT ENCHANTER STRUCTURE AUDIT: ALL PASS (six pillars, radius edges, height bounds, exact material)");
    }
}
