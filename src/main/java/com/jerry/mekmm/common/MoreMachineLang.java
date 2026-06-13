package com.jerry.mekmm.common;

import com.jerry.mekmm.Mekmm;

import mekanism.api.text.ILangEntry;

import net.minecraft.util.Util;

import org.jspecify.annotations.NullMarked;

@NullMarked
public enum MoreMachineLang implements ILangEntry {

    // Gui lang strings
    MEKANISM_MORE_MACHINE("constants", "more_machine"),
    MEKANISM_LARGE_MACHINE("constants", "large_machine"),
    // Charging
    CHARGING_EQUIPS("charging", "equips"),
    CHARGING_INVENTORY("charging", "inventory"),
    CHARGING_CURIOS("charging", "curios"),
    // Factory Type
    RECYCLING("factory", "recycling"),
    PLANTING("factory", "planting"),
    STAMPING("factory", "stamping"),
    LATHING("factory", "lathing"),
    ROLLING_MILL("factory", "rolling_mill"),
    REPLICATING("factory", "replicating"),
    // Advanced Factory Type
    OXIDIZING("factory", "oxidizing"),
    CHEMICAL_INFUSING("factory", "chemical_infusing"),
    DISSOLVING("factory", "dissolving"),
    WASHING("factory", "washing"),
    CRYSTALLIZING("factory", "crystallizing"),
    PRESSURISED_REACTING("factory", "pressurised_reacting"),
    CENTRIFUGING("factory", "centrifuging"),
    LIQUIFYING("factory", "liquifying"),
    PIGMENT_EXTRACTING("factory", "pigment_extracting"),
    PAINTING("factory", "painting"),
    // Connector
    CONNECTOR_FROM("connector", "from"),
    CONNECTOR_TO("connector", "to"),
    CONNECTOR_DISCONNECT("connector", "disconnect"),
    CONNECTOR_SELF("connector", "self"),
    CONNECTOR_FAIL("connector", "fail"),
    CONNECTOR_ACROSS_DIMENSION("connector", "across_dimension"),
    CONNECTOR_CLEARED("connector", "cleared"),
    CONNECTOR_LOSE("connector", "lose"),
    CONNECTOR_DETAIL("connector", "detail"),
    // Descriptions
    DESCRIPTION_RECYCLER("description", "recycler"),
    DESCRIPTION_PLANTING_STATION("description", "planting_station"),
    DESCRIPTION_CNC_STAMPER("description", "cnc_stamper"),
    DESCRIPTION_CNC_LATHE("description", "cnc_lathe"),
    DESCRIPTION_CNC_ROLLING_MILL("description", "cnc_rolling_mill"),
    DESCRIPTION_REPLICATOR("description", "replicator"),
    DESCRIPTION_FLUID_REPLICATOR("description", "fluid_replicator"),
    DESCRIPTION_CHEMicAL_REPLICATOR("description", "chemical_replicator"),
    DESCRIPTION_AMBIENT_GAS_COLLECTOR("description", "ambient_gas_collector"),
    DESCRIPTION_WIRELESS_CHARGING_STATION("description", "wireless_charging_station"),
    DESCRIPTION_WIRELESS_TRANSMISSION_STATION("description", "wireless_transmission_station"),
    AUTHOR_DOLL("description", "author_doll"),
    MODELER_DOLL("description", "modeler_doll"),
    // JEI
    RECIPE_VIEWER_INFO_UNSTABLE_DIMENSIONAL_GAS("info", "jei.unstable_dimensional_gas"),
    // Tooltip stuff
    IS_BLOCKING("tooltip", "is_blocking"),
    NO_BLOCKING("tooltip", "no_blocking"),
    // Gui stuff
    CONFIGURATION("gui", "configuration"),
    WTS_ENERGY_RATE("gui", "wts.energy_rate"),
    WTS_FLUIDS_RATE("gui", "wts.fluids_rate"),
    WTS_CHEMICALS_RATE("gui", "wts.chemicals_rate"),
    WTS_ITEMS_RATE("gui", "wts.items_rate"),
    WTS_HEAT_RATE("gui", "wts.heat_rate"),
    // Button
    BUTTON_DISCONNECT("button", "disconnect"),
    BUTTON_HIGHLIGHT("button", "highlight"),
    // Transmitter
    TRANSMITTER_CONFIG("transmitter", "config"),
    // List Display
    LIST_NAME("list", "name"),
    LIST_POS("list", "pos"),
    LIST_DIRECTION("list", "direction"),
    LIST_TYPE("list", "type"),
    // View Connection
    VIEW_CONNECTION("view", "connection_information");

    private final String key;

    MoreMachineLang(String type, String path) {
        this(Util.makeDescriptionId(type, Mekmm.rl(path)));
    }

    MoreMachineLang(String key) {
        this.key = key;
    }

    @Override
    public String getTranslationKey() {
        return key;
    }
}
