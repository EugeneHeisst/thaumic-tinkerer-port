package dev.eugheist.ttmodern;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.*;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import dev.eugheist.ttmodern.mixin.ExperienceOrbAccessor;
final class XPTalismanAudit {
    private static void check(boolean ok,String label){if(!ok)throw new IllegalStateException("TT PORT AUDIT FAILED: XP talisman "+label);}
    static void run(ServerLevel level){
        var player=net.neoforged.neoforge.common.util.FakePlayerFactory.getMinecraft(level);player.setPos(8,246,8);player.getInventory().clearContent();
        var item=ThaumicTinkererModern.XP_TALISMAN.get();var stack=new ItemStack(item);
        var orb=new ExperienceOrb(level,8,246,8,7);((ExperienceOrbAccessor)orb).ttinkerer$count(3);level.addFreshEntity(orb);
        item.onWornTick(stack,player);check(XPTalismanItem.xp(stack)==0&&orb.isAlive(),"disabled preserves orb");
        CleansingTalismanItem.toggle(stack);item.onWornTick(stack,player);
        check(XPTalismanItem.xp(stack)==21&&orb.isRemoved(),"merged orb counts conserved");
        XPTalismanItem.xp(stack,1490);var partial=new ExperienceOrb(level,8,246,8,7);((ExperienceOrbAccessor)partial).ttinkerer$count(3);level.addFreshEntity(partial);
        item.onWornTick(stack,player);check(XPTalismanItem.xp(stack)==1497&&partial.isAlive()&&((ExperienceOrbAccessor)partial).ttinkerer$count()==2,"capacity leaves unused merged orbs");partial.discard();
        var distant=new ExperienceOrb(level,12,246,8,3);level.addFreshEntity(distant);item.onWornTick(stack,player);check(distant.isAlive()&&XPTalismanItem.xp(stack)==1497,"three-block range");distant.discard();
        var saved=ItemStack.parseOptional(level.registryAccess(),(net.minecraft.nbt.CompoundTag)stack.save(level.registryAccess()));check(XPTalismanItem.xp(saved)==1497&&XPTalismanItem.enabled(saved),"data persistence");
        check(!XPTalismanItem.bottle(stack,player)&&XPTalismanItem.xp(stack)==1497,"bottle required");
        player.getInventory().setItem(0,new ItemStack(Items.GLASS_BOTTLE,2));check(XPTalismanItem.bottle(stack,player)&&XPTalismanItem.xp(stack)==1487,"ten XP spent");
        int bottles=0,empties=0;for(var s:player.getInventory().items){if(s.is(Items.EXPERIENCE_BOTTLE))bottles+=s.getCount();if(s.is(Items.GLASS_BOTTLE))empties+=s.getCount();}
        check(bottles==1&&empties==1,"one bottle consumed and created");
        XPTalismanItem.xp(stack,1500);item.onWornTick(stack,player);check(!XPTalismanItem.enabled(stack),"full disables absorption");
        player.getInventory().clearContent();
        var recipe=dev.eugheist.tc4modern.InfusionItemRecipes.all().stream().filter(r->r.key().equals("XP_TALISMAN")).findFirst().orElseThrow();
        check(recipe.components().stream().allMatch(s->s.get()!=Items.AIR),"infusion ingredients resolve");
        System.out.println("TT XP TALISMAN AUDIT: ALL PASS (merged orbs, capacity, range, persistence, bottle conservation)");
    }
}
