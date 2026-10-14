package dev.eugheist.ttmodern;
final class IchorSwordBlockingAudit {
 static void check(boolean value,String label){if(!value)throw new IllegalStateException("TT sword block: "+label);}
 static void run(net.minecraft.server.MinecraftServer server){
  var previousDifficulty=server.getWorldData().getDifficulty();server.setDifficulty(net.minecraft.world.Difficulty.NORMAL,true);try{var level=server.overworld();var p=new net.neoforged.neoforge.common.util.FakePlayer(level,new com.mojang.authlib.GameProfile(java.util.UUID.randomUUID(),"SwordBlockAudit")){@Override public boolean isInvulnerableTo(net.minecraft.world.damagesource.DamageSource source){return false;}};try{var field=net.minecraft.server.level.ServerPlayer.class.getDeclaredField("spawnInvulnerableTime");field.setAccessible(true);field.setInt(p,0);}catch(ReflectiveOperationException e){throw new IllegalStateException(e);}p.setGameMode(net.minecraft.world.level.GameType.SURVIVAL);p.getAbilities().invulnerable=false;p.setPos(4,290,4);
  var cow=net.minecraft.world.entity.EntityType.COW.create(level);
  for(var item:java.util.List.of(KamiTools.SWORD.get(),AwakenedSword.ITEM.get())){
   var stack=new net.minecraft.world.item.ItemStack(item);p.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,stack);p.setShiftKeyDown(false);
   check(item.getUseAnimation(stack)==net.minecraft.world.item.UseAnim.BLOCK&&item.getUseDuration(stack,p)==72000,"original animation/duration");
   for(int side:new int[]{-1,1}){
    SoulHearts.set(p,0);p.setHealth(20);p.invulnerableTime=0;cow.setPos(4,290,4+side*2);item.use(level,p,net.minecraft.world.InteractionHand.MAIN_HAND);
    check(p.isUsingItem()&&!p.isBlocking(),"instant legacy use without modern shield ability");
    check(p.hurt(p.damageSources().mobAttack(cow),8)&&p.getHealth()==15.5f,"eight damage becomes4.5 from either direction,health="+p.getHealth());
   }
   SoulHearts.set(p,2);p.setHealth(20);p.invulnerableTime=0;p.hurt(p.damageSources().mobAttack(cow),8);check(SoulHearts.get(p)==0&&p.getHealth()==16.5f,"soul reserve consumed before sword reduction");
   SoulHearts.set(p,0);p.setHealth(20);p.invulnerableTime=0;p.hurt(p.damageSources().fall(),8);check(p.getHealth()==12,"armor bypass is unblockable");
   p.stopUsingItem();p.setHealth(20);p.invulnerableTime=0;p.hurt(p.damageSources().mobAttack(cow),8);check(p.getHealth()==12&&stack.getDamageValue()==0,"release restores full damage and unbreakable sword unchanged");
  }
  var advanced=new net.minecraft.world.item.ItemStack(AwakenedSword.ITEM.get());p.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,advanced);p.setShiftKeyDown(true);AwakenedSword.ITEM.get().use(level,p,net.minecraft.world.InteractionHand.MAIN_HAND);check(!p.isUsingItem()&&AdvancedToolModes.get(advanced)==1,"shift still changes awakened mode without blocking");
  System.out.println("TT SWORD BLOCKING AUDIT: ALL PASS (both swords,native instant use,front/back4.5damage,soul-before-block order,unblockable bypass,release,unbreakable,awakened shift mode)");
  }finally{server.setDifficulty(previousDifficulty,true);}
 }
}
