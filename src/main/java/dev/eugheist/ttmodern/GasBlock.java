package dev.eugheist.ttmodern;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
/** Original six-direction spreading gas, limited to four propagation steps. */
public final class GasBlock extends Block {
    public static final IntegerProperty POWER=IntegerProperty.create("power",0,4);
    private final boolean shadow;
    public GasBlock(boolean shadow) {
        super(Properties.of().replaceable().noCollission().noOcclusion().noLootTable().lightLevel(s -> shadow?0:12));
        this.shadow=shadow;registerDefaultState(stateDefinition.any().setValue(POWER,0));
    }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(POWER);}
    @Override protected net.minecraft.world.phys.shapes.VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,net.minecraft.world.phys.shapes.CollisionContext context){return net.minecraft.world.phys.shapes.Shapes.empty();}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.INVISIBLE;}
    @Override protected int getLightBlock(BlockState state,BlockGetter level,BlockPos pos){return shadow?15:0;}
    @Override protected void onPlace(BlockState state,Level level,BlockPos pos,BlockState old,boolean moving){if(!level.isClientSide)level.scheduleTick(pos,this,10);}
    @Override protected void tick(BlockState state,ServerLevel level,BlockPos pos,RandomSource random){
        int power=state.getValue(POWER);if(power==0)return;
        for(var direction:Direction.values()) {
            var next=pos.relative(direction); if(!level.hasChunkAt(next))continue;
            var old=level.getBlockState(next);
            if((old.isAir() || old.getBlock() instanceof GasBlock || old.getBlock() instanceof NitorLightBlock)&&!old.is(this))level.setBlock(next,defaultBlockState().setValue(POWER,power-1),2);
        }
        level.setBlock(pos,state.setValue(POWER,0),2);
    }
}
