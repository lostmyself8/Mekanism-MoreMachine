package com.jerry.meklg.client.render.item;

import com.jerry.meklg.client.model.ModelSolarHeatGenerator;

import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.ItemStack;

import com.jerry.meklg.client.model.ModelSolarHeatGenerator.SolarHeatGeneratorRotationRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.MapCodec;
import org.joml.Vector3fc;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

@NullMarked
public class RenderSolarHeatGeneratorItem implements SpecialModelRenderer<SolarHeatGeneratorRotationRenderState> {

    private final ModelSolarHeatGenerator solarHeatGenerator;
    private static final SolarHeatGeneratorRotationRenderState ZERO_ANGLE = new SolarHeatGeneratorRotationRenderState(0);

    public RenderSolarHeatGeneratorItem(EntityModelSet entityModelSet) {
        solarHeatGenerator = new ModelSolarHeatGenerator(entityModelSet);
    }

    @Override
    public void submit(@Nullable SolarHeatGeneratorRotationRenderState argument, PoseStack matrix, SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
        matrix.pushPose();
        matrix.translate(0.5, 0.5, 0.5);
        matrix.mulPose(Axis.ZP.rotationDegrees(180));
        solarHeatGenerator.collectItem(argument, matrix, submitNodeCollector, lightCoords, overlayCoords, hasFoil);
        matrix.popPose();
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        solarHeatGenerator.setupAnim(ZERO_ANGLE);
        solarHeatGenerator.root().getExtentsForGui(new PoseStack(), output);
    }

    @Override
    public @Nullable SolarHeatGeneratorRotationRenderState extractArgument(ItemStack itemStack) {
        return ZERO_ANGLE;
    }

    public static class Unbaked implements SpecialModelRenderer.Unbaked<SolarHeatGeneratorRotationRenderState> {

        public static final Unbaked INSTANCE = new Unbaked();
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(INSTANCE);

        @Override
        public SpecialModelRenderer<SolarHeatGeneratorRotationRenderState> bake(BakingContext context) {
            return new RenderSolarHeatGeneratorItem(context.entityModelSet());
        }

        @Override
        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }
    }
}
