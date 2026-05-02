package com.jerry.mekmm.client.recipe_viewer.jei.machine;

import com.jerry.mekmm.Mekmm;
import com.jerry.mekmm.api.recipes.basic.MMBasicItemStackChemicalToItemStackRecipe;
import com.jerry.mekmm.common.recipe.impl.ReplicatorIRecipeSingle;

import mekanism.api.SerializationConstants;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
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

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;

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
public class ReplicatorRecipeCategory extends BaseRecipeCategory<MMBasicItemStackChemicalToItemStackRecipe> {

    // TODO: Re-evaluate
    private static final Codec<MMBasicItemStackChemicalToItemStackRecipe> RECIPE_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStackIngredient.CODEC.fieldOf(SerializationConstants.INPUT).forGetter(MMBasicItemStackChemicalToItemStackRecipe::getItemInput),
            IngredientCreatorAccess.chemicalStack().codec().fieldOf(SerializationConstants.CHEMICAL_INPUT).forGetter(MMBasicItemStackChemicalToItemStackRecipe::getChemicalInput),
            ItemStack.CODEC.fieldOf(SerializationConstants.OUTPUT).forGetter(MMBasicItemStackChemicalToItemStackRecipe::getOutputRaw)).apply(instance, ReplicatorIRecipeSingle::new));

    private final GuiGauge<?> inputGauge;
    private final GuiSlot outputSlot;
    private final GuiSlot inputSlot;
    private final GuiSlot extra;

    public ReplicatorRecipeCategory(IGuiHelper helper, IRecipeViewerRecipeType<MMBasicItemStackChemicalToItemStackRecipe> recipeType) {
        super(helper, recipeType);
        GaugeType type1 = GaugeType.STANDARD.with(DataType.INPUT);
        inputGauge = addElement(GuiChemicalGauge.getDummy(type1, this, 7, 4));
        inputSlot = addSlot(SlotType.INPUT, 29, 32);
        outputSlot = addSlot(SlotType.OUTPUT, 131, 32);
        extra = addSlot(SlotType.EXTRA, 8, 65).with(SlotOverlay.MINUS);
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
    public void setRecipe(IRecipeLayoutBuilder builder, MMBasicItemStackChemicalToItemStackRecipe recipe, IFocusGroup focuses) {
        ContextMap slotDisplayContext = getSlotDisplayContext();
        initItem(builder, RecipeIngredientRole.INPUT, inputSlot, recipe.getItemInput().getRepresentations(slotDisplayContext));
        initChemical(builder, RecipeIngredientRole.INPUT, inputGauge, recipe.getChemicalInput().getRepresentations(slotDisplayContext));
        initItem(builder, RecipeIngredientRole.OUTPUT, outputSlot, recipe.getOutputDefinition());
        initItem(builder, RecipeIngredientRole.CRAFTING_STATION, extra, RecipeViewerUtils.getStacksFor(recipe.getChemicalInput(), true));
    }

    @Override
    public @Nullable Identifier getIdentifier(MMBasicItemStackChemicalToItemStackRecipe recipe) {
        List<@NotNull ItemStack> representations = recipe.getItemInput().getRepresentations(getSlotDisplayContext());
        if (representations.size() == 1) {
            Identifier itemId = BuiltInRegistries.ITEM.getKeyOrNull(representations.getFirst().getItem());
            if (itemId != null) {
                return RecipeViewerUtils.synthetic(itemId, "replicator", Mekmm.MOD_ID);
            }
        }
        return null;
    }

    @Override
    public Codec<MMBasicItemStackChemicalToItemStackRecipe> getCodec(ICodecHelper codecHelper, IRecipeManager recipeManager) {
        return RECIPE_CODEC;
    }
}
