package com.jerry.meklm.client.render.tileentity;

import com.jerry.meklm.common.tile.machine.TileEntityLargePigmentMixer;

import com.jerry.mekmm.common.base.MoreMachineProfilerConstants;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.client.render.RenderTickHandler;
import mekanism.client.render.lib.Outlines.Line;
import mekanism.client.render.tileentity.IWireFrameRenderer;
import mekanism.client.render.tileentity.MekanismTileEntityRenderer;
import mekanism.common.block.attribute.Attribute;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@NothingNullByDefault
public class RenderLargePigmentMixer extends MekanismTileEntityRenderer<TileEntityLargePigmentMixer, RenderLargePigmentMixer.LargePigmentMixerRenderState> implements IWireFrameRenderer {

    private static final float SHAFT_SPEED = 5F;
    @Nullable
    private static List<Line> lines;

    public static void resetCached() {
        lines = null;
    }

    public RenderLargePigmentMixer(Context context) {
        super(context);
    }

    @Override
    public LargePigmentMixerRenderState createRenderState() {
        return new LargePigmentMixerRenderState();
    }

    @Override
    public void extractRenderState(TileEntityLargePigmentMixer mixer, LargePigmentMixerRenderState state, float partialTick, Vec3 cameraPosition,
                                   @Nullable ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        super.extractRenderState(mixer, state, partialTick, cameraPosition, breakProgress);
        state.direction = mixer.getDirection();
        state.rotation = (mixer.getLevel().getGameTime() + partialTick) * SHAFT_SPEED % 360;
    }

    @Override
    public void submit(LargePigmentMixerRenderState state, PoseStack matrix, SubmitNodeCollector nodeCollector, CameraRenderState camera) {
        if (state.direction == null) {
            return;
        }
        setupRenderer(state.direction, state.rotation, matrix);
        matrix.popPose();
    }

    @Override
    public void renderWireFrame(BlockEntity tile, BlockState blockState, float partialTick, PoseStack poseStack, VertexConsumer buffer, boolean isHighContrast) {
        if (tile instanceof TileEntityLargePigmentMixer mixer) {
            setupRenderer(mixer.getDirection(), (tile.getLevel().getGameTime() + partialTick) * SHAFT_SPEED % 360, poseStack);
            Pose pose = poseStack.last();
            RenderTickHandler.renderVertexWireFrame(lines, buffer, pose.pose(), pose.normal(), isHighContrast);
            poseStack.popPose();
        }
    }

    @Override
    public boolean shouldRender(TileEntityLargePigmentMixer tile, Vec3 camera) {
        return tile.getActive() && super.shouldRender(tile, camera);
    }

    @Override
    public boolean hasSelectionBox(BlockState state) {
        return Attribute.isActive(state);
    }

    @Override
    public boolean isCombined() {
        return true;
    }

    private void setupRenderer(Direction direction, float rotation, PoseStack matrix) {
        matrix.pushPose();
        matrix.translate(0.5F, 1, 0.5F);
        matrix.mulPose(Axis.YN.rotationDegrees(rotation));
    }

    @Override
    public AABB getRenderBoundingBox(TileEntityLargePigmentMixer tile) {
        return new AABB(tile.getBlockPos().above(2));
    }

    @Override
    protected String getProfilerSection() {
        return MoreMachineProfilerConstants.LARGE_PIGMENT_MIXER;
    }

    public static class LargePigmentMixerRenderState extends BlockEntityRenderState {

        @Nullable
        public Direction direction;
        public float rotation;
    }
}
