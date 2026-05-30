package com.jerry.meklg.client.model;

import com.jerry.mekmm.Mekmm;

import mekanism.client.model.MekanismJavaModel;
import mekanism.client.model.ModelPartData;

import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ModelSolarHeatGenerator extends MekanismJavaModel {

    public static final ModelLayerLocation SOLAR_HEAT_GENERATOR_LAYER = new ModelLayerLocation(Mekmm.rl("solar_heat_generator"), "main");
    private static final ResourceLocation SOLAR_HEAT_GENERATOR_TEXTURE = Mekmm.rl("render/solar_heat_generator.png");

    private static final ModelPartData ports_1 = new ModelPartData("ports_1", CubeListBuilder.create()
            .texOffs(46, 371).addBox(-5.0F, -29.0F, 55.0F, 10.0F, 10.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(334, 374).addBox(-20.0F, -28.0F, 55.0F, 8.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(352, 374).addBox(12.0F, -28.0F, 55.0F, 8.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(46, 371).addBox(-5.0F, -13.0F, 55.0F, 10.0F, 10.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(284, 375).addBox(12.0F, -12.0F, 55.0F, 8.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(334, 374).addBox(-20.0F, -12.0F, 55.0F, 8.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F));

    private static final ModelPartData ports_2 = new ModelPartData("ports_2", CubeListBuilder.create()
            .texOffs(370, 374).addBox(-20.0F, -12.0F, -56.0F, 8.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(370, 374).addBox(12.0F, -12.0F, -56.0F, 8.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F));

    private static final ModelPartData ports = new ModelPartData("ports", CubeListBuilder.create(),
            PartPose.offset(0.0F, 0.0F, 0.0F), ports_1, ports_2);

    private static final ModelPartData base_connector_r1 = new ModelPartData("base_connector_r1", CubeListBuilder.create()
            .texOffs(128, 283).addBox(-32.0F, -16.0F, -48.0F, 64.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(272, 224).addBox(-32.0F, -16.0F, -56.0F, 64.0F, 16.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

    private static final ModelPartData base_side = new ModelPartData("base_side", CubeListBuilder.create(),
            PartPose.offset(0.0F, 0.0F, 0.0F), base_connector_r1);

    private static final ModelPartData base_connector_r2 = new ModelPartData("base_connector_r2", CubeListBuilder.create()
            .texOffs(128, 283).addBox(-32.0F, -16.0F, -48.0F, 64.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(272, 224).addBox(-32.0F, -16.0F, -56.0F, 64.0F, 16.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

    private static final ModelPartData base_side2 = new ModelPartData("base_side2", CubeListBuilder.create(),
            PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F), base_connector_r2);

    private static final ModelPartData base_connector = new ModelPartData("base_connector", CubeListBuilder.create()
            .texOffs(272, 283).addBox(-24.0F, -32.0F, 48.0F, 48.0F, 32.0F, 7.0F, new CubeDeformation(0.0F))
            .texOffs(0, 283).addBox(-24.0F, -32.0F, 32.0F, 48.0F, 24.0F, 16.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F));

    private static final ModelPartData base_connector_r3 = new ModelPartData("base_connector_r3", CubeListBuilder.create()
            .texOffs(320, 104).addBox(-23.0F, -8.0F, 0.0F, 46.0F, 11.0F, 12.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-7.5F, 0.0F, -62.0F, 0.3927F, 0.0F, 0.0F));

    private static final ModelPartData screen1_r2 = new ModelPartData("screen1_r2", CubeListBuilder.create()
            .texOffs(284, 363).addBox(-14.5F, -7.0F, -2.5F, 14.0F, 10.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(364, 253).addBox(-12.0F, -4.0F, -1.0F, 12.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-16.5F, 0.0F, -7.5F, 0.0F, -0.3927F, 0.0F));

    private static final ModelPartData screen3_r2 = new ModelPartData("screen3_r2", CubeListBuilder.create()
            .texOffs(284, 363).addBox(0.5F, -7.0F, -2.5F, 14.0F, 10.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(92, 371).addBox(0.0F, -4.0F, -1.0F, 12.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.5F, 0.0F, -7.5F, 0.0F, 0.3927F, 0.0F));

    private static final ModelPartData keyboard_r1 = new ModelPartData("keyboard_r1", CubeListBuilder.create()
            .texOffs(324, 248).addBox(-7.0F, -1.0F, -2.0F, 14.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-8.0F, 6.0F, -13.0F, 0.3927F, 0.0F, 0.0F));

    private static final ModelPartData monitor = new ModelPartData("monitor", CubeListBuilder.create()
            .texOffs(364, 248).addBox(-16.5F, -4.0F, -8.5F, 17.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(258, 299).addBox(-10.5F, 0.0F, -8.5F, 5.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(284, 363).addBox(-15.0F, -7.0F, -9.75F, 14.0F, 10.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.5F, -12.0F, -53.0F), screen1_r2, screen3_r2, keyboard_r1);

    private static final ModelPartData controller = new ModelPartData("controller", CubeListBuilder.create()
            .texOffs(48, 348).mirror().addBox(-31.5F, -8.0F, -69.0F, 16.0F, 16.0F, 7.0F, new CubeDeformation(0.0F)).mirror(false)
            .texOffs(48, 348).addBox(0.5F, -8.0F, -69.0F, 16.0F, 16.0F, 7.0F, new CubeDeformation(0.0F))
            .texOffs(208, 363).addBox(-15.5F, -5.0F, -68.0F, 16.0F, 13.0F, 6.0F, new CubeDeformation(0.0F))
            .texOffs(128, 318).addBox(-31.5F, -24.0F, -58.0F, 48.0F, 24.0F, 7.0F, new CubeDeformation(0.0F))
            .texOffs(128, 299).addBox(-31.5F, -26.0F, -68.0F, 48.0F, 2.0F, 17.0F, new CubeDeformation(0.0F)),
            PartPose.offset(7.5F, -8.0F, 14.0F), base_connector_r3, monitor);

    private static final ModelPartData BASE = new ModelPartData("base", CubeListBuilder.create()
            .texOffs(0, 0).addBox(-48.0F, -8.0F, -48.0F, 96.0F, 8.0F, 96.0F, new CubeDeformation(0.0F))
            .texOffs(0, 104).addBox(-40.0F, -24.0F, -40.0F, 80.0F, 16.0F, 80.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 24.0F, 0.0F), ports, base_side, base_side2, base_connector, controller);

    private static final ModelPartData side_pillar_r1 = new ModelPartData("side_pillar_r1", CubeListBuilder.create()
            .texOffs(100, 323).addBox(-4.05F, -42.0F, 0.0F, 6.0F, 42.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-45.0F, -18.0F, -12.0F, -0.1745F, 0.0F, 0.0F));

    private static final ModelPartData side_pillar_r2 = new ModelPartData("side_pillar_r2", CubeListBuilder.create()
            .texOffs(100, 323).addBox(-3.0F, -42.0F, -6.0F, 6.0F, 42.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-46.0F, -18.0F, 12.0F, 0.1745F, 0.0F, 0.0F));

    private static final ModelPartData side_support = new ModelPartData("side_support", CubeListBuilder.create()
            .texOffs(320, 127).addBox(-52.0F, -18.0F, -16.0F, 12.0F, 2.0F, 32.0F, new CubeDeformation(0.0F))
            .texOffs(168, 349).addBox(-50.0F, -70.0F, -6.0F, 8.0F, 12.0F, 12.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 24.0F, 0.0F), side_pillar_r1, side_pillar_r2);

    private static final ModelPartData side_pillar_r3 = new ModelPartData("side_pillar_r3", CubeListBuilder.create()
            .texOffs(100, 323).addBox(-1.95F, -42.0F, 0.0F, 6.0F, 42.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(45.0F, -18.0F, -12.0F, -0.1745F, 0.0F, 0.0F));

    private static final ModelPartData side_pillar_r4 = new ModelPartData("side_pillar_r4", CubeListBuilder.create()
            .texOffs(100, 323).addBox(-3.0F, -42.0F, -6.0F, 6.0F, 42.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(46.0F, -18.0F, 12.0F, 0.1745F, 0.0F, 0.0F));

    private static final ModelPartData side_support2 = new ModelPartData("side_support2", CubeListBuilder.create()
            .texOffs(320, 127).addBox(40.0F, -18.0F, -16.0F, 12.0F, 2.0F, 32.0F, new CubeDeformation(0.0F))
            .texOffs(168, 349).addBox(42.0F, -70.0F, -6.0F, 8.0F, 12.0F, 12.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 24.0F, 0.0F), side_pillar_r3, side_pillar_r4);

    private static final ModelPartData middle_support = new ModelPartData("middle_support", CubeListBuilder.create()
            .texOffs(124, 349).addBox(-3.0F, -58.0F, -4.0F, 6.0F, 32.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(168, 349).addBox(-4.0F, -70.0F, -6.0F, 8.0F, 12.0F, 12.0F, new CubeDeformation(0.0F))
            .texOffs(320, 182).addBox(-7.0F, -26.0F, -7.0F, 14.0F, 2.0F, 14.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 24.0F, 0.0F));

    private static final ModelPartData tube_a = new ModelPartData("tube_a", CubeListBuilder.create()
            .texOffs(252, 363).mirror().addBox(14.0F, -2.0F, 20.0F, 4.0F, 2.0F, 12.0F, new CubeDeformation(0.0F)).mirror(false)
            .texOffs(272, 248).addBox(18.0F, -2.0F, 20.0F, 22.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(68, 371).addBox(40.0F, -2.0F, 20.0F, 8.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(376, 363).addBox(44.0F, 2.0F, 20.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F));

    private static final ModelPartData tube_a2 = new ModelPartData("tube_a2", CubeListBuilder.create()
            .texOffs(252, 363).addBox(-18.0F, -2.0F, 20.0F, 4.0F, 2.0F, 12.0F, new CubeDeformation(0.0F))
            .texOffs(272, 248).mirror().addBox(-40.0F, -2.0F, 20.0F, 22.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
            .texOffs(68, 371).mirror().addBox(-48.0F, -2.0F, 20.0F, 8.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
            .texOffs(376, 363).mirror().addBox(-48.0F, 2.0F, 20.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offset(0.0F, 0.0F, 0.0F));

    private static final ModelPartData tube_b = new ModelPartData("tube_b", CubeListBuilder.create()
            .texOffs(252, 377).addBox(44.0F, 2.0F, -24.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(268, 377).addBox(40.0F, 2.0F, -24.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F));

    private static final ModelPartData tube_b2 = new ModelPartData("tube_b2", CubeListBuilder.create()
            .texOffs(252, 377).mirror().addBox(-48.0F, 2.0F, -24.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
            .texOffs(268, 377).mirror().addBox(-44.0F, 2.0F, -24.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offset(0.0F, 0.0F, 0.0F));

    private static final ModelPartData handle = new ModelPartData("handle", CubeListBuilder.create()
            .texOffs(92, 376).addBox(-30.0F, -1.0F, -27.0F, 1.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(92, 376).addBox(-17.0F, -1.0F, -27.0F, 1.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(92, 376).mirror().addBox(29.0F, -1.0F, -27.0F, 1.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)).mirror(false)
            .texOffs(92, 376).mirror().addBox(16.0F, -1.0F, -27.0F, 1.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offset(0.0F, 0.0F, 0.0F));

    private static final ModelPartData MIDDLE = new ModelPartData("middle", CubeListBuilder.create()
            .texOffs(0, 323).addBox(-3.0F, -3.0F, 12.0F, 6.0F, 3.0F, 20.0F, new CubeDeformation(0.0F))
            .texOffs(320, 161).addBox(-10.0F, -1.0F, -33.0F, 20.0F, 1.0F, 20.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F), side_support, side_support2, middle_support, tube_a, tube_a2, tube_b, tube_b2, handle);

    private static final ModelPartData connector_r1 = new ModelPartData("connector_r1", CubeListBuilder.create()
            .texOffs(0, 371).addBox(-2.0F, -22.0F, -2.0F, 2.0F, 22.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(3.0F, -4.0F, 0.0F, 0.0F, 0.0F, -0.0436F));

    private static final ModelPartData tube_support_a = new ModelPartData("tube_support_a", CubeListBuilder.create()
            .texOffs(152, 349).addBox(0.0F, -49.0F, -3.0F, 2.0F, 45.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offset(50.0F, 0.0F, 0.0F), connector_r1);

    private static final ModelPartData connector_r2 = new ModelPartData("connector_r2", CubeListBuilder.create()
            .texOffs(12, 371).addBox(0.0F, -22.0F, -2.0F, 2.0F, 22.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-3.0F, -4.0F, 0.0F, 0.0F, 0.0F, 0.0436F));

    private static final ModelPartData tube_support_a2 = new ModelPartData("tube_support_a2", CubeListBuilder.create()
            .texOffs(152, 349).addBox(-2.0F, -49.0F, -3.0F, 2.0F, 45.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-50.0F, 0.0F, 0.0F), connector_r2);

    private static final ModelPartData tube_support_b = new ModelPartData("tube_support_b", CubeListBuilder.create()
            .texOffs(152, 349).addBox(18.0F, -49.0F, -3.0F, 2.0F, 45.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F));

    private static final ModelPartData tube_support_b2 = new ModelPartData("tube_support_b2", CubeListBuilder.create()
            .texOffs(152, 349).addBox(-20.0F, -49.0F, -3.0F, 2.0F, 45.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F));

    private static final ModelPartData tube_supports = new ModelPartData("tube_supports", CubeListBuilder.create(),
            PartPose.offset(0.0F, 0.0F, 0.0F), tube_support_a, tube_support_a2, tube_support_b, tube_support_b2);

    private static final ModelPartData panel_d_r1 = new ModelPartData("panel_d_r1", CubeListBuilder.create()
            .texOffs(264, 258).addBox(-55.0F, 39.0F, -11.0F, 110.0F, 3.0F, 22.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, 1.0472F, 0.0F, 0.0F));

    private static final ModelPartData panel_c_r1 = new ModelPartData("panel_c_r1", CubeListBuilder.create()
            .texOffs(0, 229).addBox(-55.01F, 39.0F, -15.0F, 110.0F, 3.0F, 26.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, 0.5236F, 0.0F, 0.0F));

    private static final ModelPartData panel_b_r1 = new ModelPartData("panel_b_r1", CubeListBuilder.create()
            .texOffs(0, 200).addBox(-54.99F, 39.0F, -11.0F, 110.0F, 3.0F, 26.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, -0.5236F, 0.0F, 0.0F));

    private static final ModelPartData panel_a_r1 = new ModelPartData("panel_a_r1", CubeListBuilder.create()
            .texOffs(0, 258).addBox(-55.0F, 39.0F, -11.0F, 110.0F, 3.0F, 22.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, -1.0472F, 0.0F, 0.0F));

    private static final ModelPartData panel = new ModelPartData("panel", CubeListBuilder.create(),
            PartPose.offset(0.0F, -48.0F, 0.0F), panel_d_r1, panel_c_r1, panel_b_r1, panel_a_r1);

    private static final ModelPartData support_top = new ModelPartData("support_top", CubeListBuilder.create()
            .texOffs(52, 323).addBox(-1.0F, -4.0F, -21.0F, 3.0F, 4.0F, 21.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, -32.0F, -0.5585F, 0.0F, 0.0F));

    private static final ModelPartData panel_support_a = new ModelPartData("panel_support_a", CubeListBuilder.create()
            .texOffs(238, 322).addBox(-1.01F, -6.0F, -32.0F, 3.0F, 6.0F, 35.0F, new CubeDeformation(0.0F))
            .texOffs(24, 383).addBox(-0.5F, -6.0F, -35.0F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-46.5F, 4.0F, -6.0F, -0.5585F, 0.0F, 0.0F), support_top);

    private static final ModelPartData support_top2 = new ModelPartData("support_top2", CubeListBuilder.create()
            .texOffs(0, 346).addBox(-1.0F, -4.0F, 0.0F, 3.0F, 4.0F, 21.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, 32.0F, 0.5585F, 0.0F, 0.0F));

    private static final ModelPartData panel_support_a2 = new ModelPartData("panel_support_a2", CubeListBuilder.create()
            .texOffs(314, 322).addBox(-1.01F, -6.0F, -3.0F, 3.0F, 6.0F, 35.0F, new CubeDeformation(0.0F))
            .texOffs(24, 383).addBox(-0.5F, -6.0F, 32.0F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-46.5F, 4.0F, 6.0F, 0.5585F, 0.0F, 0.0F), support_top2);

    private static final ModelPartData support_top3 = new ModelPartData("support_top3", CubeListBuilder.create()
            .texOffs(52, 323).addBox(-1.0F, -4.0F, -21.0F, 3.0F, 4.0F, 21.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, -32.0F, -0.5585F, 0.0F, 0.0F));

    private static final ModelPartData panel_support_b = new ModelPartData("panel_support_b", CubeListBuilder.create()
            .texOffs(238, 322).addBox(-0.99F, -6.0F, -32.0F, 3.0F, 6.0F, 35.0F, new CubeDeformation(0.0F))
            .texOffs(24, 383).addBox(-46.5F, -6.0F, -35.0F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(45.5F, 4.0F, -6.0F, -0.5585F, 0.0F, 0.0F), support_top3);

    private static final ModelPartData support_top4 = new ModelPartData("support_top4", CubeListBuilder.create()
            .texOffs(0, 346).addBox(-1.0F, -4.0F, 0.0F, 3.0F, 4.0F, 21.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, 32.0F, 0.5585F, 0.0F, 0.0F));

    private static final ModelPartData panel_support_b2 = new ModelPartData("panel_support_b2", CubeListBuilder.create()
            .texOffs(314, 322).addBox(-0.99F, -6.0F, -3.0F, 3.0F, 6.0F, 35.0F, new CubeDeformation(0.0F))
            .texOffs(24, 383).addBox(-46.5F, -6.0F, 32.0F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(45.5F, 4.0F, 6.0F, 0.5585F, 0.0F, 0.0F), support_top4);

    private static final ModelPartData support_top5 = new ModelPartData("support_top5", CubeListBuilder.create()
            .texOffs(52, 323).addBox(-1.0F, -4.0F, -21.0F, 3.0F, 4.0F, 21.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, -32.0F, -0.5585F, 0.0F, 0.0F));

    private static final ModelPartData panel_support_c = new ModelPartData("panel_support_c", CubeListBuilder.create()
            .texOffs(238, 322).addBox(-1.0F, -6.0F, -32.0F, 3.0F, 6.0F, 35.0F, new CubeDeformation(0.0F))
            .texOffs(24, 383).addBox(45.5F, -6.0F, -35.0F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.5F, 4.0F, -6.0F, -0.5585F, 0.0F, 0.0F), support_top5);

    private static final ModelPartData support_top6 = new ModelPartData("support_top6", CubeListBuilder.create()
            .texOffs(0, 346).addBox(-1.0F, -4.0F, 0.0F, 3.0F, 4.0F, 21.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, 32.0F, 0.5585F, 0.0F, 0.0F));

    private static final ModelPartData panel_support_c2 = new ModelPartData("panel_support_c2", CubeListBuilder.create()
            .texOffs(314, 322).addBox(-1.0F, -6.0F, -3.0F, 3.0F, 6.0F, 35.0F, new CubeDeformation(0.0F))
            .texOffs(24, 383).addBox(45.5F, -6.0F, 32.0F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.5F, 4.0F, 6.0F, 0.5585F, 0.0F, 0.0F), support_top6);

    private static final ModelPartData panel_supports = new ModelPartData("panel_supports", CubeListBuilder.create(),
            PartPose.offset(0.0F, 0.0F, 0.0F), panel_support_a, panel_support_a2, panel_support_b, panel_support_b2, panel_support_c, panel_support_c2);

    private static final ModelPartData TOP = new ModelPartData("top", CubeListBuilder.create()
            .texOffs(272, 216).addBox(-56.0F, -48.0F, -2.0F, 112.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(272, 200).addBox(-53.0F, -4.0F, -4.0F, 106.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, -40.0F, 0.0F), tube_supports, panel, panel_supports);

    public static LayerDefinition createLayerDefinition() {
        return createLayerDefinition(1024, 1024, BASE, MIDDLE, TOP);
    }

    private final RenderType RENDER_TYPE = renderType(SOLAR_HEAT_GENERATOR_TEXTURE);
    private final List<ModelPart> parts;
    private final ModelPart top;
    private final ModelPart panelA;
    private final ModelPart panelB;
    private final ModelPart panelC;
    private final ModelPart panelD;

    public ModelSolarHeatGenerator(EntityModelSet entityModelSet) {
        super(RenderType::entitySolid);
        ModelPart root = entityModelSet.bakeLayer(SOLAR_HEAT_GENERATOR_LAYER);
        parts = getRenderableParts(root, BASE, MIDDLE, TOP);
        top = TOP.getFromRoot(root);

        ModelPart panelPart = top.getChild(panel.name());
        panelA = panelPart.getChild(panel_a_r1.name());
        panelB = panelPart.getChild(panel_b_r1.name());
        panelC = panelPart.getChild(panel_c_r1.name());
        panelD = panelPart.getChild(panel_d_r1.name());
    }

    public void renderItem(@NotNull PoseStack matrix, @NotNull MultiBufferSource renderer, double angle, int light, int overlayLight, boolean hasEffect) {
        renderBlock(matrix, renderer, angle, light, overlayLight, hasEffect, true, true, true, true);
    }

    public void renderBlock(@NotNull PoseStack matrix, @NotNull MultiBufferSource renderer, double angle, int light, int overlayLight, boolean hasEffect, boolean renderPanelA, boolean renderPanelB, boolean renderPanelC, boolean renderPanelD) {
        float baseRotation = getAbsoluteRotation(angle);
        setRotation(top, baseRotation, 0F, 0F);
        setPanelVisibility(renderPanelA, renderPanelB, renderPanelC, renderPanelD);
        renderToBuffer(matrix, getVertexConsumer(renderer, RENDER_TYPE, hasEffect), light, overlayLight, 0xFFFFFFFF);
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, int light, int overlayLight, int color) {
        renderPartsToBuffer(parts, poseStack, vertexConsumer, light, overlayLight, color);
    }

    public void renderWireFrame(PoseStack matrix, VertexConsumer vertexBuilder, double angle, boolean renderPanelA, boolean renderPanelB, boolean renderPanelC, boolean renderPanelD) {
        float baseRotation = getAbsoluteRotation(angle);
        setRotation(top, baseRotation, 0F, 0F);
        setPanelVisibility(renderPanelA, renderPanelB, renderPanelC, renderPanelD);
        renderPartsAsWireFrame(parts, matrix, vertexBuilder);
    }

    private void setPanelVisibility(boolean renderPanelA, boolean renderPanelB, boolean renderPanelC, boolean renderPanelD) {
        panelA.visible = renderPanelA;
        panelB.visible = renderPanelB;
        panelC.visible = renderPanelC;
        panelD.visible = renderPanelD;
    }

    private float getAbsoluteRotation(double angle) {
        return (float) ((angle % 360) * Mth.DEG_TO_RAD);
    }
}
