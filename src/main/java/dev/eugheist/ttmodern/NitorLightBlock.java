package dev.eugheist.ttmodern;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
public final class NitorLightBlock extends Block {
    public static final net.minecraft.world.level.block.state.properties.BooleanProperty AWAKENED=net.minecraft.world.level.block.state.properties.BooleanProperty.create("awakened");
    public NitorLightBlock(){super(Properties.of().replaceable().noCollission().noOcclusion().noLootTable().lightLevel(s->s.getValue(AWAKENED)?15:12));registerDefaultState(stateDefinition.any().setValue(AWAKENED,false));}
    @Override protected void createBlockStateDefinition(net.minecraft.world.level.block.state.StateDefinition.Builder<Block,BlockState> builder){builder.add(AWAKENED);}
    @Override protected net.minecraft.world.phys.shapes.VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,net.minecraft.world.phys.shapes.CollisionContext context){return net.minecraft.world.phys.shapes.Shapes.empty();}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.INVISIBLE;}
    @Override protected void onPlace(BlockState state,Level level,BlockPos pos,BlockState old,boolean moving){if(!level.isClientSide)level.scheduleTick(pos,this,delay(level));}
    @Override public void animateTick(BlockState state,Level level,BlockPos pos,RandomSource random){if(random.nextFloat()<.03f)dev.eugheist.tc4modern.FociFx.sparkle(pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,1f,4,random.nextFloat()/2f);}
    private static int delay(Level level){return level.dimension()==Level.NETHER?60:20;}
    @Override protected void tick(BlockState state,ServerLevel level,BlockPos pos,RandomSource random) {
        boolean owner=false;
        for(var player:level.getEntitiesOfClass(Player.class,new AABB(pos.getX()-(state.getValue(AWAKENED)?6:1),pos.getY()-(state.getValue(AWAKENED)?6:1),pos.getZ()-(state.getValue(AWAKENED)?6:1),pos.getX()+(state.getValue(AWAKENED)?6:1),pos.getY()+(state.getValue(AWAKENED)?6:1),pos.getZ()+(state.getValue(AWAKENED)?6:1)))) {
            if(player.getInventory().items.stream().anyMatch(stack->stack.is(ThaumicTinkererModern.BRIGHT_NITOR.get()))||player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.LEGS).getItem() instanceof AwakenedLegs){owner=true;break;}
        }
        if(!owner)level.removeBlock(pos,false);else level.scheduleTick(pos,this,delay(level));
    }
}
