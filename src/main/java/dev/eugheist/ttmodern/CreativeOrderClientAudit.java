package dev.eugheist.ttmodern;

import dev.eugheist.tc4modern.WandData;
import dev.eugheist.tc4modern.WandParts;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.locale.Language;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.aspects.Aspect;
import java.util.ArrayList;
import java.util.List;

final class CreativeOrderClientAudit {
    static void run(Minecraft mc){
        var previous=Language.getInstance();
        com.google.gson.JsonObject originalNames;
        try(var reader=java.nio.file.Files.newBufferedReader(java.nio.file.Path.of("..","tools","original-item-names.json"))){
            originalNames=com.google.gson.JsonParser.parseReader(reader).getAsJsonObject();
        }catch(java.io.IOException error){throw new IllegalStateException("Original name mapping unavailable",error);}
        try{
            for(var locale:List.of("en_us","ru_ru","de_de","fr_fr","ko_kr","zh_cn")){
                Language.inject(ClientLanguage.loadFrom(mc.getResourceManager(),List.of("en_us",locale),false));
                // Independent file oracle: native language loading must include every locale key,
                // not merely make both compared names fall back to the same English text.
                try(var reader=java.nio.file.Files.newBufferedReader(java.nio.file.Path.of("..","src","main","resources","assets","ttinkerer","lang",locale+".json"))){
                    var expected=com.google.gson.JsonParser.parseReader(reader).getAsJsonObject();
                    var numeric=java.util.regex.Pattern.compile("%(\\d+\\$)?[\\d.]*[df]");
                    for(var entry:expected.entrySet()){
                        String value=numeric.matcher(entry.getValue().getAsString()).replaceAll("%$1s");
                        check(Language.getInstance().getOrDefault(entry.getKey()).equals(value),"native language key "+locale+" "+entry.getKey()+" expected="+value+" actual="+Language.getInstance().getOrDefault(entry.getKey()));
                    }
                }catch(java.io.IOException error){throw new IllegalStateException("Locale oracle unavailable",error);}
                var tab=ThaumicTinkererModern.TABS.getEntries().iterator().next().get();
                tab.buildContents(new CreativeModeTab.ItemDisplayParameters(FeatureFlags.DEFAULT_FLAGS,true,net.minecraft.core.RegistryAccess.EMPTY));
                var stacks=new ArrayList<>(tab.getDisplayItems());
                for(var stack:stacks){
                    if(WandData.isWand(stack))continue;
                    var key=stack.getDescriptionId();
                    check(originalNames.has(key),"original name mapping "+key);
                    String source=originalNames.getAsJsonObject(key).get(locale).getAsString();
                    check(stack.getHoverName().getString().equals(net.minecraft.network.chat.Component.translatable(source).getString()),"actual localized item name "+locale+" "+key);
                }
                int prelude=KamiStartupConfig.enabled()?2:1;
                check(stacks.size()>prelude,"nonempty native creative tab");
                for(int i=0;i<prelude;i++){
                    var wand=stacks.get(i);
                    check(WandParts.cap(wand).tag().equals(i==0?"thaumium":"ICHOR"),"wand cap");
                    check(WandParts.rod(wand).tag().equals(i==0?"silverwood":"ICHORCLOTH"),"wand rod");
                    for(var aspect:Aspect.getPrimalAspects())check(WandData.get(wand,aspect)==(i==0?10000:100000),"charged primal vis");
                }
                check(ItemStack.isSameItemSameComponents(tab.getIconItem(),stacks.get(prelude-1)),"original last wand icon");
                for(int i=prelude+1;i<stacks.size();i++)check(stacks.get(i-1).getHoverName().getString().compareToIgnoreCase(stacks.get(i).getHoverName().getString())<=0,"localized order "+locale);
                var moulds=stacks.stream().filter(s->s.getItem() instanceof SoulMouldItem).map(SoulMouldItem::pattern).toList();
                check(moulds.size()==30&&moulds.contains("")&&new java.util.HashSet<>(moulds).size()==30&&moulds.containsAll(MobAspects.SUMMON_ORDER),"original blank plus 29 species moulds");
                for(var entry:ThaumicTinkererModern.itemEntries().toList()){
                    var item=entry.get();long count=stacks.subList(prelude,stacks.size()).stream().filter(s->s.is(item)).count();
                    check(count==(item instanceof LegacyCropEggItem||item instanceof MobAspectItem aspect&&aspect.form==1?0:item instanceof InfusedSeedsItem?Aspect.getPrimalAspects().size():item instanceof SoulMouldItem?30:1),"exact creative multiplicity "+entry.getId());
                }
            }
        }finally{Language.inject(previous);}
        System.out.println("TT CREATIVE ORDER CLIENT AUDIT: ALL PASS (native tab EN/RU/DE/FR/KO/ZH,charged prelude,icon,all entry multiplicities)");
    }
    private static void check(boolean ok,String message){if(!ok)throw new IllegalStateException("Creative order: "+message);}
}
