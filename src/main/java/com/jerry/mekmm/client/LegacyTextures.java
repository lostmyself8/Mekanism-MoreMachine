package com.jerry.mekmm.client;

import mekanism.common.Mekanism;

import net.minecraft.resources.Identifier;

public final class LegacyTextures {

    private LegacyTextures() {}

    public enum ResourceType {
        GUI,
        GUI_BUTTON,
        GUI_BAR,
        GUI_ELEMENT,
        SOUND,
        TEXTURE,
        MODEL,
        RENDER
    }

    public static Identifier getResource(ResourceType type, String path) {
        return Mekanism.rl(switch (type) {
            case GUI -> "textures/gui/sprites/";
            case GUI_BUTTON -> "textures/gui/sprites/button/";
            case GUI_BAR -> "textures/gui/sprites/bar/";
            case GUI_ELEMENT -> "textures/gui/sprites/element/";
            case SOUND -> "sounds/";
            case TEXTURE -> "textures/";
            case MODEL -> "models/";
            case RENDER -> "textures/render/";
        } + path);
    }
}
