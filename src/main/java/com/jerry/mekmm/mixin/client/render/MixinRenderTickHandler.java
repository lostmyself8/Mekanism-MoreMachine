package com.jerry.mekmm.mixin.client.render;

import mekanism.client.render.RenderTickHandler;
import mekanism.common.item.ItemConfigurator;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.util.WorldUtils;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.event.RenderHighlightEvent;

import com.jerry.meklm.api.INeedConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = RenderTickHandler.class, remap = false)
public class MixinRenderTickHandler {

    @Unique
    private BlockEntity mekmm$tile;

    @Inject(method = "onBlockHover", at = @At(value = "INVOKE", target = "Lmekanism/common/util/WorldUtils;getTileEntity(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;", shift = At.Shift.BY, ordinal = 1), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void getTile(RenderHighlightEvent.Block event, CallbackInfo ci, Player player, BlockHitResult rayTraceResult, Level world, BlockPos pos, MultiBufferSource renderer, Camera info, PoseStack matrix, ProfilerFiller profiler, BlockState blockState, boolean shouldCancel, ItemStack stack, ItemConfigurator.ConfiguratorMode state, TransmissionType type) {
        mekmm$tile = WorldUtils.getTileEntity(world, pos);
    }

    @Inject(method = "onBlockHover", at = @At(value = "INVOKE", target = "Lmekanism/common/tile/interfaces/ISideConfiguration;getConfig()Lmekanism/common/tile/component/TileComponentConfig;"), cancellable = true)
    private void mixinOnBlockHover(RenderHighlightEvent.Block event, CallbackInfo ci) {
        if (mekmm$tile instanceof INeedConfig need) {
            if (!need.needConfig()) {
                ci.cancel();
            }
        }
    }
}
