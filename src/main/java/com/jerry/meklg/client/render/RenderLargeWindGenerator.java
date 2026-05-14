package com.jerry.meklg.client.render;

import com.jerry.mekmm.common.base.MoreMachineProfilerConstants;

import mekanism.api.annotations.NothingNullByDefault;
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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import com.jerry.meklg.client.model.ModelLargeWindGenerator;
import com.jerry.meklg.client.model.ModelLargeWindGenerator.LargeWindGeneratorRotationRenderState;
import com.jerry.meklg.client.render.RenderLargeWindGenerator.LargeWindGeneratorRenderState;
import com.jerry.meklg.common.tile.generator.TileEntityLargeWindGenerator;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import org.jetbrains.annotations.Nullable;

@NothingNullByDefault
public class RenderLargeWindGenerator extends MekanismTileEntityRenderer<TileEntityLargeWindGenerator, LargeWindGeneratorRenderState> implements IWireFrameRenderer {

    private final ModelLargeWindGenerator model;

    public RenderLargeWindGenerator(Context context) {
        super(context);
        model = new ModelLargeWindGenerator(context.entityModelSet());
    }

    @Override
    public void renderWireFrame(BlockEntity tile, float partialTick, PoseStack poseStack, VertexConsumer buffer) {
        if (tile instanceof TileEntityLargeWindGenerator generator) {
            poseStack.pushPose();
            poseStack.translate(0.5, 1.5, 0.5);
            MekanismRenderer.rotate(poseStack, generator.getDirection(), 0, 180, 90, 270);
            poseStack.mulPose(Axis.ZP.rotationDegrees(180));
            float angle = generator.getAngle();
            if (generator.getActive() && partialTick > 0) {
                angle = (angle + generator.getHeightSpeedRatio() * partialTick) % 360;
            }
            model.renderWireFrame(poseStack, buffer, angle);
            poseStack.popPose();
        }
    }

    @Override
    protected String getProfilerSection() {
        return MoreMachineProfilerConstants.LARGE_WIND_GENERATOR;
    }

    @Override
    public LargeWindGeneratorRenderState createRenderState() {
        return new LargeWindGeneratorRenderState();
    }

    @Override
    public void extractRenderState(TileEntityLargeWindGenerator tile, LargeWindGeneratorRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@org.jspecify.annotations.Nullable CrumblingOverlay breakProgress) {
        super.extractRenderState(tile, state, partialTicks, cameraPosition, breakProgress);
        state.direction = tile.getDirection();
        state.rotation.angle = tile.getAngle();
        if (tile.getActive() && partialTicks > 0) {
            state.rotation.angle = (state.rotation.angle + tile.getHeightSpeedRatio() * partialTicks) % 360;
        }
    }

    @Override
    public void submit(LargeWindGeneratorRenderState state, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        if (state.direction != null) {
            poseStack.pushPose();
            poseStack.translate(0.5, 1.5, 0.5);
            MekanismRenderer.rotate(poseStack, state.direction, 0, 180, 90, 270);
            poseStack.mulPose(Axis.ZP.rotationDegrees(180));
            this.model.collect(
                    state.rotation,
                    poseStack,
                    nodeCollector,
                    // TODO - 26.1: Do we need to do something for the light level similar to what double chests do of
                    // calculating the max of all the positions?
                    state.lightCoords,
                    OverlayTexture.NO_OVERLAY,
                    false);
            poseStack.popPose();
        }
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(TileEntityLargeWindGenerator tile) {
        // Mek采用不区分朝向的方法，但大风电叶片较大，因此还是需要区分一下朝向的
        BlockPos pos = tile.getBlockPos();
        Direction facing = tile.getDirection();
        // 如果是东西朝向就是Y轴
        if (facing == Direction.EAST || facing == Direction.WEST) {
            return AABB.encapsulatingFullBlocks(pos.offset(-5, 0, -16), pos.offset(5, 50, 16));
        }
        // 如果是南北朝向就是X轴，虽然这会包括上下，但是发电机没有上下的朝向
        return AABB.encapsulatingFullBlocks(pos.offset(-16, 0, -5), pos.offset(16, 50, 5));
    }

    public static class LargeWindGeneratorRenderState extends BlockEntityRenderState {

        public LargeWindGeneratorRotationRenderState rotation = new LargeWindGeneratorRotationRenderState(0);
        @Nullable
        public Direction direction;
    }
}
