package com.jerry.mekmm.api.loot;

import net.minecraftforge.common.crafting.conditions.ICondition;

import java.util.List;

/**
 * Exposes Forge conditions attached to a generated loot table.
 *
 * <p>
 * The interface is implemented by the loot table mixin so the datagen builder can
 * transfer conditions to the final vanilla {@code LootTable} instance without reflection.
 * </p>
 */
public interface IConditionalLootTable {

    /**
     * Sets the conditions that must pass before this loot table is loaded.
     *
     * @param conditions immutable conditions to serialize and evaluate
     */
    void mekmm$setConditions(List<ICondition> conditions);

    /**
     * Returns the conditions attached to this loot table.
     *
     * @return immutable condition list, possibly empty
     */
    List<ICondition> mekmm$getConditions();
}
