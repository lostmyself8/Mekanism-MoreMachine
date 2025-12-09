package com.jerry.mekmm.mixin;

import mekanism.common.item.ItemConfigurator;
import mekanism.common.util.WorldUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import com.jerry.meklm.api.INeedConfig;
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
    private void getTile(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir, Player player, Level world, BlockPos pos, Direction side, ItemStack stack) {
        mekmm$tile = WorldUtils.getTileEntity(world, pos);
    }

    @Inject(method = "useOn", at = @At(value = "INVOKE", target = "Lmekanism/common/tile/interfaces/ISideConfiguration;getConfig()Lmekanism/common/tile/component/TileComponentConfig;", shift = At.Shift.BY, ordinal = 1), cancellable = true)
    private void mixinUseOn(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        if (mekmm$tile instanceof INeedConfig need) {
            if (!need.needConfig()) {
                cir.setReturnValue(InteractionResult.FAIL);
            }
        }
    }
}
