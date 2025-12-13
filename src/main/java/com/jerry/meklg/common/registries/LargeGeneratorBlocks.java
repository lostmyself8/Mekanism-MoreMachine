package com.jerry.meklg.common.registries;

import com.jerry.mekmm.Mekmm;
import com.jerry.mekmm.common.config.MoreMachineConfig;

import mekanism.common.attachments.containers.ContainerType;
import mekanism.common.attachments.containers.chemical.ChemicalTanksBuilder;
import mekanism.common.attachments.containers.fluid.FluidTanksBuilder;
import mekanism.common.attachments.containers.heat.HeatCapacitorsBuilder;
import mekanism.common.attachments.containers.item.ItemSlotsBuilder;
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

public class LargeGeneratorBlocks {

    public static final BlockDeferredRegister LG_BLOCKS = new BlockDeferredRegister(Mekmm.MOD_ID);

    // Generator
    public static final BlockRegistryObject<BlockTileModel<TileEntityLargeHeatGenerator, Generator<TileEntityLargeHeatGenerator>>, ItemBlockTooltip<BlockTileModel<TileEntityLargeHeatGenerator, Generator<TileEntityLargeHeatGenerator>>>> LARGE_HEAT_GENERATOR = LG_BLOCKS.registerDetails("large_heat_generator", () -> new BlockTileModel<>(LargeGeneratorBlockTypes.LARGE_HEAT_GENERATOR, properties -> properties.mapColor(MapColor.METAL)))
            .forItemHolder(holder -> holder
                    .addAttachmentOnlyContainers(ContainerType.FLUID, () -> FluidTanksBuilder.builder()
                            .addBasic(MoreMachineConfig.generators.largeHeatTankCapacity, fluid -> fluid.is(FluidTags.LAVA))
                            .build())
                    .addAttachmentOnlyContainers(ContainerType.HEAT, () -> HeatCapacitorsBuilder.builder()
                            .addBasic(TileEntityLargeHeatGenerator.HEAT_CAPACITY, TileEntityLargeHeatGenerator.INVERSE_CONDUCTION_COEFFICIENT, TileEntityLargeHeatGenerator.INVERSE_INSULATION_COEFFICIENT)
                            .build())
                    .addAttachmentOnlyContainers(ContainerType.ITEM, () -> ItemSlotsBuilder.builder()
                            .addFluidFuelSlot(0, s -> s.getBurnTime(null) != 0)
                            .addEnergy()
                            .build()));

    public static final BlockRegistryObject<BlockTileModel<TileEntityLargeGasGenerator, Generator<TileEntityLargeGasGenerator>>, ItemBlockTooltip<BlockTileModel<TileEntityLargeGasGenerator, Generator<TileEntityLargeGasGenerator>>>> LARGE_GAS_BURNING_GENERATOR = LG_BLOCKS.registerDetails("large_gas_burning_generator", () -> new BlockTileModel<>(LargeGeneratorBlockTypes.LARGE_GAS_BURNING_GENERATOR, properties -> properties.mapColor(BlockResourceInfo.STEEL.getMapColor())))
            .forItemHolder(holder -> holder
                    .addAttachmentOnlyContainers(ContainerType.CHEMICAL, () -> ChemicalTanksBuilder.builder()
                            .addBasic(MoreMachineConfig.generators.LGBGTankCapacity, TileEntityLargeGasGenerator.HAS_FUEL)
                            .build())
                    .addAttachmentOnlyContainers(ContainerType.ITEM, () -> ItemSlotsBuilder.builder()
                            .addChemicalFillSlot(0)
                            .addEnergy()
                            .build()));

    private LargeGeneratorBlocks() {}
}
