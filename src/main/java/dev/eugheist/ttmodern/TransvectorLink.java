package dev.eugheist.ttmodern;
import net.minecraft.core.BlockPos;
/** Shared binder contract; the dislocator accepts ordinary blocks as well as tiles. */
interface TransvectorLink{
    boolean inRange(BlockPos pos);
    boolean link(BlockPos pos);
}
