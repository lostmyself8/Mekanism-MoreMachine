package com.jerry.meklg.common.registries;

import com.jerry.mekmm.common.block.attribute.MoreMachineBounding;
import com.jerry.mekmm.common.config.MoreMachineConfig;

import mekanism.api.Upgrade;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.block.attribute.AttributeCustomSelectionBox;
import mekanism.common.block.attribute.AttributeParticleFX;
import mekanism.common.block.attribute.Attributes;
import mekanism.common.config.MekanismConfig;
import mekanism.common.lib.math.Pos3D;
import mekanism.generators.common.GeneratorsLang;
import mekanism.generators.common.content.blocktype.Generator;
import mekanism.generators.common.content.blocktype.Generator.GeneratorBuilder;
import mekanism.generators.common.registries.GeneratorsSounds;

import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;

import com.jerry.meklg.common.content.blocktype.LargeGeneratorBlockShapes;
import com.jerry.meklg.common.tile.TileEntityLargeGasBurningGenerator;
import com.jerry.meklg.common.tile.TileEntityLargeHeatGenerator;
import com.jerry.meklg.common.tile.TileEntityLargeWindGenerator;

import java.util.EnumSet;

public class LargeGeneratorsBlockTypes {

    private LargeGeneratorsBlockTypes() {}

    // Heat Generator
    public static final Generator<TileEntityLargeHeatGenerator> LARGE_HEAT_GENERATOR = GeneratorBuilder
            .createGenerator(() -> LargeGeneratorsTileEntityTypes.LARGE_HEAT_GENERATOR, GeneratorsLang.DESCRIPTION_HEAT_GENERATOR)
            .withGui(() -> LargeGeneratorsContainerTypes.LARGE_HEAT_GENERATOR)
            .withEnergyConfig(MoreMachineConfig.storage.largeHeatGenerator)
            .withCustomShape(LargeGeneratorBlockShapes.LARGE_HEAT_GENERATOR)
            .withSound(GeneratorsSounds.HEAT_GENERATOR)
            .withSupportedUpgrades(EnumSet.of(Upgrade.MUFFLING))
            .withBounding(MoreMachineBounding.FULL_JAVA_ENTITY)
            .withComputerSupport("largeHeatGenerator")
            .replace(Attributes.ACTIVE_MELT_LIGHT)
            .with(new AttributeParticleFX()
                    .add(ParticleTypes.SMOKE, rand -> new Pos3D(rand.nextFloat() * 0.6F - 0.3F, rand.nextFloat() * 6.0F / 16.0F, -0.52))
                    .add(ParticleTypes.FLAME, rand -> new Pos3D(rand.nextFloat() * 0.6F - 0.3F, rand.nextFloat() * 6.0F / 16.0F, -0.52)))
            .build();

    // Gas Burning Generator
    public static final Generator<TileEntityLargeGasBurningGenerator> LARGE_GAS_BURNING_GENERATOR = GeneratorBuilder
            .createGenerator(() -> LargeGeneratorsTileEntityTypes.LARGE_GAS_BURNING_GENERATOR, GeneratorsLang.DESCRIPTION_GAS_BURNING_GENERATOR)
            .withGui(() -> LargeGeneratorsContainerTypes.LARGE_GAS_BURNING_GENERATOR)
            .withEnergyConfig(() -> MekanismConfig.general.FROM_H2.get().multiply(20_480_000L))
            .withCustomShape(LargeGeneratorBlockShapes.LARGE_GAS_BURNING_GENERATOR)
            .with(AttributeCustomSelectionBox.JSON)
            .withSound(GeneratorsSounds.GAS_BURNING_GENERATOR)
            .withSupportedUpgrades(EnumSet.of(Upgrade.MUFFLING))
            .withBounding(MoreMachineBounding.FULL_JAVA_ENTITY)
            .withComputerSupport("largeGasBurningGenerator")
            .replace(Attributes.ACTIVE_MELT_LIGHT)
            .build();

    // Wind Generator
    public static final Generator<TileEntityLargeWindGenerator> LARGE_WIND_GENERATOR = GeneratorBuilder
            .createGenerator(() -> LargeGeneratorsTileEntityTypes.LARGE_WIND_GENERATOR, GeneratorsLang.DESCRIPTION_WIND_GENERATOR)
            .withGui(() -> LargeGeneratorsContainerTypes.LARGE_WIND_GENERATOR)
            .withEnergyConfig(MoreMachineConfig.storage.largeWindGenerator)
            .withCustomShape(LargeGeneratorBlockShapes.LARGE_WIND_GENERATOR)
            .with(AttributeCustomSelectionBox.JAVA)
            .withSound(GeneratorsSounds.WIND_GENERATOR)
            .withSupportedUpgrades(EnumSet.of(Upgrade.MUFFLING))
            .withBounding((pos, state, builder) -> {
                Direction facing = Attribute.getFacing(state);
                if (facing == null) return;
                // 获取垂直于朝向的水平轴
                Vec3i axis = facing.getClockWise().getNormal();
                // 朝向的前方
                Vec3i front = facing.getOpposite().getNormal();
                // 底座
                for (int x = -3; x <= 3; x++) {
                    for (int z = -3; z <= 3; z++) {
                        if (x != 0 || z != 0) {
                            builder.add(pos.offset(x, 0, z));
                        }
                    }
                }
                // 底座上一层
                for (int x = -3; x <= 3; x++) {
                    for (int z = -3; z <= 3; z++) {
                        if (x == -3 || x == 3 || z == -3 || z == 3) {
                            if ((x == -3 || x == -2 || x == 2 || x == 3) &&
                                    (z == -3 || z == -2 || z == 2 || z == 3)) {
                                continue;
                            }
                        }
                        builder.add(pos.offset(x, 1, z));
                    }
                }
                // 柱子一层
                for (int i = -1; i <= 1; i++) {
                    builder.add(pos.offset(front.getX() * 3 + axis.getX() * i, 2, front.getZ() * 3 + axis.getZ() * i));
                }
                // 柱子二到十三层
                for (int x = -2; x <= 2; x++) {
                    for (int y = 2; y <= 13; y++) {
                        for (int z = -2; z <= 2; z++) {
                            builder.add(pos.offset(x, y, z));
                        }
                    }
                }
                // 柱子十四到三十层
                for (int x = -1; x <= 1; x++) {
                    for (int y = 14; y <= 30; y++) {
                        for (int z = -1; z <= 1; z++) {
                            builder.add(pos.offset(x, y, z));
                        }
                    }
                }
                // 三十一层（朝向前方延伸，前方多一格，后方少一格）
                int fx = front.getX(), fz = front.getZ();
                int ax = axis.getX(), az = axis.getZ();
                for (int a = -2; a <= 2; a++) {       // 侧轴 -2~2
                    for (int f = -2; f <= 4; f++) {   // 前后轴 -2(后)~4(前)
                        if (f == 4 && (a == 2 || a == -2)) continue;
                        builder.add(pos.offset(fx * f + ax * a, 31, fz * f + az * a));
                    }
                }
                // 三十二到三十五层
                for (int a = -2; a <= 2; a++) {
                    for (int y = 32; y <= 35; y++) {
                        for (int f = -3; f <= 4; f++) {
                            if (a == -2 || a == 2 || f == -3 || f == 4) {
                                if ((a == -2 || a == 2) && (f == -3 || f == 4)) continue;
                            }
                            builder.add(pos.offset(fx * f + ax * a, y, fz * f + az * a));
                        }
                    }
                }
                // 三十二到三十四层，后方突出部分
                for (int i = -1; i <= 1; i++) {
                    for (int y = 32; y <= 34; y++) {
                        builder.add(pos.offset(fx * 5 + ax * i, y, fz * 5 + az * i));
                    }
                }
                // 三十六层，后方两格
                builder.add(pos.offset(fx * 3, 36, fz * 3));
                builder.add(pos.offset(fx * 4, 36, fz * 4));
                // 三十六层中心 3x3
                for (int x = -1; x <= 1; x++) {
                    for (int z = -1; z <= 1; z++) {
                        builder.add(pos.offset(x, 36, z));
                    }
                }
            })
            .withComputerSupport("largeWindGenerator")
            .build();
}
