package com.jerry.meklg.client.render;

import com.jerry.mekmm.common.base.MoreMachineProfilerConstants;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.render.tileentity.IWireFrameRenderer;
import mekanism.client.render.tileentity.MekanismTileEntityRenderer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;

import com.jerry.meklg.client.model.ModelSolarHeatGenerator;
import com.jerry.meklg.common.tile.generator.TileEntitySolarHeatGenerator;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

@NothingNullByDefault
public class RenderSolarHeatGenerator extends MekanismTileEntityRenderer<TileEntitySolarHeatGenerator> implements IWireFrameRenderer {

    private final ModelSolarHeatGenerator model;

    public RenderSolarHeatGenerator(Context context) {
        super(context);
        model = new ModelSolarHeatGenerator(context.getModelSet());
    }

    @Override
    public void renderWireFrame(BlockEntity tile, float partialTick, PoseStack matrix, VertexConsumer buffer) {
        if (tile instanceof TileEntitySolarHeatGenerator solarHeatGenerator) {
            float angle = setupRenderer(solarHeatGenerator, matrix);
            model.renderWireFrame(matrix, buffer, angle,
                  hasPanel(solarHeatGenerator, 0), hasPanel(solarHeatGenerator, 1), hasPanel(solarHeatGenerator, 2), hasPanel(solarHeatGenerator, 3));
            matrix.popPose();
        }
    }

    @Override
    protected void render(TileEntitySolarHeatGenerator tile, float partialTick, PoseStack matrix, MultiBufferSource renderer, int light, int overlayLight, ProfilerFiller profiler) {
        float angle = setupRenderer(tile, matrix);
        model.renderBlock(matrix, renderer, angle, light, overlayLight, false,
              hasPanel(tile, 0), hasPanel(tile, 1), hasPanel(tile, 2), hasPanel(tile, 3));
        matrix.popPose();
    }

    @Override
    protected String getProfilerSection() {
        return MoreMachineProfilerConstants.SOLAR_HEAT_GENERATOR;
    }

    @Override
    public boolean shouldRenderOffScreen(TileEntitySolarHeatGenerator tile) {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(TileEntitySolarHeatGenerator tile) {
        BlockPos pos = tile.getBlockPos();
        return AABB.encapsulatingFullBlocks(pos.offset(-3, 0, -3), pos.offset(3, 7, 3));
    }

    private float setupRenderer(TileEntitySolarHeatGenerator tile, PoseStack matrix) {
        matrix.pushPose();
        matrix.translate(0.5, 1.5, 0.5);
        MekanismRenderer.rotate(matrix, tile.getDirection(), 0, 180, 90, 270);
        matrix.mulPose(Axis.ZP.rotationDegrees(180));
        return tile.getAngle();
    }

    private boolean hasPanel(TileEntitySolarHeatGenerator tile, int slot) {
        return !tile.getItemStack(slot).isEmpty();
    }
}
