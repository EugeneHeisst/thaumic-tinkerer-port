package dev.eugheist.ttmodern;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
/** Build 164's collisionless full-block portal; no item or crafting recipe. */
@net.neoforged.fml.common.EventBusSubscriber(modid="ttinkerer")
public final class BedrockPortal extends Block {
 static final net.neoforged.neoforge.registries.DeferredBlock<BedrockPortal> BLOCK=KamiRegistries.BLOCKS.register("bedrock_portal",BedrockPortal::new);
 public static final net.minecraft.resources.ResourceKey<Level> DIMENSION=net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION,net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("ttinkerer","bedrock"));
 public BedrockPortal(){super(Properties.of().strength(-1,6000000).sound(SoundType.STONE).noCollission().noOcclusion().noLootTable().isValidSpawn((s,l,p,t)->false));}
 static void init(){}
 static boolean bedrock(Level level){return level.dimension().equals(DIMENSION);}
 static boolean surface(Level level){return level.dimensionType().natural()&&!level.dimensionType().ultraWarm();}
 public static boolean open(net.minecraft.server.level.ServerPlayer player,BlockPos pos){var level=player.serverLevel();if(!TinkererConfig.bedrockEnabled()||!level.getBlockState(pos).is(Blocks.BEDROCK)||!level.mayInteract(player,pos)||player.isSpectator()||player.blockActionRestricted(level,pos,player.gameMode.getGameModeForPlayer()))return false;if(surface(level)&&pos.getY()<5||bedrock(level)&&pos.getY()>253)return level.setBlockAndUpdate(pos,BLOCK.get().defaultBlockState());if(bedrock(level)&&pos.getY()<=253)return level.setBlockAndUpdate(pos,Blocks.AIR.defaultBlockState());return false;}
 @net.neoforged.bus.api.SubscribeEvent(priority=net.neoforged.bus.api.EventPriority.LOWEST)
 public static void click(net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.LeftClickBlock event){if(event.getAction()!=net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.LeftClickBlock.Action.START||event.getUseItem()==net.neoforged.neoforge.common.util.TriState.FALSE||event.isCanceled()||event.getUseBlock()==net.neoforged.neoforge.common.util.TriState.FALSE||!(event.getEntity() instanceof net.minecraft.server.level.ServerPlayer player)||!(player.getMainHandItem().getItem() instanceof AwakenedPick)||!player.canInteractWithBlock(event.getPos(),1))return;open(player,event.getPos());}
 @Override protected net.minecraft.world.InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,net.minecraft.world.entity.player.Player player,net.minecraft.world.phys.BlockHitResult hit){if(!level.isClientSide&&level.mayInteract(player,pos))level.setBlockAndUpdate(pos,Blocks.BEDROCK.defaultBlockState());return net.minecraft.world.InteractionResult.PASS;}
 @Override protected void entityInside(BlockState state,Level level,BlockPos pos,net.minecraft.world.entity.Entity entity){if(entity instanceof net.minecraft.server.level.ServerPlayer player)travel(player,pos);}
 static boolean travel(net.minecraft.server.level.ServerPlayer player,BlockPos pos){var source=player.serverLevel();if(!TinkererConfig.bedrockEnabled()||!source.getBlockState(pos).is(BLOCK.get())||player.isPassenger()||player.isVehicle()||!player.isAlive())return false;if(surface(source)){var destination=player.server.getLevel(DIMENSION);if(destination==null)return false;chamber(destination,pos);player.teleportTo(destination,pos.getX()+.5,251,pos.getZ()+.5,0,0);return true;}if(bedrock(source)){var destination=player.server.overworld();var landing=returnPosition(destination,player.getX(),player.getZ(),new java.util.Random());if(landing==null)return false;player.teleportTo(destination,landing.getX()+.5,landing.getY(),landing.getZ()+.5,0,0);return true;}return false;}
 static void chamber(net.minecraft.server.level.ServerLevel level,BlockPos portal){for(int y=250;y<=254;y++){var at=new BlockPos(portal.getX(),y,portal.getZ());if(level.getBlockState(at).is(Blocks.BEDROCK))level.setBlockAndUpdate(at,y==254?BLOCK.get().defaultBlockState():Blocks.AIR.defaultBlockState());}}
 static BlockPos returnPosition(net.minecraft.server.level.ServerLevel level,double px,double pz,java.util.Random random){int x=(int)px+random.nextInt(100)-50,z=(int)pz+random.nextInt(100)-50;int y=120;while(y>=level.getMinBuildHeight()&&level.getBlockState(new BlockPos(x,y,z)).isAir())y--;return y<level.getMinBuildHeight()?null:new BlockPos(x,y+3,z);}
 @Override public boolean canEntityDestroy(BlockState state,net.minecraft.world.level.BlockGetter level,BlockPos pos,net.minecraft.world.entity.Entity entity){return false;}
}
