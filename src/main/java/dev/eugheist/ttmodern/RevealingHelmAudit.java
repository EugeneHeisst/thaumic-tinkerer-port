package dev.eugheist.ttmodern;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.*;
import net.minecraft.world.entity.EquipmentSlot;
import thaumcraft.api.aspects.*;
final class RevealingHelmAudit {
 private static void check(boolean ok,String label){if(!ok)throw new IllegalStateException("TT revealing helm: "+label);}
 static void run(ServerLevel level){
  var item=ThaumicTinkererModern.REVEALING_HELM.get();var stack=new ItemStack(item);var player=net.neoforged.neoforge.common.util.FakePlayerFactory.getMinecraft(level);player.getInventory().clearContent();player.removeAllEffects();
  check(stack.getMaxDamage()==500&&item.getDefense()==2&&item.getEnchantmentValue()==25&&stack.getRarity()==Rarity.UNCOMMON,"original material/durability/rarity");check(item instanceof thaumcraft.api.IRepairable&&item.showNodes(stack,player)&&item.showIngamePopups(stack,player),"repair and goggles interfaces");
  check(item.isValidRepairItem(stack,new ItemStack(dev.eugheist.tc4modern.TC4Modern.THAUMIUM.get()))&&!item.isValidRepairItem(stack,new ItemStack(Items.IRON_INGOT)),"thaumium repair only");
  var wand=new ItemStack(dev.eugheist.tc4modern.TC4Modern.GOLD_GREATWOOD_WAND.get());player.setItemSlot(EquipmentSlot.HEAD,stack);
  for(var aspect:Aspect.getPrimalAspects()){check(item.getVisDiscount(stack,player,aspect)==5,"universal5percent");WandDataTest.pay(wand,player,aspect);}
  var repair=level.registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).getHolder(net.minecraft.resources.ResourceLocation.parse("tc4modern:repair")).orElseThrow();check(repair.value().isSupportedItem(stack),"modern repair enchantment accepts original IRepairable helmet");
  var recipe=dev.eugheist.tc4modern.ArcaneRecipes.entries().stream().filter(r->r.key().equals("REVEALING_HELM")).findFirst().orElseThrow();check(recipe.width()==2&&recipe.height()==1&&recipe.ingredients().get(0).test(new ItemStack(FocusResearch.item("goggles")))&&recipe.ingredients().get(1).test(new ItemStack(FocusResearch.item("thaumium_helmet")))&&recipe.output().is(item),"original GH recipe");for(var aspect:Aspect.getPrimalAspects())check(recipe.cost().getAmount(aspect)==5,"recipe primal cost");
  var research=thaumcraft.api.research.ResearchCategories.getResearch("REVEALING_HELM");check(java.util.Arrays.asList(research.parents).contains("GOGGLES")&&java.util.Arrays.asList(research.parentsHidden).contains("THAUMIUM")&&research.getPages().length==2&&!research.isSecondary(),"original research progression");
  player.getInventory().clearContent();System.out.println("TT REVEALING HELM AUDIT: ALL PASS (armor,500durability,repair,goggles interfaces,actual vis payment/all primals,recipe,research)");
 }
 private static final class WandDataTest {
  static void pay(ItemStack wand,net.minecraft.world.entity.player.Player player,Aspect aspect){
   var head=player.getItemBySlot(EquipmentSlot.HEAD);player.setItemSlot(EquipmentSlot.HEAD,ItemStack.EMPTY);float plain=dev.eugheist.tc4modern.WandData.craftingModifier(wand,player,aspect);player.setItemSlot(EquipmentSlot.HEAD,head);float discounted=dev.eugheist.tc4modern.WandData.craftingModifier(wand,player,aspect);check(Math.abs(plain-discounted-.05f)<1e-5,"real gear discount "+aspect);
   dev.eugheist.tc4modern.WandData.add(wand,aspect,50,true);int before=dev.eugheist.tc4modern.WandData.get(wand,aspect);check(dev.eugheist.tc4modern.WandData.consumeCrafting(wand,new AspectList().add(aspect,10),true,player),"actual consume");check(before-dev.eugheist.tc4modern.WandData.get(wand,aspect)==(int)(1000*discounted),"exact discounted debit");
  }
 }
}
