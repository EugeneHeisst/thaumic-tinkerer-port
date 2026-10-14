package dev.eugheist.ttmodern.mixin;
import java.util.Collection;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.spongepowered.asm.mixin.injection.InjectionPoint;
import org.spongepowered.asm.mixin.injection.struct.InjectionPointData;
/** Select only the base pouch type checks, leaving other focus checks intact. */
public final class FocusPouchInstanceof extends InjectionPoint {
 public FocusPouchInstanceof(InjectionPointData data){super(data);}
 @Override public boolean find(String desc,InsnList instructions,Collection<AbstractInsnNode> nodes){boolean found=false;for(var node:instructions)if(node instanceof TypeInsnNode type&&type.getOpcode()==Opcodes.INSTANCEOF&&type.desc.equals("dev/eugheist/tc4modern/FocusPouchItem")){nodes.add(node);found=true;}return found;}
}
