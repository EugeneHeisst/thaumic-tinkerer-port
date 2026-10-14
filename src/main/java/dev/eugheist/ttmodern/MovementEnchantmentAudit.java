package dev.eugheist.ttmodern;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
final class MovementEnchantmentAudit {
    private static void check(boolean ok,String label){if(!ok)throw new IllegalStateException("TT movement enchants: "+label);}
    static void run(ServerLevel level){
        var registry=level.registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT);var ascent=registry.getHolderOrThrow(MovementEnchantments.ASCENT);var slow=registry.getHolderOrThrow(MovementEnchantments.SLOW_FALL);
        check(ascent.value().getMaxLevel()==4&&slow.value().getMaxLevel()==3,"original level bounds");
        check(!net.minecraft.world.item.enchantment.Enchantment.areCompatible(slow,registry.getHolderOrThrow(net.minecraft.world.item.enchantment.Enchantments.FEATHER_FALLING)),"slow fall excludes feather falling");
        check(!ascent.is(net.minecraft.tags.EnchantmentTags.IN_ENCHANTING_TABLE)&&!slow.is(net.minecraft.tags.EnchantmentTags.IN_ENCHANTING_TABLE),"not ordinary table enchantments");
        var player=net.neoforged.neoforge.common.util.FakePlayerFactory.getMinecraft(level);player.getInventory().clearContent();player.setShiftKeyDown(false);
        for(int rank=1;rank<=4;rank++){
            var legs=new ItemStack(Items.DIAMOND_LEGGINGS);legs.enchant(ascent,rank);player.setItemSlot(EquipmentSlot.LEGS,legs);player.setDeltaMovement(.1,.42,.2);
            net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.entity.living.LivingEvent.LivingJumpEvent(player));
            check(Math.abs(player.getDeltaMovement().y-.42*(rank+2)/2.0)<1e-8&&player.getDeltaMovement().x==.1,"jump multiplier rank"+rank);
        }
        player.setShiftKeyDown(true);player.setDeltaMovement(0,.42,0);net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.entity.living.LivingEvent.LivingJumpEvent(player));check(player.getDeltaMovement().y==.42,"crouch suppresses ascent");player.setShiftKeyDown(false);
        for(int rank=1;rank<=3;rank++){
            var boots=new ItemStack(Items.DIAMOND_BOOTS);boots.enchant(slow,rank);player.setItemSlot(EquipmentSlot.FEET,boots);player.setDeltaMovement(.1,-.6,.2);player.fallDistance=4;
            net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.tick.EntityTickEvent.Pre(player));
            check(Math.abs(player.getDeltaMovement().y-(-.6/(1f+rank*.33f)))<1e-8&&Math.abs(player.fallDistance-Math.max(2.9f,4-rank/3f))<1e-6,"slow fall exact formula rank"+rank);
        }
        player.setDeltaMovement(0,-.6,0);player.fallDistance=2;MovementEnchantments.tick(new net.neoforged.neoforge.event.tick.EntityTickEvent.Pre(player));check(player.getDeltaMovement().y==-.6,"short fall unaffected");
        player.fallDistance=4;player.setShiftKeyDown(true);MovementEnchantments.tick(new net.neoforged.neoforge.event.tick.EntityTickEvent.Pre(player));check(player.getDeltaMovement().y==-.6,"crouch suppresses slow fall");player.setShiftKeyDown(false);
        var selection=new EnchanterSelection();var legs=new ItemStack(Items.DIAMOND_LEGGINGS);
        check(!selection.change(level.registryAccess(),legs,java.util.Set.of(),"ttinkerer:ascent_boost",1),"research gate");
        check(selection.change(level.registryAccess(),legs,java.util.Set.of("TTENCH_ASCENT_BOOST"),"ttinkerer:ascent_boost",1),"researched leggings accepted");
        check(!new EnchanterSelection().change(level.registryAccess(),new ItemStack(Items.DIAMOND_BOOTS),java.util.Set.of("TTENCH_ASCENT_BOOST"),"ttinkerer:ascent_boost",1),"wrong armor slot rejected");
        player.getInventory().clearContent();player.fallDistance=0;player.setDeltaMovement(0,0,0);
        System.out.println("TT MOVEMENT ENCHANT AUDIT: ALL PASS (original ranks, native events, crouch, fall threshold, compatibility, research/item gates)");
    }
}
