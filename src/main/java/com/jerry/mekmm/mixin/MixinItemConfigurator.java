package com.jerry.mekmm.mixin;

import com.jerry.meklm.common.tile.INotNeedConfig;

import mekanism.common.item.ItemConfigurator;
import mekanism.common.util.WorldUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = ItemConfigurator.class, remap = false)
public class MixinItemConfigurator {

    @Unique
    private BlockEntity mekmm$tile;

    @Inject(method = "useOn", at = @At(value = "INVOKE", target = "Lmekanism/common/util/WorldUtils;getTileEntity(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void getTile(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir, Player player, Level world, BlockPos pos, Direction side) {
        mekmm$tile = WorldUtils.getTileEntity(world, pos);
    }

    @Inject(method = "useOn", at = @At(value = "INVOKE", target = "Lmekanism/common/tile/interfaces/ISideConfiguration;getConfig()Lmekanism/common/tile/component/TileComponentConfig;", shift = At.Shift.BY, ordinal = 1), cancellable = true)
    private void mixinUseOn(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        if (mekmm$tile instanceof INotNeedConfig need) {
            if (need.notNeedConfig()) {
                cir.setReturnValue(InteractionResult.FAIL);
            }
        }
    }
}
