package com.freedomeales.unhollowing;



import com.freedomeales.unhollowing.WatcherEntity;
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
}
