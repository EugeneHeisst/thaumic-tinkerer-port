package dev.eugheist.ttmodern;
final class SkyPearlDataAudit {
 static void check(boolean value,String label){if(!value)throw new IllegalStateException("TT sky pearl: "+label);}
 static void run(net.minecraft.server.MinecraftServer server){
  var stack=new net.minecraft.world.item.ItemStack(SkyPearl.ITEM.get());
  check(!SkyPearlData.attuned(stack)&&SkyPearlData.position(stack).equals(net.minecraft.core.BlockPos.ZERO),"unbound defaults");
  net.minecraft.world.item.component.CustomData.update(net.minecraft.core.component.DataComponents.CUSTOM_DATA,stack,t->t.putString("auditOther","preserve"));
  var dimension=net.minecraft.resources.ResourceLocation.withDefaultNamespace("the_nether");var pos=new net.minecraft.core.BlockPos(-30,72,-40);SkyPearlData.bind(stack,pos,dimension);
  check(SkyPearlData.attuned(stack)&&SkyPearlData.position(stack).equals(pos)&&SkyPearlData.dimension(stack).equals(dimension),"negative coordinates and dimension identity");
  check(stack.hasFoil()&&stack.getMaxStackSize()==1&&stack.getRarity()==net.minecraft.world.item.Rarity.EPIC,"original bound glint,stack1,EPIC");
  var copy=net.minecraft.world.item.ItemStack.parseOptional(server.registryAccess(),(net.minecraft.nbt.CompoundTag)stack.save(server.registryAccess()));
  check(SkyPearlData.attuned(copy)&&SkyPearlData.position(copy).equals(pos)&&SkyPearlData.dimension(copy).equals(dimension),"native stack save/load preserves binding");
  check(SkyPearlData.distance(stack,new net.minecraft.world.phys.Vec3(-33,68,-40),false).equals("5.00")&&SkyPearlData.distance(stack,new net.minecraft.world.phys.Vec3(-33,68,-40),true).equals("3.00"),"full distance versus flat simple tooltip");
  check(SkyPearlData.distance(stack,new net.minecraft.world.phys.Vec3(-31,71,-40),false).equals("1.42"),"original ceiling to two decimal places");
  SkyPearlData.reset(stack);check(!SkyPearlData.attuned(stack)&&stack.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA).copyTag().getInt("x")==-30&&stack.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA).copyTag().getString("auditOther").equals("preserve"),"reset only changes original y sentinel and preserves unrelated data");
  SkyPearlData.bind(stack,new net.minecraft.core.BlockPos(1,-1,2),dimension);check(!SkyPearlData.attuned(stack),"original y=-1 sentinel remains unbound");
  var recipe=dev.eugheist.tc4modern.InfusionItemRecipes.all().stream().filter(r->r.key().equals("SKY_PEARL")).findFirst().orElseThrow();check(recipe.outputStack(net.minecraft.world.item.ItemStack.EMPTY).getCount()==2&&recipe.componentStacks().size()==4&&recipe.componentStacks().get(2).is(net.minecraft.world.item.Items.LAPIS_BLOCK),"original two-pearl infusion output and lapis component");
  System.out.println("TT SKY PEARL DATA AUDIT: ALL PASS (binding,dimension,negative coordinates,native save/load,3D/flat distance,round-up,reset preservation,y sentinel)");
 }
}
