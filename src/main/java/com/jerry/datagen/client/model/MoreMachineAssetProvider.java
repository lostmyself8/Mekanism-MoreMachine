package com.jerry.datagen.client.model;

import com.jerry.mekmm.Mekmm;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MoreMachineAssetProvider implements DataProvider {

    private static final String[] BASE_TIERS = { "basic", "advanced", "elite", "ultimate" };
    private static final String[] EXTRA_TIERS = { "overclocked", "quantum", "dense", "multiversal", "creative" };
    private static final String[] ALL_TIERS = concat(BASE_TIERS, EXTRA_TIERS);

    private static final String[] MORE_MACHINE_FACTORIES = {
            "recycling", "planting", "stamping", "lathing", "rolling_mill", "replicating"
    };
    private static final String[] ADVANCED_FACTORIES = {
            "oxidizing", "dissolving", "washing", "crystallizing", "pressurised_reacting", "centrifuging", "liquifying", "pigment_extracting", "painting"
    };
    private static final String[] SIMPLE_ITEMS = {
            "scrap", "scrap_box", "empty_crystal", "uu_matter", "advanced_electrolysis_core", "reflector"
    };
    private static final String[] CONNECTOR_MODELS = {
            "connector", "connector_energy", "connector_fluids", "connector_chemicals", "connector_items", "connector_heat"
    };
    private static final String[] BASE_ACTIVE_MACHINES = {
            "recycler", "planting_station", "cnc_stamper", "cnc_lathe", "cnc_rolling_mill"
    };
    private static final String[] FACING_MACHINES = {
            "replicator", "fluid_replicator", "chemical_replicator", "ambient_gas_collector", "wireless_charging_station",
            "wireless_transmission_station", "author_doll", "modeler_doll"
    };
    private static final String[] LARGE_MACHINES = {
            "large_rotary_condensentrator", "large_chemical_infuser", "large_electrolytic_separator", "large_solar_neutron_activator",
            "large_antiprotonic_nucleosynthesizer", "large_pigment_mixer", "large_heat_generator", "large_gas_burning_generator",
            "large_wind_generator", "solar_heat_generator"
    };

    private final PackOutput.PathProvider blockStatePathProvider;
    private final PackOutput.PathProvider itemDefinitionPathProvider;
    private final PackOutput.PathProvider itemModelPathProvider;
    private final PackOutput.PathProvider blockModelPathProvider;

    public MoreMachineAssetProvider(PackOutput output) {
        this.blockStatePathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "blockstates");
        this.itemDefinitionPathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "items");
        this.itemModelPathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models/item");
        this.blockModelPathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models/block");
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput output) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        generateItemDefinitions(output, futures);
        generateSimpleItemModels(output, futures);
        generateBlockItemModels(output, futures);
        generateBlockStates(output, futures);
        generateTieredBlockModels(output, futures);
        generateActiveBlockModels(output, futures);
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    private void generateItemDefinitions(CachedOutput output, List<CompletableFuture<?>> futures) {
        for (String item : allItemNames()) {
            JsonObject definition = switch (item) {
                case "large_wind_generator", "solar_heat_generator" -> specialItemDefinition("mekmm:item/" + item, "mekmm:" + item);
                default -> itemDefinition("mekmm:item/" + item);
            };
            save(output, futures, itemDefinitionPathProvider.json(id(item)), definition);
        }
    }

    private void generateActiveBlockModels(CachedOutput output, List<CompletableFuture<?>> futures) {
        save(output, futures, blockModelPathProvider.json(id("recycler_active")),
                blockModelWithTextures("mekmm:block/recycler", texture("front", "mekmm:block/recycler/front_active")));
        save(output, futures, blockModelPathProvider.json(id("cnc_lathe_active")),
                blockModelWithTextures("mekmm:block/cnc_lathe", texture("front", "mekmm:block/cnc_lathe/front_active")));
        save(output, futures, blockModelPathProvider.json(id("cnc_rolling_mill_active")),
                blockModelWithTextures("mekmm:block/cnc_rolling_mill",
                        texture("front", "mekmm:block/cnc_rolling_mill/front_active"),
                        texture("side", "mekmm:block/cnc_rolling_mill/side_active")));
        save(output, futures, blockModelPathProvider.json(id("cnc_stamper_active")),
                blockModelWithTextures("mekmm:block/cnc_stamper",
                        texture("3", "mekmm:block/cnc_stamper/front_active"),
                        texture("4", "mekmm:block/cnc_stamper/side_active")));
    }

    private void generateBlockItemModels(CachedOutput output, List<CompletableFuture<?>> futures) {
        for (String tier : ALL_TIERS) {
            for (String type : MORE_MACHINE_FACTORIES) {
                String name = tier + "_" + type + "_factory";
                save(output, futures, itemModelPathProvider.json(id(name)),
                        factoryBlockItemModel("mekmm:block/factory/" + type + "/" + tier, type));
            }
            for (String type : ADVANCED_FACTORIES) {
                String name = tier + "_" + type + "_factory";
                save(output, futures, itemModelPathProvider.json(id(name)),
                        factoryBlockItemModel("mekmm:block/factory/" + type + "/" + tier, type));
            }
        }
        for (String tier : BASE_TIERS) {
            save(output, futures, itemModelPathProvider.json(id(tier + "_mid_chemical_tank")),
                    blockItemModel("mekmm:block/chemical_tank/mid_chemical_tank/" + tier));
            save(output, futures, itemModelPathProvider.json(id(tier + "_max_chemical_tank")),
                    blockItemModel("mekmm:block/chemical_tank/max_chemical_tank/" + tier));
        }
        for (String machine : BASE_ACTIVE_MACHINES) {
            save(output, futures, itemModelPathProvider.json(id(machine)), blockItemModel("mekmm:block/" + machine));
        }
        for (String machine : FACING_MACHINES) {
            save(output, futures, itemModelPathProvider.json(id(machine)), blockItemModel("mekmm:block/" + machine));
        }
        for (String machine : LARGE_MACHINES) {
            save(output, futures, itemModelPathProvider.json(id(machine)), largeMachineItemModel(machine));
        }
    }

    private void generateSimpleItemModels(CachedOutput output, List<CompletableFuture<?>> futures) {
        for (String item : SIMPLE_ITEMS) {
            save(output, futures, itemModelPathProvider.json(id(item)), flatItemModel("minecraft:item/generated", "mekmm:item/" + item));
        }
        for (String item : CONNECTOR_MODELS) {
            String texture = item.equals("connector") ? "mekmm:item/connector_energy" : "mekmm:item/" + item;
            save(output, futures, itemModelPathProvider.json(id(item)), flatItemModel("item/handheld", texture, item.equals("connector")));
        }
    }

    private void generateBlockStates(CachedOutput output, List<CompletableFuture<?>> futures) {
        for (String tier : ALL_TIERS) {
            for (String type : MORE_MACHINE_FACTORIES) {
                save(output, futures, blockStatePathProvider.json(id(tier + "_" + type + "_factory")),
                        activeFacingBlockState("mekmm:block/factory/" + type + "/" + tier, "mekmm:block/factory/" + type + "/active/" + tier));
            }
            for (String type : ADVANCED_FACTORIES) {
                save(output, futures, blockStatePathProvider.json(id(tier + "_" + type + "_factory")),
                        activeFacingBlockState("mekmm:block/factory/" + type + "/" + tier, "mekmm:block/factory/" + type + "/active/" + tier));
            }
        }
        for (String tier : BASE_TIERS) {
            save(output, futures, blockStatePathProvider.json(id(tier + "_mid_chemical_tank")),
                    facingBlockState("mekmm:block/chemical_tank/mid_chemical_tank/" + tier));
            save(output, futures, blockStatePathProvider.json(id(tier + "_max_chemical_tank")),
                    facingBlockState("mekmm:block/chemical_tank/max_chemical_tank/" + tier));
        }
        for (String machine : BASE_ACTIVE_MACHINES) {
            save(output, futures, blockStatePathProvider.json(id(machine)),
                    activeFacingBlockState("mekmm:block/" + machine, "mekmm:block/" + machine + "_active"));
        }
        for (String machine : FACING_MACHINES) {
            save(output, futures, blockStatePathProvider.json(id(machine)), facingBlockState("mekmm:block/" + machine));
        }
        for (String machine : LARGE_MACHINES) {
            save(output, futures, blockStatePathProvider.json(id(machine)),
                    activeFacingBlockState("mekmm:block/large_machine/" + machine + "/off", "mekmm:block/large_machine/" + machine + "/on"));
        }
    }

    private void generateTieredBlockModels(CachedOutput output, List<CompletableFuture<?>> futures) {
        for (String tier : ALL_TIERS) {
            for (String type : MORE_MACHINE_FACTORIES) {
                save(output, futures, blockModelPathProvider.json(id("factory/" + type + "/" + tier)), factoryModel(type, tier, false));
                save(output, futures, blockModelPathProvider.json(id("factory/" + type + "/active/" + tier)), factoryModel(type, tier, true));
            }
            for (String type : ADVANCED_FACTORIES) {
                save(output, futures, blockModelPathProvider.json(id("factory/" + type + "/" + tier)), factoryModel(type, tier, false));
                save(output, futures, blockModelPathProvider.json(id("factory/" + type + "/active/" + tier)), factoryModel(type, tier, true));
            }
        }
        for (String tier : BASE_TIERS) {
            save(output, futures, blockModelPathProvider.json(id("chemical_tank/mid_chemical_tank/" + tier)), chemicalTankModel("mid_chemical_tank", tier));
            save(output, futures, blockModelPathProvider.json(id("chemical_tank/max_chemical_tank/" + tier)), chemicalTankModel("max_chemical_tank", tier));
        }
    }

    private static JsonObject itemDefinition(String model) {
        JsonObject root = new JsonObject();
        JsonObject modelJson = new JsonObject();
        modelJson.addProperty("type", "minecraft:model");
        modelJson.addProperty("model", model);
        root.add("model", modelJson);
        return root;
    }

    private static JsonObject specialItemDefinition(String base, String rendererType) {
        JsonObject root = new JsonObject();
        JsonObject modelJson = new JsonObject();
        modelJson.addProperty("type", "minecraft:special");
        modelJson.addProperty("base", base);
        JsonObject specialModel = new JsonObject();
        specialModel.addProperty("type", rendererType);
        modelJson.add("model", specialModel);
        root.add("model", modelJson);
        return root;
    }

    private static JsonObject flatItemModel(String parent, String layer0) {
        return flatItemModel(parent, layer0, false);
    }

    private static JsonObject flatItemModel(String parent, String layer0, boolean connectorOverrides) {
        JsonObject root = new JsonObject();
        root.addProperty("parent", parent);
        JsonObject textures = new JsonObject();
        textures.addProperty("layer0", layer0);
        root.add("textures", textures);
        if (connectorOverrides) {
            JsonArray overrides = new JsonArray();
            addConnectorOverride(overrides, 1, "connector_items");
            addConnectorOverride(overrides, 2, "connector_fluids");
            addConnectorOverride(overrides, 3, "connector_chemicals");
            addConnectorOverride(overrides, 4, "connector_energy");
            addConnectorOverride(overrides, 5, "connector_heat");
            root.add("overrides", overrides);
        }
        return root;
    }

    private static JsonObject blockItemModel(String parent) {
        JsonObject root = new JsonObject();
        root.addProperty("parent", parent);
        return root;
    }

    private static JsonObject factoryBlockItemModel(String parent, String type) {
        JsonObject root = blockItemModel(parent);
        if (type.equals("planting") || type.equals("centrifuging")) {
            root.add("display", factoryItemDisplay());
        }
        return root;
    }

    private static JsonObject largeMachineItemModel(String machine) {
        JsonObject root = machine.equals("large_wind_generator") || machine.equals("solar_heat_generator") ? new JsonObject() : blockItemModel("mekmm:block/large_machine/" + machine + "/off");
        root.add("display", switch (machine) {
            case "large_chemical_infuser", "large_electrolytic_separator" -> tallLargeMachineDisplay();
            case "large_wind_generator" -> largeWindGeneratorDisplay();
            case "solar_heat_generator" -> solarHeatGeneratorDisplay();
            case "large_antiprotonic_nucleosynthesizer", "large_pigment_mixer" -> standardLargeMachineDisplay(array(0, -4, 0));
            default -> standardLargeMachineDisplay(null);
        });
        return root;
    }

    private static JsonObject solarHeatGeneratorDisplay() {
        JsonObject display = new JsonObject();
        addTransform(display, "thirdperson_righthand", array(75, 45, 0), array(0, 1, 0), array(0.09, 0.09, 0.09));
        addTransform(display, "thirdperson_lefthand", array(75, 135, 0), array(0, 1, 0), array(0.09, 0.09, 0.09));
        addTransform(display, "firstperson_righthand", array(0, 135, 0), array(0, -1, 0.75), array(0.12, 0.12, 0.12));
        addTransform(display, "firstperson_lefthand", array(0, 45, 0), array(0, -1, 0.75), array(0.12, 0.12, 0.12));
        addTransform(display, "ground", null, array(0, 2, 0), array(0.1, 0.1, 0.1));
        addTransform(display, "gui", array(30, -135, 0), array(0, -4, 0), array(0.11, 0.11, 0.11));
        addTransform(display, "fixed", null, array(0, -5, 0), array(0.14, 0.14, 0.14));
        return display;
    }

    private static JsonObject factoryItemDisplay() {
        JsonObject display = new JsonObject();
        addTransform(display, "thirdperson_righthand", array(75, 45, 0), array(0, 2.5, 1.25), array(0.375, 0.375, 0.375));
        addTransform(display, "thirdperson_lefthand", array(75, 45, 0), array(0, 2.5, 1.25), array(0.375, 0.375, 0.375));
        addTransform(display, "firstperson_righthand", array(0, 45, 0), null, array(0.4, 0.4, 0.4));
        addTransform(display, "firstperson_lefthand", array(0, 225, 0), null, array(0.4, 0.4, 0.4));
        addTransform(display, "ground", null, array(0, 3, 0), array(0.25, 0.25, 0.25));
        addTransform(display, "gui", array(30, 225, 0), array(0, -2.5, 0), array(0.43, 0.43, 0.43));
        addTransform(display, "head", null, array(0, 14, 0), null);
        addTransform(display, "fixed", null, array(0, -3.75, 0), array(0.5, 0.5, 0.5));
        return display;
    }

    private static JsonObject standardLargeMachineDisplay(JsonArray fixedTranslation) {
        JsonObject display = new JsonObject();
        addTransform(display, "thirdperson_righthand", array(75, 45, 0), array(0, 4, 0), array(0.15, 0.15, 0.15));
        addTransform(display, "thirdperson_lefthand", array(75, 135, 0), array(0, 4, 0), array(0.15, 0.15, 0.15));
        addTransform(display, "firstperson_righthand", array(0, 135, 0), array(0, 0.5, 0.75), array(0.2, 0.2, 0.2));
        addTransform(display, "firstperson_lefthand", array(0, 45, 0), array(0, 0.5, 0.75), array(0.15, 0.15, 0.15));
        addTransform(display, "ground", null, array(0, 4, 0), array(0.2, 0.2, 0.2));
        addTransform(display, "gui", array(30, -135, 0), array(0, -2.7, 0), array(0.2, 0.2, 0.2));
        addTransform(display, "fixed", null, fixedTranslation, array(0.25, 0.25, 0.25));
        return display;
    }

    private static JsonObject tallLargeMachineDisplay() {
        JsonObject display = new JsonObject();
        addTransform(display, "thirdperson_righthand", array(75, 45, 0), array(0, 5.5, -0.5), array(0.2, 0.2, 0.2));
        addTransform(display, "thirdperson_lefthand", array(75, 45, 0), array(0, 5.5, -0.5), array(0.2, 0.2, 0.2));
        addTransform(display, "firstperson_righthand", array(0, 135, 0), array(0, 0.5, 0.75), array(0.2, 0.2, 0.2));
        addTransform(display, "firstperson_lefthand", array(0, 135, 0), array(0, 0.5, 0.75), array(0.2, 0.2, 0.2));
        addTransform(display, "ground", null, array(0, 3, 0), array(0.25, 0.25, 0.25));
        addTransform(display, "gui", array(30, 225, 0), array(0.25, -2, 0), array(0.25, 0.25, 0.25));
        addTransform(display, "head", null, array(0, 9.25, -1), array(0.3, 0.3, 0.3));
        addTransform(display, "fixed", null, array(0, -1.75, 0.75), array(0.25, 0.25, 0.25));
        return display;
    }

    private static JsonObject largeWindGeneratorDisplay() {
        JsonObject display = new JsonObject();
        addTransform(display, "gui", array(30, 225, 0), array(0, -6.5, 0), array(0.0216, 0.0216, 0.0216));
        addTransform(display, "ground", array(0, 0, 0), array(0, 0.5, 0), array(0.0196, 0.0196, 0.0196));
        addTransform(display, "thirdperson_righthand", array(25, 45, 0), array(0, -1, -1), array(0.0296, 0.0296, 0.0296));
        addTransform(display, "firstperson_righthand", array(0, 45, 0), array(0, -3.5, 0), array(0.0158, 0.0158, 0.0158));
        addTransform(display, "thirdperson_lefthand", array(25, 45, 0), array(0, -1, -1), array(0.0296, 0.0296, 0.0296));
        addTransform(display, "firstperson_lefthand", array(0, 45, 0), array(0, -3.5, 0), array(0.0158, 0.0158, 0.0158));
        addTransform(display, "head", array(0, 0, 0), array(0, 0, 0), array(0.0394, 0.0394, 0.0394));
        addTransform(display, "fixed", array(0, 0, 0), array(0, -9, 0), array(0.0344, 0.0344, 0.0344));
        return display;
    }

    private static void addTransform(JsonObject display, String perspective, JsonArray rotation, JsonArray translation, JsonArray scale) {
        JsonObject transform = new JsonObject();
        if (rotation != null) {
            transform.add("rotation", rotation);
        }
        if (translation != null) {
            transform.add("translation", translation);
        }
        if (scale != null) {
            transform.add("scale", scale);
        }
        display.add(perspective, transform);
    }

    private static void addConnectorOverride(JsonArray overrides, int mode, String model) {
        JsonObject override = new JsonObject();
        JsonObject predicate = new JsonObject();
        predicate.addProperty("mekmm:mode", mode);
        override.add("predicate", predicate);
        override.addProperty("model", "mekmm:item/" + model);
        overrides.add(override);
    }

    private static JsonObject facingBlockState(String model) {
        JsonObject root = new JsonObject();
        JsonObject variants = new JsonObject();
        addFacingVariants(variants, "", model);
        root.add("variants", variants);
        return root;
    }

    private static JsonObject activeFacingBlockState(String offModel, String onModel) {
        JsonObject root = new JsonObject();
        JsonObject variants = new JsonObject();
        addFacingVariants(variants, "active=false", offModel);
        addFacingVariants(variants, "active=true", onModel);
        root.add("variants", variants);
        return root;
    }

    private static void addFacingVariants(JsonObject variants, String suffix, String model) {
        addVariant(variants, key("north", suffix), model, null);
        addVariant(variants, key("south", suffix), model, 180);
        addVariant(variants, key("east", suffix), model, 90);
        addVariant(variants, key("west", suffix), model, -90);
    }

    private static String key(String facing, String suffix) {
        return suffix.isEmpty() ? "facing=" + facing : "facing=" + facing + "," + suffix;
    }

    private static void addVariant(JsonObject variants, String key, String model, Integer rotation) {
        JsonObject variant = new JsonObject();
        variant.addProperty("model", model);
        if (rotation != null) {
            variant.addProperty("y", rotation);
        }
        variants.add(key, variant);
    }

    private static JsonObject factoryModel(String type, String tier, boolean active) {
        JsonObject root = new JsonObject();
        root.addProperty("loader", "neoforge:composite");
        root.addProperty("parent", "block/block");
        if (type.equals("planting") && active) {
            root.addProperty("gui_light", "side");
        }
        JsonObject textures = new JsonObject();
        textures.addProperty("particle", "mekanism:block/factory/factory_front_back");
        root.add("textures", textures);
        JsonObject children = new JsonObject();
        JsonObject base = new JsonObject();
        base.addProperty("parent", "mekmm:block/factory/" + type + "/" + (type.equals("planting") && active ? "base_active" : "base"));
        children.add("base", base);
        if (type.equals("planting") && active) {
            children.add("led", plantingFactoryActiveLed());
        } else {
            JsonObject frontLed = new JsonObject();
            frontLed.addProperty("parent", "mekanism:block/factory/front_led/" + (active ? "active/" : "") + tier);
            children.add("front_led", frontLed);
        }
        root.add("children", children);
        return root;
    }

    private static JsonObject plantingFactoryActiveLed() {
        JsonObject led = new JsonObject();
        led.addProperty("render_type", "cutout");
        JsonObject textures = new JsonObject();
        textures.addProperty("led", "mekanism:block/factory/led");
        led.add("textures", textures);
        JsonArray elements = new JsonArray();
        JsonObject element = new JsonObject();
        element.addProperty("name", "front_panel_led");
        element.add("from", array(4.98, 29.99, 0.01));
        element.add("to", array(11.02, 30.99, 1.01));
        JsonObject faces = new JsonObject();
        JsonObject north = face(0, 0, 6, 1, "#led", "north");
        addFullLight(north);
        JsonObject up = face(0, 0, 6, 1, "#led", "up");
        up.addProperty("rotation", 180);
        addFullLight(up);
        faces.add("north", north);
        faces.add("up", up);
        element.add("faces", faces);
        elements.add(element);
        led.add("elements", elements);
        return led;
    }

    private static JsonObject chemicalTankModel(String tank, String tier) {
        JsonObject root = new JsonObject();
        root.addProperty("parent", "mekmm:block/chemical_tank/" + tank + "/base");
        root.add("texture_size", array(64, 64));
        JsonObject textures = new JsonObject();
        textures.addProperty("1", "mekmm:block/chemical_tank/" + tier + "_valve");
        root.add("textures", textures);
        return root;
    }

    private static JsonObject blockModelWithTextures(String parent, String[]... textureEntries) {
        JsonObject root = blockItemModel(parent);
        JsonObject textures = new JsonObject();
        for (String[] textureEntry : textureEntries) {
            textures.addProperty(textureEntry[0], textureEntry[1]);
        }
        root.add("textures", textures);
        return root;
    }

    private static String[] texture(String key, String value) {
        return new String[] { key, value };
    }

    private static JsonObject face(int u1, int v1, int u2, int v2, String texture, String cullface) {
        JsonObject face = new JsonObject();
        face.add("uv", array(u1, v1, u2, v2));
        face.addProperty("texture", texture);
        face.addProperty("cullface", cullface);
        return face;
    }

    private static void addFullLight(JsonObject face) {
        JsonObject light = new JsonObject();
        light.addProperty("block_light", 15);
        light.addProperty("sky_light", 15);
        face.add("neoforge_data", light);
    }

    private static JsonArray array(Number... values) {
        JsonArray array = new JsonArray();
        for (Number value : values) {
            array.add(value);
        }
        return array;
    }

    private void save(CachedOutput output, List<CompletableFuture<?>> futures, Path path, JsonObject json) {
        futures.add(DataProvider.saveStable(output, json, path));
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(Mekmm.MOD_ID, path);
    }

    private static List<String> allItemNames() {
        List<String> names = new ArrayList<>();
        names.addAll(List.of(SIMPLE_ITEMS));
        names.add("connector");
        names.addAll(List.of(FACING_MACHINES));
        names.addAll(List.of(BASE_ACTIVE_MACHINES));
        names.addAll(List.of(LARGE_MACHINES));
        for (String tier : ALL_TIERS) {
            for (String type : MORE_MACHINE_FACTORIES) {
                names.add(tier + "_" + type + "_factory");
            }
            for (String type : ADVANCED_FACTORIES) {
                names.add(tier + "_" + type + "_factory");
            }
        }
        for (String tier : BASE_TIERS) {
            names.add(tier + "_mid_chemical_tank");
            names.add(tier + "_max_chemical_tank");
        }
        return names;
    }

    private static String[] concat(String[] first, String[] second) {
        String[] combined = new String[first.length + second.length];
        System.arraycopy(first, 0, combined, 0, first.length);
        System.arraycopy(second, 0, combined, first.length, second.length);
        return combined;
    }

    @Override
    public @NotNull String getName() {
        return "Mekanism: MoreMachine assets";
    }
}
