package dev.eugheist.ttmodern;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import static dev.eugheist.ttmodern.InfusedFireTransformations.Kind;

/** Build 164 fire algorithm, with target coordinates represented consistently by BlockPos. */
public final class InfusedFireBlock extends FireBlock {
 final Kind kind;
 InfusedFireBlock(Kind kind){super(Properties.ofFullCopy(Blocks.FIRE).noLootTable());this.kind=kind;}
 @Override public MapCodec<FireBlock> codec(){return simpleCodec(properties->new InfusedFireBlock(kind));}
 int delay(){return kind==Kind.ENTROPY?1:200;}
 BlockState transformation(ServerLevel level,BlockPos pos){
  if(!loaded(level,pos))return null;
  var state=level.getBlockState(pos);
  if(kind==Kind.ORDER)return OrderFireResolver.result(level,pos);
  if(kind==Kind.ENTROPY)return state.getBlock() instanceof InfusedFireBlock fire&&fire.kind!=Kind.ENTROPY?Blocks.FIRE.defaultBlockState():null;
  return InfusedFireTransformations.fixed(kind,state,TinkererConfig.cakeEnabled());
 }
 private static boolean loaded(LevelReader level,BlockPos pos){return !level.isOutsideBuildHeight(pos)&&level.hasChunkAt(pos);}
 boolean adjacentTarget(ServerLevel level,BlockPos pos){for(var d:Direction.values())if(transformation(level,pos.relative(d))!=null)return true;return false;}
 private boolean wet(ServerLevel level,BlockPos pos){if(loaded(level,pos)&&level.isRainingAt(pos))return true;for(var d:Direction.Plane.HORIZONTAL)if(loaded(level,pos.relative(d))&&level.isRainingAt(pos.relative(d)))return true;return false;}
 @Override protected boolean canSurvive(BlockState state,LevelReader level,BlockPos pos){return true;}
 private boolean supported(LevelReader level,BlockPos pos){
  if(loaded(level,pos.below())&&level.getBlockState(pos.below()).isFaceSturdy(level,pos.below(),Direction.UP))return true;
  return level instanceof ServerLevel server&&adjacentTarget(server,pos);
 }
 @Override public BlockState getStateForPlacement(BlockPlaceContext context){return visualState(context.getLevel(),context.getClickedPos(),0);}
 private BlockState visualState(BlockGetter level,BlockPos pos,int age){
  var state=defaultBlockState().setValue(AGE,age);
  if(level instanceof LevelReader reader&&!loaded(reader,pos.below()))return state;
  if(level.getBlockState(pos.below()).isFaceSturdy(level,pos.below(),Direction.UP))return state;
  for(var d:Direction.values())if(d!=Direction.DOWN){var p=pos.relative(d);if(level instanceof LevelReader reader&&!loaded(reader,p))continue;var target=level.getBlockState(p);boolean burn=level instanceof ServerLevel server?transformation(server,p)!=null:target.getFlammability(level,p,d)>0;
   state=state.setValue(switch(d){case NORTH->NORTH;case SOUTH->SOUTH;case WEST->WEST;case EAST->EAST;default->UP;},burn);
  }return state;
 }
 @Override protected BlockState updateShape(BlockState state,Direction direction,BlockState neighbor,LevelAccessor level,BlockPos pos,BlockPos neighborPos){
  // Client receives the authoritative state; do not remove server-supported fire locally.
  if(level instanceof ServerLevel server)return supported(server,pos)?visualState(server,pos,state.getValue(AGE)):Blocks.AIR.defaultBlockState();
  return state;
 }
 @Override protected void onPlace(BlockState state,Level level,BlockPos pos,BlockState old,boolean moving){
  if(!old.is(this)&&!level.isClientSide)level.scheduleTick(pos,this,delay()+level.random.nextInt(10));
 }
 private int decay(ServerLevel level,BlockPos pos){return kind==Kind.ORDER?OrderFireResolver.decayChance(level,pos):1;}
 /** No x/z/y convention: every branch resolves and writes this exact target. */
 void convertOrPlace(ServerLevel level,BlockPos pos,int age,Block fallback,RandomSource random){
  if(!loaded(level,pos))return;
  var result=transformation(level,pos);
  if(result!=null&&random.nextInt(decay(level,pos))==0)level.setBlock(pos,result,3);
  else level.setBlock(pos,fallback==this?visualState(level,pos,age):fallback.defaultBlockState(),3);
 }
 private int flammability(ServerLevel level,BlockPos pos,Direction face){
  if(!loaded(level,pos))return 0;
  if(transformation(level,pos)!=null)return 100;
  // Transformation products resist the same fire even if normally flammable.
  var state=level.getBlockState(pos);
  if(isResult(level,state))return 0;
  return state.getFlammability(level,pos,face);
 }
 private boolean isResult(ServerLevel level,BlockState state){
  if(kind==Kind.ORDER)return OrderFireResolver.isResult(level,state);
  if(kind==Kind.ENTROPY)return state.is(Blocks.FIRE);
  return InfusedFireTransformations.isResult(kind,state,TinkererConfig.cakeEnabled());
 }
 private void catchFire(ServerLevel level,BlockPos pos,int strength,int age,Direction face,RandomSource random){
  if(random.nextInt(strength)>=flammability(level,pos,face))return;
  var old=level.getBlockState(pos);
  if(old.is(Blocks.TNT))old.onCaughtFire(level,pos,face,null);
  if(random.nextInt(age+10)<5&&!level.isRainingAt(pos))convertOrPlace(level,pos,Math.min(15,age+random.nextInt(5)/4),this,random);
  else convertOrPlace(level,pos,0,Blocks.AIR,random);
 }
 @Override public void animateTick(BlockState state,Level level,BlockPos pos,RandomSource random){
  if(random.nextInt(24)==0)level.playLocalSound(pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,net.minecraft.sounds.SoundEvents.FIRE_AMBIENT,net.minecraft.sounds.SoundSource.BLOCKS,1+random.nextFloat(),random.nextFloat()*.7f+.3f,false);
  InfusedFireSmoke.emit(level,pos,random,p->level.addParticle(net.minecraft.core.particles.ParticleTypes.LARGE_SMOKE,p.x,p.y,p.z,0,0,0));
 }
 @Override protected void tick(BlockState state,ServerLevel level,BlockPos pos,RandomSource random){
  if(!level.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK)||!TinkererConfig.fireEnabled())return;
  if(wet(level,pos))level.removeBlock(pos,false);
  else {
   if(!adjacentTarget(level,pos))level.removeBlock(pos,false);
   if(random.nextInt(20)==0&&adjacentTarget(level,pos))for(var d:Direction.values()){
    var target=pos.relative(d);var result=transformation(level,target);if(result==null)continue;
    if(random.nextInt(decay(level,target))==0)level.setBlock(target,result,3);else level.removeBlock(target,false);
   }
  }
  int age=level.getBlockState(pos).is(this)?level.getBlockState(pos).getValue(AGE):0;
  if(age<15&&level.getBlockState(pos).is(this))level.setBlock(pos,visualState(level,pos,age+random.nextInt(3)/2),4);
  level.scheduleTick(pos,this,delay()+random.nextInt(3));
  boolean humid=level.getBiome(pos).is(BiomeTags.INCREASED_FIRE_BURNOUT);
  for(var d:Direction.values())catchFire(level,pos.relative(d),humid?250:300,age,d,random);
  for(int x=-1;x<=1;x++)for(int z=-1;z<=1;z++)for(int y=-1;y<=2;y++){
   if(x==0&&y==0&&z==0)continue;var next=pos.offset(x,y,z);
   if(!loaded(level,next)||!level.isEmptyBlock(next)||!adjacentTarget(level,next))continue;
   int chance=(100+70)/(age+30)+70;if(humid)chance/=2;
   if(chance<=0||random.nextInt(100)>chance||wet(level,next))continue;
   convertOrPlace(level,next,Math.min(15,age+random.nextInt(5)/4),this,random);
  }
 }
}
