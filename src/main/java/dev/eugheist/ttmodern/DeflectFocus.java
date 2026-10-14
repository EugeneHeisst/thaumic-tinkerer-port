package dev.eugheist.ttmodern;
import dev.eugheist.tc4modern.FociContent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.phys.AABB;
import thaumcraft.api.aspects.*;
/** Build164 displacement shield: translate along motion without reversing velocity or changing owner. */
public final class DeflectFocus extends TinkererFocus {
    @Override public AspectList getVisCost(ItemStack focus){return new AspectList().add(Aspect.ORDER,8).add(Aspect.AIR,4);}
    @Override public String getSortingHelperBase(){return "DEFLECT";}
    @Override public int getFocusColor(ItemStack focus){return 0xFFFFFF;}
    @Override public boolean isVisCostPerTick(ItemStack focus){return true;}
    @Override public int[] getPossibleUpgradesByRank(ItemStack focus,int rank){return new int[]{2};}
    @Override public void onUsingFocusTick(ItemStack wand,Player player,int count){
        if(player.level().isClientSide||!FociContent.consumeAllVis(wand,player,getVisCost(wand),true))return;
        protect(player);
    }
    public static void protect(Player player){
        if(player.level().isClientSide)return;
        var bounds=new AABB(player.position().subtract(4,4,4),player.position().add(3,3,3));
        for(var entity:player.level().getEntitiesOfClass(Projectile.class,bounds,e->e.isAlive()&&(e instanceof AbstractArrow||e instanceof ThrowableProjectile)&&e.getClass()!=ThrownExperienceBottle.class&&e.getOwner()!=player)){
            var from=entity.position();var shift=entity.getDeltaMovement().normalize().scale(entity.distanceTo(player)*2);
            if(player.level() instanceof net.minecraft.server.level.ServerLevel server)net.neoforged.neoforge.network.PacketDistributor.sendToPlayersNear(server,null,from.x,from.y,from.z,64,new dev.eugheist.tc4modern.InfusionContent.FxPayload(165,from.x,from.y,from.z,0,0,0,6));
            entity.setPos(from.add(shift));entity.hurtMarked=true;
        }
    }
}
