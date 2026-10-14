package dev.eugheist.ttmodern.mixin;
import dev.eugheist.tc4modern.CrucibleRecipes;
import dev.eugheist.ttmodern.ThaumicTinkererModern;
import java.util.Map;
import java.util.LinkedHashMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thaumcraft.api.crafting.CrucibleRecipe;
/** Add addon recipes without altering the base mod's immutable recipe map. */
@Mixin(value=CrucibleRecipes.class, remap=false)
public abstract class CrucibleRecipesMixin {
    @Inject(method="byKey",at=@At("RETURN"),cancellable=true)
    private static void addon(CallbackInfoReturnable<Map<String,CrucibleRecipe>> cir) {
        if(ThaumicTinkererModern.CRUCIBLE_RECIPES.isEmpty()) return;
        var merged=new LinkedHashMap<>(cir.getReturnValue());
        for(var entry:ThaumicTinkererModern.CRUCIBLE_RECIPES.entrySet()) {
            if(entry.getKey().startsWith("FIRE_")&&!dev.eugheist.ttmodern.TinkererConfig.fireEnabled())continue;
            if(merged.putIfAbsent(entry.getKey(),entry.getValue())!=null) throw new IllegalStateException("Duplicate addon crucible key: "+entry.getKey());
        }
        cir.setReturnValue(java.util.Collections.unmodifiableMap(merged));
    }
}
