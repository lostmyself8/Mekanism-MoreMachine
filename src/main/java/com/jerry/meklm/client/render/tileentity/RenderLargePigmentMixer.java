package com.jerry.meklm.client.render.tileentity;

import com.jerry.meklm.client.model.LargeMachineModelCache;
import com.jerry.meklm.common.tile.machine.TileEntityLargePigmentMixer;

import com.jerry.mekmm.common.base.MoreMachineProfilerConstants;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.client.render.RenderTickHandler;
import mekanism.client.render.lib.Outlines;
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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.data.ModelData;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@NothingNullByDefault
public class RenderLargePigmentMixer extends MekanismTileEntityRenderer<TileEntityLargePigmentMixer> implements IWireFrameRenderer {

    private static final float SHAFT_SPEED = 5F;
    @Nullable
    private static List<Outlines.Line> lines;

    public static void resetCached() {
        lines = null;
    }

    public RenderLargePigmentMixer(Context context) {
        super(context);
    }

    @Override
    public void renderWireFrame(BlockEntity tile, float partialTick, PoseStack matrix, VertexConsumer buffer) {
        if (tile instanceof TileEntityLargePigmentMixer mixer) {
            if (lines == null && tile.getLevel() != null) {
                lines = Outlines.extract(LargeMachineModelCache.INSTANCE.LARGE_PIGMENT_MIXER_ROD.getBakedModel(), null, tile.getLevel().random, ModelData.EMPTY, null);
            }
            setupRenderer(mixer, partialTick, matrix);
            PoseStack.Pose pose = matrix.last();
            RenderTickHandler.renderVertexWireFrame(lines, buffer, pose.pose(), pose.normal());
            matrix.popPose();
        }
    }

    @Override
    protected void render(TileEntityLargePigmentMixer tile, float partialTick, PoseStack matrix, MultiBufferSource renderer, int light, int overlayLight, ProfilerFiller profiler) {
        setupRenderer(tile, partialTick, matrix);
        PoseStack.Pose entry = matrix.last();
        VertexConsumer buffer = renderer.getBuffer(Sheets.solidBlockSheet());
        if (tile.getLevel() != null) {
            for (BakedQuad quad : LargeMachineModelCache.INSTANCE.LARGE_PIGMENT_MIXER_ROD.getQuads(tile.getLevel().random)) {
                buffer.putBulkData(entry, quad, 1, 1, 1, 1, light, overlayLight);
            }
        }
        matrix.popPose();
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

    private void setupRenderer(TileEntityLargePigmentMixer tile, float partialTick, PoseStack matrix) {
        matrix.pushPose();
        matrix.translate(0.5F, 1, 0.5F);
        if (tile.getLevel() != null) {
            matrix.mulPose(Axis.YN.rotationDegrees((tile.getLevel().getGameTime() + partialTick) * SHAFT_SPEED % 360));
        }
    }

    @Override
    public AABB getRenderBoundingBox(TileEntityLargePigmentMixer tile) {
        return new AABB(tile.getBlockPos().above(2));
    }

    @Override
    protected String getProfilerSection() {
        return MoreMachineProfilerConstants.LARGE_PIGMENT_MIXER;
    }
}
