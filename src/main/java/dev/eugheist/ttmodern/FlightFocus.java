package dev.eugheist.ttmodern;
import dev.eugheist.tc4modern.FociContent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import thaumcraft.api.aspects.*;
public final class FlightFocus extends TinkererFocus {
    @Override public AspectList getVisCost(ItemStack focus){return new AspectList().add(Aspect.AIR,15);}
    @Override public String getSortingHelperBase(){return "FLIGHT";}
    @Override public int getFocusColor(ItemStack focus){return 0x9EF2FF;}
    @Override public boolean onFocusRightClick(ItemStack wand,Level level,Player player){
        if(!TinkererConfig.flightEnabled())return true;
        if(!level.isClientSide&&FociContent.consumeAllVis(wand,player,getVisCost(wand),true)){
            double force=(1/1.5)*(1+getUpgradeLevel(FociContent.getFocusItem(wand),0)*.2);
            player.setDeltaMovement(player.getLookAngle().scale(force));player.fallDistance=0;player.hurtMarked=true;
            if(player instanceof net.minecraft.server.level.ServerPlayer serverPlayer&&serverPlayer.connection!=null)((dev.eugheist.ttmodern.mixin.FlightConnectionAccessor)serverPlayer.connection).ttinkerer$resetFloatingTicks(0);
            level.playSound(null,player.blockPosition(),net.minecraft.sounds.SoundEvent.createVariableRangeEvent(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("tc4modern","wind")),net.minecraft.sounds.SoundSource.PLAYERS,.4f,1);
            if(level instanceof net.minecraft.server.level.ServerLevel server)server.sendParticles(net.minecraft.core.particles.ParticleTypes.CLOUD,player.getX(),player.getY(),player.getZ(),5,.2,.1,.2,.02);
        }
        return true;
    }
}
