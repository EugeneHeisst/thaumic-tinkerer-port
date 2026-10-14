package dev.eugheist.ttmodern;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import thaumcraft.api.aspects.Aspect;
public final class MobAspectItem extends Item{
    public final Aspect aspect;public final int form;
    MobAspectItem(Aspect aspect,int form){super(new Properties().stacksTo(16));this.aspect=aspect;this.form=form;}
    @Override public boolean isFoil(ItemStack stack){return form==2;}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,java.util.List<Component> lines,TooltipFlag flag){lines.add(Component.literal(aspect.getName()));}
}
