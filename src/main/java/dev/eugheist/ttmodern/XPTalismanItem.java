package dev.eugheist.ttmodern;
import baubles.api.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.network.chat.Component;
import java.util.List;

public final class XPTalismanItem extends Item implements IBauble {
    public XPTalismanItem(){super(new Properties().stacksTo(1).rarity(Rarity.UNCOMMON));}
    public static int xp(ItemStack stack){return Math.clamp(stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag().getInt("xp"),0,1500);}
    public static boolean enabled(ItemStack stack){return CleansingTalismanItem.enabled(stack);}
    static void xp(ItemStack stack,int value){CustomData.update(DataComponents.CUSTOM_DATA,stack,t->t.putInt("xp",Math.clamp(value,0,1500)));}
    @Override public BaubleType getBaubleType(ItemStack stack){return BaubleType.AMULET;}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){
        var stack=player.getItemInHand(hand);
        if(!level.isClientSide){
            if(player.isShiftKeyDown()){if(xp(stack)<1500){CleansingTalismanItem.toggle(stack);level.playSound(null,player.blockPosition(),net.minecraft.sounds.SoundEvents.EXPERIENCE_ORB_PICKUP,net.minecraft.sounds.SoundSource.PLAYERS,.3f,.1f);}}
            else bottle(stack,player);
        }
        return InteractionResultHolder.sidedSuccess(stack,level.isClientSide);
    }
    static boolean bottle(ItemStack stack,Player player){
        if(xp(stack)<10)return false;
        for(int slot=0;slot<player.getInventory().getContainerSize();slot++){
            var empty=player.getInventory().getItem(slot);if(!empty.is(Items.GLASS_BOTTLE))continue;
            empty.shrink(1);xp(stack,xp(stack)-10);
            var bottle=new ItemStack(Items.EXPERIENCE_BOTTLE);player.getInventory().placeItemBackInInventory(bottle);
            player.level().playSound(null,player.blockPosition(),net.minecraft.sounds.SoundEvents.EXPERIENCE_ORB_PICKUP,net.minecraft.sounds.SoundSource.PLAYERS,.1f,.1f+player.getRandom().nextFloat()*.5f);
            fx(player,172,player.getX(),player.getY(),player.getZ(),0);
            return true;
        }
        return false;
    }
    @Override public void onWornTick(ItemStack stack,LivingEntity entity){
        if(entity.level().isClientSide||!enabled(stack))return;
        if(xp(stack)>=1500){CustomData.update(DataComponents.CUSTOM_DATA,stack,t->t.putBoolean("enabled",false));return;}
        var pos=entity.position();var box=new AABB(pos.x-3,pos.y-3,pos.z-3,pos.x+3,pos.y+3,pos.z+3);
        for(var orb:entity.level().getEntitiesOfClass(ExperienceOrb.class,box,o->o.isAlive())){
            var access=(dev.eugheist.ttmodern.mixin.ExperienceOrbAccessor)orb;
            int value=orb.getValue();if(value<=0)continue;
            int count=Math.min(access.ttinkerer$count(),(1500-xp(stack))/value);if(count<=0)continue;
            xp(stack,xp(stack)+count*value);int left=access.ttinkerer$count()-count;
            // Merged entities represent several original orbs; emit once per consumed entity,
            // sized by its denomination, never by unconsumed remainder or total stored XP.
            orb.level().playSound(null,orb.getX(),orb.getY(),orb.getZ(),net.minecraft.sounds.SoundEvent.createVariableRangeEvent(net.minecraft.resources.ResourceLocation.parse("tc4modern:zap")),net.minecraft.sounds.SoundSource.PLAYERS,value/10f,1);
            fx(entity,173,orb.getX(),orb.getY(),orb.getZ(),value/5f);
            if(left==0)orb.discard();else access.ttinkerer$count(left);
            if(xp(stack)>=1500)break;
        }
    }
    private static void fx(LivingEntity entity,int kind,double x,double y,double z,float scale){
        if(entity.level() instanceof net.minecraft.server.level.ServerLevel level)
            net.neoforged.neoforge.network.PacketDistributor.sendToPlayersNear(level,null,x,y,z,64,new dev.eugheist.tc4modern.InfusionContent.FxPayload(kind,x,y,z,scale,0,0,0));
    }
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){lines.add(Component.literal("XP: "+xp(stack)+" / 1500"));lines.add(Component.translatable(xp(stack)>=1500?"ttmisc.full":enabled(stack)?"ttmisc.absorbing":"ttmisc.notAbsorbing"));}
}
