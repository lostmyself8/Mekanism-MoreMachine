package com.jerry.meklm.common.base.holiday.holiday_info;

import mekanism.client.render.lib.QuadTransformation;

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.Identifier;

public abstract class BaseHolidayInfo {

    protected BaseHolidayInfo() {}

    protected static QuadTransformation suffixTexture(TextureAtlas atlas, Identifier screenNamespace, String screen) {
        return texture(atlas, screenNamespace.withSuffix(screen));
    }

    protected static QuadTransformation texture(TextureAtlas atlas, Identifier location) {
        return QuadTransformation.texture(atlas.getSprite(location));
    }
}
