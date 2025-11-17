package com.jerry.mekmm.client.jei;

import com.jerry.mekmm.api.recipes.FluidStackGasToFluidStackRecipe;
import com.jerry.mekmm.common.registries.MoreMachineGas;
import com.jerry.mekmm.common.tile.machine.TileEntityFluidReplicator;
import com.jerry.mekmm.common.tile.machine.TileEntityReplicator;

import mekanism.api.recipes.ItemStackGasToItemStackRecipe;
import mekanism.client.jei.RecipeRegistryHelper;
import mekanism.common.util.RegistryUtils;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import mezz.jei.api.registration.IRecipeRegistration;

import java.util.ArrayList;
import java.util.List;

public class MoreMachineRecipeRegistryHelper {

    private MoreMachineRecipeRegistryHelper() {}

    public static void registerItemReplicator(IRecipeRegistration registry) {
        List<ItemStackGasToItemStackRecipe> list = new ArrayList<>();
        for (Item item : ForgeRegistries.ITEMS.getValues()) {
            if (TileEntityReplicator.customRecipeMap.containsKey(RegistryUtils.getName(item).toString())) {
                list.add(TileEntityReplicator.getRecipe(new ItemStack(item), MoreMachineGas.UU_MATTER.getStack(1)));
            }
        }
        RecipeRegistryHelper.register(registry, MoreMachineJEIRecipeType.REPLICATOR, list);
    }

    public static void registerFluidReplicator(IRecipeRegistration registry) {
        List<FluidStackGasToFluidStackRecipe> list = new ArrayList<>();
        for (Fluid fluid : ForgeRegistries.FLUIDS.getValues()) {
            if (TileEntityFluidReplicator.customRecipeMap.containsKey(RegistryUtils.getName(fluid).toString())) {
                list.add(TileEntityFluidReplicator.getRecipe(new FluidStack(fluid, 1), MoreMachineGas.UU_MATTER.getStack(1)));
            }
        }
        RecipeRegistryHelper.register(registry, MoreMachineJEIRecipeType.FLUID_REPLICATOR, list);
    }
}
