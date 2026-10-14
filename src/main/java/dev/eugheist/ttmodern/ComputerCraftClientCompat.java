package dev.eugheist.ttmodern;
/** Event signature is only resolved on the optional CC client path. */
final class ComputerCraftClientCompat {
 static void init(net.neoforged.bus.api.IEventBus bus){bus.addListener(ComputerCraftClientCompat::modellers);}
 static void modellers(dan200.computercraft.api.client.turtle.RegisterTurtleModellersEvent event){event.register(FumeTool.TYPE.get(),dan200.computercraft.api.client.turtle.TurtleUpgradeModeller.flatItem());}
}
