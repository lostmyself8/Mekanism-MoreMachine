package com.jerry.mekaf.client.recipe_viewer.jei;

import com.jerry.mekaf.common.content.blocktype.AdvancedFactoryType;
import com.jerry.mekaf.common.registries.AdvancedFactoryBlocks;

import com.jerry.mekmm.common.util.MoreMachineUtils;

import mekanism.client.recipe_viewer.jei.MekanismJEI;
import mekanism.client.recipe_viewer.type.RecipeViewerRecipeType;
import mekanism.common.tier.FactoryTier;

import mezz.jei.api.registration.IRecipeCatalystRegistration;

public class AFCatalystRegistryHelper {

    private AFCatalystRegistryHelper() {}

    public static void register(IRecipeCatalystRegistration registry) {
        for (FactoryTier tier : MoreMachineUtils.getFactoryTier()) {
            registry.addCraftingStation(MekanismJEI.genericRecipeType(RecipeViewerRecipeType.OXIDIZING), AdvancedFactoryBlocks.getAdvancedFactory(tier, AdvancedFactoryType.OXIDIZING));
            registry.addCraftingStation(MekanismJEI.genericRecipeType(RecipeViewerRecipeType.DISSOLUTION), AdvancedFactoryBlocks.getAdvancedFactory(tier, AdvancedFactoryType.DISSOLVING));
            registry.addCraftingStation(MekanismJEI.genericRecipeType(RecipeViewerRecipeType.WASHING), AdvancedFactoryBlocks.getAdvancedFactory(tier, AdvancedFactoryType.WASHING));
            registry.addCraftingStation(MekanismJEI.genericRecipeType(RecipeViewerRecipeType.CRYSTALLIZING), AdvancedFactoryBlocks.getAdvancedFactory(tier, AdvancedFactoryType.CRYSTALLIZING));
            registry.addCraftingStation(MekanismJEI.genericRecipeType(RecipeViewerRecipeType.REACTION), AdvancedFactoryBlocks.getAdvancedFactory(tier, AdvancedFactoryType.PRESSURISED_REACTING));
            registry.addCraftingStation(MekanismJEI.genericRecipeType(RecipeViewerRecipeType.CENTRIFUGING), AdvancedFactoryBlocks.getAdvancedFactory(tier, AdvancedFactoryType.CENTRIFUGING));
            registry.addCraftingStation(MekanismJEI.genericRecipeType(RecipeViewerRecipeType.NUTRITIONAL_LIQUIFICATION), AdvancedFactoryBlocks.getAdvancedFactory(tier, AdvancedFactoryType.LIQUIFYING));
            registry.addCraftingStation(MekanismJEI.genericRecipeType(RecipeViewerRecipeType.PIGMENT_EXTRACTING), AdvancedFactoryBlocks.getAdvancedFactory(tier, AdvancedFactoryType.PIGMENT_EXTRACTING));
            registry.addCraftingStation(MekanismJEI.genericRecipeType(RecipeViewerRecipeType.PAINTING), AdvancedFactoryBlocks.getAdvancedFactory(tier, AdvancedFactoryType.PAINTING));
        }
    }

    // public static void register(EmiRegistry registry) {
    // for (FactoryTier tier : MoreMachineUtils.getFactoryTier()) {
    // registry.addWorkstation(MekanismEmiRecipeCategory.create(RecipeViewerRecipeType.OXIDIZING),
    // EmiStack.of(AdvancedFactoryBlocks.getAdvancedFactory(tier, AdvancedFactoryType.OXIDIZING)));
    // registry.addWorkstation(MekanismEmiRecipeCategory.create(RecipeViewerRecipeType.DISSOLUTION),
    // EmiStack.of(AdvancedFactoryBlocks.getAdvancedFactory(tier, AdvancedFactoryType.DISSOLVING)));
    // registry.addWorkstation(MekanismEmiRecipeCategory.create(RecipeViewerRecipeType.WASHING),
    // EmiStack.of(AdvancedFactoryBlocks.getAdvancedFactory(tier, AdvancedFactoryType.WASHING)));
    // registry.addWorkstation(MekanismEmiRecipeCategory.create(RecipeViewerRecipeType.CRYSTALLIZING),
    // EmiStack.of(AdvancedFactoryBlocks.getAdvancedFactory(tier, AdvancedFactoryType.CRYSTALLIZING)));
    // registry.addWorkstation(MekanismEmiRecipeCategory.create(RecipeViewerRecipeType.REACTION),
    // EmiStack.of(AdvancedFactoryBlocks.getAdvancedFactory(tier, AdvancedFactoryType.PRESSURISED_REACTING)));
    // registry.addWorkstation(MekanismEmiRecipeCategory.create(RecipeViewerRecipeType.CENTRIFUGING),
    // EmiStack.of(AdvancedFactoryBlocks.getAdvancedFactory(tier, AdvancedFactoryType.CENTRIFUGING)));
    // registry.addWorkstation(MekanismEmiRecipeCategory.create(RecipeViewerRecipeType.NUTRITIONAL_LIQUIFICATION),
    // EmiStack.of(AdvancedFactoryBlocks.getAdvancedFactory(tier, AdvancedFactoryType.LIQUIFYING)));
    // registry.addWorkstation(MekanismEmiRecipeCategory.create(RecipeViewerRecipeType.PIGMENT_EXTRACTING),
    // EmiStack.of(AdvancedFactoryBlocks.getAdvancedFactory(tier, AdvancedFactoryType.PIGMENT_EXTRACTING)));
    // registry.addWorkstation(MekanismEmiRecipeCategory.create(RecipeViewerRecipeType.PAINTING),
    // EmiStack.of(AdvancedFactoryBlocks.getAdvancedFactory(tier, AdvancedFactoryType.PAINTING)));
    // }
    // }
}
