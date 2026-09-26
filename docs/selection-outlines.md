# Maintaining large-machine selection outlines

This feature changes the black outline shown while hovering the eight supported large
machines. It does not change their targeting, collision, bounding-block layout, ports,
recipes, or model assets.

## Where the code lives

| File | Responsibility |
| --- | --- |
| `MachineSelectionOutline` | Supported-machine list, hover event, main-block lookup, model/state caches, block-facing transform, and drawing. |
| `outline/ModelOutlineLoader` | Read the existing JSON model elements and composite parents using the current resource manager. |
| `outline/ModelOutlineGeometry` | Pure geometry: union rotated cuboids and extract exterior crease edges. No Minecraft classes. |
| `ModelOutlineGeometryTest` in `src/outlineTest/java` | Analytical regression cases and checks against every supported on/off model. |
| `ClientRegistration` | Register the hover listener and clear both caches after model baking. |

The outline uses the model files as its coordinate source. Do not maintain another
handwritten list of keyboard or corner coordinates.

## Why a solid union is necessary

A voxel shape can only express axis-aligned boxes. Its edges cannot follow a smooth
sloped keyboard or chamfer. Drawing every edge of every model element has a different
problem: rotated pieces often overlap the neighbouring rectangular panels, exposing
buried edges as diamonds or boxes over the visible surface.

The geometry helper treats each supported solid element as a rotated cuboid:

1. Form its six outward-facing planes after applying the JSON element rotation.
2. Subtract the other cuboids from each face. Keep only exposed face fragments.
3. Give coincident outward faces one owner, so adjoining pieces cannot both delete
   the same surface.
4. Group collinear fragment edges and split them at every endpoint. This handles
   long edges meeting several shorter ones at a T-junction.
5. Keep segments shared by non-coplanar faces and merge consecutive segments.
   Coplanar fragment boundaries are seams, so they are discarded.

For example, a rotated square joining two perpendicular panels contributes the
exposed diagonal across the corner. The portions buried in those panels do not
produce outline edges. This case is covered by a regression test with an exact
expected result: a seven-sided prism with 21 edges.

The algorithm is view independent. Minecraft's normal line depth test handles
visibility when drawing, with the vanilla black colour and 0.4 alpha.

## Coordinate and rendering contracts

- Input coordinates are **model pixels**, including negative values; 16 pixels are
  one block. Element rotation occurs about the JSON `origin`, with optional
  `rescale`.
- These eight models all use north as the default orientation and a **+1 block Y**
  translation in their baked-model wrappers. `toBlock` applies that translation
  and the four horizontal facings around the block centre.
- A hit on a Mekanism bounding block is resolved to the main machine before
  choosing and translating the outline. Do not add the bounding block offset again.
- Supported block types must omit `AttributeCustomSelectionBox`. Keeping the JSON
  attribute lets Mekanism draw a second wireframe before this handler.
- The listener runs at LOWEST priority so Mekanism's configurator overlay still
  runs first. It does not receive already-cancelled highlight events.
- The pigment mixer's active rod is separate animated geometry. Its existing
  `IWireFrameRenderer` draws it using the current animation transform; only the
  machine's static model enters the outline cache.
- Seasonal transforms on the supported models currently replace textures only;
  they do not change the coordinates used here.

## Model support and deliberate exclusions

The loader supports ordinary element models, parent inheritance, and untransformed
`neoforge:composite` children. It reads the current resource manager, so edits to
supported model resources are picked up after a resource reload.

The solid union deliberately excludes:

- Zero-thickness texture planes and elements without faces.
- Inverted `from`/`to` elements used for inward decorative surfaces.
- Hidden composite children and children marked `translucent`, including glass
  and beam effects. Treating glass as an opaque solid can obscure the geometry of
  the chamber it surrounds.

This is an outline of the solid model elements, not an alpha-mask contour of every
texture. Missing individual cuboid faces do not turn a cuboid into a hollow solid.
A resource pack that represents a structural opening only with transparent pixels,
or uses a custom loader or composite transform, needs separate evaluation.

Unsupported loaders/transforms, bad resources, parent cycles, or excessive geometry
fall back to the block's ordinary voxel outline. A warning identifies the model.
The failed result is cached to avoid repeated work and log spam on every frame.

## Adding another affected machine

1. Reproduce the problem and identify whether it is a visual outline error or an
   actual targeting/collision error. This handler addresses the visual outline.
2. Verify the model uses `mekmm:block/large_machine/<block_registry_path>/off`
   and `on`, the +1 Y baked translation, and the same horizontal facing convention.
   Inspect any animated renderer and composite children as well.
3. Add the registry path to `SUPPORTED_MACHINES` in `MachineSelectionOutline`.
   Remove its `AttributeCustomSelectionBox` registration to prevent double drawing.
4. Add both model variants to the regression runner's model list. If the machine
   needs another transform or loader, implement and test that support explicitly;
   do not broaden the allowlist to every MekMM block.
5. Run the checks below and capture matching before/after screenshots in game.
   Include the new regression geometry when fixing an algorithmic edge case.

For an existing supported machine, ordinary element-coordinate or rotation changes
need no outline-coordinate changes in Java. Rerun the model checks and visual checks.

## Checks

Run with Java 21:

```text
./gradlew verifySelectionOutlines
./gradlew build
```

Use `gradlew.bat` on Windows. `check` (and therefore `build`) includes
`verifySelectionOutlines`. The standalone source set uses the existing compile
dependencies rather than downloading or launching a full development modpack; it
does not add a test framework.

The automated checks cover touching and overlapping cuboids, containment, duplicate
solids, T-junctions, the sloped panel join, keyboard rotation, all rotation axes with
rescale, inherited/composite models, unsupported resources, and all 16 supplied
machine variants. Model checks reject non-finite or zero-length edges and sample
each output edge to ensure it is not buried inside another solid.

These checks do not establish in-game visual acceptance. For each affected machine,
verify:

- Four horizontal facings, active and inactive.
- Hovering the main block and different bounding blocks.
- Keyboards, chamfered panel corners, chamber rims, pipes, and top assemblies from
  the reported angles.
- The active pigment mixer rod and translucent chambers/effects.
- Configurator/Portswitch overlays, GUI interaction, and unchanged physical collision.
- F3+T/resource-pack reload and repeated hovering without continued rebuilds.

## Cache and numerical maintenance

The model cache stores pixel-space lines once per model resource/on-off variant.
The state cache stores transformed vertices and line directions, so normalisation,
resource reads, and clipping are not repeated every frame. Both are cleared after
model baking. Geometry is currently built on first hover of a variant.

Clipping uses a small model-space tolerance (`1e-7` pixels); collinear line keys use
a `1e-6` pixel grid to absorb floating-point noise. Do not increase these to hide a
visible defect: doing so can erase small model details. Add a reduced geometry case
to the regression runner when changing clipping or edge grouping.

The loader caps solid elements at 512 and the clipper bounds fragment growth.
These limits are well above the current models and keep an unsupported resource
pack from causing unbounded work. Raising them requires checking cold-build time
and memory as well as the resulting outline.
