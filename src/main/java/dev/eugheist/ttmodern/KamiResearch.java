package dev.eugheist.ttmodern;
import thaumcraft.api.research.*;
import thaumcraft.api.aspects.AspectList;
import net.minecraft.world.item.ItemStack;
/** Original KamiResearchItem mastery snapshot, excluding special/clue research. */
final class KamiResearch extends ResearchItem {
 KamiResearch(String key,AspectList aspects,int x,int y,int complexity,ItemStack icon){super(key,"THAUMICTINKERER",aspects,x,y,complexity,icon);setConcealed();}
 static boolean required(ResearchItem r){return !r.isLost()&&!(r.parentsHidden==null&&r.parents==null)&&!r.isVirtual()&&!(r instanceof KamiResearch)&&r.getAspectTriggers()==null&&r.getEntityTriggers()==null&&r.getItemTriggers()==null&&java.util.Set.of("THAUMICTINKERER","BASICS","GOLEMANCY","ARTIFICE","ALCHEMY","THAUMATURGY").contains(r.category)&&!r.key.startsWith("MINILITH")&&!r.key.endsWith("KAMI");}
 @Override public ResearchItem setPages(ResearchPage... pages){var requirements=new java.util.LinkedHashSet<String>();if(parentsHidden!=null)java.util.Collections.addAll(requirements,parentsHidden);if(!isAutoUnlock())for(var category:ResearchCategories.researchCategories.values())for(var r:category.research.values())if(required(r))requirements.add(r.key);parentsHidden=requirements.toArray(String[]::new);return super.setPages(pages);}
 @Override public String getName(){return net.minecraft.network.chat.Component.translatable("ttresearch.name."+key).getString();}
 @Override public String getText(){return net.minecraft.network.chat.Component.translatable("ttresearch.lore."+key).getString();}
}
