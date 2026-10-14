package dev.eugheist.ttmodern;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.items.ItemStackHandler;
public final class AnimationTabletEntity extends BlockEntity implements MovableTile{
    boolean leftClick=true,redstone;public int swingProgress,ticks;int swingMod;
    boolean breaking;float damage;BlockState breakingState;TabletPlayer player;
    public final ItemStackHandler inventory=new ItemStackHandler(1){@Override protected void onContentsChanged(int slot){changed();}};
    public AnimationTabletEntity(BlockPos pos,BlockState state){super(ThaumicTinkererModern.ANIMATION_TABLET_ENTITY.get(),pos,state);}
    void changed(){setChanged();if(level!=null&&!level.isClientSide)level.sendBlockUpdated(worldPosition,getBlockState(),getBlockState(),3);}
    Direction facing(){return getBlockState().getValue(AnimationTabletBlock.FACING);}
    BlockPos target(){return worldPosition.relative(facing());}
    TabletPlayer player(){if(player==null)player=new TabletPlayer((ServerLevel)level);player.prepare(this);return player;}
    boolean detect(){var at=target();return level.hasChunkAt(at)&&(!level.isEmptyBlock(at)||!entities().isEmpty());}
    java.util.List<Entity> entities(){return level.getEntities((Entity)null,new AABB(target()),e->e.isAlive()&&!(e instanceof net.neoforged.neoforge.common.util.FakePlayer));}
    boolean trigger(){if(swingProgress!=0)return false;swingMod=3;swingProgress=1;changed();return true;}
    @Override public boolean prepareToMove(){stopBreaking();return true;}
    void stopBreaking(){if(breaking&&level instanceof ServerLevel server&&player!=null)server.destroyBlockProgress(player.getId(),target(),-1);breaking=false;damage=0;breakingState=null;}
    public static void tick(Level level,BlockPos pos,BlockState state,AnimationTabletEntity tile){tile.ticks++;if(level.isClientSide){tile.animate();return;}if(tile.inventory.getStackInSlot(0).isEmpty()){tile.swingProgress=tile.swingMod=0;tile.stopBreaking();return;}if(tile.swingProgress>=45)tile.hit();tile.animate();boolean detect=tile.detect();if(!detect)tile.stopBreaking();if(detect&&tile.breaking)tile.continueBreaking();if((!tile.redstone||tile.breaking)&&detect&&tile.swingProgress==0)tile.trigger();}
    void animate(){swingMod=swingProgress<=0?0:swingProgress>=45?-3:swingMod;swingProgress=Math.max(0,swingProgress+swingMod);}
    void hit(){if(!(level instanceof ServerLevel server)||!level.hasChunkAt(target()))return;var p=player();var entities=entities();var entity=entities.isEmpty()?null:entities.get(server.random.nextInt(entities.size()));try{
        if(leftClick){if(entity!=null){var stack=p.getMainHandItem();var cleanup=new java.util.ArrayList<Runnable>();stack.forEachModifier(EquipmentSlot.MAINHAND,(attribute,modifier)->{var instance=p.getAttribute(attribute);if(instance!=null){instance.addOrUpdateTransientModifier(modifier);cleanup.add(()->instance.removeModifier(modifier.id()));}});try{p.attack(entity);}finally{cleanup.forEach(Runnable::run);}}else if(!breaking&&!level.isEmptyBlock(target()))startBreaking(p);}
        else{
            boolean done=false;if(entity instanceof LivingEntity living){done=p.interactOn(entity,InteractionHand.MAIN_HAND).consumesAction();}
            if(!done){var at=target();var side=facing().getOpposite();if(level.isEmptyBlock(at)){at=at.below();side=Direction.UP;}if(level.hasChunkAt(at)){var hit=new BlockHitResult(Vec3.atCenterOf(at).add(Vec3.atLowerCornerOf(side.getNormal()).scale(.5)),side,at,false);done=p.gameMode.useItemOn(p,server,p.getMainHandItem(),InteractionHand.MAIN_HAND,hit).consumesAction();}}
            if(!done)p.gameMode.useItem(p,server,p.getMainHandItem(),InteractionHand.MAIN_HAND);
        }
    }finally{finishPlayer(p);}}
    void finishPlayer(TabletPlayer p){inventory.setStackInSlot(0,p.getMainHandItem());p.setItemInHand(InteractionHand.MAIN_HAND,ItemStack.EMPTY);for(int i=0;i<p.getInventory().getContainerSize();i++){var extra=p.getInventory().getItem(i);if(!extra.isEmpty())net.minecraft.world.level.block.Block.popResource(level,worldPosition,extra.copy());}p.getInventory().clearContent();changed();}
    void startBreaking(TabletPlayer p){var at=target();var state=level.getBlockState(at);if(state.getDestroySpeed(level,at)<0||!level.mayInteract(p,at))return;var event=net.neoforged.neoforge.common.CommonHooks.onLeftClickBlock(p,at,facing().getOpposite(),net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK);if(event.isCanceled()||event.getUseItem()==net.neoforged.neoforge.common.util.TriState.FALSE)return;
        if(event.getUseBlock()!=net.neoforged.neoforge.common.util.TriState.FALSE)state.attack(level,at,p);breaking=true;breakingState=state;damage=0;if(state.getDestroyProgress(p,level,at)>=1){p.gameMode.destroyBlock(at);stopBreaking();}}
    void continueBreaking(){var at=target();if(!level.hasChunkAt(at)||!level.getBlockState(at).equals(breakingState)){stopBreaking();return;}var p=player();try{damage+=breakingState.getDestroyProgress(p,level,at);((ServerLevel)level).destroyBlockProgress(p.getId(),at,Math.min(9,(int)(damage*10)));if(damage>=1){p.gameMode.destroyBlock(at);stopBreaking();}}finally{finishPlayer(p);}}
    @Override public void setRemoved(){stopBreaking();super.setRemoved();player=null;}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider registries){super.saveAdditional(tag,registries);tag.put("Inventory",inventory.serializeNBT(registries));tag.putBoolean("LeftClick",leftClick);tag.putBoolean("Redstone",redstone);tag.putInt("Progress",swingProgress);tag.putInt("Mod",swingMod);}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider registries){super.loadAdditional(tag,registries);inventory.deserializeNBT(registries,tag.getCompound("Inventory"));leftClick=!tag.contains("LeftClick")||tag.getBoolean("LeftClick");redstone=tag.getBoolean("Redstone");swingProgress=Math.clamp(tag.getInt("Progress"),0,46);swingMod=Math.clamp(tag.getInt("Mod"),-3,3);}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider registries){return saveWithoutMetadata(registries);}
    @Override public net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket getUpdatePacket(){return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);}
    static final class TabletPlayer extends net.neoforged.neoforge.common.util.FakePlayer{
        TabletPlayer(ServerLevel level){super(level,new com.mojang.authlib.GameProfile(java.util.UUID.fromString("a8f026a0-135b-11e4-9191-0800200c9a66"),"[ThaumcraftTablet]"));setGameMode(GameType.SURVIVAL);}
        void prepare(AnimationTabletEntity tile){getInventory().clearContent();getAbilities().instabuild=false;setHealth(20);stopRiding();ejectPassengers();setDeltaMovement(Vec3.ZERO);moveTo(tile.worldPosition.getX()+.5,tile.worldPosition.getY()+1.6,tile.worldPosition.getZ()+.5,tile.facing().toYRot(),-15);setItemInHand(InteractionHand.MAIN_HAND,tile.inventory.getStackInSlot(0));attackStrengthTicker=100;}
        @Override public void displayClientMessage(net.minecraft.network.chat.Component message,boolean actionBar){}
    }
}
