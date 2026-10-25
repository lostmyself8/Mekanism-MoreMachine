package com.jerry.mekmm.smoke;

import com.jerry.mekmm.Mekmm;
import com.jerry.mekmm.common.registries.*;

import mekanism.api.MekanismRegistries;
import mekanism.api.chemical.ChemicalResource;
import mekanism.api.upgrade.UpgradeIds;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.util.UpgradeUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;

import com.mojang.serialization.MapCodec;

import java.util.function.Consumer;

@EventBusSubscriber(modid = "mekmm_smoketests")
public final class PortSmokeTests {

    @SubscribeEvent
    public static void register(RegisterGameTestsEvent event) {
        if (!Boolean.getBoolean("mekmm.serverSmoke")) return;
        var env = event.registerEnvironment(Mekmm.rl("port_smoke"));
        register(event, env, "registry_and_recipes", PortSmokeTests::registryAndRecipes);
        register(event, env, "machine_recipe_displays", PortSmokeTests::machineRecipeDisplays);
        register(event, env, "all_machine_entities", PortSmokeTests::allMachineEntities);
        register(event, env, "processing_and_upgrades", PortSmokeTests::processingAndUpgrades);
        register(event, env, "station_keeps_sapling", h -> plantingPreservesInput(h, false, Items.OAK_SAPLING, Items.OAK_LOG, 6));
        register(event, env, "station_keeps_seed", h -> plantingPreservesInput(h, false, Items.WHEAT_SEEDS, Items.WHEAT, 3));
        register(event, env, "factory_keeps_sapling", h -> plantingPreservesInput(h, true, Items.OAK_SAPLING, Items.OAK_LOG, 6));
        register(event, env, "factory_keeps_seed", h -> plantingPreservesInput(h, true, Items.WHEAT_SEEDS, Items.WHEAT, 3));
        register(event, env, "station_non_per_tick_keeps_input", h -> plantingPreservesInput(h, false, Items.STICK, Items.OAK_LOG, 6));
        register(event, env, "factory_non_per_tick_keeps_input", h -> plantingPreservesInput(h, true, Items.STICK, Items.OAK_LOG, 6));
        register(event, env, "croparium_optional_recipes", PortSmokeTests::cropariumRecipes);
        register(event, env, "large_wind_crafting", PortSmokeTests::largeWindCrafting);
        register(event, env, "hbm_optional_recipes", PortSmokeTests::hbmRecipes);
        register(event, env, "large_gas_hydrogen", h -> MachinePortTests.gasProduction(h, mekanism.api.chemical.ChemicalIds.HYDROGEN));
        register(event, env, "large_gas_ethene", h -> MachinePortTests.gasProduction(h, mekanism.api.chemical.ChemicalIds.ETHENE));
        register(event, env, "large_heat_output", MachinePortTests::heatOutput);
        for (var facing : java.util.List.of(net.minecraft.core.Direction.NORTH, net.minecraft.core.Direction.SOUTH,
                net.minecraft.core.Direction.WEST, net.minecraft.core.Direction.EAST)) {
            register(event, env, "large_rotary_ports_" + facing.getName(), h -> MachinePortTests.rotaryPorts(h, facing));
            register(event, env, "large_machine_ports_" + facing.getName(), h -> MachinePortTests.allMachinePorts(h, facing));
        }
        if (net.neoforged.fml.ModList.get().isLoaded("hbm")) {
            String[] willow = { "plant_flower_cd0", "plant_flower_cd1", "plant_tall_cd2", "plant_tall_cd3", "plant_tall_cd4" };
            for (int i = 0; i < willow.length; i++) {
                var plant = foreignItem("hbm", willow[i]);
                var leaves = foreignItem("hbm", "plant_item_mustardwillow");
                int yield = i + 1;
                register(event, env, "hbm_station_cd" + i, h -> plantingPreservesInput(h, false, plant, leaves, yield));
                register(event, env, "hbm_factory_cd" + i, h -> plantingPreservesInput(h, true, plant, leaves, yield));
            }
            var hemp = foreignItem("hbm", "plant_flower_weed");
            register(event, env, "hbm_station_hemp", h -> plantingPreservesInput(h, false, hemp, hemp, 1));
            register(event, env, "hbm_factory_hemp", h -> plantingPreservesInput(h, true, hemp, hemp, 1));
        }
        if (net.neoforged.fml.ModList.get().isLoaded("cp_lib")) {
            register(event, env, "croparium_station_diamond", h -> plantingPreservesInput(h, false, cropItem("diamond_seeds"), Items.DIAMOND, 1));
            register(event, env, "croparium_factory_diamond", h -> plantingPreservesInput(h, true, cropItem("diamond_seeds"), Items.DIAMOND, 1));
            register(event, env, "croparium_station_honey", h -> plantingPreservesInput(h, false, cropItem("honey_seeds"), cropItem("honey_cream"), 1));
            register(event, env, "croparium_factory_sea_heart", h -> plantingPreservesInput(h, true, cropItem("sea_heart_seeds"), Items.HEART_OF_THE_SEA, 1));
        }
    }

    private static net.minecraft.world.item.Item cropItem(String path) {
        return foreignItem("cp_lib", path);
    }

    private static net.minecraft.world.item.Item foreignItem(String modid, String path) {
        return BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(modid, path));
    }

    private static void largeWindCrafting(GameTestHelper h) {
        var holder = h.getLevel().getServer().getRecipeManager().byKey(ResourceKey.create(Registries.RECIPE, Mekmm.rl("large_wind_generator")));
        if (!net.neoforged.fml.ModList.get().isLoaded("mekanismgenerators")) {
            h.assertTrue(holder.isEmpty(), "Wind recipe loaded without Mekanism Generators");
            h.succeed();
            return;
        }
        var recipe = (net.minecraft.world.item.crafting.ShapedRecipe) holder.orElseThrow().value();
        var steel = mekanism.common.registries.MekanismBlocks.STEEL_BLOCK.asItem();
        var slots = new java.util.ArrayList<ItemStack>();
        for (int i = 0; i < 9; i++) slots.add(new ItemStack(steel));
        slots.set(1, new ItemStack(mekanism.generators.common.registries.GeneratorsBlocks.WIND_GENERATOR));
        slots.set(4, new ItemStack(mekanism.common.registries.MekanismItems.ROBIT.asItem()));
        slots.set(7, new ItemStack(mekanism.common.registries.MekanismBlocks.ULTIMATE_INDUCTION_CELL));
        var input = net.minecraft.world.item.crafting.CraftingInput.of(3, 3, slots);
        h.assertTrue(recipe.matches(input, h.getLevel()), "Large Wind Generator does not match the requested crafting grid");
        var output = recipe.assemble(input);
        h.assertTrue(output.is(com.jerry.meklg.common.registries.LargeGeneratorBlocks.LARGE_WIND_GENERATOR.asItem()) && output.getCount() == 1, "Wrong wind generator crafting output");
        slots.set(7, new ItemStack(Items.DIRT));
        h.assertTrue(!recipe.matches(net.minecraft.world.item.crafting.CraftingInput.of(3, 3, slots), h.getLevel()), "Wind recipe accepts an invalid induction cell");
        h.succeed();
    }

    private static void hbmRecipes(GameTestHelper h) {
        var manager = h.getLevel().getServer().getRecipeManager();
        String[] plants = { "plant_flower_cd0", "plant_flower_cd1", "plant_tall_cd2", "plant_tall_cd3", "plant_tall_cd4", "plant_flower_weed" };
        boolean loaded = net.neoforged.fml.ModList.get().isLoaded("hbm");
        var nutrient = new mekanism.api.chemical.ChemicalStack(h.getLevel().registryAccess().getOrThrow(MoreMachineChemicals.NUTRIENT_SOLUTION), 1);
        for (int i = 0; i < plants.length; i++) {
            var holder = manager.byKey(ResourceKey.create(Registries.RECIPE, Mekmm.rl("compat/hbm/planting/" + plants[i])));
            if (!loaded) {
                h.assertTrue(holder.isEmpty(), "HBM planting recipe loaded without HBM");
                continue;
            }
            var recipe = (com.jerry.mekmm.api.recipes.PlantingRecipe) holder.orElseThrow().value();
            var plant = foreignItem("hbm", plants[i]);
            var product = foreignItem("hbm", i == 5 ? "plant_flower_weed" : "plant_item_mustardwillow");
            h.assertTrue(plant != Items.AIR && product != Items.AIR && recipe.test(new ItemStack(plant), nutrient), "Invalid HBM planting input: " + plants[i]);
            var output = recipe.getMainOutputDefinition().getFirst();
            h.assertTrue(output.is(product) && output.getCount() == (i == 5 ? 1 : i + 1), "Wrong HBM planting yield: " + plants[i]);
        }
        h.assertTrue(manager.byKey(ResourceKey.create(Registries.RECIPE, Mekmm.rl("compat/hbm/planting/plant_tall_weed"))).isEmpty(), "Tall hemp should not have a planting recipe");
        h.succeed();
    }

    private static void cropariumRecipes(GameTestHelper h) {
        var manager = h.getLevel().getServer().getRecipeManager();
        if (!net.neoforged.fml.ModList.get().isLoaded("cp_lib")) {
            for (String crop : java.util.List.of("diamond", "honey", "sea_heart", "everrose")) {
                h.assertTrue(manager.byKey(ResourceKey.create(Registries.RECIPE, Mekmm.rl("compat/cp_lib/planting/" + crop))).isEmpty(), "Optional crop recipe loaded without cp_lib");
            }
            h.succeed();
            return;
        }
        // Compare recipes to Croparium's own harvest mappings without linking the release mod to its API.
        String raw;
        try {
            raw = (String) Class.forName("cn.cp.lib.network.CpLibModVariables").getField("common_crop_mappings").get(null);
        } catch (ReflectiveOperationException error) {
            throw new RuntimeException(error);
        }
        var harvests = new java.util.HashMap<String, String>();
        for (String pair : raw.split(",")) {
            if (!pair.isBlank()) {
                var parts = pair.trim().split("=");
                harvests.put(parts[0], parts[1]);
            }
        }
        harvests.put("cp_lib:sea_heart_crop", "minecraft:heart_of_the_sea");
        harvests.put("cp_lib:turtle_scute_crop", "minecraft:turtle_scute");
        int crops = 0, loaded = 0;
        var nutrient = ChemicalResource.of(h.getLevel().registryAccess().getOrThrow(MoreMachineChemicals.NUTRIENT_SOLUTION));
        for (var entry : BuiltInRegistries.BLOCK.entrySet()) {
            var id = entry.getKey().identifier();
            if (!id.getNamespace().equals("cp_lib") || !id.getPath().endsWith("_crop")) continue;
            crops++;
            String crop = id.getPath().substring(0, id.getPath().length() - 5);
            var holder = manager.byKey(ResourceKey.create(Registries.RECIPE, Mekmm.rl("compat/cp_lib/planting/" + crop)));
            if (crop.equals("everrose") && !net.neoforged.fml.ModList.get().isLoaded("everrose")) {
                h.assertTrue(holder.isEmpty(), "Everrose crop recipe loaded without its output mod");
                continue;
            }
            var recipe = (com.jerry.mekmm.api.recipes.PlantingRecipe) holder.orElseThrow().value();
            var seed = cropItem(crop + "_seeds");
            h.assertTrue(seed != Items.AIR && recipe.test(new ItemStack(seed), new mekanism.api.chemical.ChemicalStack(nutrient.typeHolder(), 1)), "Crop input or nutrient mismatch: " + crop);
            var expected = Identifier.parse(java.util.Objects.requireNonNull(harvests.get(id.toString()), "Unmapped crop: " + id));
            h.assertTrue(BuiltInRegistries.ITEM.containsKey(expected), "Missing crop product: " + expected);
            var output = recipe.getMainOutputDefinition().getFirst();
            h.assertTrue(output.is(BuiltInRegistries.ITEM.getValue(expected)) && output.getCount() == 1, "Incorrect crop product: " + crop);
            h.assertTrue(recipe.perTickUsage() && !recipe.isIncomplete() && !recipe.display().isEmpty(), "Incomplete crop recipe: " + crop);
            loaded++;
        }
        h.assertTrue(crops == 49, "Unexpected number of Croparium crops: " + crops);
        h.assertTrue(loaded == (net.neoforged.fml.ModList.get().isLoaded("everrose") ? 49 : 48), "Missing Croparium recipes: " + loaded);
        System.out.println("MEKMM_CROPARIUM_SMOKE: validated " + loaded + " loaded crop recipes out of " + crops);
        h.succeed();
    }

    private static void register(RegisterGameTestsEvent event, Holder<TestEnvironmentDefinition<?>> env, String name, Consumer<GameTestHelper> body) {
        var data = new TestData<>(env, Mekmm.rl("smoke_empty"), 300, 0, true);
        event.registerTest(Mekmm.rl(name), new GameTestInstance(data) {

            @Override
            public void run(GameTestHelper helper) {
                try {
                    body.accept(helper);
                } catch (RuntimeException error) {
                    error.printStackTrace();
                    throw error;
                }
            }

            @Override
            public MapCodec<? extends GameTestInstance> codec() {
                return FunctionGameTestInstance.CODEC;
            }

            @Override
            protected net.minecraft.network.chat.MutableComponent typeDescription() {
                return Component.literal(name);
            }
        });
    }

    private static void registryAndRecipes(GameTestHelper h) {
        var access = h.getLevel().registryAccess();
        for (var key : java.util.List.of(MoreMachineChemicals.NUTRIENT_SOLUTION, MoreMachineChemicals.NUTRITIONAL_PASTE, MoreMachineChemicals.UU_MATTER, MoreMachineChemicals.UNSTABLE_DIMENSIONAL_GAS)) {
            h.assertTrue(!ChemicalResource.of(access.getOrThrow(key)).isEmpty(), "Missing chemical: " + key);
        }
        var manager = h.getLevel().getServer().getRecipeManager();
        for (String id : java.util.List.of("planting/sapling/oak_sapling", "processing/silver/clump/from_ore", "processing/silver/slurry/clean", "nucleosynthesizing/uu_matter")) {
            var holder = manager.byKey(ResourceKey.create(Registries.RECIPE, Mekmm.rl(id))).orElseThrow();
            h.assertTrue(!holder.value().display().isEmpty(), "Missing display: " + id);
        }
        h.succeed();
    }

    private static void machineRecipeDisplays(GameTestHelper h) {
        var manager = h.getLevel().getServer().getRecipeManager();
        var stamp = (com.jerry.mekmm.api.recipes.StamperRecipe) manager.byKey(ResourceKey.create(Registries.RECIPE,
                Mekmm.rl("smoke/stamper_display"))).orElseThrow().value();
        h.assertTrue(stamp.getToastSymbol().is(MoreMachineBlocks.CNC_STAMPER.asItem()), "Stamper display references an unregistered machine item");
        h.assertTrue(!stamp.display().isEmpty(), "Missing stamper recipe display");
        for (var holder : manager.getRecipes()) {
            if (holder.id().identifier().getNamespace().equals(Mekmm.MOD_ID)) {
                h.assertTrue(!holder.value().display().isEmpty(), "Missing recipe display: " + holder.id());
            }
        }
        h.succeed();
    }

    private static void allMachineEntities(GameTestHelper h) {
        var level = h.getLevel();
        BlockPos pos = h.absolutePos(new BlockPos(2, 2, 2));
        int checked = 0;
        for (var entry : BuiltInRegistries.BLOCK.entrySet()) {
            if (entry.getKey().identifier().getNamespace().equals("mekmm") && entry.getValue() instanceof EntityBlock block) {
                var state = entry.getValue().defaultBlockState();
                var tile = block.newBlockEntity(pos, state);
                h.assertTrue(tile != null, "Missing block entity: " + entry.getKey());
                tile.setLevel(level);
                if (tile instanceof TileEntityMekanism machine) {
                    machine.getComponents();
                    if (machine.getSecurity() != null) {
                        var owner = java.util.UUID.fromString("00000000-0000-0000-0000-000000000123");
                        machine.setOwnerUUID(owner, null);
                        h.assertTrue(owner.equals(machine.getOwnerUUID()), "Owner not retained: " + entry.getKey());
                    }
                }
                checked++;
            }
        }
        h.assertTrue(checked > 50, "Too few machine variants: " + checked);
        System.out.println("MEKMM_PORT_SMOKE: instantiated " + checked + " block entity variants");
        h.succeed();
    }

    private static void processingAndUpgrades(GameTestHelper h) {
        var level = h.getLevel();
        var pos = new BlockPos(2, 2, 2);
        h.setBlock(pos, MoreMachineBlocks.RECYCLER.get());
        var tile = (com.jerry.mekmm.common.tile.machine.TileEntityRecycler) level.getBlockEntity(h.absolutePos(pos));
        var upgrades = level.registryAccess().lookupOrThrow(MekanismRegistries.Keys.UPGRADES);
        var speed = upgrades.getOrThrow(UpgradeIds.SPEED);
        var energy = upgrades.getOrThrow(UpgradeIds.ENERGY);
        int baseline = UpgradeUtils.getEnergyPerTick(tile, 100, energy);
        tile.addUpgrades(level.registryAccess(), speed, speed.value().max());
        h.assertTrue(UpgradeUtils.getEnergyPerTick(tile, 100, energy) == baseline, "Speed upgrade increased power penalty");
        try (var transaction = net.neoforged.neoforge.transfer.transaction.Transaction.openRoot()) {
            tile.getEnergyContainerTyped().insert(1000000, transaction, mekanism.api.AutomationType.INTERNAL);
            var slot = (mekanism.api.inventory.IInventorySlot) field(tile, "inputSlot");
            slot.insert(net.neoforged.neoforge.transfer.item.ItemResource.of(Items.DIRT), 64, transaction, mekanism.api.AutomationType.INTERNAL);
            transaction.commit();
        }
        h.runAfterDelay(80, () -> {
            var input = (mekanism.api.inventory.IInventorySlot) field(tile, "inputSlot");
            h.assertTrue(input.amountAsInt() < 64, "Recycler did not process input");
            h.succeed();
        });
    }

    @SuppressWarnings("unchecked")
    private static void plantingPreservesInput(GameTestHelper h, boolean factory, net.minecraft.world.item.Item seed, net.minecraft.world.item.Item product, int perCycle) {
        var level = h.getLevel();
        var pos = new BlockPos(2, 2, 2);
        var block = factory ? MoreMachineBlocks.getMoreMachineFactory(mekanism.common.tier.FactoryTier.BASIC,
                com.jerry.mekmm.common.content.blocktype.MoreMachineFactoryType.PLANTING_STATION).get() : MoreMachineBlocks.PLANTING_STATION.get();
        h.setBlock(pos, block);
        var tile = (TileEntityMekanism) level.getBlockEntity(h.absolutePos(pos));
        var speed = level.registryAccess().getOrThrow(UpgradeIds.SPEED);
        tile.addUpgrades(level.registryAccess(), speed, speed.value().max());
        var energy = (mekanism.api.energy.IEnergyContainer) field(tile, "energyContainer");
        var input = factory ? ((java.util.List<mekanism.api.inventory.IInventorySlot>) field(tile, "inputSlots")).getFirst() : (mekanism.api.inventory.IInventorySlot) field(tile, "inputSlot");
        var output = factory ? ((java.util.List<mekanism.api.inventory.IInventorySlot>) field(tile, "outputSlots")).getFirst() : (mekanism.api.inventory.IInventorySlot) field(tile, "mainOutputSlot");
        var tank = (mekanism.api.chemical.IChemicalTank) field(tile, "chemicalTank");
        var nutrient = ChemicalResource.of(level.registryAccess().getOrThrow(MoreMachineChemicals.NUTRIENT_SOLUTION));
        try (var tx = net.neoforged.neoforge.transfer.transaction.Transaction.openRoot()) {
            energy.insert(1000000, tx, mekanism.api.AutomationType.INTERNAL);
            h.assertTrue(input.insert(net.neoforged.neoforge.transfer.item.ItemResource.of(seed), 1, tx, mekanism.api.AutomationType.INTERNAL) == 1, "Cannot insert seed");
            tank.insert(nutrient, 10000, tx, mekanism.api.AutomationType.INTERNAL);
            tx.commit();
        }
        long startEnergy = energy.getAmountAsLong();
        long startNutrients = tank.amountAsLong();
        h.runAfterDelay(100, () -> {
            h.assertTrue(input.amountAsInt() == 1 && input.resource().is(seed), "Planting consumed its seed/sapling");
            h.assertTrue(output.amountAsInt() >= 2 * perCycle && output.resource().is(product), "One seed did not produce multiple harvests");
            h.assertTrue(tank.amountAsLong() < startNutrients, "Planting stopped consuming nutrients");
            h.assertTrue(energy.getAmountAsLong() < startEnergy, "Planting stopped consuming energy");
            h.succeed();
        });
    }

    private static Object field(Object target, String name) {
        for (Class<?> type = target.getClass(); type != null; type = type.getSuperclass()) try {
            var field = type.getDeclaredField(name);
            field.setAccessible(true);
            return field.get(target);
        } catch (NoSuchFieldException ignored) {} catch (ReflectiveOperationException error) {
            throw new RuntimeException(error);
        }
        throw new IllegalArgumentException(name);
    }
}
