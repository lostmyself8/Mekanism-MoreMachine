package com.jerry.meklg.client.render.item;

import mekanism.api.annotations.NothingNullByDefault;

import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.ItemStack;

import com.jerry.meklg.client.model.ModelSolarHeatGenerator;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.MapCodec;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

@NothingNullByDefault
public class RenderSolarHeatGeneratorItem implements SpecialModelRenderer<Void> {

    private final ModelSolarHeatGenerator solarHeatGenerator;

    public RenderSolarHeatGeneratorItem(EntityModelSet entityModelSet) {
        solarHeatGenerator = new ModelSolarHeatGenerator(entityModelSet);
    }

    @Override
    public void submit(@Nullable Void argument, PoseStack matrix, SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
        matrix.pushPose();
        matrix.translate(0.5, 0.5, 0.5);
        matrix.mulPose(Axis.ZP.rotationDegrees(180));
        solarHeatGenerator.collectItem(matrix, submitNodeCollector, lightCoords, overlayCoords, hasFoil);
        matrix.popPose();
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        // Keep the item renderer bounded roughly to the 7x7x7 multiblock model.
    }

    @Override
    public @Nullable Void extractArgument(ItemStack itemStack) {
        return null;
    }

    public static class Unbaked implements SpecialModelRenderer.Unbaked<Void> {

        public static final Unbaked INSTANCE = new Unbaked();
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(INSTANCE);

        @Override
        public SpecialModelRenderer<Void> bake(BakingContext context) {
            return new RenderSolarHeatGeneratorItem(context.entityModelSet());
        }

        @Override
        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }
    }
}
