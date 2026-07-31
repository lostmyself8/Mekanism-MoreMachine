package com.jerry.mekmm.mixin;

import mekanism.api.Upgrade;
import mekanism.common.util.UpgradeUtils;

import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = UpgradeUtils.class, remap = false)
public abstract class MixinUpgradeUtils {

    @Inject(method = "getItem", at = @At(value = "HEAD"))
    private static void getItem(Upgrade upgrade, CallbackInfoReturnable<Holder<Item>> cir) {
        // switch (upgrade.toString()) {
        // case "THREAD" -> cir.setReturnValue(MMItems.THREAD_UPGRADE);
        // default -> throw new IllegalStateException(String.valueOf(ExtraUpgrade.STACK.ordinal()));
        // }
    }
}
