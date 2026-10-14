package dev.eugheist.ttmodern;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import thaumcraft.api.research.ResearchCategories;

/** Explicit isolated-server audit; never enabled in production. */
final class PortAudit {
    static void register() {
        if(Boolean.getBoolean("ttinkerer.ccLuaAudit"))ComputerCraftLuaAudit.register();
        if(Boolean.getBoolean("ttinkerer.kamiStartupAudit"))NeoForge.EVENT_BUS.addListener(KamiStartupAudit::run);
        if(Boolean.getBoolean("ttinkerer.bedrockConfigAudit"))NeoForge.EVENT_BUS.addListener(BedrockDisabledAudit::run);
        if(Boolean.getBoolean("ttinkerer.mirrorsConfigAudit"))NeoForge.EVENT_BUS.addListener(MirrorsDisabledAudit::run);
        if(Boolean.getBoolean("ttinkerer.flightConfigAudit"))NeoForge.EVENT_BUS.addListener(FlightDisabledAudit::run);
        if(Boolean.getBoolean("ttinkerer.fireConfigAudit"))NeoForge.EVENT_BUS.addListener(FireDisabledAudit::run);
        if (Boolean.getBoolean("ttinkerer.portAudit")){AdvancedMiningHookAudit.init(); ProtoclayAudit.init(); NeoForge.EVENT_BUS.addListener(PortAudit::run); }
    }
    private static void require(boolean value, String label) {
        if (!value) throw new IllegalStateException("TT PORT AUDIT FAILED: " + label);
    }
    private static void run(ServerStartedEvent event) {
        var server=event.getServer();
        require(ResearchCategories.getResearch("BASICTHAUMATURGY")!=null,"base research retained");
        require(ResearchCategories.getResearch("DARK_QUARTZ")!=null,"addon research registered");
        for (String key : new String[]{"dark_quartz_coal","dark_quartz_charcoal","dark_quartz_block","dark_quartz_pillar","chiseled_dark_quartz","dark_quartz_slab","dark_quartz_stairs"})
            require(server.getRecipeManager().byKey(ResourceLocation.fromNamespaceAndPath("ttinkerer",key)).isPresent(),"recipe " + key);
        var level=server.overworld(); if(!net.neoforged.fml.ModList.get().isLoaded("computercraft")){var golemId=net.minecraft.resources.ResourceLocation.parse("ttinkerer:golem_connector");if(net.minecraft.core.registries.BuiltInRegistries.BLOCK.containsKey(golemId)||net.minecraft.core.registries.BuiltInRegistries.ITEM.containsKey(golemId)||net.minecraft.core.registries.BuiltInRegistries.BLOCK_ENTITY_TYPE.containsKey(golemId)||ResearchCategories.getResearch("GOLEM_CONNECTOR")!=null)throw new IllegalStateException("Golem connector registered without CC");var analyzerId=net.minecraft.resources.ResourceLocation.parse("ttinkerer:aspect_analyzer");if(net.minecraft.core.registries.BuiltInRegistries.BLOCK.containsKey(analyzerId)||net.minecraft.core.registries.BuiltInRegistries.ITEM.containsKey(analyzerId)||net.minecraft.core.registries.BuiltInRegistries.BLOCK_ENTITY_TYPE.containsKey(analyzerId)||net.minecraft.core.registries.BuiltInRegistries.MENU.containsKey(analyzerId)||ResearchCategories.getResearch("ASPECT_ANALYZER")!=null)throw new IllegalStateException("CC analyzer registered without CC");} if(!net.neoforged.fml.ModList.get().isLoaded("computercraft")&&ResearchCategories.getResearch("PERIPHERALS")!=null)throw new IllegalStateException("CC research registered without CC"); if(net.neoforged.fml.ModList.get().isLoaded("ae2"))AE2MovableAudit.run(server); if(net.neoforged.fml.ModList.get().isLoaded("computercraft"))ComputerCraftCompat.audit(server); DeviceTooltipAudit.run(server); IchorSwordBlockingAudit.run(server); RecallFocusAudit.run(server); WarpGateAudit.run(server); SkyPearlDataAudit.run(server); ShadowbeamPathAudit.run(server); XPDrainFocusAudit.run(server); PlacementMirrorAudit.run(server); BlockTalismanAudit.run(server); CatAmuletAudit.run(server); IchorPouchAudit.run(server); BedrockGenerationAudit.run(server); BedrockPortalAudit.run(server); KamiResourcesAudit.run(server); IchorAudit.run(); KamiMaterialAudit.run(); KamiWandAudit.run(server); KamiToolsAudit.run(server); AdvancedToolModesAudit.run(server); AdvancedToolMiningAudit.run(server); AdvancedMiningHookAudit.run(server); AwakenedPickAudit.run(server); AwakenedShovelAudit.run(server); AwakenedAxeAudit.run(server); AwakenedSwordAudit.run(server); ProtoclayAudit.run(server); KamiArmorAudit.run(server); AwakenedHelmAudit.run(server); AwakenedChestAudit.run(server); AwakenedLegsAudit.run(server); AwakenedBootsAudit.run(server); InfusedFireTransformationsAudit.run(); OrderFireAudit.run(level); InfusedFireAudit.run(level); InfusedFireSmokeAudit.run(level); InfusedSeedDataAudit.run(level); InfusedCropProcessAudit.run(); InfusedSoilDataAudit.run(); InfusedFarmlandAudit.run(level); InfusedGrainAudit.run(level); InfusedSeedsAudit.run(level); InfusedCropHarvestAudit.run(); InfusedGrainItemAudit.run(); InfusedCropLootAudit.run(level); CropOreTagsAudit.run(); InfusedHarvestWorldAudit.run(level); InfusedCropReloadAudit.run(level); InfusedSeedRecipesAudit.run(); InfusedPotionResearchAudit.run(); InfusedPotionDrinkAudit.run(level); ImbuedWaterAudit.run(level); ImbuedCombatAudit.run(level); ForcefieldAudit.run(level); EnchanterCostsAudit.run(); EnchanterStructureAudit.run(level); EnchanterSelectionAudit.run(level); EnchanterProcessAudit.run(level); EnchanterMenuAudit.run(level); EnchanterLifecycleAudit.run(level); MovementEnchantmentAudit.run(level); ToolEnchantmentAudit.run(level); QuickDrawAudit.run(level); VampirismAudit.run(level); StrikeEnchantmentAudit.run(level); CriticalEnchantmentAudit.run(level); HarvestSpeedAudit.run(level); FallCombatAudit.run(level); EnchanterAddonCatalogAudit.run(level); RevealingHelmAudit.run(level); PlatformAudit.run(level); TransvectorAudit.run(level); XPTalismanAudit.run(level); FunnelAudit.run(level); FunnelInteractionAudit.run(level); ReturnedItemModesAudit.run(level); RepairerAudit.run(level); FocusAudit.run(level); MagnetAudit.run(level); DislocatorAudit.run(level); ShareTomeAudit.run(level); BloodSwordAudit.run(level); SummonAudit.run(level); LocomotiveAudit.run(level); AnimationTabletAudit.run(level); var pos=new BlockPos(0,200,0);
        for(var holder:ThaumicTinkererModern.blockEntries().toList()) {
            if(holder.get() instanceof ForcefieldBlock || holder.get() instanceof InfusedGrainBlock || holder.get() instanceof InfusedFireBlock || holder.get() instanceof GasBlock || holder.get() instanceof NitorLightBlock)continue;
            level.removeBlock(pos,false); // Repeatable even after an earlier failed audit saved its fixture.
            var state=holder.get().defaultBlockState();
            require(level.setBlock(pos,state,3),"place " + holder.getId());
            require(level.getBlockState(pos).is(holder.get()),"read placed " + holder.getId());
            require(!state.requiresCorrectToolForDrops()||new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.NETHERITE_PICKAXE).isCorrectToolForDrops(state),"harvest tool " + holder.getId());
            var drops=net.minecraft.world.level.block.Block.getDrops(state,level,pos,null);
            if(holder.get() instanceof BedrockPortal)require(drops.isEmpty()&&holder.get().asItem()==net.minecraft.world.item.Items.AIR,"portal has no item/drop");else require(drops.size()==1 && drops.getFirst().is(holder.get() instanceof InfusedFarmlandBlock?net.minecraft.world.item.Items.DIRT:holder.get().asItem()) && drops.getFirst().getCount()==1,"single drop " + holder.getId());
        }
        var doubleSlab=ThaumicTinkererModern.SLAB.get().defaultBlockState().setValue(SlabBlock.TYPE,SlabType.DOUBLE);
        var drops=net.minecraft.world.level.block.Block.getDrops(doubleSlab,level,pos,null);
        require(drops.size()==1 && drops.getFirst().getCount()==2,"double slab drops two");
        level.removeBlock(pos,false);
        var origin=new BlockPos(8,210,8);
        var wall=origin.east();level.setBlock(wall,net.minecraft.world.level.block.Blocks.STONE.defaultBlockState(),3);
        var gas=ThaumicTinkererModern.LIGHT_GAS.get();
        var initial=gas.defaultBlockState().setValue(GasBlock.POWER,4);level.setBlock(origin,initial,2);
        gas.tick(initial,level,origin,level.random);
        require(level.getBlockState(origin).getValue(GasBlock.POWER)==0,"gas source spent");
        require(level.getBlockState(origin.west()).is(gas) && level.getBlockState(origin.west()).getValue(GasBlock.POWER)==3,"gas propagation decrements power");
        require(level.getBlockState(wall).is(net.minecraft.world.level.block.Blocks.STONE),"gas respects solid obstacles");
        require(ThaumicTinkererModern.SHADOW_GAS.get().defaultBlockState().getLightBlock(level,origin)==15,"shadow blocks light");
        require(gas.defaultBlockState().getLightEmission()==12,"gas light level 12");
        require(ResearchCategories.getResearch("GASEOUS_LIGHT").parents[0].equals("NITOR"),"light parent nitor");
        require(ResearchCategories.getResearch("GASEOUS_SHADOW").parents[0].equals("GASEOUS_LIGHT"),"shadow parent light");
        for(var direction:net.minecraft.core.Direction.values())level.removeBlock(origin.relative(direction),false);
        level.removeBlock(origin,false);
        var cleanerCenter=new BlockPos(8,220,8);
        for(var fixture:BlockPos.betweenClosed(cleanerCenter.offset(-4,-4,-4),cleanerCenter.offset(4,4,4)))level.removeBlock(fixture,false);
        var near=cleanerCenter.offset(-3,0,0);var outside=cleanerCenter.offset(3,0,0);var stone=cleanerCenter.above();
        level.setBlock(near,gas.defaultBlockState(),2);level.setBlock(outside,gas.defaultBlockState(),2);level.setBlock(stone,net.minecraft.world.level.block.Blocks.STONE.defaultBlockState(),2);
        int removed=GasRemoverItem.clear(level,cleanerCenter);require(removed==1,"gas cleaner original asymmetric six-block bounds; removed="+removed);
        require(level.getBlockState(outside).is(gas) && level.getBlockState(stone).is(net.minecraft.world.level.block.Blocks.STONE),"cleaner preserves outside/solid blocks");
        level.removeBlock(outside,false);level.removeBlock(stone,false);
        var fake=net.neoforged.neoforge.common.util.FakePlayerFactory.getMinecraft(level);fake.setPos(8.5,230,8.5);
        ThaumicTinkererModern.BRIGHT_NITOR.get().inventoryTick(new net.minecraft.world.item.ItemStack(ThaumicTinkererModern.BRIGHT_NITOR.get()),level,fake,0,false);
        var nitorPos=new BlockPos(8,231,8);require(level.getBlockState(nitorPos).is(ThaumicTinkererModern.NITOR_LIGHT.get()),"carried nitor produces light");
        ThaumicTinkererModern.NITOR_LIGHT.get().tick(level.getBlockState(nitorPos),level,nitorPos,level.random);
        require(!level.getBlockState(nitorPos).is(ThaumicTinkererModern.NITOR_LIGHT.get()),"orphan nitor light expires");
        var arcane=dev.eugheist.tc4modern.ArcaneRecipes.entries().stream().filter(r->r.key().equals("GAS_REMOVER")).findFirst().orElseThrow();
        var grid=arcane.ingredients().stream().map(i->i.isEmpty()?net.minecraft.world.item.ItemStack.EMPTY:i.getItems()[0].copy()).toList();
        require(!arcane.matches(grid,java.util.Set.of()) && arcane.matches(grid,java.util.Set.of("GAS_REMOVER")),"cleaner arcane research gate");
        require(arcane.cost().getAmount(thaumcraft.api.aspects.Aspect.AIR)==2 && arcane.cost().getAmount(thaumcraft.api.aspects.Aspect.ORDER)==2,"cleaner original vis cost");
        fake.removeAllEffects();fake.tickCount=20;fake.getAbilities().instabuild=false;
        var talisman=new net.minecraft.world.item.ItemStack(ThaumicTinkererModern.CLEANSING_TALISMAN.get());
        fake.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.POISON,200));
        ThaumicTinkererModern.CLEANSING_TALISMAN.get().onWornTick(talisman,fake);
        require(fake.hasEffect(net.minecraft.world.effect.MobEffects.POISON)&&talisman.getDamageValue()==0,"disabled talisman leaves poison");
        CleansingTalismanItem.toggle(talisman);
        ThaumicTinkererModern.CLEANSING_TALISMAN.get().onWornTick(talisman,fake);
        require(!fake.hasEffect(net.minecraft.world.effect.MobEffects.POISON)&&talisman.getDamageValue()==1,"talisman cleanses poison costs one");
        fake.addEffect(new net.minecraft.world.effect.MobEffectInstance(dev.eugheist.tc4modern.WarpEffects.SUN_SCORNED,200));
        fake.addEffect(new net.minecraft.world.effect.MobEffectInstance(dev.eugheist.tc4modern.WarpEffects.WARP_WARD,200));
        ThaumicTinkererModern.CLEANSING_TALISMAN.get().onWornTick(talisman,fake);
        require(!fake.hasEffect(dev.eugheist.tc4modern.WarpEffects.SUN_SCORNED)&&fake.hasEffect(dev.eugheist.tc4modern.WarpEffects.WARP_WARD)&&talisman.getDamageValue()==11,"warp cleansing costs ten and preserves ward");
        fake.igniteForSeconds(10);ThaumicTinkererModern.CLEANSING_TALISMAN.get().onWornTick(talisman,fake);
        require(!fake.isOnFire()&&talisman.getDamageValue()==12,"talisman extinguishes costs one");
        require(ThaumicTinkererModern.CLEANSING_TALISMAN.get().getBaubleType(talisman)==baubles.api.BaubleType.AMULET,"talisman baubles slot");
        var talismanRecipe=dev.eugheist.tc4modern.InfusionItemRecipes.all().stream().filter(r->r.key().equals("CLEANSING_TALISMAN")).findFirst().orElseThrow();
        require(talismanRecipe.instability()==5 && talismanRecipe.components().size()==6 && talismanRecipe.central().get()==net.minecraft.world.item.Items.ENDER_PEARL,"original talisman infusion recipe");
        fake.removeAllEffects();
        System.out.println("TT TALISMAN AUDIT: ALL PASS (toggle, poison, fire, warp cost/ward, Baubles slot, infusion recipe)");
        System.out.println("TT NITOR/CLEANER AUDIT: ALL PASS (carried light/expiry, bounded removal, solid blocks preserved, arcane gate/cost)");
        System.out.println("TT GAS AUDIT: ALL PASS (bounded propagation, obstacle, light/shadow, research parents)");
        var cloth=new net.minecraft.world.item.ItemStack(ThaumicTinkererModern.SPELL_CLOTH.get());
        var target=new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.DIAMOND_PICKAXE);
        target.setDamageValue(123);
        target.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME,net.minecraft.network.chat.Component.literal("preserve-me"));
        var enchantments=server.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT);
        target.enchant(enchantments.getOrThrow(net.minecraft.world.item.enchantment.Enchantments.EFFICIENCY),3);
        var recipe=new SpellClothRecipe(net.minecraft.world.item.crafting.CraftingBookCategory.MISC);
        var input=net.minecraft.world.item.crafting.CraftingInput.of(2,1,java.util.List.of(cloth,target));
        require(recipe.matches(input,level),"cloth matches enchanted tool");
        var output=recipe.assemble(input,server.registryAccess());
        require(!output.isEnchanted() && output.getDamageValue()==123 && output.getHoverName().getString().equals("preserve-me"),"cloth strips only enchantments");
        require(target.isEnchanted() && cloth.getDamageValue()==0,"input stacks not mutated");
        require(recipe.getRemainingItems(input).getFirst().getDamageValue()==1,"cloth uses one durability");
        cloth.setDamageValue(34);
        require(recipe.getRemainingItems(input).getFirst().isEmpty(),"cloth breaks on final use");
        var extra=net.minecraft.world.item.crafting.CraftingInput.of(3,1,java.util.List.of(cloth,target,new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.DIRT)));
        require(!recipe.matches(extra,level),"extra input rejected");
        var crucible=dev.eugheist.tc4modern.CrucibleRecipes.byKey().get("SPELL_CLOTH");
        require(crucible!=null && crucible.aspects.getAmount(thaumcraft.api.aspects.Aspect.MAGIC)==10,"original cloth crucible cost");
        require(thaumcraft.api.crafting.CrucibleRecipe.findMatching(java.util.List.of(crucible),java.util.Set.of(),crucible.aspects,new net.minecraft.world.item.ItemStack(dev.eugheist.tc4modern.TC4Modern.ENCHANTED_FABRIC.get()))==null,"cloth research gate");
        System.out.println("TT SPELL CLOTH AUDIT: ALL PASS (components conserved, durability, invalid input, crucible research gate)");
        System.out.println("TT PORT AUDIT: ALL PASS (base/addon research, seven recipes, five blocks/drops, double slab)");
    }
}
