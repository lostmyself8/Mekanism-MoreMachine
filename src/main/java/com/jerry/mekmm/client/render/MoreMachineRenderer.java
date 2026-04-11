package com.jerry.mekmm.client.render;

import com.jerry.meklm.client.render.tileentity.RenderLargePigmentMixer;

import com.jerry.mekmm.Mekmm;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.TextureAtlasStitchedEvent;

@EventBusSubscriber(modid = Mekmm.MOD_ID, value = Dist.CLIENT)
public class MoreMachineRenderer {

    @SubscribeEvent
    public static void onStitch(TextureAtlasStitchedEvent event) {
        RenderLargePigmentMixer.resetCached();
    }
}
