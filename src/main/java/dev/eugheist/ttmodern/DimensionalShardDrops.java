package dev.eugheist.ttmodern;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
@net.neoforged.fml.common.EventBusSubscriber(modid=ThaumicTinkererModern.ID)
public final class DimensionalShardDrops {
 static net.minecraft.world.item.ItemStack drop(LivingEntity victim,net.minecraft.world.damagesource.DamageSource source,double roll){
  if(!KamiStartupConfig.enabled())return net.minecraft.world.item.ItemStack.EMPTY;if(victim.level().isClientSide||!(source.getEntity() instanceof Player))return net.minecraft.world.item.ItemStack.EMPTY;
  if(victim instanceof net.minecraft.world.entity.monster.EnderMan&&victim.level().dimension().equals(Level.END)&&roll<=.03125)return new net.minecraft.world.item.ItemStack(KamiResources.item(KamiResources.Kind.ENDER_SHARD));
  if(victim instanceof net.minecraft.world.entity.monster.ZombifiedPiglin&&victim.level().dimension().equals(Level.NETHER)&&roll<=.0625)return new net.minecraft.world.item.ItemStack(KamiResources.item(KamiResources.Kind.NETHER_SHARD));
  return net.minecraft.world.item.ItemStack.EMPTY;
 }
 @net.neoforged.bus.api.SubscribeEvent public static void drops(net.neoforged.neoforge.event.entity.living.LivingDropsEvent event){append(event,Math.random());}
 static void append(net.neoforged.neoforge.event.entity.living.LivingDropsEvent event,double roll){var stack=drop(event.getEntity(),event.getSource(),roll);if(!stack.isEmpty()){var entity=event.getEntity();event.getDrops().add(new net.minecraft.world.entity.item.ItemEntity(entity.level(),entity.getX(),entity.getY(),entity.getZ(),stack));}}
}
