package com.jerry.mekmm.mixin;

import com.jerry.mekmm.api.loot.IConditionalLootTable;

import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTable.Serializer;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Type;
import java.util.List;

@Mixin(Serializer.class)
public class MixinLootTableSerializer {

    @Inject(method = "serialize(Lnet/minecraft/world/level/storage/loot/LootTable;Ljava/lang/reflect/Type;Lcom/google/gson/JsonSerializationContext;)Lcom/google/gson/JsonElement;", at = @At("RETURN"), remap = false)
    private void mekmm$serializeConditions(LootTable table, Type type, JsonSerializationContext context,
                                           CallbackInfoReturnable<JsonElement> cir) {
        List<ICondition> conditions = ((IConditionalLootTable) table).mekmm$getConditions();
        if (conditions.isEmpty()) {
            return;
        }
        JsonElement serialized = cir.getReturnValue();
        if (!serialized.isJsonObject()) {
            throw new IllegalStateException("Loot table serializer returned a non-object for " + table);
        }
        JsonObject json = serialized.getAsJsonObject();
        json.add("forge:conditions", CraftingHelper.serialize(conditions.toArray(ICondition[]::new)));
    }
}
