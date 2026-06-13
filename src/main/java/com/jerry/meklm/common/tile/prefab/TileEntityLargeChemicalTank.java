package com.jerry.meklm.common.tile.prefab;

import com.jerry.meklm.api.tier.ILargeChemicalTankTier;
import com.jerry.meklm.common.capabilities.holder.chemical.AdjustableChemicalTankHelper;
import com.jerry.meklm.common.capabilities.holder.chemical.LargeChemicalTankChemicalTank;
import com.jerry.meklm.common.tile.INotNeedConfig;

import mekanism.api.IContentsListener;
import mekanism.api.RelativeSide;
import mekanism.api.SerializationConstants;
import mekanism.api.chemical.ChemicalResource;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.inventory.IInventorySlot;
import mekanism.api.math.MathUtils;
import mekanism.common.Mekanism;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.holder.container.IContainerHolder;
import mekanism.common.capabilities.holder.container.MekContainerHelper;
import mekanism.common.component.containers.type.ContainerType;
import mekanism.common.component.containers.type.IContainerType;
import mekanism.common.config.MekanismConfig;
import mekanism.common.integration.computer.ComputerException;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.integration.computer.annotation.SyntheticComputerMethod;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.inventory.container.slot.ContainerSlotType;
import mekanism.common.inventory.container.slot.SlotOverlay;
import mekanism.common.inventory.container.sync.SyncableEnum;
import mekanism.common.inventory.slot.ChemicalInventorySlot;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.registries.MekanismDataComponents;
import mekanism.common.tile.TileEntityChemicalTank.GasMode;
import mekanism.common.tile.component.ITileComponent;
import mekanism.common.tile.component.TileComponentEjector;
import mekanism.common.tile.component.config.ConfigInfo;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.tile.interfaces.IBoundingBlock;
import mekanism.common.tile.interfaces.IHasGasMode;
import mekanism.common.tile.prefab.TileEntityConfigurableMachine;
import mekanism.common.upgrade.ChemicalTankUpgradeData;
import mekanism.common.upgrade.IUpgradeData;
import mekanism.common.util.ChemicalUtils;
import mekanism.common.util.NBTUtils;
import mekanism.common.util.ResourceUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TileEntityLargeChemicalTank<TIER extends ILargeChemicalTankTier> extends TileEntityConfigurableMachine implements IHasGasMode, IBoundingBlock, INotNeedConfig {

    private static final RelativeSide[] CHEMICAL_SIDES = { RelativeSide.TOP };

    @SyntheticComputerMethod(getter = "getDumpingMode", getterDescription = "Get the current Dumping configuration")
    public GasMode dumping = GasMode.IDLE;
    protected int numPowering;

    @Nullable
    private List<BlockCapabilityCache<ResourceHandler<ChemicalResource>, @Nullable Direction>> outputCaches;

    @Getter
    private IChemicalTank chemicalTank;
    @Getter
    protected TIER tier;

    @WrappingComputerMethod(wrapper = SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.class, methodNames = "getDrainItem", docPlaceholder = "drain slot")
    ChemicalInventorySlot drainSlot;
    @WrappingComputerMethod(wrapper = SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.class, methodNames = "getFillItem", docPlaceholder = "fill slot")
    ChemicalInventorySlot fillSlot;

    public TileEntityLargeChemicalTank(Holder<Block> blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state, tile -> new TileComponentEjector(tile, () -> MathUtils.clampToInt(((TileEntityLargeChemicalTank<?>) tile).tier.getOutput())));
        configComponent.setupIOConfig(TransmissionType.ITEM, drainSlot, fillSlot, true).setCanEject(false);
        setupIOConfig(TransmissionType.CHEMICAL, getChemicalTank(), RelativeSide.TOP);
        ejectorComponent.setOutputData(configComponent, TransmissionType.CHEMICAL)
                .setCanEject(type -> canFunction() && dumping != GasMode.DUMPING);
    }

    private <CONTAINER> ConfigInfo setupIOConfig(TransmissionType type, CONTAINER container, RelativeSide side) {
        ConfigInfo config = configComponent.setupIOConfig(type, container);
        if (config != null) {
            config.setDataType(DataType.INPUT_OUTPUT, side);
        }
        return config;
    }

    @Override
    public @Nullable IContainerHolder<IChemicalTank> getInitialChemicalTanks(IContentsListener listener) {
        // 化学品下进上出
        AdjustableChemicalTankHelper builder = AdjustableChemicalTankHelper.forSide(facingSupplier, side -> side == RelativeSide.BACK, side -> side == RelativeSide.TOP);
        builder.addTank(chemicalTank = LargeChemicalTankChemicalTank.create(tier, this::getGameTime, listener), RelativeSide.TOP, RelativeSide.BACK);
        return builder.build();
    }

    protected RelativeSide[] getChemicalSides() {
        return CHEMICAL_SIDES;
    }

    @NotNull
    @Override
    protected IContainerHolder<IInventorySlot> getInitialInventory(IContentsListener listener) {
        // 物品上进下出
        MekContainerHelper<IInventorySlot> builder = MekContainerHelper.forSide(facingSupplier, side -> side == RelativeSide.TOP, side -> side == RelativeSide.BACK);
        builder.addContainer(drainSlot = ChemicalInventorySlot.drain(chemicalTank, listener, 16, 16), RelativeSide.TOP, RelativeSide.BACK);
        builder.addContainer(fillSlot = ChemicalInventorySlot.fill(chemicalTank, listener, 16, 48), RelativeSide.TOP, RelativeSide.BACK);
        drainSlot.setSlotType(ContainerSlotType.OUTPUT);
        drainSlot.setSlotOverlay(SlotOverlay.PLUS);
        fillSlot.setSlotType(ContainerSlotType.INPUT);
        fillSlot.setSlotOverlay(SlotOverlay.MINUS);
        return builder.build();
    }

    @Override
    protected boolean onUpdateServer(net.minecraft.server.level.ServerLevel level) {
        boolean sendUpdatePacket = super.onUpdateServer(level);
        drainSlot.drainTankIntoSlot(null);
        fillSlot.fillTankFromSlot(null);
        if (dumping != GasMode.IDLE) {
            if (dumping == GasMode.DUMPING) {
                ChemicalUtils.dump(chemicalTank, dumping, tier.getStorage() / 400);
            } else {// dumping == GasMode.DUMPING_EXCESS
                long target = MathUtils.clampToLong(chemicalTank.capacityAsLong(chemicalTank.resource()) * MekanismConfig.general.dumpExcessKeepRatio.get());
                long stored = chemicalTank.amountAsLong();
                if (target < stored) {
                    // Dump excess that we need to get to the target (capping at our eject rate for how much we can dump
                    // at once)
                    ChemicalUtils.dump(chemicalTank, dumping, Math.min(stored - target, tier.getOutput()));
                }
            }
        }
        if (canFunction()) {
            if (outputCaches == null) {
                Direction direction = getDirection();
                RelativeSide[] chemicalSides = getChemicalSides();
                outputCaches = new ArrayList<>(chemicalSides.length);
                for (RelativeSide chemicalSide : chemicalSides) {
                    Direction side = chemicalSide.getDirection(direction);
                    outputCaches.add(Capabilities.CHEMICAL.createCache((ServerLevel) level, offSetOutput(worldPosition, side), side.getOpposite()));
                }
            }
            ResourceUtils.emit(outputCaches, chemicalTank, MathUtils.clampToInt(tier.getOutput()), null);
        }
        return sendUpdatePacket;
    }

    protected BlockPos offSetOutput(BlockPos from, Direction side) {
        return from.relative(side);
    }

    @Override
    protected void invalidateDirectionCaches(Direction newDirection) {
        super.invalidateDirectionCaches(newDirection);
        outputCaches = null;
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
        IChemicalTank currentTank = getCurrentTank();
        return ContainerType.CHEMICAL.getRedstoneSignalFromContainer(currentTank);
    }

    @Override
    protected boolean makesComparatorDirty(IContainerType<?, ?> type) {
        return type == ContainerType.CHEMICAL;
    }

    @WrappingComputerMethod(wrapper = SpecialComputerMethodWrapper.ComputerChemicalTankWrapper.class,
                            methodNames = { "getStored", "getCapacity", "getNeeded",
                                    "getFilledPercentage" },
                            docPlaceholder = "tank")
    IChemicalTank getCurrentTank() {
        return chemicalTank;
    }

    @Override
    public void parseUpgradeData(@NotNull IUpgradeData upgradeData, HolderLookup.Provider provider, TransactionContext transaction) {
        if (upgradeData instanceof ChemicalTankUpgradeData data) {
            redstone = data.redstone;
            setControlType(data.controlType);
            drainSlot.copyContents(data.drainSlot, transaction);
            fillSlot.copyContents(data.fillSlot, transaction);
            dumping = data.dumping;
            getChemicalTank().copyContents(data.chemicalTank, transaction);
            try (var reporter = new ProblemReporter.ScopedCollector(problemPath(), Mekanism.logger)) {
                ValueInput input = TagValueInput.create(reporter, provider, data.components);
                for (ITileComponent component : getComponents()) {
                    component.read(input);
                }
            }
        } else {
            super.parseUpgradeData(upgradeData, provider, transaction);
        }
    }

    @NotNull
    @Override
    public ChemicalTankUpgradeData getUpgradeData(HolderLookup.Provider provider) {
        return new ChemicalTankUpgradeData(provider, redstone, getControlType(), drainSlot, fillSlot, dumping, getChemicalTank(), getComponents(), problemPath());
    }

    @Override
    public void writeSustainedData(@NotNull ValueOutput output) {
        super.writeSustainedData(output);
        NBTUtils.writeEnum(output, SerializationConstants.DUMP_MODE, dumping);
    }

    @Override
    public void readSustainedData(@NotNull ValueInput input) {
        super.readSustainedData(input);
        NBTUtils.setEnumIfPresent(input, SerializationConstants.DUMP_MODE, GasMode.BY_ID, mode -> dumping = mode);
    }

    @Override
    protected void collectImplicitComponents(@NotNull DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        builder.set(MekanismDataComponents.DUMP_MODE, dumping);
    }

    @Override
    protected void applyImplicitComponents(@NotNull DataComponentGetter input) {
        super.applyImplicitComponents(input);
        dumping = input.getOrDefault(MekanismDataComponents.DUMP_MODE, dumping);
    }

    @Override
    public void addContainerTrackers(MekanismContainer container) {
        super.addContainerTrackers(container);
        container.track(SyncableEnum.create(GasMode.BY_ID, GasMode.IDLE, () -> dumping, v -> dumping = v));
    }

    @Override
    public boolean isPowered() {
        return redstone || numPowering > 0;
    }

    @Override
    public void onBoundingBlockPowerChange(BlockPos boundingPos, int oldLevel, int newLevel) {
        if (oldLevel > 0) {
            if (newLevel == 0) {
                numPowering--;
            }
        } else if (newLevel > 0) {
            numPowering++;
        }
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
}
