package com.jerry.mekaf.common.tile.base;

import com.jerry.mekaf.common.upgrade.MergedToItemUpgradeData;

import mekanism.api.IContentsListener;
import mekanism.api.RelativeSide;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.chemical.infuse.IInfusionTank;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.merged.MergedChemicalTank;
import mekanism.api.chemical.pigment.IPigmentTank;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.ISlurryTank;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.api.inventory.IInventorySlot;
import mekanism.api.providers.IBlockProvider;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.api.recipes.outputs.IOutputHandler;
import mekanism.api.recipes.outputs.OutputHelper;
import mekanism.common.capabilities.holder.chemical.ChemicalTankHelper;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.inventory.slot.OutputInventorySlot;
import mekanism.common.inventory.warning.WarningTracker.WarningType;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.tile.component.ITileComponent;
import mekanism.common.tile.component.config.ConfigInfo;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.tile.component.config.slot.ChemicalSlotInfo;
import mekanism.common.upgrade.IUpgradeData;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public abstract class TileEntityMergedToItemFactory<RECIPE extends MekanismRecipe> extends TileEntityAdvancedFactoryBase<RECIPE> {

    protected MergedToItemProcessInfo[] processInfoSlots;
    protected OutputInventorySlot[] outputSlot;
    protected MergedChemicalTank[] inputTank;

    public final List<MergedChemicalTank> inputChemicalTanks;
    public final List<IInventorySlot> outputItemSlots;
    public final List<IGasTank> inputGasTanks;
    public final List<IInfusionTank> inputInfusionTanks;
    public final List<IPigmentTank> inputPigmentTanks;
    public final List<ISlurryTank> inputSlurryTanks;

    protected TileEntityMergedToItemFactory(IBlockProvider blockProvider, BlockPos pos, BlockState state, List<RecipeError> errorTypes, Set<RecipeError> globalErrorTypes) {
        super(blockProvider, pos, state, errorTypes, globalErrorTypes);
        outputItemSlots = new ArrayList<>();
        inputChemicalTanks = new ArrayList<>();

        processInfoSlots = new MergedToItemProcessInfo[tier.processes];
        for (int i = 0; i < tier.processes; i++) {
            processInfoSlots[i] = new MergedToItemProcessInfo(i, inputTank[i], outputSlot[i]);
        }

        for (MergedToItemProcessInfo info : processInfoSlots) {
            inputChemicalTanks.add(info.inputTank);
            outputItemSlots.add(info.outputSlot);
        }

        addSupported(TransmissionType.GAS, TransmissionType.INFUSION, TransmissionType.PIGMENT, TransmissionType.SLURRY);
        // 初始化其他储罐
        inputGasTanks = new ArrayList<>();
        inputInfusionTanks = new ArrayList<>();
        inputPigmentTanks = new ArrayList<>();
        inputSlurryTanks = new ArrayList<>();
        for (MergedChemicalTank tank : inputChemicalTanks) {
            inputGasTanks.add(tank.getGasTank());
            inputInfusionTanks.add(tank.getInfusionTank());
            inputPigmentTanks.add(tank.getPigmentTank());
            inputSlurryTanks.add(tank.getSlurryTank());
        }
        ConfigInfo gasConfig = configComponent.getConfig(TransmissionType.GAS);
        if (gasConfig != null) {
            gasConfig.addSlotInfo(DataType.INPUT, new ChemicalSlotInfo.GasSlotInfo(true, false, inputGasTanks));
            gasConfig.setDataType(DataType.INPUT, RelativeSide.RIGHT);
            gasConfig.fill(DataType.INPUT);
            gasConfig.setCanEject(false);
        }
        ConfigInfo infusionConfig = configComponent.getConfig(TransmissionType.INFUSION);
        if (infusionConfig != null) {
            infusionConfig.addSlotInfo(DataType.INPUT, new ChemicalSlotInfo.InfusionSlotInfo(true, false, inputInfusionTanks));
            infusionConfig.fill(DataType.INPUT);
            infusionConfig.setCanEject(false);
        }
        ConfigInfo pigmentConfig = configComponent.getConfig(TransmissionType.PIGMENT);
        if (pigmentConfig != null) {
            pigmentConfig.addSlotInfo(DataType.INPUT, new ChemicalSlotInfo.PigmentSlotInfo(true, false, inputPigmentTanks));
            pigmentConfig.setDataType(DataType.INPUT, RelativeSide.RIGHT);
            pigmentConfig.fill(DataType.INPUT);
            pigmentConfig.setCanEject(false);
        }
        ConfigInfo slurryConfig = configComponent.getConfig(TransmissionType.SLURRY);
        if (slurryConfig != null) {
            slurryConfig.addSlotInfo(DataType.INPUT, new ChemicalSlotInfo.SlurrySlotInfo(true, false, inputSlurryTanks));
            slurryConfig.setDataType(DataType.INPUT, RelativeSide.RIGHT);
            slurryConfig.fill(DataType.INPUT);
            slurryConfig.setCanEject(false);
        }
        configComponent.setupItemIOConfig(Collections.emptyList(), outputItemSlots, energySlot, false);
    }

    @Override
    protected void addGasTanks(ChemicalTankHelper<Gas, GasStack, IGasTank> builder, IContentsListener listener, IContentsListener updateSortingListener) {
        for (int i = 0; i < tier.processes; i++) {
            builder.addTank(inputTank[i].getGasTank());

        }
    }

    @Override
    protected void addInfusionTanks(ChemicalTankHelper<InfuseType, InfusionStack, IInfusionTank> builder, IContentsListener listener, IContentsListener updateSortingListener) {
        for (int i = 0; i < tier.processes; i++) {
            builder.addTank(inputTank[i].getInfusionTank());
        }
    }

    @Override
    protected void addPigmentTanks(ChemicalTankHelper<Pigment, PigmentStack, IPigmentTank> builder, IContentsListener listener, IContentsListener updateSortingListener) {
        for (int i = 0; i < tier.processes; i++) {
            builder.addTank(inputTank[i].getPigmentTank());
        }
    }

    @Override
    protected void addSlurryTanks(ChemicalTankHelper<Slurry, SlurryStack, ISlurryTank> builder, IContentsListener listener, IContentsListener updateSortingListener) {
        for (int i = 0; i < tier.processes; i++) {
            builder.addTank(inputTank[i].getSlurryTank());
        }
    }

    @Override
    protected void addSlots(InventorySlotHelper builder, IContentsListener listener, IContentsListener updateSortingListener) {
        outputSlot = new OutputInventorySlot[tier.processes];
        itemOutputHandlers = new IOutputHandler[tier.processes];
        for (int i = 0; i < tier.processes; i++) {
            outputSlot[i] = OutputInventorySlot.at(recipeCacheLookupMonitors[i], getXPos(i), 70);
            int index = i;
            builder.addSlot(outputSlot[i]).tracksWarnings(slot -> slot.warning(WarningType.NO_SPACE_IN_OUTPUT, getWarningCheck(RecipeError.NOT_ENOUGH_OUTPUT_SPACE, index)));
            itemOutputHandlers[i] = OutputHelper.getOutputHandler(outputSlot[i], RecipeError.NOT_ENOUGH_OUTPUT_SPACE);
        }
    }

    @Override
    public void parseUpgradeData(@NotNull IUpgradeData upgradeData) {
        if (upgradeData instanceof MergedToItemUpgradeData data) {
            redstone = data.redstone;
            setControlType(data.controlType);
            getEnergyContainer().setEnergy(data.energyContainer.getEnergy());
            sorting = data.sorting;
            energySlot.deserializeNBT(data.energySlot.serializeNBT());
            System.arraycopy(data.progress, 0, progress, 0, data.progress.length);
            for (int i = 0; i < data.outputSlots.size(); i++) {
                // Copy the stack using NBT so that if it is not actually valid due to a reload we don't crash
                outputItemSlots.get(i).deserializeNBT(data.outputSlots.get(i).serializeNBT());
            }
            for (int i = 0; i < data.inputTanks.size(); i++) {
                inputChemicalTanks.get(i).getGasTank().setStack(data.inputTanks.get(i).getGasTank().getStack());
                inputChemicalTanks.get(i).getInfusionTank().setStack(data.inputTanks.get(i).getInfusionTank().getStack());
                inputChemicalTanks.get(i).getPigmentTank().setStack(data.inputTanks.get(i).getPigmentTank().getStack());
                inputChemicalTanks.get(i).getSlurryTank().setStack(data.inputTanks.get(i).getSlurryTank().getStack());
            }
            for (ITileComponent component : getComponents()) {
                component.read(data.components);
            }
        } else {
            super.parseUpgradeData(upgradeData);
        }
    }

    @Override
    protected void sortInventoryOrTank() {}

    public record MergedToItemProcessInfo(int process, MergedChemicalTank inputTank, @NotNull IInventorySlot outputSlot) {}
}
