package dev.eugheist.ttmodern;
import java.util.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.*;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
@EventBusSubscriber(modid="ttinkerer")
public final class BloodSwordItem extends SwordItem implements thaumcraft.api.IRepairable{
    static final Tier TIER=new net.neoforged.neoforge.common.SimpleTier(net.minecraft.tags.BlockTags.INCORRECT_FOR_WOODEN_TOOL,950,0,0,22,()->net.minecraft.world.item.crafting.Ingredient.EMPTY);
    static final Set<UUID> SELF_DAMAGE=new HashSet<>();
    public BloodSwordItem(){super(TIER,new Properties().attributes(ItemAttributeModifiers.builder().add(Attributes.ATTACK_DAMAGE,new AttributeModifier(BASE_ATTACK_DAMAGE_ID,10,AttributeModifier.Operation.ADD_VALUE),EquipmentSlotGroup.MAINHAND).add(Attributes.ATTACK_SPEED,new AttributeModifier(BASE_ATTACK_SPEED_ID,-2.4,AttributeModifier.Operation.ADD_VALUE),EquipmentSlotGroup.MAINHAND).add(Attributes.MOVEMENT_SPEED,new AttributeModifier(ResourceLocation.fromNamespaceAndPath("ttinkerer","blood_sword_speed"),.25,AttributeModifier.Operation.ADD_MULTIPLIED_BASE),EquipmentSlotGroup.MAINHAND).build()));}
    public static boolean active(ItemStack stack){return stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag().getBoolean("Activated");}
    public static void toggle(ItemStack stack){CustomData.update(DataComponents.CUSTOM_DATA,stack,tag->tag.putBoolean("Activated",!tag.getBoolean("Activated")));}
    @Override public int getUseDuration(ItemStack stack,LivingEntity user){return 72000;}
    @Override public UseAnim getUseAnimation(ItemStack stack){return UseAnim.BLOCK;}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){var stack=player.getItemInHand(hand);player.startUsingItem(hand);if(player.isShiftKeyDown()&&!level.isClientSide){toggle(stack);player.displayClientMessage(net.minecraft.network.chat.Component.translatable(active(stack)?"ttmisc.bloodSword.activateEssentiaHarvest":"ttmisc.bloodSword.deactivateEssentiaHarvest"),false);}return InteractionResultHolder.consume(stack);}
    private static void pay(LivingEntity entity,float amount){if(!SELF_DAMAGE.add(entity.getUUID()))return;try{entity.hurt(entity.damageSources().fellOutOfWorld(),amount);}finally{SELF_DAMAGE.remove(entity.getUUID());}}
    @SubscribeEvent public static void incoming(net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent event){
        var victim=event.getEntity();if(victim.level().isClientSide||SELF_DAMAGE.contains(victim.getUUID()))return;
        if(victim instanceof Player&&victim.isUsingItem()&&victim.getUseItem().getItem() instanceof BloodSwordItem){event.setCanceled(true);pay(victim,3);}
        if(event.getSource().getDirectEntity() instanceof LivingEntity attacker&&attacker.getMainHandItem().getItem() instanceof BloodSwordItem&&!SELF_DAMAGE.contains(attacker.getUUID()))pay(attacker,2);
    }
    @SubscribeEvent(priority=net.neoforged.bus.api.EventPriority.LOWEST) public static void drops(net.neoforged.neoforge.event.entity.living.LivingDropsEvent event){
        if(event.getEntity().level().isClientSide||!event.getSource().is(net.minecraft.world.damagesource.DamageTypes.PLAYER_ATTACK)||!(event.getSource().getEntity() instanceof Player player))return;
        var held=player.getMainHandItem();if(!(held.getItem() instanceof BloodSwordItem)||!active(held))return;
        var aspects=MobAspects.MOBS.get(net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(event.getEntity().getType()).toString());if(aspects==null)return;
        event.getDrops().clear();for(var aspect:aspects){var drop=new net.minecraft.world.entity.item.ItemEntity(event.getEntity().level(),event.getEntity().getX(),event.getEntity().getY(),event.getEntity().getZ(),MobAspects.stack(aspect,0));drop.setPickUpDelay(10);event.getDrops().add(drop);}
    }
}
