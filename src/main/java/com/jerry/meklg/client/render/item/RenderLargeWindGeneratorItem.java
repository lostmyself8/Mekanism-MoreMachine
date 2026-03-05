package com.jerry.meklg.client.render.item;

import mekanism.client.render.item.MekanismISTER;
import mekanism.generators.common.config.MekanismGeneratorsConfig;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import com.jerry.meklg.client.model.ModelLargeWindGenerator;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RenderLargeWindGeneratorItem extends MekanismISTER {

    public static final RenderLargeWindGeneratorItem RENDERER = new RenderLargeWindGeneratorItem();
    private static float lastTicksUpdated = 0;
    private static int angle = 0;
    private ModelLargeWindGenerator largeWindGenerator;

    @Override
    public void onResourceManagerReload(@NotNull ResourceManager resourceManager) {
        largeWindGenerator = new ModelLargeWindGenerator(getEntityModels());
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext displayContext, @NotNull PoseStack matrix, @NotNull MultiBufferSource renderer,
                             int light, int overlayLight) {
        float renderPartialTicks = Minecraft.getInstance().getFrameTime();
        if (lastTicksUpdated != renderPartialTicks) {
            // Only update the angle if we are in a world and that world is not blacklisted
            if (Minecraft.getInstance().level != null) {
                List<ResourceLocation> blacklistedDimensions = MekanismGeneratorsConfig.generators.windGenerationDimBlacklist.get();
                if (blacklistedDimensions.isEmpty() || !blacklistedDimensions.contains(Minecraft.getInstance().level.dimension().location())) {
                    angle = (angle + 2) % 360;
                }
            }
            lastTicksUpdated = renderPartialTicks;
        }
        matrix.pushPose();
        matrix.translate(0.5, 0.5, 0.5);
        matrix.mulPose(Axis.ZP.rotationDegrees(180));
        largeWindGenerator.render(matrix, renderer, angle, light, overlayLight, stack.hasFoil());
        matrix.popPose();
    }
}
