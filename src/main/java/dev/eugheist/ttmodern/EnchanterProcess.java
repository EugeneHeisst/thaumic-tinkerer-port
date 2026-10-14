package dev.eugheist.ttmodern;

import java.util.ArrayList;
import java.util.Set;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import dev.eugheist.tc4modern.WandData;
import dev.eugheist.tc4modern.WandParts;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

/** Machine-independent server process; the block supplies the verified structure state. */
final class EnchanterProcess {
    final EnchanterSelection selection=new EnchanterSelection();
    private AspectList current=new AspectList();
    private boolean working;
    int lastDrainIndex;
    boolean working(){return working;}
    int collected(Aspect aspect){return current.getAmount(aspect);}
    boolean start(){if(working||selection.entries().isEmpty())return false;working=true;return true;}
    boolean change(ServerLevel level,ItemStack tool,Set<String> research,String id,int rank){
        return !working&&selection.change(level.registryAccess(),tool,research,id,rank);
    }
    /** Returns true whenever persistent state or inventory was changed. */
    boolean tick(ServerLevel level,ItemStack tool,ItemStack wand,boolean structureValid){
        if(tool.isEmpty()){boolean changed=working||!selection.entries().isEmpty();working=false;selection.clear();return changed;}
        if(!working)return false;
        if(!structureValid){working=false;current=new AspectList();return true;}
        var total=selection.total();
        var missing=new ArrayList<Aspect>();
        for(var aspect:Aspect.getPrimalAspects())if(current.getAmount(aspect)<total.getAmount(aspect))missing.add(aspect);
        if(missing.isEmpty()){
            var registry=level.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
            // Resolve all entries before mutating the tool.
            for(var id:selection.entries().keySet())if(registry.getHolder(ResourceLocation.parse(id)).isEmpty()){working=false;return true;}
            selection.entries().forEach((id,rank)->tool.enchant(registry.getHolder(ResourceLocation.parse(id)).orElseThrow(),rank));
            working=false;current=new AspectList();selection.clear();return true;
        }
        if(!WandData.isWand(wand)||WandParts.isStaff(wand))return false;
        missing.removeIf(aspect->WandData.get(wand,aspect)<100);
        if(missing.isEmpty())return false;
        lastDrainIndex=level.random.nextInt(missing.size());
        var aspect=missing.get(lastDrainIndex);
        if(!WandData.consumeCrafting(wand,new AspectList().add(aspect,1),true,null))return false;
        current.add(aspect,1);return true;
    }
    CompoundTag save(){
        var tag=new CompoundTag();tag.putBoolean("Working",working);
        var choices=new CompoundTag();selection.entries().forEach(choices::putInt);tag.put("Selection",choices);
        var progress=new CompoundTag();for(var aspect:Aspect.getPrimalAspects())progress.putInt(aspect.getTag(),current.getAmount(aspect));tag.put("Progress",progress);return tag;
    }
    void load(ServerLevel level,ItemStack tool,CompoundTag tag){
        selection.clear();current=new AspectList();working=false;
        var choices=tag.getCompound("Selection");
        var research=EnchanterCosts.ENTRIES.values().stream().map(EnchanterCosts.Entry::research).collect(java.util.stream.Collectors.toSet());
        for(var id:choices.getAllKeys()){
            if(selection.change(level.registryAccess(),tool,research,id,1))selection.change(level.registryAccess(),tool,research,id,choices.getInt(id));
        }
        var total=selection.total();var progress=tag.getCompound("Progress");
        for(var aspect:Aspect.getPrimalAspects())current.add(aspect,Math.max(0,Math.min(total.getAmount(aspect),progress.getInt(aspect.getTag()))));
        working=tag.getBoolean("Working")&&!selection.entries().isEmpty();
    }
}
