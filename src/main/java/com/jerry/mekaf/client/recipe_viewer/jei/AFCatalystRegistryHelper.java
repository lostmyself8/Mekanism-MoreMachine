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
            registry.addRecipeCatalyst(AdvancedFactoryBlocks.getAdvancedFactory(tier, AdvancedFactoryType.OXIDIZING), MekanismJEI.genericRecipeType(RecipeViewerRecipeType.OXIDIZING));
            registry.addRecipeCatalyst(AdvancedFactoryBlocks.getAdvancedFactory(tier, AdvancedFactoryType.DISSOLVING), MekanismJEI.genericRecipeType(RecipeViewerRecipeType.DISSOLUTION));
            registry.addRecipeCatalyst(AdvancedFactoryBlocks.getAdvancedFactory(tier, AdvancedFactoryType.WASHING), MekanismJEI.genericRecipeType(RecipeViewerRecipeType.WASHING));
            registry.addRecipeCatalyst(AdvancedFactoryBlocks.getAdvancedFactory(tier, AdvancedFactoryType.CRYSTALLIZING), MekanismJEI.genericRecipeType(RecipeViewerRecipeType.CRYSTALLIZING));
            registry.addRecipeCatalyst(AdvancedFactoryBlocks.getAdvancedFactory(tier, AdvancedFactoryType.PRESSURISED_REACTING), MekanismJEI.genericRecipeType(RecipeViewerRecipeType.REACTION));
            registry.addRecipeCatalyst(AdvancedFactoryBlocks.getAdvancedFactory(tier, AdvancedFactoryType.CENTRIFUGING), MekanismJEI.genericRecipeType(RecipeViewerRecipeType.CENTRIFUGING));
            registry.addRecipeCatalyst(AdvancedFactoryBlocks.getAdvancedFactory(tier, AdvancedFactoryType.LIQUIFYING), MekanismJEI.genericRecipeType(RecipeViewerRecipeType.NUTRITIONAL_LIQUIFICATION));
            registry.addRecipeCatalyst(AdvancedFactoryBlocks.getAdvancedFactory(tier, AdvancedFactoryType.PIGMENT_EXTRACTING), MekanismJEI.genericRecipeType(RecipeViewerRecipeType.PIGMENT_EXTRACTING));
            registry.addRecipeCatalyst(AdvancedFactoryBlocks.getAdvancedFactory(tier, AdvancedFactoryType.PAINTING), MekanismJEI.genericRecipeType(RecipeViewerRecipeType.PAINTING));
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
