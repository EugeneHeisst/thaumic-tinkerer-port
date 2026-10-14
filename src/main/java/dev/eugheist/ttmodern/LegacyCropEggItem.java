package dev.eugheist.ttmodern;
import net.minecraft.world.item.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.component.CustomData;
/** Original crop BEAST entries used invalid egg IDs 0..23; preserve inert distinct variants. */
public final class LegacyCropEggItem extends Item {
 public LegacyCropEggItem(){super(new Properties());}
 static ItemStack stack(int id){if(id<0||id>=24)throw new IllegalArgumentException("legacy crop egg id");var stack=new ItemStack(ThaumicTinkererModern.LEGACY_CROP_EGG.get());var tag=new CompoundTag();tag.putInt("legacyEggId",id);stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));return stack;}
}
