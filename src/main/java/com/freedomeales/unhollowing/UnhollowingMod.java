package com.freedomeales.unhollowing;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(UnhollowingMod.MOD_ID)
@SuppressWarnings("null")
public final class UnhollowingMod {
    public static final String MOD_ID = "unhollowing";

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MOD_ID);
        public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
        public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
            public static final DeferredRegister<SoundEvent> SOUNDS =
                DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MOD_ID);
            public static final DeferredRegister<ConfiguredFeature<?, ?>> CONFIGURED_FEATURES =
                DeferredRegister.create(Registries.CONFIGURED_FEATURE, MOD_ID);
            public static final DeferredRegister<PlacedFeature> PLACED_FEATURES =
                DeferredRegister.create(Registries.PLACED_FEATURE, MOD_ID);

        public static final RegistryObject<Block> REDWOOD_LOG = registerBlock("redwood_log",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN)
                .strength(3.0F).sound(net.minecraft.world.level.block.SoundType.WOOD)));
        public static final RegistryObject<Block> REDWOOD_LEAVES = registerBlock("redwood_leaves",
            () -> new LeavesBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN)
                .strength(0.2F).randomTicks().sound(net.minecraft.world.level.block.SoundType.GRASS)
                .noOcclusion()));
        public static final RegistryObject<Block> BLACKBARK = registerBlock("blackbark",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK)
                .strength(4.0F).sound(net.minecraft.world.level.block.SoundType.WOOD)));

        public static final RegistryObject<SoundEvent> FORGOTTEN_CALL = registerSound("forgotten_call");
        public static final RegistryObject<SoundEvent> FORGOTTEN_HURT = registerSound("forgotten_hurt");
        public static final RegistryObject<SoundEvent> FORGOTTEN_DEATH = registerSound("forgotten_death");

            public static final RegistryObject<ConfiguredFeature<?, ?>> REDWOOD_TREE = CONFIGURED_FEATURES.register(
                "redwood_tree", () -> new ConfiguredFeature<>(Feature.TREE,
                    new TreeConfiguration.TreeConfigurationBuilder(
                        BlockStateProvider.simple(REDWOOD_LOG.get()),
                        new StraightTrunkPlacer(24, 8, 8),
                        BlockStateProvider.simple(REDWOOD_LEAVES.get()),
                            new BlobFoliagePlacer(ConstantInt.of(4), ConstantInt.of(2), 4),
                        new TwoLayersFeatureSize(2, 1, 2))
                        .build()));
            public static final RegistryObject<PlacedFeature> REDWOOD_TREE_PLACED = PLACED_FEATURES.register(
                "redwood_tree", () -> new PlacedFeature(REDWOOD_TREE.getHolder().get(),
                    java.util.List.of(CountPlacement.of(1), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP,
                        BiomeFilter.biome())));

    public static final RegistryObject<EntityType<WatcherEntity>> WATCHER = ENTITY_TYPES.register("watcher",
            () -> EntityType.Builder.of(WatcherEntity::new, MobCategory.MONSTER)
                    .sized(0.45F, 2.7F)
                    .clientTrackingRange(8)
                    .build(MOD_ID + ":watcher"));

    public UnhollowingMod(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        ENTITY_TYPES.register(modEventBus);
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        SOUNDS.register(modEventBus);
        CONFIGURED_FEATURES.register(modEventBus);
        PLACED_FEATURES.register(modEventBus);
        modEventBus.addListener(this::registerAttributes);
    }

    private static <T extends Block> RegistryObject<T> registerBlock(String name,
            java.util.function.Supplier<T> blockSupplier) {
        RegistryObject<T> block = BLOCKS.register(name, blockSupplier);
        ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        return block;
    }

    @SuppressWarnings("removal")
    private static RegistryObject<SoundEvent> registerSound(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(
                new ResourceLocation(MOD_ID + ":" + name)));
    }

    private void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(WATCHER.get(), WatcherEntity.createAttributes().build());
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static final class ModEvents {
        private ModEvents() {
        }

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> net.minecraft.client.renderer.entity.EntityRenderers.register(
                    WATCHER.get(), WatcherRenderer::new));
        }
    }
}