package com.jerry.mekmm.common.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jerry.mekmm.Mekmm;

import net.minecraft.resources.ResourceLocation;

/**
 * Parses the {@code dimensionGasMappings} config list into a lookup table
 * for the Ambient Gas Collector.
 * <p>
 * Each config entry has the format {@code dimensionId|gasId|amount}.
 * <p>
 * Example:
 * <pre>{@code
 * "ad_astra:moon_orbit|mekanismgenerators:deuterium|10"
 * "ad_astra:mars_orbit|mekanismgenerators:tritium|8"
 * }</pre>
 */
public class AtmosphereConfig {

    private static Map<ResourceLocation, AtmosphereEntry> dimensionMap = Collections.emptyMap();
    /** Previous raw config reference — used to detect changes without polling. */
    private static List<? extends String> lastRawMappings = null;

    private AtmosphereConfig() {}

    /**
     * Looks up the configured atmosphere entry for the given dimension.
     * Automatically re-parses the config when it changes.
     *
     * @return the matching entry, or {@code null} if this dimension is not configured
     */
    public static AtmosphereEntry getEntry(ResourceLocation dimension) {
        List<? extends String> current = MoreMachineConfig.general.dimensionGasMappings.get();
        if (current != lastRawMappings) {
            reload(current);
            lastRawMappings = current;
        }
        return dimensionMap.get(dimension);
    }

    /**
     * Rebuilds the dimension → gas lookup table from the raw config list.
     */
    private static void reload(List<? extends String> rawMappings) {
        Map<ResourceLocation, AtmosphereEntry> map = new HashMap<>();
        for (String entry : rawMappings) {
            try {
                String[] parts = entry.split("\\|");
                if (parts.length != 3) {
                    Mekmm.LOGGER.warn("[AtmosphereConfig] Invalid entry (expected dim|gas|amount): {}", entry);
                    continue;
                }
                ResourceLocation dimId = new ResourceLocation(parts[0].trim());
                ResourceLocation gasId = new ResourceLocation(parts[1].trim());
                int amount = Integer.parseInt(parts[2].trim());
                if (amount <= 0) {
                    Mekmm.LOGGER.warn("[AtmosphereConfig] Invalid amount (must be >0): {}", entry);
                    continue;
                }
                map.put(dimId, new AtmosphereEntry(gasId, amount));
                Mekmm.LOGGER.info("[AtmosphereConfig] {} → {} @ {}mB/t", dimId, gasId, amount);
            } catch (Exception e) {
                Mekmm.LOGGER.error("[AtmosphereConfig] Failed to parse entry: {}", entry, e);
            }
        }
        dimensionMap = Collections.unmodifiableMap(map);
    }

    /**
     * A single dimension → gas mapping entry.
     *
     * @param gasId  the registry name of the gas (e.g. {@code mekanismgenerators:deuterium})
     * @param amount the amount (mB) collected per operation cycle
     */
    public record AtmosphereEntry(ResourceLocation gasId, int amount) {}
}
