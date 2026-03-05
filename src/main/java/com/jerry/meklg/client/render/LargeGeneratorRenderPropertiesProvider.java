package com.jerry.meklg.client.render;

import mekanism.client.render.RenderPropertiesProvider;

import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import com.jerry.meklg.client.render.item.RenderLargeWindGeneratorItem;

public class LargeGeneratorRenderPropertiesProvider {

    private LargeGeneratorRenderPropertiesProvider() {}

    public static IClientItemExtensions wind() {
        return new RenderPropertiesProvider.MekRenderProperties(RenderLargeWindGeneratorItem.RENDERER);
    }
}
