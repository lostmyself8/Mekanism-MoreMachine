package com.jerry.mekmm.api.datamaps;

import mekanism.api.MekanismAPI;
import mekanism.api.chemical.Chemical;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

import org.jetbrains.annotations.Nullable;

public interface IMoreMachineDataMapTypes {

    /**
     * Provides access to Mekanism's data map types.
     */
    IMoreMachineDataMapTypes INSTANCE = MekanismAPI.getService(IMoreMachineDataMapTypes.class);

    /**
     * Helper to get data from a holder. This method supports both reference and direct holders.
     *
     * @param registryAccess Registry access to look up the reference if a direct holder was provided.
     * @param registryName   Name of the registry that contains the holder.
     * @param holder         Holder to query.
     * @param type           Type of data to lookup.
     *
     * @return Absorption values or null if there are no absorption values defined for the damage type.
     */
    @Nullable
    <TYPE, DATA> DATA getData(RegistryAccess registryAccess, ResourceKey<? extends Registry<? extends TYPE>> registryName, Holder<TYPE> holder, DataMapType<TYPE, DATA> type);

    DataMapType<Item, ItemReplicatorRecipe> itemReplicatorRecipe();

    DataMapType<Fluid, FluidReplicatorRecipe> fluidReplicatorRecipe();

    DataMapType<Fluid, SolarHeatFluid> solarHeatFluid();

    DataMapType<Chemical, ChemicalReplicatorRecipe> chemicalReplicatorRecipe();

    @Nullable
    default ItemReplicatorRecipe getItemReplicatorRecipe(Holder<Item> holder) {
        return holder.getData(itemReplicatorRecipe());
    }

    @Nullable
    default FluidReplicatorRecipe getFluidReplicatorRecipe(Holder<Fluid> holder) {
        return holder.getData(fluidReplicatorRecipe());
    }

    @Nullable
    default SolarHeatFluid getSolarHeatFluid(Holder<Fluid> holder) {
        return holder.getData(solarHeatFluid());
    }

    @Nullable
    default ChemicalReplicatorRecipe getChemicalReplicatorRecipe(Holder<Chemical> holder) {
        return holder.getData(chemicalReplicatorRecipe());
    }
}
