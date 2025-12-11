package com.jerry.meklm.common.registries;

import com.jerry.meklm.api.tier.ILargeChemicalTankTier;
import com.jerry.meklm.common.content.blocktype.LargeMachineBlockShapes;
import com.jerry.meklm.common.tier.MaxChemicalTankTier;
import com.jerry.meklm.common.tier.MidChemicalTankTier;
import com.jerry.meklm.common.tile.TileEntityMaxChemicalTank;
import com.jerry.meklm.common.tile.TileEntityMidChemicalTank;
import com.jerry.meklm.common.tile.generator.TileEntityLargeGasGenerator;
import com.jerry.meklm.common.tile.generator.TileEntityLargeHeatGenerator;
import com.jerry.meklm.common.tile.machine.TileEntityLargeChemicalInfuser;
import com.jerry.meklm.common.tile.machine.TileEntityLargeElectrolyticSeparator;
import com.jerry.meklm.common.tile.machine.TileEntityLargeRotaryCondensentrator;
import com.jerry.meklm.common.tile.machine.TileEntityLargeSolarNeutronActivator;
import com.jerry.meklm.common.tile.prefab.TileEntityLargeChemicalTank;

import com.jerry.mekmm.common.block.attribute.MoreMachineBounding;
import com.jerry.mekmm.common.config.MoreMachineConfig;

import mekanism.api.Upgrade;
import mekanism.api.math.MathUtils;
import mekanism.common.MekanismLang;
import mekanism.common.block.attribute.*;
import mekanism.common.block.attribute.AttributeHasBounding.HandleBoundingBlock;
import mekanism.common.block.attribute.AttributeHasBounding.TriBooleanFunction;
import mekanism.common.content.blocktype.Machine;
import mekanism.common.content.blocktype.Machine.MachineBuilder;
import mekanism.common.lib.math.Pos3D;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.registration.impl.BlockRegistryObject;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.common.registries.MekanismSounds;
import mekanism.common.util.ChemicalUtil;
import mekanism.generators.common.GeneratorsLang;
import mekanism.generators.common.content.blocktype.Generator;
import mekanism.generators.common.registries.GeneratorsSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class LargeMachineBlockTypes {

    // TODO:看看能不能缩减成一个方法
    // Mid-Chemical Tanks
    public static final Machine<TileEntityMidChemicalTank> BASIC_MID_CHEMICAL_TANK = createLargeChemicalTank(MidChemicalTankTier.BASIC, () -> LargeMachineTileEntityTypes.BASIC_MID_CHEMICAL_TANK, () -> LargeMachineBlocks.ADVANCED_MID_CHEMICAL_TANK);
    public static final Machine<TileEntityMidChemicalTank> ADVANCED_MID_CHEMICAL_TANK = createLargeChemicalTank(MidChemicalTankTier.ADVANCED, () -> LargeMachineTileEntityTypes.ADVANCED_MID_CHEMICAL_TANK, () -> LargeMachineBlocks.ELITE_MID_CHEMICAL_TANK);
    public static final Machine<TileEntityMidChemicalTank> ELITE_MID_CHEMICAL_TANK = createLargeChemicalTank(MidChemicalTankTier.ELITE, () -> LargeMachineTileEntityTypes.ELITE_MID_CHEMICAL_TANK, () -> LargeMachineBlocks.ULTIMATE_MID_CHEMICAL_TANK);
    public static final Machine<TileEntityMidChemicalTank> ULTIMATE_MID_CHEMICAL_TANK = createLargeChemicalTank(MidChemicalTankTier.ULTIMATE, () -> LargeMachineTileEntityTypes.ULTIMATE_MID_CHEMICAL_TANK, null);

    // Max-Chemical Tanks
    public static final Machine<TileEntityMaxChemicalTank> BASIC_MAX_CHEMICAL_TANK = createLargeChemicalTank(MaxChemicalTankTier.BASIC, () -> LargeMachineTileEntityTypes.BASIC_MAX_CHEMICAL_TANK, () -> LargeMachineBlocks.ADVANCED_MAX_CHEMICAL_TANK);
    public static final Machine<TileEntityMaxChemicalTank> ADVANCED_MAX_CHEMICAL_TANK = createLargeChemicalTank(MaxChemicalTankTier.ADVANCED, () -> LargeMachineTileEntityTypes.ADVANCED_MAX_CHEMICAL_TANK, () -> LargeMachineBlocks.ELITE_MAX_CHEMICAL_TANK);
    public static final Machine<TileEntityMaxChemicalTank> ELITE_MAX_CHEMICAL_TANK = createLargeChemicalTank(MaxChemicalTankTier.ELITE, () -> LargeMachineTileEntityTypes.ELITE_MAX_CHEMICAL_TANK, () -> LargeMachineBlocks.ULTIMATE_MAX_CHEMICAL_TANK);
    public static final Machine<TileEntityMaxChemicalTank> ULTIMATE_MAX_CHEMICAL_TANK = createLargeChemicalTank(MaxChemicalTankTier.ULTIMATE, () -> LargeMachineTileEntityTypes.ULTIMATE_MAX_CHEMICAL_TANK, null);

    // Rotary Condensentrator
    public static final Machine<TileEntityLargeRotaryCondensentrator> LARGE_ROTARY_CONDENSENTRATOR = MachineBuilder
            .createMachine(() -> LargeMachineTileEntityTypes.LARGE_ROTARY_CONDENSENTRATOR, MekanismLang.DESCRIPTION_ROTARY_CONDENSENTRATOR)
            .withGui(() -> LargeMachineContainerTypes.LARGE_ROTARY_CONDENSENTRATOR)
            .withSound(MekanismSounds.ROTARY_CONDENSENTRATOR)
            .withEnergyConfig(MoreMachineConfig.usage.largeRotaryCondensentrator, MoreMachineConfig.storage.largeRotaryCondensentrator)
            .withSideConfig(TransmissionType.CHEMICAL, TransmissionType.FLUID, TransmissionType.ITEM, TransmissionType.ENERGY)
            .withCustomShape(LargeMachineBlockShapes.LARGE_ROTARY_CONDENSENTRATOR)
            .with(AttributeCustomSelectionBox.JSON)
            .with(MoreMachineBounding.FULL_JAVA_ENTITY)
            .withComputerSupport("largeRotaryCondensentrator")
            .build();

    // Chemical Infuser
    public static final Machine<TileEntityLargeChemicalInfuser> LARGE_CHEMICAL_INFUSER = MachineBuilder
            .createMachine(() -> LargeMachineTileEntityTypes.LARGE_CHEMICAL_INFUSER, MekanismLang.DESCRIPTION_CHEMICAL_INFUSER)
            .withGui(() -> LargeMachineContainerTypes.LARGE_CHEMICAL_INFUSER)
            .withSound(MekanismSounds.CHEMICAL_INFUSER)
            .withEnergyConfig(MoreMachineConfig.usage.largeChemicalInfuser, MoreMachineConfig.storage.largeChemicalInfuser)
            .withSideConfig(TransmissionType.CHEMICAL, TransmissionType.ITEM, TransmissionType.ENERGY)
            .withCustomShape(LargeMachineBlockShapes.LARGE_CHEMICAL_INFUSER)
            .with(AttributeCustomSelectionBox.JSON)
            .with(MoreMachineBounding.FULL_JAVA_ENTITY_BUT_TOP_BACK_2X3)
            .withComputerSupport("largeChemicalInfuser")
            .build();

    // Electrolytic Separator
    public static final Machine<TileEntityLargeElectrolyticSeparator> LARGE_ELECTROLYTIC_SEPARATOR = MachineBuilder
            .createMachine(() -> LargeMachineTileEntityTypes.LARGE_ELECTROLYTIC_SEPARATOR, MekanismLang.DESCRIPTION_ELECTROLYTIC_SEPARATOR)
            .withGui(() -> LargeMachineContainerTypes.LARGE_ELECTROLYTIC_SEPARATOR)
            .withSound(MekanismSounds.ELECTROLYTIC_SEPARATOR)
            .withEnergyConfig(() -> MathUtils.multiplyClamped(2, ChemicalUtil.hydrogenEnergyDensity()), MoreMachineConfig.storage.largeElectrolyticSeparator)
            .withSideConfig(TransmissionType.FLUID, TransmissionType.CHEMICAL, TransmissionType.ITEM, TransmissionType.ENERGY)
            .withCustomShape(LargeMachineBlockShapes.LARGE_ELECTROLYTIC_SEPARATOR)
            .with(AttributeCustomSelectionBox.JSON)
            .withBounding(new HandleBoundingBlock() {

                @Override
                public <DATA> boolean handle(Level level, BlockPos pos, BlockState state, DATA data, TriBooleanFunction<Level, BlockPos, DATA> consumer) {
                    BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
                    for (int x = -1; x <= 1; x++) {
                        for (int y = 0; y <= 1; y++) {
                            for (int z = -1; z <= 1; z++) {
                                if (x != 0 || y != 0 || z != 0) {
                                    mutable.setWithOffset(pos, x, y, z);
                                    if (!consumer.accept(level, mutable, data)) {
                                        return false;
                                    }
                                }
                            }
                        }
                    }
                    return true;
                }
            })
            .withComputerSupport("largeElectrolyticSeparator")
            .build();

    // Solar Neutron Activator
    public static final Machine<TileEntityLargeSolarNeutronActivator> LARGE_SOLAR_NEUTRON_ACTIVATOR = MachineBuilder
            .createMachine(() -> LargeMachineTileEntityTypes.LARGE_SOLAR_NEUTRON_ACTIVATOR, MekanismLang.DESCRIPTION_SOLAR_NEUTRON_ACTIVATOR)
            .withGui(() -> LargeMachineContainerTypes.LARGE_SOLAR_NEUTRON_ACTIVATOR)
            .without(AttributeParticleFX.class)
            .withSupportedUpgrades(Upgrade.SPEED, Upgrade.MUFFLING)
            .withCustomShape(LargeMachineBlockShapes.LARGE_SOLAR_NEUTRON_ACTIVATOR)
            .with(AttributeCustomSelectionBox.JSON)
            .withSideConfig(TransmissionType.CHEMICAL, TransmissionType.ITEM)
            .with(MoreMachineBounding.FULL_JAVA_ENTITY)
            .withComputerSupport("largeSolarNeutronActivator")
            .replace(Attributes.ACTIVE)
            .build();

    // Heat Generator
    public static final Generator<TileEntityLargeHeatGenerator> LARGE_HEAT_GENERATOR = Generator.GeneratorBuilder
            .createGenerator(() -> LargeMachineTileEntityTypes.LARGE_HEAT_GENERATOR, GeneratorsLang.DESCRIPTION_HEAT_GENERATOR)
            .withGui(() -> LargeMachineContainerTypes.LARGE_HEAT_GENERATOR)
            .withEnergyConfig(MoreMachineConfig.storage.largeHeatGenerator)
            .withCustomShape(LargeMachineBlockShapes.LARGE_HEAT_GENERATOR)
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
    public static final Generator<TileEntityLargeGasGenerator> LARGE_GAS_BURNING_GENERATOR = Generator.GeneratorBuilder
            .createGenerator(() -> LargeMachineTileEntityTypes.LARGE_GAS_BURNING_GENERATOR, GeneratorsLang.DESCRIPTION_GAS_BURNING_GENERATOR)
            .withGui(() -> LargeMachineContainerTypes.LARGE_GAS_BURNING_GENERATOR)
            .withEnergyConfig(() -> MathUtils.multiplyClamped(20_480_000L, ChemicalUtil.hydrogenEnergyDensity()))
            .withCustomShape(LargeMachineBlockShapes.LARGE_GAS_BURNING_GENERATOR)
            .with(AttributeCustomSelectionBox.JSON)
            .withSound(GeneratorsSounds.GAS_BURNING_GENERATOR)
            .with(AttributeUpgradeSupport.MUFFLING_ONLY)
            .with(MoreMachineBounding.FULL_JAVA_ENTITY)
            .withComputerSupport("largeGasBurningGenerator")
            .replace(Attributes.ACTIVE_MELT_LIGHT)
            .build();

    private static <TILE extends TileEntityLargeChemicalTank<?>> Machine<TILE> createLargeChemicalTank(ILargeChemicalTankTier tier, Supplier<TileEntityTypeRegistryObject<TILE>> tile, Supplier<BlockRegistryObject<?, ?>> upgradeBlock) {
        return MachineBuilder.createMachine(tile, MekanismLang.DESCRIPTION_CHEMICAL_TANK)
                .withGui(() -> LargeMachineContainerTypes.CHEMICAL_TANK)
                .withCustomShape(LargeMachineBlockShapes.getLargeChemicalTank(tier))
                .with(new AttributeTier<>(tier), new AttributeUpgradeable(upgradeBlock))
                .withSideConfig(TransmissionType.CHEMICAL, TransmissionType.ITEM)
                .without(AttributeParticleFX.class, AttributeStateActive.class, AttributeUpgradeSupport.class)
                // 使用json选框会显示所有小方块，不使用则只有外轮廓。如果有倾斜模型需要使用json选框，不然可能会出现选框位置不对以及严重卡顿的情况
                // .with(AttributeCustomSelectionBox.JSON)
                .with(getBounding(tier))
                .withComputerSupport(tier, tier.getType() + "ChemicalTank")
                .build();
    }

    private static AttributeHasBounding getBounding(ILargeChemicalTankTier tier) {
        return tier instanceof MidChemicalTankTier ? AttributeHasBounding.ABOVE_ONLY : MoreMachineBounding.VERTICAL_THREE_BLOCK;
    }

    private LargeMachineBlockTypes() {}
}
