package dev.eugheist.ttmodern;
final class WarpGateAudit {
 static void check(boolean ok,String label){if(!ok)throw new IllegalStateException("TT warp gate: "+label);}
 static void run(net.minecraft.server.MinecraftServer server){var level=server.overworld();var from=new net.minecraft.core.BlockPos(5,290,5);var to=new net.minecraft.core.BlockPos(7,290,5);var oldFrom=level.getBlockState(from);var oldTo=level.getBlockState(to);WarpGateEntity source=null;
  try{level.setBlockAndUpdate(from,WarpGateBlock.BLOCK.get().defaultBlockState());level.setBlockAndUpdate(to,WarpGateBlock.BLOCK.get().defaultBlockState());source=(WarpGateEntity)level.getBlockEntity(from);var dest=(WarpGateEntity)level.getBlockEntity(to);
   check(source.inventory.getSlots()==10&&source.inventory.getSlotLimit(0)==1&&!source.inventory.isItemValid(0,new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.COAL)),"ten pearl-only single slots");
   var p=new net.neoforged.neoforge.common.util.FakePlayer(level,new com.mojang.authlib.GameProfile(java.util.UUID.randomUUID(),"WarpGateAudit"));p.setPos(5.5,291,5.5);p.setYRot(20);p.setXRot(10);
   var connection=new RecordingConnection(server,p);p.connection=connection;
   var pearl=new net.minecraft.world.item.ItemStack(SkyPearl.ITEM.get());p.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,pearl);
   var hit=new net.minecraft.world.phys.BlockHitResult(net.minecraft.world.phys.Vec3.atCenterOf(to),net.minecraft.core.Direction.UP,to,false);
   SkyPearl.ITEM.get().useOn(new net.minecraft.world.item.context.UseOnContext(p,net.minecraft.world.InteractionHand.MAIN_HAND,hit));check(SkyPearlData.position(pearl).equals(to),"actual pearl use binds native gate");
   source.inventory.setStackInSlot(0,pearl.copy());source.locked=true;var saved=source.saveWithoutMetadata(server.registryAccess());var restored=new WarpGateEntity(from,source.getBlockState());restored.loadWithComponents(saved,server.registryAccess());check(restored.locked&&SkyPearlData.position(restored.inventory.getStackInSlot(0)).equals(to),"block entity native save/load lock and destination");
   var menu=new WarpGateMenu(1,p.getInventory(),source);check(menu.slots.size()==46&&menu.slots.get(0).x==30&&menu.slots.get(5).y==52,"native46-slot original management layout");
   p.getInventory().setItem(9,pearl.copy());check(!menu.quickMoveStack(p,10).isEmpty()&&p.getInventory().getItem(9).isEmpty()&&source.inventory.getStackInSlot(1).is(SkyPearl.ITEM.get()),"shift insert pearl into next free single slot");
   check(!menu.quickMoveStack(p,1).isEmpty()&&source.inventory.getStackInSlot(1).isEmpty(),"shift withdrawal conserves pearl");
   check(menu.clickMenuButton(p,0)&&!source.locked&&menu.clickMenuButton(p,0)&&source.locked&&!menu.clickMenuButton(p,1),"lock toggles via native menu and rejects unknown button");
   p.setPos(100,291,100);check(!menu.clickMenuButton(p,0)&&source.locked,"remote stale management menu cannot toggle gate");p.setPos(5.5,291,5.5);
   dest.locked=true;check(!WarpGateEntity.teleport(p,to)&&p.getX()==5.5,"locked destination refuses");dest.locked=false;
   check(!source.teleport(p,-1)&&!source.teleport(p,10)&&!source.teleport(p,1),"invalid and empty slots refuse");
   var request=new WarpGateNetwork.Teleport(from,0);var buffer=new net.minecraft.network.FriendlyByteBuf(io.netty.buffer.Unpooled.buffer());try{WarpGateNetwork.Teleport.CODEC.encode(buffer,request);check(WarpGateNetwork.Teleport.CODEC.decode(buffer).equals(request),"native destination packet codec");}finally{buffer.release();}
   p.setPos(100,291,100);check(!WarpGateNetwork.handle(p,request),"remote destination request denied");p.setPos(5.5,291,5.5);check(!WarpGateNetwork.handle(p,new WarpGateNetwork.Teleport(from,-1))&&!WarpGateNetwork.handle(p,new WarpGateNetwork.Teleport(from,10)),"invalid destination packet index denied");
   check(WarpGateNetwork.handle(p,request)&&Math.abs(connection.x-7.5)<1e-12&&Math.abs(connection.y-291.6)<1e-12&&connection.z==5.5&&connection.yaw==20&&connection.pitch==10,"teleport dispatch centered+1.6 preserving angles; locked source permits outbound");
   check(!source.teleport(p,0),"one successful outbound teleport per gate per tick");
   System.out.println("TT WARP GATE AUDIT: ALL PASS (native gate pearl binding,ten single slots,save/load lock and destinations,locked destination,invalid indices,centered teleport dispatch/angles,source lock outbound,per-tick guard,46-slot management/shift transfer/lock/distance,network codec/index/distance/dispatch)");
  }finally{if(source!=null)for(int i=0;i<10;i++)source.inventory.setStackInSlot(i,net.minecraft.world.item.ItemStack.EMPTY);level.setBlockAndUpdate(from,oldFrom);level.setBlockAndUpdate(to,oldTo);}
 }
 static final class RecordingConnection extends net.minecraft.server.network.ServerGamePacketListenerImpl {
  double x,y,z;float yaw,pitch;
  RecordingConnection(net.minecraft.server.MinecraftServer server,net.minecraft.server.level.ServerPlayer player){super(server,new net.minecraft.network.Connection(net.minecraft.network.protocol.PacketFlow.SERVERBOUND),player,net.minecraft.server.network.CommonListenerCookie.createInitial(player.getGameProfile(),false));}
  @Override public void teleport(double x,double y,double z,float yaw,float pitch){this.x=x;this.y=y;this.z=z;this.yaw=yaw;this.pitch=pitch;}
 }

}
