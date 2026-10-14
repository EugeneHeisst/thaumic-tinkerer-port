package dev.eugheist.ttmodern.mixin;
@org.spongepowered.asm.mixin.Mixin(value=dev.eugheist.tc4modern.ArcaneEar.EarEntity.class,remap=false)
public interface ArcaneEarAccessor {
 @org.spongepowered.asm.mixin.gen.Accessor("note") byte ttinkerer$getNote();
 @org.spongepowered.asm.mixin.gen.Accessor("note") void ttinkerer$setNote(byte note);
}
