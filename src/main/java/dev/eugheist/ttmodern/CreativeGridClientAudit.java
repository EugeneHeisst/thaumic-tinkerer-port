package dev.eugheist.ttmodern;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import java.util.List;

/** Real GUI item renderer contact sheets, not a replacement creative inventory. Audit-only. */
final class CreativeGridClientAudit extends Screen {
    private final List<ItemStack> stacks;
    private final Language previousLanguage;
    private final Screen previousScreen;
    private int frames,localeIndex;
    private String locale;
    private CreativeGridClientAudit(Minecraft mc){
        super(Component.literal("Thaumic Tinkerer creative item audit"));
        previousLanguage=Language.getInstance();previousScreen=mc.screen;
        locale="en_us";loadLocale(mc);stacks=TinkererCreativeTab.contents();
    }
    static void start(Minecraft mc){mc.setScreen(new CreativeGridClientAudit(mc));}
    private void loadLocale(Minecraft mc){Language.inject(ClientLanguage.loadFrom(mc.getResourceManager(),List.of("en_us",locale),false));}
    @Override public void render(GuiGraphics gui,int mouseX,int mouseY,float partial){
        gui.fill(0,0,width,height,0xFF202020);
        gui.drawString(font,"Thaumic Tinkerer / "+locale+" / "+stacks.size()+" stacks",10,8,0xFFFFFF,false);
        int columns=(width-20)/18;
        if(columns<1||26+((stacks.size()+columns-1)/columns)*23>height)throw new IllegalStateException("Creative contact sheet does not fit framebuffer");
        for(int i=0;i<stacks.size();i++){
            int x=10+i%columns*18,y=26+i/columns*23;
            gui.fill(x,y,x+18,y+18,0xFF8B8B8B);
            gui.renderItem(stacks.get(i),x+1,y+1);
        }
        gui.flush();frames++;
    }
    static void capture(Minecraft mc){
        if(!(mc.screen instanceof CreativeGridClientAudit screen)||screen.frames<6)return;
        EnchanterClientAudit.Client.shot(mc,"creative-"+screen.locale+".png");
        try{
            var lines=new java.util.ArrayList<String>();lines.add("index\titem\tname\tpattern\tfoil\ttint");
            for(int i=0;i<screen.stacks.size();i++){
                var stack=screen.stacks.get(i);
                lines.add(i+"\t"+net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem())+"\t"+stack.getHoverName().getString()+"\t"+(stack.getItem() instanceof SoulMouldItem?SoulMouldItem.pattern(stack):"")+"\t"+stack.hasFoil()+"\t"+Integer.toHexString(mc.getItemColors().getColor(stack,0)));
            }
            java.nio.file.Files.write(java.nio.file.Path.of("enchanter-audit","creative-"+screen.locale+".tsv"),lines);
        }catch(java.io.IOException error){throw new java.io.UncheckedIOException(error);}
        if(++screen.localeIndex==2){
            Language.inject(screen.previousLanguage);mc.setScreen(screen.previousScreen);
            System.out.println("TT CREATIVE GRID FRAME AUDIT: ALL PASS (actual GUI item renderer EN/RU contact sheets and manifests)");
        }else{
            screen.locale="ru_ru";screen.loadLocale(mc);screen.stacks.clear();screen.stacks.addAll(TinkererCreativeTab.contents());screen.frames=0;
        }
    }
}
