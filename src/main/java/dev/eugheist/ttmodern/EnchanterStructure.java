package dev.eugheist.ttmodern;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import dev.eugheist.tc4modern.TC4Modern;

/** Original six obsidian-totem pillars, each capped by nitor. Never loads chunks. */
final class EnchanterStructure {
    static BlockPos cap(Level level, BlockPos base) {
        var tile=BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath("tc4modern","obsidian_totem"));
        for(int height=0;height<=12;height++) {
            var pos=base.above(height);
            if(!level.isInWorldBounds(pos)||!level.hasChunkAt(pos)) return null;
            var state=level.getBlockState(pos);
            if(state.is(tile)) continue;
            return height>=2 && state.is(TC4Modern.NITOR_BLOCK.get()) ? pos : null;
        }
        return null;
    }
    static List<BlockPos> find(Level level, BlockPos center) {
        var result=new ArrayList<BlockPos>(6);
        for(int x=-4;x<=4;x++) for(int z=-4;z<=4;z++) {
            var cap=cap(level,center.offset(x,0,z));
            if(cap!=null) result.add(cap);
            if(result.size()==6) return List.copyOf(result);
        }
        return List.of();
    }
    private EnchanterStructure() {}
}
