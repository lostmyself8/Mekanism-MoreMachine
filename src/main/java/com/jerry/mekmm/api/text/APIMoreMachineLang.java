package com.jerry.mekmm.api.text;

import com.jerry.mekmm.Mekmm;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.text.ILangEntry;

import net.minecraft.util.Util;
import net.minecraft.resources.Identifier;

@NothingNullByDefault
public enum APIMoreMachineLang implements ILangEntry {

    // Upgrades
    UPGRADE_THREAD("upgrade", "thread"),
    UPGRADE_THREAD_DESCRIPTION("upgrade", "thread.description");

    private final String key;

    APIMoreMachineLang(String type, String path) {
        this(Util.makeDescriptionId(type, Identifier.fromNamespaceAndPath(Mekmm.MOD_ID, path)));
    }

    APIMoreMachineLang(String key) {
        this.key = key;
    }

    @Override
    public String getTranslationKey() {
        return key;
    }
}
