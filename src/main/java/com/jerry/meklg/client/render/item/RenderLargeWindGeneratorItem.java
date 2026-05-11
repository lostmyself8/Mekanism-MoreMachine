package com.jerry.meklg.client.render.item;

import mekanism.api.MekanismAPITags;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.render.item.MekanismISTER;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.item.ItemStack;

import com.jerry.meklg.client.model.ModelLargeWindGenerator;
import com.jerry.meklg.client.model.ModelLargeWindGenerator.LargeWindGeneratorRotationRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

@NothingNullByDefault
public class RenderLargeWindGeneratorItem extends MekanismISTER<LargeWindGeneratorRotationRenderState> {

    // public static final RenderLargeWindGeneratorItem RENDERER = new RenderLargeWindGeneratorItem();
    private static final int SPEED = 16;
    private static int lastTicksUpdated = 0;
    private static int angle = 0;
    private ModelLargeWindGenerator windGenerator;
    private static LargeWindGeneratorRotationRenderState ZERO_ANGLE = new LargeWindGeneratorRotationRenderState(0);

    public RenderLargeWindGeneratorItem(EntityModelSet entityModelSet) {
        windGenerator = new ModelLargeWindGenerator(entityModelSet);
    }

    @Override
    public void submit(@Nullable LargeWindGeneratorRotationRenderState argument, PoseStack matrix, SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
        if (argument == null) {
            return;
        }
        windGenerator.setupAnim(argument);
        matrix.pushPose();
        matrix.translate(0.5, 0.5, 0.5);
        matrix.mulPose(Axis.ZP.rotationDegrees(180));
        windGenerator.collect(argument, matrix, submitNodeCollector, lightCoords, overlayCoords, hasFoil);
        matrix.popPose();
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        windGenerator.setupAnim(ZERO_ANGLE);
        windGenerator.root().getExtentsForGui(new PoseStack(), output);
    }

    @Override
    public @Nullable LargeWindGeneratorRotationRenderState extractArgument(ItemStack itemStack) {
        Minecraft minecraft = Minecraft.getInstance();
        boolean tickingNormally = MekanismRenderer.isRunningNormally();
        if (tickingNormally && minecraft.level != null) {
            // Only update the angle if we are in a world and that world is not blacklisted
            if (minecraft.level.dimensionTypeRegistration().is(MekanismAPITags.DimensionTypes.NO_WIND)) {
                // If the dimension is blacklisted, don't try to tick it at all
                tickingNormally = false;
            } else {
                int ticks = Minecraft.getInstance().levelRenderer.getTicks();
                if (lastTicksUpdated != ticks) {
                    angle = (angle + SPEED) % 360;
                    lastTicksUpdated = ticks;
                }
            }
        }
        LargeWindGeneratorRotationRenderState state = new LargeWindGeneratorRotationRenderState(angle);
        if (tickingNormally) {
            state.angle = (state.angle + SPEED * MekanismRenderer.getPartialTick()) % 360;
        }
        return state;
    }
}
