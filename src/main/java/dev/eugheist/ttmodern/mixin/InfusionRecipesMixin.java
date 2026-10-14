package dev.eugheist.ttmodern.mixin;
import dev.eugheist.tc4modern.InfusionItemRecipes;
import dev.eugheist.ttmodern.ThaumicTinkererModern;
import java.util.List;
import java.util.ArrayList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(value=InfusionItemRecipes.class,remap=false)
public abstract class InfusionRecipesMixin {
    @Inject(method="all",at=@At("RETURN"),cancellable=true)
    private static void addon(CallbackInfoReturnable<List<InfusionItemRecipes.Recipe>> cir){
        if(ThaumicTinkererModern.INFUSION_RECIPES.isEmpty())return;
        var result=new ArrayList<>(cir.getReturnValue());
        for(var recipe:ThaumicTinkererModern.INFUSION_RECIPES){
            if(result.stream().anyMatch(r->r.key().equals(recipe.key())))throw new IllegalStateException("Duplicate infusion recipe "+recipe.key());
            result.add(recipe);
        }
        cir.setReturnValue(List.copyOf(result));
    }
}
