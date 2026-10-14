package dev.eugheist.ttmodern;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.api.aspects.*;
import thaumcraft.api.research.*;
final class MovementEnchantmentResearch {
    static void register(){critical("TTENCH_POUNCE","pounce",7,0,new AspectList().add(Aspect.AIR,3).add(Aspect.ORDER,3).add(Aspect.ARMOR,3).add(Aspect.MAGIC,2),"TTENCH_ASCENT_BOOST");critical("TTENCH_SHOCKWAVE","shockwave",9,2,new AspectList().add(Aspect.AIR,3).add(Aspect.ENTROPY,3).add(Aspect.ARMOR,1).add(Aspect.MAGIC,2),"TTENCH_SLOW_FALL");critical("TTENCH_SHATTER","shatter",5,8,new AspectList().add(Aspect.EARTH,3).add(Aspect.ENTROPY,3).add(Aspect.TOOL,1).add(Aspect.MAGIC,2),"TTENCH_DESINTEGRATE");critical("TTENCH_TUNNEL","tunnel",9,6,new AspectList().add(Aspect.EARTH,3).add(Aspect.ORDER,3).add(Aspect.TOOL,1).add(Aspect.MAGIC,2),"TTENCH_AUTO_SMELT");critical("TTENCH_VALIANCE","valiance",1,4,new AspectList().add(Aspect.WEAPON,3).add(Aspect.HEAL,3).add(Aspect.MAGIC,2),"TTENCH_VAMPIRISM");critical("TTENCH_FINAL","finalstrike",0,8,new AspectList().add(Aspect.ENTROPY,3).add(Aspect.ORDER,3).add(Aspect.WEAPON,3).add(Aspect.MAGIC,2),"TTENCH_FOCUSED","TTENCH_DISPERSED");strike("TTENCH_FOCUSED","focusedstrikes",2,7,Aspect.ORDER);strike("TTENCH_DISPERSED","dispersedstrikes",1,6,Aspect.ENTROPY);tool("TTENCH_VAMPIRISM","vamprisim",3,5,Aspect.HUNGER,Aspect.WEAPON);tool("TTENCH_QUICK_DRAW","quickdraw",4,6,Aspect.SENSES,Aspect.WEAPON);entry("TTENCH_ASCENT_BOOST","ascentboost",6,2);entry("TTENCH_SLOW_FALL","slowfall",7,3);tool("TTENCH_AUTO_SMELT","autosmelt",7,5,Aspect.FIRE,Aspect.ENTROPY);tool("TTENCH_DESINTEGRATE","desintegrate",6,6,Aspect.ENTROPY,Aspect.VOID);}
    private static void critical(String key,String icon,int x,int y,AspectList aspects,String... parents){
        new ResearchItem(key,"THAUMICTINKERER",aspects,x,y,2,ResourceLocation.fromNamespaceAndPath("ttinkerer","textures/enchants/"+icon+".png")){
            @Override public String getName(){return Component.translatable("ttresearch.name."+key).getString();}
            @Override public String getText(){return Component.translatable("ttresearch.lore."+key).getString();}
        }.setParents(parents).setSecondary().setPages(new ResearchPage("ttresearch.page."+key+".0")).registerResearchItem();
    }
    private static void strike(String key,String icon,int x,int y,Aspect first){
        new ResearchItem(key,"THAUMICTINKERER",new AspectList().add(first,1).add(Aspect.WEAPON,1).add(Aspect.MAGIC,2),x,y,2,ResourceLocation.fromNamespaceAndPath("ttinkerer","textures/enchants/"+icon+".png")){
            @Override public String getName(){return Component.translatable("ttresearch.name."+key).getString();}
            @Override public String getText(){return Component.translatable("ttresearch.lore."+key).getString();}
        }.setParents("TTENCH_VAMPIRISM").setSecondary().setPages(new ResearchPage("ttresearch.page."+key+".0")).registerResearchItem();
    }
    private static void tool(String key,String icon,int x,int y,Aspect first,Aspect second){
        new ResearchItem(key,"THAUMICTINKERER",new AspectList().add(first,1).add(second,1).add(Aspect.MAGIC,2),x,y,2,ResourceLocation.fromNamespaceAndPath("ttinkerer","textures/enchants/"+icon+".png")){
            @Override public String getName(){return Component.translatable("ttresearch.name."+key).getString();}
            @Override public String getText(){return Component.translatable("ttresearch.lore."+key).getString();}
        }.setParents("ENCHANTER").setSecondary().setPages(new ResearchPage("ttresearch.page."+key+".0")).registerResearchItem();
    }
    private static void entry(String key,String icon,int x,int y){
        new ResearchItem(key,"THAUMICTINKERER",new AspectList().add(Aspect.AIR,1).add(Aspect.MOTION,1).add(Aspect.MAGIC,2),x,y,2,ResourceLocation.fromNamespaceAndPath("ttinkerer","textures/enchants/"+icon+".png")){
            @Override public String getName(){return Component.translatable("ttresearch.name."+key).getString();}
            @Override public String getText(){return Component.translatable("ttresearch.lore."+key).getString();}
        }.setParents("ENCHANTER").setSecondary().setPages(new ResearchPage("ttresearch.page."+key+".0")).registerResearchItem();
    }
}
