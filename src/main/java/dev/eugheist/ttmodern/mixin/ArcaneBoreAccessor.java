package dev.eugheist.ttmodern.mixin;
@org.spongepowered.asm.mixin.Mixin(value=dev.eugheist.tc4modern.ArcaneBore.BoreEntity.class,remap=false)
public interface ArcaneBoreAccessor {
 @org.spongepowered.asm.mixin.gen.Accessor("area") int ttinkerer$getArea();
 @org.spongepowered.asm.mixin.gen.Accessor("speed") int ttinkerer$getSpeed();
 @org.spongepowered.asm.mixin.gen.Invoker("gettingPower") boolean ttinkerer$gettingPower();
}
