package com.jerry.mekmm.client.render;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ExtractLevelRenderStateEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent.AfterTranslucentBlocks;

import com.mojang.blaze3d.vertex.*;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class RenderTickHandler {

    // 每帧提取到的数据，跨两个阶段传递
    private record HighlightEntry(BlockPos pos, float r, float g, float b, float alpha) {}

    private static final List<HighlightEntry> pendingHighlights = new ArrayList<>();
    private static Vec3 cameraPos = Vec3.ZERO;

    // ByteBufferBuilder 复用，不要每帧 new
    private static final ByteBufferBuilder ALLOCATOR = new ByteBufferBuilder(RenderType.SMALL_BUFFER_SIZE);
    private static BufferBuilder bufferBuilder = null;
    private static MeshData builtMesh = null;

    // ① 提取阶段：从 level 读取需要的数据（必须在此阶段读，不能在渲染阶段读 level）
    @SubscribeEvent
    public void onExtract(ExtractLevelRenderStateEvent event) {
        pendingHighlights.clear();
        cameraPos = event.getCamera().position();
        long gameTime = event.getLevel().getGameTime();

        for (BlockPos pos : BlockHighlightManager.getInstance().getHighlightedPositions()) {
            BlockHighlightManager.HighlightData data = BlockHighlightManager.getInstance().getHighlightData(pos);
            if (data != null) {
                pendingHighlights.add(new HighlightEntry(
                        pos,
                        data.getRed(), data.getGreen(), data.getBlue(),
                        data.getAlpha(gameTime)));
            }
        }

        if (pendingHighlights.isEmpty()) {
            builtMesh = null;
            return;
        }

        // 在提取阶段写顶点
        bufferBuilder = new BufferBuilder(ALLOCATOR, VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL_LINE_WIDTH);

        PoseStack poseStack = new PoseStack();
        for (HighlightEntry entry : pendingHighlights) {
            poseStack.pushPose();
            poseStack.translate(
                    entry.pos().getX() - cameraPos.x,
                    entry.pos().getY() - cameraPos.y,
                    entry.pos().getZ() - cameraPos.z);
            addLineBox(poseStack.last().pose(), bufferBuilder,
                    entry.r(), entry.g(), entry.b(), entry.alpha());
            poseStack.popPose();
        }

        builtMesh = bufferBuilder.buildOrThrow();
    }

    // ② 绘制阶段：提交 mesh 给 GPU
    @SubscribeEvent
    public void onRender(AfterTranslucentBlocks event) {
        if (builtMesh == null) return;

        // 用 RenderSystem 提交绘制（26.1 的新方式）
        // RenderSystem.submitAndDraw(MoreMachineRenderType.LINES_NO_DEPTH, builtMesh);
        // MoreMachineRenderType.LINES_NO_DEPTH.draw(pendingMesh);
        builtMesh = null;
    }

    // 给一个完整的 1x1x1 box 添加 12 条线段的顶点
    private static void addLineBox(Matrix4f pose, BufferBuilder buf,
                                   float r, float g, float b, float a) {
        // 仿照 LevelRenderer.renderLineBox 手动添加线框
        float x0 = 0, y0 = 0, z0 = 0;
        float x1 = 1, y1 = 1, z1 = 1;

        // 底面 4 条
        addLine(pose, buf, x0, y0, z0, x1, y0, z0, r, g, b, a);
        addLine(pose, buf, x1, y0, z0, x1, y0, z1, r, g, b, a);
        addLine(pose, buf, x1, y0, z1, x0, y0, z1, r, g, b, a);
        addLine(pose, buf, x0, y0, z1, x0, y0, z0, r, g, b, a);
        // 顶面 4 条
        addLine(pose, buf, x0, y1, z0, x1, y1, z0, r, g, b, a);
        addLine(pose, buf, x1, y1, z0, x1, y1, z1, r, g, b, a);
        addLine(pose, buf, x1, y1, z1, x0, y1, z1, r, g, b, a);
        addLine(pose, buf, x0, y1, z1, x0, y1, z0, r, g, b, a);
        // 竖向 4 条
        addLine(pose, buf, x0, y0, z0, x0, y1, z0, r, g, b, a);
        addLine(pose, buf, x1, y0, z0, x1, y1, z0, r, g, b, a);
        addLine(pose, buf, x1, y0, z1, x1, y1, z1, r, g, b, a);
        addLine(pose, buf, x0, y0, z1, x0, y1, z1, r, g, b, a);
    }

    private static void addLine(Matrix4f pose, BufferBuilder buf,
                                float x0, float y0, float z0, float x1, float y1, float z1,
                                float r, float g, float b, float a) {
        buf.addVertex(pose, x0, y0, z0).setColor(r, g, b, a);
        buf.addVertex(pose, x1, y1, z1).setColor(r, g, b, a);
    }
}
