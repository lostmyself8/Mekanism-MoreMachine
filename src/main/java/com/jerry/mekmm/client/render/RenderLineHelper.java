package com.jerry.mekmm.client.render;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderTypes;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class RenderLineHelper {

    public static void renderLine(PoseStack poseStack, MultiBufferSource buffers, Vector3f start, Vector3f end, float r, float g, float b, float thickness) {
        poseStack.pushPose();
        poseStack.translate(start.x(), start.y(), start.z());
        renderLine(poseStack.last(), buffers.getBuffer(RenderTypes.lightning()), new Vector3f(), new Vector3f(end).sub(start), r, g, b, thickness);
        poseStack.popPose();
    }

    public static void renderLine(Pose pose, VertexConsumer consumer, Vector3f start, Vector3f end, float r, float g, float b, float thickness) {
        Vector3f dir = new Vector3f(end).sub(start);
        float len = dir.length();
        if (len <= 1e-5) return;

        dir.normalize();
        Matrix4f matrix = pose.pose();
        Vector3f perpendicular1 = getPerpendicular(dir);
        Vector3f perpendicular2 = new Vector3f(dir).cross(perpendicular1).normalize();

        float radius = thickness / 2F;
        int light = 0xF000F0;
        int overlay = 0;
        int segments = 8;
        for (int i = 0; i < segments; i++) {
            double angle1 = Math.PI * 2 * i / segments;
            double angle2 = Math.PI * 2 * (i + 1) / segments;
            Vector3f offset1 = getCirclePoint(perpendicular1, perpendicular2, angle1, radius);
            Vector3f offset2 = getCirclePoint(perpendicular1, perpendicular2, angle2, radius);
            // 起点的两个顶点
            Vector3f v1Start = new Vector3f(start).add(offset1);
            Vector3f v2Start = new Vector3f(start).add(offset2);
            // 终点的两个顶点 (沿方向移动)
            Vector3f v1End = new Vector3f(start).add(offset1).add(new Vector3f(dir).mul(len));
            Vector3f v2End = new Vector3f(start).add(offset2).add(new Vector3f(dir).mul(len));
            // 绘制四边形面片
            addQuad(consumer, matrix, v1Start, v2Start, v2End, v1End, r, g, b, 0.8F, light, overlay);
        }
    }

    private static Vector3f getPerpendicular(Vector3f dir) {
        Vector3f perpendicular = Math.abs(dir.y()) < 0.99F ? new Vector3f(0, 1, 0) : new Vector3f(1, 0, 0);
        perpendicular.cross(dir).normalize();
        return perpendicular;
    }

    private static Vector3f getCirclePoint(Vector3f perpendicular1, Vector3f perpendicular2, double angle, float radius) {
        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);
        return new Vector3f(perpendicular1).mul(cos * radius).add(new Vector3f(perpendicular2).mul(sin * radius));
    }

    private static void addQuad(VertexConsumer consumer, Matrix4f matrix, Vector3f p1, Vector3f p2, Vector3f p3, Vector3f p4, float r, float g, float b, float a, int light, int overlay) {
        Vector3f edge1 = new Vector3f(p2).sub(p1);
        Vector3f edge2 = new Vector3f(p4).sub(p1);
        Vector3f normal = new Vector3f(edge1).cross(edge2).normalize();
        consumer.addVertex(matrix, p1.x(), p1.y(), p1.z()).setColor(r, g, b, a).setUv(0, 0).setLight(light).setOverlay(overlay).setNormal(normal.x(), normal.y(), normal.z());
        consumer.addVertex(matrix, p2.x(), p2.y(), p2.z()).setColor(r, g, b, a).setUv(1, 0).setLight(light).setOverlay(overlay).setNormal(normal.x(), normal.y(), normal.z());
        consumer.addVertex(matrix, p3.x(), p3.y(), p3.z()).setColor(r, g, b, a).setUv(1, 1).setLight(light).setOverlay(overlay).setNormal(normal.x(), normal.y(), normal.z());
        consumer.addVertex(matrix, p4.x(), p4.y(), p4.z()).setColor(r, g, b, a).setUv(0, 1).setLight(light).setOverlay(overlay).setNormal(normal.x(), normal.y(), normal.z());
    }
}
