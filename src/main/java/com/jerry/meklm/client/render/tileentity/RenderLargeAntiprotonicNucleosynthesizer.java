package com.jerry.meklm.client.render.tileentity;

import com.jerry.meklm.client.render.tileentity.RenderLargeAntiprotonicNucleosynthesizer.LargeAntiprotonicNucleosynthesizerRenderState;
import com.jerry.meklm.common.tile.machine.TileEntityLargeAntiprotonicNucleosynthesizer;

import com.jerry.mekmm.common.base.MoreMachineProfilerConstants;

import mekanism.api.text.EnumColor;
import mekanism.client.model.ModelEnergyCore;
import mekanism.client.render.tileentity.MekanismTileEntityRenderer;
import mekanism.client.render.tileentity.RenderEnergyCube;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.phys.Vec3;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class RenderLargeAntiprotonicNucleosynthesizer extends MekanismTileEntityRenderer<@NotNull TileEntityLargeAntiprotonicNucleosynthesizer, LargeAntiprotonicNucleosynthesizerRenderState> {

    private final ModelEnergyCore core;

    public RenderLargeAntiprotonicNucleosynthesizer(Context context) {
        super(context);
        core = new ModelEnergyCore(context.entityModelSet());
    }

    @Override
    public LargeAntiprotonicNucleosynthesizerRenderState createRenderState() {
        return new LargeAntiprotonicNucleosynthesizerRenderState();
    }

    @Override
    public void extractRenderState(TileEntityLargeAntiprotonicNucleosynthesizer tile, LargeAntiprotonicNucleosynthesizerRenderState state, float partialTick,
                                   Vec3 cameraPosition, @Nullable ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        super.extractRenderState(tile, state, partialTick, cameraPosition, breakProgress);
        state.direction = tile.getDirection();
        state.ticks = tile.getLevel().getGameTime() + partialTick;
    }

    @Override
    public void submit(LargeAntiprotonicNucleosynthesizerRenderState state, PoseStack matrix, SubmitNodeCollector nodeCollector, CameraRenderState camera) {
        if (state.direction == null) {
            return;
        }
        float scaledTicks = 4 * state.ticks;
        matrix.pushPose();
        switch (state.direction) {
            case NORTH -> matrix.translate(2.5 / 16, 1.5, 2.5 / 16);
            case SOUTH -> matrix.translate(13.5 / 16, 1.5, 13.5 / 16);
            case WEST -> matrix.translate(2.5 / 16, 1.5, 13.5 / 16F);
            case EAST -> matrix.translate(13.5 / 16F, 1.5, 2.5 / 16);
        }
        matrix.scale(0.5F, 0.5F, 0.5F);
        matrix.mulPose(Axis.YP.rotationDegrees(scaledTicks));
        matrix.mulPose(RenderEnergyCube.coreVec.rotationDegrees(36F + scaledTicks));
        core.collect(EnumColor.PURPLE.getPackedColor(), matrix, nodeCollector, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, false);
        matrix.popPose();
    }

    @Override
    public boolean shouldRender(TileEntityLargeAntiprotonicNucleosynthesizer blockEntity, Vec3 cameraPos) {
        return blockEntity.getActive() && super.shouldRender(blockEntity, cameraPos);
    }

    @Override
    protected String getProfilerSection() {
        return MoreMachineProfilerConstants.LARGE_ANTIPROTONIC_NUCLEOSYNTHESIZER;
    }

    public static class LargeAntiprotonicNucleosynthesizerRenderState extends BlockEntityRenderState {

        @Nullable
        public Direction direction;
        public float ticks;
    }
}
