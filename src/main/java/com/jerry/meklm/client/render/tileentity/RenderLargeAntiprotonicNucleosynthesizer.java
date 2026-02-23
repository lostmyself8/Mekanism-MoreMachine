package com.jerry.meklm.client.render.tileentity;

import com.jerry.mekmm.common.base.MoreMachineProfilerConstants;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.text.EnumColor;
import mekanism.client.model.ModelEnergyCore;
import mekanism.client.render.tileentity.MekanismTileEntityRenderer;
import mekanism.client.render.tileentity.RenderEnergyCube;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.phys.Vec3;

import com.jerry.meklm.common.tile.machine.TileEntityLargeAntiprotonicNucleosynthesizer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

@NothingNullByDefault
public class RenderLargeAntiprotonicNucleosynthesizer extends MekanismTileEntityRenderer<TileEntityLargeAntiprotonicNucleosynthesizer> {

    private final ModelEnergyCore core;

    public RenderLargeAntiprotonicNucleosynthesizer(Context context) {
        super(context);
        core = new ModelEnergyCore(context.getModelSet());
    }

    @Override
    protected void render(TileEntityLargeAntiprotonicNucleosynthesizer tile, float partialTick, PoseStack matrix, MultiBufferSource renderer, int light, int overlayLight, ProfilerFiller profiler) {
        float ticks = Minecraft.getInstance().levelRenderer.ticks + partialTick;
        float scaledTicks = 4 * ticks;
        matrix.pushPose();
        switch (tile.getDirection()) {
            case NORTH -> matrix.translate(2.5 / 16, 1.5, 2.5 / 16);
            case SOUTH -> matrix.translate(13.5 / 16, 1.5, 13.5 / 16);
            case WEST -> matrix.translate(2.5 / 16, 1.5, 13.5 / 16f);
            case EAST -> matrix.translate(13.5 / 16f, 1.5, 2.5 / 16);
        }
        // if (tile.getDirection() == Direction.NORTH) {
        // matrix.translate(2.5/16, 1.5, 2.5/16);
        // } else if (tile.getDirection() == Direction.SOUTH) {
        // matrix.translate(13.5/16, 1.5, 13.5/16);
        // } else if (tile.getDirection() == Direction.EAST) {
        // matrix.translate(13.5/16f, 1.5, 2.5/16);
        // } else if (tile.getDirection() == Direction.WEST) {
        // matrix.translate(2.5/16, 1.5, 13.5/16f);
        // }
        matrix.scale(0.5F, 0.5F, 0.5F);
        renderCore(matrix, renderer, overlayLight, scaledTicks);
        matrix.popPose();
        endIfNeeded(renderer, core.RENDER_TYPE);
    }

    @Override
    protected String getProfilerSection() {
        return MoreMachineProfilerConstants.LARGE_ANTIPROTONIC_NUCLEOSYNTHESIZER;
    }

    private void renderCore(PoseStack matrix, MultiBufferSource renderer, int overlayLight, float scaledTicks) {
        matrix.pushPose();
        matrix.mulPose(Axis.YP.rotationDegrees(scaledTicks));
        matrix.mulPose(RenderEnergyCube.coreVec.rotationDegrees(36F + scaledTicks));
        core.render(matrix, renderer.getBuffer(core.RENDER_TYPE), LightTexture.FULL_BRIGHT, overlayLight, EnumColor.PURPLE, 1);
        matrix.popPose();
    }

    @Override
    public boolean shouldRender(TileEntityLargeAntiprotonicNucleosynthesizer blockEntity, Vec3 cameraPos) {
        return blockEntity.getActive() && super.shouldRender(blockEntity, cameraPos);
    }
}
