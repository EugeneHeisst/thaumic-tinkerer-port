package dev.eugheist.ttmodern;
import dev.eugheist.tc4modern.FociContent;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.*;
import net.minecraft.world.phys.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.api.aspects.*;
public final class SmeltFocus extends TinkererFocus {
    private record Progress(net.minecraft.resources.ResourceKey<Level> dimension,BlockPos pos,BlockState input,int ticks){}
    private final java.util.Map<java.util.UUID,Progress> progress=new java.util.HashMap<>();
    @Override public AspectList getVisCost(ItemStack focus){return new AspectList().add(Aspect.FIRE,45).add(Aspect.ENTROPY,12);}
    @Override public String getSortingHelperBase(){return "SMELT";}
    @Override public int getFocusColor(ItemStack focus){return 0xFF0000;}
    @Override public void onPlayerStoppedUsingFocus(ItemStack wand,Level level,Player player,int count){progress.remove(player.getUUID());}
    @Override public void onUsingFocusTick(ItemStack wand,Player player,int count){
        if(player.level().isClientSide)return;
        var eye=player.getEyePosition();var hit=player.level().clip(new ClipContext(eye,eye.add(player.getLookAngle().scale(10)),ClipContext.Block.OUTLINE,ClipContext.Fluid.NONE,player));
        if(hit.getType()!=HitResult.Type.BLOCK){progress.remove(player.getUUID());return;}
        smelt(wand,player,hit.getBlockPos());
    }
    boolean smelt(ItemStack wand,Player player,BlockPos pos){
        var level=player.level();if(!level.hasChunkAt(pos)||!level.mayInteract(player,pos)||!player.mayUseItemAt(pos,net.minecraft.core.Direction.UP,wand)||level.getBlockEntity(pos)!=null){progress.remove(player.getUUID());return false;}
        var state=level.getBlockState(pos);var input=new ItemStack(state.getBlock());
        if(input.isEmpty()||!FociContent.consumeAllVis(wand,player,getVisCost(wand),false)){progress.remove(player.getUUID());return false;}
        var recipe=level.getRecipeManager().getRecipeFor(RecipeType.SMELTING,new SingleRecipeInput(input),level);
        var result=recipe.map(r->r.value().assemble(new SingleRecipeInput(input),level.registryAccess())).orElse(ItemStack.EMPTY);
        if(!(result.getItem() instanceof BlockItem block)||block.getBlock().defaultBlockState().hasBlockEntity()){progress.remove(player.getUUID());return false;}
        var old=progress.get(player.getUUID());int ticks=20-Math.min(3,getUpgradeLevel(FociContent.getFocusItem(wand),0))*5;
        if(old!=null&&old.dimension()==level.dimension()&&old.pos().equals(pos)&&old.input()==state)ticks=old.ticks()-1;
        if(ticks>0){progress.put(player.getUUID(),new Progress(level.dimension(),pos.immutable(),state,ticks));return false;}
        progress.remove(player.getUUID());
        var event=new net.neoforged.neoforge.event.level.BlockEvent.BreakEvent(level,pos,state,player);net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(event);if(event.isCanceled())return false;
        if(!FociContent.consumeAllVis(wand,player,getVisCost(wand),true))return false;
        level.setBlock(pos,block.getBlock().defaultBlockState(),3);
        level.playSound(null,pos,net.minecraft.sounds.SoundEvents.FIRECHARGE_USE,net.minecraft.sounds.SoundSource.BLOCKS,.6f,1);
        if(level instanceof net.minecraft.server.level.ServerLevel server)server.sendParticles(net.minecraft.core.particles.ParticleTypes.FLAME,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,25,.3,.3,.3,.02);
        return true;
    }
}
