package dev.eugheist.ttmodern;
import dev.eugheist.tc4modern.FociContent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import thaumcraft.api.aspects.*;
public final class HealFocus extends TinkererFocus {
    private final java.util.Map<java.util.UUID,Integer> progress=new java.util.HashMap<>();
    @Override public AspectList getVisCost(ItemStack focus){return new AspectList().add(Aspect.EARTH,45).add(Aspect.WATER,45);}
    @Override public String getSortingHelperBase(){return "HEAL";}
    @Override public int getFocusColor(ItemStack focus){return 16580822;}
    @Override public String getDepthTexture(ItemStack focus){return "ttinkerer_focus_heal_depth";}
    @Override public void onPlayerStoppedUsingFocus(ItemStack wand,Level level,Player player,int count){progress.remove(player.getUUID());}
    @Override public void onUsingFocusTick(ItemStack wand,Player player,int count){
        if(player.level().isClientSide||!player.isAlive()||!player.isHurt()||!FociContent.consumeAllVis(wand,player,getVisCost(wand),false))return;
        int ticks=progress.getOrDefault(player.getUUID(),0)+1;progress.put(player.getUUID(),ticks);
        if(player.level() instanceof net.minecraft.server.level.ServerLevel server){var r=server.random;double x=player.getX()+r.nextFloat()-.5,y=player.getY()+r.nextFloat(),z=player.getZ()+r.nextFloat()-.5;net.neoforged.neoforge.network.PacketDistributor.sendToPlayersNear(server,null,x,y,z,64,new dev.eugheist.tc4modern.InfusionContent.FxPayload(166,x,y,z,0,0,0,0));}
        if(ticks>=30-getUpgradeLevel(FociContent.getFocusItem(wand),0)*10/3){
            progress.remove(player.getUUID());
            if(FociContent.consumeAllVis(wand,player,getVisCost(wand),true)){player.heal(1);player.level().playSound(null,player.blockPosition(),net.minecraft.sounds.SoundEvent.createVariableRangeEvent(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("tc4modern","wand")),net.minecraft.sounds.SoundSource.PLAYERS,.5f,1);}
        }
    }
}
