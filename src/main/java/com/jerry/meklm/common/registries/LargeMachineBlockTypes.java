package com.jerry.meklm.common.registries;

import mekanism.api.functions.TriConsumer;
import mekanism.common.MekanismLang;
import mekanism.common.block.attribute.*;
import mekanism.common.content.blocktype.Machine;
import mekanism.common.registration.impl.BlockRegistryObject;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import com.jerry.meklm.common.content.blocktype.LargeMachineBlockShapes;
import com.jerry.meklm.common.tier.ILargeTankTier;
import com.jerry.meklm.common.tier.MaxChemicalTankTier;
import com.jerry.meklm.common.tier.MidChemicalTankTier;
import com.jerry.meklm.common.tile.TileEntityMaxChemicalTank;
import com.jerry.meklm.common.tile.TileEntityMidChemicalTank;
import com.jerry.meklm.common.tile.prefab.TileEntityLargeChemicalTank;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class LargeMachineBlockTypes {

    private LargeMachineBlockTypes() {}

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

    private static <TILE extends TileEntityLargeChemicalTank<?>> Machine<TILE> createLargeChemicalTank(ILargeTankTier tier, Supplier<TileEntityTypeRegistryObject<TILE>> tile, Supplier<BlockRegistryObject<?, ?>> upgradeBlock) {
        return Machine.MachineBuilder.createMachine(tile, MekanismLang.DESCRIPTION_CHEMICAL_TANK)
                .withGui(() -> LargeMachineContainerTypes.LARGE_CHEMICAL_TANK)
                .withCustomShape(LargeMachineBlockShapes.getTankShape(tier))
                .with(new AttributeTier<>(tier), new AttributeUpgradeable(upgradeBlock))
                .without(AttributeParticleFX.class, AttributeStateActive.class, AttributeUpgradeSupport.class)
                .withBounding(getBounding(tier))
                .withComputerSupport(tier, tier.type() + "ChemicalTank")
                .build();
    }

    private static TriConsumer<BlockPos, BlockState, Stream.Builder<BlockPos>> getBounding(ILargeTankTier tier) {
        return tier instanceof MidChemicalTankTier ?
                (pos, state, builder) -> builder.add(pos.above(1)) :
                (pos, state, builder) -> {
                    for (int i = 0; i < 2; i++) {
                        builder.add(pos.above(i + 1));
                    }
                };
    }
}
