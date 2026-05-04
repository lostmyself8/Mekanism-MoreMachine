package com.jerry.mekmm.client.model;

import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.SimpleModelWrapper;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class TranslatedBlockStateModel implements BlockStateModel {

    private final BlockStateModel original;
    private final float x;
    private final float y;
    private final float z;
    private final Map<BlockStateModelPart, BlockStateModelPart> transformedParts = Collections.synchronizedMap(new IdentityHashMap<>());

    public TranslatedBlockStateModel(BlockStateModel original, float x, float y, float z) {
        this.original = original;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    @Deprecated
    public void collectParts(RandomSource random, List<BlockStateModelPart> output) {
        List<BlockStateModelPart> parts = new ArrayList<>();
        original.collectParts(random, parts);
        addTransformedParts(parts, output);
    }

    @Override
    public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockStateModelPart> parts) {
        List<BlockStateModelPart> originalParts = new ArrayList<>();
        original.collectParts(level, pos, state, random, originalParts);
        addTransformedParts(originalParts, parts);
    }

    @Override
    public @Nullable Object createGeometryKey(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random) {
        Object key = original.createGeometryKey(level, pos, state, random);
        return key == null ? null : new GeometryKey(key, this);
    }

    @Override
    @Deprecated
    public Material.Baked particleMaterial() {
        return original.particleMaterial();
    }

    @Override
    public Material.Baked particleMaterial(BlockAndTintGetter level, BlockPos pos, BlockState state) {
        return original.particleMaterial(level, pos, state);
    }

    @Override
    @Deprecated
    public @BakedQuad.MaterialFlags int materialFlags() {
        return original.materialFlags();
    }

    @Override
    public @BakedQuad.MaterialFlags int materialFlags(BlockAndTintGetter level, BlockPos pos, BlockState state) {
        return original.materialFlags(level, pos, state);
    }

    private void addTransformedParts(List<BlockStateModelPart> originalParts, List<BlockStateModelPart> output) {
        for (BlockStateModelPart part : originalParts) {
            output.add(transformedParts.computeIfAbsent(part, this::transformPart));
        }
    }

    private BlockStateModelPart transformPart(BlockStateModelPart part) {
        QuadCollection.Builder builder = new QuadCollection.Builder();
        for (BakedQuad quad : translate(part.getQuads(null))) {
            builder.addUnculledFace(quad);
        }
        for (Direction direction : Direction.values()) {
            for (BakedQuad quad : translate(part.getQuads(direction))) {
                builder.addCulledFace(direction, quad);
            }
        }
        return new SimpleModelWrapper(builder.build(), part.useAmbientOcclusion(), part.particleMaterial());
    }

    private List<BakedQuad> translate(List<BakedQuad> quads) {
        List<BakedQuad> translated = new ArrayList<>(quads.size());
        for (BakedQuad quad : quads) {
            translated.add(new BakedQuad(
                    translate(quad.position0()),
                    translate(quad.position1()),
                    translate(quad.position2()),
                    translate(quad.position3()),
                    quad.packedUV0(),
                    quad.packedUV1(),
                    quad.packedUV2(),
                    quad.packedUV3(),
                    quad.direction(),
                    quad.materialInfo(),
                    quad.bakedNormals(),
                    quad.bakedColors()));
        }
        return translated;
    }

    private Vector3f translate(Vector3fc position) {
        return new Vector3f(position).add(x, y, z);
    }

    private record GeometryKey(Object originalKey, TranslatedBlockStateModel model) {}
}
