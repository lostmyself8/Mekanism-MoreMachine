package com.jerry.meklm.common.registries;

import com.jerry.mekmm.Mekmm;

import mekanism.common.registration.impl.TileEntityTypeDeferredRegister;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.common.tile.base.TileEntityMekanism;

import com.jerry.meklm.common.tile.TileEntityMaxChemicalTank;
import com.jerry.meklm.common.tile.TileEntityMidChemicalTank;

public class LargeMachineTileEntityTypes {

    private LargeMachineTileEntityTypes() {}

    public static final TileEntityTypeDeferredRegister LM_TILE_ENTITY_TYPES = new TileEntityTypeDeferredRegister(Mekmm.MOD_ID);

    // Mid-Chemical Tanks
    public static final TileEntityTypeRegistryObject<TileEntityMidChemicalTank> BASIC_MID_CHEMICAL_TANK = LM_TILE_ENTITY_TYPES.register(LargeMachineBlocks.BASIC_MID_CHEMICAL_TANK, (pos, state) -> new TileEntityMidChemicalTank(LargeMachineBlocks.BASIC_MID_CHEMICAL_TANK, pos, state), TileEntityMekanism::tickServer, TileEntityMekanism::tickClient);
    public static final TileEntityTypeRegistryObject<TileEntityMidChemicalTank> ADVANCED_MID_CHEMICAL_TANK = LM_TILE_ENTITY_TYPES.register(LargeMachineBlocks.ADVANCED_MID_CHEMICAL_TANK, (pos, state) -> new TileEntityMidChemicalTank(LargeMachineBlocks.ADVANCED_MID_CHEMICAL_TANK, pos, state), TileEntityMekanism::tickServer, TileEntityMekanism::tickClient);
    public static final TileEntityTypeRegistryObject<TileEntityMidChemicalTank> ELITE_MID_CHEMICAL_TANK = LM_TILE_ENTITY_TYPES.register(LargeMachineBlocks.ELITE_MID_CHEMICAL_TANK, (pos, state) -> new TileEntityMidChemicalTank(LargeMachineBlocks.ELITE_MID_CHEMICAL_TANK, pos, state), TileEntityMekanism::tickServer, TileEntityMekanism::tickClient);
    public static final TileEntityTypeRegistryObject<TileEntityMidChemicalTank> ULTIMATE_MID_CHEMICAL_TANK = LM_TILE_ENTITY_TYPES.register(LargeMachineBlocks.ULTIMATE_MID_CHEMICAL_TANK, (pos, state) -> new TileEntityMidChemicalTank(LargeMachineBlocks.ULTIMATE_MID_CHEMICAL_TANK, pos, state), TileEntityMekanism::tickServer, TileEntityMekanism::tickClient);
    // Mid-Chemical Tanks
    public static final TileEntityTypeRegistryObject<TileEntityMaxChemicalTank> BASIC_MAX_CHEMICAL_TANK = LM_TILE_ENTITY_TYPES.register(LargeMachineBlocks.BASIC_MAX_CHEMICAL_TANK, (pos, state) -> new TileEntityMaxChemicalTank(LargeMachineBlocks.BASIC_MAX_CHEMICAL_TANK, pos, state), TileEntityMekanism::tickServer, TileEntityMekanism::tickClient);
    public static final TileEntityTypeRegistryObject<TileEntityMaxChemicalTank> ADVANCED_MAX_CHEMICAL_TANK = LM_TILE_ENTITY_TYPES.register(LargeMachineBlocks.ADVANCED_MAX_CHEMICAL_TANK, (pos, state) -> new TileEntityMaxChemicalTank(LargeMachineBlocks.ADVANCED_MAX_CHEMICAL_TANK, pos, state), TileEntityMekanism::tickServer, TileEntityMekanism::tickClient);
    public static final TileEntityTypeRegistryObject<TileEntityMaxChemicalTank> ELITE_MAX_CHEMICAL_TANK = LM_TILE_ENTITY_TYPES.register(LargeMachineBlocks.ELITE_MAX_CHEMICAL_TANK, (pos, state) -> new TileEntityMaxChemicalTank(LargeMachineBlocks.ELITE_MAX_CHEMICAL_TANK, pos, state), TileEntityMekanism::tickServer, TileEntityMekanism::tickClient);
    public static final TileEntityTypeRegistryObject<TileEntityMaxChemicalTank> ULTIMATE_MAX_CHEMICAL_TANK = LM_TILE_ENTITY_TYPES.register(LargeMachineBlocks.ULTIMATE_MAX_CHEMICAL_TANK, (pos, state) -> new TileEntityMaxChemicalTank(LargeMachineBlocks.ULTIMATE_MAX_CHEMICAL_TANK, pos, state), TileEntityMekanism::tickServer, TileEntityMekanism::tickClient);
}
