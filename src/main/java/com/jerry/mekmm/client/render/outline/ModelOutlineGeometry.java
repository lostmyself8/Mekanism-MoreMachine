package com.jerry.mekmm.client.render.outline;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * Extracts the crease edges of a union of rotated cuboids. All coordinates are in
 * model pixels (16 per block). This class has no Minecraft dependency so the
 * clipping rules can be tested without starting a game.
 *
 * <p>
 * Drawing each cuboid separately exposes buried corners. Instead, clip every
 * face against the other solids, then retain edges shared by non-coplanar faces.
 * Splitting collinear edges at every endpoint also removes seams at T-junctions.
 */
public final class ModelOutlineGeometry {

    private static final double EPSILON = 1.0e-7;
    private static final double KEY_SCALE = 1.0e6;
    private static final int MAX_FRAGMENTS = 20_000;
    private static final int[][] FACE_CORNERS = {
            { 0, 4, 6, 2 }, { 1, 3, 7, 5 }, { 0, 1, 5, 4 },
            { 2, 6, 7, 3 }, { 0, 2, 3, 1 }, { 4, 5, 7, 6 }
    };

    private ModelOutlineGeometry() {}

    public record Point(double x, double y, double z) {

        public Point add(Point p) {
            return new Point(x + p.x, y + p.y, z + p.z);
        }

        public Point subtract(Point p) {
            return new Point(x - p.x, y - p.y, z - p.z);
        }

        public Point scale(double factor) {
            return new Point(x * factor, y * factor, z * factor);
        }

        double dot(Point p) {
            return x * p.x + y * p.y + z * p.z;
        }

        Point cross(Point p) {
            return new Point(y * p.z - z * p.y, z * p.x - x * p.z, x * p.y - y * p.x);
        }

        double lengthSquared() {
            return dot(this);
        }

        Point normalize() {
            return scale(1 / Math.sqrt(lengthSquared()));
        }
    }

    public record Line(Point start, Point end) {}

    /** Matches a JSON element rotation, including Minecraft's optional rescale. */
    public record Rotation(Point origin, String axis, double degrees, boolean rescale) {

        public Rotation {
            if (!Double.isFinite(degrees) || Math.abs(degrees) > 45 || !(axis.equals("x") || axis.equals("y") || axis.equals("z"))) {
                throw new IllegalArgumentException("Unsupported model element rotation");
            }
        }

        Point apply(Point vertex) {
            Point p = vertex.subtract(origin);
            double angle = Math.toRadians(degrees);
            double sine = Math.sin(angle);
            double cosine = Math.cos(angle);
            Point rotated = switch (axis) {
                case "x" -> new Point(p.x, p.y * cosine - p.z * sine, p.y * sine + p.z * cosine);
                case "y" -> new Point(p.x * cosine + p.z * sine, p.y, -p.x * sine + p.z * cosine);
                case "z" -> new Point(p.x * cosine - p.y * sine, p.x * sine + p.y * cosine, p.z);
                default -> throw new IllegalArgumentException("Unknown model rotation axis: " + axis);
            };
            if (rescale) {
                double scale = 1 / Math.abs(cosine);
                rotated = new Point(rotated.x * (axis.equals("x") ? 1 : scale),
                        rotated.y * (axis.equals("y") ? 1 : scale), rotated.z * (axis.equals("z") ? 1 : scale));
            }
            return rotated.add(origin);
        }
    }

    public record Cuboid(Point from, Point to, Rotation rotation) {

        /** Inverted elements and texture-only planes do not define solid volume. */
        public boolean hasVolume() {
            return to.x - from.x > EPSILON && to.y - from.y > EPSILON && to.z - from.z > EPSILON;
        }
    }

    /**
     * Weld authored shallow insets only along a rotated element's unchanged axis.
     * The nucleosynthesizer corners are 0.01 pixels thinner than their frame to
     * avoid coplanar texture overlap. Exact solid union preserves that unwanted
     * step. Extend those end planes to nearby, overlapping unrotated neighbours
     * for the outline only. Zero disables this policy for all other JSON models.
     */
    public static List<Cuboid> joinInsets(List<Cuboid> cuboids, double tolerance) {
        if (tolerance <= 0) return cuboids;
        return cuboids.stream().map(cuboid -> {
            if (cuboid.rotation == null || cuboid.rotation.degrees == 0) return cuboid;
            String axis = cuboid.rotation.axis;
            double low = coordinate(cuboid.from, axis);
            double high = coordinate(cuboid.to, axis);
            Solid bounds = Solid.of(cuboid);
            for (Cuboid neighbour : cuboids) {
                if (neighbour.rotation != null && neighbour.rotation.degrees != 0) continue;
                if (!bounds.overlaps(Solid.of(neighbour))) continue;
                double candidateLow = coordinate(neighbour.from, axis);
                double candidateHigh = coordinate(neighbour.to, axis);
                // Compare with the ORIGINAL planes: never chain tiny adjustments.
                if (candidateLow < low && coordinate(cuboid.from, axis) - candidateLow <= tolerance) low = candidateLow;
                if (candidateHigh > high && candidateHigh - coordinate(cuboid.to, axis) <= tolerance) high = candidateHigh;
            }
            return new Cuboid(withCoordinate(cuboid.from, axis, low), withCoordinate(cuboid.to, axis, high), cuboid.rotation);
        }).toList();
    }

    private static double coordinate(Point point, String axis) {
        return switch (axis) {
            case "x" -> point.x;
            case "y" -> point.y;
            default -> point.z;
        };
    }

    private static Point withCoordinate(Point point, String axis, double value) {
        return switch (axis) {
            case "x" -> new Point(value, point.y, point.z);
            case "y" -> new Point(point.x, value, point.z);
            default -> new Point(point.x, point.y, value);
        };
    }

    private record Face(List<Point> vertices, Point normal, double distance) {}

    private record Solid(List<Face> faces, Point min, Point max) {

        static Solid of(Cuboid cuboid) {
            List<Point> vertices = new ArrayList<>(8);
            for (int i = 0; i < 8; i++) {
                Point p = new Point((i & 1) == 0 ? cuboid.from.x : cuboid.to.x,
                        (i & 2) == 0 ? cuboid.from.y : cuboid.to.y,
                        (i & 4) == 0 ? cuboid.from.z : cuboid.to.z);
                vertices.add(cuboid.rotation == null ? p : cuboid.rotation.apply(p));
            }
            return ofCorners(vertices);
        }

        static Solid ofCorners(List<Point> vertices) {
            if (vertices.size() != 8) throw new IllegalArgumentException("A transformed box needs eight ordered corners");
            List<Face> faces = new ArrayList<>(6);
            for (int[] indices : FACE_CORNERS) {
                List<Point> polygon = new ArrayList<>(4);
                for (int i : indices) polygon.add(vertices.get(i));
                Point normal = polygon.get(1).subtract(polygon.getFirst()).cross(polygon.get(2).subtract(polygon.getFirst())).normalize();
                faces.add(new Face(polygon, normal, normal.dot(polygon.getFirst())));
            }
            Point min = new Point(vertices.stream().mapToDouble(Point::x).min().orElseThrow(),
                    vertices.stream().mapToDouble(Point::y).min().orElseThrow(), vertices.stream().mapToDouble(Point::z).min().orElseThrow());
            Point max = new Point(vertices.stream().mapToDouble(Point::x).max().orElseThrow(),
                    vertices.stream().mapToDouble(Point::y).max().orElseThrow(), vertices.stream().mapToDouble(Point::z).max().orElseThrow());
            return new Solid(faces, min, max);
        }

        boolean overlaps(Solid other) {
            return max.x >= other.min.x - EPSILON && other.max.x >= min.x - EPSILON && max.y >= other.min.y - EPSILON && other.max.y >= min.y - EPSILON && max.z >= other.min.z - EPSILON && other.max.z >= min.z - EPSILON;
        }
    }

    public static List<Line> build(List<Cuboid> cuboids) {
        List<Solid> solids = cuboids.stream().filter(Cuboid::hasVolume).distinct().map(Solid::of).toList();
        return buildSolids(solids);
    }

    /** Corners use bit 0 = high X, bit 1 = high Y, bit 2 = high Z, before transformation. */
    public static List<Line> buildTransformedBoxes(List<List<Point>> boxes) {
        return buildSolids(boxes.stream().map(Solid::ofCorners).toList());
    }

    private static List<Line> buildSolids(List<Solid> solids) {
        List<Face> boundary = new ArrayList<>();
        for (int i = 0; i < solids.size(); i++) {
            Solid solid = solids.get(i);
            for (Face face : solid.faces) {
                List<List<Point>> fragments = List.of(face.vertices);
                for (int j = 0; j < solids.size() && !fragments.isEmpty(); j++) {
                    Solid other = solids.get(j);
                    if (i == j || !solid.overlaps(other)) continue;
                    // Coincident outward faces have one owner. Without this rule,
                    // both solids would discard the shared surface and leave a hole.
                    if (j > i && other.faces.stream().anyMatch(f -> face.normal.dot(f.normal) > 1 - EPSILON && Math.abs(face.distance - f.distance) < EPSILON)) continue;
                    List<List<Point>> remaining = new ArrayList<>();
                    for (List<Point> fragment : fragments) remaining.addAll(subtract(fragment, other));
                    if (remaining.size() + boundary.size() > MAX_FRAGMENTS) {
                        throw new IllegalArgumentException("Model is too complex for a selection outline");
                    }
                    fragments = remaining;
                }
                for (List<Point> fragment : fragments) boundary.add(new Face(fragment, face.normal, face.distance));
            }
        }
        return creaseEdges(boundary);
    }

    /** Subtract a convex solid by retaining the outside part at each of its planes. */
    private static List<List<Point>> subtract(List<Point> polygon, Solid solid) {
        List<List<Point>> outside = new ArrayList<>();
        for (Face plane : solid.faces) {
            double min = Double.POSITIVE_INFINITY;
            double max = Double.NEGATIVE_INFINITY;
            for (Point vertex : polygon) {
                double distance = plane.normal.dot(vertex) - plane.distance;
                min = Math.min(min, distance);
                max = Math.max(max, distance);
            }
            if (min > EPSILON) {
                outside.add(polygon);
                return outside;
            }
            if (max <= EPSILON) continue;
            List<Point> insidePart = new ArrayList<>();
            List<Point> outsidePart = new ArrayList<>();
            for (int i = 0; i < polygon.size(); i++) {
                Point a = polygon.get(i);
                Point b = polygon.get((i + 1) % polygon.size());
                double da = plane.normal.dot(a) - plane.distance;
                double db = plane.normal.dot(b) - plane.distance;
                if (da <= EPSILON) insidePart.add(a);
                if (da >= -EPSILON) outsidePart.add(a);
                if (da < -EPSILON && db > EPSILON || da > EPSILON && db < -EPSILON) {
                    Point intersection = a.add(b.subtract(a).scale(da / (da - db)));
                    insidePart.add(intersection);
                    outsidePart.add(intersection);
                }
            }
            if (hasArea(outsidePart)) outside.add(outsidePart);
            if (!hasArea(insidePart)) return outside;
            polygon = insidePart;
        }
        return outside;
    }

    private static boolean hasArea(List<Point> polygon) {
        if (polygon.size() < 3) return false;
        Point normal = new Point(0, 0, 0);
        for (int i = 0; i < polygon.size(); i++) {
            normal = normal.add(polygon.get(i).cross(polygon.get((i + 1) % polygon.size())));
        }
        return normal.lengthSquared() > EPSILON * EPSILON;
    }

    private record LineKey(long dx, long dy, long dz, long ox, long oy, long oz) {

        static LineKey of(Point direction, Point origin) {
            return new LineKey(Math.round(direction.x * KEY_SCALE), Math.round(direction.y * KEY_SCALE),
                    Math.round(direction.z * KEY_SCALE), Math.round(origin.x * KEY_SCALE),
                    Math.round(origin.y * KEY_SCALE), Math.round(origin.z * KEY_SCALE));
        }
    }

    private record Edge(double start, double end, Point normal, Point direction, Point origin) {}

    private static List<Line> creaseEdges(List<Face> faces) {
        Map<LineKey, List<Edge>> groups = new LinkedHashMap<>();
        for (Face face : faces) {
            for (int i = 0; i < face.vertices.size(); i++) {
                Point a = face.vertices.get(i);
                Point b = face.vertices.get((i + 1) % face.vertices.size());
                Point delta = b.subtract(a);
                if (delta.lengthSquared() < EPSILON * EPSILON) continue;
                Point direction = delta.normalize();
                double leading = Math.abs(direction.x) > EPSILON ? direction.x : Math.abs(direction.y) > EPSILON ? direction.y : direction.z;
                if (leading < 0) direction = direction.scale(-1);
                Point origin = a.subtract(direction.scale(a.dot(direction)));
                double start = a.dot(direction);
                double end = b.dot(direction);
                groups.computeIfAbsent(LineKey.of(direction, origin), ignored -> new ArrayList<>())
                        .add(new Edge(Math.min(start, end), Math.max(start, end), face.normal, direction, origin));
            }
        }
        List<Line> lines = new ArrayList<>();
        for (List<Edge> edges : groups.values()) {
            // A long edge can meet several short edges. Partition it before
            // comparing normals; otherwise a coplanar seam survives at the join.
            TreeSet<Double> endpoints = new TreeSet<>();
            for (Edge edge : edges) {
                endpoints.add(edge.start);
                endpoints.add(edge.end);
            }
            List<Double> cuts = new ArrayList<>(endpoints);
            double runStart = Double.NaN;
            double runEnd = Double.NaN;
            Edge reference = edges.getFirst();
            for (int i = 1; i < cuts.size(); i++) {
                double start = cuts.get(i - 1);
                double end = cuts.get(i);
                if (end - start < EPSILON) continue;
                double midpoint = (start + end) / 2;
                Point firstNormal = null;
                boolean crease = false;
                for (Edge edge : edges) {
                    if (edge.start - EPSILON <= midpoint && midpoint <= edge.end + EPSILON) {
                        if (firstNormal == null) firstNormal = edge.normal;
                        else if (Math.abs(firstNormal.dot(edge.normal)) < 1 - 1.0e-6) crease = true;
                    }
                }
                if (crease) {
                    if (Double.isNaN(runStart)) runStart = start;
                    runEnd = end;
                } else if (!Double.isNaN(runStart)) {
                    lines.add(line(reference, runStart, runEnd));
                    runStart = Double.NaN;
                }
            }
            if (!Double.isNaN(runStart)) lines.add(line(reference, runStart, runEnd));
        }
        lines.sort(Comparator.comparingDouble((Line l) -> l.start.x).thenComparingDouble(l -> l.start.y).thenComparingDouble(l -> l.start.z));
        return List.copyOf(lines);
    }

    private static Line line(Edge reference, double start, double end) {
        return new Line(reference.origin.add(reference.direction.scale(start)), reference.origin.add(reference.direction.scale(end)));
    }
}
