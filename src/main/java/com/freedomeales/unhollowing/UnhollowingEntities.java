package com.freedomeales.unhollowing;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class UnhollowingEntities {

    // Create the deferred register for entity types
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, "unhollowing");

    // Register the Watcher entity
    public static final RegistryObject<EntityType<WatcherEntity>> WATCHER =
            ENTITIES.register("watcher", () ->
                    EntityType.Builder.<WatcherEntity>of(WatcherEntity::new, MobCategory.MONSTER)
                            .sized(1.0F, 3.0F)
                            .build("watcher")
            );

    // Register the Forgotten entity
    public static final RegistryObject<EntityType<ForgottenEntity>> FORGOTTEN =
            ENTITIES.register("forgotten", () ->
                    EntityType.Builder.<ForgottenEntity>of(ForgottenEntity::new, MobCategory.MONSTER)
                            .sized(0.8F, 3.5F)
                            .build("forgotten")
            );
}
