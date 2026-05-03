package com.jerry.mekmm.client.render;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderTypes;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class RenderLineHelper {

    public static void renderLine(PoseStack poseStack, MultiBufferSource buffers, Vector3f start, Vector3f end, float r, float g, float b, float thickness) {
        // 计算向量和长度
        Vector3f dir = new Vector3f(end).sub(start);
        float len = dir.length();
        if (len <= 1e-5) return;

        // 归一化方向
        dir.normalize();
        // 在起点渲染
        poseStack.pushPose();
        poseStack.translate(start.x(), start.y(), start.z());

        Matrix4f matrix = poseStack.last().pose();
        VertexConsumer vc = buffers.getBuffer(RenderTypes.lightning());

        // 计算垂直于方向的两个正交向量 (用于构建圆柱体)
        Vector3f perpendicular1 = getPerpendicular(dir);
        Vector3f perpendicular2 = new Vector3f(dir).cross(perpendicular1).normalize();

        // 光束半径
        float radius = thickness / 2f;
        int light = 0xF000F0;
        int overlay = 0;

        // 用 4 个矩形近似圆柱（比 20 面快很多）
        int segments = 8;
        for (int i = 0; i < segments; i++) {
            double angle1 = (Math.PI * 2 * i) / segments;
            double angle2 = (Math.PI * 2 * (i + 1)) / segments;
            // 计算圆周上的两个点
            Vector3f offset1 = getCirclePoint(perpendicular1, perpendicular2, angle1, radius);
            Vector3f offset2 = getCirclePoint(perpendicular1, perpendicular2, angle2, radius);
            // 起点的两个顶点
            Vector3f v1Start = new Vector3f(offset1);
            Vector3f v2Start = new Vector3f(offset2);
            // 终点的两个顶点 (沿方向移动)
            Vector3f v1End = new Vector3f(offset1).add(new Vector3f(dir).mul(len));
            Vector3f v2End = new Vector3f(offset2).add(new Vector3f(dir).mul(len));
            // 绘制四边形面片
            addQuad(vc, matrix, v1Start, v2Start, v2End, v1End, r, g, b, 0.8f, light, overlay);
        }

        poseStack.popPose();
    }

    /**
     * 获取一个垂直于给定向量的向量
     */
    private static Vector3f getPerpendicular(Vector3f dir) {
        Vector3f perp = new Vector3f();
        // 如果方向主要不是沿 Y 轴,使用 Y 轴叉乘
        if (Math.abs(dir.y()) < 0.99f) {
            perp.set(0, 1, 0);
        } else {
            // 否则使用 X 轴
            perp.set(1, 0, 0);
        }
        // 叉乘得到垂直向量
        perp.cross(dir).normalize();
        return perp;
    }

    /**
     * 计算圆周上的点
     */
    private static Vector3f getCirclePoint(Vector3f perp1, Vector3f perp2, double angle, float radius) {
        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);
        return new Vector3f(perp1).mul(cos * radius).add(new Vector3f(perp2).mul(sin * radius));
    }

    /**
     * 绘制一个四边形面片
     */
    private static void addQuad(VertexConsumer vc, Matrix4f matrix, Vector3f p1, Vector3f p2, Vector3f p3, Vector3f p4, float r, float g, float b, float a, int light, int overlay) {
        // 计算法向量 (用于正确的光照,虽然我们使用完全发光)
        Vector3f edge1 = new Vector3f(p2).sub(p1);
        Vector3f edge2 = new Vector3f(p4).sub(p1);
        Vector3f normal = new Vector3f(edge1).cross(edge2).normalize();
        // 添加四个顶点
        vc.addVertex(matrix, p1.x(), p1.y(), p1.z()).setColor(r, g, b, a).setUv(0, 0).setLight(light).setOverlay(overlay).setNormal(normal.x(), normal.y(), normal.z());
        vc.addVertex(matrix, p2.x(), p2.y(), p2.z()).setColor(r, g, b, a).setUv(1, 0).setLight(light).setOverlay(overlay).setNormal(normal.x(), normal.y(), normal.z());
        vc.addVertex(matrix, p3.x(), p3.y(), p3.z()).setColor(r, g, b, a).setUv(1, 1).setLight(light).setOverlay(overlay).setNormal(normal.x(), normal.y(), normal.z());
        vc.addVertex(matrix, p4.x(), p4.y(), p4.z()).setColor(r, g, b, a).setUv(0, 1).setLight(light).setOverlay(overlay).setNormal(normal.x(), normal.y(), normal.z());
    }
}
