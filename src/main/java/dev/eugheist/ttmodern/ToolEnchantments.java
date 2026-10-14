package dev.eugheist.ttmodern;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.*;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
@EventBusSubscriber(modid="ttinkerer")
public final class ToolEnchantments {
    static final ResourceKey<Enchantment> FLAME=key("auto_smelt"),SOFT=key("desintegrate");
    static final TagKey<net.minecraft.world.level.block.Block> WOOD=TagKey.create(Registries.BLOCK,id("wood_material"));
    private static ResourceLocation id(String name){return ResourceLocation.fromNamespaceAndPath("ttinkerer",name);}
    private static ResourceKey<Enchantment> key(String name){return ResourceKey.create(Registries.ENCHANTMENT,id(name));}
    @SubscribeEvent(priority=net.neoforged.bus.api.EventPriority.HIGHEST) public static void speed(net.neoforged.neoforge.event.entity.player.PlayerEvent.BreakSpeed event){
        HarvestSpeedEnchantments.speed(event);
        var player=event.getEntity();var stack=player.getMainHandItem();var registry=player.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
        var flame=registry.getHolder(FLAME).orElse(null);var soft=registry.getHolder(SOFT).orElse(null);
        boolean flaming=flame!=null&&stack.getEnchantmentLevel(flame)>0,disintegrating=soft!=null&&stack.getEnchantmentLevel(soft)>0;if(!flaming&&!disintegrating)return;
        var pos=event.getPosition().orElse(player.blockPosition());float hardness=event.getState().getDestroySpeed(player.level(),pos);
        if(hardness>=0&&(flaming&&event.getState().is(WOOD)||disintegrating&&hardness<=1.5f)){
            if(!player.level().isClientSide)stack.hurtAndBreak(1,player,net.minecraft.world.entity.EquipmentSlot.MAINHAND);
            event.setNewSpeed(Float.MAX_VALUE);
        }else event.setCanceled(true);
    }
    private ToolEnchantments(){}
}
