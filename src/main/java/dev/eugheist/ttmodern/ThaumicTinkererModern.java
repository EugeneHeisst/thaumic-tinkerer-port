package dev.eugheist.ttmodern;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.*;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.*;

/** Build-164 content, adapted to NeoForge 1.21.1. Original authors: Vazkii, pixlepix, nekosune. */
@Mod(ThaumicTinkererModern.ID)
public final class ThaumicTinkererModern {
    public static final String ID = "ttinkerer";
    public static final java.util.Map<String,thaumcraft.api.crafting.CrucibleRecipe> CRUCIBLE_RECIPES = new java.util.LinkedHashMap<>();
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ID);
    public static final DeferredItem<LegacyCropEggItem> LEGACY_CROP_EGG=ITEMS.register("legacy_crop_egg",LegacyCropEggItem::new);
    public static final DeferredItem<InfusedGrainItem> INFUSED_GRAIN_AIR=ITEMS.register("infused_grain_air",()->new InfusedGrainItem(thaumcraft.api.aspects.Aspect.AIR));
    public static final DeferredItem<InfusedGrainItem> INFUSED_GRAIN_FIRE=ITEMS.register("infused_grain_fire",()->new InfusedGrainItem(thaumcraft.api.aspects.Aspect.FIRE));
    public static final DeferredItem<InfusedGrainItem> INFUSED_GRAIN_EARTH=ITEMS.register("infused_grain_earth",()->new InfusedGrainItem(thaumcraft.api.aspects.Aspect.EARTH));
    public static final DeferredItem<InfusedGrainItem> INFUSED_GRAIN_WATER=ITEMS.register("infused_grain_water",()->new InfusedGrainItem(thaumcraft.api.aspects.Aspect.WATER));
    public static final DeferredItem<InfusedSeedsItem> INFUSED_SEEDS=ITEMS.register("infused_seeds",InfusedSeedsItem::new);
    public static final DeferredBlock<ForcefieldBlock> FORCEFIELD=BLOCKS.register("forcefield",ForcefieldBlock::new);
    public static final DeferredBlock<InfusedGrainBlock> INFUSED_GRAIN=BLOCKS.register("infused_grain",InfusedGrainBlock::new);
    public static final DeferredBlock<InfusedFarmlandBlock> INFUSED_FARMLAND=BLOCKS.register("infused_farmland",InfusedFarmlandBlock::new);
    public static final DeferredBlock<PlatformBlock> PLATFORM=BLOCKS.register("platform",PlatformBlock::new);
    public static final DeferredBlock<AnimationTabletBlock> ANIMATION_TABLET=BLOCKS.register("animation_tablet",AnimationTabletBlock::new);
    public static final DeferredBlock<LocomotiveBlock> LOCOMOTIVE=BLOCKS.register("locomotive",()->new LocomotiveBlock(false));
    public static final DeferredBlock<LocomotiveBlock> LOCOMOTIVE_RELAY=BLOCKS.register("locomotive_relay",()->new LocomotiveBlock(true));
    public static final DeferredBlock<SummonBlock> SUMMON=BLOCKS.register("summon_tablet",SummonBlock::new);
    public static final DeferredItem<RevealingHelmItem> REVEALING_HELM=ITEMS.register("revealing_helm",RevealingHelmItem::new);
    public static final DeferredItem<BloodSwordItem> BLOOD_SWORD=ITEMS.register("blood_sword",BloodSwordItem::new);
    public static final DeferredItem<ShareTomeItem> SHARE_TOME=ITEMS.register("share_tome",ShareTomeItem::new);
    public static final DeferredBlock<DislocatorBlock> DISLOCATOR=BLOCKS.register("dislocator",DislocatorBlock::new);
    public static final DeferredBlock<MagnetBlock> MAGNET=BLOCKS.register("magnet",()->new MagnetBlock(false));
    public static final DeferredBlock<MagnetBlock> MOB_MAGNET=BLOCKS.register("mob_magnet",()->new MagnetBlock(true));
    public static final DeferredItem<SoulMouldItem> SOUL_MOULD=ITEMS.register("soul_mould",SoulMouldItem::new);
    public static final DeferredRegister<net.minecraft.world.inventory.MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,ID);
    public static final java.util.function.Supplier<net.minecraft.world.inventory.MenuType<EnchanterMenu>> ENCHANTER_MENU=MENUS.register("enchanter",()->net.neoforged.neoforge.common.extensions.IMenuTypeExtension.create((id,inv,buf)->new EnchanterMenu(id,inv,(EnchanterEntity)inv.player.level().getBlockEntity(buf.readBlockPos()))));
    public static final java.util.function.Supplier<net.minecraft.world.inventory.MenuType<AnimationTabletMenu>> ANIMATION_TABLET_MENU=MENUS.register("animation_tablet",()->net.neoforged.neoforge.common.extensions.IMenuTypeExtension.create((id,inv,buf)->new AnimationTabletMenu(id,inv,(AnimationTabletEntity)inv.player.level().getBlockEntity(buf.readBlockPos()))));
    public static final java.util.function.Supplier<net.minecraft.world.inventory.MenuType<MagnetMenu>> MAGNET_MENU=MENUS.register("mob_magnet",()->net.neoforged.neoforge.common.extensions.IMenuTypeExtension.create((id,inv,buf)->new MagnetMenu(id,inv,(MagnetEntity)inv.player.level().getBlockEntity(buf.readBlockPos()))));
    public static final DeferredItem<SmeltFocus> FOCUS_SMELT=ITEMS.register("focus_smelt",SmeltFocus::new);
    public static final DeferredItem<FlightFocus> FOCUS_FLIGHT=ITEMS.register("focus_flight",FlightFocus::new);
    public static final DeferredItem<DislocationFocus> FOCUS_DISLOCATION=ITEMS.register("focus_dislocation",DislocationFocus::new);
    public static final DeferredItem<EnderChestFocus> FOCUS_ENDER_CHEST=ITEMS.register("focus_ender_chest",EnderChestFocus::new);
    public static final DeferredItem<HealFocus> FOCUS_HEAL=ITEMS.register("focus_heal",HealFocus::new);
    public static final DeferredItem<DeflectFocus> FOCUS_DEFLECT=ITEMS.register("focus_deflect",DeflectFocus::new);
    public static final DeferredItem<TelekinesisFocus> FOCUS_TELEKINESIS=ITEMS.register("focus_telekinesis",TelekinesisFocus::new);
    public static final DeferredBlock<EnchanterBlock> ENCHANTER=BLOCKS.register("enchanter",EnchanterBlock::new);
    public static final DeferredBlock<RepairerBlock> REPAIRER=BLOCKS.register("repairer",RepairerBlock::new);
    public static final DeferredBlock<FunnelBlock> FUNNEL=BLOCKS.register("funnel",FunnelBlock::new);
    public static final DeferredBlock<TransvectorBlock> INTERFACE=BLOCKS.register("transvector_interface",TransvectorBlock::new);
    public static final DeferredItem<ConnectorItem> CONNECTOR=ITEMS.register("connector",ConnectorItem::new);
    public static final DeferredRegister<net.minecraft.world.level.block.entity.BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,ID);
    public static final java.util.function.Supplier<net.minecraft.world.level.block.entity.BlockEntityType<ForcefieldEntity>> FORCEFIELD_ENTITY=ENTITIES.register("forcefield",()->net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(ForcefieldEntity::new,FORCEFIELD.get()).build(null));
    public static final java.util.function.Supplier<net.minecraft.world.level.block.entity.BlockEntityType<InfusedGrainEntity>> INFUSED_GRAIN_ENTITY=ENTITIES.register("infused_grain",()->net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(InfusedGrainEntity::new,INFUSED_GRAIN.get()).build(null));
    public static final java.util.function.Supplier<net.minecraft.world.level.block.entity.BlockEntityType<InfusedFarmlandEntity>> INFUSED_FARMLAND_ENTITY=ENTITIES.register("infused_farmland",()->net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(InfusedFarmlandEntity::new,INFUSED_FARMLAND.get()).build(null));
    public static final java.util.function.Supplier<net.minecraft.world.level.block.entity.BlockEntityType<PlatformEntity>> PLATFORM_ENTITY=ENTITIES.register("platform",()->net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(PlatformEntity::new,PLATFORM.get()).build(null));
    public static final java.util.function.Supplier<net.minecraft.world.level.block.entity.BlockEntityType<AnimationTabletEntity>> ANIMATION_TABLET_ENTITY=ENTITIES.register("animation_tablet",()->net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(AnimationTabletEntity::new,ANIMATION_TABLET.get()).build(null));
    public static final java.util.function.Supplier<net.minecraft.world.level.block.entity.BlockEntityType<LocomotiveEntity>> LOCOMOTIVE_ENTITY=ENTITIES.register("locomotive",()->net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(LocomotiveEntity::new,LOCOMOTIVE.get()).build(null));
    public static final java.util.function.Supplier<net.minecraft.world.level.block.entity.BlockEntityType<LocomotiveRelayEntity>> LOCOMOTIVE_RELAY_ENTITY=ENTITIES.register("locomotive_relay",()->net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(LocomotiveRelayEntity::new,LOCOMOTIVE_RELAY.get()).build(null));
    public static final java.util.function.Supplier<net.minecraft.world.level.block.entity.BlockEntityType<SummonEntity>> SUMMON_ENTITY=ENTITIES.register("summon_tablet",()->net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(SummonEntity::new,SUMMON.get()).build(null));
    public static final java.util.function.Supplier<net.minecraft.world.level.block.entity.BlockEntityType<DislocatorEntity>> DISLOCATOR_ENTITY=ENTITIES.register("dislocator",()->net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(DislocatorEntity::new,DISLOCATOR.get()).build(null));
    public static final java.util.function.Supplier<net.minecraft.world.level.block.entity.BlockEntityType<MagnetEntity>> MAGNET_ENTITY=ENTITIES.register("magnet",()->net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(MagnetEntity::new,MAGNET.get(),MOB_MAGNET.get()).build(null));
    public static final java.util.function.Supplier<net.minecraft.world.level.block.entity.BlockEntityType<EnchanterEntity>> ENCHANTER_ENTITY=ENTITIES.register("enchanter",()->net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(EnchanterEntity::new,ENCHANTER.get()).build(null));
    public static final java.util.function.Supplier<net.minecraft.world.level.block.entity.BlockEntityType<RepairerEntity>> REPAIRER_ENTITY=ENTITIES.register("repairer",()->net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(RepairerEntity::new,REPAIRER.get()).build(null));
    public static final java.util.function.Supplier<net.minecraft.world.level.block.entity.BlockEntityType<FunnelEntity>> FUNNEL_ENTITY=ENTITIES.register("funnel",()->net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(FunnelEntity::new,FUNNEL.get()).build(null));
    public static final java.util.function.Supplier<net.minecraft.world.level.block.entity.BlockEntityType<TransvectorEntity>> TRANSVECTOR_ENTITY=ENTITIES.register("transvector_interface",()->net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(TransvectorEntity::new,INTERFACE.get()).build(null));
    public static final DeferredItem<Item> DARK_QUARTZ = ITEMS.registerSimpleItem("dark_quartz");
    public static final DeferredBlock<GasBlock> LIGHT_GAS = BLOCKS.register("gaseous_light",()->new GasBlock(false));
    public static final DeferredBlock<GasBlock> SHADOW_GAS = BLOCKS.register("gaseous_shadow",()->new GasBlock(true));
    public static final DeferredItem<GasItem> LIGHT_BOTTLE = ITEMS.register("gaseous_light_bottle",()->new GasItem(LIGHT_GAS));
    public static final DeferredItem<GasItem> SHADOW_BOTTLE = ITEMS.register("gaseous_shadow_bottle",()->new GasItem(SHADOW_GAS));
    public static final java.util.List<dev.eugheist.tc4modern.ArcaneRecipes.Entry> ARCANE_RECIPES=new java.util.ArrayList<>();
    public static final DeferredBlock<NitorLightBlock> NITOR_LIGHT=BLOCKS.register("nitor_light",NitorLightBlock::new);
    public static final DeferredItem<BrightNitorItem> BRIGHT_NITOR=ITEMS.register("bright_nitor",BrightNitorItem::new);
    public static final DeferredItem<GasRemoverItem> GAS_REMOVER=ITEMS.register("gas_remover",GasRemoverItem::new);
    public static final java.util.List<dev.eugheist.tc4modern.InfusionItemRecipes.Recipe> INFUSION_RECIPES=new java.util.ArrayList<>();
    public static final DeferredItem<XPTalismanItem> XP_TALISMAN=ITEMS.register("xp_talisman",XPTalismanItem::new);
    public static final DeferredItem<CleansingTalismanItem> CLEANSING_TALISMAN=ITEMS.register("cleansing_talisman",CleansingTalismanItem::new);
    public static final DeferredItem<InfusedInkwellItem> INFUSED_INKWELL = ITEMS.register("infused_inkwell", InfusedInkwellItem::new);
    public static final DeferredItem<Item> SPELL_CLOTH = ITEMS.register("spell_cloth", () -> new Item(new Item.Properties().durability(35).rarity(Rarity.UNCOMMON)) {
        @Override public boolean isValidRepairItem(ItemStack stack, ItemStack ingredient) { return false; }
    });
    public static final DeferredRegister<net.minecraft.world.item.crafting.RecipeSerializer<?>> SERIALIZERS=DeferredRegister.create(Registries.RECIPE_SERIALIZER,ID);
    public static final java.util.function.Supplier<net.minecraft.world.item.crafting.RecipeSerializer<SpellClothRecipe>> SPELL_CLOTH_SERIALIZER=SERIALIZERS.register("spell_cloth", () -> new net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer<>(SpellClothRecipe::new));
    public static final java.util.function.Supplier<net.minecraft.world.item.crafting.RecipeSerializer<ShareTomeRecipe>> SHARE_TOME_SERIALIZER=SERIALIZERS.register("share_tome",()->new net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer<>(ShareTomeRecipe::new));
    private static BlockBehaviour.Properties quartzProperties() {
        return BlockBehaviour.Properties.ofFullCopy(Blocks.QUARTZ_BLOCK).strength(.8f, 10f).requiresCorrectToolForDrops();
    }
    public static final DeferredBlock<Block> QUARTZ = BLOCKS.register("dark_quartz_block", () -> new Block(quartzProperties()));
    public static final DeferredBlock<Block> CHISELED = BLOCKS.register("chiseled_dark_quartz", () -> new Block(quartzProperties()));
    public static final DeferredBlock<RotatedPillarBlock> PILLAR = BLOCKS.register("dark_quartz_pillar", () -> new RotatedPillarBlock(quartzProperties()));
    public static final DeferredBlock<StairBlock> STAIRS = BLOCKS.register("dark_quartz_stairs", () -> new StairBlock(QUARTZ.get().defaultBlockState(), quartzProperties()));
    public static final DeferredBlock<SlabBlock> SLAB = BLOCKS.register("dark_quartz_slab", () -> new SlabBlock(quartzProperties()));
    static final java.util.Map<InfusedFireTransformations.Kind,DeferredBlock<InfusedFireBlock>> FIRES=new java.util.EnumMap<>(InfusedFireTransformations.Kind.class);
    static {
        for(var kind:InfusedFireTransformations.Kind.values()){var fire=BLOCKS.register("fire_"+kind.name().toLowerCase(java.util.Locale.ROOT),()->new InfusedFireBlock(kind));FIRES.put(kind,fire);ITEMS.registerSimpleBlockItem(fire);}
        ITEMS.registerSimpleBlockItem(PLATFORM); ITEMS.registerSimpleBlockItem(LOCOMOTIVE); ITEMS.registerSimpleBlockItem(LOCOMOTIVE_RELAY); ITEMS.registerSimpleBlockItem(ANIMATION_TABLET); MobAspects.register(); ITEMS.registerSimpleBlockItem(SUMMON);
        ITEMS.registerSimpleBlockItem(DISLOCATOR); ITEMS.registerSimpleBlockItem(MAGNET); ITEMS.registerSimpleBlockItem(MOB_MAGNET); ITEMS.registerSimpleBlockItem(REPAIRER); ITEMS.registerSimpleBlockItem(ENCHANTER); ITEMS.registerSimpleBlockItem(FUNNEL); ITEMS.registerSimpleBlockItem(INTERFACE); ITEMS.registerSimpleBlockItem(QUARTZ); ITEMS.registerSimpleBlockItem(CHISELED);
        ITEMS.registerSimpleBlockItem(PILLAR); ITEMS.registerSimpleBlockItem(STAIRS); ITEMS.registerSimpleBlockItem(SLAB);
        TABS.register("thaumic_tinkerer", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.ttinkerer"))
            .icon(TinkererCreativeTab::icon)
            .displayItems((p, out) -> TinkererCreativeTab.contents().forEach(out::accept))
            .build());
    }
    public ThaumicTinkererModern(IEventBus bus,net.neoforged.fml.ModContainer container) {
        if(net.neoforged.fml.ModList.get().isLoaded("computercraft"))ComputerCraftCompat.init(bus);
        KamiStartupConfig.register(container); container.registerConfig(net.neoforged.fml.config.ModConfig.Type.COMMON,TinkererConfig.SPEC); ShareKnowledgeArchive.register();
        WarpGateBlock.init(); SkyPearl.init(); ShadowbeamFocus.init(bus); RecallFocus.init(bus); XPDrainFocus.init(bus); PlacementMirror.init(); BlockTalisman.init(); CatAmulet.init(); IchorPouch.init(); BedrockDimensionCondition.init(bus); BedrockPortal.init(); KamiResources.init(); KamiTools.init(); AwakenedPick.init(); AwakenedShovel.init(); AwakenedAxe.init(); AwakenedSword.init(); Protoclay.init(); KamiArmor.init(bus); AwakenedHelm.init(); AwakenedChest.init(); AwakenedLegs.init(); AwakenedBoots.init(); InfusedPotions.register(bus); KamiRegistries.register(bus); MENUS.register(bus); ENTITIES.register(bus); BLOCKS.register(bus); ITEMS.register(bus); TABS.register(bus); SERIALIZERS.register(bus); PortAudit.register(); OrderFireResolver.register(); InfusedCropLootRuntime.register();
        bus.addListener(this::setup);
        bus.addListener((net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent e)->{
            if(Boolean.getBoolean("ttinkerer.portAudit"))TransvectorAudit.capabilities(e);
            e.registerBlockEntity(net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK,ANIMATION_TABLET_ENTITY.get(),(tile,side)->tile.inventory);
            e.registerBlockEntity(net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK,REPAIRER_ENTITY.get(),(tile,side)->tile.inventory);
            e.registerBlockEntity(net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK,FUNNEL_ENTITY.get(),(tile,side)->side==net.minecraft.core.Direction.DOWN?null:tile.inventory);
            e.registerBlockEntity(net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK,TRANSVECTOR_ENTITY.get(),(tile,side)->TransvectorCapabilities.items(tile,side));
            e.registerBlockEntity(net.neoforged.neoforge.capabilities.Capabilities.FluidHandler.BLOCK,TRANSVECTOR_ENTITY.get(),(tile,side)->TransvectorCapabilities.fluids(tile,side));
            e.registerBlockEntity(net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage.BLOCK,TRANSVECTOR_ENTITY.get(),(tile,side)->TransvectorCapabilities.energy(tile,side));
        });
    }
    static java.util.stream.Stream<DeferredHolder<Item,? extends Item>> itemEntries(){return java.util.stream.Stream.concat(java.util.stream.Stream.concat(ITEMS.getEntries().stream(),KamiStartupConfig.enabled()?KamiRegistries.ITEMS.getEntries().stream():java.util.stream.Stream.empty()),net.neoforged.fml.ModList.get().isLoaded("computercraft")?AspectAnalyzer.ITEMS.getEntries().stream():java.util.stream.Stream.empty());}
    static java.util.stream.Stream<DeferredHolder<Block,? extends Block>> blockEntries(){return java.util.stream.Stream.concat(java.util.stream.Stream.concat(BLOCKS.getEntries().stream(),KamiStartupConfig.enabled()?KamiRegistries.BLOCKS.getEntries().stream():java.util.stream.Stream.empty()),net.neoforged.fml.ModList.get().isLoaded("computercraft")?AspectAnalyzer.BLOCKS.getEntries().stream():java.util.stream.Stream.empty());}
    private static void gasResearch(String key,Item item,int x,int y,AspectList aspects,String parent,boolean secondary) {
        var research=new ResearchItem(key,"THAUMICTINKERER",aspects,x,y,secondary?2:1,new ItemStack(item)) {
            @Override public String getName(){return Component.translatable("ttresearch.name."+key).getString();}
            @Override public String getText(){return Component.translatable("ttresearch.lore."+key).getString();}
        }.setParents(parent).setPages(new ResearchPage("ttresearch.page."+key+".0"),cruciblePage(key));
        if(secondary)research.setSecondary();research.registerResearchItem();
    }
    private static void registerGasRemoverRecipe() {
        var ingredients=java.util.List.of(net.minecraft.world.item.crafting.Ingredient.of(DARK_QUARTZ.get()),net.minecraft.world.item.crafting.Ingredient.of(DARK_QUARTZ.get()),net.minecraft.world.item.crafting.Ingredient.of(DARK_QUARTZ.get()),net.minecraft.world.item.crafting.Ingredient.of(SHADOW_BOTTLE.get()),net.minecraft.world.item.crafting.Ingredient.EMPTY,net.minecraft.world.item.crafting.Ingredient.of(LIGHT_BOTTLE.get()),net.minecraft.world.item.crafting.Ingredient.of(Items.QUARTZ),net.minecraft.world.item.crafting.Ingredient.of(Items.QUARTZ),net.minecraft.world.item.crafting.Ingredient.of(Items.QUARTZ));
        var predicates=new java.util.ArrayList<java.util.function.Predicate<ItemStack>>();
        for(var ingredient:ingredients)predicates.add(ingredient.isEmpty()?null:ingredient::test);
        var cost=new AspectList().add(thaumcraft.api.aspects.Aspect.AIR,2).add(thaumcraft.api.aspects.Aspect.ORDER,2);
        ARCANE_RECIPES.add(new dev.eugheist.tc4modern.ArcaneRecipes.Entry("GAS_REMOVER","GAS_REMOVER",3,3,ingredients,new ItemStack(GAS_REMOVER.get()),cost,new thaumcraft.api.crafting.ArcanePattern<ItemStack>("GAS_REMOVER",3,3,predicates).setMirrored(true)));
    }
    private static ResearchPage arcanePage(String key) {
        var page=new ResearchPage("");page.type=ResearchPage.PageType.ARCANE_CRAFTING;page.recipeKey=key;return page;
    }
    private static ResearchPage craftingPage(String id) {
        var page=new ResearchPage("");page.type=ResearchPage.PageType.NORMAL_CRAFTING;page.recipeKey=ID+":"+id;return page;
    }
    private static ResearchPage cruciblePage(String key) {
        var page=new ResearchPage("");page.type=ResearchPage.PageType.CRUCIBLE_CRAFTING;page.recipeKey=key;return page;
    }
    private void setup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            if(net.neoforged.fml.ModList.get().isLoaded("ae2"))AE2Compat.init();
            // A vis charge relay above the osmotic enchanter recharges its wand like the arcane workbench slot.
            dev.eugheist.tc4modern.api.TC4Addons.registerWandHolder(tile->tile instanceof EnchanterEntity enchanter?enchanter.inventory.getStackInSlot(1):null);
            // Base initialization must run first: TC4's lazy init tests an empty category map.
            thaumcraft.common.config.ConfigResearchData.init();
            ResearchCategories.registerCategory("THAUMICTINKERER",
                ResourceLocation.fromNamespaceAndPath(ID, "textures/items/darkquartzitem.png"),
                ResourceLocation.fromNamespaceAndPath("tc4modern", "textures/gui/gui_researchback.png"));
            new ResearchItem("DARK_QUARTZ", "THAUMICTINKERER", new AspectList(), -2, 2, 0, new ItemStack(DARK_QUARTZ.get())) {
                @Override public String getName() { return Component.translatable("ttresearch.name.DARK_QUARTZ").getString(); }
                @Override public String getText() { return Component.translatable("ttresearch.lore.DARK_QUARTZ").getString(); }
            }.setPages(new ResearchPage("ttresearch.page.DARK_QUARTZ.0"),craftingPage("dark_quartz_coal"),craftingPage("dark_quartz_block"),craftingPage("dark_quartz_slab"),craftingPage("dark_quartz_pillar"),craftingPage("chiseled_dark_quartz"),craftingPage("dark_quartz_stairs")).setStub().setAutoUnlock().setRound().registerResearchItem();
            new ResearchItem("SPELL_CLOTH","THAUMICTINKERER",new AspectList().add(thaumcraft.api.aspects.Aspect.MAGIC,2).add(thaumcraft.api.aspects.Aspect.CLOTH,1),3,2,2,new ItemStack(SPELL_CLOTH.get())) {
                @Override public String getName(){return Component.translatable("ttresearch.name.SPELL_CLOTH").getString();}
                @Override public String getText(){return Component.translatable("ttresearch.lore.SPELL_CLOTH").getString();}
            }.setParentsHidden("ENCHFABRIC").setPages(new ResearchPage("ttresearch.page.SPELL_CLOTH.0"),cruciblePage("SPELL_CLOTH")).registerResearchItem();
            gasResearch("GASEOUS_LIGHT",LIGHT_BOTTLE.get(),0,-3,new AspectList().add(thaumcraft.api.aspects.Aspect.LIGHT,2).add(thaumcraft.api.aspects.Aspect.AIR,1),"NITOR",false);
            gasResearch("GASEOUS_SHADOW",SHADOW_BOTTLE.get(),-1,-5,new AspectList().add(thaumcraft.api.aspects.Aspect.DARKNESS,2).add(thaumcraft.api.aspects.Aspect.AIR,1).add(thaumcraft.api.aspects.Aspect.MOTION,4),"GASEOUS_LIGHT",true);
            CRUCIBLE_RECIPES.put("GASEOUS_LIGHT",new thaumcraft.api.crafting.CrucibleRecipe("GASEOUS_LIGHT",new ItemStack(LIGHT_BOTTLE.get()),net.minecraft.world.item.crafting.Ingredient.of(net.minecraft.core.registries.BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("tc4modern","phial"))),new AspectList().add(thaumcraft.api.aspects.Aspect.LIGHT,16).add(thaumcraft.api.aspects.Aspect.AIR,10).add(thaumcraft.api.aspects.Aspect.MOTION,8)));
            CRUCIBLE_RECIPES.put("GASEOUS_SHADOW",new thaumcraft.api.crafting.CrucibleRecipe("GASEOUS_SHADOW",new ItemStack(SHADOW_BOTTLE.get()),net.minecraft.world.item.crafting.Ingredient.of(net.minecraft.core.registries.BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("tc4modern","phial"))),new AspectList().add(thaumcraft.api.aspects.Aspect.DARKNESS,16).add(thaumcraft.api.aspects.Aspect.AIR,10).add(thaumcraft.api.aspects.Aspect.MOTION,8)));
            gasResearch("BRIGHT_NITOR",BRIGHT_NITOR.get(),1,-5,new AspectList().add(thaumcraft.api.aspects.Aspect.LIGHT,2).add(thaumcraft.api.aspects.Aspect.FIRE,1).add(thaumcraft.api.aspects.Aspect.ENERGY,1).add(thaumcraft.api.aspects.Aspect.AIR,1),"GASEOUS_LIGHT",true);
            ResearchCategories.getResearch("BRIGHT_NITOR").setConcealed();
            new ResearchItem("GAS_REMOVER","THAUMICTINKERER",new AspectList().add(thaumcraft.api.aspects.Aspect.DARKNESS,2).add(thaumcraft.api.aspects.Aspect.LIGHT,2),-2,-7,0,new ItemStack(GAS_REMOVER.get())) {
                @Override public String getName(){return Component.translatable("ttresearch.name.GAS_REMOVER").getString();}
                @Override public String getText(){return Component.translatable("ttresearch.lore.GAS_REMOVER").getString();}
            }.setRound().setParents("GASEOUS_SHADOW").setPages(new ResearchPage("ttresearch.page.GAS_REMOVER.0"),arcanePage("GAS_REMOVER")).registerResearchItem();
            CRUCIBLE_RECIPES.put("BRIGHT_NITOR",new thaumcraft.api.crafting.CrucibleRecipe("BRIGHT_NITOR",new ItemStack(BRIGHT_NITOR.get()),net.minecraft.world.item.crafting.Ingredient.of(dev.eugheist.tc4modern.TC4Modern.NITOR.get()),new AspectList().add(thaumcraft.api.aspects.Aspect.ENERGY,25).add(thaumcraft.api.aspects.Aspect.LIGHT,25).add(thaumcraft.api.aspects.Aspect.AIR,10).add(thaumcraft.api.aspects.Aspect.FIRE,10)));
            registerGasRemoverRecipe(); InfusedFireResearch.register(); InfusedSeedRecipes.register(); InfusedPotionResearch.register(); if(net.neoforged.fml.ModList.get().isLoaded("computercraft"))ComputerCraftCompat.research(); if(KamiStartupConfig.enabled())KamiResources.research();
            BloodSwordResearch.register();
            SummonResearch.register(); LocomotiveResearch.register(); AnimationTabletResearch.register();
            ShareTomeResearch.register();
            TransvectorResearch.register();
            DislocatorResearch.register();
            XPTalismanResearch.register();
            FunnelResearch.register();
            PlatformResearch.register(); RevealingHelmResearch.register(); RepairerResearch.register(); EnchanterResearch.register(); MovementEnchantmentResearch.register();
            FocusResearch.register();
            MagnetResearch.register();
            var cleansing=new ResearchItem("CLEANSING_TALISMAN","THAUMICTINKERER",new AspectList().add(thaumcraft.api.aspects.Aspect.HEAL,2).add(thaumcraft.api.aspects.Aspect.ORDER,1).add(thaumcraft.api.aspects.Aspect.POISON,1),-3,4,3,new ItemStack(CLEANSING_TALISMAN.get())) {
                @Override public String getName(){return Component.translatable("ttresearch.name.CLEANSING_TALISMAN").getString();}
                @Override public String getText(){return Component.translatable("ttresearch.lore.CLEANSING_TALISMAN").getString();}
            }.setSecondary().setParents("DARK_QUARTZ").setParentsHidden("INFUSION");
            var infusionPage=new ResearchPage("");infusionPage.type=ResearchPage.PageType.INFUSION_CRAFTING;infusionPage.recipeKey="CLEANSING_TALISMAN";
            cleansing.setPages(new ResearchPage("ttresearch.page.CLEANSING_TALISMAN.0"),infusionPage).registerResearchItem();
            INFUSION_RECIPES.add(new dev.eugheist.tc4modern.InfusionItemRecipes.Recipe("CLEANSING_TALISMAN","CLEANSING_TALISMAN",CLEANSING_TALISMAN::get,5,new AspectList().add(thaumcraft.api.aspects.Aspect.HEAL,10).add(thaumcraft.api.aspects.Aspect.TOOL,10).add(thaumcraft.api.aspects.Aspect.MAN,20).add(thaumcraft.api.aspects.Aspect.LIFE,10),()->Items.ENDER_PEARL,java.util.List.of(DARK_QUARTZ::get,DARK_QUARTZ::get,DARK_QUARTZ::get,DARK_QUARTZ::get,()->Items.GHAST_TEAR,dev.eugheist.tc4modern.TC4Modern.NITOR::get)));
            // ItemInfusedInkwell.getRecipeItem: research INFUSED_INKWELL is never registered by the original, so only granted players can infuse it.
            INFUSION_RECIPES.add(new dev.eugheist.tc4modern.InfusionItemRecipes.Recipe("INFUSED_INKWELL","INFUSED_INKWELL",INFUSED_INKWELL::get,2,new AspectList().add(thaumcraft.api.aspects.Aspect.VOID,8).add(thaumcraft.api.aspects.Aspect.DARKNESS,8),dev.eugheist.tc4modern.TC4Modern.SCRIBING_TOOLS::get,java.util.List.of(()->dev.eugheist.tc4modern.TC4Modern.SHARDS.get(0).get(),()->dev.eugheist.tc4modern.EssentiaDevices.JAR.get().asItem(),dev.eugheist.tc4modern.AlchemyContent.QUICKSILVER::get)));
            CRUCIBLE_RECIPES.put("SPELL_CLOTH",new thaumcraft.api.crafting.CrucibleRecipe("SPELL_CLOTH",new ItemStack(SPELL_CLOTH.get()),net.minecraft.world.item.crafting.Ingredient.of(dev.eugheist.tc4modern.TC4Modern.ENCHANTED_FABRIC.get()),new AspectList().add(thaumcraft.api.aspects.Aspect.MAGIC,10).add(thaumcraft.api.aspects.Aspect.ENTROPY,6).add(thaumcraft.api.aspects.Aspect.EXCHANGE,4)));
            if(KamiStartupConfig.enabled()){RecallFocus.research(); WarpGateBlock.research(); SkyPearl.recipe(); ShadowbeamFocus.research(); XPDrainFocus.research(); PlacementMirror.research(); BlockTalisman.research(); CatAmulet.research(); IchorPouch.research(); KamiWands.register(); IchorResearch.register(); KamiMaterialResearch.register(); KamiTools.register(); AwakenedPick.research(); AwakenedShovel.research(); AwakenedAxe.research(); AwakenedSword.research(); Protoclay.research(); KamiArmor.research(); AwakenedHelm.research(); AwakenedChest.research(); AwakenedLegs.research(); AwakenedBoots.research();}
        });
    }
}
