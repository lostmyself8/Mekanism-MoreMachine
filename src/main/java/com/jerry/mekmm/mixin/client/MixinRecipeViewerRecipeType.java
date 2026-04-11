package com.jerry.mekmm.mixin.client;

import com.jerry.meklm.common.registries.LargeMachineBlocks;

import com.jerry.mekmm.common.registries.MoreMachineBlocks;

import mekanism.api.recipes.*;
import mekanism.client.recipe_viewer.type.RVRecipeTypeWrapper;
import mekanism.client.recipe_viewer.type.RecipeViewerRecipeType;
import mekanism.client.recipe_viewer.type.SimpleRVRecipeType;
import mekanism.common.MekanismLang;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.util.MekanismUtils;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RecipeViewerRecipeType.class, remap = false)
public abstract class MixinRecipeViewerRecipeType {

    @Final
    @Shadow
    @Mutable
    public static RVRecipeTypeWrapper<?, ChemicalChemicalToChemicalRecipe, ?> CHEMICAL_INFUSING;

    @Final
    @Shadow
    @Mutable
    public static RVRecipeTypeWrapper<?, ElectrolysisRecipe, ?> SEPARATING;

    @Final
    @Shadow
    @Mutable
    public static RVRecipeTypeWrapper<?, ChemicalToChemicalRecipe, ?> ACTIVATING;

    @Final
    @Shadow
    @Mutable
    public static RVRecipeTypeWrapper<?, NucleosynthesizingRecipe, ?> NUCLEOSYNTHESIZING;

    @Final
    @Shadow
    @Mutable
    public static SimpleRVRecipeType<?, ItemStackToChemicalRecipe, ?> CHEMICAL_CONVERSION;

    @Final
    @Shadow
    @Mutable
    public static RVRecipeTypeWrapper<?, ChemicalChemicalToChemicalRecipe, ?> PIGMENT_MIXING;

    /**
     * 使JEI侧栏显示种植机和复制机等机器（给化学品的物品）
     * 往mekanism的“CHEMICAL_CONVERSION”中添加新的workstations
     */
    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void modifyEnergyConversion(CallbackInfo ci) {
        CHEMICAL_INFUSING = new RVRecipeTypeWrapper<>(MekanismRecipeType.CHEMICAL_INFUSING, ChemicalChemicalToChemicalRecipe.class, -3, -3, 170, 80, MekanismBlocks.CHEMICAL_INFUSER, LargeMachineBlocks.LARGE_CHEMICAL_INFUSER);

        SEPARATING = new RVRecipeTypeWrapper<>(MekanismRecipeType.SEPARATING, ElectrolysisRecipe.class, -4, -9, 167, 62, MekanismBlocks.ELECTROLYTIC_SEPARATOR, LargeMachineBlocks.LARGE_ELECTROLYTIC_SEPARATOR);

        ACTIVATING = new RVRecipeTypeWrapper<>(MekanismRecipeType.ACTIVATING, ChemicalToChemicalRecipe.class, -4, -13, 168, 60, MekanismBlocks.SOLAR_NEUTRON_ACTIVATOR, LargeMachineBlocks.LARGE_SOLAR_NEUTRON_ACTIVATOR);

        NUCLEOSYNTHESIZING = new RVRecipeTypeWrapper<>(MekanismRecipeType.NUCLEOSYNTHESIZING, NucleosynthesizingRecipe.class, -6, -18, 182, 80, MekanismBlocks.ANTIPROTONIC_NUCLEOSYNTHESIZER, LargeMachineBlocks.LARGE_ANTIPROTONIC_NUCLEOSYNTHESIZER);

        CHEMICAL_CONVERSION = new SimpleRVRecipeType<>(MekanismRecipeType.CHEMICAL_CONVERSION, ItemStackToChemicalRecipe.class, MekanismLang.CONVERSION_CHEMICAL, MekanismUtils.getResource(MekanismUtils.ResourceType.GUI, "chemicals.png"), -20, -12, 132, 62,
                MekanismBlocks.PURIFICATION_CHAMBER, MekanismBlocks.METALLURGIC_INFUSER, MekanismBlocks.OSMIUM_COMPRESSOR, MekanismBlocks.CHEMICAL_INJECTION_CHAMBER, MekanismBlocks.CHEMICAL_DISSOLUTION_CHAMBER, MekanismBlocks.ANTIPROTONIC_NUCLEOSYNTHESIZER,
                // 这里的顺序会影响显示效果，这很奇怪
                MoreMachineBlocks.REPLICATOR, MoreMachineBlocks.PLANTING_STATION);

        PIGMENT_MIXING = new RVRecipeTypeWrapper<>(MekanismRecipeType.PIGMENT_MIXING, ChemicalChemicalToChemicalRecipe.class, -3, -3, 170, 80, MekanismBlocks.PIGMENT_MIXER, LargeMachineBlocks.LARGE_PIGMENT_MIXER);
    }
}
