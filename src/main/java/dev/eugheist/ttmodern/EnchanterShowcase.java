package dev.eugheist.ttmodern;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

/** Visible demonstration (-Dttinkerer.enchanterShow): a correct osmotic enchanter next to one whose pillars stand on obsidian tile. */
@EventBusSubscriber(modid="ttinkerer")
public final class EnchanterShowcase {
    static final BlockPos GOOD=new BlockPos(0,100,0),BAD=new BlockPos(12,100,0);
    static final int[][] PILLARS={{-2,-2},{-2,2},{0,-3},{0,3},{2,-2},{2,2}};
    static boolean enabled(){return Boolean.getBoolean("ttinkerer.enchanterShow");}
    static void say(ServerPlayer p,String text){p.sendSystemMessage(Component.literal(text));System.out.println("TT SHOW: "+text);}

    @SubscribeEvent public static void join(net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent event){
        if(!enabled()||!(event.getEntity() instanceof ServerPlayer p))return;
        var level=p.serverLevel();var server=level.getServer();
        for(var e:EnchanterCosts.ENTRIES.values())server.getCommands().performPrefixedCommand(server.createCommandSourceStack().withEntity(p).withPermission(4).withSuppressedOutput(),"tc4dev research "+e.research());
        server.getCommands().performPrefixedCommand(server.createCommandSourceStack().withEntity(p).withPermission(4).withSuppressedOutput(),"tc4dev research ENCHANTER");
        level.setDayTime(6000);
        for(int x=-6;x<=18;x++)for(int z=-7;z<=7;z++)for(int y=0;y<=8;y++)level.setBlockAndUpdate(GOOD.offset(x,y,z),y==0?Blocks.AIR.defaultBlockState():Blocks.AIR.defaultBlockState());
        for(int x=-6;x<=18;x++)for(int z=-7;z<=7;z++)level.setBlockAndUpdate(GOOD.offset(x,-1,z),Blocks.SMOOTH_STONE.defaultBlockState());
        build(level,GOOD,false);build(level,BAD,true);
        p.setGameMode(net.minecraft.world.level.GameType.CREATIVE);p.getInventory().clearContent();p.getAbilities().flying=true;p.onUpdateAbilities();
        p.connection.teleport(GOOD.getX()+6.5,GOOD.getY()+7,GOOD.getZ()-11,0,30);
        say(p,"§eСлева — правильный осмотический зачарователь: 6 столбов из обсидиановых тотемов стоят на одном уровне со столом, сверху нитор.");
        say(p,"§eСправа — как у тебя: внизу каждого столба обсидиановая плитка, тотемы начинаются выше.");
    }
    static void build(ServerLevel level,BlockPos center,boolean tileBase){
        level.setBlockAndUpdate(center,ThaumicTinkererModern.ENCHANTER.get().defaultBlockState());
        var totem=BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath("tc4modern","obsidian_totem")).defaultBlockState();
        var tile=BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath("tc4modern","obsidian_tile")).defaultBlockState();
        for(var o:PILLARS){var base=center.offset(o[0],0,o[1]);int h=0;
            if(tileBase)level.setBlockAndUpdate(base.above(h++),tile);
            for(int i=0;i<3;i++)level.setBlockAndUpdate(base.above(h++),totem);
            level.setBlockAndUpdate(base.above(h),dev.eugheist.tc4modern.TC4Modern.NITOR_BLOCK.get().defaultBlockState());}
        var tileEntity=(EnchanterEntity)level.getBlockEntity(center);
        tileEntity.inventory.setStackInSlot(0,new ItemStack(Items.DIAMOND_SWORD));
        var wand=new ItemStack(dev.eugheist.tc4modern.TC4Modern.GOLD_GREATWOOD_WAND.get());for(var a:thaumcraft.api.aspects.Aspect.getPrimalAspects())dev.eugheist.tc4modern.WandData.add(wand,a,dev.eugheist.tc4modern.WandData.capacity(wand),true);
        tileEntity.inventory.setStackInSlot(1,wand);
    }

    @EventBusSubscriber(modid="ttinkerer",value=net.neoforged.api.distmarker.Dist.CLIENT)
    public static final class Client {
        static int ticks,doneAt=-1;
        @SubscribeEvent public static void tick(net.neoforged.neoforge.client.event.ClientTickEvent.Post event){
            if(!enabled())return;var mc=net.minecraft.client.Minecraft.getInstance();
            if(mc.player==null||mc.level==null||mc.screen instanceof net.minecraft.client.gui.screens.ReceivingLevelScreen)return;ticks++;
            var server=mc.getSingleplayerServer();var uuid=mc.player.getUUID();
            if(ticks==100)server.execute(()->{var p=server.getPlayerList().getPlayer(uuid);say(p,"§bОткрываю левый стол, выбираю «Остроту» и жму красную кнопку.");var t=(EnchanterEntity)p.level().getBlockEntity(GOOD);
                p.openMenu(new net.minecraft.world.SimpleMenuProvider((id,inv,pl)->new EnchanterMenu(id,inv,t),Component.translatable("block.ttinkerer.enchanter")),buf->buf.writeBlockPos(GOOD));});
            if(ticks==140&&mc.screen instanceof EnchanterScreen)EnchanterClientAudit.Client.click(mc,34,54);
            if(ticks==170&&mc.screen instanceof EnchanterScreen)EnchanterClientAudit.Client.click(mc,201,-1);
            if(ticks==200&&mc.screen instanceof EnchanterScreen)EnchanterClientAudit.Client.click(mc,201,-1);
            if(ticks==240&&mc.screen instanceof EnchanterScreen)EnchanterClientAudit.Client.click(mc,155,38);
            if(ticks==280){mc.setScreen(null);server.execute(()->{var p=server.getPlayerList().getPlayer(uuid);var bad=(EnchanterEntity)p.level().getBlockEntity(BAD);
                var research=EnchanterCosts.ENTRIES.values().stream().map(EnchanterCosts.Entry::research).collect(java.util.stream.Collectors.toSet());
                bad.process.change(p.serverLevel(),bad.inventory.getStackInSlot(0),research,"minecraft:sharpness",3);boolean started=bad.process.start();bad.setChanged();
                say(p,"§bПравый стол: то же самое — Острота III, запуск ("+(started?"запущен":"не запустился")+"). Смотри на столбы: у левого идут руны, у правого ничего.");});}
            if(ticks>280&&ticks%20==0&&doneAt<0)server.execute(()->{var p=server.getPlayerList().getPlayer(uuid);if(p==null)return;var level=p.serverLevel();
                var good=(EnchanterEntity)level.getBlockEntity(GOOD);var bad=(EnchanterEntity)level.getBlockEntity(BAD);
                var sword=good.inventory.getStackInSlot(0);
                if(!sword.isEnchanted())return;
                doneAt=ticks;
                say(p,"§aЛЕВЫЙ стол: готово! Меч получил "+sword.getTagEnchantments().entrySet().stream().map(e->e.getKey().value().description().getString()+" "+e.getIntValue()).toList()+".");
                say(p,"§cПРАВЫЙ стол (плитка внизу столбов): "+(bad.inventory.getStackInSlot(0).isEnchanted()?"тоже сработал":"не работает — работа сбросилась, меч без зачарования")+". Столбы должны начинаться тотемами прямо на уровне стола.");});
            if(doneAt>0&&ticks==doneAt+400)mc.stop();
            if(ticks==6000)mc.stop();
        }
    }
}
