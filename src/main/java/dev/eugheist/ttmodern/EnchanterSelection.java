package dev.eugheist.ttmodern;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import thaumcraft.api.aspects.AspectList;

/** Server-side selection validation; absent optional enchantments cannot be selected. */
final class EnchanterSelection {
    private final LinkedHashMap<String,Integer> selected = new LinkedHashMap<>();
    Map<String,Integer> entries() { return java.util.Collections.unmodifiableMap(selected); }
    boolean change(RegistryAccess access, ItemStack tool, Set<String> research, String id, int requested) {
        if(requested==-1) return selected.remove(id)!=null;
        var data=EnchanterCosts.ENTRIES.get(id);
        var location=ResourceLocation.tryParse(id);
        if(id.equals("ttinkerer:auto_smelt")&&net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(tool.getItem()).toString().equals("tc4modern:elemental_axe"))return false;
        if(data==null || location==null || tool.isEmpty() || tool.isEnchanted()
            || !data.research().isEmpty() && !research.contains(data.research())) return false;
        var registry=access.registryOrThrow(Registries.ENCHANTMENT);
        var holder=registry.getHolder(location).orElse(null);
        if(holder==null || !holder.value().isSupportedItem(tool)) return false;
        int level=selected.containsKey(id)?Math.max(1,Math.min(holder.value().getMaxLevel(),requested)):1;
        for(var other:selected.keySet()) {
            if(other.equals(id)) continue;
            var otherHolder=registry.getHolder(ResourceLocation.parse(other)).orElse(null);
            if(otherHolder==null || !Enchantment.areCompatible(holder,otherHolder)) return false;
        }
        selected.put(id,level);
        return true;
    }
    AspectList total() {
        var result=new AspectList();
        selected.forEach((id,level)->{
            var cost=EnchanterCosts.ENTRIES.get(id).cost(level);
            for(var aspect:cost.getAspects()) result.add(aspect,cost.getAmount(aspect));
        });
        return result;
    }
    void clear(){selected.clear();}
}
