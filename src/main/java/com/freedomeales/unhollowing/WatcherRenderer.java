package com.freedomeales.unhollowing;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import javax.annotation.Nonnull;
import java.util.Objects;

@SuppressWarnings({"removal", "null"})
public final class WatcherRenderer extends MobRenderer<WatcherEntity, HumanoidModel<WatcherEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(
            "minecraft", "textures/entity/enderman/enderman.png");

    public WatcherRenderer(@Nonnull EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(Objects.requireNonNull(context.bakeLayer(ModelLayers.PLAYER))), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(@Nonnull WatcherEntity entity) {
        return TEXTURE;
    }
}