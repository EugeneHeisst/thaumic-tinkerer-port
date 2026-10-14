package dev.eugheist.ttmodern;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.Blocks;
import dev.eugheist.tc4modern.*;
import thaumcraft.api.aspects.*;
final class FocusAudit {
    private static void check(boolean ok,String label){if(!ok)throw new IllegalStateException("TT PORT AUDIT FAILED: focus "+label);}
    static void run(ServerLevel level){
        DislocationStorageAudit.run(level);
        var player=net.neoforged.neoforge.common.util.FakePlayerFactory.getMinecraft(level);player.getInventory().clearContent();player.setPos(8,270,8);player.setYRot(0);player.setXRot(0);
        var wand=new ItemStack(TC4Modern.GOLD_GREATWOOD_WAND.get());for(var aspect:Aspect.getPrimalAspects())WandData.add(wand,aspect,1000,true);player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,wand);
        var flight=ThaumicTinkererModern.FOCUS_FLIGHT.get();FociContent.setFocus(wand,new ItemStack(flight));int air=WandData.get(wand,Aspect.AIR);player.fallDistance=10;
        flight.onFocusRightClick(wand,level,player);check(Math.abs(player.getDeltaMovement().length()-1/1.5)<1e-6&&player.fallDistance==0,"flight impulse and fall reset");check(air-WandData.get(wand,Aspect.AIR)==15,"flight cost centivis");
        var empty=new ItemStack(TC4Modern.GOLD_GREATWOOD_WAND.get());FociContent.setFocus(empty,new ItemStack(flight));player.setDeltaMovement(0,0,0);flight.onFocusRightClick(empty,level,player);check(player.getDeltaMovement().lengthSqr()==0,"no free flight");
        var tele=ThaumicTinkererModern.FOCUS_TELEKINESIS.get();FociContent.setFocus(wand,new ItemStack(tele));
        var item=new ItemEntity(level,8,271,14,new ItemStack(Items.DIAMOND));level.addFreshEntity(item);air=WandData.get(wand,Aspect.AIR);int entropy=WandData.get(wand,Aspect.ENTROPY);
        tele.onUsingFocusTick(wand,player,100);check(item.getDeltaMovement().length()>0&&item.getDeltaMovement().length()<=.33331,"telekinesis bounded motion");check(air-WandData.get(wand,Aspect.AIR)==5&&entropy-WandData.get(wand,Aspect.ENTROPY)==5,"telekinesis per-tick costs");item.discard();
        air=WandData.get(wand,Aspect.AIR);tele.onUsingFocusTick(wand,player,99);check(air==WandData.get(wand,Aspect.AIR),"no items no charge");
        check(tele.getPossibleUpgradesByRank(new ItemStack(tele),1).length==0,"telekinesis no upgrades");
        var deflect=ThaumicTinkererModern.FOCUS_DEFLECT.get();FociContent.setFocus(wand,new ItemStack(deflect));
        var arrow=new net.minecraft.world.entity.projectile.Arrow(net.minecraft.world.entity.EntityType.ARROW,level);arrow.setPos(10,270,8);arrow.setDeltaMovement(-1,0,0);level.addFreshEntity(arrow);
        var own=new net.minecraft.world.entity.projectile.Snowball(net.minecraft.world.entity.EntityType.SNOWBALL,level);own.setPos(8,270,10);own.setOwner(player);own.setDeltaMovement(0,0,-1);level.addFreshEntity(own);
        var bottle=new net.minecraft.world.entity.projectile.ThrownExperienceBottle(net.minecraft.world.entity.EntityType.EXPERIENCE_BOTTLE,level);bottle.setPos(8,270,9);bottle.setDeltaMovement(0,0,-1);level.addFreshEntity(bottle);
        var fireball=new net.minecraft.world.entity.projectile.SmallFireball(net.minecraft.world.entity.EntityType.SMALL_FIREBALL,level);fireball.setPos(9,270,8);fireball.setDeltaMovement(-1,0,0);level.addFreshEntity(fireball);
        var outside=new net.minecraft.world.entity.projectile.Arrow(net.minecraft.world.entity.EntityType.ARROW,level);outside.setPos(12,270,8);outside.setDeltaMovement(-1,0,0);level.addFreshEntity(outside);
        air=WandData.get(wand,Aspect.AIR);int order=WandData.get(wand,Aspect.ORDER);deflect.onUsingFocusTick(wand,player,100);
        check(Math.abs(arrow.getX()-6)<1e-6&&arrow.getDeltaMovement().x==-1&&arrow.getOwner()==null,"deflect translates incoming arrow without reversing or stealing ownership");
        check(own.getZ()==10&&bottle.getZ()==9&&fireball.getX()==9&&outside.getX()==12,"deflect excludes own projectiles, XP bottles, fireballs and outside bounds");
        check(air-WandData.get(wand,Aspect.AIR)==4&&order-WandData.get(wand,Aspect.ORDER)==8,"deflect per-tick charge");
        for(var projectile:java.util.List.of(arrow,own,bottle,fireball,outside))projectile.discard();
        order=WandData.get(wand,Aspect.ORDER);deflect.onUsingFocusTick(wand,player,99);check(order-WandData.get(wand,Aspect.ORDER)==8,"deflect idle ticks still cost vis");
        var denied=new net.minecraft.world.entity.projectile.Arrow(net.minecraft.world.entity.EntityType.ARROW,level);denied.setPos(10,270,8);denied.setDeltaMovement(-1,0,0);level.addFreshEntity(denied);FociContent.setFocus(empty,new ItemStack(deflect));deflect.onUsingFocusTick(empty,player,98);check(denied.getX()==10,"unfunded deflect cannot move arrows");denied.discard();
        check(deflect.getPossibleUpgradesByRank(new ItemStack(deflect),1).length==1&&deflect.getPossibleUpgradesByRank(new ItemStack(deflect),1)[0]==2,"original treasure-only deflect upgrade");
        var deflectRecipe=InfusionItemRecipes.all().stream().filter(r->r.key().equals("FOCUS_DEFLECT")).findFirst().orElseThrow();check(deflectRecipe.central().get()==ThaumicTinkererModern.FOCUS_FLIGHT.get()&&deflectRecipe.components().get(2).get()==FocusResearch.item("paving_stone_warding")&&deflectRecipe.components().get(0).get()==FocusResearch.item("mirror_glass"),"deflect exact legacy metadata ingredients");
        var heal=ThaumicTinkererModern.FOCUS_HEAL.get();FociContent.setFocus(wand,new ItemStack(heal));player.setHealth(10);int earth=WandData.get(wand,Aspect.EARTH),water=WandData.get(wand,Aspect.WATER);
        for(int i=0;i<29;i++)heal.onUsingFocusTick(wand,player,100-i);check(player.getHealth()==10&&WandData.get(wand,Aspect.EARTH)==earth,"heal 29 tick warmup without spending vis");
        heal.onUsingFocusTick(wand,player,70);check(player.getHealth()==11&&earth-WandData.get(wand,Aspect.EARTH)==45&&water-WandData.get(wand,Aspect.WATER)==45,"heal one HP and both aspects once on tick30");
        player.setHealth(player.getMaxHealth());earth=WandData.get(wand,Aspect.EARTH);for(int i=0;i<60;i++)heal.onUsingFocusTick(wand,player,100-i);check(WandData.get(wand,Aspect.EARTH)==earth,"full health costs no vis");
        player.setHealth(10);for(int i=0;i<20;i++)heal.onUsingFocusTick(wand,player,100-i);heal.onPlayerStoppedUsingFocus(wand,level,player,0);for(int i=0;i<29;i++)heal.onUsingFocusTick(wand,player,100-i);check(player.getHealth()==10,"release resets heal progress");heal.onUsingFocusTick(wand,player,70);check(player.getHealth()==11,"new hold heals after full delay");
        heal.onPlayerStoppedUsingFocus(wand,level,player,0);FociContent.setFocus(empty,new ItemStack(heal));for(int i=0;i<40;i++)heal.onUsingFocusTick(empty,player,100-i);check(player.getHealth()==11,"empty wand cannot heal or build progress");
        var upgradedHeal=new ItemStack(heal);for(int rank=1;rank<=3;rank++)check(heal.applyUpgrade(upgradedHeal,0,rank),"install potency rank "+rank);FociContent.setFocus(wand,upgradedHeal);player.setHealth(10);for(int i=0;i<19;i++)heal.onUsingFocusTick(wand,player,100-i);check(player.getHealth()==10,"potencyIII 19 tick warmup");heal.onUsingFocusTick(wand,player,80);check(player.getHealth()==11,"potencyIII heals on tick20");heal.onPlayerStoppedUsingFocus(wand,level,player,0);player.setHealth(player.getMaxHealth());
        var healRecipe=InfusionItemRecipes.all().stream().filter(r->r.key().equals("FOCUS_HEAL")).findFirst().orElseThrow();check(healRecipe.central().get()==FocusResearch.item("focus_pech")&&healRecipe.instability()==4&&healRecipe.components().get(0).get()==Items.GOLDEN_CARROT&&healRecipe.components().subList(1,4).stream().allMatch(c->c.get()==Items.GOLD_NUGGET),"heal original pech/golden carrot/three nuggets recipe");
        var ender=ThaumicTinkererModern.FOCUS_ENDER_CHEST.get();
        var chestPlayer=new net.neoforged.neoforge.common.util.FakePlayer(level,new com.mojang.authlib.GameProfile(java.util.UUID.fromString("0ab26809-5895-45ca-8472-63a75e416c25"),"TTEnderAudit")){
            @Override public java.util.OptionalInt openMenu(net.minecraft.world.MenuProvider provider){containerMenu=provider.createMenu(42,getInventory(),this);return java.util.OptionalInt.of(42);}
        };
        var chestWand=new ItemStack(TC4Modern.GOLD_GREATWOOD_WAND.get());WandData.add(chestWand,Aspect.ORDER,500,true);WandData.add(chestWand,Aspect.ENTROPY,500,true);FociContent.setFocus(chestWand,new ItemStack(ender));chestPlayer.getEnderChestInventory().setItem(0,new ItemStack(Items.COAL,23));
        order=WandData.get(chestWand,Aspect.ORDER);entropy=WandData.get(chestWand,Aspect.ENTROPY);ender.onFocusRightClick(chestWand,level,chestPlayer);
        check(chestPlayer.containerMenu instanceof net.minecraft.world.inventory.ChestMenu&&chestPlayer.containerMenu.slots.size()==63&&chestPlayer.containerMenu.getSlot(0).getItem().getCount()==23,"ender opens own27slot storage");
        check(order-WandData.get(chestWand,Aspect.ORDER)==100&&entropy-WandData.get(chestWand,Aspect.ENTROPY)==100&&!FociContent.usingFocus(chestPlayer),"ender charges once without starting held focus");
        chestPlayer.containerMenu.quickMoveStack(chestPlayer,0);check(chestPlayer.getEnderChestInventory().getItem(0).isEmpty()&&chestPlayer.getInventory().countItem(Items.COAL)==23,"ender menu transfer conserves personal items");chestPlayer.containerMenu.removed(chestPlayer);
        chestPlayer.containerMenu=chestPlayer.inventoryMenu;FociContent.setFocus(empty,new ItemStack(ender));ender.onFocusRightClick(empty,level,chestPlayer);check(chestPlayer.containerMenu==chestPlayer.inventoryMenu,"empty wand cannot open ender menu");chestPlayer.getInventory().clearContent();
        var enderRecipe=ArcaneRecipes.entries().stream().filter(r->r.key().equals("FOCUS_ENDER_CHEST")).findFirst().orElseThrow();check(enderRecipe.width()==1&&enderRecipe.height()==3&&enderRecipe.ingredients().get(0).test(new ItemStack(FocusResearch.item("mirror")))&&enderRecipe.ingredients().get(1).test(new ItemStack(Items.ENDER_CHEST))&&enderRecipe.ingredients().get(2).test(new ItemStack(FocusResearch.item("focus_portable_hole")))&&ResearchWarp.amount("FOCUS_ENDER_CHEST")==1,"ender vertical recipe and warp");
        var smelt=ThaumicTinkererModern.FOCUS_SMELT.get();FociContent.setFocus(wand,new ItemStack(smelt));var pos=new BlockPos(8,270,10);level.removeBlock(pos,false);level.setBlockAndUpdate(pos,Blocks.SAND.defaultBlockState());
        int fire=WandData.get(wand,Aspect.FIRE);entropy=WandData.get(wand,Aspect.ENTROPY);
        for(int i=0;i<20;i++)check(!smelt.smelt(wand,player,pos),"smelt warmup "+i);check(level.getBlockState(pos).is(Blocks.SAND),"input remains during warmup");
        check(smelt.smelt(wand,player,pos)&&level.getBlockState(pos).is(Blocks.GLASS),"sand turns into glass");check(fire-WandData.get(wand,Aspect.FIRE)==45&&entropy-WandData.get(wand,Aspect.ENTROPY)==12,"smelt cost once");
        level.setBlockAndUpdate(pos,Blocks.CHEST.defaultBlockState());check(!smelt.smelt(wand,player,pos),"block entity never replaced");level.removeBlock(pos,false);
        FociContent.setFocus(wand,new ItemStack(tele));check(FociContent.getFocus(wand)==tele,"base wand recognizes addon focus");
        tele.onFocusRightClick(wand,level,player);check(FociContent.usingFocus(player),"continuous use activated");FociContent.release(wand,level,player,0);check(!FociContent.usingFocus(player),"continuous use released");
        player.getInventory().clearContent();
        for(var recipe:InfusionItemRecipes.all())check(recipe.central().get()!=Items.AIR&&recipe.components().stream().allMatch(s->s.get()!=Items.AIR),"infusion ingredient registry "+recipe.key());
        for(var recipe:ArcaneRecipes.entries())if(recipe.key().startsWith("FOCUS_"))check(recipe.ingredients().stream().filter(i->!i.isEmpty()).allMatch(i->i.getItems().length>0),"arcane ingredient registry "+recipe.key());
        System.out.println("TT FOCUS AUDIT: ALL PASS (base wand integration, centivis costs, flight, telekinesis, deflect, healing, ender menu/item conservation, smelt delay, unsafe targets)");
    }
}
