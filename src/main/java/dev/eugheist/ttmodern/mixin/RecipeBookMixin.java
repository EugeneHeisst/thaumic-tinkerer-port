package dev.eugheist.ttmodern.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Add namespaced vanilla recipes to TC4's recipe pages. Client-only. */
@Mixin(targets="dev.eugheist.tc4modern.RecipeBookBridge",remap=false)
public abstract class RecipeBookMixin {
    @ModifyVariable(method="draw",at=@At("HEAD"),argsOnly=true,ordinal=0)
    private static String infusedSeedCycle(String key){return key.equals("INFUSED_POTIONS")?"INFUSED_POTIONS"+(System.currentTimeMillis()/1000%4):key;}
    @Inject(method="draw",at=@At("HEAD"),cancellable=true)
    private static void drawAddon(GuiGraphics gui,String key,int x,int y,CallbackInfoReturnable<Boolean> cir) {
        if(!key.startsWith("ttinkerer:")) return;
        var mc=Minecraft.getInstance(); if(mc.level==null || mc.getConnection()==null) return;
        var recipe=mc.getConnection().getRecipeManager().byKey(ResourceLocation.parse(key));
        if(recipe.isEmpty()) return;
        var value=recipe.get().value();var ingredients=value.getIngredients();
        int width=value instanceof ShapedRecipe shaped?shaped.getWidth():3;
        var title=net.minecraft.network.chat.Component.translatable("recipe.type.workbench");
        gui.drawString(mc.font,title,x+56-mc.font.width(title)/2,y,0x505050,false);
        var out=value.getResultItem(mc.level.registryAccess());
        gui.renderItem(out,x+48,y+32);gui.renderItemDecorations(mc.font,out,x+48,y+32);
        for(int i=0;i<ingredients.size();i++) {
            var choices=ingredients.get(i).getItems();if(choices.length==0)continue;
            var stack=choices[(int)(System.currentTimeMillis()/1000%choices.length)];
            int sx=x+16+(i%width)*32, sy=y+76+(i/width)*32;
            gui.fill(sx-1,sy-1,sx+17,sy+17,0x30303030);gui.renderItem(stack,sx,sy);
        }
        cir.setReturnValue(true);
    }
}
