package dev.eugheist.ttmodern;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import thaumcraft.api.aspects.Aspect;
final class EnchanterSelectionAudit {
    private static void check(boolean ok,String label){if(!ok)throw new IllegalStateException("Enchanter selection: "+label);}
    static void run(ServerLevel level){
        var selection=new EnchanterSelection();var sword=new ItemStack(Items.DIAMOND_SWORD);var access=level.registryAccess();
        check(selection.change(access,sword,Set.of(),"minecraft:sharpness",5)&&selection.entries().get("minecraft:sharpness")==1,"new selection starts at one");
        check(!selection.change(access,sword,Set.of(),"minecraft:smite",1),"incompatible enchant rejected");
        check(selection.change(access,sword,Set.of(),"minecraft:sharpness",999)&&selection.entries().get("minecraft:sharpness")==5,"level clamped to registry maximum");
        check(selection.total().getAmount(Aspect.ORDER)==100,"level five exact cost");
        check(!selection.change(access,sword,Set.of(),"minecraft:respiration",1),"wrong item rejected");
        check(!selection.change(access,sword,Set.of(),"unknown:missing",1),"unknown enchant rejected");
        check(!selection.change(access,sword,Set.of(),"ttinkerer:vampirism",1),"locked or absent enchant rejected");
        check(!selection.change(access,sword,Set.of(),"minecraft:missing",-1),"absent removal safe");
        check(selection.change(access,sword,Set.of(),"minecraft:sharpness",-1)&&selection.entries().isEmpty(),"selection removed");
        sword.enchant(access.registryOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).getHolderOrThrow(net.minecraft.world.item.enchantment.Enchantments.UNBREAKING),1);
        check(!selection.change(access,sword,Set.of(),"minecraft:sharpness",1),"already enchanted tool refused like original slot");
        System.out.println("TT ENCHANTER SELECTION AUDIT: ALL PASS (registry levels, compatibility, item eligibility, missing and locked IDs)");
    }
}
