package com.freedomeales.unhollowing;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.MultiBufferSource;
import java.util.Objects;

@SuppressWarnings({"removal", "null"})
public class ForgottenRenderer extends MobRenderer<ForgottenEntity, HumanoidModel<ForgottenEntity>> {

    private static final ResourceLocation FORGOTTEN_TEXTURE = 
        new ResourceLocation("unhollowing", "textures/entity/forgotten.png");
    private static final ResourceLocation DISTORTED_TEXTURE = 
        new ResourceLocation("unhollowing", "textures/entity/forgotten_distorted.png");

    public ForgottenRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(Objects.requireNonNull(context.bakeLayer(ModelLayers.PLAYER))), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(ForgottenEntity entity) {
        // Use distorted texture when phasing
        if (entity.isPhasing()) {
            return DISTORTED_TEXTURE;
        }
        return FORGOTTEN_TEXTURE;
    }

    @Override
    public void render(ForgottenEntity entity, float entityYaw, float partialTicks, 
                      PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        
        // Apply distortion effect if phasing
        if (entity.isPhasing()) {
            applyDistortionShader(poseStack, entity);
        }

        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);

        if (entity.isPhasing()) {
            resetDistortionShader();
        }
    }

    /**
     * Apply distortion/glitch shader effect
     */
    private void applyDistortionShader(PoseStack poseStack, ForgottenEntity entity) {
        // Create glitch/distortion effect
        float distortion = (float) Math.sin(entity.tickCount * 0.15F) * 0.05F;
        
        poseStack.pushPose();
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(distortion * 360.0F));
        
        // Slight scale variation for distortion
        float scaleVar = 1.0F + distortion * 0.1F;
        poseStack.scale(scaleVar, 1.0F - distortion * 0.05F, scaleVar);
        
        // Set partial transparency
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.7F - distortion * 0.2F);
    }

    /**
     * Reset shader after distortion
     */
    private void resetDistortionShader() {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * Render glowing eyes at night
     */
    @Override
    protected void setupRotations(ForgottenEntity entity, PoseStack poseStack, float bobAmount, float yBodyRot, float partialTicks) {
        super.setupRotations(entity, poseStack, bobAmount, yBodyRot, partialTicks);
        
        // Eyes glow at night
        if (entity.level().isNight()) {
            renderGlowingEyes(entity, poseStack);
        }
    }

    /**
     * Render glowing white eyes for horror effect
     */
    private void renderGlowingEyes(ForgottenEntity entity, PoseStack poseStack) {
        // Render white glowing effect around eyes
        poseStack.pushPose();
        poseStack.translate(0.0D, 3.0D, 0.5D);
        
        // Glow intensity varies with sin wave
        float glowIntensity = 0.5F + 0.5F * (float) Math.sin(entity.tickCount * 0.1F);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, glowIntensity);
        
        poseStack.popPose();
    }

    /**
     * Should render shadow - less shadow when phasing
     */
    @Override
    protected boolean shouldShowName(ForgottenEntity entity) {
        return false; // Don't show name plate
    }
}
