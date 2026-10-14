package dev.eugheist.ttmodern.mixin;
/** A saved level.dat can reintroduce a dimension even when its datapack condition is false. */
@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.level.levelgen.WorldDimensions.class)
abstract class BedrockDimensionsMixin {
 @org.spongepowered.asm.mixin.injection.ModifyArg(method="bake",at=@org.spongepowered.asm.mixin.injection.At(value="INVOKE",target="Lnet/minecraft/world/level/levelgen/WorldDimensions;keysInOrder(Ljava/util/stream/Stream;)Ljava/util/stream/Stream;"),index=0)
 private java.util.stream.Stream<net.minecraft.resources.ResourceKey<net.minecraft.world.level.dimension.LevelStem>> withoutDisabledBedrock(java.util.stream.Stream<net.minecraft.resources.ResourceKey<net.minecraft.world.level.dimension.LevelStem>> keys){return dev.eugheist.ttmodern.BedrockDimensionCondition.enabled()?keys:keys.filter(key->!key.location().equals(dev.eugheist.ttmodern.BedrockPortal.DIMENSION.location()));}
}
