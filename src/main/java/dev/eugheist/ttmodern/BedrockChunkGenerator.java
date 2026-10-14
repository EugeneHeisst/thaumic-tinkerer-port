package dev.eugheist.ttmodern;
@net.neoforged.fml.common.EventBusSubscriber(modid="ttinkerer",bus=net.neoforged.fml.common.EventBusSubscriber.Bus.MOD)
public final class BedrockChunkGenerator extends net.minecraft.world.level.levelgen.FlatLevelSource {
 public static final com.mojang.serialization.MapCodec<BedrockChunkGenerator> CODEC=com.mojang.serialization.codecs.RecordCodecBuilder.mapCodec(i->i.group(net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings.CODEC.fieldOf("settings").forGetter(BedrockChunkGenerator::settings)).apply(i,BedrockChunkGenerator::new));
 public BedrockChunkGenerator(net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings settings){super(settings);}
 @Override protected com.mojang.serialization.MapCodec<? extends net.minecraft.world.level.chunk.ChunkGenerator> codec(){return CODEC;}
 @net.neoforged.bus.api.SubscribeEvent static void register(net.neoforged.neoforge.registries.RegisterEvent event){event.register(net.minecraft.core.registries.Registries.CHUNK_GENERATOR,h->h.register(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("ttinkerer","bedrock"),CODEC));}
 @Override public void applyBiomeDecoration(net.minecraft.world.level.WorldGenLevel world,net.minecraft.world.level.chunk.ChunkAccess chunk,net.minecraft.world.level.StructureManager structures){var pos=chunk.getPos();populate(world,pos.x,pos.z);}
 static void populate(net.minecraft.world.level.WorldGenLevel world,int cx,int cz){var random=new java.util.Random(world.getSeed());long xs=random.nextLong()/2*2+1,zs=random.nextLong()/2*2+1;random.setSeed((cx*xs+cz*zs)^world.getSeed());random.nextInt(16);random.nextInt(128);random.nextInt(16);var entries=BedrockOres.entries(TinkererConfig.oreBlacklist());for(int cluster=0;cluster<TinkererConfig.oreDensity();cluster++){int x=16*cx+random.nextInt(16),z=16*cz+random.nextInt(16);var ore=BedrockOres.choose(random,entries).defaultBlockState();for(int i=0;i<200;i++){int y=random.nextInt(245)+6,size=random.nextInt(20);BedrockVeins.generate(world,random,x,y,z,size,ore);}}}
 @Override public int getSpawnHeight(net.minecraft.world.level.LevelHeightAccessor level){return 5;}
}
