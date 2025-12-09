package com.jerry.meklm.common.tile.prefab;

import mekanism.api.*;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.chemical.infuse.IInfusionTank;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.merged.MergedChemicalTank;
import mekanism.api.chemical.merged.MergedChemicalTank.Current;
import mekanism.api.chemical.pigment.IPigmentTank;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.ISlurryTank;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.api.math.MathUtils;
import mekanism.api.providers.IBlockProvider;
import mekanism.common.capabilities.holder.chemical.IChemicalTankHolder;
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.config.MekanismConfig;
import mekanism.common.integration.computer.ComputerException;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerChemicalTankWrapper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.integration.computer.annotation.SyntheticComputerMethod;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.inventory.container.slot.ContainerSlotType;
import mekanism.common.inventory.container.slot.SlotOverlay;
import mekanism.common.inventory.container.sync.SyncableEnum;
import mekanism.common.inventory.slot.chemical.MergedChemicalInventorySlot;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.tile.TileEntityChemicalTank.GasMode;
import mekanism.common.tile.base.SubstanceType;
import mekanism.common.tile.component.ITileComponent;
import mekanism.common.tile.component.TileComponentConfig;
import mekanism.common.tile.component.TileComponentEjector;
import mekanism.common.tile.component.config.ConfigInfo;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.tile.component.config.slot.ChemicalSlotInfo;
import mekanism.common.tile.component.config.slot.InventorySlotInfo;
import mekanism.common.tile.interfaces.IBoundingBlock;
import mekanism.common.tile.interfaces.IHasGasMode;
import mekanism.common.tile.interfaces.ISustainedData;
import mekanism.common.tile.prefab.TileEntityConfigurableMachine;
import mekanism.common.upgrade.ChemicalTankUpgradeData;
import mekanism.common.upgrade.IUpgradeData;
import mekanism.common.util.*;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import com.jerry.meklm.api.INeedConfig;
import com.jerry.meklm.common.capabilities.holder.chemical.CanAdjustChemicalTankHelper;
import com.jerry.meklm.common.tier.ILargeTankTier;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public abstract class TileEntityLargeChemicalTank<TIER extends ILargeTankTier> extends TileEntityConfigurableMachine implements ISustainedData, IHasGasMode, IBoundingBlock, INeedConfig {

    @SyntheticComputerMethod(getter = "getDumpingMode", getterDescription = "Get the current Dumping configuration")
    public GasMode dumping = GasMode.IDLE;

    @Getter
    protected MergedChemicalTank chemicalTank;
    @Getter
    protected TIER tier;

    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getDrainItem", docPlaceholder = "drain slot")
    MergedChemicalInventorySlot<MergedChemicalTank> drainSlot;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getFillItem", docPlaceholder = "fill slot")
    MergedChemicalInventorySlot<MergedChemicalTank> fillSlot;

    protected TileEntityLargeChemicalTank(IBlockProvider blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state);
        // 这里仅设置主方块的配置
        configComponent = new TileComponentConfig(this, TransmissionType.GAS, TransmissionType.INFUSION, TransmissionType.PIGMENT, TransmissionType.SLURRY,
                TransmissionType.ITEM);
        ConfigInfo itemConfig = configComponent.getConfig(TransmissionType.ITEM);
        if (itemConfig != null) {
            // 设置输入输出槽
            itemConfig.addSlotInfo(DataType.INPUT, new InventorySlotInfo(true, false, drainSlot));
            itemConfig.addSlotInfo(DataType.OUTPUT, new InventorySlotInfo(false, true, fillSlot));
            // 只需要背后输出，输入被偏移处理
            itemConfig.setDataType(DataType.OUTPUT, RelativeSide.BACK);
            // 设置自动弹出
            itemConfig.setCanEject(false);
        }
        // 化学品是背后输入，输出和自动弹出被偏移处理
        ConfigInfo gasConfig = configComponent.getConfig(TransmissionType.GAS);
        if (gasConfig != null) {
            gasConfig.addSlotInfo(DataType.INPUT, new ChemicalSlotInfo.GasSlotInfo(true, false, getGasTank()));
            gasConfig.setDataType(DataType.INPUT, RelativeSide.BACK);
            gasConfig.setCanEject(false);
        }
        ConfigInfo infusionConfig = configComponent.getConfig(TransmissionType.INFUSION);
        if (infusionConfig != null) {
            infusionConfig.addSlotInfo(DataType.INPUT, new ChemicalSlotInfo.InfusionSlotInfo(true, false, getInfusionTank()));
            infusionConfig.setDataType(DataType.INPUT, RelativeSide.BACK);
            infusionConfig.setCanEject(false);
        }
        ConfigInfo pigmentConfig = configComponent.getConfig(TransmissionType.PIGMENT);
        if (pigmentConfig != null) {
            pigmentConfig.addSlotInfo(DataType.INPUT, new ChemicalSlotInfo.PigmentSlotInfo(true, false, getPigmentTank()));
            pigmentConfig.setDataType(DataType.INPUT, RelativeSide.BACK);
            pigmentConfig.setCanEject(false);
        }
        ConfigInfo slurryConfig = configComponent.getConfig(TransmissionType.SLURRY);
        if (slurryConfig != null) {
            slurryConfig.addSlotInfo(DataType.INPUT, new ChemicalSlotInfo.SlurrySlotInfo(true, false, getSlurryTank()));
            slurryConfig.setDataType(DataType.INPUT, RelativeSide.BACK);
            slurryConfig.setCanEject(false);
        }
        ejectorComponent = new TileComponentEjector(this, () -> tier.getOutput());
        ejectorComponent.setOutputData(configComponent, TransmissionType.GAS, TransmissionType.INFUSION, TransmissionType.PIGMENT, TransmissionType.SLURRY)
                .setCanEject(type -> MekanismUtils.canFunction(this) && dumping != GasMode.DUMPING);
    }

    // 限制化学品只能从背后输入，顶部输出
    @NotNull
    @Override
    public IChemicalTankHolder<Gas, GasStack, IGasTank> getInitialGasTanks(IContentsListener listener) {
        CanAdjustChemicalTankHelper<Gas, GasStack, IGasTank> builder = CanAdjustChemicalTankHelper.forSide(this::getDirection, side -> side == RelativeSide.BACK, side -> side == RelativeSide.TOP);
        builder.addTank(getGasTank(), RelativeSide.TOP, RelativeSide.BACK);
        return builder.build();
    }

    @NotNull
    @Override
    public IChemicalTankHolder<InfuseType, InfusionStack, IInfusionTank> getInitialInfusionTanks(IContentsListener listener) {
        CanAdjustChemicalTankHelper<InfuseType, InfusionStack, IInfusionTank> builder = CanAdjustChemicalTankHelper.forSide(this::getDirection, side -> side == RelativeSide.BACK, side -> side == RelativeSide.TOP);
        builder.addTank(getInfusionTank(), RelativeSide.TOP, RelativeSide.BACK);
        return builder.build();
    }

    @NotNull
    @Override
    public IChemicalTankHolder<Pigment, PigmentStack, IPigmentTank> getInitialPigmentTanks(IContentsListener listener) {
        CanAdjustChemicalTankHelper<Pigment, PigmentStack, IPigmentTank> builder = CanAdjustChemicalTankHelper.forSide(this::getDirection, side -> side == RelativeSide.BACK, side -> side == RelativeSide.TOP);
        builder.addTank(getPigmentTank(), RelativeSide.TOP, RelativeSide.BACK);
        return builder.build();
    }

    @NotNull
    @Override
    public IChemicalTankHolder<Slurry, SlurryStack, ISlurryTank> getInitialSlurryTanks(IContentsListener listener) {
        CanAdjustChemicalTankHelper<Slurry, SlurryStack, ISlurryTank> builder = CanAdjustChemicalTankHelper.forSide(this::getDirection, side -> side == RelativeSide.BACK, side -> side == RelativeSide.TOP);
        builder.addTank(getSlurryTank(), RelativeSide.TOP, RelativeSide.BACK);
        return builder.build();
    }

    // 限制物品只能从顶部输入，背后输出
    @NotNull
    @Override
    protected IInventorySlotHolder getInitialInventory(IContentsListener listener) {
        InventorySlotHelper builder = InventorySlotHelper.forSide(this::getDirection, side -> side == RelativeSide.TOP, side -> side == RelativeSide.BACK);
        builder.addSlot(drainSlot = MergedChemicalInventorySlot.drain(chemicalTank, listener, 16, 16), RelativeSide.TOP, RelativeSide.BACK);
        builder.addSlot(fillSlot = MergedChemicalInventorySlot.fill(chemicalTank, listener, 16, 48), RelativeSide.TOP, RelativeSide.BACK);
        drainSlot.setSlotType(ContainerSlotType.OUTPUT);
        drainSlot.setSlotOverlay(SlotOverlay.PLUS);
        fillSlot.setSlotType(ContainerSlotType.INPUT);
        fillSlot.setSlotOverlay(SlotOverlay.MINUS);
        return builder.build();
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        drainSlot.drainChemicalTanks();
        fillSlot.fillChemicalTanks();
        if (dumping != GasMode.IDLE) {
            Current current = chemicalTank.getCurrent();
            if (current != Current.EMPTY) {
                IChemicalTank<?, ?> currentTank = chemicalTank.getTankFromCurrent(current);
                if (dumping == GasMode.DUMPING) {
                    currentTank.shrinkStack(tier.getStorage() / 400, Action.EXECUTE);
                } else {// dumping == GasMode.DUMPING_EXCESS
                    long target = MathUtils.clampToLong(currentTank.getCapacity() * MekanismConfig.general.dumpExcessKeepRatio.get());
                    long stored = currentTank.getStored();
                    if (target < stored) {
                        // Dump excess that we need to get to the target (capping at our eject rate for how much we can
                        // dump at once)
                        currentTank.shrinkStack(Math.min(stored - target, tier.getOutput()), Action.EXECUTE);
                    }
                }
            }
        }
        handleEject();
    }

    protected void handleEject() {
        if (MekanismUtils.canFunction(this)) {
            Set<Direction> emitDirections = EnumSet.noneOf(Direction.class);
            // 大型储罐默认顶部弹出，无论多高的顶部（应该没有更好的弹出位置了），如果你需要自定义的方向，可以在github上提出建议
            emitDirections.add(RelativeSide.TOP.getDirection(getDirection()));
            // 一个储罐只能同时存在一种化学品，因此不需要每次都检查四种类型，根据存储的类型弹出相应的化学品即可
            switch (getChemicalTank().getCurrent()) {
                case GAS -> ChemicalUtil.emit(emitDirections, getChemicalTank().getGasTank(), fromTile(), tier.getOutput());
                case INFUSION -> ChemicalUtil.emit(emitDirections, getChemicalTank().getInfusionTank(), fromTile(), tier.getOutput());
                case PIGMENT -> ChemicalUtil.emit(emitDirections, getChemicalTank().getPigmentTank(), fromTile(), tier.getOutput());
                case SLURRY -> ChemicalUtil.emit(emitDirections, getChemicalTank().getSlurryTank(), fromTile(), tier.getOutput());
            }
        }
    }

    protected BlockEntity fromTile() {
        return WorldUtils.getTileEntity(getLevel(), getBlockPos());
    }

    @Override
    public boolean needConfig() {
        return false;
    }

    @Override
    public void nextMode(int tank) {
        if (tank == 0) {
            dumping = dumping.getNext();
            markForSave();
        }
    }

    @Override
    public int getRedstoneLevel() {
        IChemicalTank<?, ?> currentTank = getCurrentTank();
        return MekanismUtils.redstoneLevelFromContents(currentTank.getStored(), currentTank.getCapacity());
    }

    @Override
    protected boolean makesComparatorDirty(@Nullable SubstanceType type) {
        return type == SubstanceType.GAS || type == SubstanceType.INFUSION || type == SubstanceType.PIGMENT || type == SubstanceType.SLURRY;
    }

    @WrappingComputerMethod(wrapper = ComputerChemicalTankWrapper.class, methodNames = { "getStored", "getCapacity", "getNeeded", "getFilledPercentage" }, docPlaceholder = "tank")
    IChemicalTank<?, ?> getCurrentTank() {
        Current current = chemicalTank.getCurrent();
        return chemicalTank.getTankFromCurrent(current == Current.EMPTY ? Current.GAS : current);
    }

    public IGasTank getGasTank() {
        return chemicalTank.getGasTank();
    }

    public IInfusionTank getInfusionTank() {
        return chemicalTank.getInfusionTank();
    }

    public IPigmentTank getPigmentTank() {
        return chemicalTank.getPigmentTank();
    }

    public ISlurryTank getSlurryTank() {
        return chemicalTank.getSlurryTank();
    }

    @Override
    public void parseUpgradeData(@NotNull IUpgradeData upgradeData) {
        if (upgradeData instanceof ChemicalTankUpgradeData data) {
            redstone = data.redstone;
            setControlType(data.controlType);
            drainSlot.setStack(data.drainSlot.getStack());
            fillSlot.setStack(data.fillSlot.getStack());
            dumping = data.dumping;
            getGasTank().setStack(data.storedGas);
            getInfusionTank().setStack(data.storedInfusion);
            getPigmentTank().setStack(data.storedPigment);
            getSlurryTank().setStack(data.storedSlurry);
            for (ITileComponent component : getComponents()) {
                component.read(data.components);
            }
        } else {
            super.parseUpgradeData(upgradeData);
        }
    }

    @NotNull
    @Override
    public ChemicalTankUpgradeData getUpgradeData() {
        return new ChemicalTankUpgradeData(redstone, getControlType(), drainSlot, fillSlot, dumping, getGasTank().getStack(), getInfusionTank().getStack(),
                getPigmentTank().getStack(), getSlurryTank().getStack(), getComponents());
    }

    @Override
    public void writeSustainedData(CompoundTag dataMap) {
        NBTUtils.writeEnum(dataMap, NBTConstants.DUMP_MODE, dumping);
    }

    @Override
    public void readSustainedData(CompoundTag dataMap) {
        NBTUtils.setEnumIfPresent(dataMap, NBTConstants.DUMP_MODE, GasMode::byIndexStatic, mode -> dumping = mode);
    }

    @Override
    public Map<String, String> getTileDataRemap() {
        Map<String, String> remap = new Object2ObjectOpenHashMap<>();
        remap.put(NBTConstants.DUMP_MODE, NBTConstants.DUMP_MODE);
        return remap;
    }

    @Override
    public void addContainerTrackers(MekanismContainer container) {
        super.addContainerTrackers(container);
        container.track(SyncableEnum.create(GasMode::byIndexStatic, GasMode.IDLE, () -> dumping, value -> dumping = value));
    }

    // Methods relating to IComputerTile
    @ComputerMethod(requiresPublicSecurity = true, methodDescription = "Set the Dumping mode of the tank")
    void setDumpingMode(GasMode mode) throws ComputerException {
        validateSecurityIsPublic();
        if (dumping != mode) {
            dumping = mode;
            markForSave();
        }
    }

    @ComputerMethod(requiresPublicSecurity = true, methodDescription = "Advance the Dumping mode to the next configuration in the list")
    void incrementDumpingMode() throws ComputerException {
        validateSecurityIsPublic();
        nextMode(0);
    }

    @ComputerMethod(requiresPublicSecurity = true, methodDescription = "Descend the Dumping mode to the previous configuration in the list")
    void decrementDumpingMode() throws ComputerException {
        validateSecurityIsPublic();
        dumping = dumping.getPrevious();
        markForSave();
    }
    // End methods IComputerTile
}
