package com.jerry.meklg.common.registries;

import com.jerry.mekmm.Mekmm;
import com.jerry.mekmm.api.datamaps.IMoreMachineDataMapTypes;
import com.jerry.mekmm.common.config.MoreMachineConfig;

import mekanism.api.datamaps.IMekanismDataMapTypes;
import mekanism.common.attachments.containers.chemical.ChemicalTanksBuilder;
import mekanism.common.attachments.containers.fluid.FluidTanksBuilder;
import mekanism.common.attachments.containers.heat.HeatCapacitorsBuilder;
import mekanism.common.attachments.containers.item.ItemSlotsBuilder;
import mekanism.common.attachments.containers.type.ContainerType;
import mekanism.common.block.prefab.BlockTile;
import mekanism.common.block.prefab.BlockTile.BlockTileModel;
import mekanism.common.item.block.ItemBlockTooltip;
import mekanism.common.registration.impl.BlockDeferredRegister;
import mekanism.common.registration.impl.BlockRegistryObject;
import mekanism.common.resource.BlockResourceInfo;
import mekanism.generators.common.content.blocktype.Generator;

import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.material.MapColor;

import com.jerry.meklg.common.tile.generator.TileEntityLargeGasGenerator;
import com.jerry.meklg.common.tile.generator.TileEntityLargeHeatGenerator;
import com.jerry.meklg.common.tile.generator.TileEntityLargeWindGenerator;
import com.jerry.meklg.common.tile.generator.TileEntitySolarHeatGenerator;
import org.jetbrains.annotations.NotNull;

public class LargeGeneratorBlocks {

    public static final BlockDeferredRegister LG_BLOCKS = new BlockDeferredRegister(Mekmm.MOD_ID);

    // Generator
    public static final BlockRegistryObject<@NotNull BlockTileModel<TileEntityLargeHeatGenerator, Generator<TileEntityLargeHeatGenerator>>, @NotNull ItemBlockTooltip<BlockTileModel<TileEntityLargeHeatGenerator, Generator<TileEntityLargeHeatGenerator>>>> LARGE_HEAT_GENERATOR = LG_BLOCKS.registerDetails("large_heat_generator", properties -> new BlockTileModel<>(LargeGeneratorBlockTypes.LARGE_HEAT_GENERATOR, BlockTile.defaultProperties(properties).mapColor(MapColor.METAL)))
            .forItemHolder(holder -> holder
                    .addAttachmentOnlyContainers(ContainerType.FLUID, () -> FluidTanksBuilder.builder()
                            .addBasic(MoreMachineConfig.generators.largeHeatTankCapacity, fluid -> fluid.is(FluidTags.LAVA))
                            .build())
                    .addAttachmentOnlyContainers(ContainerType.HEAT, () -> HeatCapacitorsBuilder.builder()
                            .addBasic(TileEntityLargeHeatGenerator.HEAT_CAPACITY, TileEntityLargeHeatGenerator.INVERSE_CONDUCTION_COEFFICIENT, TileEntityLargeHeatGenerator.INVERSE_INSULATION_COEFFICIENT)
                            .build())
                    .addAttachmentOnlyContainers(ContainerType.ITEM, () -> ItemSlotsBuilder.builder()
                            .addBasic(1)
                            .addEnergy()
                            .build()));

    public static final BlockRegistryObject<@NotNull BlockTileModel<TileEntityLargeGasGenerator, Generator<TileEntityLargeGasGenerator>>, @NotNull ItemBlockTooltip<BlockTileModel<TileEntityLargeGasGenerator, Generator<TileEntityLargeGasGenerator>>>> LARGE_GAS_BURNING_GENERATOR = LG_BLOCKS.registerDetails("large_gas_burning_generator", properties -> new BlockTileModel<>(LargeGeneratorBlockTypes.LARGE_GAS_BURNING_GENERATOR, BlockTile.defaultProperties(properties).mapColor(BlockResourceInfo.STEEL.getMapColor())))
            .forItemHolder(holder -> holder
                    .addAttachmentOnlyContainers(ContainerType.CHEMICAL, () -> ChemicalTanksBuilder.builder()
                            .addBasic(MoreMachineConfig.generators.LGBGTankCapacity, TileEntityLargeGasGenerator.HAS_FUEL)
                            .build())
                    .addAttachmentOnlyContainers(ContainerType.ITEM, () -> ItemSlotsBuilder.builder()
                            .addChemicalFillSlot(0)
                            .addEnergy()
                            .build()));

    public static final BlockRegistryObject<@NotNull BlockTileModel<TileEntityLargeWindGenerator, Generator<TileEntityLargeWindGenerator>>, @NotNull ItemBlockTooltip<BlockTileModel<TileEntityLargeWindGenerator, Generator<TileEntityLargeWindGenerator>>>> LARGE_WIND_GENERATOR = LG_BLOCKS.registerDetails("large_wind_generator", properties -> new BlockTileModel<>(LargeGeneratorBlockTypes.LARGE_WIND_GENERATOR, BlockTile.defaultProperties(properties).mapColor(MapColor.METAL)))
            .forItemHolder(holder -> holder.addAttachmentOnlyContainers(ContainerType.ITEM, () -> ItemSlotsBuilder.builder().addEnergy().build()));

    public static final BlockRegistryObject<@NotNull BlockTileModel<TileEntitySolarHeatGenerator, Generator<TileEntitySolarHeatGenerator>>, @NotNull ItemBlockTooltip<BlockTileModel<TileEntitySolarHeatGenerator, Generator<TileEntitySolarHeatGenerator>>>> SOLAR_HEAT_GENERATOR = LG_BLOCKS.registerDetails("solar_heat_generator", properties -> new BlockTileModel<>(LargeGeneratorBlockTypes.SOLAR_HEAT_GENERATOR, BlockTile.defaultProperties(properties).mapColor(MapColor.METAL)))
            .forItemHolder(holder -> holder
                    .addAttachmentOnlyContainers(ContainerType.CHEMICAL, () -> ChemicalTanksBuilder.builder()
                            .addBasic(TileEntitySolarHeatGenerator.MAX_GAS, chemical -> chemical.getData(IMekanismDataMapTypes.INSTANCE.cooledChemicalCoolant()) != null)
                            .addBasic(TileEntitySolarHeatGenerator.MAX_GAS, chemical -> chemical.getData(IMekanismDataMapTypes.INSTANCE.heatedChemicalCoolant()) != null)
                            .build())
                    .addAttachmentOnlyContainers(ContainerType.FLUID, () -> FluidTanksBuilder.builder()
                            .addBasic(TileEntitySolarHeatGenerator.MAX_FLUID, fluid -> IMoreMachineDataMapTypes.INSTANCE.getSolarHeatFluid(fluid.typeHolder()) != null)
                            .build())
                    .addAttachmentOnlyContainers(ContainerType.HEAT, () -> HeatCapacitorsBuilder.builder()
                            .addBasic(TileEntitySolarHeatGenerator.HEAT_CAPACITY, TileEntitySolarHeatGenerator.INVERSE_CONDUCTION_COEFFICIENT, TileEntitySolarHeatGenerator.INVERSE_INSULATION_COEFFICIENT)
                            .build())
                    .addAttachmentOnlyContainers(ContainerType.ITEM, () -> ItemSlotsBuilder.builder()
                            .addInput(item -> TileEntitySolarHeatGenerator.isValidReflector(item.toStack()))
                            .addInput(item -> TileEntitySolarHeatGenerator.isValidReflector(item.toStack()))
                            .addInput(item -> TileEntitySolarHeatGenerator.isValidReflector(item.toStack()))
                            .addInput(item -> TileEntitySolarHeatGenerator.isValidReflector(item.toStack()))
                            .addEnergy()
                            .build()));

    private LargeGeneratorBlocks() {}
}
