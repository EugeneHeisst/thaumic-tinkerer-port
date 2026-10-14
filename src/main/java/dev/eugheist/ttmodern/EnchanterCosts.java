package dev.eugheist.ttmodern;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

/** Exact build164 cost catalog. Amounts are whole vis; conversion to centivis is at consumption. */
final class EnchanterCosts {
    record Entry(String texture, boolean vanilla, String research, Map<Aspect,Integer> base) {
        AspectList cost(int level) {
            if (level < 1 || level > 255) throw new IllegalArgumentException("Invalid enchantment level");
            double multiplier = level * (1.0 + level * 0.2);
            var result = new AspectList();
            base.forEach((aspect, amount) -> result.add(aspect, (int)(amount * multiplier)));
            return result;
        }
    }
    static final Map<String,Entry> ENTRIES;
    static {
        var entries = new LinkedHashMap<String,Entry>();
        add(entries, "minecraft:protection", "ttinkerer:textures/enchants/protection.png", true, "", Aspect.EARTH, 10, Aspect.ENTROPY, 7);
        add(entries, "minecraft:fire_protection", "ttinkerer:textures/enchants/fireprotection.png", true, "", Aspect.FIRE, 10, Aspect.ENTROPY, 3, Aspect.WATER, 4);
        add(entries, "minecraft:feather_falling", "ttinkerer:textures/enchants/featherfalling.png", true, "", Aspect.AIR, 16, Aspect.ORDER, 5);
        add(entries, "minecraft:blast_protection", "ttinkerer:textures/enchants/blastprotection.png", true, "", Aspect.EARTH, 5, Aspect.FIRE, 5, Aspect.ENTROPY, 8);
        add(entries, "minecraft:projectile_protection", "ttinkerer:textures/enchants/projectileprotection.png", true, "", Aspect.AIR, 10, Aspect.ENTROPY, 7);
        add(entries, "minecraft:respiration", "ttinkerer:textures/enchants/respiration.png", true, "", Aspect.WATER, 10, Aspect.AIR, 8, Aspect.ORDER, 5);
        add(entries, "minecraft:aqua_affinity", "ttinkerer:textures/enchants/aquaaffinity.png", true, "", Aspect.WATER, 25, Aspect.ORDER, 20, Aspect.EARTH, 5);
        add(entries, "minecraft:thorns", "ttinkerer:textures/enchants/thorns.png", true, "", Aspect.EARTH, 10, Aspect.ENTROPY, 12);
        add(entries, "minecraft:sharpness", "ttinkerer:textures/enchants/sharpness.png", true, "", Aspect.ORDER, 10);
        add(entries, "minecraft:smite", "ttinkerer:textures/enchants/smite.png", true, "", Aspect.ORDER, 5, Aspect.AIR, 5);
        add(entries, "minecraft:bane_of_arthropods", "ttinkerer:textures/enchants/baneofawfulturds.png", true, "", Aspect.ORDER, 5, Aspect.FIRE, 5);
        add(entries, "minecraft:knockback", "ttinkerer:textures/enchants/knockback.png", true, "", Aspect.ENTROPY, 5, Aspect.AIR, 10);
        add(entries, "minecraft:fire_aspect", "ttinkerer:textures/enchants/fireaspect.png", true, "", Aspect.FIRE, 15, Aspect.EARTH, 4);
        add(entries, "minecraft:looting", "ttinkerer:textures/enchants/looting.png", true, "", Aspect.AIR, 10, Aspect.FIRE, 10, Aspect.WATER, 10, Aspect.EARTH, 10, Aspect.ORDER, 15, Aspect.ENTROPY, 15);
        add(entries, "minecraft:efficiency", "ttinkerer:textures/enchants/efficiency.png", true, "", Aspect.ENTROPY, 12, Aspect.EARTH, 4);
        add(entries, "minecraft:silk_touch", "ttinkerer:textures/enchants/silktouch.png", true, "", Aspect.ORDER, 50, Aspect.EARTH, 10, Aspect.ENTROPY, 10);
        add(entries, "minecraft:unbreaking", "ttinkerer:textures/enchants/unbreaking.png", true, "", Aspect.ORDER, 15, Aspect.WATER, 8, Aspect.EARTH, 8);
        add(entries, "minecraft:fortune", "ttinkerer:textures/enchants/fortune.png", true, "", Aspect.AIR, 10, Aspect.FIRE, 10, Aspect.WATER, 10, Aspect.EARTH, 10, Aspect.ORDER, 15, Aspect.ENTROPY, 15);
        add(entries, "minecraft:power", "ttinkerer:textures/enchants/power.png", true, "", Aspect.EARTH, 5, Aspect.ORDER, 10);
        add(entries, "minecraft:punch", "ttinkerer:textures/enchants/punch.png", true, "", Aspect.AIR, 4, Aspect.EARTH, 10, Aspect.ENTROPY, 5);
        add(entries, "minecraft:flame", "ttinkerer:textures/enchants/flame.png", true, "", Aspect.ENTROPY, 5, Aspect.FIRE, 20, Aspect.EARTH, 5);
        add(entries, "minecraft:infinity", "ttinkerer:textures/enchants/infinity.png", true, "", Aspect.ENTROPY, 40, Aspect.ORDER, 40, Aspect.EARTH, 10);
        add(entries, "minecraft:lure", "ttinkerer:textures/enchants/lure.png", true, "", Aspect.WATER, 20, Aspect.BEAST, 20);
        add(entries, "minecraft:luck_of_the_sea", "ttinkerer:textures/enchants/luckofthesea.png", true, "", Aspect.ENTROPY, 20, Aspect.WATER, 20);
        add(entries, "tc4modern:haste", "ttinkerer:textures/enchants/haste.png", true, "", Aspect.AIR, 10, Aspect.ENTROPY, 5, Aspect.EARTH, 5);
        add(entries, "tc4modern:repair", "ttinkerer:textures/enchants/repair.png", true, "", Aspect.WATER, 20, Aspect.FIRE, 20, Aspect.EARTH, 20, Aspect.AIR, 20, Aspect.ORDER, 20, Aspect.ENTROPY, 5);
        add(entries, "ttinkerer:ascent_boost", "ttinkerer:textures/enchants/ascentboost.png", false, "TTENCH_ASCENT_BOOST", Aspect.ENTROPY, 8, Aspect.AIR, 10);
        add(entries, "ttinkerer:slow_fall", "ttinkerer:textures/enchants/slowfall.png", false, "TTENCH_SLOW_FALL", Aspect.ORDER, 8, Aspect.AIR, 10);
        add(entries, "ttinkerer:auto_smelt", "ttinkerer:textures/enchants/autosmelt.png", false, "TTENCH_AUTO_SMELT", Aspect.ENTROPY, 20, Aspect.FIRE, 30);
        add(entries, "ttinkerer:desintegrate", "ttinkerer:textures/enchants/desintegrate.png", false, "TTENCH_DESINTEGRATE", Aspect.ENTROPY, 25, Aspect.AIR, 10, Aspect.EARTH, 10);
        add(entries, "ttinkerer:quick_draw", "ttinkerer:textures/enchants/quickdraw.png", false, "TTENCH_QUICK_DRAW", Aspect.ORDER, 10, Aspect.AIR, 10, Aspect.WATER, 5);
        add(entries, "ttinkerer:vampirism", "ttinkerer:textures/enchants/vamprisim.png", false, "TTENCH_VAMPIRISM", Aspect.ENTROPY, 8, Aspect.FIRE, 10, Aspect.WATER, 10);
        add(entries, "ttinkerer:focused_strike", "ttinkerer:textures/enchants/focusedstrikes.png", false, "TTENCH_VAMPIRISM", Aspect.ORDER, 12, Aspect.AIR, 10, Aspect.WATER, 10);
        add(entries, "ttinkerer:dispersed_strikes", "ttinkerer:textures/enchants/dispersedstrikes.png", false, "TTENCH_VAMPIRISM", Aspect.ENTROPY, 12, Aspect.FIRE, 10, Aspect.EARTH, 10);
        add(entries, "ttinkerer:valiance", "ttinkerer:textures/enchants/valiance.png", false, "TTENCH_VAMPIRISM", Aspect.ORDER, 12, Aspect.FIRE, 10, Aspect.EARTH, 10);
        add(entries, "ttinkerer:final_strike", "ttinkerer:textures/enchants/finalstrike.png", false, "TTENCH_VAMPIRISM", Aspect.ENTROPY, 16, Aspect.FIRE, 16);
        add(entries, "ttinkerer:tunnel", "ttinkerer:textures/enchants/tunnel.png", false, "TTENCH_VAMPIRISM", Aspect.EARTH, 16, Aspect.ORDER, 16);
        add(entries, "ttinkerer:shatter", "ttinkerer:textures/enchants/shatter.png", false, "TTENCH_VAMPIRISM", Aspect.EARTH, 16, Aspect.ENTROPY, 16);
        add(entries, "ttinkerer:shockwave", "ttinkerer:textures/enchants/shockwave.png", false, "TTENCH_VAMPIRISM", Aspect.EARTH, 16, Aspect.AIR, 16);
        add(entries, "ttinkerer:pounce", "ttinkerer:textures/enchants/pounce.png", false, "TTENCH_VAMPIRISM", Aspect.EARTH, 16, Aspect.AIR, 16);
        ENTRIES = Collections.unmodifiableMap(entries);
    }
    private static void add(Map<String,Entry> entries, String key, String texture, boolean vanilla, String research, Object... pairs) {
        var base = new LinkedHashMap<Aspect,Integer>();
        for (int i=0; i<pairs.length; i+=2) base.put((Aspect)pairs[i], (Integer)pairs[i+1]);
        if (entries.put(key,new Entry(texture,vanilla,research,Collections.unmodifiableMap(base))) != null)
            throw new IllegalStateException("Duplicate enchanter cost: " + key);
    }
    private EnchanterCosts() {}
}
