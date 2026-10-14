package dev.eugheist.ttmodern;
/** Original general.kami.forceenabled is read before item registration. */
public final class KamiStartupConfig {
 static final net.neoforged.neoforge.common.ModConfigSpec SPEC;
 private static final net.neoforged.neoforge.common.ModConfigSpec.BooleanValue FORCE_ENABLED;
 static {var builder=new net.neoforged.neoforge.common.ModConfigSpec.Builder();builder.push("general");FORCE_ENABLED=builder.comment("Build164 general.kami.forceenabled; registry-affecting setting, applied on application startup.").define("kami.forceenabled",true);builder.pop();SPEC=builder.build();}
 static void register(net.neoforged.fml.ModContainer container){container.registerConfig(net.neoforged.fml.config.ModConfig.Type.STARTUP,SPEC);if(!SPEC.isLoaded())throw new IllegalStateException("KAMI startup config must load before registries");}
 public static boolean enabled(){return !SPEC.isLoaded()||FORCE_ENABLED.get();}
}
