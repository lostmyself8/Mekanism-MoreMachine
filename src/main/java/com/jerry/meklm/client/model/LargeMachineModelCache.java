package com.jerry.meklm.client.model;

import com.jerry.mekmm.Mekmm;

import mekanism.client.model.BaseModelCache;

public class LargeMachineModelCache extends BaseModelCache {

    public static final LargeMachineModelCache INSTANCE = new LargeMachineModelCache();

    public final JSONModelData LARGE_PIGMENT_MIXER_ROD = registerJSON("block/large_machine/large_pigment_mixer/rod");

    private LargeMachineModelCache() {
        super(Mekmm.MOD_ID);
    }
}
