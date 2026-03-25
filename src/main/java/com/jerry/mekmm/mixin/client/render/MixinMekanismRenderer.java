package com.jerry.mekmm.mixin.client.render;

import mekanism.client.render.MekanismRenderer;

import net.minecraftforge.client.event.TextureStitchEvent.Post;

import com.jerry.meklm.client.render.tileentity.RenderLargePigmentMixer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MekanismRenderer.class, remap = false)
public class MixinMekanismRenderer {

    @Inject(method = "onStitch", at = @At(value = "INVOKE", target = "Lmekanism/client/render/tileentity/RenderPigmentMixer;resetCached()V"))
    private static void onStitch(Post event, CallbackInfo ci) {
        RenderLargePigmentMixer.resetCached();
    }
}
