package com.jerry.datagen.common;

import com.jerry.mekmm.api.datamaps.ChemicalReplicatorRecipe;
import com.jerry.mekmm.api.datamaps.FluidReplicatorRecipe;
import com.jerry.mekmm.api.datamaps.IMoreMachineDataMapTypes;
import com.jerry.mekmm.api.datamaps.ItemReplicatorRecipe;
import com.jerry.mekmm.api.datamaps.SolarHeatFluid;

import mekanism.common.registries.MekanismChemicals;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.data.DataMapProvider;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class MoreMachineDataMapsProvider extends DataMapProvider {

    public MoreMachineDataMapsProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather(@NotNull HolderLookup.Provider provider) {
        builder(IMoreMachineDataMapTypes.INSTANCE.itemReplicatorRecipe())
                .add(ItemTags.create(common("stones")), new ItemReplicatorRecipe(1), false)
                .add(ItemTags.create(common("ores")), new ItemReplicatorRecipe(1), false)
                .add(ItemTags.create(common("ingots")), new ItemReplicatorRecipe(5), false)
                .add(ItemTags.LOGS, new ItemReplicatorRecipe(4), false)
                .add(ItemTags.PLANKS, new ItemReplicatorRecipe(1), false);

        builder(IMoreMachineDataMapTypes.INSTANCE.fluidReplicatorRecipe())
                .add(FluidTags.create(common("water")), new FluidReplicatorRecipe(1_000, 1, 500), false)
                .add(FluidTags.create(common("lava")), new FluidReplicatorRecipe(1_000, 1, 500), false);

        builder(IMoreMachineDataMapTypes.INSTANCE.solarHeatFluid())
                .add(Fluids.WATER.builtInRegistryHolder(), new SolarHeatFluid(0.5, 0.02), false);

        builder(IMoreMachineDataMapTypes.INSTANCE.chemicalReplicatorRecipe())
                .add(MekanismChemicals.FISSILE_FUEL, new ChemicalReplicatorRecipe(1, 1, 100), false)
                .add(MekanismChemicals.ANTIMATTER, new ChemicalReplicatorRecipe(1, 249, 1), false);
    }

    private static Identifier common(String path) {
        return Identifier.fromNamespaceAndPath("c", path);
    }
}
