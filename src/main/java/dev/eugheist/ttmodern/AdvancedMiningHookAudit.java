package dev.eugheist.ttmodern;
/** Audit-only item registration is absent from ordinary clients and production. */
final class AdvancedMiningHookAudit {
 static final net.neoforged.neoforge.registries.DeferredItem<Probe> ITEM=ThaumicTinkererModern.ITEMS.register("audit_mining_probe",Probe::new);
 static int calls;
 static void init(){}
 static final class Probe extends net.minecraft.world.item.PickaxeItem implements AdvancedMiningTool {
  Probe(){super(KamiTools.TIER,KamiTools.properties());}
  public void startMining(net.minecraft.server.level.ServerPlayer p,net.minecraft.core.BlockPos pos){calls++;AdvancedToolMining.harvest(p,pos,AdvancedToolModes.pick(1,net.minecraft.core.Direction.UP),s->s.is(net.minecraft.tags.BlockTags.MINEABLE_WITH_PICKAXE));}
 }
 static void run(net.minecraft.server.MinecraftServer server){var level=server.overworld();var p=new net.neoforged.neoforge.common.util.FakePlayer(level,new com.mojang.authlib.GameProfile(java.util.UUID.randomUUID(),"HookAudit"));p.setGameMode(net.minecraft.world.level.GameType.CREATIVE);p.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,new net.minecraft.world.item.ItemStack(ITEM.get()));var origin=new net.minecraft.core.BlockPos(5,290,5);var positions=new java.util.ArrayList<>(AdvancedToolModes.pick(1,net.minecraft.core.Direction.UP).positions(origin));positions.add(origin);var saved=new java.util.HashMap<net.minecraft.core.BlockPos,net.minecraft.world.level.block.state.BlockState>();for(var pos:positions){saved.put(pos,level.getBlockState(pos));level.setBlock(pos,net.minecraft.world.level.block.Blocks.STONE.defaultBlockState(),2);}
 java.util.function.Consumer<net.neoforged.neoforge.event.level.BlockEvent.BreakEvent> deny=e->{if(e.getPlayer()==p&&e.getPos().equals(origin))e.setCanceled(true);};net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(deny);calls=0;
 try{AdvancedToolMiningAudit.check(!p.gameMode.destroyBlock(origin)&&calls==0&&positions.stream().noneMatch(at->level.getBlockState(at).isAir()),"canceled center never invokes area hook");}finally{net.neoforged.neoforge.common.NeoForge.EVENT_BUS.unregister(deny);}
 try{AdvancedToolMiningAudit.check(p.gameMode.destroyBlock(origin)&&calls==1&&positions.stream().allMatch(at->level.getBlockState(at).isAir()),"native center invokes hook once,extra breaks do not recurse");AdvancedToolMiningAudit.check(!AdvancedToolMining.breaking(p),"native recursion guard cleanup");System.out.println("TT ADVANCED MINING HOOK AUDIT: ALL PASS (real gameMode center dispatch,25block area,no recursion,canceled center no extras,audit-only probe)");}finally{saved.forEach((pos,state)->level.setBlock(pos,state,2));p.discard();}
 }
}
