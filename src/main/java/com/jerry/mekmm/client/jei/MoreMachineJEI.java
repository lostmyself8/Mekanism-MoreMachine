package com.jerry.mekmm.client.jei;

import com.jerry.mekaf.client.jei.AdvancedFactoryCatalystRegistryHelper;

import com.jerry.mekmm.Mekmm;
import com.jerry.mekmm.client.jei.machine.*;
import com.jerry.mekmm.common.recipe.MoreMachineRecipeType;
import com.jerry.mekmm.common.registries.MoreMachineBlocks;

import mekanism.client.jei.MekanismJEIRecipeType;
import mekanism.client.jei.RecipeRegistryHelper;
import mekanism.client.jei.machine.ItemStackToItemStackRecipeCategory;
import mekanism.common.registries.MekanismBlocks;

import net.minecraft.resources.ResourceLocation;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class MoreMachineJEI implements IModPlugin {

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return Mekmm.rl("jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();

        registration.addRecipeCategories(new RecyclerRecipeCategory(guiHelper, MoreMachineJEIRecipeType.RECYCLING));
        registration.addRecipeCategories(new PlantingRecipeCategory(guiHelper, MoreMachineJEIRecipeType.PLANTING));

        registration.addRecipeCategories(new StamperRecipeCategory(guiHelper, MoreMachineJEIRecipeType.CNC_STAMPING));
        registration.addRecipeCategories(new ItemStackToItemStackRecipeCategory(guiHelper, MoreMachineJEIRecipeType.CNC_LATHING, MoreMachineBlocks.CNC_LATHE));
        registration.addRecipeCategories(new ItemStackToItemStackRecipeCategory(guiHelper, MoreMachineJEIRecipeType.CNC_ROLLING_MILL, MoreMachineBlocks.CNC_ROLLING_MILL));

        registration.addRecipeCategories(new ReplicatorRecipeCategory(guiHelper, MoreMachineJEIRecipeType.REPLICATOR));
        registration.addRecipeCategories(new FluidReplicatorRecipeCategory(guiHelper, MoreMachineJEIRecipeType.FLUID_REPLICATOR));
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registry) {
        RecipeRegistryHelper.register(registry, MoreMachineJEIRecipeType.RECYCLING, MoreMachineRecipeType.RECYCLING);
        RecipeRegistryHelper.register(registry, MoreMachineJEIRecipeType.PLANTING, MoreMachineRecipeType.PLANTING);

        RecipeRegistryHelper.register(registry, MoreMachineJEIRecipeType.CNC_STAMPING, MoreMachineRecipeType.STAMPING);
        RecipeRegistryHelper.register(registry, MoreMachineJEIRecipeType.CNC_LATHING, MoreMachineRecipeType.LATHING);
        RecipeRegistryHelper.register(registry, MoreMachineJEIRecipeType.CNC_ROLLING_MILL, MoreMachineRecipeType.ROLLING_MILL);

        MoreMachineRecipeRegistryHelper.registerItemReplicator(registry);
        MoreMachineRecipeRegistryHelper.registerFluidReplicator(registry);

        // 这玩意写在这没用，我也不知道为什么，但使用mixin可以使这行代码生效。
        // registry.addIngredientInfo(MoreMachineGas.UNSTABLE_DIMENSIONAL_GAS.getStack(FluidType.BUCKET_VOLUME),
        // MekanismJEI.TYPE_GAS,
        // MoreMachineLang.JEI_INFO_UNSTABLE_DIMENSIONAL_GAS.translate(MoreMachineConfig.general.gasCollectAmount.get()));
    }

    @Override
    public void registerRecipeCatalysts(@NotNull IRecipeCatalystRegistration registry) {
        MoreMachineCatalystRegistryHelper.register(registry, MoreMachineBlocks.RECYCLER);
        MoreMachineCatalystRegistryHelper.register(registry, MoreMachineBlocks.PLANTING_STATION, MekanismJEIRecipeType.GAS_CONVERSION);

        MoreMachineCatalystRegistryHelper.register(registry, MoreMachineBlocks.CNC_STAMPER);
        MoreMachineCatalystRegistryHelper.register(registry, MoreMachineBlocks.CNC_LATHE);
        MoreMachineCatalystRegistryHelper.register(registry, MoreMachineBlocks.CNC_ROLLING_MILL);

        MoreMachineCatalystRegistryHelper.register(registry, MoreMachineBlocks.REPLICATOR, MekanismJEIRecipeType.GAS_CONVERSION);
        MoreMachineCatalystRegistryHelper.register(registry, MoreMachineBlocks.FLUID_REPLICATOR, MekanismJEIRecipeType.GAS_CONVERSION);

        AdvancedFactoryCatalystRegistryHelper.register(registry, MekanismBlocks.CHEMICAL_OXIDIZER);
        AdvancedFactoryCatalystRegistryHelper.register(registry, MekanismBlocks.CHEMICAL_INFUSER);
        AdvancedFactoryCatalystRegistryHelper.register(registry, MekanismBlocks.CHEMICAL_DISSOLUTION_CHAMBER);
        AdvancedFactoryCatalystRegistryHelper.register(registry, MekanismBlocks.CHEMICAL_WASHER);
        AdvancedFactoryCatalystRegistryHelper.register(registry, MekanismBlocks.CHEMICAL_CRYSTALLIZER);
        AdvancedFactoryCatalystRegistryHelper.register(registry, MekanismBlocks.PRESSURIZED_REACTION_CHAMBER);
        AdvancedFactoryCatalystRegistryHelper.register(registry, MekanismBlocks.ISOTOPIC_CENTRIFUGE);
        AdvancedFactoryCatalystRegistryHelper.register(registry, MekanismBlocks.NUTRITIONAL_LIQUIFIER);
    }
}
