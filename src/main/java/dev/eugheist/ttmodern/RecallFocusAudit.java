package dev.eugheist.ttmodern;
import dev.eugheist.tc4modern.*;
import net.minecraft.world.item.*;
import thaumcraft.api.aspects.*;
final class RecallFocusAudit {
 static void check(boolean value,String message){if(!value)throw new IllegalStateException("TT recall: "+message);}
 static void run(net.minecraft.server.MinecraftServer server){
  var level=server.overworld();var pos=new net.minecraft.core.BlockPos(9,290,5);var old=level.getBlockState(pos);
  try{
   level.setBlockAndUpdate(pos,WarpGateBlock.BLOCK.get().defaultBlockState());var gate=(WarpGateEntity)level.getBlockEntity(pos);
   var player=new net.neoforged.neoforge.common.util.FakePlayer(level,new com.mojang.authlib.GameProfile(java.util.UUID.randomUUID(),"RecallAudit"));
   var connection=new WarpGateAudit.RecordingConnection(server,player);player.connection=connection;
   var wand=WandParts.make(WandParts.CAPS.get("gold"),WandParts.RODDEFS.get("silverwood"));var focus=RecallFocus.ITEM.get();
   FociContent.setFocus(wand,new ItemStack(focus));for(var a:new Aspect[]{Aspect.AIR,Aspect.EARTH,Aspect.ORDER})WandData.add(wand,a,80,true);
   player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,wand);
   var pearl=new ItemStack(SkyPearl.ITEM.get());SkyPearlData.bind(pearl,pos,level.dimension().location());player.getInventory().setItem(1,pearl);
   int duration=wand.getUseDuration(player);int before=WandData.get(wand,Aspect.AIR);
   player.startUsingItem(net.minecraft.world.InteractionHand.MAIN_HAND);focus.onUsingFocusTick(wand,player,duration-60);
   check(player.isUsingItem()&&connection.x==0&&WandData.get(wand,Aspect.AIR)==before,"exact60ticks does not activate");
   var cost=focus.getVisCost(wand);int expected=(int)(4000*Math.max(WandData.craftingModifier(wand,player,Aspect.AIR),.1f));
   focus.onUsingFocusTick(wand,player,duration-61);
   check(!player.isUsingItem()&&connection.x==9.5&&connection.y==291.6&&WandData.get(wand,Aspect.AIR)==before-expected&&player.getInventory().getItem(1).is(SkyPearl.ITEM.get()),"61tick dispatch,discounted40vis and reusable pearl");
   before=WandData.get(wand,Aspect.AIR);connection.x=0;gate.locked=true;focus.onUsingFocusTick(wand,player,duration-61);
   check(connection.x==0&&WandData.get(wand,Aspect.AIR)==before,"locked destination consumes no vis");gate.locked=false;
   var wrong=pearl.copy();SkyPearlData.bind(wrong,pos,net.minecraft.resources.ResourceLocation.withDefaultNamespace("the_nether"));player.getInventory().setItem(0,wrong);
   focus.onUsingFocusTick(wand,player,duration-61);check(connection.x==0&&WandData.get(wand,Aspect.AIR)==before,"first bound wrong-dimension pearl prevents searching later slots");
   player.getInventory().setItem(0,ItemStack.EMPTY);player.getInventory().setItem(1,ItemStack.EMPTY);player.getInventory().setItem(9,pearl);
   focus.onUsingFocusTick(wand,player,duration-61);check(connection.x==0&&WandData.get(wand,Aspect.AIR)==before,"nonhotbar pearl ignored");
   player.getInventory().setItem(1,pearl);var empty=WandParts.make(WandParts.CAPS.get("gold"),WandParts.RODDEFS.get("silverwood"));focus.onUsingFocusTick(empty,player,duration-61);check(connection.x==0,"insufficient vis refuses");
   var stack=new ItemStack(focus);check(stack.getRarity()==Rarity.EPIC&&!focus.isVisCostPerTick(stack)&&cost.getAmount(Aspect.ORDER)==4000&&focus.getPossibleUpgradesByRank(stack,1).length==0,"original focus properties");
   var recipe=InfusionItemRecipes.all().stream().filter(r->r.key().equals("FOCUS_RECALL")).findFirst().orElseThrow();check(recipe.componentStacks().size()==6&&recipe.componentStacks().get(2).is(Items.ENDER_PEARL)&&recipe.componentStacks().get(4).is(FocusResearch.item("mirror")),"original infusion components");
   System.out.println("TT RECALL FOCUS AUDIT: ALL PASS (60/61 tick boundary,first bound hotbar pearl,dimension refusal,locked/insufficient/no-hotbar no cost,success-only discounted40vis,reusable pearl,EPIC/no upgrades,recipe)");
  }finally{level.setBlockAndUpdate(pos,old);}
 }
}
