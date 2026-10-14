package dev.eugheist.ttmodern;
import net.minecraft.world.item.*;
import thaumcraft.api.aspects.Aspect;
/** Build-164 grain is an essentia ingredient, not edible food. */
public final class InfusedGrainItem extends Item {
 public final Aspect aspect;
 public InfusedGrainItem(Aspect aspect){super(new Properties());this.aspect=aspect;}
 public java.util.Map<String,Integer> aspects(){return java.util.Map.of(aspect.getTag(),1,Aspect.CROP.getTag(),1);}
}
