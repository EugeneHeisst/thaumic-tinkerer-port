package dev.eugheist.ttmodern;
public final class TinkererConfig{
    static final net.neoforged.neoforge.common.ModConfigSpec SPEC;
    static final net.neoforged.neoforge.common.ModConfigSpec.BooleanValue SURVIVAL_SHARE_TOME;
    static final net.neoforged.neoforge.common.ModConfigSpec.BooleanValue ENABLE_FIRE,ENABLE_CAKE,ENABLE_FLIGHT;
    static final net.neoforged.neoforge.common.ModConfigSpec.IntValue BEDROCK_DIMENSION_ID,ORE_DENSITY;
    static final net.neoforged.neoforge.common.ModConfigSpec.ConfigValue<java.util.List<? extends String>> ORE_BLACKLIST;
    static{var builder=new net.neoforged.neoforge.common.ModConfigSpec.Builder();SURVIVAL_SHARE_TOME=builder.comment("Build 164: allow crafting the Tome of Knowledge Sharing in survival.").define("enableSurvivalShareTome",true);ENABLE_FIRE=builder.comment("Build 164: enable infused fire transformations and spread.").define("enableFire",true);ENABLE_CAKE=builder.comment("Build 164: Aer fire may transform source water into cake.").define("enableCake",true);ENABLE_FLIGHT=builder.comment("Build 164: allow awakened ichorcloth chestplate flight.").define("enableFlight",true);BEDROCK_DIMENSION_ID=builder.comment("Legacy Bedrock dimension id; 0 disables portal access. Modern key: ttinkerer:bedrock.").defineInRange("bedrockDimensionID",-19,Integer.MIN_VALUE,Integer.MAX_VALUE);ORE_DENSITY=builder.comment("Build 164 vertical ore clusters per chunk (200 vein attempts per cluster).").defineInRange("bedrockOreDensity",1,0,Integer.MAX_VALUE);ORE_BLACKLIST=builder.comment("Original OreDictionary names excluded from Bedrock generation.").defineListAllowEmpty("bedrockOreBlacklist",java.util.List.of("oreFirestone"),()->"oreFirestone",v->v instanceof String);SPEC=builder.build();}
    static boolean bedrockEnabled(){return KamiStartupConfig.enabled()&&(!SPEC.isLoaded()||BEDROCK_DIMENSION_ID.get()!=0);}
    static int oreDensity(){return SPEC.isLoaded()?ORE_DENSITY.get():1;}
    static java.util.Set<String> oreBlacklist(){return SPEC.isLoaded()?java.util.Set.copyOf(ORE_BLACKLIST.get()):java.util.Set.of("oreFirestone");}
    static boolean flightEnabled(){return !SPEC.isLoaded()||ENABLE_FLIGHT.get();}
    public static boolean fireEnabled(){return !SPEC.isLoaded()||ENABLE_FIRE.get();}
    static boolean cakeEnabled(){return !SPEC.isLoaded()||ENABLE_CAKE.get();}
    static boolean survivalShareTome(){return !SPEC.isLoaded()||SURVIVAL_SHARE_TOME.get();}
}
