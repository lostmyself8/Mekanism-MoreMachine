# Maintaining large-machine selection outlines

This feature changes the black outline shown while hovering eight JSON-model machines and the wind generator base. It does not change their targeting, collision, bounding-block layout, ports,
recipes, or model assets.

## Where the code lives

| File | Responsibility |
| --- | --- |
| `MachineSelectionOutline` | Hover event, main-block lookup, model/state caches, block-facing transform, and drawing. |
| `outline/ModelOutlineProfiles` | Explicit JSON-machine list, baked Y offsets, and optional inset-joining policy. |
| `outline/ModelPartOutline` | Capture the wind generator base from its actual nested Java ModelParts and draw cached union edges. |
| `outline/ModelOutlineLoader` | Read the existing JSON model elements and composite parents using the current resource manager. |
| `outline/ModelOutlineGeometry` | Pure geometry: union rotated cuboids and extract exterior crease edges. No Minecraft classes. |
| `ModelOutlineGeometryTest` in `src/outlineTest/java` | Analytical regression cases and checks against every supported on/off model. |
| `ClientRegistration` | Register the hover listener and clear both caches after model baking. |

The outline uses JSON elements or Java ModelParts as its coordinate source. Do not maintain another
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
- These eight JSON models use north as the default orientation. Seven wrappers
  translate **+1 block Y**, but `LargeElectrolyticSeparatorBakedModel` applies
  **no Y translation**. `ModelOutlineProfiles` records that distinction; `toBlock`
  applies it along with the four horizontal facings around the block centre.
  The model regression checks require every transformed base to meet Y=0.
- A hit on a Mekanism bounding block is resolved to the main machine before
  choosing and translating the outline. Do not add the bounding block offset again.
- JSON-profile block types must omit `AttributeCustomSelectionBox`. Keeping the JSON
  attribute lets Mekanism draw a second wireframe before this handler.
- The listener runs at LOWEST priority so Mekanism's configurator overlay still
  runs first. It does not receive already-cancelled highlight events.
- The pigment mixer's active rod is separate animated geometry. Its existing
  `IWireFrameRenderer` draws it using the current animation transform; only the
  machine's static model enters the outline cache.
- Seasonal transforms on the supported models currently replace textures only;
  they do not change the coordinates used here.

## Nucleosynthesizer inset joins

The chamber's rotated corner pieces are authored 0.01 model pixels thinner than
its rectangular frame (for example, -14.99/-11.01 beside -15/-11). Exact union
therefore leaves a shallow step, so the slant appears separate from the frame.
Its profile enables `joinInsets` with a 0.011-pixel limit. Before clipping, it
extends only the rotated element's unchanged-axis end planes to the matching
outer planes of overlapping unrotated neighbours. It does not round coordinates,
change the rotation, or modify the actual mesh/collision. Comparisons use the
original planes to prevent chained expansion. The heat generator uses the separate alignment option below; all other JSON
profiles disable joining.

This is an explicit cosmetic simplification of a known modelling inset, separate
from the clipper's numerical epsilon. The recessed-corner regression requires a
continuous bevel; a smaller tolerance must preserve the original geometry.

## Heat generator shifted joins

The top and side cover corners are shifted by 0.001 pixels along their rotation
axis: side X=-14.999/-13.999 versus panel X=-15/-14, and top Y=27.999/28.999
versus panel Y=28/29. Expansion alone cannot remove the small step on both faces.
The heat profile enables `alignShiftedFaces` with a 0.0011-pixel limit, selecting
the nearest matching end plane of an overlapping unrotated neighbour. This can
move an outline plane inward or outward. It keeps the original rotation and
compares original coordinates to avoid cumulative adjustments.

The chamber profile retains expansion-only joining. Neither policy edits model
assets or collision. Tests cover both signs of the shift, preserve larger steps,
and verify that precisely the two heat cover corners change in both model states.

## Wind generator base

The wind generator uses a Java model for its structure and an OBJ for screen
textures. Keep its existing JAVA selection attribute and renderer: the separate
JSON hover handler does not intercept it. `ModelLargeWindGenerator` replaces only
its static BASE subtree's wireframe with union edges; the fan, tower, and top use
their existing renderer and animation. Screen texture quads remain with Mekanism's
combined outline path.

`ModelPartOutline` visits that subtree with an identity pose, including nested
part rotations and offsets. It converts its eight cube corners to local pixel
coordinates and reuses the same union helper. It then draws beneath the existing
wind renderer's world/facing pose. Capture happens once on first hover and the
model instance is recreated on resource reload. A failed capture logs once and
falls back to the original base wireframe.

The adapter is scoped to the base's visible, undeformed, positively scaled cubes.
It reads cube bounds, not UVs or cube deformation; if this model starts using
nonzero CubeDeformation, hidden parts, or negative scaling, extend the adapter
before reusing it. Do not cache an animated subtree in this way. The regression
runner bakes the actual wind model layer and checks its floor and rotated keyboard.

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
   and `on`, its baked Y translation, and the same horizontal facing convention.
   Inspect any animated renderer and composite children as well.
3. Add the registry path and its Y offset to `ModelOutlineProfiles.MACHINES`.
   Remove its `AttributeCustomSelectionBox` registration to prevent double drawing.
4. The regression runner checks both variants of every profile automatically. If the machine
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
rescale, inset joins, inherited/composite models, unsupported resources, the wind
base/keyboard transform, and all 16 supplied
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
