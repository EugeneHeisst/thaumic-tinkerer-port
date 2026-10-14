package dev.eugheist.ttmodern.mixin;
import net.minecraft.world.entity.ExperienceOrb;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
@Mixin(ExperienceOrb.class)
public interface ExperienceOrbAccessor {
    @Accessor("count") int ttinkerer$count();
    @Accessor("count") void ttinkerer$count(int count);
}
