package com.jerry.mekmm.client.recipe_viewer.jei.machine;

import com.jerry.mekmm.Mekmm;
import com.jerry.mekmm.api.recipes.basic.MMBasicChemicalChemicalToChemicalRecipe;
import com.jerry.mekmm.common.recipe.impl.ChemicalReplicatorIRecipeSingle;

import mekanism.api.MekanismAPI;
import mekanism.api.SerializationConstants;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.client.gui.element.gauge.GaugeType;
import mekanism.client.gui.element.gauge.GuiChemicalGauge;
import mekanism.client.gui.element.gauge.GuiEnergyGauge;
import mekanism.client.gui.element.gauge.GuiGauge;
import mekanism.client.gui.element.progress.ProgressType;
import mekanism.client.gui.element.slot.GuiSlot;
import mekanism.client.gui.element.slot.SlotType;
import mekanism.client.recipe_viewer.RecipeViewerUtils;
import mekanism.client.recipe_viewer.jei.BaseRecipeCategory;
import mekanism.client.recipe_viewer.type.IRecipeViewerRecipeType;
import mekanism.common.inventory.container.slot.SlotOverlay;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.util.RegistryUtils;

import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextMap;

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

import java.util.List;

@NothingNullByDefault
public class ChemicalReplicatorRecipeCategory extends BaseRecipeCategory<MMBasicChemicalChemicalToChemicalRecipe> {

    // TODO: Re-evaluate
    private static final Codec<MMBasicChemicalChemicalToChemicalRecipe> RECIPE_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ChemicalStackIngredient.CODEC.fieldOf(SerializationConstants.INPUT).forGetter(MMBasicChemicalChemicalToChemicalRecipe::getLeftInput),
            IngredientCreatorAccess.chemicalStack().codec().fieldOf(SerializationConstants.CHEMICAL_INPUT).forGetter(MMBasicChemicalChemicalToChemicalRecipe::getRightInput),
            ChemicalStack.CODEC.fieldOf(SerializationConstants.OUTPUT).forGetter(MMBasicChemicalChemicalToChemicalRecipe::getOutputRaw)).apply(instance, ChemicalReplicatorIRecipeSingle::new));

    private final GuiGauge<?> secondaryGauge;
    private final GuiGauge<?> outputGauge;
    private final GuiGauge<?> firstGauge;
    private final GuiSlot extra;

    public ChemicalReplicatorRecipeCategory(IGuiHelper helper, IRecipeViewerRecipeType<MMBasicChemicalChemicalToChemicalRecipe> recipeType) {
        super(helper, recipeType);
        GaugeType input_1 = GaugeType.STANDARD.with(DataType.INPUT_1);
        GaugeType input_2 = GaugeType.STANDARD.with(DataType.INPUT_2);
        GaugeType output = GaugeType.STANDARD.with(DataType.OUTPUT);
        secondaryGauge = addElement(GuiChemicalGauge.getDummy(input_2, this, 7, 4));
        firstGauge = addElement(GuiChemicalGauge.getDummy(input_1, this, 28, 4));
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
    public void setRecipe(IRecipeLayoutBuilder builder, MMBasicChemicalChemicalToChemicalRecipe recipe, IFocusGroup focuses) {
        ContextMap slotDisplayContext = getSlotDisplayContext();
        initChemical(builder, RecipeIngredientRole.INPUT, firstGauge, recipe.getLeftInput().getRepresentations(slotDisplayContext));
        initChemical(builder, RecipeIngredientRole.INPUT, secondaryGauge, recipe.getRightInput().getRepresentations(slotDisplayContext));
        initChemical(builder, RecipeIngredientRole.OUTPUT, outputGauge, recipe.getOutputDefinition());
        initItem(builder, RecipeIngredientRole.CRAFTING_STATION, extra, RecipeViewerUtils.getStacksFor(recipe.getRightInput(), true));
    }

    @Override
    public @Nullable Identifier getIdentifier(MMBasicChemicalChemicalToChemicalRecipe recipe) {
        List<@NotNull ChemicalStack> representations = recipe.getLeftInput().getRepresentations(getSlotDisplayContext());
        if (representations.size() == 1) {
            Identifier chemicalId = MekanismAPI.CHEMICAL_REGISTRY.getKey(representations.getFirst().getChemical());
            return RegistryUtils.synthetic(chemicalId, "replicator", Mekmm.MOD_ID);
        }
        return null;
    }

    @Override
    public Codec<MMBasicChemicalChemicalToChemicalRecipe> getCodec(ICodecHelper codecHelper, IRecipeManager recipeManager) {
        return RECIPE_CODEC;
    }
}
