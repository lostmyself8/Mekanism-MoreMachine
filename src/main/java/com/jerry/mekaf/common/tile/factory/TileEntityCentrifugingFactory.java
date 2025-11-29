package com.jerry.mekaf.common.tile.factory;

import com.jerry.mekaf.common.upgrade.ChemicalToChemicalUpgradeData;

import mekanism.api.IContentsListener;
import mekanism.api.RelativeSide;
import mekanism.api.Upgrade;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.math.MathUtils;
import mekanism.api.recipes.ChemicalToChemicalRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.api.recipes.cache.OneInputCachedRecipe;
import mekanism.client.recipe_viewer.type.IRecipeViewerRecipeType;
import mekanism.client.recipe_viewer.type.RecipeViewerRecipeType;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.recipe.lookup.ISingleRecipeLookupHandler.ChemicalRecipeLookupHandler;
import mekanism.common.recipe.lookup.cache.InputRecipeCache.SingleChemical;
import mekanism.common.tile.component.TileComponentEjector;
import mekanism.common.tile.component.config.ConfigInfo;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.tile.component.config.slot.ChemicalSlotInfo;
import mekanism.common.tile.interfaces.IBoundingBlock;
import mekanism.common.upgrade.IUpgradeData;
import mekanism.common.util.UpgradeUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.TriPredicate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class TileEntityCentrifugingFactory extends TileEntityChemicalToChemicalFactory<ChemicalToChemicalRecipe> implements IBoundingBlock, ChemicalRecipeLookupHandler<ChemicalToChemicalRecipe> {

    protected static final TriPredicate<ChemicalToChemicalRecipe, ChemicalStack, ChemicalStack> OUTPUT_CHECK = (recipe, input, output) -> ChemicalStack.isSameChemical(recipe.getOutput(input), output);
    private static final List<RecipeError> TRACKED_ERROR_TYPES = List.of(
            RecipeError.NOT_ENOUGH_ENERGY,
            RecipeError.NOT_ENOUGH_ENERGY_REDUCED_RATE,
            RecipeError.NOT_ENOUGH_INPUT,
            RecipeError.NOT_ENOUGH_OUTPUT_SPACE,
            RecipeError.INPUT_DOESNT_PRODUCE_OUTPUT);
    private static final Set<RecipeError> GLOBAL_ERROR_TYPES = Set.of(RecipeError.NOT_ENOUGH_ENERGY);

    public TileEntityCentrifugingFactory(Holder<Block> blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state, TRACKED_ERROR_TYPES, GLOBAL_ERROR_TYPES);
        ConfigInfo config = configComponent.getConfig(TransmissionType.CHEMICAL);
        if (config != null) {
            config.addSlotInfo(DataType.INPUT, new ChemicalSlotInfo(true, false, inputChemicalTanks));
            List<IChemicalTank> ioTank = outputChemicalTanks;
            ioTank.addAll(inputChemicalTanks);
            config.addSlotInfo(DataType.INPUT_OUTPUT, new ChemicalSlotInfo(true, true, ioTank));
        }
        configComponent.addDisabledSides(RelativeSide.TOP);

        ejectorComponent = new TileComponentEjector(this);
        ejectorComponent.setOutputData(configComponent, TransmissionType.ITEM, TransmissionType.CHEMICAL)
                .setCanTankEject(tank -> !inputChemicalTanks.contains(tank));
    }

    @Override
    protected void addSlots(InventorySlotHelper builder, IContentsListener listener, IContentsListener updateSortingListener) {}

    @Override
    protected boolean isCachedRecipeValid(@Nullable CachedRecipe<ChemicalToChemicalRecipe> cached, @NotNull ChemicalStack stack) {
        return cached != null && cached.getRecipe().getInput().testType(stack);
    }

    @Override
    protected @Nullable ChemicalToChemicalRecipe findRecipe(int process, @NotNull ChemicalStack fallbackInput, @NotNull IChemicalTank outputSlot) {
        return getRecipeType().getInputCache().findTypeBasedRecipe(level, fallbackInput, outputSlot.getStack(), OUTPUT_CHECK);
    }

    @Override
    public boolean isChemicalValidForTank(@NotNull ChemicalStack stack) {
        return containsRecipe(stack);
    }

    @Override
    public boolean isValidInputChemical(@NotNull ChemicalStack stack) {
        return containsRecipe(stack);
    }

    @Override
    protected int getNeededInput(ChemicalToChemicalRecipe recipe, ChemicalStack inputStack) {
        return MathUtils.clampToInt(recipe.getInput().getNeededAmount(inputStack));
    }

    @Override
    public @NotNull IMekanismRecipeTypeProvider<?, ChemicalToChemicalRecipe, SingleChemical<ChemicalToChemicalRecipe>> getRecipeType() {
        return MekanismRecipeType.CENTRIFUGING;
    }

    @Override
    public @Nullable IRecipeViewerRecipeType<ChemicalToChemicalRecipe> recipeViewerType() {
        return RecipeViewerRecipeType.CENTRIFUGING;
    }

    @Override
    public @Nullable ChemicalToChemicalRecipe getRecipe(int cacheIndex) {
        return findFirstRecipe(chemicalInputHandlers[cacheIndex]);
    }

    @Override
    public @NotNull CachedRecipe<ChemicalToChemicalRecipe> createNewCachedRecipe(@NotNull ChemicalToChemicalRecipe recipe, int cacheIndex) {
        return OneInputCachedRecipe.chemicalToChemical(recipe, recheckAllRecipeErrors[cacheIndex], chemicalInputHandlers[cacheIndex], chemicalOutputHandlers[cacheIndex])
                .setErrorsChanged(errors -> errorTracker.onErrorsChanged(errors, cacheIndex))
                .setCanHolderFunction(this::canFunction)
                // 一定要更改这里，不然会导致能量显示错误
                .setActive(active -> setActiveState(active, cacheIndex))
                .setOnFinish(this::markForSave)
                .setEnergyRequirements(energyContainer::getEnergyPerTick, energyContainer)
                .setBaselineMaxOperations(() -> baselineMaxOperations);
    }

    @Override
    public void recalculateUpgrades(Upgrade upgrade) {
        super.recalculateUpgrades(upgrade);
        if (upgrade == Upgrade.SPEED) {
            baselineMaxOperations = (int) Math.pow(2, upgradeComponent.getUpgrades(Upgrade.SPEED));
        }
    }

    // 更改加速升级的显示的，默认是10x，气体工厂是256x，当然只有速度升级需要更改
    @NotNull
    @Override
    public List<Component> getInfo(@NotNull Upgrade upgrade) {
        return upgrade == Upgrade.SPEED ? UpgradeUtils.getExpScaledInfo(this, upgrade) : super.getInfo(upgrade);
    }

    @Override
    public @Nullable IUpgradeData getUpgradeData(HolderLookup.Provider provider) {
        return new ChemicalToChemicalUpgradeData(provider, redstone, getControlType(), getEnergyContainer(),
                progress, energySlot, inputChemicalTanks, outputChemicalTanks, isSorting(), getComponents());
    }
}
