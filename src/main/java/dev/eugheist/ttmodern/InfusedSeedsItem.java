package dev.eugheist.ttmodern;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.FarmBlock;
import thaumcraft.api.aspects.*;
public final class InfusedSeedsItem extends Item {
 public InfusedSeedsItem(){super(new Properties().rarity(Rarity.RARE));}
 static ItemStack stack(Aspect aspect){var stack=new ItemStack(ThaumicTinkererModern.INFUSED_SEEDS.get());new InfusedSeedData(aspect,new AspectList()).write(stack);return stack;}
 @Override public boolean isFoil(ItemStack stack){return true;}
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,java.util.List<Component> lines,TooltipFlag flag){
  super.appendHoverText(stack,context,lines,flag);var data=InfusedSeedData.read(stack);if(data.mainAspect()!=null)lines.add(Component.literal(data.mainAspect().getName()));
  var tendencies=data.tendencies();for(var aspect:tendencies.getAspects())lines.add(Component.literal(aspect.getName()+": "+tendencies.getAmount(aspect)));
 }
 @Override public InteractionResult useOn(UseOnContext context){
  var level=context.getLevel();var pos=context.getClickedPos();var above=pos.above();var player=context.getPlayer();var stack=context.getItemInHand();
  if(context.getClickedFace()!=Direction.UP||player==null||!(level.getBlockState(pos).getBlock() instanceof FarmBlock)||!level.isEmptyBlock(above)||!level.mayInteract(player,pos)||!level.mayInteract(player,above)||!player.mayUseItemAt(pos,Direction.UP,stack)||!player.mayUseItemAt(above,Direction.UP,stack))return InteractionResult.FAIL;
  if(level.isClientSide)return InteractionResult.SUCCESS;
  var data=InfusedSeedData.read(stack);var previous=level.getBlockState(pos);
  if(!(previous.getBlock() instanceof InfusedFarmlandBlock)&&!level.setBlockAndUpdate(pos,ThaumicTinkererModern.INFUSED_FARMLAND.get().defaultBlockState()))return InteractionResult.FAIL;
  if(!level.setBlockAndUpdate(above,ThaumicTinkererModern.INFUSED_GRAIN.get().defaultBlockState())){if(!(previous.getBlock() instanceof InfusedFarmlandBlock))level.setBlockAndUpdate(pos,previous);return InteractionResult.FAIL;}
  ((InfusedGrainEntity)level.getBlockEntity(above)).data(data);
  // Build 164 consumes seeds even in creative mode.
  stack.shrink(1);level.gameEvent(player,net.minecraft.world.level.gameevent.GameEvent.BLOCK_PLACE,above);return InteractionResult.CONSUME;
 }
}
