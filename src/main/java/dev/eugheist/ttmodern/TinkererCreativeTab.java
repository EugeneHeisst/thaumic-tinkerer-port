package dev.eugheist.ttmodern;

import dev.eugheist.tc4modern.WandData;
import dev.eugheist.tc4modern.WandParts;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.aspects.Aspect;
import java.util.ArrayList;
import java.util.List;

/** build164 ModCreativeTab: charged wand prelude followed by localized name order. */
final class TinkererCreativeTab {
    private static ItemStack wand(boolean kami) {
        var result=WandParts.make(WandParts.CAPS.get(kami?"ICHOR":"thaumium"),
                WandParts.RODDEFS.get(kami?"ICHORCLOTH":"silverwood"));
        for(var aspect:Aspect.getPrimalAspects())WandData.add(result,aspect,kami?100000:10000,true);
        return result;
    }
    static ItemStack icon(){return wand(KamiStartupConfig.enabled());}
    static List<ItemStack> contents(){
        var result=new ArrayList<ItemStack>();
        result.add(wand(false));
        if(KamiStartupConfig.enabled())result.add(wand(true));
        var queue=new ArrayList<ItemStack>();
        ThaumicTinkererModern.itemEntries().forEach(entry->{
            var item=entry.get();
            if(item instanceof InfusedSeedsItem){
                for(var aspect:Aspect.getPrimalAspects())queue.add(InfusedSeedsItem.stack(aspect));
            }else if(item instanceof SoulMouldItem){
                queue.add(new ItemStack(item));
                for(var type:MobAspects.SUMMON_ORDER){
                    var mould=new ItemStack(item);
                    net.minecraft.world.item.component.CustomData.update(net.minecraft.core.component.DataComponents.CUSTOM_DATA,mould,t->t.putString("pattern",type));
                    queue.add(mould);
                }
            }else if(item instanceof InfusedInkwellItem){/* ItemInfusedInkwell.shouldDisplayInTab false */}else if(!(item instanceof LegacyCropEggItem)&&!(item instanceof MobAspectItem aspect&&aspect.form==1))queue.add(new ItemStack(item));
        });
        queue.sort((a,b)->a.getHoverName().getString().compareToIgnoreCase(b.getHoverName().getString()));
        result.addAll(queue);
        return result;
    }
}
