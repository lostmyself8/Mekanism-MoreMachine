package com.jerry.mekmm.client.recipe_viewer.jei.machine;

import com.jerry.mekmm.Mekmm;
import com.jerry.mekmm.api.recipes.basic.BasicFluidChemicalToFluidRecipe;
import com.jerry.mekmm.common.recipe.impl.FluidReplicatorIRecipeSingle;

import mekanism.api.SerializationConstants;
import mekanism.api.recipes.ingredients.FluidStackIngredient;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.client.gui.element.gauge.*;
import mekanism.client.gui.element.progress.ProgressType;
import mekanism.client.gui.element.slot.GuiSlot;
import mekanism.client.gui.element.slot.SlotType;
import mekanism.client.recipe_viewer.RecipeViewerUtils;
import mekanism.client.recipe_viewer.jei.BaseRecipeCategory;
import mekanism.client.recipe_viewer.type.IRecipeViewerRecipeType;
import mekanism.common.inventory.container.slot.SlotOverlay;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.util.RegistryUtils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextMap;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidStackTemplate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.helpers.ICodecHelper;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.RecipeIngredientRole;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public class FluidReplicatorRecipeCategory extends BaseRecipeCategory<BasicFluidChemicalToFluidRecipe> {

    // TODO: Re-evaluate
    private static final Codec<BasicFluidChemicalToFluidRecipe> RECIPE_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            FluidStackIngredient.CODEC.fieldOf(SerializationConstants.INPUT).forGetter(BasicFluidChemicalToFluidRecipe::getFluidInput),
            IngredientCreatorAccess.chemicalStack().codec().fieldOf(SerializationConstants.CHEMICAL_INPUT).forGetter(BasicFluidChemicalToFluidRecipe::getChemicalInput),
            FluidStackTemplate.CODEC.fieldOf(SerializationConstants.OUTPUT).forGetter(BasicFluidChemicalToFluidRecipe::getOutputRaw)).apply(instance, FluidReplicatorIRecipeSingle::new));

    private final GuiGauge<?> gasInputGauge;
    private final GuiGauge<?> fluidInputGauge;
    private final GuiGauge<?> outputGauge;
    private final GuiSlot extra;

    public FluidReplicatorRecipeCategory(IGuiHelper helper, IRecipeViewerRecipeType<BasicFluidChemicalToFluidRecipe> recipeType) {
        super(helper, recipeType);
        GaugeType input = GaugeType.STANDARD.with(DataType.INPUT);
        GaugeType output = GaugeType.STANDARD.with(DataType.OUTPUT);
        gasInputGauge = addElement(GuiChemicalGauge.getDummy(input, this, 7, 4));
        fluidInputGauge = addElement(GuiFluidGauge.getDummy(input, this, 28, 4));
        outputGauge = addElement(GuiChemicalGauge.getDummy(output, this, 131, 4));
        extra = addSlot(SlotType.EXTRA, 8, 65).with(SlotOverlay.MINUS);
        addSlot(SlotType.INPUT, 29, 65).with(SlotOverlay.PLUS);
        addSlot(SlotType.OUTPUT, 132, 65).with(SlotOverlay.PLUS);
        addSlot(SlotType.POWER, 152, 65).with(SlotOverlay.POWER);
        addSimpleProgress(ProgressType.LARGE_RIGHT, 64, 36);
        addElement(new GuiEnergyGauge(new GuiEnergyGauge.IEnergyInfoHandler() {

            @Override
            public long getEnergy() {
                return 1L;
            }

            @Override
            public long getMaxEnergy() {
                return 1L;
            }
        }, GaugeType.STANDARD, this, 151, 4));
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, BasicFluidChemicalToFluidRecipe recipe, IFocusGroup focuses) {
        ContextMap slotDisplayContext = getSlotDisplayContext();
        initFluid(builder, RecipeIngredientRole.INPUT, fluidInputGauge, recipe.getFluidInput().getRepresentations(slotDisplayContext));
        initChemical(builder, RecipeIngredientRole.INPUT, gasInputGauge, recipe.getChemicalInput().getRepresentations(slotDisplayContext));
        initFluid(builder, RecipeIngredientRole.OUTPUT, outputGauge, recipe.getOutputDefinition());
        initItem(builder, RecipeIngredientRole.CRAFTING_STATION, extra, RecipeViewerUtils.getStacksFor(recipe.getChemicalInput(), true));
    }

    @Override
    public @Nullable Identifier getIdentifier(BasicFluidChemicalToFluidRecipe recipe) {
        List<@NotNull FluidStack> representations = recipe.getFluidInput().getRepresentations(getSlotDisplayContext());
        if (representations.size() == 1) {
            Identifier fluidId = BuiltInRegistries.FLUID.getKeyOrNull(representations.getFirst().getFluid());
            if (fluidId != null) {
                return RegistryUtils.synthetic(fluidId, "replicator", Mekmm.MOD_ID);
            }
        }
        return null;
    }

    @Override
    public Codec<BasicFluidChemicalToFluidRecipe> getCodec(ICodecHelper codecHelper, IRecipeManager recipeManager) {
        return RECIPE_CODEC;
    }
}
