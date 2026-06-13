package com.jerry.meklg.client.render;

import com.jerry.mekmm.common.base.MoreMachineProfilerConstants;

import mekanism.client.render.MekanismRenderer;
import mekanism.client.render.tileentity.IWireFrameRenderer;
import mekanism.client.render.tileentity.MekanismTileEntityRenderer;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import com.jerry.meklg.client.model.ModelSolarHeatGenerator;
import com.jerry.meklg.client.model.ModelSolarHeatGenerator.SolarHeatGeneratorRotationRenderState;
import com.jerry.meklg.client.render.RenderSolarHeatGenerator.SolarHeatGeneratorRenderState;
import com.jerry.meklg.common.tile.generator.TileEntitySolarHeatGenerator;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class RenderSolarHeatGenerator extends MekanismTileEntityRenderer<TileEntitySolarHeatGenerator, SolarHeatGeneratorRenderState> implements IWireFrameRenderer {

    private final ModelSolarHeatGenerator model;

    public RenderSolarHeatGenerator(Context context) {
        super(context);
        model = new ModelSolarHeatGenerator(context.entityModelSet());
    }

    @Override
    public void renderWireFrame(BlockEntity tile, BlockState blockState, float partialTick, PoseStack poseStack, VertexConsumer buffer, boolean isHighContrast) {
        if (tile instanceof TileEntitySolarHeatGenerator solarHeatGenerator) {
            poseStack.pushPose();
            poseStack.translate(0.5, 1.5, 0.5);
            MekanismRenderer.rotate(poseStack, solarHeatGenerator.getDirection(), 0, 180, 90, 270);
            poseStack.mulPose(Axis.ZP.rotationDegrees(180));
            model.renderWireFrame(poseStack, buffer, new SolarHeatGeneratorRotationRenderState(solarHeatGenerator.getAngle()), getPanelVisibility(solarHeatGenerator), isHighContrast);
            poseStack.popPose();
        }
    }

    @Override
    public void submit(SolarHeatGeneratorRenderState state, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        if (state.direction != null) {
            poseStack.pushPose();
            poseStack.translate(0.5, 1.5, 0.5);
            MekanismRenderer.rotate(poseStack, state.direction, 0, 180, 90, 270);
            poseStack.mulPose(Axis.ZP.rotationDegrees(180));
            model.collectBlock(state.rotation, poseStack, nodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.panels);
            poseStack.popPose();
        }
    }

    @Override
    public SolarHeatGeneratorRenderState createRenderState() {
        return new SolarHeatGeneratorRenderState();
    }

    @Override
    public void extractRenderState(TileEntitySolarHeatGenerator tile, SolarHeatGeneratorRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@org.jspecify.annotations.Nullable CrumblingOverlay breakProgress) {
        super.extractRenderState(tile, state, partialTicks, cameraPosition, breakProgress);
        state.direction = tile.getDirection();
        state.rotation.angle = tile.getAngle();
        for (int slot = 0; slot < state.panels.length; slot++) {
            state.panels[slot] = tile.shouldRenderPanel(slot);
        }
    }

    @Override
    protected String getProfilerSection() {
        return MoreMachineProfilerConstants.SOLAR_HEAT_GENERATOR;
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(TileEntitySolarHeatGenerator tile) {
        BlockPos pos = tile.getBlockPos();
        return AABB.encapsulatingFullBlocks(pos.offset(-3, 0, -3), pos.offset(3, 7, 3));
    }

    private boolean[] getPanelVisibility(TileEntitySolarHeatGenerator tile) {
        boolean[] panels = new boolean[TileEntitySolarHeatGenerator.SLOT_COUNT];
        for (int slot = 0; slot < panels.length; slot++) {
            panels[slot] = tile.shouldRenderPanel(slot);
        }
        return panels;
    }

    public static class SolarHeatGeneratorRenderState extends BlockEntityRenderState {

        public final boolean[] panels = new boolean[TileEntitySolarHeatGenerator.SLOT_COUNT];
        public SolarHeatGeneratorRotationRenderState rotation = new SolarHeatGeneratorRotationRenderState(0);
        @Nullable
        public Direction direction;
    }
}
