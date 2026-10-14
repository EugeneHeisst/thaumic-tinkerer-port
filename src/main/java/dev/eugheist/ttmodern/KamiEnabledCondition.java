package dev.eugheist.ttmodern;
public record KamiEnabledCondition() implements net.neoforged.neoforge.common.conditions.ICondition {
 public static final com.mojang.serialization.MapCodec<KamiEnabledCondition> CODEC=com.mojang.serialization.MapCodec.unit(new KamiEnabledCondition());
 @Override public boolean test(IContext context){return KamiStartupConfig.enabled();}
 @Override public com.mojang.serialization.MapCodec<? extends net.neoforged.neoforge.common.conditions.ICondition> codec(){return CODEC;}
}
