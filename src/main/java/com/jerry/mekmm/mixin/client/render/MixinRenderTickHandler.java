package com.jerry.mekmm.mixin.client.render;

import com.jerry.meklm.common.tile.IConfiguratorExempt;

import mekanism.client.render.RenderTickHandler;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.client.event.RenderHighlightEvent.Block;

import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RenderTickHandler.class, remap = false)
public class MixinRenderTickHandler {

    @Inject(method = "onBlockHover", at = @At(value = "INVOKE", target = "Lmekanism/common/tile/interfaces/ISideConfiguration;getConfig()Lmekanism/common/tile/component/TileComponentConfig;"), cancellable = true)
    private void mixinOnBlockHover(Block event, CallbackInfo ci, @Local(name = "tile") BlockEntity tile) {
        if (tile instanceof IConfiguratorExempt exempt && exempt.isConfiguratorExempt()) {
            ci.cancel();
        }
    }
}
