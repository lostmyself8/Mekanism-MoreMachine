package com.jerry.mekmm.client.render.outline;

import com.jerry.mekmm.client.render.outline.ModelOutlineGeometry.Cuboid;
import com.jerry.mekmm.client.render.outline.ModelOutlineGeometry.Point;
import com.jerry.mekmm.client.render.outline.ModelOutlineGeometry.Rotation;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Reads the solid portions of vanilla element models and NeoForge composites.
 * Texture planes, inward decorative faces, and translucent children are excluded:
 * treating those as solid blocks would close glass chambers or cover sloped rims.
 */
public final class ModelOutlineLoader {

    private static final int MAX_CUBOIDS = 512;

    @FunctionalInterface
    public interface Source {

        Reader open(String modelId) throws IOException;
    }

    private ModelOutlineLoader() {}

    public static List<Cuboid> load(Source source, String modelId) throws IOException {
        List<Cuboid> cuboids = new ArrayList<>();
        readModel(source, normalize(modelId), new HashSet<>(), cuboids);
        return List.copyOf(cuboids);
    }

    private static void readModel(Source source, String modelId, Set<String> parents, List<Cuboid> cuboids) throws IOException {
        if (parents.size() >= 64 || !parents.add(modelId)) {
            throw new IOException("Cyclic or excessively deep outline model parent: " + modelId);
        }
        try (Reader reader = source.open(modelId)) {
            readNode(source, JsonParser.parseReader(reader).getAsJsonObject(), parents, cuboids);
        } finally {
            parents.remove(modelId);
        }
    }

    private static void readNode(Source source, JsonObject node, Set<String> parents, List<Cuboid> cuboids) throws IOException {
        if (node.has("visible") && !node.get("visible").getAsBoolean()) return;
        if (node.has("render_type") && normalize(node.get("render_type").getAsString()).equals("minecraft:translucent")) return;
        if (node.has("transform") || node.has("loader") && !node.get("loader").getAsString().equals("neoforge:composite")) {
            throw new IOException("Selection outlines support vanilla elements and untransformed composites");
        }
        if (node.has("elements")) {
            for (JsonElement entry : node.getAsJsonArray("elements")) {
                JsonObject element = entry.getAsJsonObject();
                if (!element.has("faces") || element.getAsJsonObject("faces").size() == 0) continue;
                Rotation rotation = null;
                if (element.has("rotation")) {
                    JsonObject value = element.getAsJsonObject("rotation");
                    rotation = new Rotation(point(value.get("origin")), value.get("axis").getAsString(),
                            value.get("angle").getAsDouble(), value.has("rescale") && value.get("rescale").getAsBoolean());
                }
                Cuboid cuboid = new Cuboid(point(element.get("from")), point(element.get("to")), rotation);
                if (cuboid.hasVolume()) cuboids.add(cuboid);
                if (cuboids.size() > MAX_CUBOIDS) throw new IOException("Too many solid elements in selection model");
            }
        } else if (node.has("parent")) {
            readModel(source, normalize(node.get("parent").getAsString()), parents, cuboids);
        }
        if (node.has("children")) {
            for (JsonElement child : node.getAsJsonObject("children").asMap().values()) {
                readNode(source, child.getAsJsonObject(), parents, cuboids);
            }
        }
    }

    private static Point point(JsonElement element) {
        var coordinates = element.getAsJsonArray();
        if (coordinates.size() != 3) throw new IllegalArgumentException("Model point must have three coordinates");
        double x = coordinates.get(0).getAsDouble();
        double y = coordinates.get(1).getAsDouble();
        double z = coordinates.get(2).getAsDouble();
        if (!Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(z)) {
            throw new IllegalArgumentException("Model coordinates must be finite");
        }
        return new Point(x, y, z);
    }

    private static String normalize(String modelId) {
        return modelId.contains(":") ? modelId : "minecraft:" + modelId;
    }
}
