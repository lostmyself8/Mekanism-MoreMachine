package com.jerry.mekmm.client.renderer;

import com.jerry.mekmm.Mekmm;

import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.mojang.blaze3d.pipeline.RenderPipeline;

import java.util.Optional;

public class MoreMachineRenderType {

    private static final RenderPipeline LINES_NO_DEPTH_PIPELINE = RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
            .withLocation(Mekmm.rl("pipeline/lines_no_depth"))
            .withDepthStencilState(Optional.empty())
            .build();

    public static final RenderType LINES_NO_DEPTH = RenderType.create(
            "lines_no_depth",
            RenderSetup.builder(LINES_NO_DEPTH_PIPELINE)
                    .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                    .setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
                    .createRenderSetup());
}
