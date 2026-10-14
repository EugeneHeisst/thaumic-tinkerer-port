package dev.eugheist.ttmodern;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.EquipmentSlot;
final class RevealingHelmClientAudit {
 static final net.minecraft.core.BlockPos JAR=EnchanterClientAudit.POS.above(3);
 static void tick(Minecraft mc,int tick){
  if(tick==142){mc.player.closeContainer();var uuid=mc.player.getUUID();mc.getSingleplayerServer().execute(()->{var p=mc.getSingleplayerServer().getPlayerList().getPlayer(uuid);p.setItemSlot(EquipmentSlot.HEAD,new ItemStack(ThaumicTinkererModern.REVEALING_HELM.get()));var level=p.serverLevel();level.setBlockAndUpdate(JAR,dev.eugheist.tc4modern.EssentiaDevices.JAR.get().defaultBlockState());var jar=(dev.eugheist.tc4modern.WardedJarEntity)level.getBlockEntity(JAR);jar.state.aspect=thaumcraft.api.aspects.Aspect.AIR;jar.state.amount=16;jar.setChanged();level.sendBlockUpdated(JAR,jar.getBlockState(),jar.getBlockState(),3);});}
  if(tick==155){EnchanterClientAudit.check(mc.player.getItemBySlot(EquipmentSlot.HEAD).is(ThaumicTinkererModern.REVEALING_HELM.get()),"revealing helmet equipped through real server inventory sync");
   try{var scale=dev.eugheist.tc4modern.GogglesAspectTags.class.getDeclaredField("tagscale");scale.setAccessible(true);scale.setFloat(null,0);var hit=new net.minecraft.world.phys.BlockHitResult(net.minecraft.world.phys.Vec3.atCenterOf(JAR),net.minecraft.core.Direction.UP,JAR,false);var event=new net.neoforged.neoforge.client.event.RenderHighlightEvent.Block(mc.levelRenderer,mc.gameRenderer.getMainCamera(),hit,mc.getTimer(),new com.mojang.blaze3d.vertex.PoseStack(),mc.renderBuffers().bufferSource());net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(event);mc.renderBuffers().bufferSource().endBatch();EnchanterClientAudit.check(scale.getFloat(null)>0,"real goggles highlight listener renders aspect popup for addon helmet");var head=mc.player.getItemBySlot(EquipmentSlot.HEAD);mc.player.setItemSlot(EquipmentSlot.HEAD,ItemStack.EMPTY);scale.setFloat(null,0);net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(event);EnchanterClientAudit.check(scale.getFloat(null)==0,"popup hidden without revealing gear");mc.player.setItemSlot(EquipmentSlot.HEAD,head);}catch(ReflectiveOperationException e){throw new IllegalStateException(e);}
  }
  if(tick==158){var uuid=mc.player.getUUID();mc.getSingleplayerServer().execute(()->{var p=mc.getSingleplayerServer().getPlayerList().getPlayer(uuid);p.setGameMode(net.minecraft.world.level.GameType.SURVIVAL);p.getAbilities().mayfly=true;p.getAbilities().flying=true;p.onUpdateAbilities();});}
  if(tick==165){EnchanterClientAudit.check(!mc.gameMode.hasInfiniteItems(),"survival inventory exposes native armor preview");mc.setScreen(new net.minecraft.client.gui.screens.inventory.InventoryScreen(mc.player));}
  if(tick==175){var item=ThaumicTinkererModern.REVEALING_HELM.get();var texture=item.getArmorTexture(mc.player.getItemBySlot(EquipmentSlot.HEAD),mc.player,EquipmentSlot.HEAD,dev.eugheist.tc4modern.ThaumiumContent.ARMOR.value().layers().getFirst(),false);EnchanterClientAudit.check(mc.getResourceManager().getResource(texture).isPresent(),"original helmet armor texture available");EnchanterClientAudit.Client.shot(mc,"revealing-helmet.png");}
 }
 private RevealingHelmClientAudit(){}
}
