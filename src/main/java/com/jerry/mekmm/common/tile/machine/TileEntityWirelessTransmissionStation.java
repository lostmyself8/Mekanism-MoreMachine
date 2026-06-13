package com.jerry.mekmm.common.tile.machine;

import com.jerry.mekmm.api.MoreMachineSerializationConstants;
import com.jerry.mekmm.common.attachments.component.ConnectionConfig;
import com.jerry.mekmm.common.attachments.component.WirelessConnectionManager;
import com.jerry.mekmm.common.config.MoreMachineConfig;
import com.jerry.mekmm.common.inventory.container.tile.WirelessTransmissionStationConfigContainer;
import com.jerry.mekmm.common.registries.MoreMachineBlocks;
import com.jerry.mekmm.common.registries.MoreMachineDataComponents;
import com.jerry.mekmm.common.tile.interfaces.ITileConnectHolder;
import com.jerry.mekmm.common.tile.prefab.TileEntityConnectableMachine;

import mekanism.api.IContentsListener;
import mekanism.api.RelativeSide;
import mekanism.api.chemical.BasicChemicalTank;
import mekanism.api.chemical.ChemicalResource;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.fluid.IFluidTank;
import mekanism.api.heat.HeatAPI.HeatTransfer;
import mekanism.api.heat.IHeatCapacitor;
import mekanism.api.heat.IHeatHandler;
import mekanism.api.inventory.IInventorySlot;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.capabilities.fluid.BasicFluidTank;
import mekanism.common.capabilities.heat.BasicHeatCapacitor;
import mekanism.common.capabilities.heat.CachedAmbientTemperature;
import mekanism.common.capabilities.heat.ITileHeatHandler;
import mekanism.common.capabilities.holder.container.IContainerHolder;
import mekanism.common.capabilities.holder.container.MekContainerHelper;
import mekanism.common.capabilities.holder.energy.EnergyConfigHolder;
import mekanism.common.capabilities.holder.energy.IEnergyContainerHolder;
import mekanism.common.component.containers.type.ContainerType;
import mekanism.common.component.containers.type.IContainerType;
import mekanism.common.integration.computer.ComputerException;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.*;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.inventory.container.slot.SlotOverlay;
import mekanism.common.inventory.container.sync.SyncableDouble;
import mekanism.common.inventory.container.sync.SyncableInt;
import mekanism.common.inventory.container.sync.SyncableLong;
import mekanism.common.inventory.slot.BasicInventorySlot;
import mekanism.common.inventory.slot.ChemicalInventorySlot;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.inventory.slot.FluidInventorySlot;
import mekanism.common.inventory.slot.OutputInventorySlot;
import mekanism.common.lib.inventory.Finder;
import mekanism.common.lib.inventory.TransitRequest;
import mekanism.common.lib.inventory.TransitRequest.TransitResponse;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.tile.component.config.ConfigInfo;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.tile.interfaces.IBoundingBlock;
import mekanism.common.util.*;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import com.mojang.serialization.MapCodec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class TileEntityWirelessTransmissionStation extends TileEntityConnectableMachine implements IBoundingBlock, ITileConnectHolder {

    public static final long DEFAULT_ENERGY_RATE = 0;
    public static final int DEFAULT_FLUIDS_RATE = 0;
    public static final long DEFAULT_CHEMICALS_RATE = 0;
    public static final int DEFAULT_ITEMS_RATE = 0;
    public static final double DEFAULT_HEAT_RATE = 0;

    public final WirelessConnectionManager connectionManager = new WirelessConnectionManager(this);

    private long energyRate;
    private int fluidsRate;
    private long chemicalsRate;
    private int itemsRate;
    private double heatRate;

    public static final long MAX_CHEMICAL = 10_000;
    public static final int MAX_FLUID = 10_000;
    public static final double HEAT_CAPACITY = 10;
    public static final double INVERSE_CONDUCTION_COEFFICIENT = 2;
    public static final double INVERSE_INSULATION_COEFFICIENT = 100;
    public static final double MAX_MULTIPLIER_TEMP = 10_000;

    private double lastTransferLoss;
    private double lastEnvironmentLoss;

    @WrappingComputerMethod(wrapper = ComputerFluidTankWrapper.class, methodNames = { "getFluid", "getFluidCapacity", "getFluidNeeded", "getFluidFilledPercentage" }, docPlaceholder = "fluid tank")
    public BasicFluidTank fluidTank;
    @WrappingComputerMethod(wrapper = ComputerChemicalTankWrapper.class, methodNames = { "getChemical", "getChemicalCapacity", "getChemicalNeeded", "getChemicalFilledPercentage" }, docPlaceholder = "chemical tank")
    public IChemicalTank chemicalTank;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getItemSlot", docPlaceholder = "item slot")
    public BasicInventorySlot inventorySlot;
    public MachineEnergyContainer<TileEntityWirelessTransmissionStation> energyContainer;
    @WrappingComputerMethod(wrapper = ComputerHeatCapacitorWrapper.class, methodNames = "getTemperature", docPlaceholder = "transmission")
    public BasicHeatCapacitor heatCapacitor;

    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getFluidFillItem", docPlaceholder = "fill fluid slot")
    FluidInventorySlot fluidFillSlot;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getFluidDrainItem", docPlaceholder = "drain fluid slot")
    FluidInventorySlot fluidDrainSlot;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getFluidItemOutput", docPlaceholder = "fluid item output slot")
    OutputInventorySlot fluidOutputSlot;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getChemicalFillItem", docPlaceholder = "fill chemical slot")
    ChemicalInventorySlot chemicalInputSlot;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getChemicalDrainItem", docPlaceholder = "drain chemical slot")
    ChemicalInventorySlot chemicalOutputSlot;
    EnergyInventorySlot energySlot;

    public TileEntityWirelessTransmissionStation(BlockPos pos, BlockState state) {
        super(MoreMachineBlocks.WIRELESS_TRANSMISSION_STATION, pos, state);
        energyRate = DEFAULT_ENERGY_RATE;
        fluidsRate = DEFAULT_FLUIDS_RATE;
        chemicalsRate = DEFAULT_CHEMICALS_RATE;
        itemsRate = DEFAULT_ITEMS_RATE;
        heatRate = DEFAULT_HEAT_RATE;
        setupIOConfig(TransmissionType.ENERGY, energyContainer, RelativeSide.FRONT);
        setupIOConfig(TransmissionType.FLUID, fluidTank, RelativeSide.LEFT);
        setupIOConfig(TransmissionType.CHEMICAL, chemicalTank, RelativeSide.RIGHT);
        configComponent.setupItemIOConfig(List.of(inventorySlot, fluidFillSlot, chemicalInputSlot), List.of(fluidDrainSlot, chemicalOutputSlot, fluidOutputSlot), energySlot, false);
        setupIOConfig(TransmissionType.HEAT, heatCapacitor, RelativeSide.BACK);
        configComponent.addDisabledSides(RelativeSide.TOP);
        ejectorComponent.setOutputData(configComponent, TransmissionType.ITEM, TransmissionType.CHEMICAL, TransmissionType.FLUID, TransmissionType.ENERGY, TransmissionType.HEAT);
    }

    private <CONTAINER> ConfigInfo setupIOConfig(TransmissionType type, CONTAINER container, RelativeSide side) {
        ConfigInfo config = configComponent.setupIOConfig(type, container);
        if (config != null) {
            config.setDataType(DataType.INPUT_OUTPUT, side);
        }
        return config;
    }

    @Override
    protected @Nullable IContainerHolder<IInventorySlot> getInitialInventory(IContentsListener listener) {
        MekContainerHelper<IInventorySlot> builder = MekContainerHelper.forSideWithItemConfig(this);
        builder.addContainer(inventorySlot = BasicInventorySlot.at(listener, 8, 77));
        builder.addContainer(chemicalInputSlot = ChemicalInventorySlot.fill(chemicalTank, listener, 28, 15));
        builder.addContainer(chemicalOutputSlot = ChemicalInventorySlot.drain(chemicalTank, listener, 28, 57));
        builder.addContainer(fluidFillSlot = FluidInventorySlot.fill(fluidTank, listener, 131, 15));
        builder.addContainer(fluidDrainSlot = FluidInventorySlot.drain(fluidTank, listener, 131, 57));
        builder.addContainer(fluidOutputSlot = OutputInventorySlot.at(listener, 131, 36));
        builder.addContainer(energySlot = EnergyInventorySlot.fillOrConvert(energyContainer, this::getLevel, listener, 28, 36));
        chemicalInputSlot.setSlotOverlay(SlotOverlay.MINUS);
        chemicalOutputSlot.setSlotOverlay(SlotOverlay.PLUS);
        fluidFillSlot.setSlotOverlay(SlotOverlay.MINUS);
        fluidDrainSlot.setSlotOverlay(SlotOverlay.PLUS);
        return builder.build();
    }

    @Override
    public @Nullable IContainerHolder<IChemicalTank> getInitialChemicalTanks(IContentsListener listener) {
        MekContainerHelper<IChemicalTank> builder = MekContainerHelper.forSideWithChemicalConfig(this);
        builder.addContainer(chemicalTank = BasicChemicalTank.create(MAX_CHEMICAL, listener));
        return builder.build();
    }

    @Override
    protected @Nullable IContainerHolder<IFluidTank> getInitialFluidTanks(IContentsListener listener) {
        MekContainerHelper<IFluidTank> builder = MekContainerHelper.forSideWithFluidConfig(this);
        builder.addContainer(fluidTank = BasicFluidTank.create(MAX_FLUID, listener));
        return builder.build();
    }

    @Override
    protected @Nullable IEnergyContainerHolder getInitialEnergyContainer(IContentsListener listener) {
        energyContainer = MachineEnergyContainer.input(this, listener);
        return new EnergyConfigHolder(energyContainer, this);
    }

    @Override
    protected @Nullable IContainerHolder<IHeatCapacitor> getInitialHeatCapacitors(IContentsListener listener, CachedAmbientTemperature ambientTemperature) {
        MekContainerHelper<IHeatCapacitor> builder = MekContainerHelper.forSideWithHeatConfig(this);
        builder.addContainer(heatCapacitor = BasicHeatCapacitor.create(HEAT_CAPACITY, INVERSE_CONDUCTION_COEFFICIENT, INVERSE_INSULATION_COEFFICIENT, ambientTemperature, listener));
        return builder.build();
    }

    private void closeInvalidScreens() {
        if (getActive() && !playersUsing.isEmpty()) {
            for (Player player : new HashSet<>(playersUsing)) {
                if (player.containerMenu instanceof WirelessTransmissionStationConfigContainer) {
                    player.closeContainer();
                }
            }
        }
    }

    @Override
    protected void onUpdateClient(Level level) {
        super.onUpdateClient(level);
        closeInvalidScreens();
    }

    @Override
    protected boolean onUpdateServer(ServerLevel level) {
        boolean sendUpdatePacket = super.onUpdateServer(level);
        closeInvalidScreens();
        chemicalInputSlot.fillTankOrConvert(null);
        chemicalOutputSlot.drainTankIntoSlot(null);
        fluidFillSlot.fillTankFromSlot(fluidOutputSlot, null);
        fluidDrainSlot.drainTankIntoSlot(fluidOutputSlot, null);
        energySlot.fillContainerOrConvert(null);
        // 2.5秒检测一次
        if (level != null && level.getGameTime() % 50 == 0) {
            connectionManager.validateConnections();
        }
        // TODO:添加一个延时，不需要每tick都发送（2秒发送一次应该可以）
        if (canFunction()) {
            // 不需要检测速率是否小于等于0，emit会检测
            // 传输能量
            Collection<BlockCapabilityCache<EnergyHandler, @Nullable Direction>> energyCaches = (Collection<BlockCapabilityCache<EnergyHandler, @Nullable Direction>>) (Collection<?>) connectionManager.getEnergyCaches();
            EnergyUtils.emit(energyCaches, energyRate, null);
            // 传输流体
            Collection<BlockCapabilityCache<ResourceHandler<FluidResource>, @Nullable Direction>> fluidCaches = (Collection<BlockCapabilityCache<ResourceHandler<FluidResource>, @Nullable Direction>>) (Collection<?>) connectionManager.getFluidCaches();
            ResourceUtils.emit(fluidCaches, fluidTank, fluidsRate, null);
            // 传输化学品
            Collection<BlockCapabilityCache<ResourceHandler<ChemicalResource>, @Nullable Direction>> chemicalCaches = (Collection<BlockCapabilityCache<ResourceHandler<ChemicalResource>, @Nullable Direction>>) (Collection<?>) connectionManager.getChemicalCaches();
            ResourceUtils.emit(chemicalCaches, chemicalTank, Math.toIntExact(Math.min(Integer.MAX_VALUE, chemicalsRate)), null);
            // 传输物品
            transportItems();
        }
        // 传输热量
        HeatTransfer loss = simulate();
        // 如果有无线交换热量缓存时要加上无线交换的热量损失
        // 交换热量需要每tick进行
        // 如果没有无线热量传递，则只计算相邻方块的热传导；如果有无线热量传递，则计算两者的加和
        lastTransferLoss = loss.adjacentTransfer();
        lastEnvironmentLoss = loss.environmentTransfer();
        return sendUpdatePacket;
    }

    @SuppressWarnings("removal")
    private void transportItems() {
        if (itemsRate <= 0) return;
        // TODO:在某种情况下可以平分，希望可以做到不需要给定面即可输出
        // 获取自身的弹出能力
        ResourceHandler<ItemResource> selfHandler = Capabilities.ITEM.createCache((ServerLevel) level, getBlockPos(), Direction.DOWN).getCapability();
        if (selfHandler == null) return;

        Collection<BlockCapabilityCache<ResourceHandler<ItemResource>, @Nullable Direction>> itemCaches = (Collection<BlockCapabilityCache<ResourceHandler<ItemResource>, @Nullable Direction>>) (Collection<?>) connectionManager.getItemCaches();
        for (BlockCapabilityCache<ResourceHandler<ItemResource>, @Nullable Direction> cache : itemCaches) {
            ResourceHandler<ItemResource> target = cache.getCapability();
            if (target != null) {
                try (Transaction transaction = Transaction.openRoot()) {
                    TransitRequest request = TransitRequest.definedItem(selfHandler, 1, itemsRate, Finder.ANY, transaction);
                    if (request.isEmpty()) {
                        continue;
                    }
                    TransitResponse response = request.eject(this, getBlockPos(), target, 0, null, transaction);
                    if (!response.isEmpty()) {
                        response.useAll(transaction);
                        transaction.commit();
                    }
                }
            }
        }
    }

    /**
     * 与{@link ITileHeatHandler#simulateAdjacent()}相似
     *
     * @return double 与连接方块的热量传递值
     */
    // 连接两个及以上数量的方块时会导致热量频繁交换（
    private double exchangeHeat() {
        // double adjacentTransfer = 0;
        // // 累积总热量变化
        // double totalHeatToTransfer = 0;
        // // 当前温度(在循环开始前获取,避免循环中温度变化影响计算)
        // double currentTemp = getTemperature();
        // // 获取当前系统该方向的热容量（在simulateAdjacent()中是这样，但在这只是获取热量容器的热容量）
        // double heatCapacity = heatCapacitor.getHeatCapacity();
        //
        // for (ConnectionConfig config : connectionManager.getConnectionsByType(TransmissionType.HEAT)) {
        // // 检查该方向是否有相邻的热处理系统
        // IHeatHandler sink = WorldUtils.getCapability(currentLevel, Capabilities.HEAT, config.pos(),
        // config.direction());
        // // 只有存在相邻系统时才进行热交换计算
        // if (sink != null) {
        // // 获取目标温度
        // double sinkTemp = sink.getTotalTemperature();
        // // 计算总热阻
        // double invConduction = sink.getTotalInverseConduction() + heatCapacitor.getInverseConduction();
        // if (invConduction == 0) continue;
        // double tempDifference = currentTemp - sinkTemp;
        // double tempToTransfer = tempDifference / invConduction;
        // // 将温度差转换为实际热量Q = ΔT × C
        // double heatToTransfer = tempToTransfer * heatCapacity;
        // // 限制热量传递速率，最多传递50%的温差
        // double maxHeatTransfer = Math.abs(tempDifference) * heatCapacity * 0.5;
        // heatToTransfer = Mth.clamp(heatToTransfer, -maxHeatTransfer, maxHeatTransfer);
        // totalHeatToTransfer -= heatToTransfer;
        // // 对方接收热量
        // sink.handleHeat(heatToTransfer);
        // // 对方接收热量
        // adjacentTransfer = incrementAdjacentTransfer(adjacentTransfer, tempToTransfer, config.direction());
        // }
        // }
        // // 一次性应用所有热量变化
        // if (totalHeatToTransfer != 0) {
        // heatCapacitor.handleHeat(totalHeatToTransfer);
        // }
        // return adjacentTransfer;

        // 如果热量速率为0或没有热量连接,直接返回
        if (heatRate <= 0) return 0;
        List<ConnectionConfig> heatConnections = connectionManager.getConnectionsByType(TransmissionType.HEAT).stream().toList();
        if (heatConnections.isEmpty()) return 0;
        double adjacentTransfer = 0;
        double currentTemp = getTemperature();
        double heatCapacity = heatCapacitor.getHeatCapacity();

        // 计算每个连接分配的温度变化量
        // 将总速率平均分配给所有连接
        double tempChangePerConnection = heatRate / heatConnections.size();
        for (ConnectionConfig config : heatConnections) {
            IHeatHandler sink = WorldUtils.getCapability(level, Capabilities.HEAT, config.pos(), config.direction());
            if (sink != null) {
                double sinkTemp = sink.getTotalTemperature();
                // 单向传输:只有当传输站温度高于目标温度时才传输
                if (currentTemp > sinkTemp) {
                    // 计算实际可传输的温度(不能超过温差)
                    double maxTempTransfer = currentTemp - sinkTemp;
                    double actualTempTransfer = Math.min(tempChangePerConnection, maxTempTransfer);
                    // 计算对应的热量 Q = ΔT × C
                    double heatToTransfer = actualTempTransfer * heatCapacity;
                    // 从传输站移除热量
                    heatCapacitor.handleHeat(-heatToTransfer);
                    // 向目标添加热量
                    sink.handleHeat(heatToTransfer);
                    adjacentTransfer += actualTempTransfer;
                }
            }
        }
        return adjacentTransfer;
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        connectionManager.clear();
    }

    @Override
    public double simulateAdjacent() {
        return super.simulateAdjacent() + exchangeHeat();
    }

    @Override
    public ConnectStatus connectOrCut(BlockPos blockPos, Direction direction, TransmissionType type) {
        ConnectStatus status = connectionManager.linkOrCut(blockPos, direction, type);
        if (status != ConnectStatus.CONNECT_FAIL && !isRemote()) {
            sendUpdatePacket();
            markForSave();
        }
        return status;
    }

    public @Nullable MachineEnergyContainer<TileEntityWirelessTransmissionStation> getWirelessEnergyContainer() {
        return energyContainer;
    }

    public long getEnergyRate() {
        return energyRate;
    }

    public int getFluidsRate() {
        return fluidsRate;
    }

    public long getChemicalsRate() {
        return chemicalsRate;
    }

    public int getItemsRate() {
        return itemsRate;
    }

    public double getHeatRate() {
        return heatRate;
    }

    public void setEnergyRateFromPacket(long newRate) {
        setEnergyRate(Mth.clamp(newRate, 0, MoreMachineConfig.general.energyRate.get()));
    }

    public void setEnergyRate(long newRate) {
        if (energyRate != newRate) {
            energyRate = newRate;
        }
    }

    public void setFluidsRateFromPacket(int newRate) {
        setFluidsRate(Mth.clamp(newRate, 0, MoreMachineConfig.general.fluidsRate.get()));
    }

    public void setFluidsRate(int newRate) {
        if (fluidsRate != newRate) {
            fluidsRate = newRate;
        }
    }

    public void setChemicalsRateFromPacket(long newRate) {
        setChemicalsRate(Mth.clamp(newRate, 0, MoreMachineConfig.general.chemicalsRate.get()));
    }

    public void setChemicalsRate(long newRate) {
        if (chemicalsRate != newRate) {
            chemicalsRate = newRate;
        }
    }

    public void setItemsRateFromPacket(int newRate) {
        setItemsRate(Mth.clamp(newRate, 0, MoreMachineConfig.general.itemsRate.get()));
    }

    public void setItemsRate(int newRate) {
        if (itemsRate != newRate) {
            itemsRate = newRate;
        }
    }

    public void setHeatRateFromPacket(double newRate) {
        setHeatRate(Mth.clamp(newRate, 0, MoreMachineConfig.general.heatRate.get()));
    }

    public void setHeatRate(double newRate) {
        if (heatRate != newRate) {
            heatRate = newRate;
        }
    }

    public double getTemperature() {
        return heatCapacitor.getTemperature();
    }

    public double getLastTransferLoss() {
        return lastTransferLoss;
    }

    public double getLastEnvironmentLoss() {
        return lastEnvironmentLoss;
    }

    @Override
    public void writeReducedUpdatedTag(@NotNull ValueOutput output) {
        super.writeReducedUpdatedTag(output);
        CompoundTag connections = new CompoundTag();
        connectionManager.saveToNBT(connections);
        output.store(connections);
        output.putLong(MoreMachineSerializationConstants.ENERGY_RATE, getEnergyRate());
        output.putInt(MoreMachineSerializationConstants.FLUIDS_RATE, getFluidsRate());
        output.putLong(MoreMachineSerializationConstants.CHEMICALS_RATE, getChemicalsRate());
        output.putInt(MoreMachineSerializationConstants.ITEM_RATE, getItemsRate());
        output.putDouble(MoreMachineSerializationConstants.HEAT_RATE, getHeatRate());
    }

    @Override
    public void handleUpdateTag(@NotNull ValueInput input) {
        super.handleUpdateTag(input);
        input.read(MapCodec.assumeMapUnsafe(CompoundTag.CODEC)).ifPresent(connectionManager::loadFromNBT);
        readRates(input);
    }

    private void readRates(ValueInput input) {
        setEnergyRate(Math.min(input.getLongOr(MoreMachineSerializationConstants.ENERGY_RATE, energyRate), MoreMachineConfig.general.energyRate.get()));
        setFluidsRate(Math.min(input.getIntOr(MoreMachineSerializationConstants.FLUIDS_RATE, fluidsRate), MoreMachineConfig.general.fluidsRate.get()));
        setChemicalsRate(Math.min(input.getLongOr(MoreMachineSerializationConstants.CHEMICALS_RATE, chemicalsRate), MoreMachineConfig.general.chemicalsRate.get()));
        setItemsRate(Math.min(input.getIntOr(MoreMachineSerializationConstants.ITEM_RATE, itemsRate), MoreMachineConfig.general.itemsRate.get()));
        setHeatRate(Math.min(input.getDoubleOr(MoreMachineSerializationConstants.HEAT_RATE, heatRate), MoreMachineConfig.general.heatRate.get()));
    }

    @Override
    public void readSustainedData(@NotNull ValueInput input) {
        super.readSustainedData(input);
        readRates(input);
    }

    @Override
    public void writeSustainedData(@NotNull ValueOutput output) {
        super.writeSustainedData(output);
        output.putLong(MoreMachineSerializationConstants.ENERGY_RATE, getEnergyRate());
        output.putInt(MoreMachineSerializationConstants.FLUIDS_RATE, getFluidsRate());
        output.putLong(MoreMachineSerializationConstants.CHEMICALS_RATE, getChemicalsRate());
        output.putInt(MoreMachineSerializationConstants.ITEM_RATE, getItemsRate());
        output.putDouble(MoreMachineSerializationConstants.HEAT_RATE, getHeatRate());
    }

    @Override
    public void loadAdditional(@NotNull ValueInput input) {
        super.loadAdditional(input);
        input.read(MapCodec.assumeMapUnsafe(CompoundTag.CODEC)).ifPresent(connectionManager::loadFromNBT);
    }

    @Override
    public void saveAdditional(@NotNull ValueOutput output) {
        super.saveAdditional(output);
        CompoundTag connections = new CompoundTag();
        connectionManager.saveToNBT(connections);
        output.store(connections);
    }

    @Override
    public void addContainerTrackers(MekanismContainer container) {
        super.addContainerTrackers(container);
        addConfigContainerTrackers(container);
        container.track(SyncableDouble.create(this::getLastTransferLoss, v -> lastTransferLoss = v));
        container.track(SyncableDouble.create(this::getLastEnvironmentLoss, v -> lastEnvironmentLoss = v));
        container.track(SyncableInt.create(connectionManager::getConnectionCount, count -> {}));
    }

    public void addConfigContainerTrackers(MekanismContainer container) {
        container.track(SyncableLong.create(this::getEnergyRate, this::setEnergyRate));
        container.track(SyncableInt.create(this::getFluidsRate, this::setFluidsRate));
        container.track(SyncableLong.create(this::getChemicalsRate, this::setChemicalsRate));
        container.track(SyncableInt.create(this::getItemsRate, this::setItemsRate));
        container.track(SyncableDouble.create(this::getHeatRate, this::setHeatRate));
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.@NotNull Builder builder) {
        super.collectImplicitComponents(builder);
        builder.set(MoreMachineDataComponents.ENERGY_RATE, getEnergyRate());
        builder.set(MoreMachineDataComponents.FLUIDS_RATE, getFluidsRate());
        builder.set(MoreMachineDataComponents.CHEMICALS_RATE, getChemicalsRate());
        builder.set(MoreMachineDataComponents.ITEMS_RATE, getItemsRate());
        builder.set(MoreMachineDataComponents.HEAT_RATE, getHeatRate());
    }

    @Override
    protected void applyImplicitComponents(@NotNull DataComponentGetter input) {
        super.applyImplicitComponents(input);
        setEnergyRate(Math.min(input.getOrDefault(MoreMachineDataComponents.ENERGY_RATE, energyRate), MoreMachineConfig.general.energyRate.get()));
        setFluidsRate(Math.min(input.getOrDefault(MoreMachineDataComponents.FLUIDS_RATE, fluidsRate), MoreMachineConfig.general.fluidsRate.get()));
        setChemicalsRate(Math.min(input.getOrDefault(MoreMachineDataComponents.CHEMICALS_RATE, chemicalsRate), MoreMachineConfig.general.chemicalsRate.get()));
        setItemsRate(Math.min(input.getOrDefault(MoreMachineDataComponents.ITEMS_RATE, itemsRate), MoreMachineConfig.general.itemsRate.get()));
        setHeatRate(Math.min(input.getOrDefault(MoreMachineDataComponents.HEAT_RATE, heatRate), MoreMachineConfig.general.heatRate.get()));
    }

    @Override
    public int getRedstoneLevel() {
        return MekanismUtils.redstoneLevelFromContents(energyContainer.getAmountAsLong(), energyContainer.getCapacityAsLong());
    }

    @Override
    protected boolean makesComparatorDirty(IContainerType<?, ?> type) {
        return type == ContainerType.ENERGY;
    }

    @Override
    public WirelessConnectionManager getConnectManager() {
        return connectionManager;
    }

    // Methods relating to IComputerTile
    @ComputerMethod(requiresPublicSecurity = true, methodDescription = "Set energy transmission rate")
    void computerSetEnergyRate(long rate) throws ComputerException {
        validateSecurityIsPublic();
        if (energyRate != rate) {
            energyRate = rate;
            markForSave();
        }
    }

    @ComputerMethod(requiresPublicSecurity = true, methodDescription = "Set fluids transmission rate")
    void computerSetFluidsRate(int rate) throws ComputerException {
        validateSecurityIsPublic();
        if (fluidsRate != rate) {
            fluidsRate = rate;
            markForSave();
        }
    }

    @ComputerMethod(requiresPublicSecurity = true, methodDescription = "Set chemicals transmission rate")
    void computerSetChemicalsRate(long rate) throws ComputerException {
        validateSecurityIsPublic();
        if (chemicalsRate != rate) {
            chemicalsRate = rate;
            markForSave();
        }
    }

    @ComputerMethod(requiresPublicSecurity = true, methodDescription = "Set items transmission rate")
    void computerSetItemsRate(int rate) throws ComputerException {
        validateSecurityIsPublic();
        if (itemsRate != rate) {
            itemsRate = rate;
            markForSave();
        }
    }

    @ComputerMethod(requiresPublicSecurity = true, methodDescription = "Set heat transmission rate")
    void computerSetHeatRate(double rate) throws ComputerException {
        validateSecurityIsPublic();
        if (heatRate != rate) {
            heatRate = rate;
            markForSave();
        }
    }
    // End methods IComputerTile
}
