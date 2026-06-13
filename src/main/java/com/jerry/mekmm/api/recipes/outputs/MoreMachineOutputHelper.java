package com.jerry.mekmm.api.recipes.outputs;

import com.jerry.mekmm.api.recipes.RecyclerRecipe.ChanceOutput;

import mekanism.api.AutomationType;
import mekanism.api.inventory.IInventorySlot;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.outputs.IOutputHandler;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public class MoreMachineOutputHelper {

    private MoreMachineOutputHelper() {}

    public static IOutputHandler<ChanceOutput> getOutputHandler(IInventorySlot chanceSlot, CachedRecipe.OperationTracker.RecipeError chanceSlotNotEnoughSpaceError) {
        Objects.requireNonNull(chanceSlot, "Chance slot cannot be null.");
        Objects.requireNonNull(chanceSlotNotEnoughSpaceError, "Chance slot not enough space error cannot be null.");

        return new IOutputHandler<>() {

            @Override
            public boolean handleOutput(ChanceOutput toOutput, int operations, TransactionContext transaction) {
                if (toOutput == null) {
                    return false;
                } else if (operations == 0) {
                    return true;
                }
                ItemStack chanceOutput = toOutput.getChanceOutput();
                for (int i = 0; i < operations; i++) {
                    if (!MoreMachineOutputHelper.handleOutput(chanceSlot, chanceOutput, 1, transaction)) {
                        return false;
                    }
                    if (i < operations - 1) {
                        chanceOutput = toOutput.nextChanceOutput();
                    }
                }
                return true;
            }

            @Override
            public void calculateOperationsCanSupport(CachedRecipe.OperationTracker tracker, ChanceOutput toOutput) {
                MoreMachineOutputHelper.calculateOperationsCanSupport(tracker, chanceSlotNotEnoughSpaceError, chanceSlot, toOutput.getMaxChanceOutput());
            }
        };
    }

    private static boolean handleOutput(IInventorySlot inventorySlot, ItemStack toOutput, int operations, TransactionContext transaction) {
        if (operations == 0 || toOutput.isEmpty()) {
            return !toOutput.isEmpty();
        }
        ItemStack output = toOutput.copy();
        if (operations > 1) {
            // If we are doing more than one operation we need to make a copy of our stack and change the amount
            // that we are using the fill the tank with
            output.setCount(output.getCount() * operations);
        }
        return inventorySlot.insert(ItemResource.of(output), output.getCount(), transaction, AutomationType.INTERNAL) == output.getCount();
    }

    private static void calculateOperationsCanSupport(CachedRecipe.OperationTracker tracker, CachedRecipe.OperationTracker.RecipeError notEnoughSpace, IInventorySlot slot, ItemStack toOutput) {
        // If our output is empty, we have nothing to add, so we treat it as being able to fit all
        if (!toOutput.isEmpty()) {
            // Make a copy of the stack we are outputting with its maximum size
            ItemStack output = toOutput.copyWithCount(toOutput.getMaxStackSize());
            int amountUsed;
            try (Transaction transaction = Transaction.openRoot()) {
                amountUsed = slot.insert(ItemResource.of(output), output.getCount(), transaction, AutomationType.INTERNAL);
            }
            // Divide the amount we can actually use by the amount one output operation is equal to, capping it at the
            // max we were told about
            int operations = amountUsed / toOutput.getCount();
            tracker.updateOperations(operations);
            if (operations == 0) {
                if (amountUsed == 0 && slot.capacityAsInt(ItemResource.of(toOutput)) - slot.amountAsInt() > 0) {
                    tracker.addError(CachedRecipe.OperationTracker.RecipeError.INPUT_DOESNT_PRODUCE_OUTPUT);
                } else {
                    tracker.addError(notEnoughSpace);
                }
            }
        }
    }
}
