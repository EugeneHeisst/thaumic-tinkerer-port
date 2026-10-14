package dev.eugheist.ttmodern;
/** Build164 upgrade171, represented by a stable modern namespaced upgrade ID. */
final class FumeTool implements dan200.computercraft.api.turtle.ITurtleUpgrade {
 static final net.neoforged.neoforge.registries.DeferredRegister<dan200.computercraft.api.upgrades.UpgradeType<? extends dan200.computercraft.api.turtle.ITurtleUpgrade>> TYPES=net.neoforged.neoforge.registries.DeferredRegister.create(dan200.computercraft.api.turtle.ITurtleUpgrade.typeRegistry(),"ttinkerer");
 static final java.util.function.Supplier<dan200.computercraft.api.upgrades.UpgradeType<FumeTool>> TYPE=TYPES.register("fume_tool",()->dan200.computercraft.api.upgrades.UpgradeType.simple(new FumeTool()));
 static void init(net.neoforged.bus.api.IEventBus bus){TYPES.register(bus);}
 @Override public dan200.computercraft.api.upgrades.UpgradeType<FumeTool> getType(){return TYPE.get();}
 @Override public dan200.computercraft.api.turtle.TurtleUpgradeType getUpgradeType(){return dan200.computercraft.api.turtle.TurtleUpgradeType.TOOL;}
 @Override public net.minecraft.network.chat.Component getAdjective(){return net.minecraft.network.chat.Component.translatable("ttcomputer.dissipator");}
 @Override public net.minecraft.world.item.ItemStack getCraftingItem(){return new net.minecraft.world.item.ItemStack(ThaumicTinkererModern.GAS_REMOVER.get());}
 @Override public dan200.computercraft.api.turtle.TurtleCommandResult useTool(dan200.computercraft.api.turtle.ITurtleAccess turtle,dan200.computercraft.api.turtle.TurtleSide side,dan200.computercraft.api.turtle.TurtleVerb verb,net.minecraft.core.Direction direction){
  if(verb!=dan200.computercraft.api.turtle.TurtleVerb.DIG)return dan200.computercraft.api.turtle.TurtleCommandResult.failure();
  GasRemoverItem.clear(turtle.getLevel(),turtle.getPosition());return dan200.computercraft.api.turtle.TurtleCommandResult.success();
 }
}
