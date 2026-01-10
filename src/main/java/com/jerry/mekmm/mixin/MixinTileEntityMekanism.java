package com.jerry.mekmm.mixin;

import com.jerry.mekmm.api.ITileEntityMekanismAccessor;

import mekanism.common.capabilities.resolver.manager.ChemicalHandlerManager.GasHandlerManager;
import mekanism.common.capabilities.resolver.manager.ChemicalHandlerManager.InfusionHandlerManager;
import mekanism.common.capabilities.resolver.manager.ChemicalHandlerManager.PigmentHandlerManager;
import mekanism.common.capabilities.resolver.manager.ChemicalHandlerManager.SlurryHandlerManager;
import mekanism.common.capabilities.resolver.manager.EnergyHandlerManager;
import mekanism.common.tile.base.TileEntityMekanism;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = TileEntityMekanism.class, remap = false)
public abstract class MixinTileEntityMekanism implements ITileEntityMekanismAccessor {

    @Accessor("gasHandlerManager")
    public abstract GasHandlerManager getGasHandlerManager();

    @Accessor("infusionHandlerManager")
    public abstract InfusionHandlerManager getInfusionHandlerManager();

    @Accessor("pigmentHandlerManager")
    public abstract PigmentHandlerManager getPigmentHandlerManager();

    @Accessor("slurryHandlerManager")
    public abstract SlurryHandlerManager getSlurryHandlerManager();

    @Accessor("energyHandlerManager")
    public abstract EnergyHandlerManager getEnergyHandlerManager();
}
