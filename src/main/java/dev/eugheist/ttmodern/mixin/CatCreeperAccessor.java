package dev.eugheist.ttmodern.mixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
@Mixin(net.minecraft.world.entity.monster.Creeper.class)
public interface CatCreeperAccessor {
 @Accessor("swell") void ttinkerer$fuse(int value);
}
