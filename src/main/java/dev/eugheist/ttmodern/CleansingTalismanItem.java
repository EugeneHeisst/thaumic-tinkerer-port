package dev.eugheist.ttmodern;
import baubles.api.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.effect.MobEffectCategory;
import java.util.List;
import net.minecraft.network.chat.Component;
public final class CleansingTalismanItem extends Item implements IBauble {
    public CleansingTalismanItem(){super(new Properties().durability(100).rarity(Rarity.UNCOMMON));}
    public static boolean enabled(ItemStack stack){return stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag().getBoolean("enabled");}
    static void toggle(ItemStack stack){CustomData.update(DataComponents.CUSTOM_DATA,stack,t->t.putBoolean("enabled",!t.getBoolean("enabled")));}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){
        var stack=player.getItemInHand(hand);if(player.isShiftKeyDown()&&!level.isClientSide)toggle(stack);
        return InteractionResultHolder.sidedSuccess(stack,level.isClientSide);
    }
    @Override public BaubleType getBaubleType(ItemStack stack){return BaubleType.AMULET;}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){lines.add(Component.translatable(enabled(stack)?"ttmisc.active":"ttmisc.inactive"));}
    @Override public void onWornTick(ItemStack stack,LivingEntity entity){
        if(entity.level().isClientSide || !(entity instanceof Player player) || entity.tickCount%20!=0 || !enabled(stack))return;
        boolean removed=false;int cost=1;
        if(entity.isOnFire()){entity.clearFire();removed=true;}
        else for(var effect:List.copyOf(entity.getActiveEffects())){
            if(effect.getEffect().value().getCategory()!=MobEffectCategory.HARMFUL)continue;
            var key=effect.getEffect().unwrapKey().orElse(null);
            if(key!=null && key.location().toString().equals("tc4modern:warp_ward"))continue;
            entity.removeEffect(effect.getEffect());removed=true;
            if(key!=null && key.location().getNamespace().equals("tc4modern") && List.of("blurred_vision","death_gaze","infectious_vis_exhaust","sun_scorned","unnatural_hunger").contains(key.location().getPath()))cost=10;
            break;
        }
        if(removed&&!player.getAbilities().instabuild){int damage=stack.getDamageValue()+cost;if(damage>=stack.getMaxDamage())stack.shrink(1);else stack.setDamageValue(damage);}
    }
}
