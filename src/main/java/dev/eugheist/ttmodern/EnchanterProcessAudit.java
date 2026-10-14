package dev.eugheist.ttmodern;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import dev.eugheist.tc4modern.TC4Modern;
import dev.eugheist.tc4modern.WandData;
import thaumcraft.api.aspects.Aspect;
final class EnchanterProcessAudit {
    private static void check(boolean ok,String label){if(!ok)throw new IllegalStateException("Enchanter process: "+label);}
    static void run(ServerLevel level){
        var process=new EnchanterProcess();var tool=new ItemStack(Items.DIAMOND_SWORD);var wand=new ItemStack(TC4Modern.GOLD_GREATWOOD_WAND.get());
        check(!process.start(),"empty selection cannot start");
        check(process.change(level,tool,Set.of(),"minecraft:sharpness",1)&&process.start(),"selected start");
        check(!process.change(level,tool,Set.of(),"minecraft:sharpness",5),"working selection locked");
        check(!process.tick(level,tool,wand,true)&&process.collected(Aspect.ORDER)==0,"empty wand no free progress");
        WandData.add(wand,Aspect.ORDER,2000,true);int before=WandData.get(wand,Aspect.ORDER);
        process.tick(level,tool,wand,true);check(process.collected(Aspect.ORDER)==1&&before-WandData.get(wand,Aspect.ORDER)==(int)(100*WandData.craftingModifier(wand,null,Aspect.ORDER)),"one whole vis per tick progress="+process.collected(Aspect.ORDER)+" spent="+(before-WandData.get(wand,Aspect.ORDER))+" modifier="+WandData.craftingModifier(wand,null,Aspect.ORDER));
        var restored=new EnchanterProcess();restored.load(level,tool,process.save());check(restored.working()&&restored.collected(Aspect.ORDER)==1,"persisted progress and selection");
        for(int i=0;i<11;i++)restored.tick(level,tool,wand,true);
        check(!tool.isEnchanted()&&restored.collected(Aspect.ORDER)==12,"apply on tick after final collection");
        restored.tick(level,tool,wand,true);check(tool.isEnchanted()&&!restored.working()&&restored.selection.entries().isEmpty(),"completion applies and clears");
        before=WandData.get(wand,Aspect.ORDER);restored.tick(level,tool,wand,true);check(before==WandData.get(wand,Aspect.ORDER),"completed process cannot charge twice");
        tool=new ItemStack(Items.DIAMOND_SWORD);process=new EnchanterProcess();process.change(level,tool,Set.of(),"minecraft:sharpness",1);process.start();process.tick(level,tool,wand,true);
        check(process.collected(Aspect.ORDER)==1,"restart fixture");process.tick(level,tool,wand,false);check(!process.working()&&process.collected(Aspect.ORDER)==0&&!tool.isEnchanted(),"broken structure stops and discards paid progress");
        check(process.start(),"selection retained after broken structure");process.tick(level,ItemStack.EMPTY,wand,true);check(!process.working()&&process.selection.entries().isEmpty(),"missing tool stops process");
        System.out.println("TT ENCHANTER PROCESS AUDIT: ALL PASS (vis conservation, persistence, completion once, locked selection, broken structure)");
    }
}
