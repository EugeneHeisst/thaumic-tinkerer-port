package dev.eugheist.ttmodern.mixin;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Accessor;
@Mixin(net.minecraft.world.entity.ai.goal.AvoidEntityGoal.class)
public interface CatAvoidGoalAccessor {
 @Accessor("avoidClass") Class<?> ttinkerer$avoidClass();
 @Mutable @Accessor("avoidClass") void ttinkerer$avoidClass(Class<?> type);
}
