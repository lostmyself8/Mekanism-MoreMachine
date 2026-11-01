package com.jerry.mekmm.mixin;

import com.jerry.mekmm.api.loot.IConditionalLootTable;

import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraftforge.common.crafting.conditions.ICondition;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(LootTable.class)
public class MixinLootTable implements IConditionalLootTable {

    @Unique
    private List<ICondition> mekmm$conditions = List.of();

    @Override
    public void mekmm$setConditions(List<ICondition> conditions) {
        mekmm$conditions = List.copyOf(conditions);
    }

    @Override
    public List<ICondition> mekmm$getConditions() {
        return mekmm$conditions;
    }
}
