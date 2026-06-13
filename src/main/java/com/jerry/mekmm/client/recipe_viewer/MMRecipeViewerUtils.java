package com.jerry.mekmm.client.recipe_viewer;

import com.jerry.mekmm.Mekmm;
import com.jerry.mekmm.api.recipes.basic.BasicFluidChemicalToFluidRecipe;
import com.jerry.mekmm.api.recipes.basic.MMBasicChemicalChemicalToChemicalRecipe;
import com.jerry.mekmm.api.recipes.basic.MMBasicItemStackChemicalToItemStackRecipe;
import com.jerry.mekmm.common.registries.MoreMachineChemicals;
import com.jerry.mekmm.common.tile.machine.TileEntityChemicalReplicator;
import com.jerry.mekmm.common.tile.machine.TileEntityFluidReplicator;
import com.jerry.mekmm.common.tile.machine.TileEntityReplicator;

import mekanism.api.MekanismAPI;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.common.util.RegistryUtils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.HashMap;
import java.util.Map;

public class MMRecipeViewerUtils {

    private MMRecipeViewerUtils() {}

    public static Map<Identifier, MMBasicItemStackChemicalToItemStackRecipe> getItemReplicatorRecipes() {
        Map<Identifier, MMBasicItemStackChemicalToItemStackRecipe> replicator = new HashMap<>();
        // TODO: Do we want to loop creative tabs or something instead?
        // In theory recipe loaders should init the creative tabs before we are called so we wouldn't need to call
        // CreativeModeTab#buildContents, and in theory we only need to care about things in search so could use:
        // CreativeModeTabs.searchTab().getDisplayItems(). The bigger issue is how to come up with unique synthetic
        // names for the recipes as EMI requires they be unique. (Maybe index them?)
        for (Map.Entry<ResourceKey<Item>, Item> entry : BuiltInRegistries.ITEM.entrySet()) {
            MMBasicItemStackChemicalToItemStackRecipe recipe = TileEntityReplicator.getRecipe(entry.getValue().getDefaultInstance(), MoreMachineChemicals.UU_MATTER.asResource().toStack(1));
            if (recipe != null) {
                replicator.put(RegistryUtils.synthetic(entry.getKey().identifier(), "replicator", Mekmm.MOD_ID), recipe);
            }
        }
        return replicator;
    }

    public static Map<Identifier, BasicFluidChemicalToFluidRecipe> getFluidReplicatorRecipes() {
        Map<Identifier, BasicFluidChemicalToFluidRecipe> replicator = new HashMap<>();
        for (Map.Entry<ResourceKey<Fluid>, Fluid> entry : BuiltInRegistries.FLUID.entrySet()) {
            BasicFluidChemicalToFluidRecipe recipe = TileEntityFluidReplicator.getRecipe(new FluidStack(entry.getValue(), 1), MoreMachineChemicals.UU_MATTER.asResource().toStack(1));
            if (recipe != null) {
                replicator.put(RegistryUtils.synthetic(entry.getKey().identifier(), "replicator", Mekmm.MOD_ID), recipe);
            }
        }
        return replicator;
    }

    public static Map<Identifier, MMBasicChemicalChemicalToChemicalRecipe> getChemicalReplicatorRecipes() {
        Map<Identifier, MMBasicChemicalChemicalToChemicalRecipe> replicator = new HashMap<>();
        for (Map.Entry<ResourceKey<Chemical>, Chemical> entry : MekanismAPI.CHEMICAL_REGISTRY.entrySet()) {
            // mek将很多方法弃用了，所以只能使用这个办法。
            MMBasicChemicalChemicalToChemicalRecipe recipe = TileEntityChemicalReplicator.getRecipe(new ChemicalStack(MekanismAPI.CHEMICAL_REGISTRY.wrapAsHolder(entry.getValue()), 1), MoreMachineChemicals.UU_MATTER.asResource().toStack(1));
            if (recipe != null) {
                replicator.put(RegistryUtils.synthetic(entry.getKey().identifier(), "replicator", Mekmm.MOD_ID), recipe);
            }
        }
        return replicator;
    }
}
