package com.jerry.meklg.client.render.item;

import com.jerry.meklm.client.model.LargeMachineModelCache;

import com.jerry.mekmm.Mekmm;

import mekanism.client.render.item.MekanismISTER;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.StandaloneGeometryBakingContext;

import com.jerry.meklg.client.model.ModelSolarHeatGenerator;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class RenderSolarHeatGeneratorItem extends MekanismISTER {

    private static final ResourceLocation SCREEN_OFF_TEXTURE = Mekmm.rl("block/large_machine/screen_off");
    private static final IGeometryBakingContext BASE_CONFIGURATION = StandaloneGeometryBakingContext.builder()
            .withGui3d(false)
            .withUseBlockLight(false)
            .withUseAmbientOcclusion(false)
            .withTextures(Map.of(
                    "#Screen1", SCREEN_OFF_TEXTURE,
                    "#Screen2", SCREEN_OFF_TEXTURE,
                    "#Screen3", SCREEN_OFF_TEXTURE), SCREEN_OFF_TEXTURE)
            .build(Mekmm.rl("solar_heat_generator_screen"));

    public static final RenderSolarHeatGeneratorItem RENDERER = new RenderSolarHeatGeneratorItem();
    private static final RandomSource RANDOM = RandomSource.create();

    private ModelSolarHeatGenerator solarHeatGenerator;

    @Override
    public void onResourceManagerReload(@NotNull ResourceManager resourceManager) {
        solarHeatGenerator = new ModelSolarHeatGenerator(getEntityModels());
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext displayContext, @NotNull PoseStack matrix, @NotNull MultiBufferSource renderer,
                             int light, int overlayLight) {
        matrix.pushPose();
        matrix.translate(0, -1, 0);
        renderBase(matrix, renderer, RANDOM, light, overlayLight);
        matrix.popPose();

        matrix.pushPose();
        matrix.translate(0.5, 0.5, 0.5);
        matrix.mulPose(Axis.ZP.rotationDegrees(180));
        solarHeatGenerator.renderItem(matrix, renderer, 0, light, overlayLight, stack.hasFoil());
        matrix.popPose();
    }

    public static void renderBase(PoseStack matrix, MultiBufferSource renderer, RandomSource random, int light, int overlayLight) {
        PoseStack.Pose entry = matrix.last();
        VertexConsumer buffer = renderer.getBuffer(Sheets.solidBlockSheet());
        for (BakedQuad quad : getBaseModel().getQuads(null, null, random, ModelData.EMPTY, null)) {
            buffer.putBulkData(entry, quad, 1, 1, 1, 1, light, overlayLight);
        }
    }

    private static BakedModel getBaseModel() {
        return LargeMachineModelCache.INSTANCE.SOLAR_HEAT_GENERATOR.bake(BASE_CONFIGURATION);
    }
}
