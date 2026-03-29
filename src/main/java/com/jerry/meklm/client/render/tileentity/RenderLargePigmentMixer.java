package com.jerry.meklm.client.render.tileentity;

import com.jerry.mekmm.common.base.MoreMachineProfilerConstants;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.client.render.RenderTickHandler;
import mekanism.client.render.lib.Vertex;
import mekanism.client.render.tileentity.IWireFrameRenderer;
import mekanism.client.render.tileentity.MekanismTileEntityRenderer;
import mekanism.common.block.attribute.Attribute;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import com.jerry.meklm.client.model.LargeMachineModelCache;
import com.jerry.meklm.common.tile.machine.TileEntityLargePigmentMixer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@NothingNullByDefault
public class RenderLargePigmentMixer extends MekanismTileEntityRenderer<TileEntityLargePigmentMixer> implements IWireFrameRenderer {

    private static final List<Vertex[]> vertices = new ArrayList<>();
    private static final float SHAFT_SPEED = 5F;

    public static void resetCached() {
        vertices.clear();
    }

    public RenderLargePigmentMixer(Context context) {
        super(context);
    }

    @Override
    public void renderWireFrame(BlockEntity tile, float partialTick, PoseStack matrix, VertexConsumer buffer, int red, int green, int blue, int alpha) {
        if (tile instanceof TileEntityLargePigmentMixer mixer) {
            if (vertices.isEmpty()) {
                LargeMachineModelCache.INSTANCE.LARGE_PIGMENT_MIXER_ROD.collectQuadVertices(vertices, tile.getLevel().random);
            }
            renderTranslated(mixer, partialTick, matrix, poseStack -> RenderTickHandler.renderVertexWireFrame(vertices, buffer, poseStack.last().pose(),
                    red, green, blue, alpha));
        }
    }

    @Override
    protected void render(TileEntityLargePigmentMixer tile, float partialTick, PoseStack matrix, MultiBufferSource renderer, int light, int overlayLight, ProfilerFiller profiler) {
        renderTranslated(tile, partialTick, matrix, poseStack -> {
            PoseStack.Pose entry = poseStack.last();
            VertexConsumer buffer = renderer.getBuffer(Sheets.solidBlockSheet());
            for (BakedQuad quad : LargeMachineModelCache.INSTANCE.LARGE_PIGMENT_MIXER_ROD.getQuads(tile.getLevel().random)) {
                buffer.putBulkData(entry, quad, 1, 1, 1, light, overlayLight);
            }
        });
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

    @Override
    protected String getProfilerSection() {
        return MoreMachineProfilerConstants.LARGE_PIGMENT_MIXER;
    }

    private void renderTranslated(TileEntityLargePigmentMixer tile, float partialTick, PoseStack matrix, Consumer<PoseStack> renderer) {
        matrix.pushPose();
        matrix.translate(0.5F, 1, 0.5F);
        if (tile.getLevel() != null) {
            matrix.mulPose(Axis.YN.rotationDegrees((tile.getLevel().getGameTime() + partialTick) * SHAFT_SPEED % 360));
        }
        renderer.accept(matrix);
        matrix.popPose();
    }
}
