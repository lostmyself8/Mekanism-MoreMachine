package com.jerry.mekaf.common.attachments.containers.item;

import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.functions.ConstantPredicates;
import mekanism.common.attachments.containers.ContainerType;
import mekanism.common.attachments.containers.creator.BaseContainerCreator;
import mekanism.common.attachments.containers.creator.IBasicContainerCreator;
import mekanism.common.attachments.containers.item.AttachedItems;
import mekanism.common.attachments.containers.item.ComponentBackedInventorySlot;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.integration.energy.EnergyCompatUtils;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.inventory.slot.chemical.ChemicalInventorySlot;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.access.ItemAccess;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class AFItemSlotsBuilder {

    private static final IBasicContainerCreator<ComponentBackedInventorySlot> OUTPUT_SLOT_CREATOR = (type, attachedTo, containerIndex) -> new ComponentBackedInventorySlot(attachedTo,
            containerIndex, ConstantPredicates.alwaysTrueBi(), ConstantPredicates.internalOnly(), ConstantPredicates.alwaysTrue());

    private static final BiPredicate<@NotNull ItemStack, @NotNull AutomationType> FILL_CONVERT_ENERGY_SLOT_CAN_EXTRACT = (stack, automationType) ->
    // Allow extraction if something went horribly wrong, and we are not an energy container item or no longer have any
    // energy left to give,
    // or we are no longer a valid conversion, this might happen after a reload for example
    automationType == AutomationType.MANUAL || !EnergyInventorySlot.fillInsertCheck(stack) && EnergyInventorySlot.getPotentialConversion(null, stack) == 0L;
    private static final BiPredicate<@NotNull ItemStack, @NotNull AutomationType> FILL_CONVERT_ENERGY_SLOT_CAN_INSERT = (stack, automationType) -> {
        if (EnergyInventorySlot.fillInsertCheck(stack)) {
            return true;
        }
        // Note: We recheck about this being empty and that it is still valid as the conversion list might have changed,
        // such as after a reload
        // Unlike with the chemical conversions, we don't check if the type is "valid" as we only have one "type" of
        // energy.
        return EnergyInventorySlot.getPotentialConversion(null, stack) > 0L;
    };
    // Note: we mark all energy handler items as valid and have a more restrictive insert check so that we allow full
    // containers when they are done being filled
    // We also allow energy conversion of items that can be converted
    private static final Predicate<ItemStack> FILL_CONVERT_ENERGY_SLOT_VALIDATOR = stack -> EnergyCompatUtils.hasStrictEnergyHandler(stack) || EnergyInventorySlot.getPotentialConversion(null, stack) > 0L;
    private static final IBasicContainerCreator<ComponentBackedInventorySlot> FILL_CONVERT_ENERGY_SLOT_CREATOR = (type, attachedTo, containerIndex) -> new ComponentBackedInventorySlot(attachedTo,
            containerIndex, FILL_CONVERT_ENERGY_SLOT_CAN_EXTRACT, FILL_CONVERT_ENERGY_SLOT_CAN_INSERT, FILL_CONVERT_ENERGY_SLOT_VALIDATOR);

    public static AFItemSlotsBuilder builder() {
        return new AFItemSlotsBuilder();
    }

    private final List<IBasicContainerCreator<? extends ComponentBackedInventorySlot>> slotCreators = new ArrayList<>();

    private AFItemSlotsBuilder() {}

    public BaseContainerCreator<AttachedItems, ComponentBackedInventorySlot> build() {
        return new AFBaseInventorySlotCreator(slotCreators);
    }

    public AFItemSlotsBuilder addInputFactorySlots(int process, Predicate<ItemStack> recipeInputPredicate) {
        IBasicContainerCreator<ComponentBackedInventorySlot> inputSlotCreator = (type, attachedTo, containerIndex) -> new ComponentBackedInventorySlot(attachedTo, containerIndex,
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

    public AFItemSlotsBuilder addSlot(IBasicContainerCreator<? extends ComponentBackedInventorySlot> slot) {
        slotCreators.add(slot);
        return this;
    }

    private boolean canChemicalFillOrConvertExtract(ItemStack attachedTo, int tankIndex, ItemStack stack) {
        // Copy of logic from ChemicalInventorySlot#getFillOrConvertExtractPredicate
        IChemicalHandler handler = Capabilities.CHEMICAL.getCapability(ItemAccess.forStack(stack));
        IChemicalTank chemicalTank = null;
        if (handler != null) {
            int tanks = handler.getChemicalTanks();
            if (tanks > 0) {
                chemicalTank = ContainerType.CHEMICAL.createContainer(attachedTo, tankIndex);
                for (int tank = 0; tank < tanks; tank++) {
                    if (chemicalTank.isValid(handler.getChemicalInTank(tank))) {
                        // False if the items contents are still valid
                        return false;
                    }
                }
            }
            // Only allow extraction if our item is out of chemical, and doesn't have a valid conversion for it
        }
        // Always allow extraction if something went horribly wrong, and we are not a chemical item AND we can't provide
        // a valid type of chemical
        // This might happen after a reload for example
        ChemicalStack conversion = ChemicalInventorySlot.getPotentialConversion(null, stack);
        if (conversion.isEmpty()) {
            return true;
        } else if (chemicalTank == null) {
            // If we haven't resolved the tank yet, we need to do it now
            chemicalTank = ContainerType.CHEMICAL.createContainer(attachedTo, tankIndex);
        }
        return !chemicalTank.isValid(conversion);
    }

    private boolean canChemicalFillOrConvertInsert(ItemStack attachedTo, int tankIndex, ItemStack stack) {
        // Copy of logic from ChemicalInventorySlot#getFillOrConvertInsertPredicate
        IChemicalTank chemicalTank = null;
        {// Fill insert check logic, we want to avoid resolving the tank as long as possible
            IChemicalHandler handler = Capabilities.CHEMICAL.getCapability(ItemAccess.forStack(stack));
            if (handler != null) {
                for (int tank = 0; tank < handler.getChemicalTanks(); tank++) {
                    ChemicalStack chemicalInTank = handler.getChemicalInTank(tank);
                    if (!chemicalInTank.isEmpty()) {
                        if (chemicalTank == null) {
                            chemicalTank = ContainerType.CHEMICAL.createContainer(attachedTo, tankIndex);
                        }
                        if (chemicalTank.insert(chemicalInTank, Action.SIMULATE, AutomationType.INTERNAL).amount() < chemicalInTank.amount()) {
                            // True if we can fill the tank with any of our contents
                            // Note: We need to recheck the fact the chemical is not empty in case the item has multiple
                            // tanks and only some of the chemicals are valid
                            return true;
                        }
                    }
                }
            }
        }
        ChemicalStack conversion = ChemicalInventorySlot.getPotentialConversion(null, stack);
        // Note: We recheck about this being empty and that it is still valid as the conversion list might have changed,
        // such as after a reload
        if (conversion.isEmpty()) {
            return false;
        } else if (chemicalTank == null) {
            // If we haven't resolved the tank yet, we need to do it now
            chemicalTank = ContainerType.CHEMICAL.createContainer(attachedTo, tankIndex);
        }
        if (chemicalTank.insert(conversion, Action.SIMULATE, AutomationType.INTERNAL).amount() < conversion.amount()) {
            // If we can insert the converted substance into the tank allow insertion
            return true;
        }
        // If we can't because the tank is full, we do a slightly less accurate check and validate that the type matches
        // the stored type
        // and that it is still actually valid for the tank, as a reload could theoretically make it no longer be valid
        // while there is still some stored
        return chemicalTank.getNeeded() == 0 && chemicalTank.isTypeEqual(conversion) && chemicalTank.isValid(conversion);
    }

    public AFItemSlotsBuilder addChemicalFillOrConvertSlot(int tankIndex) {
        return addSlot(((type, attachedTo, containerIndex) -> new ComponentBackedInventorySlot(attachedTo, containerIndex,
                (stack, automationType) -> automationType == AutomationType.MANUAL || canChemicalFillOrConvertExtract(attachedTo, tankIndex, stack),
                (stack, automationType) -> canChemicalFillOrConvertInsert(attachedTo, tankIndex, stack), ConstantPredicates.alwaysTrue())));
    }

    private static class AFBaseInventorySlotCreator extends BaseContainerCreator<AttachedItems, ComponentBackedInventorySlot> {

        public AFBaseInventorySlotCreator(List<IBasicContainerCreator<? extends ComponentBackedInventorySlot>> creators) {
            super(creators);
        }

        @Override
        public AttachedItems initStorage(int containers) {
            return AttachedItems.create(containers);
        }
    }
}
