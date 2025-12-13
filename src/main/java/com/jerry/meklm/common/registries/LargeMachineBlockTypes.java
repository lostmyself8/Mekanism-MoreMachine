package com.jerry.meklm.common.registries;

import com.jerry.meklm.api.tier.ILargeChemicalTankTier;
import com.jerry.meklm.common.content.blocktype.LargeMachineBlockShapes;
import com.jerry.meklm.common.tier.MaxChemicalTankTier;
import com.jerry.meklm.common.tier.MidChemicalTankTier;
import com.jerry.meklm.common.tile.TileEntityMaxChemicalTank;
import com.jerry.meklm.common.tile.TileEntityMidChemicalTank;
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
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.registration.impl.BlockRegistryObject;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.common.registries.MekanismSounds;
import mekanism.common.util.ChemicalUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class LargeMachineBlockTypes {

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
