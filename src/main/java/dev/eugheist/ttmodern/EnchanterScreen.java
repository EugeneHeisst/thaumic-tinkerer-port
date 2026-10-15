package dev.eugheist.ttmodern;
import java.util.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import thaumcraft.api.aspects.*;
public final class EnchanterScreen extends AbstractContainerScreen<EnchanterMenu> {
    private static final ResourceLocation TEXTURE=ResourceLocation.fromNamespaceAndPath("ttinkerer","textures/gui/enchanter.png");
    private record Hit(int x,int y,int width,int height,int button,List<Component> tooltip) {
        boolean contains(double mx,double my){return mx>=x&&mx<x+width&&my>=y&&my<y+height;}
    }
    private final List<Hit> hits=new ArrayList<>();
    public EnchanterScreen(EnchanterMenu menu,Inventory inventory,Component title){super(menu,inventory,title);}
    private List<Integer> available(){
        var result=new ArrayList<Integer>();if(minecraft.level==null||menu.working())return result;
        var tag=new net.minecraft.nbt.CompoundTag();tag.put(dev.eugheist.tc4modern.ResearchKnowledge.KEY,dev.eugheist.tc4modern.ClientKnowledge.knowledge.copy());
        var research=dev.eugheist.tc4modern.ResearchProgress.completed(tag);var selection=new EnchanterSelection();var tool=menu.slots.get(0).getItem();
        for(int i=0;i<40;i++)if(menu.rank(i)>0){selection.change(minecraft.level.registryAccess(),tool,research,EnchanterMenu.IDS.get(i),1);selection.change(minecraft.level.registryAccess(),tool,research,EnchanterMenu.IDS.get(i),menu.rank(i));}
        for(int i=0;i<40&&result.size()<16;i++)if(menu.rank(i)==0&&selection.change(minecraft.level.registryAccess(),tool,research,EnchanterMenu.IDS.get(i),1)){
            result.add(i);selection.change(minecraft.level.registryAccess(),tool,research,EnchanterMenu.IDS.get(i),-1);
        }
        return result;
    }
    private void icon(GuiGraphics g,int index,int x,int y,boolean selected){
        var entry=EnchanterCosts.ENTRIES.get(EnchanterMenu.IDS.get(index));blend();g.blit(ResourceLocation.parse(entry.texture()),x,y,0,0,16,16,16,16);
        var tooltip=new ArrayList<Component>();
        var enchant=minecraft.level==null?null:minecraft.level.registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).getHolder(ResourceLocation.parse(EnchanterMenu.IDS.get(index))).orElse(null);
        tooltip.add(enchant==null?Component.literal(EnchanterMenu.IDS.get(index)):enchant.value().description());
        var cost=entry.cost(Math.max(1,menu.rank(index)));for(var aspect:cost.getAspectsSorted())tooltip.add(Component.literal(aspect.getName()+" x "+cost.getAmount(aspect)).withColor(aspect.getColor()));
        if(selected)tooltip.add(Component.translatable("ttmisc.clickToRemove"));
        hits.add(new Hit(x,y,16,16,1+index*3,tooltip));
    }
    @Override protected void renderBg(GuiGraphics g,float partial,int mx,int my){
        // The GUI sheet and enchantment icons rely on alpha (glass jars, soft icon edges); without blending near-transparent pixels show as solid dots.
        com.mojang.blaze3d.systems.RenderSystem.enableBlend();com.mojang.blaze3d.systems.RenderSystem.defaultBlendFunc();
        hits.clear();g.blit(TEXTURE,leftPos,topPos,0,0,imageWidth,imageHeight);
        var choices=available();boolean twoRows=choices.size()>8;var tool=menu.slots.get(0).getItem();
        if(!tool.isEmpty()&&!tool.isEnchanted())g.blit(TEXTURE,leftPos+30,topPos+50,0,166,147,24);
        if(twoRows)g.blit(TEXTURE,leftPos+30,topPos+26,0,166,147,24);
        for(int n=0;n<choices.size();n++)icon(g,choices.get(n),leftPos+34+n%8*16,topPos+(twoRows&&n<8?30:54),false);
        var total=new AspectList();int row=0;
        for(int i=0;i<40;i++)if(menu.rank(i)>0){
            var cost=EnchanterCosts.ENTRIES.get(EnchanterMenu.IDS.get(i)).cost(menu.rank(i));for(var aspect:cost.getAspects())total.add(aspect,cost.getAmount(aspect));
            int x=leftPos+imageWidth+4,y=topPos+row++*26;g.fill(x-2,y-2,x+20,y+20,0xCC303030);icon(g,i,x,y,true);
            g.drawString(font,Component.translatable("enchantment.level."+menu.rank(i)),x+26,y+8,0xFFFFFF,false);
            for(int action=1;action<=2;action++){int bx=x+20+(action-1)*8;g.drawString(font,action==1?"+":"−",bx,y-5,0xFFFFFF,false);hits.add(new Hit(bx,y-5,8,10,1+i*3+action,List.of()));}
        }
        if(row>0){
            int sx=leftPos+151,sy=topPos+33;g.blit(TEXTURE,sx,sy,176,menu.working()?39:24,15,15);hits.add(new Hit(sx,sy,15,15,0,List.of(Component.translatable("ttmisc.startEnchant"))));
            blend(); // GuiEnchanting enables GL_BLEND for the aspect jars; g.fill above has switched it off again
            var primals=Aspect.getPrimalAspects();for(int i=0;i<6;i++){
                var aspect=primals.get(i);int needed=total.getAmount(aspect),current=menu.progress(i),size=needed==0?11:59,x=leftPos+55+i*15,y=topPos+(twoRows?26:50)-size;
                if(needed>0){int pixels=Math.min(48,(int)(48.0*current/needed));int color=aspect.getColor();com.mojang.blaze3d.systems.RenderSystem.setShaderColor((color>>16&255)/255f,(color>>8&255)/255f,(color&255)/255f,1);g.blit(TEXTURE,x+1,y+4+48-pixels,210,48-pixels,8,pixels);com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1,1,1,1);}
                g.blit(TEXTURE,x,y,200,0,10,size);if(needed==0)g.blit(TEXTURE,x,y+4,200,52,10,10);
                hits.add(new Hit(x,y,10,size,-1,List.of(Component.literal(aspect.getName()),Component.literal(current+"/"+needed))));
            }
        }
    }
    private static void blend(){com.mojang.blaze3d.systems.RenderSystem.enableBlend();com.mojang.blaze3d.systems.RenderSystem.defaultBlendFunc();}
    @Override protected void renderLabels(GuiGraphics g,int x,int y){}
    @Override public boolean mouseClicked(double x,double y,int button){if(button==0&&!menu.working())for(var hit:hits)if(hit.button>=0&&hit.contains(x,y)){if(minecraft.gameMode!=null)minecraft.gameMode.handleInventoryButtonClick(menu.containerId,hit.button);return true;}return super.mouseClicked(x,y,button);}
    @Override public void render(GuiGraphics g,int x,int y,float partial){super.render(g,x,y,partial);renderTooltip(g,x,y);for(var hit:hits)if(hit.contains(x,y)&&!hit.tooltip.isEmpty()){g.renderComponentTooltip(font,hit.tooltip,x,y);break;}}
}
