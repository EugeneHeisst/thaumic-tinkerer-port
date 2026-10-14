package dev.eugheist.ttmodern.mixin;
import dev.eugheist.tc4modern.ArcaneRecipes;
import dev.eugheist.ttmodern.ThaumicTinkererModern;
import java.util.List;
import java.util.ArrayList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(value=ArcaneRecipes.class,remap=false)
public abstract class ArcaneRecipesMixin {
    @Inject(method="entries",at=@At("RETURN"),cancellable=true)
    private static void addon(CallbackInfoReturnable<List<ArcaneRecipes.Entry>> cir) {
        if(ThaumicTinkererModern.ARCANE_RECIPES.isEmpty())return;
        var result=new ArrayList<>(cir.getReturnValue());
        for(var recipe:ThaumicTinkererModern.ARCANE_RECIPES) {
            if(result.stream().anyMatch(r->r.key().equals(recipe.key())))throw new IllegalStateException("Duplicate arcane recipe "+recipe.key());
            result.add(recipe);
        }
        cir.setReturnValue(List.copyOf(result));
    }
}
