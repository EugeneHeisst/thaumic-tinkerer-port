package dev.eugheist.ttmodern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
/** Original HUDHandler dislocation indicator: item icon and tile-data marker beside wand dial. */
@EventBusSubscriber(modid="ttinkerer",value=Dist.CLIENT)
public final class DislocationHud {
    @SubscribeEvent public static void render(RenderGuiEvent.Post event){
        var mc=Minecraft.getInstance();if(mc.player==null||mc.screen!=null||mc.options.hideGui)return;
        var wand=mc.player.getMainHandItem();if(!dev.eugheist.tc4modern.WandData.isWand(wand))return;
        var focus=dev.eugheist.tc4modern.FociContent.getFocusItem(wand);if(!(focus.getItem() instanceof DislocationFocus))return;
        draw(event.getGuiGraphics(),focus,dev.eugheist.tc4modern.WarpClientConfig.dialBottom());
    }
    static boolean draw(GuiGraphics gui,ItemStack focus,boolean bottom){
        var tag=DislocationFocus.payload(focus);if(!DislocationStorage.available(tag))return false;
        var stateTag=tag.getCompound("dislocation");if(!stateTag.contains("state",10))return false;
        var state=net.minecraft.nbt.NbtUtils.readBlockState(net.minecraft.core.registries.BuiltInRegistries.BLOCK.asLookup(),stateTag.getCompound("state"));if(state.isAir())return false;
        var font=Minecraft.getInstance().font;int x=4,y=bottom?gui.guiHeight()-70:50;
        var title=Component.translatable("ttmisc.focusDislocation.tooltip");boolean tile=!stateTag.getCompound("tileCmp").isEmpty();
        gui.fill(x-1,y-1,x+18,y+18,0x66000000);gui.fill(x+18,y,x+22+font.width(title),y+(tile?18:9),0x66000000);
        gui.drawString(font,title,x+20,y,0xFFAA00,false);
        if(tile)gui.drawString(font,Component.translatable("ttmisc.focusDislocation.tooltipExtra"),x+20,y+9,0xFFAA00,false);
        var icon=new ItemStack(state.getBlock());if(!icon.isEmpty())gui.renderItem(icon,x,y);
        return true;
    }
}
