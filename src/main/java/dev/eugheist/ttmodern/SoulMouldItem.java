package dev.eugheist.ttmodern;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;
import java.util.List;
public final class SoulMouldItem extends Item {
    public SoulMouldItem(){super(new Properties().stacksTo(1));}
    public static String pattern(ItemStack stack){return stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag().getString("pattern");}
    @Override public InteractionResult interactLivingEntity(ItemStack stack,Player player,LivingEntity target,InteractionHand hand){
        if(!(target instanceof Player)&&!player.level().isClientSide)CustomData.update(DataComponents.CUSTOM_DATA,stack,tag->tag.putString("pattern",BuiltInRegistries.ENTITY_TYPE.getKey(target.getType()).toString()));
        return InteractionResult.sidedSuccess(player.level().isClientSide);
    }
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){var stack=player.getItemInHand(hand);if(player.isShiftKeyDown()&&!level.isClientSide)CustomData.update(DataComponents.CUSTOM_DATA,stack,tag->tag.remove("pattern"));return InteractionResultHolder.sidedSuccess(stack,level.isClientSide);}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){
        var name=pattern(stack);var id=net.minecraft.resources.ResourceLocation.tryParse(name);
        var type=id==null?null:BuiltInRegistries.ENTITY_TYPE.getOptional(id).orElse(null);
        lines.add(type==null?Component.translatable("ttmisc.soulMould.nonAssigned"):Component.translatable("ttmisc.soulMould.pattern",type.getDescription()));
    }
}
