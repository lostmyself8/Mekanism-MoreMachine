package com.jerry.mekmm.mixin.client.render;

import mekanism.client.render.RenderTickHandler;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = RenderTickHandler.class, remap = false)
public class MixinRenderTickHandler {

    // @Unique
    // private BlockEntity mekmm$tile;
    //
    // @Inject(method = "onBlockHover", at = @At(value = "INVOKE", target =
    // "Lmekanism/common/util/WorldUtils;getTileEntity(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;",
    // shift = At.Shift.BY, ordinal = 1), locals = LocalCapture.CAPTURE_FAILSOFT)
    // private void getTile(RenderHighlightEvent.Block event, CallbackInfo ci, Player player, BlockHitResult
    // rayTraceResult, Level world, BlockPos pos, MultiBufferSource renderer, Camera info, PoseStack matrix,
    // ProfilerFiller profiler, BlockState blockState, boolean shouldCancel, ItemStack stack,
    // ItemConfigurator.ConfiguratorMode state, TransmissionType type) {
    // mekmm$tile = WorldUtils.getTileEntity(world, pos);
    // }
    //
    // @Inject(method = "onBlockHover", at = @At(value = "INVOKE", target =
    // "Lmekanism/common/tile/interfaces/ISideConfiguration;getConfig()Lmekanism/common/tile/component/TileComponentConfig;"),
    // cancellable = true)
    // private void mixinOnBlockHover(RenderHighlightEvent.Block event, CallbackInfo ci) {
    // if (mekmm$tile instanceof INotNeedConfig need) {
    // if (need.notNeedConfig()) {
    // ci.cancel();
    // }
    // }
    // }
}
