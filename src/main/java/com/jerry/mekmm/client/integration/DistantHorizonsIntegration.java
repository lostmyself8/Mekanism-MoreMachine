package com.jerry.mekmm.client.integration;

import com.seibel.distanthorizons.api.enums.rendering.EDhApiBlockMaterial;
import com.seibel.distanthorizons.api.methods.events.DhApiEventRegister;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiBlockStateWrapperCreatedEvent;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiEventParam;

/**
 * Integrates invisible internal blocks with Distant Horizons LOD generation.
 */
public final class DistantHorizonsIntegration {

    private static final String LARGE_WIND_GENERATOR_PROXY = "mekmm:large_wind_generator_proxy";

    private DistantHorizonsIntegration() {}

    /**
     * Registers the LOD override before Distant Horizons creates block-state wrappers.
     */
    public static void register() {
        DhApiEventRegister.on(DhApiBlockStateWrapperCreatedEvent.class, new DhApiBlockStateWrapperCreatedEvent() {

            @Override
            public void blockStateWrapperCreated(DhApiEventParam<EventParam> event) {
                if (event.value.getBlockStateWrapper().getSerialString().startsWith(LARGE_WIND_GENERATOR_PROXY)) {
                    event.value.setBlockMaterial(EDhApiBlockMaterial.AIR);
                    event.value.setOpacity(0);
                }
            }
        });
    }
}
