package com.jerry.mekaf.common.tile;

import com.jerry.mekaf.common.tile.base.TileEntityItemToItemAdvancedFactory;
import com.jerry.mekaf.common.upgrade.ItemPigmentToItemUpgradeData;

import mekanism.api.IContentsListener;
import mekanism.api.RelativeSide;
import mekanism.api.chemical.ChemicalTankBuilder;
import mekanism.api.chemical.pigment.IPigmentTank;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.inventory.IInventorySlot;
import mekanism.api.math.MathUtils;
import mekanism.api.providers.IBlockProvider;
import mekanism.api.recipes.PaintingRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.api.recipes.cache.TwoInputCachedRecipe;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.inputs.InputHelper;
import mekanism.common.Mekanism;
import mekanism.common.capabilities.holder.chemical.ChemicalTankHelper;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod;
import mekanism.common.inventory.slot.chemical.PigmentInventorySlot;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.recipe.lookup.IDoubleRecipeLookupHandler.ItemChemicalRecipeLookupHandler;
import mekanism.common.recipe.lookup.cache.InputRecipeCache.ItemChemical;
import mekanism.common.tile.component.TileComponentEjector;
import mekanism.common.tile.component.config.ConfigInfo;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.tile.component.config.slot.InventorySlotInfo;
import mekanism.common.tile.interfaces.IHasDumpButton;
import mekanism.common.upgrade.IUpgradeData;
import mekanism.common.util.InventoryUtils;
import mekanism.common.util.MekanismUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class TileEntityPaintingFactory extends TileEntityItemToItemAdvancedFactory<PaintingRecipe> implements ItemChemicalRecipeLookupHandler<Pigment, PigmentStack, PaintingRecipe>, IHasDumpButton {

    private static final List<RecipeError> TRACKED_ERROR_TYPES = List.of(
            RecipeError.NOT_ENOUGH_ENERGY,
            RecipeError.NOT_ENOUGH_INPUT,
            RecipeError.NOT_ENOUGH_SECONDARY_INPUT,
            RecipeError.NOT_ENOUGH_OUTPUT_SPACE,
            RecipeError.INPUT_DOESNT_PRODUCE_OUTPUT);
    private static final Set<RecipeError> GLOBAL_ERROR_TYPES = Set.of(
            RecipeError.NOT_ENOUGH_ENERGY,
            RecipeError.NOT_ENOUGH_SECONDARY_INPUT);
    public static final long MAX_PIGMENT = 15_000;

    @WrappingComputerMethod(wrapper = SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.class,
                            methodNames = { "getPigmentInput", "getPigmentInputCapacity", "getPigmentInputNeeded",
                                    "getPigmentInputFilledPercentage" },
                            docPlaceholder = "pigment tank")
    public IPigmentTank pigmentTank;

    private final IInputHandler<@NotNull PigmentStack> pigmentInputHandler;

    @WrappingComputerMethod(wrapper = SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.class, methodNames = "getInputPigmentItem", docPlaceholder = "pigment slot")
    PigmentInventorySlot pigmentInputSlot;

    public TileEntityPaintingFactory(IBlockProvider blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state, TRACKED_ERROR_TYPES, GLOBAL_ERROR_TYPES);

        addSupported(TransmissionType.PIGMENT);
        ConfigInfo itemConfig = configComponent.getConfig(TransmissionType.ITEM);
        if (itemConfig != null) {
            itemConfig.addSlotInfo(DataType.EXTRA, new InventorySlotInfo(true, true, pigmentInputSlot));
            itemConfig.setDataType(DataType.EXTRA, RelativeSide.BOTTOM);
        }
        configComponent.setupInputConfig(TransmissionType.PIGMENT, pigmentTank);

        ejectorComponent = new TileComponentEjector(this);
        ejectorComponent.setOutputData(configComponent, TransmissionType.ITEM);

        pigmentInputHandler = InputHelper.getInputHandler(pigmentTank, RecipeError.NOT_ENOUGH_SECONDARY_INPUT);
    }

    @Override
    protected void addPigmentTanks(ChemicalTankHelper<Pigment, PigmentStack, IPigmentTank> builder, IContentsListener listener, IContentsListener updateSortingListener) {
        builder.addTank(pigmentTank = ChemicalTankBuilder.PIGMENT.input(MAX_PIGMENT * tier.processes, this::containsRecipeB, markAllMonitorsChanged(listener)));
    }

    @Override
    protected void addSlots(InventorySlotHelper builder, IContentsListener listener, IContentsListener updateSortingListener) {
        super.addSlots(builder, listener, updateSortingListener);
        builder.addSlot(pigmentInputSlot = PigmentInventorySlot.fill(pigmentTank, listener, 6, 56));
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        pigmentInputSlot.fillTankOrConvert();
    }

    @Override
    protected boolean isCachedRecipeValid(@Nullable CachedRecipe<PaintingRecipe> cached, @NotNull ItemStack stack) {
        return cached != null && cached.getRecipe().getItemInput().testType(stack);
    }

    @Override
    protected @Nullable PaintingRecipe findRecipe(int process, @NotNull ItemStack fallbackInput, @NotNull IInventorySlot outputSlot) {
        PigmentStack stored = pigmentTank.getStack();
        ItemStack output = outputSlot.getStack();
        return getRecipeType().getInputCache().findTypeBasedRecipe(level, fallbackInput, stored,
                recipe -> InventoryUtils.areItemsStackable(recipe.getOutput(fallbackInput, stored), output));
    }

    @Override
    public boolean isValidInputItem(@NotNull ItemStack stack) {
        return containsRecipeA(stack);
    }

    @Override
    protected int getNeededInput(PaintingRecipe recipe, ItemStack inputStack) {
        return MathUtils.clampToInt(recipe.getItemInput().getNeededAmount(inputStack));
    }

    @Override
    public @NotNull IMekanismRecipeTypeProvider<PaintingRecipe, ItemChemical<Pigment, PigmentStack, PaintingRecipe>> getRecipeType() {
        return MekanismRecipeType.PAINTING;
    }

    @Override
    public @Nullable PaintingRecipe getRecipe(int cacheIndex) {
        return findFirstRecipe(itemInputHandlers[cacheIndex], pigmentInputHandler);
    }

    @Override
    public @NotNull CachedRecipe<PaintingRecipe> createNewCachedRecipe(@NotNull PaintingRecipe recipe, int cacheIndex) {
        return TwoInputCachedRecipe.itemChemicalToItem(recipe, recheckAllRecipeErrors[cacheIndex], itemInputHandlers[cacheIndex], pigmentInputHandler, itemOutputHandlers[cacheIndex])
                .setErrorsChanged(errors -> errorTracker.onErrorsChanged(errors, cacheIndex))
                .setCanHolderFunction(() -> MekanismUtils.canFunction(this))
                .setActive(active -> setActiveState(active, cacheIndex))
                .setEnergyRequirements(energyContainer::getEnergyPerTick, energyContainer)
                .setRequiredTicks(this::getTicksRequired)
                .setOnFinish(this::markForSave)
                .setOperatingTicksChanged(operatingTicks -> progress[cacheIndex] = operatingTicks);
    }

    @Override
    public boolean hasExtrasResourceBar() {
        return true;
    }

    @Override
    public void parseUpgradeData(@NotNull IUpgradeData upgradeData) {
        if (upgradeData instanceof ItemPigmentToItemUpgradeData data) {
            super.parseUpgradeData(upgradeData);
            pigmentTank.deserializeNBT(data.stored.serializeNBT());
            pigmentInputSlot.deserializeNBT(data.pigmentSlot.serializeNBT());
        } else {
            Mekanism.logger.warn("Unhandled upgrade data.", new Throwable());
        }
    }

    @Override
    public @Nullable ItemPigmentToItemUpgradeData getUpgradeData() {
        return new ItemPigmentToItemUpgradeData(redstone, getControlType(), getEnergyContainer(),
                progress, pigmentTank, pigmentInputSlot, energySlot, inputSlots, outputSlots, isSorting(), getComponents());
    }

    @Override
    public void dump() {
        pigmentTank.setEmpty();
    }
}
