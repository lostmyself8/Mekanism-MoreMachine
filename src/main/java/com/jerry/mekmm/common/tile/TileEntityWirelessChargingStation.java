package com.jerry.mekmm.common.tile;

import com.jerry.mekmm.api.MoreMachineSerializationConstants;
import com.jerry.mekmm.common.config.MoreMachineConfig;
import com.jerry.mekmm.common.registries.MoreMachineBlocks;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import mekanism.api.*;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.energy.IStrictEnergyHandler;
import mekanism.api.math.FloatingLong;
import mekanism.common.Mekanism;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.capabilities.holder.energy.EnergyContainerHelper;
import mekanism.common.capabilities.holder.energy.IEnergyContainerHolder;
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.integration.curios.CuriosIntegration;
import mekanism.common.integration.energy.EnergyCompatUtils;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.inventory.container.slot.SlotOverlay;
import mekanism.common.inventory.container.sync.SyncableBoolean;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.tile.component.TileComponentConfig;
import mekanism.common.tile.component.TileComponentEjector;
import mekanism.common.tile.interfaces.IBoundingBlock;
import mekanism.common.tile.interfaces.ISustainedData;
import mekanism.common.tile.prefab.TileEntityConfigurableMachine;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.NBTUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class TileEntityWirelessChargingStation extends TileEntityConfigurableMachine implements ISustainedData, IBoundingBlock {

    @Getter
    private MachineEnergyContainer<TileEntityWirelessChargingStation> energyContainer;

    private boolean chargeEquipment = false;
    private boolean chargeInventory = false;
    private boolean chargeCurios = false;

    EnergyInventorySlot chargeSlot;
    EnergyInventorySlot dischargeSlot;

    public TileEntityWirelessChargingStation(BlockPos pos, BlockState state) {
        super(MoreMachineBlocks.WIRELESS_CHARGING_STATION, pos, state);
        configComponent = new TileComponentConfig(this, TransmissionType.ENERGY, TransmissionType.ITEM);
        configComponent.setupIOConfig(TransmissionType.ITEM, chargeSlot, dischargeSlot, RelativeSide.FRONT, true);
        configComponent.setupIOConfig(TransmissionType.ENERGY, energyContainer, RelativeSide.FRONT);
        configComponent.addDisabledSides(RelativeSide.TOP);
        ejectorComponent = new TileComponentEjector(this);
        ejectorComponent.setOutputData(configComponent, TransmissionType.ITEM, TransmissionType.ENERGY).setCanEject(type -> MekanismUtils.canFunction(this));
    }

    @Override
    protected @Nullable IEnergyContainerHolder getInitialEnergyContainers(IContentsListener listener) {
        EnergyContainerHelper builder = EnergyContainerHelper.forSideWithConfig(this::getDirection, this::getConfig);
        builder.addContainer(energyContainer = MachineEnergyContainer.input(this, listener));
        return builder.build();
    }

    @NotNull
    @Override
    protected IInventorySlotHolder getInitialInventory(IContentsListener listener) {
        InventorySlotHelper builder = InventorySlotHelper.forSideWithConfig(this::getDirection, this::getConfig);
        builder.addSlot(dischargeSlot = EnergyInventorySlot.fillOrConvert(energyContainer, this::getLevel, listener, 17, 35));
        builder.addSlot(chargeSlot = EnergyInventorySlot.drain(energyContainer, listener, 143, 35));
        dischargeSlot.setSlotOverlay(SlotOverlay.MINUS);
        chargeSlot.setSlotOverlay(SlotOverlay.PLUS);
        return builder.build();
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        chargeSlot.drainContainer();
        dischargeSlot.fillContainerOrConvert();
        if (!energyContainer.isEmpty() && MekanismUtils.canFunction(this)) {
            Level level = getLevel();
            UUID uuid = getSecurity().getOwnerUUID();
            if (level != null && uuid != null) {
                Player player = level.getPlayerByUUID(uuid);
                if (player == null) {
                    return;
                }
                FloatingLong maxChargeRate = MoreMachineConfig.general.wirelessChargingStationChargingRate.get();
                FloatingLong availableEnergy = energyContainer.getEnergy();
                FloatingLong toCharge = maxChargeRate.min(availableEnergy);
                if (toCharge.greaterThan(FloatingLong.ZERO)) {
                    // 优先充能盔甲，其次是主副手和饰品，最后是物品栏
                    if (chargeEquipment) {
                        toCharge = chargeSuit(player, toCharge);
                    }
                    if (toCharge.greaterThan(FloatingLong.ZERO) && chargeInventory) {
                        toCharge = chargeInventory(player, toCharge);
                    }
                    if (toCharge.greaterThan(FloatingLong.ZERO) && chargeCurios) {
                        chargeCurios(player, toCharge);
                    }
                }
            }
        }
    }

    private FloatingLong chargeSuit(Player player, FloatingLong toCharge) {
        for (ItemStack stack : player.getArmorSlots()) {
            // charge方法会检测是否是含能量槽的物品
            toCharge = charge(energyContainer, stack, toCharge);
            if (toCharge.isZero()) break;
        }
        return toCharge;
    }

    private FloatingLong chargeInventory(Player player, FloatingLong toCharge) {
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();
        toCharge = charge(energyContainer, mainHand, toCharge);
        toCharge = charge(energyContainer, offHand, toCharge);
        if (toCharge.greaterThan(FloatingLong.ZERO)) {
            for (ItemStack stack : player.getInventory().items) {
                if (stack != mainHand && stack != offHand) {
                    toCharge = charge(energyContainer, stack, toCharge);
                    if (toCharge.isZero()) break;
                }
            }

        }
        return toCharge;
    }

    private void chargeCurios(Player player, FloatingLong toCharge) {
        if (Mekanism.hooks.CuriosLoaded) {
            Optional<? extends IItemHandler> curiosInventory = CuriosIntegration.getCuriosInventory(player);
            if (curiosInventory.isEmpty()) {
                return;
            }
            IItemHandler handler = curiosInventory.get();
            for (int slot = 0, slots = handler.getSlots(); slot < slots; slot++) {
                toCharge = charge(energyContainer, handler.getStackInSlot(slot), toCharge);
                if (toCharge.isZero()) {
                    return;
                }
            }
        }
    }

    private FloatingLong charge(IEnergyContainer energyContainer, ItemStack stack, FloatingLong amount) {
        if (stack.isEmpty() || amount.smallerOrEqual(FloatingLong.ZERO)) {
            return amount;
        }
        IStrictEnergyHandler handler = EnergyCompatUtils.getStrictEnergyHandler(stack);
        if (handler == null) {
            return amount;
        }
        // 模拟接收后剩余的量
        FloatingLong remaining = handler.insertEnergy(amount, Action.SIMULATE);
        if (remaining.smallerThan(amount)) {
            // 物品需要的量=总量-模拟接收后剩余的量
            FloatingLong toExtract = amount.minusEqual(remaining);
            // 实际提取的量
            FloatingLong extracted = energyContainer.extract(toExtract, Action.EXECUTE, AutomationType.MANUAL);
            // 实际插入后剩余的量
            FloatingLong inserted = handler.insertEnergy(extracted, Action.EXECUTE);
            // 返回模拟剩余的量和实际插入后剩余的量的和
            return inserted.plusEqual(remaining);
        }
        return amount;
    }

    @Override
    public void writeSustainedData(CompoundTag data) {
        data.putBoolean(MoreMachineSerializationConstants.CHARGE_EQUIPMENT, getChargeEquipment());
        data.putBoolean(MoreMachineSerializationConstants.CHARGE_INVENTORY, getChargeInventory());
        data.putBoolean(MoreMachineSerializationConstants.CHARGE_CURIOS, getChargeCurios());
    }

    @Override
    public void readSustainedData(CompoundTag data) {
        NBTUtils.setBooleanIfPresent(data, MoreMachineSerializationConstants.CHARGE_EQUIPMENT, value -> chargeEquipment = value);
        NBTUtils.setBooleanIfPresent(data, MoreMachineSerializationConstants.CHARGE_INVENTORY, value -> chargeInventory = value);
        NBTUtils.setBooleanIfPresent(data, MoreMachineSerializationConstants.CHARGE_CURIOS, value -> chargeCurios = value);
    }

    @Override
    public Map<String, String> getTileDataRemap() {
        Map<String, String> remap = new Object2ObjectOpenHashMap<>();
        remap.put(NBTConstants.RADIUS, NBTConstants.RADIUS);
        return remap;
    }

    public void toggleChargeEquipment() {
        chargeEquipment = !chargeEquipment;
        markForSave();
    }

    public void toggleChargeInventory() {
        chargeInventory = !chargeInventory;
        markForSave();
    }

    public void toggleChargeCurios() {
        chargeCurios = !chargeCurios;
        markForSave();
    }

    public boolean getChargeEquipment() {
        return chargeEquipment;
    }

    public boolean getChargeInventory() {
        return chargeInventory;
    }

    public boolean getChargeCurios() {
        return chargeCurios;
    }

    public FloatingLong getOutput() {
        return MoreMachineConfig.general.wirelessChargingStationChargingRate.get().min(energyContainer.getEnergy());
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag nbtTags) {
        super.saveAdditional(nbtTags);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
    }

//    @Override
//    protected void collectImplicitComponents(DataComponentMap.@NotNull Builder builder) {
//        super.collectImplicitComponents(builder);
//        builder.set(MoreMachineDataComponents.CHARGE_EQUIPMENT, getChargeEquipment());
//        builder.set(MoreMachineDataComponents.CHARGE_INVENTORY, getChargeInventory());
//        builder.set(MoreMachineDataComponents.CHARGE_CURIOS, getChargeCurios());
//    }
//
//    @Override
//    protected void applyImplicitComponents(@NotNull DataComponentInput input) {
//        super.applyImplicitComponents(input);
//        chargeEquipment = input.getOrDefault(MoreMachineDataComponents.CHARGE_EQUIPMENT, chargeEquipment);
//        chargeInventory = input.getOrDefault(MoreMachineDataComponents.CHARGE_INVENTORY, chargeInventory);
//        chargeCurios = input.getOrDefault(MoreMachineDataComponents.CHARGE_CURIOS, chargeCurios);
//    }

    @Override
    public void addContainerTrackers(MekanismContainer container) {
        super.addContainerTrackers(container);
        container.track(SyncableBoolean.create(this::getChargeEquipment, value -> chargeEquipment = value));
        container.track(SyncableBoolean.create(this::getChargeInventory, value -> chargeInventory = value));
        container.track(SyncableBoolean.create(this::getChargeCurios, value -> chargeCurios = value));
    }
}
