package dev.eugheist.ttmodern;
import dev.eugheist.tc4modern.FociContent;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.*;
import net.minecraft.world.phys.*;
import thaumcraft.api.aspects.*;
public final class DislocationFocus extends TinkererFocus {
    @Override public String getSortingHelperBase(){return "DISLOCATION";}
    @Override public int getFocusColor(ItemStack focus){return 16757248;}
    @Override public int[] getPossibleUpgradesByRank(ItemStack focus,int rank){return new int[]{2};}
    @Override public AspectList getVisCost(ItemStack focus){return cost(null);}
    static AspectList cost(net.minecraft.world.level.block.entity.BlockEntity tile){int order=tile==null?500:tile instanceof net.minecraft.world.level.block.entity.SpawnerBlockEntity?10000:2500;int earth=tile==null?100:tile instanceof net.minecraft.world.level.block.entity.SpawnerBlockEntity?5000:500;return new AspectList().add(Aspect.ENTROPY,order).add(Aspect.ORDER,order).add(Aspect.EARTH,earth);}
    static CompoundTag payload(ItemStack focus){var data=focus.get(DataComponents.CUSTOM_DATA);return data==null?new CompoundTag():data.copyTag();}
    @Override public String getSortingHelper(ItemStack focus){var tag=payload(focus);return super.getSortingHelper(focus)+(DislocationStorage.available(tag)?tag.getCompound("dislocation").toString():"");}
    @Override public boolean onFocusRightClick(ItemStack wand,Level level,Player player){
        if(!level.isClientSide){var hit=getPlayerPOVHitResult(level,player,ClipContext.Fluid.SOURCE_ONLY);if(hit.getType()==HitResult.Type.BLOCK)useAt(wand,player,hit);}
        return true;
    }
    static net.minecraft.world.level.block.state.BlockState orient(net.minecraft.world.level.block.state.BlockState state,Player player,BlockPos pos){
        // These blocks reoriented in legacy onBlockPlacedBy; modern orientation moved into getStateForPlacement.
        var block=state.getBlock();
        if(block instanceof net.minecraft.world.level.block.ChestBlock){var context=new net.minecraft.world.item.context.BlockPlaceContext(player,net.minecraft.world.InteractionHand.MAIN_HAND,new ItemStack(block),new BlockHitResult(Vec3.atCenterOf(pos),Direction.UP,pos,false));var placement=block.getStateForPlacement(context);if(placement!=null)return placement;}
        if(block instanceof net.minecraft.world.level.block.AbstractFurnaceBlock||block instanceof net.minecraft.world.level.block.EnderChestBlock)return state.setValue(net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING,player.getDirection().getOpposite());
        if(block instanceof net.minecraft.world.level.block.DispenserBlock||block instanceof net.minecraft.world.level.block.piston.PistonBaseBlock){
            var facing=player.getDirection().getOpposite();
            if(Math.abs(player.getX()-pos.getX())<2&&Math.abs(player.getZ()-pos.getZ())<2){if(player.getEyeY()-pos.getY()>2)facing=Direction.UP;else if(pos.getY()>player.getEyeY())facing=Direction.DOWN;}
            state=state.setValue(net.minecraft.world.level.block.DirectionalBlock.FACING,facing);
            if(state.hasProperty(net.minecraft.world.level.block.piston.PistonBaseBlock.EXTENDED))state=state.setValue(net.minecraft.world.level.block.piston.PistonBaseBlock.EXTENDED,false);
            if(state.hasProperty(net.minecraft.world.level.block.DispenserBlock.TRIGGERED))state=state.setValue(net.minecraft.world.level.block.DispenserBlock.TRIGGERED,false);
        }
        return state;
    }
    boolean useAt(ItemStack wand,Player player,BlockHitResult hit){
        if(!(player.level() instanceof net.minecraft.server.level.ServerLevel level))return false;
        var focus=FociContent.getFocusItem(wand);if(focus.getItem()!=this)return false;var data=payload(focus);var at=hit.getBlockPos();boolean placing=DislocationStorage.available(data);var target=placing?at.relative(hit.getDirection()):at;
        if(!level.hasChunkAt(at)||!level.hasChunkAt(target)||!level.mayInteract(player,target)||!player.mayUseItemAt(target,hit.getDirection(),wand))return false;
        var interact=new net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock(player,player.getOffhandItem()==wand?net.minecraft.world.InteractionHand.OFF_HAND:net.minecraft.world.InteractionHand.MAIN_HAND,at,hit);net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(interact);if(interact.isCanceled()||interact.getUseItem()==net.neoforged.neoforge.common.util.TriState.FALSE)return false;
        var pickupSound=level.getBlockState(target).getSoundType(level,target,player).getBreakSound();
        if(placing){
            var snapshot=net.neoforged.neoforge.common.util.BlockSnapshot.create(level.dimension(),level,target);
            if(!DislocationStorage.place(level,target,data,()->{var event=new net.neoforged.neoforge.event.level.BlockEvent.EntityPlaceEvent(snapshot,level.getBlockState(at),player);net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(event);return !event.isCanceled();},state->orient(state,player,target),state->state.getBlock().setPlacedBy(level,target,state,player,wand)))return false;
        }else{
            if(!DislocationStorage.movable(level,target))return false;
            var event=new net.neoforged.neoforge.event.level.BlockEvent.BreakEvent(level,target,level.getBlockState(target),player);net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(event);if(event.isCanceled())return false;
            var usage=cost(level.getBlockEntity(target));if(!FociContent.consumeAllVis(wand,player,usage,false))return false;
            var old=wand.get(DataComponents.CUSTOM_DATA);if(!FociContent.consumeAllVis(wand,player,usage,true))return false;
            if(!DislocationStorage.pickup(level,target,data)){wand.set(DataComponents.CUSTOM_DATA,old);return false;}
        }
        focus.set(DataComponents.CUSTOM_DATA,CustomData.of(data));FociContent.setFocus(wand,focus);
        for(int i=0;i<8;i++){double x=target.getX()+level.random.nextFloat(),y=target.getY()+level.random.nextFloat()+(placing?.65:0),z=target.getZ()+level.random.nextFloat();net.neoforged.neoforge.network.PacketDistributor.sendToPlayersNear(level,null,x,y,z,64,new dev.eugheist.tc4modern.InfusionContent.FxPayload(167,x,y,z,0,0,0,0));}
        if(!placing)level.playSound(null,target,pickupSound,net.minecraft.sounds.SoundSource.BLOCKS,1,1);
        level.playSound(null,target,net.minecraft.sounds.SoundEvent.createVariableRangeEvent(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("tc4modern","wand")),net.minecraft.sounds.SoundSource.PLAYERS,.5f,1);return true;
    }
}
