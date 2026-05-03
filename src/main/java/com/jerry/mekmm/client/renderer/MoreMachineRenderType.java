package com.jerry.mekmm.client.renderer;

import net.minecraft.client.renderer.RenderPipelines;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;

import java.util.Optional;

public class MoreMachineRenderType {

    // public static final RenderType LINES_NO_DEPTH = RenderType.create(
    // "lines_no_depth",
    // DefaultVertexFormat.POSITION_COLOR_NORMAL,
    // VertexFormat.Mode.LINES,
    // 1536,
    // RenderType.CompositeState.builder()
    // .setShaderState(RENDERTYPE_LINES_SHADER)
    // .setLineState(new LineStateShard(OptionalDouble.empty()))
    // .setLayeringState(VIEW_OFFSET_Z_LAYERING)
    // .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
    // .setOutputState(ITEM_ENTITY_TARGET)
    // // 禁用深度测试
    // .setDepthTestState(NO_DEPTH_TEST)
    // .setCullState(NO_CULL)
    // .createCompositeState(false));
    //
    // public static final RenderPipeline LINES_NO_DEPTH = RenderPipeline.builder(
    // RenderPipeline.builder(RenderPipeline.DEBUG_LINE_SNIPPET)
    // .withLocation(Mekmm.rl("pipeline/lines_no_depth"))
    // .withDepthStencilState(Optional.empty()) // 禁用深度测试，即穿墙显示
    // .build()
    // );

    public static final RenderPipeline.Snippet LINES_NO_DEPTH = RenderPipeline.builder(RenderPipelines.MATRICES_FOG_SNIPPET, RenderPipelines.GLOBALS_SNIPPET)
            .withVertexShader("core/rendertype_lines")
            .withFragmentShader("core/rendertype_lines")
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .withCull(false)
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR_NORMAL_LINE_WIDTH, VertexFormat.Mode.LINES)
            .withDepthStencilState(Optional.empty())
            .buildSnippet();
}
