package dev.eugheist.ttmodern;
import thaumcraft.api.aspects.Aspect;
final class EnchanterCostsAudit {
    private static void check(boolean ok,String message) { if(!ok) throw new IllegalStateException("Enchanter cost audit: " + message); }
    static void run() {
        check(EnchanterCosts.ENTRIES.size()==40,"40 unique original entries");
        var protection=EnchanterCosts.ENTRIES.get("minecraft:protection");
        check(protection.cost(1).getAmount(Aspect.EARTH)==12 && protection.cost(1).getAmount(Aspect.ENTROPY)==8,"level one multiplier and truncation");
        check(protection.cost(4).getAmount(Aspect.EARTH)==72 && protection.cost(4).getAmount(Aspect.ENTROPY)==50,"level four scaling");
        var lure=EnchanterCosts.ENTRIES.get("minecraft:lure");
        check(lure.cost(1).getAmount(Aspect.BEAST)==24,"original compound aspect retained");
        check(EnchanterCosts.ENTRIES.get("ttinkerer:focused_strike").research().equals("TTENCH_VAMPIRISM"),"original research key retained");
        check(EnchanterCosts.ENTRIES.get("ttinkerer:vampirism").texture().endsWith("vamprisim.png"),"original texture spelling");
        var isolated=protection.cost(1);isolated.add(Aspect.EARTH,900);
        check(protection.cost(1).getAmount(Aspect.EARTH)==12,"cost results independent");
        boolean rejected=false;try { protection.cost(0); } catch(IllegalArgumentException expected) { rejected=true; }
        check(rejected,"invalid level rejected");
        System.out.println("TT ENCHANTER COST AUDIT: ALL PASS (40 entries, scaling, truncation, independent costs, original metadata)");
    }
}
