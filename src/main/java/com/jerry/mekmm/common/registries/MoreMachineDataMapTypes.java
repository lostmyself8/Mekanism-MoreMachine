package com.jerry.mekmm.common.registries;

import com.jerry.mekmm.Mekmm;
import com.jerry.mekmm.api.datamaps.ChemicalReplicatorRecipe;
import com.jerry.mekmm.api.datamaps.FluidReplicatorRecipe;
import com.jerry.mekmm.api.datamaps.IMoreMachineDataMapTypes;
import com.jerry.mekmm.api.datamaps.ItemReplicatorRecipe;
import com.jerry.mekmm.api.datamaps.SolarHeatFluid;

import mekanism.api.MekanismAPI;
import mekanism.api.chemical.Chemical;
import mekanism.common.registration.impl.DataMapTypeRegister;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class MoreMachineDataMapTypes implements IMoreMachineDataMapTypes {

    public static final DataMapTypeRegister REGISTER = new DataMapTypeRegister(Mekmm.MOD_ID);

    private static final DataMapType<Item, ItemReplicatorRecipe> ITEM_REPLICATOR_RECIPE = REGISTER.registerSimple(ItemReplicatorRecipe.ID, Registries.ITEM, ItemReplicatorRecipe.CODEC);
    private static final DataMapType<Fluid, FluidReplicatorRecipe> FLUID_REPLICATOR_RECIPE = REGISTER.registerSimple(FluidReplicatorRecipe.ID, Registries.FLUID, FluidReplicatorRecipe.CODEC);
    private static final DataMapType<Fluid, SolarHeatFluid> SOLAR_HEAT_FLUID = REGISTER.registerSimple(SolarHeatFluid.ID, Registries.FLUID, SolarHeatFluid.CODEC);
    private static final DataMapType<Chemical, ChemicalReplicatorRecipe> CHEMICAL_REPLICATOR_RECIPE = REGISTER.registerSimple(ChemicalReplicatorRecipe.ID, MekanismAPI.CHEMICAL_REGISTRY_NAME, ChemicalReplicatorRecipe.CODEC);

    @Override
    public DataMapType<Item, ItemReplicatorRecipe> itemReplicatorRecipe() {
        return ITEM_REPLICATOR_RECIPE;
    }

    @Override
    public DataMapType<Fluid, FluidReplicatorRecipe> fluidReplicatorRecipe() {
        return FLUID_REPLICATOR_RECIPE;
    }

    @Override
    public DataMapType<Fluid, SolarHeatFluid> solarHeatFluid() {
        return SOLAR_HEAT_FLUID;
    }

    @Override
    public DataMapType<Chemical, ChemicalReplicatorRecipe> chemicalReplicatorRecipe() {
        return CHEMICAL_REPLICATOR_RECIPE;
    }

    @Override
    public <TYPE, DATA> @Nullable DATA getData(RegistryAccess registryAccess, ResourceKey<? extends Registry<? extends TYPE>> registryName, Holder<TYPE> holder, DataMapType<TYPE, DATA> type) {
        if (holder.kind() == Holder.Kind.REFERENCE) {
            // Reference holders can query data map values
            return holder.getData(type);
        }
        Optional<Registry<TYPE>> registry = registryAccess.registry(registryName);
        // noinspection OptionalIsPresent - Capturing lambda
        if (registry.isPresent()) {
            return registry.get().wrapAsHolder(holder.value()).getData(type);
        }
        return null;
    }
}
