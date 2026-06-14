package com.jerry.mekmm.common.tile.machine;

import com.jerry.mekmm.api.MoreMachineSerializationConstants;
import com.jerry.mekmm.common.config.MoreMachineConfig;
import com.jerry.mekmm.common.integration.computer.ComputerEnergyContainerWrapper;
import com.jerry.mekmm.common.registries.MoreMachineBlocks;
import com.jerry.mekmm.common.registries.MoreMachineDataComponents;

import mekanism.api.IContentsListener;
import mekanism.api.RelativeSide;
import mekanism.api.inventory.IInventorySlot;
import mekanism.common.Mekanism;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.capabilities.holder.container.IContainerHolder;
import mekanism.common.capabilities.holder.container.MekContainerHelper;
import mekanism.common.capabilities.holder.energy.EnergyConfigHolder;
import mekanism.common.capabilities.holder.energy.IEnergyContainerHolder;
import mekanism.common.capabilities.proxy.AutomatedEnergyHandler;
import mekanism.common.config.MekanismConfig;
import mekanism.common.integration.computer.ComputerException;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod;
import mekanism.common.integration.curios.CuriosIntegration;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.inventory.container.slot.SlotOverlay;
import mekanism.common.inventory.container.sync.SyncableBoolean;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.tile.component.config.ConfigInfo;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.tile.interfaces.IBoundingBlock;
import mekanism.common.tile.prefab.TileEntityConfigurableMachine;
import mekanism.common.util.EnergyUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public class TileEntityWirelessChargingStation extends TileEntityConfigurableMachine implements IBoundingBlock {

    @WrappingComputerMethod(wrapper = ComputerEnergyContainerWrapper.class, methodNames = { "getEnergy", "getEnergyCapacity", "getEnergyNeeded", "getEnergyFilledPercentage" }, docPlaceholder = "energy container")
    MachineEnergyContainer<TileEntityWirelessChargingStation> energyContainer;

    private boolean chargeEquipment = false;
    private boolean chargeInventory = false;
    private boolean chargeCurios = false;

    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getChargeItem", docPlaceholder = "charge slot")
    EnergyInventorySlot chargeSlot;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getDischargeItem", docPlaceholder = "discharge slot")
    EnergyInventorySlot dischargeSlot;

    public TileEntityWirelessChargingStation(BlockPos pos, BlockState state) {
        super(MoreMachineBlocks.WIRELESS_CHARGING_STATION, pos, state);
        configComponent.setupIOConfig(TransmissionType.ITEM, chargeSlot, dischargeSlot, true).setCanEject(false);
        setupIOConfig(TransmissionType.ENERGY, energyContainer, RelativeSide.FRONT);
        configComponent.addDisabledSides(RelativeSide.TOP);
        ejectorComponent.setOutputData(configComponent, TransmissionType.ITEM, TransmissionType.ENERGY).setCanEject(type -> canFunction());
    }

    private <CONTAINER> ConfigInfo setupIOConfig(TransmissionType type, CONTAINER container, RelativeSide side) {
        ConfigInfo config = configComponent.setupIOConfig(type, container);
        if (config != null) {
            config.setDataType(DataType.INPUT_OUTPUT, side);
        }
        return config;
    }

    @Override
    protected @Nullable IEnergyContainerHolder getInitialEnergyContainer(IContentsListener listener) {
        energyContainer = MachineEnergyContainer.input(this, listener);
        return new EnergyConfigHolder(energyContainer, this);
    }

    @NotNull
    @Override
    protected IContainerHolder<IInventorySlot> getInitialInventory(IContentsListener listener) {
        MekContainerHelper<IInventorySlot> builder = MekContainerHelper.forSideWithItemConfig(this);
        builder.addContainer(dischargeSlot = EnergyInventorySlot.fillOrConvert(energyContainer, this::getLevel, listener, 17, 35));
        builder.addContainer(chargeSlot = EnergyInventorySlot.drain(energyContainer, listener, 143, 35));
        dischargeSlot.setSlotOverlay(SlotOverlay.MINUS);
        chargeSlot.setSlotOverlay(SlotOverlay.PLUS);
        return builder.build();
    }

    public MachineEnergyContainer<TileEntityWirelessChargingStation> getEnergyContainerTyped() {
        return energyContainer;
    }

    @Override
    protected boolean onUpdateServer(ServerLevel level) {
        boolean sendUpdatePacket = super.onUpdateServer(level);
        chargeSlot.drainContainerIntoSlot(null);
        dischargeSlot.fillContainerOrConvert(null);
        if (!energyContainer.isEmpty() && canFunction()) {
            Level currentLevel = getLevel();
            UUID uuid = getOwnerUUID();
            if (level != null && uuid != null) {
                Player player = level.getPlayerByUUID(uuid);
                if (player == null) {
                    return sendUpdatePacket;
                }
                long maxChargeRate = MoreMachineConfig.general.wirelessChargingStationChargingRate.get();
                long availableEnergy = energyContainer.getAmountAsLong();
                long toCharge = Math.min(maxChargeRate, availableEnergy);
                if (toCharge > 0L) {
                    // 优先充能盔甲，其次是主副手和饰品，最后是物品栏
                    if (chargeEquipment) {
                        toCharge = chargeSuit(player, toCharge);
                    }
                    if (toCharge > 0L && chargeInventory) {
                        toCharge = chargeInventory(player, toCharge);
                    }
                    if (toCharge > 0L && chargeCurios) {
                        chargeCurios(player, toCharge);
                    }
                }
            }
        }
        return sendUpdatePacket;
    }

    private long chargeSuit(Player player, long toCharge) {
        for (EquipmentSlot slot : EquipmentSlot.VALUES) {
            if (!slot.isArmor()) {
                continue;
            }
            ItemStack stack = player.getItemBySlot(slot);
            // charge方法会检测是否是含能量槽的物品
            toCharge = charge(energyContainer, stack, toCharge);
            if (toCharge == 0L) break;
        }
        return toCharge;
    }

    private long chargeInventory(Player player, long toCharge) {
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();
        toCharge = charge(energyContainer, mainHand, toCharge);
        toCharge = charge(energyContainer, offHand, toCharge);
        if (toCharge > 0L) {
            for (ItemStack stack : player.getInventory().getNonEquipmentItems()) {
                if (stack != mainHand && stack != offHand) {
                    toCharge = charge(energyContainer, stack, toCharge);
                    if (toCharge == 0L) break;
                }
            }

        }
        return toCharge;
    }

    private void chargeCurios(Player player, long toCharge) {
        if (Mekanism.hooks.curios.isLoaded()) {
            ResourceHandler<ItemResource> handler = CuriosIntegration.getCuriosInventory(player);
            if (handler == null) {
                return;
            }
            for (int slot = 0, slots = handler.size(); slot < slots; slot++) {
                toCharge = chargeCurio(handler, slot, toCharge);
                if (toCharge == 0L) {
                    return;
                }
            }
        }
    }

    private long chargeCurio(ResourceHandler<ItemResource> handler, int slot, long amount) {
        if (amount <= 0L) {
            return amount;
        }
        ItemStack stack = handler.getResource(slot).toStack(handler.getAmountAsInt(slot));
        if (stack.isEmpty()) {
            return amount;
        }
        EnergyHandler energySource = AutomatedEnergyHandler.manual(energyContainer);
        if (energySource == null) {
            return amount;
        }
        ItemResource originalResource = handler.getResource(slot);
        int count = handler.getAmountAsInt(slot);
        try (Transaction transaction = Transaction.openRoot()) {
            int charged = EnergyUtils.charge(energySource, ItemAccess.forHandlerIndex(handler, slot), Math.toIntExact(Math.min(Integer.MAX_VALUE, amount)), transaction);
            if (charged > 0 && handler.extract(slot, originalResource, count, transaction) == count &&
                    handler.insert(slot, ItemResource.of(stack), count, transaction) == count) {
                transaction.commit();
                return amount - charged;
            }
        }
        return amount;
    }

    private long charge(MachineEnergyContainer<TileEntityWirelessChargingStation> energyContainer, ItemStack stack, long amount) {
        if (stack.isEmpty() || amount <= 0L) {
            return amount;
        }
        EnergyHandler energySource = AutomatedEnergyHandler.manual(energyContainer);
        if (energySource == null) {
            return amount;
        }
        try (Transaction transaction = Transaction.openRoot()) {
            int charged = EnergyUtils.charge(energySource, ItemAccess.forStack(stack), Math.toIntExact(Math.min(Integer.MAX_VALUE, amount)), transaction);
            if (charged > 0) {
                transaction.commit();
                return amount - charged;
            }
        }
        return amount;
    }

    @Override
    public void writeSustainedData(@NotNull ValueOutput output) {
        super.writeSustainedData(output);
        output.putBoolean(MoreMachineSerializationConstants.CHARGE_EQUIPMENT, getChargeEquipment());
        output.putBoolean(MoreMachineSerializationConstants.CHARGE_INVENTORY, getChargeInventory());
        output.putBoolean(MoreMachineSerializationConstants.CHARGE_CURIOS, getChargeCurios());
    }

    @Override
    public void readSustainedData(@NotNull ValueInput input) {
        super.readSustainedData(input);
        chargeEquipment = input.getBooleanOr(MoreMachineSerializationConstants.CHARGE_EQUIPMENT, chargeEquipment);
        chargeInventory = input.getBooleanOr(MoreMachineSerializationConstants.CHARGE_INVENTORY, chargeInventory);
        chargeCurios = input.getBooleanOr(MoreMachineSerializationConstants.CHARGE_CURIOS, chargeCurios);
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

    public long getOutput() {
        return Math.min(MekanismConfig.gear.mekaSuitInventoryChargeRate.get(), energyContainer.getAmountAsLong());
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.@NotNull Builder builder) {
        super.collectImplicitComponents(builder);
        builder.set(MoreMachineDataComponents.CHARGE_EQUIPMENT, getChargeEquipment());
        builder.set(MoreMachineDataComponents.CHARGE_INVENTORY, getChargeInventory());
        builder.set(MoreMachineDataComponents.CHARGE_CURIOS, getChargeCurios());
    }

    @Override
    protected void applyImplicitComponents(@NotNull DataComponentGetter input) {
        super.applyImplicitComponents(input);
        chargeEquipment = input.getOrDefault(MoreMachineDataComponents.CHARGE_EQUIPMENT, chargeEquipment);
        chargeInventory = input.getOrDefault(MoreMachineDataComponents.CHARGE_INVENTORY, chargeInventory);
        chargeCurios = input.getOrDefault(MoreMachineDataComponents.CHARGE_CURIOS, chargeCurios);
    }

    @Override
    public void addContainerTrackers(MekanismContainer container) {
        super.addContainerTrackers(container);
        container.track(SyncableBoolean.create(this::getChargeEquipment, v -> chargeEquipment = v));
        container.track(SyncableBoolean.create(this::getChargeInventory, v -> chargeInventory = v));
        container.track(SyncableBoolean.create(this::getChargeCurios, v -> chargeCurios = v));
    }

    // Methods relating to IComputerTile
    @ComputerMethod(requiresPublicSecurity = true, methodDescription = "Set whether to charge equipment")
    void setChargeEquipment(boolean charge) throws ComputerException {
        validateSecurityIsPublic();
        if (chargeEquipment != charge) {
            chargeEquipment = charge;
            markForSave();
        }
    }

    @ComputerMethod(requiresPublicSecurity = true, methodDescription = "Set whether to charge inventory")
    void setChargeInventory(boolean charge) throws ComputerException {
        validateSecurityIsPublic();
        if (chargeInventory != charge) {
            chargeInventory = charge;
            markForSave();
        }
    }

    @ComputerMethod(requiresPublicSecurity = true, methodDescription = "Set whether to charge curios")
    void setChargeCurios(boolean charge) throws ComputerException {
        validateSecurityIsPublic();
        if (chargeCurios != charge) {
            chargeCurios = charge;
            markForSave();
        }
    }
    // End methods IComputerTile
}
