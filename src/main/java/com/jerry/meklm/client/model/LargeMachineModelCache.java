package com.jerry.meklm.client.model;

import com.jerry.mekmm.Mekmm;

import mekanism.client.model.BaseModelCache;

public class LargeMachineModelCache extends BaseModelCache {

    public static final LargeMachineModelCache INSTANCE = new LargeMachineModelCache();

    public final OBJModelData SOLAR_HEAT_GENERATOR = register(Mekmm.rl("models/block/large_machine/solar_heat_generator/solar_heat_generator.obj"), rl -> new OBJModelData(rl) {

        @Override
        protected boolean useDiffuseLighting() {
            return false;
        }
    });

    public final JSONModelData LARGE_PIGMENT_MIXER_ROD = registerJSON("block/large_machine/large_pigment_mixer/rod");

    private LargeMachineModelCache() {
        super(Mekmm.MOD_ID);
    }
}
