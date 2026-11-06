package com.jerry.mekmm.mixin;

import com.jerry.meklm.common.tile.IConfiguratorExempt;

import mekanism.common.item.ItemConfigurator;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;

import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemConfigurator.class, remap = false)
public class MixinItemConfigurator {

    @Inject(method = "useOn", at = @At(value = "INVOKE", target = "Lmekanism/common/tile/interfaces/ISideConfiguration;getConfig()Lmekanism/common/tile/component/TileComponentConfig;", shift = At.Shift.BY, ordinal = 1), cancellable = true)
    private void mixinUseOn(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir, @Local(name = "tile") BlockEntity tile) {
        if (tile instanceof IConfiguratorExempt exempt && exempt.isConfiguratorExempt()) {
            cir.setReturnValue(InteractionResult.FAIL);
        }
    }
}
