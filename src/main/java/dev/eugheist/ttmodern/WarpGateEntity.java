package dev.eugheist.ttmodern;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
public final class WarpGateEntity extends BlockEntity {
 boolean locked;private long lastTeleportTick=Long.MIN_VALUE;
 final net.neoforged.neoforge.items.ItemStackHandler inventory=new net.neoforged.neoforge.items.ItemStackHandler(10){
  @Override public int getSlotLimit(int slot){return 1;}
  @Override public boolean isItemValid(int slot,ItemStack stack){return stack.is(SkyPearl.ITEM.get());}
  @Override protected void onContentsChanged(int slot){changed();}
 };
 WarpGateEntity(BlockPos pos,BlockState state){super(WarpGateBlock.ENTITY.get(),pos,state);}
 void changed(){setChanged();if(level!=null)level.sendBlockUpdated(worldPosition,getBlockState(),getBlockState(),3);}
 boolean valid(net.minecraft.world.entity.player.Player player){return level!=null&&level.getBlockEntity(worldPosition)==this&&player.distanceToSqr(net.minecraft.world.phys.Vec3.atCenterOf(worldPosition))<=64;}
 boolean teleport(net.minecraft.server.level.ServerPlayer player,int index){
  if(level==null||level.isClientSide||index<0||index>=10||lastTeleportTick==level.getGameTime())return false;
  var pearl=inventory.getStackInSlot(index);if(pearl.isEmpty()||!SkyPearlData.attuned(pearl))return false;
  // Build164 resolves coordinates in the player's CURRENT world; pearl dimension is tooltip metadata.
  if(!teleport(player,SkyPearlData.position(pearl)))return false;lastTeleportTick=level.getGameTime();return true;
 }
 static boolean teleport(net.minecraft.server.level.ServerPlayer player,BlockPos destination){
  var level=player.serverLevel();
  if(!(level.getBlockEntity(destination) instanceof WarpGateEntity gate)){player.displayClientMessage(net.minecraft.network.chat.Component.translatable("ttmisc.noDest"),false);return false;}
  if(gate.locked){player.displayClientMessage(net.minecraft.network.chat.Component.translatable("ttmisc.noTeleport"),false);return false;}
  var sound=net.minecraft.sounds.SoundEvent.createVariableRangeEvent(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("tc4modern","wand"));
  level.playSound(null,player.blockPosition(),sound,net.minecraft.sounds.SoundSource.PLAYERS,1,1);WarpGateEffects.sparkle(player);player.stopRiding();
  player.connection.teleport(destination.getX()+.5,destination.getY()+1.6,destination.getZ()+.5,player.getYRot(),player.getXRot());
  WarpGateEffects.sparkle(player);level.playSound(null,player.blockPosition(),sound,net.minecraft.sounds.SoundSource.PLAYERS,1,.1f);return true;
 }
 @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider registries){super.saveAdditional(tag,registries);tag.putBoolean("locked",locked);tag.put("Inventory",inventory.serializeNBT(registries));}
 @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider registries){super.loadAdditional(tag,registries);locked=tag.getBoolean("locked");inventory.deserializeNBT(registries,tag.getCompound("Inventory"));lastTeleportTick=Long.MIN_VALUE;}
 @Override public CompoundTag getUpdateTag(HolderLookup.Provider registries){return saveWithoutMetadata(registries);}
 @Override public net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket getUpdatePacket(){return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);}
}
