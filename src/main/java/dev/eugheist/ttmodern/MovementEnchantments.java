package dev.eugheist.ttmodern;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
@EventBusSubscriber(modid="ttinkerer")
public final class MovementEnchantments {
    static final ResourceKey<Enchantment> ASCENT=ResourceKey.create(Registries.ENCHANTMENT,ResourceLocation.fromNamespaceAndPath("ttinkerer","ascent_boost"));
    static final ResourceKey<Enchantment> SLOW_FALL=ResourceKey.create(Registries.ENCHANTMENT,ResourceLocation.fromNamespaceAndPath("ttinkerer","slow_fall"));
    static int armorRank(Player player,ResourceKey<Enchantment> key){
        var holder=player.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolder(key).orElse(null);if(holder==null)return 0;
        int result=0;for(var stack:player.getArmorSlots())result=Math.max(result,stack.getEnchantmentLevel(holder));return result;
    }
    @SubscribeEvent public static void jump(net.neoforged.neoforge.event.entity.living.LivingEvent.LivingJumpEvent event){
        if(!(event.getEntity() instanceof Player player)||player.isShiftKeyDown())return;int rank=armorRank(player,ASCENT);
        if(rank>0){var velocity=player.getDeltaMovement();player.setDeltaMovement(velocity.x,velocity.y*(rank+2)/2.0,velocity.z);}
    }
    @SubscribeEvent(priority=net.neoforged.bus.api.EventPriority.HIGHEST) public static void tick(net.neoforged.neoforge.event.tick.EntityTickEvent.Pre event){
        if(!(event.getEntity() instanceof Player player)||player.isShiftKeyDown())return;int rank=armorRank(player,SLOW_FALL);var velocity=player.getDeltaMovement();
        if(rank>0&&velocity.y<-.0784000015258789&&player.fallDistance>=2.9){
            double vy=velocity.y/(1.0f+rank*.33f);player.setDeltaMovement(velocity.x,vy,velocity.z);player.fallDistance=Math.max(2.9f,player.fallDistance-rank/3f);
            if(player.level().isClientSide)player.level().addParticle(net.minecraft.core.particles.ParticleTypes.CLOUD,player.getX()+.25,player.getY()-1,player.getZ()+.25,-velocity.x,vy,-velocity.z);
        }
    }
    private MovementEnchantments(){}
}
