package com.jerry.mekmm.client.render.outline;

import com.jerry.mekmm.client.render.outline.ModelOutlineGeometry.Cuboid;
import com.jerry.mekmm.client.render.outline.ModelOutlineGeometry.Line;
import com.jerry.mekmm.client.render.outline.ModelOutlineGeometry.Point;
import com.jerry.mekmm.client.render.outline.ModelOutlineGeometry.Rotation;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/** Executed by verifySelectionOutlines; no game instance or test framework needed. */
public final class ModelOutlineGeometryTest {

    private static final double TOLERANCE = 1.0e-5;

    private ModelOutlineGeometryTest() {}

    public static void main(String[] args) throws IOException {
        Cuboid cube = box(0, 0, 0, 1, 1, 1);
        check(ModelOutlineGeometry.build(List.of(cube)).size() == 12, "Single cube needs twelve edges");
        check(ModelOutlineGeometry.build(List.of(cube, cube)).size() == 12, "Duplicate cube must not duplicate edges");
        check(ModelOutlineGeometry.build(List.of(cube, box(1, 0, 0, 2, 1, 1))).size() == 12, "Touching cubes must lose the shared seam");
        check(ModelOutlineGeometry.build(List.of(box(0, 0, 0, 3, 3, 3), cube)).size() == 12, "Buried cube must not add edges");
        check(ModelOutlineGeometry.build(List.of(box(0, 0, 0, 2, 2, 2), box(1, 1, 1, 3, 3, 3))).size() == 30,
                "Overlapping cubes must preserve the reentrant intersection edges");
        check(ModelOutlineGeometry.build(List.of(cube, box(0, 0, 0, 0, 1, 1))).size() == 12, "Texture planes must not add boxes");
        check(ModelOutlineGeometry.build(List.of(cube, box(1, 1, 1, 0, 0, 0))).size() == 12, "Inward decorative elements are not solids");

        // Three blocks tile a rectangle, with a T-junction on its front and back.
        check(ModelOutlineGeometry.build(List.of(box(0, 0, 0, 1, 2, 1),
                box(1, 0, 0, 2, 1, 1), box(1, 1, 0, 2, 2, 1))).size() == 12, "T-junction must not leave a seam");

        // A rotated square bridges two perpendicular panels. Only its exposed
        // diagonal belongs in the outline; the other diamond edges are buried.
        double rootTwo = Math.sqrt(2);
        List<Line> corner = ModelOutlineGeometry.build(List.of(
                box(-2, -2, 0, 0, 2, 1), box(0, -2, 0, 2, 0, 1),
                new Cuboid(new Point(-1, -1, 0), new Point(1, 1, 1), new Rotation(new Point(0, 0, 0), "z", 45, false))));
        check(corner.size() == 21, "Bevelled L-shaped prism must have 21 edges, without a box around the bevel");
        check(contains(corner, new Point(0, rootTwo, 0), new Point(rootTwo, 0, 0)), "Front bevel diagonal missing");
        check(contains(corner, new Point(0, rootTwo, 1), new Point(rootTwo, 0, 1)), "Back bevel diagonal missing");

        Rotation keyboard = new Rotation(new Point(18, 0, -13), "x", -22.5, false);
        Point front = keyboard.apply(new Point(18, 0, -16));
        check(Math.abs(front.y() + 3 * Math.sin(Math.PI / 8)) < TOLERANCE, "Keyboard front must slope downward");
        check(ModelOutlineGeometry.build(List.of(new Cuboid(new Point(18, -1, -16), new Point(28, 0, -11), keyboard))).size() == 12,
                "Sloped keyboard must keep its twelve cuboid edges");
        for (String axis : List.of("x", "y", "z")) {
            Rotation rotation = new Rotation(new Point(0, 0, 0), axis, 45, true);
            check(ModelOutlineGeometry.build(List.of(new Cuboid(cube.from(), cube.to(), rotation))).size() == 12,
                    "Rescaled rotation must preserve cuboid topology");
        }
        // The chamber corners have a real 0.01-pixel inset. Only the explicit
        // per-model join policy may weld this; larger steps must remain intact.
        Cuboid insetCorner = new Cuboid(new Point(-1, -1, 0.01), new Point(1, 1, 0.99),
                new Rotation(new Point(0, 0, 0), "z", 45, false));
        List<Cuboid> insetJoin = List.of(box(-2, -2, 0, 0, 2, 1), box(0, -2, 0, 2, 0, 1), insetCorner);
        check(ModelOutlineGeometry.build(ModelOutlineGeometry.joinInsets(insetJoin, 0.011)).size() == 21,
                "Recessed chamber corner must blend into the adjacent panels");
        check(ModelOutlineGeometry.joinInsets(insetJoin, 0).equals(insetJoin), "Other model profiles must not change geometry");
        check(ModelOutlineGeometry.joinInsets(insetJoin, 0.001).equals(insetJoin), "Do not weld steps outside the declared tolerance");
        verifyLoader();
        verifyModels(Path.of(args[0]));
        verifyWindBase();
        System.out.println("Selection outline geometry checks passed");
    }

    private static void verifyWindBase() {
        var base = com.jerry.meklg.client.model.ModelLargeWindGenerator.createLayerDefinition().bakeRoot().getChild("base");
        List<Line> lines = ModelPartOutline.buildGeometry(base);
        check(!lines.isEmpty() && lines.size() < 5_000, "Wind base outline must be bounded and nonempty");
        double lowest = lines.stream().flatMap(line -> java.util.stream.Stream.of(line.start(), line.end()))
                .mapToDouble(Point::y).max().orElseThrow();
        check(Math.abs(1.5 - lowest / 16) < TOLERANCE, "Wind Java-model pose must place the base at ground level");
        // Controller keyboard, expressed through its real nested part pose.
        // This catches losing BASE's +24 Y or applying part rotation twice.
        double angle = -0.3927F;
        Point keyboardFront = new Point(-7, 12 + (-3.2F) * Math.cos(angle) - Math.sin(angle),
                51 + (-3.2F) * Math.sin(angle) + Math.cos(angle));
        check(lines.stream().anyMatch(line -> near(line.start(), keyboardFront) || near(line.end(), keyboardFront)),
                "Wind keyboard front corner must use its rotated model pose");
        System.out.printf("large_wind_generator/base: %d lines, nested pose and keyboard checks passed%n", lines.size());
    }

    private static void verifyLoader() throws IOException {
        String element = """
                {"from":[0,0,0],"to":[1,1,1],"faces":{"up":{"texture":"#0"}}}
                """;
        Map<String, String> resources = Map.of(
                "test:base", "{\"elements\":[" + element + "]}",
                "test:child", "{\"parent\":\"test:base\"}",
                "test:cycle", "{\"parent\":\"test:cycle\"}",
                "test:unsupported", "{\"loader\":\"test:custom\"}",
                "test:composite", """
                        {"loader":"neoforge:composite","children":{
                        "solid":{"parent":"test:child"},
                        "glass":{"parent":"test:base","render_type":"translucent"},
                        "hidden":{"parent":"test:base","visible":false}}}
                        """);
        ModelOutlineLoader.Source source = id -> new StringReader(resources.get(id));
        check(ModelOutlineLoader.load(source, "test:composite").size() == 1, "Composite must inherit solids and exclude glass/hidden children");
        for (String id : List.of("test:cycle", "test:unsupported")) {
            try {
                ModelOutlineLoader.load(source, id);
                throw new AssertionError("Unsupported model did not report failure: " + id);
            } catch (IOException expected) {
                // The client renderer catches this and falls back to the voxel outline.
            }
        }
    }

    private static void verifyModels(Path project) throws IOException {
        Path assets = project.resolve("src/main/resources/assets");
        ModelOutlineLoader.Source source = id -> {
            String[] parts = id.split(":", 2);
            return Files.newBufferedReader(assets.resolve(parts[0]).resolve("models").resolve(parts[1] + ".json"));
        };
        for (String machine : ModelOutlineProfiles.MACHINES.keySet()) {
            var profile = ModelOutlineProfiles.MACHINES.get(machine);
            for (String state : List.of("off", "on")) {
                long started = System.nanoTime();
                List<Cuboid> cuboids = ModelOutlineLoader.load(source, "mekmm:block/large_machine/" + machine + "/" + state);
                cuboids = ModelOutlineGeometry.joinInsets(cuboids, profile.insetJoinPixels());
                List<Line> lines = ModelOutlineGeometry.build(cuboids);
                double floor = lines.stream().flatMap(line -> java.util.stream.Stream.of(line.start(), line.end()))
                        .mapToDouble(Point::y).min().orElseThrow() / 16 + profile.yOffsetBlocks();
                check(Math.abs(floor) < TOLERANCE, "Model outline must meet the main-block floor: " + machine);
                double buildMillis = (System.nanoTime() - started) / 1_000_000.0;
                check(!lines.isEmpty() && lines.size() < 5_000, "Invalid outline complexity for " + machine);
                for (Line line : lines) {
                    check(Double.isFinite(line.start().lengthSquared()) && Double.isFinite(line.end().lengthSquared()), "Non-finite edge");
                    check(line.end().subtract(line.start()).lengthSquared() > 1.0e-12, "Zero-length edge");
                    for (double t : new double[] { 0.25, 0.5, 0.75 }) {
                        Point sample = line.start().add(line.end().subtract(line.start()).scale(t));
                        for (Cuboid cuboid : cuboids) {
                            check(!strictlyInside(sample, cuboid), "Buried outline edge in " + machine + "/" + state + ": " + line);
                        }
                    }
                }
                System.out.printf("%s/%s: %d solids, %d lines, %.1f ms to load/build%n",
                        machine, state, cuboids.size(), lines.size(), buildMillis);
            }
        }
    }

    private static boolean strictlyInside(Point point, Cuboid cuboid) {
        Rotation rotation = cuboid.rotation();
        if (rotation != null) {
            if (rotation.rescale()) {
                Point p = point.subtract(rotation.origin());
                double scale = Math.abs(Math.cos(Math.toRadians(rotation.degrees())));
                point = new Point(p.x() * (rotation.axis().equals("x") ? 1 : scale),
                        p.y() * (rotation.axis().equals("y") ? 1 : scale), p.z() * (rotation.axis().equals("z") ? 1 : scale)).add(rotation.origin());
            }
            point = new Rotation(rotation.origin(), rotation.axis(), -rotation.degrees(), false).apply(point);
        }
        return point.x() > cuboid.from().x() + TOLERANCE && point.x() < cuboid.to().x() - TOLERANCE && point.y() > cuboid.from().y() + TOLERANCE && point.y() < cuboid.to().y() - TOLERANCE && point.z() > cuboid.from().z() + TOLERANCE && point.z() < cuboid.to().z() - TOLERANCE;
    }

    private static Cuboid box(double x1, double y1, double z1, double x2, double y2, double z2) {
        return new Cuboid(new Point(x1, y1, z1), new Point(x2, y2, z2), null);
    }

    private static boolean contains(List<Line> lines, Point a, Point b) {
        return lines.stream().anyMatch(line -> near(line.start(), a) && near(line.end(), b) || near(line.start(), b) && near(line.end(), a));
    }

    private static boolean near(Point a, Point b) {
        return a.subtract(b).lengthSquared() < TOLERANCE * TOLERANCE;
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
