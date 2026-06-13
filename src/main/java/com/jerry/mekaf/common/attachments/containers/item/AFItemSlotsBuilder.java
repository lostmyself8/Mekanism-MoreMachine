package com.jerry.mekaf.common.attachments.containers.item;

import mekanism.api.AutomationType;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.functions.ConstantPredicates;
import mekanism.api.inventory.IInventorySlot;
import mekanism.api.resource.LargeResourceStack;
import mekanism.common.component.containers.creator.BaseContainerCreator;
import mekanism.common.component.containers.creator.IBasicContainerCreator;
import mekanism.common.component.containers.item.ComponentBackedInventorySlot;
import mekanism.common.component.containers.resource.AttachedResources;
import mekanism.common.component.containers.resource.ResourceContainersBuilder.BaseContainerBuilder;
import mekanism.common.component.containers.type.ContainerType;
import mekanism.common.inventory.slot.BasicInventorySlot;
import mekanism.common.inventory.slot.ChemicalInventorySlot;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.util.EnergyUtils;
import mekanism.common.util.ItemAccessUtils;

import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemResource;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class AFItemSlotsBuilder {

    private static final IBasicContainerCreator<IInventorySlot> OUTPUT_SLOT_CREATOR = (attachedTo, containerIndex) -> new ComponentBackedInventorySlot(attachedTo,
            containerIndex, ConstantPredicates.alwaysTrueBi(), ConstantPredicates.internalOnly(), ConstantPredicates.alwaysTrue());

    private static final BiPredicate<@NotNull ItemResource, @NotNull AutomationType> FILL_CONVERT_ENERGY_SLOT_CAN_EXTRACT = (itemType, automationType) ->
    // Allow extraction if something went horribly wrong, and we are not an energy container item or no longer have any
    // energy left to give,
    // or we are no longer a valid conversion, this might happen after a reload for example
    !automationType.isExternal() || !EnergyInventorySlot.canFillOrConvert(null, BasicInventorySlot.NO_LEVEL, itemType);
    private static final BiPredicate<@NotNull ItemResource, @NotNull AutomationType> FILL_CONVERT_ENERGY_SLOT_CAN_INSERT = (itemType, automationType) -> {
        if (automationType.isInternal()) {
            return true;
        }
        IEnergyContainer energyContainer = EnergyUtils.getEnergyContainer(ContainerType.ENERGY.getCapOrUnexposed(ItemAccessUtils.sideEffectFreeAccess(itemType)));
        return EnergyInventorySlot.canFillOrConvert(energyContainer, BasicInventorySlot.NO_LEVEL, itemType);
    };
    // Note: we mark all energy handler items as valid and have a more restrictive insert check so that we allow full
    // containers when they are done being filled
    // We also allow energy conversion of items that can be converted
    private static final IBasicContainerCreator<IInventorySlot> FILL_CONVERT_ENERGY_SLOT_CREATOR = (attachedTo, containerIndex) -> new ComponentBackedInventorySlot(attachedTo,
            containerIndex, FILL_CONVERT_ENERGY_SLOT_CAN_EXTRACT, FILL_CONVERT_ENERGY_SLOT_CAN_INSERT, ConstantPredicates.alwaysTrue());

    public static AFItemSlotsBuilder builder() {
        return new AFItemSlotsBuilder();
    }

    private final List<IBasicContainerCreator<IInventorySlot>> slotCreators = new ArrayList<>();

    private AFItemSlotsBuilder() {}

    public BaseContainerCreator<AttachedResources<ItemResource>, IInventorySlot> build() {
        return new BaseContainerBuilder<>(slotCreators, LargeResourceStack.ITEM_HELPER);
    }

    public AFItemSlotsBuilder addInputFactorySlots(int process, Predicate<ItemResource> recipeInputPredicate) {
        IBasicContainerCreator<IInventorySlot> inputSlotCreator = (attachedTo, containerIndex) -> new ComponentBackedInventorySlot(attachedTo, containerIndex,
                ConstantPredicates.notExternal(), ConstantPredicates.alwaysTrueBi(), recipeInputPredicate);
        for (int i = 0; i < process; i++) {
            // Note: We can just get away with using a simple input instead of a factory input slot and skip checking
            // insert based on producing output
            addSlot(inputSlotCreator);
        }
        return this;
    }

    public AFItemSlotsBuilder addOutputFactorySlots(int process) {
        for (int i = 0; i < process; i++) {
            addOutput();
        }
        return this;
    }

    public AFItemSlotsBuilder addOutput() {
        return addSlot(OUTPUT_SLOT_CREATOR);
    }

    public AFItemSlotsBuilder addEnergy() {
        return addSlot(FILL_CONVERT_ENERGY_SLOT_CREATOR);
    }

    public AFItemSlotsBuilder addSlot(IBasicContainerCreator<IInventorySlot> slot) {
        slotCreators.add(slot);
        return this;
    }

    private boolean canChemicalFillOrConvertExtract(ItemAccess attachedTo, int tankIndex, ItemResource itemType) {
        return !ChemicalInventorySlot.canFillOrConvert(ContainerType.CHEMICAL.createContainer(attachedTo, tankIndex), BasicInventorySlot.NO_LEVEL, itemType);
    }

    private boolean canChemicalFillOrConvertInsert(ItemAccess attachedTo, int tankIndex, ItemResource itemType) {
        return ChemicalInventorySlot.canFillOrConvert(ContainerType.CHEMICAL.createContainer(attachedTo, tankIndex), BasicInventorySlot.NO_LEVEL, itemType);
    }

    public AFItemSlotsBuilder addChemicalFillOrConvertSlot(int tankIndex) {
        return addSlot(((attachedTo, containerIndex) -> new ComponentBackedInventorySlot(attachedTo, containerIndex,
                (itemType, automationType) -> !automationType.isExternal() || canChemicalFillOrConvertExtract(attachedTo, tankIndex, itemType),
                (itemType, automationType) -> automationType.isInternal() || canChemicalFillOrConvertInsert(attachedTo, tankIndex, itemType), ConstantPredicates.alwaysTrue())));
    }
}
