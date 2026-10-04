package com.jerry.mekmm.client.render.outline;

import java.util.Map;

/** Per-model rendering contracts; offsets mirror the corresponding baked-model wrappers. */
public final class ModelOutlineProfiles {

    public record Profile(double yOffsetBlocks, double insetJoinPixels, boolean alignShiftedFaces) {

        public Profile(double yOffsetBlocks, double insetJoinPixels) {
            this(yOffsetBlocks, insetJoinPixels, false);
        }
    }

    // Keep the scope explicit. The separator's wrapper has NO +1 Y translation.
    public static final Map<String, Profile> MACHINES = Map.of(
            "large_heat_generator", new Profile(1, 0.0011, true),
            "large_gas_burning_generator", new Profile(1, 0),
            "large_solar_neutron_activator", new Profile(1, 0),
            "large_antiprotonic_nucleosynthesizer", new Profile(1, 0.011),
            "large_chemical_infuser", new Profile(1, 0),
            "large_electrolytic_separator", new Profile(0, 0),
            "large_rotary_condensentrator", new Profile(1, 0),
            "large_pigment_mixer", new Profile(1, 0));

    private ModelOutlineProfiles() {}
}
