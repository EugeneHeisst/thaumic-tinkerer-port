package dev.eugheist.ttmodern;
import dev.eugheist.tc4modern.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import thaumcraft.api.aspects.Aspect;
/** A vis charge relay above the osmotic enchanter draws network vis into the enchanter's wand. */
@EventBusSubscriber(modid="ttinkerer",value=net.neoforged.api.distmarker.Dist.CLIENT)
final class EnchanterChargerAudit {
 static int ticks,failures;static BlockPos table;
 static void check(boolean ok,String m){if(!ok)failures++;System.out.println("TT_ENCHANTER_CHARGER "+(ok?"PASS ":"FAIL ")+m);}
 @SubscribeEvent static void tick(net.neoforged.neoforge.client.event.ClientTickEvent.Post e){if(!Boolean.getBoolean("ttinkerer.enchanterChargerAudit"))return;var mc=net.minecraft.client.Minecraft.getInstance();if(mc.player==null||mc.level==null)return;var server=mc.getSingleplayerServer();var id=mc.player.getUUID();ticks++;
  if(ticks==40){var r=new java.util.Random();table=new BlockPos(30000+r.nextInt(4000),200,30000+r.nextInt(4000));server.execute(()->{var p=server.getPlayerList().getPlayer(id);var l=p.serverLevel();
   p.setGameMode(net.minecraft.world.level.GameType.CREATIVE);p.getAbilities().flying=true;p.onUpdateAbilities();p.connection.teleport(table.getX()+4.5,table.getY(),table.getZ()+.5,90,0);
   for(int x=-6;x<=6;x++)for(int z=-6;z<=6;z++)l.setBlock(table.offset(x,-1,z),Blocks.STONE.defaultBlockState(),3);
   l.setBlock(table,ThaumicTinkererModern.ENCHANTER.get().defaultBlockState(),3);l.setBlock(table.above(),VisChargeRelay.BLOCK.get().defaultBlockState(),3);
   var source=table.offset(-4,2,0);l.setBlock(source,TC4Modern.ENERGIZED_NODE.get().defaultBlockState(),3);
   var node=(EnergizedNodeEntity)l.getBlockEntity(source);for(var a:Aspect.getPrimalAspects())node.auraBase.add(a,64);node.energy.setup(node.auraBase,node.nodeType,node.modifier,new java.util.Random(0));node.changed();
   ((EnchanterEntity)l.getBlockEntity(table)).inventory.setStackInSlot(1,WandParts.make(WandParts.CAPS.get("gold"),WandParts.RODDEFS.get("greatwood")));});}
  if(ticks>40&&ticks<400&&table!=null)server.execute(()->{var p=server.getPlayerList().getPlayer(id);if(p!=null)p.connection.teleport(table.getX()+4.5,table.getY(),table.getZ()+.5,90,0);});
  if(ticks==400)server.execute(()->{var l=server.getPlayerList().getPlayer(id).serverLevel();var wand=((EnchanterEntity)l.getBlockEntity(table)).inventory.getStackInSlot(1);
   var sb=new StringBuilder();int charged=0;for(var a:Aspect.getPrimalAspects()){int v=WandData.get(wand,a);sb.append(a.getTag()).append('=').append(v).append(' ');if(v>0)charged++;}
   check(charged==6,"all six primals charged into the enchanter wand: "+sb);
   check(l.getBlockEntity(table).getUpdateTag(l.registryAccess()).toString().contains("Inventory"),"enchanter syncs its inventory");
   System.out.println("TT_ENCHANTER_CHARGER "+(failures==0?"ALL PASS":"FAILURES "+failures));mc.execute(mc::stop);});
 }
}
