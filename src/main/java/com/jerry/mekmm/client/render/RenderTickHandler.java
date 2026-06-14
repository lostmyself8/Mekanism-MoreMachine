package com.jerry.mekmm.client.render;

import com.jerry.mekmm.client.renderer.MoreMachineRenderType;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Matrix4f;

public class RenderTickHandler {

    @SubscribeEvent
    public void renderWorld(RenderLevelStageEvent.AfterTranslucentBlocks event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        Vec3 cameraPos = mc.gameRenderer.getMainCamera().position();
        long gameTime = mc.level.getGameTime();

        poseStack.pushPose();
        for (BlockPos pos : BlockHighlightManager.getInstance().getHighlightedPositions()) {
            BlockHighlightManager.HighlightData data = BlockHighlightManager.getInstance().getHighlightData(pos);
            if (data != null) {
                renderHighlight(poseStack, bufferSource, pos, cameraPos, data, gameTime);
            }
        }
        poseStack.popPose();
        bufferSource.endBatch(MoreMachineRenderType.LINES_NO_DEPTH);
    }

    private static void renderHighlight(PoseStack poseStack, MultiBufferSource bufferSource, BlockPos pos, Vec3 cameraPos, BlockHighlightManager.HighlightData data, long gameTime) {
        poseStack.pushPose();
        poseStack.translate(pos.getX() - cameraPos.x, pos.getY() - cameraPos.y, pos.getZ() - cameraPos.z);
        VertexConsumer consumer = bufferSource.getBuffer(MoreMachineRenderType.LINES_NO_DEPTH);
        float alpha = data.getAlpha(gameTime);
        renderLineBox(poseStack, consumer, data.getRed(), data.getGreen(), data.getBlue(), alpha);
        poseStack.popPose();
    }

    private static void renderLineBox(PoseStack poseStack, VertexConsumer consumer, float red, float green, float blue, float alpha) {
        line(poseStack, consumer, 0, 0, 0, 1, 0, 0, red, green, blue, alpha);
        line(poseStack, consumer, 1, 0, 0, 1, 0, 1, red, green, blue, alpha);
        line(poseStack, consumer, 1, 0, 1, 0, 0, 1, red, green, blue, alpha);
        line(poseStack, consumer, 0, 0, 1, 0, 0, 0, red, green, blue, alpha);
        line(poseStack, consumer, 0, 1, 0, 1, 1, 0, red, green, blue, alpha);
        line(poseStack, consumer, 1, 1, 0, 1, 1, 1, red, green, blue, alpha);
        line(poseStack, consumer, 1, 1, 1, 0, 1, 1, red, green, blue, alpha);
        line(poseStack, consumer, 0, 1, 1, 0, 1, 0, red, green, blue, alpha);
        line(poseStack, consumer, 0, 0, 0, 0, 1, 0, red, green, blue, alpha);
        line(poseStack, consumer, 1, 0, 0, 1, 1, 0, red, green, blue, alpha);
        line(poseStack, consumer, 1, 0, 1, 1, 1, 1, red, green, blue, alpha);
        line(poseStack, consumer, 0, 0, 1, 0, 1, 1, red, green, blue, alpha);
    }

    private static void line(PoseStack poseStack, VertexConsumer consumer, float x1, float y1, float z1, float x2, float y2, float z2, float red, float green, float blue, float alpha) {
        Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();
        float normalX = x2 - x1;
        float normalY = y2 - y1;
        float normalZ = z2 - z1;
        float length = (float) Math.sqrt(normalX * normalX + normalY * normalY + normalZ * normalZ);
        if (length != 0) {
            normalX /= length;
            normalY /= length;
            normalZ /= length;
        }
        consumer.addVertex(matrix, x1, y1, z1).setColor(red, green, blue, alpha).setNormal(pose, normalX, normalY, normalZ).setLineWidth(2F);
        consumer.addVertex(matrix, x2, y2, z2).setColor(red, green, blue, alpha).setNormal(pose, normalX, normalY, normalZ).setLineWidth(2F);
    }
}
