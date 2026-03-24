package com.jerry.meklg.client.render;

import com.jerry.mekmm.common.base.MoreMachineProfilerConstants;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.render.tileentity.IWireFrameRenderer;
import mekanism.client.render.tileentity.ModelTileEntityRenderer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.entity.BlockEntity;

import com.jerry.meklg.client.model.ModelLargeWindGenerator;
import com.jerry.meklg.common.tile.TileEntityLargeWindGenerator;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

@NothingNullByDefault
public class RenderLargeWindGenerator extends ModelTileEntityRenderer<TileEntityLargeWindGenerator, ModelLargeWindGenerator> implements IWireFrameRenderer {

    public RenderLargeWindGenerator(Context context) {
        super(context, ModelLargeWindGenerator::new);
    }

    @Override
    public void renderWireFrame(BlockEntity tile, float partialTick, PoseStack matrix, VertexConsumer buffer, int red, int green, int blue, int alpha) {
        if (tile instanceof TileEntityLargeWindGenerator windGenerator) {
            renderTranslated(windGenerator, partialTick, matrix, (poseStack, angle) -> model.renderWireFrame(poseStack, buffer, angle, red, green, blue, alpha));
        }
    }

    @Override
    protected void render(TileEntityLargeWindGenerator tile, float partialTick, PoseStack matrix, MultiBufferSource renderer, int light, int overlayLight, ProfilerFiller profiler) {
        renderTranslated(tile, partialTick, matrix, (poseStack, angle) -> model.render(poseStack, renderer, angle, light, overlayLight, false));
    }

    @Override
    public boolean shouldRenderOffScreen(TileEntityLargeWindGenerator blockEntity) {
        return true;
    }

    @Override
    protected String getProfilerSection() {
        return MoreMachineProfilerConstants.LARGE_WIND_GENERATOR;
    }

    private void renderTranslated(TileEntityLargeWindGenerator tile, float partialTick, PoseStack matrix, LargeWindGeneratorRenderer renderer) {
        matrix.pushPose();
        matrix.translate(0.5, 1.5, 0.5);
        MekanismRenderer.rotate(matrix, tile.getDirection(), 0, 180, 90, 270);
        matrix.mulPose(Axis.ZP.rotationDegrees(180));
        double angle = tile.getAngle();
        if (tile.getActive()) {
            angle = (tile.getAngle() + ((tile.getBlockPos().getY() + TileEntityLargeWindGenerator.TOP_Y) / TileEntityLargeWindGenerator.SPEED_SCALED) * partialTick) % 360;
        }
        renderer.render(matrix, angle);
        matrix.popPose();
    }

    private interface LargeWindGeneratorRenderer {

        void render(PoseStack poseStack, double angle);
    }
}
