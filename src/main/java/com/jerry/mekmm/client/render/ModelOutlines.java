package com.jerry.mekmm.client.render;

import mekanism.client.render.outline.Outlines.Line;

import net.minecraft.client.model.geom.ModelPart;

import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Vector3f;

import java.util.*;

public final class ModelOutlines {

    private ModelOutlines() {}

    public static Collection<Line> extract(List<ModelPart> parts) {
        Set<Line> lines = new HashSet<>();
        PoseStack stack = new PoseStack();
        for (ModelPart part : parts) if (part.visible) part.visit(stack, (pose, path, index, cube) -> {
            for (ModelPart.Polygon polygon : cube.polygons) {
                var vertices = polygon.vertices();
                for (int edge = 0; edge < 4; edge++) {
                    var a = vertices[edge];
                    var b = vertices[(edge + 1) % 4];
                    Vector3f from = pose.pose().transformPosition(a.worldX(), a.worldY(), a.worldZ(), new Vector3f());
                    Vector3f to = pose.pose().transformPosition(b.worldX(), b.worldY(), b.worldZ(), new Vector3f());
                    lines.add(Line.from(from, to));
                }
            }
        });
        return lines;
    }
}
