package com.jerry.mekmm.mixin;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.crafting.conditions.ICondition;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ForgeHooks.class, remap = false)
public class MixinForgeHooks {

    @Inject(method = "loadLootTable", at = @At("HEAD"), cancellable = true, remap = false)
    private static void mekmm$skipDisabledLootTable(Gson gson, ResourceLocation id, JsonElement data, boolean custom,
                                                    CallbackInfoReturnable<LootTable> cir) {
        if (!ICondition.shouldRegisterEntry(data)) {
            cir.setReturnValue(null);
        }
    }
}
