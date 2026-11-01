package com.jerry.datagen.common.loot.builder;

import com.jerry.mekmm.api.loot.IConditionalLootTable;

import mekanism.api.annotations.NothingNullByDefault;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTable.Builder;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraftforge.common.crafting.conditions.ICondition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Loot table builder that carries Forge data-loading conditions to the final table.
 */
@NothingNullByDefault
public class ConditionalLootTableBuilder extends Builder {

    private final List<ICondition> conditions = new ArrayList<>();

    /**
     * Creates an empty conditional loot table builder.
     *
     * @return new builder instance
     */
    public static ConditionalLootTableBuilder lootTable() {
        return new ConditionalLootTableBuilder();
    }

    /**
     * Adds a condition that must pass before this table is loaded.
     *
     * @param condition Forge condition to add
     * @return this builder
     */
    public ConditionalLootTableBuilder addCondition(ICondition condition) {
        conditions.add(Objects.requireNonNull(condition, "condition"));
        return this;
    }

    /**
     * Adds several Forge conditions in declaration order.
     *
     * @param conditions conditions to add
     * @return this builder
     */
    public ConditionalLootTableBuilder addConditions(Collection<? extends ICondition> conditions) {
        conditions.forEach(this::addCondition);
        return this;
    }

    @Override
    public ConditionalLootTableBuilder withPool(LootPool.Builder poolBuilder) {
        super.withPool(poolBuilder);
        return this;
    }

    @Override
    public ConditionalLootTableBuilder setParamSet(LootContextParamSet paramSet) {
        super.setParamSet(paramSet);
        return this;
    }

    @Override
    public ConditionalLootTableBuilder setRandomSequence(ResourceLocation randomSequence) {
        super.setRandomSequence(randomSequence);
        return this;
    }

    @Override
    public ConditionalLootTableBuilder apply(LootItemFunction.Builder functionBuilder) {
        super.apply(functionBuilder);
        return this;
    }

    @Override
    public ConditionalLootTableBuilder unwrap() {
        return this;
    }

    @Override
    public LootTable build() {
        LootTable table = super.build();
        ((IConditionalLootTable) table).mekmm$setConditions(List.copyOf(conditions));
        return table;
    }
}
