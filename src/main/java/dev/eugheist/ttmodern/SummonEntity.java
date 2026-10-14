package dev.eugheist.ttmodern;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import dev.eugheist.tc4modern.InfusionContent.PedestalEntity;
public final class SummonEntity extends BlockEntity{
    long lastCheck=Long.MIN_VALUE;
    static final Map<String,String> RECIPES=recipes();
    private static Map<String,String> recipes(){var recipes=new LinkedHashMap<String,String>();for(var id:MobAspects.SUMMON_ORDER)recipes.putIfAbsent(signature(MobAspects.MOBS.get(id)),id);return Map.copyOf(recipes);}
    static String signature(List<thaumcraft.api.aspects.Aspect> aspects){return aspects.stream().map(thaumcraft.api.aspects.Aspect::getTag).sorted().collect(java.util.stream.Collectors.joining(","));}
    public SummonEntity(BlockPos pos,BlockState state){super(ThaumicTinkererModern.SUMMON_ENTITY.get(),pos,state);}
    static boolean matchesLast(ItemStack stack,Level level,BlockPos pos){var tag=stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();return !tag.contains("LastTablet")||tag.getLong("LastTablet")==pos.asLong()&&tag.getString("LastDimension").equals(level.dimension().location().toString());}
    static void mark(ItemStack stack,Level level,BlockPos pos){CustomData.update(DataComponents.CUSTOM_DATA,stack,tag->{tag.putLong("LastTablet",pos.asLong());tag.putString("LastDimension",level.dimension().location().toString());});}
    public static void tick(Level level,BlockPos pos,BlockState state,SummonEntity tile){
        if(!(level instanceof ServerLevel server))return;long time=level.getGameTime();if(time%300!=0||time==tile.lastCheck)return;tile.lastCheck=time;tile.setChanged();if(level.hasNeighborSignal(pos))return;
        for(int radius=1;radius<6;radius++){
            var pedestals=new ArrayList<PedestalEntity>();
            for(int x=-radius;x<radius;x++)for(int z=-radius;z<radius;z++){var at=pos.offset(x,0,z);if(level.hasChunkAt(at)&&level.getBlockEntity(at) instanceof PedestalEntity pedestal&&pedestal.item().getItem() instanceof MobAspectItem)pedestals.add(pedestal);}
            for(int i=0;i<pedestals.size();i++)for(int j=i+1;j<pedestals.size();j++)for(int k=j+1;k<pedestals.size();k++){
                var selected=List.of(pedestals.get(i),pedestals.get(j),pedestals.get(k));var aspects=selected.stream().map(p->((MobAspectItem)p.item().getItem()).aspect).toList();String id=RECIPES.get(signature(aspects));if(id==null)continue;
                boolean infused=selected.stream().allMatch(p->((MobAspectItem)p.item().getItem()).form==2);if(infused&&time%1200!=0)return;
                boolean same=selected.stream().allMatch(p->matchesLast(p.item(),level,pos));
                if(!infused||same){
                    if(id.equals("minecraft:skeleton")&&level.dimension()==Level.NETHER)id="minecraft:wither_skeleton";
                    var type=net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.get(net.minecraft.resources.ResourceLocation.parse(id));var entity=type.create(level);if(!(entity instanceof Mob mob))return;
                    mob.moveTo(pos.getX()+.5,pos.getY()+1,pos.getZ()+.5,0,0);mob.finalizeSpawn(server,server.getCurrentDifficultyAt(pos),MobSpawnType.MOB_SUMMONED,null);mob.setPersistenceRequired();
                    if(!server.addFreshEntity(mob))return;
                    // Original consumes whole non-infused pedestal stacks, rather than one item each.
                    if(!infused)selected.forEach(p->p.setItem(ItemStack.EMPTY));
                    for(int n=0;n<3;n++){var at=selected.get(n).getBlockPos();net.neoforged.neoforge.network.PacketDistributor.sendToPlayersNear(server,null,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,64,new dev.eugheist.tc4modern.InfusionContent.FxPayload(164,at.getX(),at.getY(),at.getZ(),pos.getX(),pos.getY(),pos.getZ(),aspects.get(n).getColor()));}
                }
                if(infused)selected.forEach(p->{mark(p.item(),level,pos);p.setItem(p.item());});
                return;
            }
        }
    }
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider registries){super.saveAdditional(tag,registries);tag.putLong("LastCheck",lastCheck);}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider registries){super.loadAdditional(tag,registries);lastCheck=tag.contains("LastCheck")?tag.getLong("LastCheck"):Long.MIN_VALUE;}
}
