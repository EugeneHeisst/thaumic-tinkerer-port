package dev.eugheist.ttmodern.mixin;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/** Legacy EnderCrystal class is not LivingEntity: its nearest living target search always found none. */
@Mixin(net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal.class)
public abstract class CatTargetGoalMixin implements dev.eugheist.ttmodern.CatAmulet.TargetGoal {
 @Shadow @Final @Mutable protected Class<?> targetType;
 @Shadow protected net.minecraft.world.entity.LivingEntity target;
 public Class<?> ttinkerer$targetType(){return targetType;}
 public void ttinkerer$targetType(Class<?> type){targetType=type;}
 @Inject(method="findTarget",at=@At("HEAD"),cancellable=true)
 private void emptyCrystalSearch(CallbackInfo ci){if(targetType==net.minecraft.world.entity.boss.enderdragon.EndCrystal.class){target=null;ci.cancel();}}
}
