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
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

import com.jerry.meklg.client.model.ModelLargeWindGenerator.LargeWindGeneratorRotationRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ModelLargeWindGenerator extends MekanismJavaModel<LargeWindGeneratorRotationRenderState> {

    public static final ModelLayerLocation LARGE_WIND_GENERATOR_LAYER = new ModelLayerLocation(Mekmm.rl("large_wind_generator"), "main");
    private static final Identifier LARGE_WIND_GENERATOR_TEXTURE = Mekmm.rl("render/large_wind_generator.png");

    private static final ModelPartData body_r1 = new ModelPartData("body_r1", CubeListBuilder.create()
            .texOffs(488, 484).addBox(-4.0F, -15.0F, -2.0F, 8.0F, 15.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, -4.0F, 0.0F, 0.0F, -0.5236F));

    private static final ModelPartData body_r2 = new ModelPartData("body_r2", CubeListBuilder.create()
            .texOffs(488, 484).addBox(-4.0F, -15.0F, -2.0F, 8.0F, 15.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, -4.0F, 0.0F, 0.0F, -1.0472F));

    private static final ModelPartData body_r3 = new ModelPartData("body_r3", CubeListBuilder.create()
            .texOffs(488, 484).addBox(-4.0F, -15.0F, -2.0F, 8.0F, 15.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, -4.0F, 0.0F, 0.0F, -1.5708F));

    private static final ModelPartData body_r4 = new ModelPartData("body_r4", CubeListBuilder.create()
            .texOffs(488, 484).addBox(-4.0F, -15.0F, -2.0F, 8.0F, 15.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, -4.0F, 0.0F, 0.0F, -2.0944F));

    private static final ModelPartData body_r5 = new ModelPartData("body_r5", CubeListBuilder.create()
            .texOffs(488, 484).addBox(-4.0F, -15.0F, -2.0F, 8.0F, 15.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, -4.0F, 0.0F, 0.0F, -2.618F));

    private static final ModelPartData body_r6 = new ModelPartData("body_r6", CubeListBuilder.create()
            .texOffs(488, 484).addBox(-4.0F, -15.0F, -2.0F, 8.0F, 15.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, -4.0F, 0.0F, 0.0F, -3.1416F));

    private static final ModelPartData body_r7 = new ModelPartData("body_r7", CubeListBuilder.create()
            .texOffs(488, 484).addBox(-4.0F, -15.0F, -2.0F, 8.0F, 15.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, -4.0F, 0.0F, 0.0F, 2.618F));

    private static final ModelPartData body_r8 = new ModelPartData("body_r8", CubeListBuilder.create()
            .texOffs(488, 484).addBox(-4.0F, -15.0F, -2.0F, 8.0F, 15.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, -4.0F, 0.0F, 0.0F, 2.0944F));

    private static final ModelPartData body_r9 = new ModelPartData("body_r9", CubeListBuilder.create()
            .texOffs(488, 484).addBox(-4.0F, -15.0F, -2.0F, 8.0F, 15.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, -4.0F, 0.0F, 0.0F, 1.5708F));

    private static final ModelPartData body_r10 = new ModelPartData("body_r10", CubeListBuilder.create()
            .texOffs(488, 484).addBox(-4.0F, -15.0F, -2.0F, 8.0F, 15.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, -4.0F, 0.0F, 0.0F, 1.0472F));

    private static final ModelPartData body_r11 = new ModelPartData("body_r11", CubeListBuilder.create()
            .texOffs(488, 484).addBox(-4.0F, -15.0F, -2.0F, 8.0F, 15.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, -4.0F, 0.0F, 0.0F, 0.5236F));

    // FAN_BASE_CONNECTOR
    private static final ModelPartData FAN_BASE_CONNECTOR = new ModelPartData("fan_base_connector", CubeListBuilder.create()
            .texOffs(488, 484).addBox(-4.0F, -15.0F, -6.0F, 8.0F, 15.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F), body_r1, body_r2, body_r3, body_r4, body_r5, body_r6, body_r7, body_r8, body_r9, body_r10, body_r11);

    private static final ModelPartData body_r12 = new ModelPartData("body_r12", CubeListBuilder.create()
            .texOffs(448, 265).addBox(-6.0F, -22.0F, -17.98F, 12.0F, 22.0F, 20.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, -8.0F, 0.0F, 0.0F, -0.5236F));

    private static final ModelPartData body_r13 = new ModelPartData("body_r13", CubeListBuilder.create()
            .texOffs(448, 265).addBox(-6.0F, -22.0F, -18.0F, 12.0F, 22.0F, 20.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, -8.0F, 0.0F, 0.0F, -1.0472F));

    private static final ModelPartData body_r14 = new ModelPartData("body_r14", CubeListBuilder.create()
            .texOffs(448, 265).addBox(-6.0F, -22.0F, -17.98F, 12.0F, 22.0F, 20.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, -8.0F, 0.0F, 0.0F, -1.5708F));

    private static final ModelPartData body_r15 = new ModelPartData("body_r15", CubeListBuilder.create()
            .texOffs(448, 265).addBox(-6.0F, -22.0F, -18.0F, 12.0F, 22.0F, 20.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, -8.0F, 0.0F, 0.0F, -2.0944F));

    private static final ModelPartData body_r16 = new ModelPartData("body_r16", CubeListBuilder.create()
            .texOffs(448, 265).addBox(-6.0F, -22.0F, -17.98F, 12.0F, 22.0F, 20.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, -8.0F, 0.0F, 0.0F, -2.618F));

    private static final ModelPartData body_r17 = new ModelPartData("body_r17", CubeListBuilder.create()
            .texOffs(448, 265).addBox(-6.0F, -22.0F, -18.0F, 12.0F, 22.0F, 20.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, -8.0F, 0.0F, 0.0F, -3.1416F));

    private static final ModelPartData body_r18 = new ModelPartData("body_r18", CubeListBuilder.create()
            .texOffs(448, 265).addBox(-6.0F, -22.0F, -17.98F, 12.0F, 22.0F, 20.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, -8.0F, 0.0F, 0.0F, 2.618F));

    private static final ModelPartData body_r19 = new ModelPartData("body_r19", CubeListBuilder.create()
            .texOffs(448, 265).addBox(-6.0F, -22.0F, -18.0F, 12.0F, 22.0F, 20.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, -8.0F, 0.0F, 0.0F, 2.0944F));

    private static final ModelPartData body_r20 = new ModelPartData("body_r20", CubeListBuilder.create()
            .texOffs(448, 265).addBox(-6.0F, -22.0F, -17.98F, 12.0F, 22.0F, 20.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, -8.0F, 0.0F, 0.0F, 1.5708F));

    private static final ModelPartData body_r21 = new ModelPartData("body_r21", CubeListBuilder.create()
            .texOffs(448, 265).addBox(-6.0F, -22.0F, -18.0F, 12.0F, 22.0F, 20.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, -8.0F, 0.0F, 0.0F, 1.0472F));

    private static final ModelPartData body_r22 = new ModelPartData("body_r22", CubeListBuilder.create()
            .texOffs(448, 265).addBox(-6.0F, -22.0F, -17.98F, 12.0F, 22.0F, 20.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, -8.0F, 0.0F, 0.0F, 0.5236F));

    // FAN_BODY
    private static final ModelPartData FAN_BODY = new ModelPartData("fan_body", CubeListBuilder.create()
            .texOffs(448, 265).addBox(-6.0F, -22.0F, -26.0F, 12.0F, 22.0F, 20.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F), body_r12, body_r13, body_r14, body_r15, body_r16, body_r17, body_r18, body_r19, body_r20, body_r21, body_r22);

    private static final ModelPartData body_r23 = new ModelPartData("body_r23", CubeListBuilder.create()
            .texOffs(480, 442).addBox(-5.0F, -18.0F, -16.0F, 10.0F, 18.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, -8.0F, 0.0F, 0.0F, -0.5236F));

    private static final ModelPartData body_r24 = new ModelPartData("body_r24", CubeListBuilder.create()
            .texOffs(480, 442).addBox(-5.0F, -18.0F, -15.98F, 10.0F, 18.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, -8.0F, 0.0F, 0.0F, -1.0472F));

    private static final ModelPartData body_r25 = new ModelPartData("body_r25", CubeListBuilder.create()
            .texOffs(480, 442).addBox(-5.0F, -18.0F, -16.0F, 10.0F, 18.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, -8.0F, 0.0F, 0.0F, -1.5708F));

    private static final ModelPartData body_r26 = new ModelPartData("body_r26", CubeListBuilder.create()
            .texOffs(480, 442).addBox(-5.0F, -18.0F, -15.98F, 10.0F, 18.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, -8.0F, 0.0F, 0.0F, -2.0944F));

    private static final ModelPartData body_r27 = new ModelPartData("body_r27", CubeListBuilder.create()
            .texOffs(480, 442).addBox(-5.0F, -18.0F, -16.0F, 10.0F, 18.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, -8.0F, 0.0F, 0.0F, -2.618F));

    private static final ModelPartData body_r28 = new ModelPartData("body_r28", CubeListBuilder.create()
            .texOffs(480, 442).addBox(-5.0F, -18.0F, -15.98F, 10.0F, 18.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, -8.0F, 0.0F, 0.0F, -3.1416F));

    private static final ModelPartData body_r29 = new ModelPartData("body_r29", CubeListBuilder.create()
            .texOffs(480, 442).addBox(-5.0F, -18.0F, -16.0F, 10.0F, 18.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, -8.0F, 0.0F, 0.0F, 2.618F));

    private static final ModelPartData body_r30 = new ModelPartData("body_r30", CubeListBuilder.create()
            .texOffs(480, 442).addBox(-5.0F, -18.0F, -15.98F, 10.0F, 18.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, -8.0F, 0.0F, 0.0F, 2.0944F));

    private static final ModelPartData body_r31 = new ModelPartData("body_r31", CubeListBuilder.create()
            .texOffs(480, 442).addBox(-5.0F, -18.0F, -16.0F, 10.0F, 18.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, -8.0F, 0.0F, 0.0F, 1.5708F));

    private static final ModelPartData body_r32 = new ModelPartData("body_r32", CubeListBuilder.create()
            .texOffs(480, 442).addBox(-5.0F, -18.0F, -15.98F, 10.0F, 18.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, -8.0F, 0.0F, 0.0F, 1.0472F));

    private static final ModelPartData body_r33 = new ModelPartData("body_r33", CubeListBuilder.create()
            .texOffs(480, 442).addBox(-5.0F, -18.0F, -16.0F, 10.0F, 18.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, -8.0F, 0.0F, 0.0F, 0.5236F));

    // FAN_END
    private static final ModelPartData FAN_END = new ModelPartData("fan_end", CubeListBuilder.create()
            .texOffs(480, 442).addBox(-5.0F, -18.0F, -23.98F, 10.0F, 18.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, -5.0F), body_r23, body_r24, body_r25, body_r26, body_r27, body_r28, body_r29, body_r30, body_r31, body_r32, body_r33);

    private static final ModelPartData fan_b_r1 = new ModelPartData("fan_b_r1", CubeListBuilder.create()
            .texOffs(176, 424).addBox(0.0F, -246.0F, -3.65F, 15.0F, 240.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-16.0F, -14.0F, -15.6F, 0.0F, 0.0F, 0.0305F));

    private static final ModelPartData base_r1 = new ModelPartData("base_r1", CubeListBuilder.create()
            .texOffs(448, 307).addBox(-12.5F, -6.0F, -6.0F, 28.0F, 11.0F, 10.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-4.0F, -18.0F, -14.6F, 0.0F, 0.0F, -0.2182F));

    private static final ModelPartData FAN_1 = new ModelPartData("fan1", CubeListBuilder.create()
            .texOffs(128, 424).addBox(-8.0F, -270.0F, -19.6F, 16.0F, 250.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F), fan_b_r1, base_r1);

    private static final ModelPartData fan_b_r2 = new ModelPartData("fan_b_r2", CubeListBuilder.create()
            .texOffs(176, 424).addBox(0.0F, -246.0F, -3.65F, 15.0F, 240.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-16.0F, -14.0F, -15.6F, 0.0F, 0.0F, 0.0305F));

    private static final ModelPartData base_r2 = new ModelPartData("base_r2", CubeListBuilder.create()
            .texOffs(448, 307).addBox(-12.5F, -6.0F, -6.0F, 28.0F, 11.0F, 10.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-4.0F, -18.0F, -14.6F, 0.0F, 0.0F, -0.2182F));

    private static final ModelPartData FAN_2 = new ModelPartData("fan2", CubeListBuilder.create()
            .texOffs(128, 424).addBox(-8.0F, -270.0F, -19.6F, 16.0F, 250.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 2.0944F), fan_b_r2, base_r2);

    private static final ModelPartData fan_b_r3 = new ModelPartData("fan_b_r3", CubeListBuilder.create()
            .texOffs(176, 424).addBox(0.0F, -246.0F, -3.65F, 15.0F, 240.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-16.0F, -14.0F, -15.6F, 0.0F, 0.0F, 0.0305F));

    private static final ModelPartData base_r3 = new ModelPartData("base_r3", CubeListBuilder.create()
            .texOffs(448, 307).addBox(-12.5F, -6.0F, -6.0F, 28.0F, 11.0F, 10.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-4.0F, -18.0F, -14.6F, 0.0F, 0.0F, -0.2182F));

    private static final ModelPartData FAN_3 = new ModelPartData("fan3", CubeListBuilder.create()
            .texOffs(128, 424).addBox(-8.0F, -270.0F, -19.6F, 16.0F, 250.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -2.0944F), fan_b_r3, base_r3);

    // FAN_MAIN
    private static final ModelPartData FAN_MAIN = new ModelPartData("fan_main", CubeListBuilder.create(),
            PartPose.offset(0.0F, 0.0F, 0.0F), FAN_1, FAN_2, FAN_3);

    // FAN
    private static final ModelPartData FAN = new ModelPartData("fan", CubeListBuilder.create()
            .texOffs(256, 480).addBox(-10.0F, -10.0F, -29.02F, 20.0F, 20.0F, 0.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, -517.0F, -30.0F), FAN_BASE_CONNECTOR, FAN_BODY, FAN_END, FAN_MAIN);

    private static final ModelPartData pillar1_r1 = new ModelPartData("pillar1_r1", CubeListBuilder.create()
            .texOffs(0, 0).addBox(0.0F, -480.0F, -32.0F, 32.0F, 480.0F, 32.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-29.0F, -32.0F, 29.0F, 0.0262F, 0.0F, 0.0262F));

    private static final ModelPartData PILLAR_1 = new ModelPartData("pillar1", CubeListBuilder.create(),
            PartPose.offset(0.0F, 32.0F, 0.0F), pillar1_r1);

    private static final ModelPartData pillar2_r1 = new ModelPartData("pillar2_r1", CubeListBuilder.create()
            .texOffs(0, 0).addBox(0.04F, -480.0F, -32.03F, 32.0F, 480.0F, 32.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-29.0F, -32.0F, 29.0F, 0.0262F, 0.0F, 0.0262F));

    private static final ModelPartData PILLAR_2 = new ModelPartData("pillar2", CubeListBuilder.create(),
            PartPose.offsetAndRotation(0.0F, 32.0F, 0.0F, 0.0F, -1.5708F, 0.0F), pillar2_r1);

    private static final ModelPartData pillar3_r1 = new ModelPartData("pillar3_r1", CubeListBuilder.create()
            .texOffs(0, 0).addBox(0.0F, -480.0F, -32.0F, 32.0F, 480.0F, 32.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-29.0F, -32.0F, 29.0F, 0.0262F, 0.0F, 0.0262F));

    private static final ModelPartData PILLAR_3 = new ModelPartData("pillar3", CubeListBuilder.create(),
            PartPose.offsetAndRotation(0.0F, 32.0F, 0.0F, 0.0F, 3.1416F, 0.0F), pillar3_r1);

    private static final ModelPartData pillar4_r1 = new ModelPartData("pillar4_r1", CubeListBuilder.create()
            .texOffs(0, 0).addBox(0.03F, -480.0F, -32.02F, 32.0F, 480.0F, 32.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-29.0F, -32.0F, 29.0F, 0.0262F, 0.0F, 0.0262F));

    private static final ModelPartData PILLAR_4 = new ModelPartData("pillar4", CubeListBuilder.create(),
            PartPose.offsetAndRotation(0.0F, 32.0F, 0.0F, 0.0F, 1.5708F, 0.0F), pillar4_r1);

    // BODY
    private static final ModelPartData BODY = new ModelPartData("body", CubeListBuilder.create()
            .texOffs(128, 342).addBox(-37.0F, 0.0F, -37.0F, 74.0F, 8.0F, 74.0F, new CubeDeformation(0.0F))
            .texOffs(420, 465).addBox(-8.0F, -32.0F, -30.0F, 16.0F, 32.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(478, 496).addBox(6.0F, -21.0F, -31.0F, 1.0F, 10.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(478, 496).addBox(-7.0F, -21.0F, -31.0F, 1.0F, 10.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, -8.0F, 0.0F), PILLAR_1, PILLAR_2, PILLAR_3, PILLAR_4);

    private static final ModelPartData TOP_TOP_R_1 = new ModelPartData("top_top_r1", CubeListBuilder.create()
            .texOffs(384, 443).addBox(-16.0F, 0.0F, 0.0F, 32.0F, 6.0F, 16.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, -66.0F, -4.0F, 0.3054F, 0.0F, 0.0F));

    private static final ModelPartData TOP_TOP_R_2 = new ModelPartData("top_top_r2", CubeListBuilder.create()
            .texOffs(384, 443).addBox(-16.0F, 0.0F, 0.0F, 32.0F, 6.0F, 16.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, -66.0F, -24.0F, 0.3054F, 0.0F, 0.0F));

    private static final ModelPartData BACK_FIN_R_1 = new ModelPartData("back_fin_r1", CubeListBuilder.create()
            .texOffs(296, 480).addBox(-1.98F, 0.0F, 0.0F, 4.0F, 17.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, -76.0F, 57.0F, -0.3927F, 0.0F, 0.0F));

    private static final ModelPartData TOP_BOTTOM_CONNECTOR_R_1 = new ModelPartData("top_bottom_connector_r1", CubeListBuilder.create()
            .texOffs(404, 176).addBox(-3.0F, -16.0F, -1.0F, 6.0F, 16.0F, 48.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 13.0F, 17.0F, 0.3927F, 0.0F, 0.0F));

    private static final ModelPartData base_r4 = new ModelPartData("base_r4", CubeListBuilder.create()
            .texOffs(448, 240).addBox(0.0F, -5.0F, -10.0F, 24.0F, 5.0F, 20.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.7854F, 0.0F));

    private static final ModelPartData base_r5 = new ModelPartData("base_r5", CubeListBuilder.create()
            .texOffs(448, 240).addBox(0.0F, -5.02F, -10.0F, 24.0F, 5.0F, 20.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

    private static final ModelPartData base_r6 = new ModelPartData("base_r6", CubeListBuilder.create()
            .texOffs(448, 240).addBox(0.0F, -5.0F, -10.0F, 24.0F, 5.0F, 20.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 2.3562F, 0.0F));

    private static final ModelPartData base_r7 = new ModelPartData("base_r7", CubeListBuilder.create()
            .texOffs(448, 240).addBox(0.0F, -5.02F, -10.0F, 24.0F, 5.0F, 20.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

    private static final ModelPartData base_r8 = new ModelPartData("base_r8", CubeListBuilder.create()
            .texOffs(448, 240).addBox(0.0F, -5.0F, -10.0F, 24.0F, 5.0F, 20.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -2.3562F, 0.0F));

    private static final ModelPartData base_r9 = new ModelPartData("base_r9", CubeListBuilder.create()
            .texOffs(448, 240).addBox(0.0F, -5.02F, -10.0F, 24.0F, 5.0F, 20.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

    private static final ModelPartData base_r10 = new ModelPartData("base_r10", CubeListBuilder.create()
            .texOffs(448, 240).addBox(0.0F, -5.0F, -10.0F, 24.0F, 5.0F, 20.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

    private static final ModelPartData TOP_BASE = new ModelPartData("top_base", CubeListBuilder.create()
            .texOffs(448, 240).addBox(0.0F, -5.02F, -10.0F, 24.0F, 5.0F, 20.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, -0.3927F, 0.0F), base_r4, base_r5, base_r6, base_r7, base_r8, base_r9, base_r10);

    private static final ModelPartData base_r11 = new ModelPartData("base_r11", CubeListBuilder.create()
            .texOffs(222, 480).addBox(-7.0F, -26.0F, -2.0F, 14.0F, 26.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, 0.0F, 0.0F, -0.5236F));

    private static final ModelPartData base_r12 = new ModelPartData("base_r12", CubeListBuilder.create()
            .texOffs(222, 480).addBox(-7.0F, -26.0F, -1.98F, 14.0F, 26.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, 0.0F, 0.0F, -1.0472F));

    private static final ModelPartData base_r13 = new ModelPartData("base_r13", CubeListBuilder.create()
            .texOffs(222, 480).addBox(-7.0F, -26.0F, -2.0F, 14.0F, 26.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, 0.0F, 0.0F, -1.5708F));

    private static final ModelPartData base_r14 = new ModelPartData("base_r14", CubeListBuilder.create()
            .texOffs(222, 480).addBox(-7.0F, -26.0F, -1.98F, 14.0F, 26.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, 0.0F, 0.0F, -2.0944F));

    private static final ModelPartData base_r15 = new ModelPartData("base_r15", CubeListBuilder.create()
            .texOffs(222, 480).addBox(-7.0F, -26.0F, -2.0F, 14.0F, 26.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, 0.0F, 0.0F, -2.618F));

    private static final ModelPartData base_r16 = new ModelPartData("base_r16", CubeListBuilder.create()
            .texOffs(222, 480).addBox(-7.0F, -26.0F, -1.98F, 14.0F, 26.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, 0.0F, 0.0F, -3.1416F));

    private static final ModelPartData base_r17 = new ModelPartData("base_r17", CubeListBuilder.create()
            .texOffs(222, 480).addBox(-7.0F, -26.0F, -2.0F, 14.0F, 26.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, 0.0F, 0.0F, 2.618F));

    private static final ModelPartData base_r18 = new ModelPartData("base_r18", CubeListBuilder.create()
            .texOffs(222, 480).addBox(-7.0F, -26.0F, -1.98F, 14.0F, 26.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, 0.0F, 0.0F, 2.0944F));

    private static final ModelPartData base_r19 = new ModelPartData("base_r19", CubeListBuilder.create()
            .texOffs(222, 480).addBox(-7.0F, -26.0F, -2.0F, 14.0F, 26.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, 0.0F, 0.0F, 1.5708F));

    private static final ModelPartData base_r20 = new ModelPartData("base_r20", CubeListBuilder.create()
            .texOffs(222, 480).addBox(-7.0F, -26.0F, -1.98F, 14.0F, 26.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, 0.0F, 0.0F, 1.0472F));

    private static final ModelPartData base_r21 = new ModelPartData("base_r21", CubeListBuilder.create()
            .texOffs(222, 480).addBox(-7.0F, -26.0F, -2.0F, 14.0F, 26.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, 0.0F, 0.0F, 0.5236F));

    private static final ModelPartData FRONT_BASE = new ModelPartData("front_base", CubeListBuilder.create()
            .texOffs(222, 480).addBox(-7.0F, -26.0F, -2.98F, 14.0F, 26.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, -35.0F, -30.0F), base_r11, base_r12, base_r13, base_r14, base_r15, base_r16, base_r17, base_r18, base_r19, base_r20, base_r21);

    private static final ModelPartData top_back2_r1 = new ModelPartData("top_back2_r1", CubeListBuilder.create()
            .texOffs(222, 424).addBox(-16.0F, -40.0F, 0.0F, 32.0F, 40.0F, 16.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, -3.0F, 16.0F, 0.1745F, 0.0F, 0.0F));

    private static final ModelPartData TOP_BACK = new ModelPartData("top_back", CubeListBuilder.create()
            .texOffs(404, 104).addBox(-24.0F, -56.0F, 0.0F, 48.0F, 56.0F, 16.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, -4.0F, 50.0F, 0.0873F, 0.0F, 0.0F), top_back2_r1);

    // TOP
    private static final ModelPartData TOP = new ModelPartData("top", CubeListBuilder.create()
            .texOffs(128, 104).addBox(-29.0F, -66.0F, -30.0F, 58.0F, 62.0F, 80.0F, new CubeDeformation(0.0F))
            .texOffs(384, 465).addBox(-2.0F, -76.0F, 57.0F, 4.0F, 28.0F, 14.0F, new CubeDeformation(0.0F))
            .texOffs(318, 443).addBox(29.0F, -52.0F, -23.0F, 1.0F, 32.0F, 32.0F, new CubeDeformation(0.0F))
            .texOffs(318, 443).mirror().addBox(-30.0F, -52.0F, -23.0F, 1.0F, 32.0F, 32.0F, new CubeDeformation(0.0F)).mirror(false)
            .texOffs(500, 463).addBox(15.0F, -67.0F, 27.0F, 1.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(500, 463).addBox(2.0F, -67.0F, 27.0F, 1.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(500, 463).addBox(-3.0F, -67.0F, 27.0F, 1.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(500, 463).addBox(-16.0F, -67.0F, 27.0F, 1.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, -482.0F, 0.0F), TOP_TOP_R_1, TOP_TOP_R_2, BACK_FIN_R_1, TOP_BOTTOM_CONNECTOR_R_1, TOP_BASE, FRONT_BASE, TOP_BACK);

    private static final ModelPartData base_connector_r1 = new ModelPartData("base_connector_r1", CubeListBuilder.create()
            .texOffs(424, 373).addBox(-23.0F, -8.0F, 0.0F, 46.0F, 11.0F, 12.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, -8.0F, -48.0F, 0.3927F, 0.0F, 0.0F));

    private static final ModelPartData BASE_CONNECTOR_3 = new ModelPartData("base_connector3", CubeListBuilder.create()
            .texOffs(424, 396).addBox(-24.0F, -16.0F, -55.0F, 48.0F, 16.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F), base_connector_r1);

    private static final ModelPartData base_connector_r2 = new ModelPartData("base_connector_r2", CubeListBuilder.create()
            .texOffs(424, 373).addBox(-23.0F, -8.0F, 0.0F, 46.0F, 11.0F, 12.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, -8.0F, -48.0F, 0.3927F, 0.0F, 0.0F));

    private static final ModelPartData BASE_CONNECTOR_2 = new ModelPartData("base_connector2", CubeListBuilder.create()
            .texOffs(424, 396).addBox(-24.0F, -16.0F, -55.0F, 48.0F, 16.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F), base_connector_r2);

    private static final ModelPartData base_connector_r3 = new ModelPartData("base_connector_r3", CubeListBuilder.create()
            .texOffs(424, 373).addBox(-23.0F, -8.0F, 0.0F, 46.0F, 11.0F, 12.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, -8.0F, -48.0F, 0.3927F, 0.0F, 0.0F));

    private static final ModelPartData BASE_CONNECTOR = new ModelPartData("base_connector", CubeListBuilder.create()
            .texOffs(424, 396).addBox(-24.0F, -16.0F, -55.0F, 48.0F, 16.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F), base_connector_r3);

    private static final ModelPartData ports_1 = new ModelPartData("ports_1", CubeListBuilder.create()
            .texOffs(456, 496).addBox(-5.0F, -13.0F, -56.0F, 10.0F, 10.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(256, 500).addBox(-20.0F, -12.0F, -56.0F, 8.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(256, 500).addBox(12.0F, -12.0F, -56.0F, 8.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F));

    private static final ModelPartData ports_2 = new ModelPartData("ports_2", CubeListBuilder.create()
            .texOffs(456, 496).addBox(-5.0F, -13.0F, 55.0F, 10.0F, 10.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(256, 500).addBox(-20.0F, -12.0F, 55.0F, 8.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(256, 500).addBox(12.0F, -12.0F, 55.0F, 8.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

    private static final ModelPartData ports_3 = new ModelPartData("ports_3", CubeListBuilder.create()
            .texOffs(456, 496).addBox(-5.0F, -13.0F, 55.0F, 10.0F, 10.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(256, 500).addBox(-20.0F, -12.0F, 55.0F, 8.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(256, 500).addBox(12.0F, -12.0F, 55.0F, 8.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

    private static final ModelPartData ports_4 = new ModelPartData("ports_4", CubeListBuilder.create()
            .texOffs(256, 500).addBox(-20.0F, -12.0F, -56.0F, 8.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(256, 500).addBox(12.0F, -12.0F, -56.0F, 8.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -3.1416F, 0.0F));

    private static final ModelPartData PORTS = new ModelPartData("ports", CubeListBuilder.create(),
            PartPose.offset(0.0F, 0.0F, 0.0F), ports_1, ports_2, ports_3, ports_4);

    private static final ModelPartData screen3_r1 = new ModelPartData("screen3_r1", CubeListBuilder.create()
            .texOffs(456, 484).addBox(0.5F, -10.0F, 1.0F, 14.0F, 10.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(448, 335).addBox(0.0F, -7.0F, 0.0F, 12.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(8.5F, -18.0F, 45.5F, 0.0F, -0.3927F, 0.0F));

    private static final ModelPartData screen1_r1 = new ModelPartData("screen1_r1", CubeListBuilder.create()
            .texOffs(456, 484).addBox(-14.5F, -10.0F, 1.0F, 14.0F, 10.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(424, 419).addBox(-11.0F, -7.0F, 0.0F, 11.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-8.5F, -18.0F, 45.5F, 0.0F, 0.3927F, 0.0F));

    private static final ModelPartData base_connector_r4 = new ModelPartData("base_connector_r4", CubeListBuilder.create()
            .texOffs(424, 373).addBox(-23.0F, -8.0F, -12.0F, 46.0F, 11.0F, 12.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, -8.0F, 48.0F, -0.3927F, 0.0F, 0.0F));

    private static final ModelPartData keyboard_r1 = new ModelPartData("keyboard_r1", CubeListBuilder.create()
            .texOffs(448, 328).addBox(-7.0F, -3.2F, -5.0F, 14.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, -12.0F, 51.0F, -0.3927F, 0.0F, 0.0F));

    // CONTROLLER
    private static final ModelPartData CONTROLLER = new ModelPartData("controller", CubeListBuilder.create()
            .texOffs(474, 335).addBox(-2.5F, -21.0F, 45.5F, 5.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(404, 240).addBox(-8.5F, -25.0F, 45.5F, 17.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(448, 419).addBox(-24.0F, -16.0F, 48.0F, 16.0F, 16.0F, 7.0F, new CubeDeformation(0.0F))
            .texOffs(448, 419).mirror().addBox(8.0F, -16.0F, 48.0F, 16.0F, 16.0F, 7.0F, new CubeDeformation(0.0F)).mirror(false)
            .texOffs(456, 465).addBox(-8.0F, -13.0F, 48.0F, 16.0F, 13.0F, 6.0F, new CubeDeformation(0.0F))
            .texOffs(424, 342).addBox(-24.0F, -32.0F, 37.0F, 48.0F, 24.0F, 7.0F, new CubeDeformation(0.0F))
            .texOffs(318, 424).addBox(-24.0F, -34.0F, 37.0F, 48.0F, 2.0F, 17.0F, new CubeDeformation(0.0F))
            .texOffs(456, 484).addBox(-7.0F, -28.0F, 46.5F, 14.0F, 10.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F), screen3_r1, screen1_r1, base_connector_r4, keyboard_r1);

    // BASE
    private static final ModelPartData BASE = new ModelPartData("base", CubeListBuilder.create()
            .texOffs(128, 0).addBox(-48.0F, -8.0F, -48.0F, 96.0F, 8.0F, 96.0F, new CubeDeformation(0.0F))
            .texOffs(128, 246).addBox(-40.0F, -24.0F, -40.0F, 80.0F, 16.0F, 80.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 24.0F, 0.0F), BASE_CONNECTOR_3, BASE_CONNECTOR_2, BASE_CONNECTOR, PORTS, CONTROLLER);

    public static LayerDefinition createLayerDefinition() {
        return createLayerDefinition(1024, 1024, FAN, BODY, TOP, BASE);
    }

    private final RenderType RENDER_TYPE = RenderTypes.entitySolid(LARGE_WIND_GENERATOR_TEXTURE);
    private final List<ModelPart> parts;
    private final ModelPart fans;

    public ModelLargeWindGenerator(EntityModelSet entityModelSet) {
        super(entityModelSet.bakeLayer(LARGE_WIND_GENERATOR_LAYER));
        parts = getRenderableParts(root, FAN, BODY, TOP, BASE);
        fans = FAN.getFromRoot(root);
    }

    @Override
    public void collect(LargeWindGeneratorRotationRenderState state, @NotNull PoseStack poseStack, @NotNull SubmitNodeCollector submitNodeCollector, int light, int overlayLight, boolean hasEffect) {
        setupAnim(state);
        // 不直接使用allParts，会导致多渲染一份子模块
        collectParts(parts, poseStack, RENDER_TYPE, submitNodeCollector, light, overlayLight, -1, null, hasEffect);
    }

    @Override
    public void setupAnim(LargeWindGeneratorRotationRenderState state) {
        super.setupAnim(state);
        fans.setRotation(0F, 0F, getAbsoluteRotation(state.angle));
    }

    public void renderWireFrame(PoseStack matrix, VertexConsumer vertexBuilder, LargeWindGeneratorRotationRenderState state, boolean isHighContrast) {
        setupAnim(state);
        // 不直接使用root().getAllParts()，会导致多渲染一份子模块
        renderPartsAsWireFrame(parts, matrix, vertexBuilder, isHighContrast);
    }

    private float getAbsoluteRotation(float angle) {
        return (angle % 360) * Mth.DEG_TO_RAD;
    }

    public static class LargeWindGeneratorRotationRenderState {

        public float angle;

        public LargeWindGeneratorRotationRenderState(float angle) {
            this.angle = angle;
        }
    }
}
