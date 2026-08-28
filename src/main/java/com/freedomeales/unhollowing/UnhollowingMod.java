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
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;

import com.freedomeales.unhollowing.UnhollowingEntities;


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
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;



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

        public static final RegistryObject<Block> REDWOOD_LOG = registerBlock("redwood_log",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED)
                .strength(3.0F).sound(net.minecraft.world.level.block.SoundType.WOOD)));
        public static final RegistryObject<Block> REDWOOD_LEAVES = registerBlock("redwood_leaves",
            () -> new LeavesBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN)
                .strength(0.2F).randomTicks().sound(net.minecraft.world.level.block.SoundType.GRASS)
                .noOcclusion()));
        public static final RegistryObject<Block> REDWOOD_PLANKS = registerBlock("redwood_planks",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED)
                .strength(2.0F).sound(net.minecraft.world.level.block.SoundType.WOOD)));
        public static final RegistryObject<Block> BLACKWOOD_LOG = registerBlock("blackwood_log",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK)
                .strength(4.0F).sound(net.minecraft.world.level.block.SoundType.WOOD)));
        public static final RegistryObject<Block> BLACKWOOD_LEAVES = registerBlock("blackwood_leaves",
            () -> new LeavesBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK)
                .strength(0.2F).randomTicks().sound(net.minecraft.world.level.block.SoundType.GRASS)
                .noOcclusion()));
        public static final RegistryObject<Block> BLACKWOOD_PLANKS = registerBlock("blackwood_planks",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK)
                .strength(2.0F).sound(net.minecraft.world.level.block.SoundType.WOOD)));
        public static final RegistryObject<Block> BLACKBARK = registerBlock("blackbark",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK)
                .strength(4.0F).sound(net.minecraft.world.level.block.SoundType.WOOD)));

        public static final RegistryObject<SoundEvent> FORGOTTEN_CALL = registerSound("forgotten_call");
        public static final RegistryObject<SoundEvent> FORGOTTEN_HURT = registerSound("forgotten_hurt");
        public static final RegistryObject<SoundEvent> FORGOTTEN_DEATH = registerSound("forgotten_death");
        public static final RegistryObject<SoundEvent> CAVE_FOOTSTEPS = registerSound("cave_footsteps");
        public static final RegistryObject<SoundEvent> CAVE_BREATHING = registerSound("cave_breathing");

        public static final DeferredRegister<Biome> BIOMES = DeferredRegister.create(ForgeRegistries.BIOMES, MOD_ID);
        public static final RegistryObject<Biome> REDWOOD_FOREST = BIOMES.register("redwood_forest",
            () -> new Biome.BiomeBuilder().temperature(0.7F).downfall(0.8F)
                .specialEffects(new BiomeSpecialEffects.Builder().fogColor(0x4A5147).waterColor(0x385F65)
                    .waterFogColor(0x1B2525).skyColor(0x56605A).build())
                .mobSpawnSettings(new MobSpawnSettings.Builder().build())
                .generationSettings(BiomeGenerationSettings.EMPTY).build());
        public static final RegistryObject<Biome> BLACKWOOD_FOREST = BIOMES.register("blackwood_forest",
            () -> new Biome.BiomeBuilder().temperature(0.3F).downfall(0.9F)
                .specialEffects(new BiomeSpecialEffects.Builder().fogColor(0x171A18).waterColor(0x202D31)
                    .waterFogColor(0x090C0C).skyColor(0x202422).build())
                .mobSpawnSettings(new MobSpawnSettings.Builder().build())
                .generationSettings(BiomeGenerationSettings.EMPTY).build());

public UnhollowingMod() {
    UnhollowingEntities.ENTITIES.register(
        net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext.get().getModEventBus()
    );
}


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
        BIOMES.register(modEventBus);
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