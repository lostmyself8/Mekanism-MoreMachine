package com.jerry.meklm.common.registries;

import com.jerry.meklm.common.item.block.ItemBlockMaxChemicalTank;
import com.jerry.meklm.common.item.block.ItemBlockMidChemicalTank;
import com.jerry.meklm.common.tile.TileEntityMaxChemicalTank;
import com.jerry.meklm.common.tile.TileEntityMidChemicalTank;
import com.jerry.meklm.common.tile.machine.TileEntityLargeChemicalInfuser;
import com.jerry.meklm.common.tile.machine.TileEntityLargeElectrolyticSeparator;
import com.jerry.meklm.common.tile.machine.TileEntityLargeRotaryCondensentrator;
import com.jerry.meklm.common.tile.machine.TileEntityLargeSolarNeutronActivator;

import com.jerry.mekmm.Mekmm;

import mekanism.common.capabilities.Capabilities;
import mekanism.common.registration.impl.BlockRegistryObject;
import mekanism.common.registration.impl.TileEntityTypeDeferredRegister;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.common.tile.base.TileEntityMekanism;

public class LargeMachineTileEntityTypes {

    private LargeMachineTileEntityTypes() {}

    public static final TileEntityTypeDeferredRegister LM_TILE_ENTITY_TYPES = new TileEntityTypeDeferredRegister(Mekmm.MOD_ID);

    // TODO:看看能不能缩减成一个方法
    // Mid-Chemical Tanks
    public static final TileEntityTypeRegistryObject<TileEntityMidChemicalTank> BASIC_MID_CHEMICAL_TANK = registerMidChemicalTank(LargeMachineBlocks.BASIC_MID_CHEMICAL_TANK);
    public static final TileEntityTypeRegistryObject<TileEntityMidChemicalTank> ADVANCED_MID_CHEMICAL_TANK = registerMidChemicalTank(LargeMachineBlocks.ADVANCED_MID_CHEMICAL_TANK);
    public static final TileEntityTypeRegistryObject<TileEntityMidChemicalTank> ELITE_MID_CHEMICAL_TANK = registerMidChemicalTank(LargeMachineBlocks.ELITE_MID_CHEMICAL_TANK);
    public static final TileEntityTypeRegistryObject<TileEntityMidChemicalTank> ULTIMATE_MID_CHEMICAL_TANK = registerMidChemicalTank(LargeMachineBlocks.ULTIMATE_MID_CHEMICAL_TANK);

    private static TileEntityTypeRegistryObject<TileEntityMidChemicalTank> registerMidChemicalTank(BlockRegistryObject<?, ItemBlockMidChemicalTank> block) {
        return LM_TILE_ENTITY_TYPES.mekBuilder(block, (pos, state) -> new TileEntityMidChemicalTank(block, pos, state))
                .serverTicker(TileEntityMekanism::tickServer)
                .withSimple(Capabilities.CONFIG_CARD)
                .build();
    }

    // Max-Chemical Tanks
    public static final TileEntityTypeRegistryObject<TileEntityMaxChemicalTank> BASIC_MAX_CHEMICAL_TANK = registerMaxChemicalTank(LargeMachineBlocks.BASIC_MAX_CHEMICAL_TANK);
    public static final TileEntityTypeRegistryObject<TileEntityMaxChemicalTank> ADVANCED_MAX_CHEMICAL_TANK = registerMaxChemicalTank(LargeMachineBlocks.ADVANCED_MAX_CHEMICAL_TANK);
    public static final TileEntityTypeRegistryObject<TileEntityMaxChemicalTank> ELITE_MAX_CHEMICAL_TANK = registerMaxChemicalTank(LargeMachineBlocks.ELITE_MAX_CHEMICAL_TANK);
    public static final TileEntityTypeRegistryObject<TileEntityMaxChemicalTank> ULTIMATE_MAX_CHEMICAL_TANK = registerMaxChemicalTank(LargeMachineBlocks.ULTIMATE_MAX_CHEMICAL_TANK);

    private static TileEntityTypeRegistryObject<TileEntityMaxChemicalTank> registerMaxChemicalTank(BlockRegistryObject<?, ItemBlockMaxChemicalTank> block) {
        return LM_TILE_ENTITY_TYPES.mekBuilder(block, (pos, state) -> new TileEntityMaxChemicalTank(block, pos, state))
                .serverTicker(TileEntityMekanism::tickServer)
                .withSimple(Capabilities.CONFIG_CARD)
                .build();
    }

    public static final TileEntityTypeRegistryObject<TileEntityLargeRotaryCondensentrator> LARGE_ROTARY_CONDENSENTRATOR = LM_TILE_ENTITY_TYPES
            .mekBuilder(LargeMachineBlocks.LARGE_ROTARY_CONDENSENTRATOR, TileEntityLargeRotaryCondensentrator::new)
            .clientTicker(TileEntityMekanism::tickClient)
            .serverTicker(TileEntityMekanism::tickServer)
            .withSimple(Capabilities.CONFIG_CARD)
            .build();

    public static final TileEntityTypeRegistryObject<TileEntityLargeChemicalInfuser> LARGE_CHEMICAL_INFUSER = LM_TILE_ENTITY_TYPES
            .mekBuilder(LargeMachineBlocks.LARGE_CHEMICAL_INFUSER, TileEntityLargeChemicalInfuser::new)
            .clientTicker(TileEntityMekanism::tickClient)
            .serverTicker(TileEntityMekanism::tickServer)
            .withSimple(Capabilities.CONFIG_CARD)
            .build();

    public static final TileEntityTypeRegistryObject<TileEntityLargeElectrolyticSeparator> LARGE_ELECTROLYTIC_SEPARATOR = LM_TILE_ENTITY_TYPES
            .mekBuilder(LargeMachineBlocks.LARGE_ELECTROLYTIC_SEPARATOR, TileEntityLargeElectrolyticSeparator::new)
            .clientTicker(TileEntityMekanism::tickClient)
            .serverTicker(TileEntityMekanism::tickServer)
            .withSimple(Capabilities.CONFIG_CARD)
            .build();

    public static final TileEntityTypeRegistryObject<TileEntityLargeSolarNeutronActivator> LARGE_SOLAR_NEUTRON_ACTIVATOR = LM_TILE_ENTITY_TYPES
            .mekBuilder(LargeMachineBlocks.LARGE_SOLAR_NEUTRON_ACTIVATOR, TileEntityLargeSolarNeutronActivator::new)
            .serverTicker(TileEntityMekanism::tickServer)
            .withSimple(Capabilities.CONFIG_CARD)
            .build();
}
