package dev.eugheist.ttmodern;
import net.minecraft.world.item.*;
import net.minecraft.world.InteractionResult;
/** Build164 pearl: bind once to a gate, sneak-use in air to clear only its y sentinel. */
public final class SkyPearl extends Item {
 static final net.neoforged.neoforge.registries.DeferredItem<SkyPearl> ITEM=KamiRegistries.ITEMS.register("sky_pearl",SkyPearl::new);
 SkyPearl(){super(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));}
 static void init(){}
 @Override public boolean isFoil(ItemStack stack){return SkyPearlData.attuned(stack);}
 @Override public InteractionResult useOn(net.minecraft.world.item.context.UseOnContext context){
  var level=context.getLevel();var stack=context.getItemInHand();
  if(!SkyPearlData.attuned(stack)&&net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(level.getBlockState(context.getClickedPos()).getBlock()).equals(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("ttinkerer","warp_gate"))){
   SkyPearlData.bind(stack,context.getClickedPos(),level.dimension().location());sound(level,context.getPlayer());
  }
  return InteractionResult.sidedSuccess(level.isClientSide);
 }
 @Override public net.minecraft.world.InteractionResultHolder<ItemStack> use(net.minecraft.world.level.Level level,net.minecraft.world.entity.player.Player player,net.minecraft.world.InteractionHand hand){var stack=player.getItemInHand(hand);if(player.isShiftKeyDown()&&SkyPearlData.attuned(stack)){sound(level,player);SkyPearlData.reset(stack);}return net.minecraft.world.InteractionResultHolder.success(stack);}
 private static void sound(net.minecraft.world.level.Level level,net.minecraft.world.entity.player.Player player){if(player!=null)level.playSound(player,player.blockPosition(),net.minecraft.sounds.SoundEvents.EXPERIENCE_ORB_PICKUP,net.minecraft.sounds.SoundSource.PLAYERS,.3f,.1f);}
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,java.util.List<net.minecraft.network.chat.Component> lines,TooltipFlag flag){super.appendHoverText(stack,context,lines,flag);if(!SkyPearlData.attuned(stack))return;var p=SkyPearlData.position(stack);lines.add(net.minecraft.network.chat.Component.literal("X: "+p.getX()));lines.add(net.minecraft.network.chat.Component.literal("Y: "+p.getY()));lines.add(net.minecraft.network.chat.Component.literal("Z: "+p.getZ()));if(net.neoforged.fml.loading.FMLEnvironment.dist.isClient())Client.info(stack,lines);}
 private static final class Client {static void info(ItemStack stack,java.util.List<net.minecraft.network.chat.Component> lines){var player=net.minecraft.client.Minecraft.getInstance().player;if(player==null)return;if(!SkyPearlData.dimension(stack).equals(player.level().dimension().location()))lines.add(net.minecraft.network.chat.Component.translatable("ttmisc.differentDim").withStyle(net.minecraft.ChatFormatting.RED));else lines.add(net.minecraft.network.chat.Component.translatable("ttmisc.distance").append(": "+SkyPearlData.distance(stack,player.position().add(0,player.getBbHeight()/2d,0),false)+"m").withStyle(net.minecraft.ChatFormatting.BLUE));}}
 static void recipe(){ThaumicTinkererModern.INFUSION_RECIPES.add(new dev.eugheist.tc4modern.InfusionItemRecipes.Recipe("SKY_PEARL","WARP_GATE",ITEM::get,6,new thaumcraft.api.aspects.AspectList().add(thaumcraft.api.aspects.Aspect.TRAVEL,32).add(thaumcraft.api.aspects.Aspect.ELDRITCH,32).add(thaumcraft.api.aspects.Aspect.FLIGHT,32).add(thaumcraft.api.aspects.Aspect.AIR,16),()->Items.ENDER_PEARL,java.util.List.of(()->KamiResources.item(KamiResources.Kind.ICHOR),()->KamiResources.item(KamiResources.Kind.ENDER_SHARD),()->Items.LAPIS_BLOCK,()->Items.DIAMOND)));}
}
