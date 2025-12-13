package com.jerry.meklg.common.registries;

import com.jerry.mekmm.common.block.attribute.MoreMachineBounding;
import com.jerry.mekmm.common.config.MoreMachineConfig;

import mekanism.api.math.MathUtils;
import mekanism.common.block.attribute.*;
import mekanism.common.lib.math.Pos3D;
import mekanism.common.util.ChemicalUtil;
import mekanism.generators.common.GeneratorsLang;
import mekanism.generators.common.content.blocktype.Generator;
import mekanism.generators.common.content.blocktype.Generator.GeneratorBuilder;
import mekanism.generators.common.registries.GeneratorsSounds;

import net.minecraft.core.particles.ParticleTypes;

import com.jerry.meklg.common.content.blocktype.LargeGeneratorBlockShapes;
import com.jerry.meklg.common.tile.generator.TileEntityLargeGasGenerator;
import com.jerry.meklg.common.tile.generator.TileEntityLargeHeatGenerator;

public class LargeGeneratorBlockTypes {

    // Heat Generator
    public static final Generator<TileEntityLargeHeatGenerator> LARGE_HEAT_GENERATOR = GeneratorBuilder
            .createGenerator(() -> LargeGeneratorTileEntityTypes.LARGE_HEAT_GENERATOR, GeneratorsLang.DESCRIPTION_HEAT_GENERATOR)
            .withGui(() -> LargeGeneratorContainerTypes.LARGE_HEAT_GENERATOR)
            .withEnergyConfig(MoreMachineConfig.storage.largeHeatGenerator)
            .withCustomShape(LargeGeneratorBlockShapes.LARGE_HEAT_GENERATOR)
            .withSound(GeneratorsSounds.HEAT_GENERATOR)
            .with(AttributeUpgradeSupport.MUFFLING_ONLY)
            .with(AttributeCustomSelectionBox.JSON)
            .with(MoreMachineBounding.FULL_JAVA_ENTITY)
            .withComputerSupport("largeHeatGenerator")
            .replace(Attributes.ACTIVE_MELT_LIGHT)
            .with(new AttributeParticleFX()
                    .add(ParticleTypes.SMOKE, rand -> new Pos3D(rand.nextFloat() * 0.6F - 0.3F, rand.nextFloat() * 6.0F / 16.0F, -0.52))
                    .add(ParticleTypes.FLAME, rand -> new Pos3D(rand.nextFloat() * 0.6F - 0.3F, rand.nextFloat() * 6.0F / 16.0F, -0.52)))
            .build();

    // Gas Burning Generator
    public static final Generator<TileEntityLargeGasGenerator> LARGE_GAS_BURNING_GENERATOR = GeneratorBuilder
            .createGenerator(() -> LargeGeneratorTileEntityTypes.LARGE_GAS_BURNING_GENERATOR, GeneratorsLang.DESCRIPTION_GAS_BURNING_GENERATOR)
            .withGui(() -> LargeGeneratorContainerTypes.LARGE_GAS_BURNING_GENERATOR)
            .withEnergyConfig(() -> MathUtils.multiplyClamped(20_480_000L, ChemicalUtil.hydrogenEnergyDensity()))
            .withCustomShape(LargeGeneratorBlockShapes.LARGE_GAS_BURNING_GENERATOR)
            .with(AttributeCustomSelectionBox.JSON)
            .withSound(GeneratorsSounds.GAS_BURNING_GENERATOR)
            .with(AttributeUpgradeSupport.MUFFLING_ONLY)
            .with(MoreMachineBounding.FULL_JAVA_ENTITY)
            .withComputerSupport("largeGasBurningGenerator")
            .replace(Attributes.ACTIVE_MELT_LIGHT)
            .build();

    private LargeGeneratorBlockTypes() {}
}
