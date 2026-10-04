package com.jerry.mekmm.client.render;

import com.jerry.mekmm.Mekmm;
import com.jerry.mekmm.client.render.outline.ModelOutlineGeometry;
import com.jerry.mekmm.client.render.outline.ModelOutlineGeometry.Line;
import com.jerry.mekmm.client.render.outline.ModelOutlineGeometry.Point;
import com.jerry.mekmm.client.render.outline.ModelOutlineLoader;
import com.jerry.mekmm.client.render.outline.ModelOutlineProfiles;
import com.jerry.mekmm.client.render.outline.ModelOutlineProfiles.Profile;

import mekanism.client.render.tileentity.IWireFrameRenderer;
import mekanism.common.block.BlockBounding;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.block.attribute.AttributeStateFacing;
import mekanism.common.registries.MekanismBlocks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Cosmetic selection outlines for the reported large machines. Geometry comes
 * from their solid model elements, including rotations, rather than the separate
 * axis-aligned targeting/collision shapes. See docs/selection-outlines.md.
 */
public class MachineSelectionOutline {

    public static final MachineSelectionOutline INSTANCE = new MachineSelectionOutline();

    private final Map<String, List<Line>> modelLines = new HashMap<>();
    private final Map<BlockState, List<RenderLine>> stateLines = new HashMap<>();

    // Let Mekanism handle configurator overlays first. Supported blocks must not
    // also register AttributeCustomSelectionBox, which would draw another outline.
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onBlockHover(RenderHighlightEvent.Block event) {
        Minecraft minecraft = Minecraft.getInstance();
        Level level = minecraft.level;
        if (level == null || minecraft.player == null) return;
        BlockPos mainPos = event.getTarget().getBlockPos();
        if (!level.getWorldBorder().isWithinBounds(mainPos)) return;
        BlockState state = level.getBlockState(mainPos);
        if (state.is(MekanismBlocks.BOUNDING_BLOCK)) {
            mainPos = BlockBounding.getMainBlockPos(level, mainPos);
            if (mainPos == null) return;
            state = level.getBlockState(mainPos);
        }
        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        if (!blockId.getNamespace().equals(Mekmm.MOD_ID) || !ModelOutlineProfiles.MACHINES.containsKey(blockId.getPath())) return;
        AttributeStateFacing attribute = Attribute.get(state, AttributeStateFacing.class);
        if (attribute == null) return;
        Direction facing = attribute.getDirection(state);
        if (!facing.getAxis().isHorizontal()) return;
        Profile profile = ModelOutlineProfiles.MACHINES.get(blockId.getPath());
        List<RenderLine> lines = stateLines.computeIfAbsent(state, key -> {
            String model = Mekmm.MOD_ID + ":block/large_machine/" + blockId.getPath() + "/" + (Attribute.isActive(key) ? "on" : "off");
            return modelLines.computeIfAbsent(model, id -> loadLines(id, profile)).stream().map(line -> RenderLine.of(line, facing, profile.yOffsetBlocks())).toList();
        });
        // Unsupported resource-pack geometry or a failed load retains vanilla's
        // voxel outline. Cache the failure too, so it is not retried every frame.
        if (lines.isEmpty()) return;
        Vec3 camera = event.getCamera().getPosition();
        PoseStack pose = event.getPoseStack();
        pose.pushPose();
        pose.translate(mainPos.getX() - camera.x, mainPos.getY() - camera.y, mainPos.getZ() - camera.z);
        VertexConsumer buffer = event.getMultiBufferSource().getBuffer(RenderType.lines());
        for (RenderLine line : lines) line.render(pose.last(), buffer);

        // The pigment mixer rod is rendered separately while active. Keep its
        // existing animation transform; it must never enter the static cache.
        BlockEntity tile = level.getBlockEntity(mainPos);
        if (tile != null && minecraft.getBlockEntityRenderDispatcher().getRenderer(tile) instanceof IWireFrameRenderer animated && animated.hasSelectionBox(state)) {
            animated.renderWireFrame(tile, event.getDeltaTracker().getGameTimeDeltaPartialTick(false), pose, buffer);
        }
        pose.popPose();
        event.setCanceled(true);
    }

    private List<Line> loadLines(String model, Profile profile) {
        try {
            var cuboids = ModelOutlineLoader.load(id -> {
                ResourceLocation location = ResourceLocation.parse(id);
                return Minecraft.getInstance().getResourceManager().getResourceOrThrow(
                        location.withPath("models/" + location.getPath() + ".json")).openAsReader();
            }, model);
            return ModelOutlineGeometry.build(ModelOutlineGeometry.joinInsets(cuboids, profile.insetJoinPixels(), profile.alignShiftedFaces()));
        } catch (IOException | RuntimeException exception) {
            Mekmm.LOGGER.warn("Could not build selection outline for {}; using voxel outline", model, exception);
            return List.of();
        }
    }

    /** Called after every model bake, including F3+T and resource-pack changes. */
    public void clearCache() {
        modelLines.clear();
        stateLines.clear();
    }

    private static Vec3 toBlock(Point point, Direction facing, double yOffset) {
        double x = point.x() / 16;
        double y = point.y() / 16 + yOffset;
        double z = point.z() / 16;
        return switch (facing) {
            case NORTH -> new Vec3(x, y, z);
            case SOUTH -> new Vec3(1 - x, y, 1 - z);
            case WEST -> new Vec3(z, y, 1 - x);
            case EAST -> new Vec3(1 - z, y, x);
            default -> throw new IllegalArgumentException("Expected horizontal facing");
        };
    }

    private record RenderLine(Vec3 start, Vec3 end, Vec3 direction) {

        static RenderLine of(Line line, Direction facing, double yOffset) {
            Vec3 start = toBlock(line.start(), facing, yOffset);
            Vec3 end = toBlock(line.end(), facing, yOffset);
            return new RenderLine(start, end, end.subtract(start).normalize());
        }

        void render(PoseStack.Pose pose, VertexConsumer buffer) {
            buffer.addVertex(pose, (float) start.x, (float) start.y, (float) start.z)
                    .setColor(0, 0, 0, 102).setNormal(pose, (float) direction.x, (float) direction.y, (float) direction.z);
            buffer.addVertex(pose, (float) end.x, (float) end.y, (float) end.z)
                    .setColor(0, 0, 0, 102).setNormal(pose, (float) direction.x, (float) direction.y, (float) direction.z);
        }
    }
}
