package dev.eugheist.ttmodern.mixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
@Mixin(net.minecraft.world.entity.Mob.class)
public interface CatMobAccessor {
 @Accessor("goalSelector") net.minecraft.world.entity.ai.goal.GoalSelector ttinkerer$goals();
 @Accessor("targetSelector") net.minecraft.world.entity.ai.goal.GoalSelector ttinkerer$targets();
}
