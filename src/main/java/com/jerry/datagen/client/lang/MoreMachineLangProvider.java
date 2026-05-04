package com.jerry.datagen.client.lang;

import com.jerry.mekmm.Mekmm;
import com.jerry.mekmm.common.MoreMachineLang;
import com.jerry.mekmm.common.config.MoreMachineConfig;
import com.jerry.mekmm.common.config.MoreMachineConfigTranslations;

import net.minecraft.data.PackOutput;

public class MoreMachineLangProvider extends BaseLanguageProvider {

    private static final String[] BASE_TIERS = { "basic", "advanced", "elite", "ultimate" };
    private static final String[] EXTRA_TIERS = { "overclocked", "quantum", "dense", "multiversal", "creative" };
    private static final String[] ALL_TIERS = concat(BASE_TIERS, EXTRA_TIERS);

    private static final String[][] FACTORIES = {
            { "oxidizing", "Oxidizing" },
            { "dissolving", "Dissolving" },
            { "washing", "Washing" },
            { "crystallizing", "Crystallizing" },
            { "pressurised_reacting", "Pressurised Reacting" },
            { "centrifuging", "Centrifuging" },
            { "liquifying", "Liquifying" },
            { "pigment_extracting", "Pigment Extracting" },
            { "painting", "Painting" },
            { "recycling", "Recycling" },
            { "planting", "Planting" },
            { "stamping", "Stamping" },
            { "lathing", "Lathe" },
            { "rolling_mill", "Rolling Mill" },
            { "replicating", "Replicating" }
    };

    public MoreMachineLangProvider(PackOutput output) {
        super(output, Mekmm.MOD_ID, Mekmm.instance);
    }

    @Override
    protected void addTranslations() {
        addItemsAndBlocks();
        addLangEntries();
        addConfigs();
        addAliases();
        addModInfo("Add more machines and factories to mekanism");
    }

    private void addItemsAndBlocks() {
        addItem("scrap", "Scrap");
        addItem("scrap_box", "Scrap Box");
        addItem("empty_crystal", "Empty Crystal");
        addItem("uu_matter", "UU Matter");
        addItem("connector", "Connector");
        addItem("advanced_electrolysis_core", "Advanced Electrolysis Core");

        addBlockItem("recycler", "Recycler");
        addBlockItem("planting_station", "Planting Station");
        addBlockItem("cnc_stamper", "CNC Stamper");
        addBlockItem("cnc_lathe", "CNC Lathe");
        addBlockItem("cnc_rolling_mill", "CNC Rolling Mill");
        addBlockItem("replicator", "Replicator");
        addBlockItem("fluid_replicator", "Fluid Replicator");
        addBlockItem("chemical_replicator", "Chemical Replicator");
        addBlockItem("ambient_gas_collector", "Ambient Gas Collector");
        addBlockItem("wireless_charging_station", "Wireless Charging Station");
        addBlockItem("wireless_transmission_station", "Wireless Transmission Station");
        addBlockItem("author_doll", "LostMyself");
        addBlockItem("modeler_doll", "TedXenon");

        for (String tier : BASE_TIERS) {
            String tierName = toTitle(tier);
            addBlockItem(tier + "_mid_chemical_tank", tierName + " Mid Chemical Tank");
            addBlockItem(tier + "_max_chemical_tank", tierName + " Max Chemical Tank");
        }

        addBlockItem("large_rotary_condensentrator", "Large Rotary Condensentrator");
        addBlockItem("large_chemical_infuser", "Large Chemical Infuser");
        addBlockItem("large_electrolytic_separator", "Large Electrolytic Separator");
        addBlockItem("large_solar_neutron_activator", "Large Solar Neutron Activator");
        addBlockItem("large_antiprotonic_nucleosynthesizer", "Large Antiprotonic Nucleosynthesizer");
        addBlockItem("large_pigment_mixer", "Large Pigment Mixer");
        addBlockItem("large_heat_generator", "Large Heat Generator");
        addBlockItem("large_gas_burning_generator", "Large Gas-Burning Generator");
        addBlockItem("large_wind_generator", "Large Wind Generator");

        for (String tier : ALL_TIERS) {
            String tierName = toTitle(tier);
            for (String[] factory : FACTORIES) {
                addBlockItem(tier + "_" + factory[0] + "_factory", tierName + " " + factory[1] + " Factory");
            }
        }
    }

    private void addLangEntries() {
        add(MoreMachineLang.MEKANISM_MORE_MACHINE, "Mekanism:More Machine");
        add(MoreMachineLang.MEKANISM_LARGE_MACHINE, "Mekanism:Large Machine");
        add(MoreMachineLang.CHARGING_EQUIPS, "Equipment");
        add(MoreMachineLang.CHARGING_INVENTORY, "Inventory");
        add(MoreMachineLang.CHARGING_CURIOS, "Curios");

        add(MoreMachineLang.RECYCLING, "Recycling");
        add(MoreMachineLang.PLANTING, "Planting");
        add(MoreMachineLang.STAMPING, "Stamping");
        add(MoreMachineLang.LATHING, "Lathing");
        add(MoreMachineLang.ROLLING_MILL, "Rolling Mill");
        add(MoreMachineLang.REPLICATING, "Replicating");
        add(MoreMachineLang.OXIDIZING, "Oxidizing");
        add(MoreMachineLang.CHEMICAL_INFUSING, "Chemical Infusing");
        add(MoreMachineLang.DISSOLVING, "Dissolving");
        add(MoreMachineLang.WASHING, "Washing");
        add(MoreMachineLang.CRYSTALLIZING, "Crystallizing");
        add(MoreMachineLang.PRESSURISED_REACTING, "Pressurised Reacting");
        add(MoreMachineLang.CENTRIFUGING, "Centrifuging");
        add(MoreMachineLang.LIQUIFYING, "Liquifying");
        add(MoreMachineLang.PIGMENT_EXTRACTING, "Pigment Extracting");
        add(MoreMachineLang.PAINTING, "Painting");

        add(MoreMachineLang.DESCRIPTION_RECYCLER, "A machine for recycling items.");
        add(MoreMachineLang.DESCRIPTION_PLANTING_STATION, "A machine for automatically growing plants.");
        add(MoreMachineLang.DESCRIPTION_CNC_STAMPER, "A machine for stamping items.");
        add(MoreMachineLang.DESCRIPTION_CNC_LATHE, "A machine for lathing items.");
        add(MoreMachineLang.DESCRIPTION_CNC_ROLLING_MILL, "A machine for rolling items.");
        add(MoreMachineLang.DESCRIPTION_REPLICATOR, "A machine for replicating items with UU Matter.");
        add(MoreMachineLang.DESCRIPTION_FLUID_REPLICATOR, "A machine for replicating fluids with UU Matter.");
        add(MoreMachineLang.DESCRIPTION_CHEMicAL_REPLICATOR, "A machine for replicating chemicals with UU Matter.");
        add(MoreMachineLang.DESCRIPTION_AMBIENT_GAS_COLLECTOR, "A machine for collecting unstable dimensional gas.");
        add(MoreMachineLang.DESCRIPTION_WIRELESS_CHARGING_STATION, "A machine for wireless charging of inventory and armor.");
        add(MoreMachineLang.DESCRIPTION_WIRELESS_TRANSMISSION_STATION, "A machine capable of wirelessly transmitting five types of substances.");
        add(MoreMachineLang.AUTHOR_DOLL, "A doll with the author's skin. Just decorations.");
        add(MoreMachineLang.MODELER_DOLL, "A doll with the modeler's skin. Thanks to everyone who provided textures and models for mekmm.");

        add(MoreMachineLang.IS_BLOCKING, "Blocked above the machine");
        add(MoreMachineLang.NO_BLOCKING, "Not blocked above the machine");
        add(MoreMachineLang.CONFIGURATION, "Configuration");
        add(MoreMachineLang.WTS_ENERGY_RATE, "Energy Transmission Rate: %1$s/t");
        add(MoreMachineLang.WTS_FLUIDS_RATE, "Fluids Transmission Rate: %1$s mb/t");
        add(MoreMachineLang.WTS_CHEMICALS_RATE, "Chemicals Transmission Rate: %1$s mb/t");
        add(MoreMachineLang.WTS_ITEMS_RATE, "Items Transmission Rate: %1$s/t");
        add(MoreMachineLang.WTS_HEAT_RATE, "Heat Transmission Rate: %1$s/t");
        add(MoreMachineLang.BUTTON_DISCONNECT, "Disconnect");
        add(MoreMachineLang.BUTTON_HIGHLIGHT, "Highlight");
        add(MoreMachineLang.TRANSMITTER_CONFIG, "Wireless Transmission Station Config");
        add(MoreMachineLang.LIST_NAME, "Name: %1$s");
        add(MoreMachineLang.LIST_POS, "BlockPos: %1$s");
        add(MoreMachineLang.LIST_DIRECTION, "Direction: %1$s");
        add(MoreMachineLang.LIST_TYPE, "TransmitterType: %1$s");
        add(MoreMachineLang.VIEW_CONNECTION, "View connection information");

        add(MoreMachineLang.CONNECTOR_FROM, "Connect from %1$s");
        add(MoreMachineLang.CONNECTOR_TO, "Connect to %1$s's %2$s side");
        add(MoreMachineLang.CONNECTOR_DISCONNECT, "Disconnect from %1$s's %2$s side");
        add(MoreMachineLang.CONNECTOR_SELF, "Can't connect to self");
        add(MoreMachineLang.CONNECTOR_FAIL, "Can't connect to %1$s's %2$s side");
        add(MoreMachineLang.CONNECTOR_ACROSS_DIMENSION, "Can't connect across dimensions");
        add(MoreMachineLang.CONNECTOR_CLEARED, "Connection terminated");
        add(MoreMachineLang.CONNECTOR_LOSE, "There is no block at %1$s, or the block is not the one before connecting");
        add(MoreMachineLang.CONNECTOR_DETAIL, "Dimension: %1$s; BlockPos: %2$s");
        add(MoreMachineLang.RECIPE_VIEWER_INFO_UNSTABLE_DIMENSIONAL_GAS, "Use an Ambient Gas Collector for collection (%1$smb/t), and be careful not to place blocks above the machine.");
    }

    private void addConfigs() {
        addConfigs(MoreMachineConfig.getConfigs());
        addConfigs(MoreMachineConfigTranslations.values());
    }

    private void addItem(String path, String name) {
        add("item." + Mekmm.MOD_ID + "." + path, name);
    }

    private void addBlockItem(String path, String name) {
        add("block." + Mekmm.MOD_ID + "." + path, name);
        addItem(path, name);
    }

    private static String toTitle(String path) {
        StringBuilder builder = new StringBuilder();
        for (String part : path.split("_")) {
            if (!builder.isEmpty()) {
                builder.append(' ');
            }
            builder.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
        }
        return builder.toString();
    }

    private static String[] concat(String[] first, String[] second) {
        String[] combined = new String[first.length + second.length];
        System.arraycopy(first, 0, combined, 0, first.length);
        System.arraycopy(second, 0, combined, first.length, second.length);
        return combined;
    }
}
