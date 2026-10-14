package dev.eugheist.ttmodern;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;

public final class ConnectorItem extends Item {
    public ConnectorItem(){super(new Properties().stacksTo(1));}
    @Override public InteractionResult useOn(UseOnContext context){
        var level=context.getLevel();var player=context.getPlayer();var pos=context.getClickedPos();
        if(player==null||!player.mayUseItemAt(pos,context.getClickedFace(),context.getItemInHand())||!level.mayInteract(player,pos))return InteractionResult.FAIL;
        if(level.isClientSide)return InteractionResult.SUCCESS;
        var stack=context.getItemInHand();var data=stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();String message;
        if(!data.contains("Interface")){
            if(level.getBlockEntity(pos) instanceof TransvectorLink||level.getBlockEntity(pos) instanceof GolemConnector.Entity){data.putLong("Interface",pos.asLong());data.putString("Dimension",level.dimension().location().toString());message="ttmisc.connector.set";}
            else message="ttmisc.connector.notinterf";
        }else if(!level.dimension().location().toString().equals(data.getString("Dimension"))){message="ttmisc.connector.dimension";}
        else {
            var source=BlockPos.of(data.getLong("Interface"));
            if(!level.hasChunkAt(source)){message="ttmisc.connector.unloaded";}
            else if(!(level.getBlockEntity(source) instanceof TransvectorLink tile)){data.remove("Interface");data.remove("Dimension");message="ttmisc.connector.notpresent";}
            else if(level.getBlockEntity(pos) instanceof TransvectorLink){message="ttmisc.connector.interffail";}
            else if(!tile.inRange(pos)){message="ttmisc.connector.toofar";}
            else if(!level.mayInteract(player,source)){return InteractionResult.FAIL;}
            else if(tile.link(pos)){data.remove("Interface");data.remove("Dimension");message="ttmisc.connector.complete";}
            else message="ttmisc.connector.notpresent";
        }
        if(message.equals("ttmisc.connector.set")||message.equals("ttmisc.connector.complete"))level.playSound(null,pos,net.minecraft.sounds.SoundEvents.EXPERIENCE_ORB_PICKUP,net.minecraft.sounds.SoundSource.BLOCKS,.8f,1);
        stack.set(DataComponents.CUSTOM_DATA,CustomData.of(data));player.displayClientMessage(Component.translatable(message),false);
        return InteractionResult.CONSUME;
    }
    @Override public InteractionResult interactLivingEntity(ItemStack stack,net.minecraft.world.entity.player.Player player,net.minecraft.world.entity.LivingEntity target,net.minecraft.world.InteractionHand hand){
        if(!player.isShiftKeyDown()||!(target instanceof dev.eugheist.tc4modern.StrawGolem golem))return InteractionResult.PASS;
        // Creative Player.interactOn invokes the item fallback on a disposable copy.
        // Original ItemConnector also explicitly used the current held stack.
        var held=player.getItemInHand(hand);if(held.getItem()!=this)return InteractionResult.PASS;stack=held;
        var level=player.level();if(level.isClientSide)return InteractionResult.SUCCESS;
        var data=stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();String message="ttmisc.golemconnector.notinterf";
        if(data.contains("Interface")&&level.dimension().location().toString().equals(data.getString("Dimension"))){
            var source=BlockPos.of(data.getLong("Interface"));
            if(!level.hasChunkAt(source))message="ttmisc.connector.unloaded";
            else if(level.getBlockEntity(source) instanceof GolemConnector.Entity tile){
                if(!level.mayInteract(player,source))return InteractionResult.FAIL;
                tile.connect(golem.getUUID());data.remove("Interface");data.remove("Dimension");message="ttmisc.golemconnector.complete";
                level.playSound(null,target.blockPosition(),net.minecraft.sounds.SoundEvents.EXPERIENCE_ORB_PICKUP,net.minecraft.sounds.SoundSource.BLOCKS,.8f,1);
            }else {data.remove("Interface");data.remove("Dimension");message="ttmisc.golemconnector.notpresent";}
        }
        stack.set(DataComponents.CUSTOM_DATA,CustomData.of(data));player.displayClientMessage(Component.translatable(message),false);return InteractionResult.CONSUME;
    }
}
