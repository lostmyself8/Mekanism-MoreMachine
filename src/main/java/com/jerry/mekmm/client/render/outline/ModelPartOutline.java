package com.jerry.mekmm.client.render.outline;

import com.jerry.mekmm.client.render.outline.ModelOutlineGeometry.Line;
import com.jerry.mekmm.client.render.outline.ModelOutlineGeometry.Point;

import net.minecraft.client.model.geom.ModelPart;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

/**
 * Outline adapter for a static Java-model subtree with undeformed cubes.
 * Visit uses the model's existing nested part poses. Capture in local space once,
 * then draw under the same outer pose as the textured model. Never capture a
 * camera/world transform or an animated subtree into this cache.
 */
public final class ModelPartOutline {

    private final List<Segment> segments;

    private ModelPartOutline(List<Line> lines) {
        segments = lines.stream().map(line -> new Segment(line.start().scale(1.0 / 16),
                line.end().scale(1.0 / 16), line.end().subtract(line.start()).normalize())).toList();
    }

    public static ModelPartOutline capture(ModelPart part) {
        return new ModelPartOutline(buildGeometry(part));
    }

    /** Local pixel-space geometry, also used by the standalone regression runner. */
    public static List<Line> buildGeometry(ModelPart part) {
        List<List<Point>> boxes = new ArrayList<>();
        part.visit(new PoseStack(), (pose, path, index, cube) -> {
            if (cube.maxX <= cube.minX || cube.maxY <= cube.minY || cube.maxZ <= cube.minZ) return;
            List<Point> corners = new ArrayList<>(8);
            Matrix4f transform = pose.pose();
            for (int i = 0; i < 8; i++) {
                double x = (i & 1) == 0 ? cube.minX : cube.maxX;
                double y = (i & 2) == 0 ? cube.minY : cube.maxY;
                double z = (i & 4) == 0 ? cube.minZ : cube.maxZ;
                // Pose translations are blocks; geometry coordinates are pixels.
                // Double arithmetic preserves shared planes during clipping.
                corners.add(new Point(transform.m00() * x + transform.m10() * y + transform.m20() * z + transform.m30() * 16,
                        transform.m01() * x + transform.m11() * y + transform.m21() * z + transform.m31() * 16,
                        transform.m02() * x + transform.m12() * y + transform.m22() * z + transform.m32() * 16));
            }
            boxes.add(corners);
        });
        return ModelOutlineGeometry.buildTransformedBoxes(boxes);
    }

    public void render(PoseStack pose, VertexConsumer buffer) {
        for (Segment segment : segments) {
            vertex(pose.last(), buffer, segment.start, segment.direction);
            vertex(pose.last(), buffer, segment.end, segment.direction);
        }
    }

    private static void vertex(PoseStack.Pose pose, VertexConsumer buffer, Point point, Point direction) {
        buffer.addVertex(pose, (float) point.x(), (float) point.y(), (float) point.z())
                .setColor(0, 0, 0, 102).setNormal(pose, (float) direction.x(), (float) direction.y(), (float) direction.z());
    }

    private record Segment(Point start, Point end, Point direction) {}
}
