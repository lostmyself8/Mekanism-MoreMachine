package com.jerry.datagen.common.recipe.imp;

import com.jerry.datagen.common.recipe.BaseRecipeProvider;
import com.jerry.datagen.common.recipe.ISubRecipeProvider;
import com.jerry.datagen.common.recipe.builder.ExtendedShapedRecipeBuilder;
import com.jerry.datagen.common.recipe.builder.MoreMachineDataShapedRecipeBuilder;
import com.jerry.datagen.common.recipe.pattern.Pattern;
import com.jerry.datagen.common.recipe.pattern.RecipePattern;

import com.jerry.meklm.common.registries.LargeMachineBlocks;

import com.jerry.mekmm.Mekmm;
import com.jerry.mekmm.common.registries.MoreMachineBlocks;
import com.jerry.mekmm.common.registries.MoreMachineItems;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.common.registration.impl.BlockRegistryObject;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismItems;
import mekanism.common.resource.PrimaryResource;
import mekanism.common.tags.MekanismTags;
import mekanism.generators.common.registries.GeneratorsBlocks;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.HolderSet;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;

import com.jerry.meklg.common.registries.LargeGeneratorBlocks;

import java.util.*;
import java.util.function.BiFunction;

@NothingNullByDefault
public class MoreMachineRecipeProvider extends BaseRecipeProvider {

    static final char DIAMOND_CHAR = 'D';
    static final char GLASS_CHAR = 'G';
    static final char PERSONAL_STORAGE_CHAR = 'P';
    static final char MIXING_CHAR = 'M';
    static final char ROBIT_CHAR = 'R';
    static final char SORTER_CHAR = 'S';
    static final char TELEPORTATION_CORE_CHAR = 'T';

    // TODO: Do we want to use same pattern for fluid tank and chemical tank at some point
    public static final RecipePattern TIER_PATTERN = RecipePattern.createPattern(
            RecipePattern.TripleLine.of(Pattern.ALLOY, Pattern.CIRCUIT, Pattern.ALLOY),
            RecipePattern.TripleLine.of(Pattern.INGOT, Pattern.PREVIOUS, Pattern.INGOT),
            RecipePattern.TripleLine.of(Pattern.ALLOY, Pattern.CIRCUIT, Pattern.ALLOY));
    static final RecipePattern STORAGE_PATTERN = RecipePattern.createPattern(
            RecipePattern.TripleLine.of(Pattern.CONSTANT, Pattern.CONSTANT, Pattern.CONSTANT),
            RecipePattern.TripleLine.of(Pattern.CONSTANT, Pattern.CONSTANT, Pattern.CONSTANT),
            RecipePattern.TripleLine.of(Pattern.CONSTANT, Pattern.CONSTANT, Pattern.CONSTANT));
    static final RecipePattern TYPED_STORAGE_PATTERN = RecipePattern.createPattern(
            RecipePattern.TripleLine.of(Pattern.CONSTANT, Pattern.CONSTANT, Pattern.CONSTANT),
            RecipePattern.TripleLine.of(Pattern.CONSTANT, Pattern.PREVIOUS, Pattern.CONSTANT),
            RecipePattern.TripleLine.of(Pattern.CONSTANT, Pattern.CONSTANT, Pattern.CONSTANT));
    public static final RecipePattern BASIC_MODULE = RecipePattern.createPattern(
            RecipePattern.TripleLine.of(Pattern.ALLOY, Pattern.CONSTANT, Pattern.ALLOY),
            RecipePattern.TripleLine.of(Pattern.ALLOY, Pattern.PREVIOUS, Pattern.ALLOY),
            RecipePattern.TripleLine.of(Pattern.HDPE_CHAR, Pattern.HDPE_CHAR, Pattern.HDPE_CHAR));

    private final List<ISubRecipeProvider> compatProviders = new ArrayList<>();
    private final Set<String> disabledCompats;

    public MoreMachineRecipeProvider(Provider registries, RecipeOutput output, Set<String> disabledCompats) {
        super(output, registries);

        // Mod Compat Recipe providers
        this.disabledCompats = disabledCompats;
        // checkCompat("mysticalagriculture", MysticalRecipeProvider::new);
        // checkCompat("immersiveengineering", IERecipeProvider::new);
        // checkCompat("evolvedmekanism", EMMoreMachineRecipeProvider::new);
        // checkCompat("evolvedmekanism", EMAdvancedFactoryRecipeProvider::new);
    }

    private void checkCompat(String modid, BiFunction<Provider, String, ISubRecipeProvider> providerCreator) {
        if (ModList.get().isLoaded(modid)) {
            compatProviders.add(providerCreator.apply(registries, modid));
        } else {
            disabledCompats.add(modid);
        }
    }

    public Set<String> getDisabledCompats() {
        return Collections.unmodifiableSet(disabledCompats);
    }

    @Override
    protected void addRecipes(Provider registries) {
        addMiscRecipes();
        addChemicalTankRecipes();
        addGearModuleRecipes();
        addLateGameRecipes();
        for (ISubRecipeProvider compatProvider : compatProviders) {
            compatProvider.addRecipes(output, registries);
        }
    }

    @Override
    protected List<ISubRecipeProvider> getSubRecipeProviders() {
        return List.of(
                new MMFactoryRecipeProvider(items),
                new AdvancedFactoryRecipeProvider(items),
                new PlantingRecipeProvider());
    }

    private void addMiscRecipes() {
        // 高级电解核心
        MoreMachineDataShapedRecipeBuilder.shapedRecipe(MoreMachineItems.ADVANCED_ELECTROLYSIS_CORE)
                .pattern(RecipePattern.createPattern(
                        RecipePattern.TripleLine.of(Pattern.ALLOY, 'E', Pattern.ALLOY),
                        RecipePattern.TripleLine.of('B', 'D', 'C'),
                        RecipePattern.TripleLine.of(Pattern.ALLOY, 'E', Pattern.ALLOY)))
                .key(Pattern.ALLOY, items, MekanismTags.Items.ALLOYS_ULTIMATE)
                .key('B', items, MekanismTags.Items.DUSTS_LAPIS)
                .key('C', items, MekanismTags.Items.DUSTS_DIAMOND)
                .key('D', items, MekanismTags.Items.DUSTS_NETHERITE)
                .key('E', MekanismItems.ELECTROLYTIC_CORE)
                // .save(Mekmm.rl("advanced_electrolysis_core"));
                .save(output);
        // 回收机
        MoreMachineDataShapedRecipeBuilder.shapedRecipe(MoreMachineBlocks.RECYCLER)
                .pattern(RecipePattern.createPattern(
                        RecipePattern.TripleLine.of(Pattern.ALLOY, Pattern.CIRCUIT, Pattern.ALLOY),
                        RecipePattern.TripleLine.of(Pattern.OSMIUM, Pattern.CONSTANT, Pattern.OSMIUM),
                        RecipePattern.TripleLine.of(Pattern.ALLOY, Pattern.CIRCUIT, Pattern.ALLOY)))
                .key(Pattern.ALLOY, items, MekanismTags.Items.ALLOYS_ADVANCED)
                .key(Pattern.CIRCUIT, items, MekanismTags.Items.CIRCUITS_ADVANCED)
                .key(Pattern.OSMIUM, osmiumIngot(items))
                .key(Pattern.CONSTANT, MekanismBlocks.CRUSHER)
                // .save(consumer, Mekmm.rl("recycler"));
                .save(output);
        // 种植机
        MoreMachineDataShapedRecipeBuilder.shapedRecipe(MoreMachineBlocks.PLANTING_STATION)
                .pattern(RecipePattern.createPattern(
                        RecipePattern.TripleLine.of(Pattern.ALLOY, Pattern.CIRCUIT, Pattern.ALLOY),
                        RecipePattern.TripleLine.of(Pattern.CONSTANT, Pattern.STEEL_CASING, Pattern.CONSTANT),
                        RecipePattern.TripleLine.of(Pattern.ALLOY, Pattern.CIRCUIT, Pattern.ALLOY)))
                .key(Pattern.ALLOY, items, MekanismTags.Items.ALLOYS_REINFORCED)
                .key(Pattern.CIRCUIT, items, MekanismTags.Items.CIRCUITS_ELITE)
                .key(Pattern.CONSTANT, MekanismItems.BIO_FUEL)
                .key(Pattern.STEEL_CASING, MekanismBlocks.STEEL_CASING)
                // .save(consumer, Mekmm.rl("planting_station"));
                .save(output);
        // 压模机
        MoreMachineDataShapedRecipeBuilder.shapedRecipe(MoreMachineBlocks.CNC_STAMPER)
                .pattern(RecipePattern.createPattern(
                        RecipePattern.TripleLine.of(Pattern.ALLOY, Pattern.CIRCUIT, Pattern.ALLOY),
                        RecipePattern.TripleLine.of(Pattern.CONSTANT, Pattern.STEEL_CASING, Pattern.CONSTANT),
                        RecipePattern.TripleLine.of(Pattern.ALLOY, Pattern.CIRCUIT, Pattern.ALLOY)))
                .key(Pattern.ALLOY, items, MekanismTags.Items.ALLOYS_BASIC)
                .key(Pattern.CIRCUIT, items, MekanismTags.Items.CIRCUITS_BASIC)
                .key(Pattern.CONSTANT, Ingredient.of(Items.PISTON, Items.STICKY_PISTON))
                .key(Pattern.STEEL_CASING, MekanismBlocks.STEEL_CASING)
                // .save(consumer, Mekmm.rl("cnc_stamper"));
                .save(output);
        // 车床
        MoreMachineDataShapedRecipeBuilder.shapedRecipe(MoreMachineBlocks.CNC_LATHE)
                .pattern(RecipePattern.createPattern(
                        RecipePattern.TripleLine.of(Pattern.ALLOY, Pattern.CIRCUIT, Pattern.ALLOY),
                        RecipePattern.TripleLine.of(Pattern.CONSTANT, Pattern.STEEL_CASING, Pattern.CONSTANT),
                        RecipePattern.TripleLine.of(Pattern.ALLOY, Pattern.CIRCUIT, Pattern.ALLOY)))
                .key(Pattern.ALLOY, items, MekanismTags.Items.ALLOYS_BASIC)
                .key(Pattern.CIRCUIT, items, MekanismTags.Items.CIRCUITS_BASIC)
                .key(Pattern.CONSTANT, MekanismItems.ROBIT)
                .key(Pattern.STEEL_CASING, MekanismBlocks.STEEL_CASING)
                // .save(consumer, Mekmm.rl("cnc_lathe"));
                .save(output);
        // 轧机
        MoreMachineDataShapedRecipeBuilder.shapedRecipe(MoreMachineBlocks.CNC_ROLLING_MILL)
                .pattern(RecipePattern.createPattern(
                        RecipePattern.TripleLine.of(Pattern.ALLOY, Pattern.CIRCUIT, Pattern.ALLOY),
                        RecipePattern.TripleLine.of(Pattern.STEEL, Pattern.STEEL_CASING, Pattern.STEEL),
                        RecipePattern.TripleLine.of(Pattern.ALLOY, Pattern.CIRCUIT, Pattern.ALLOY)))
                .key(Pattern.ALLOY, items, MekanismTags.Items.ALLOYS_BASIC)
                .key(Pattern.CIRCUIT, items, MekanismTags.Items.CIRCUITS_BASIC)
                .key(Pattern.STEEL, items, MekanismTags.Items.INGOTS_STEEL)
                .key(Pattern.STEEL_CASING, MekanismBlocks.STEEL_CASING)
                // .save(consumer, Mekmm.rl("cnc_rolling_mill"));
                .save(output);
        // 大型回旋
        MoreMachineDataShapedRecipeBuilder.shapedRecipe(LargeMachineBlocks.LARGE_ROTARY_CONDENSENTRATOR)
                .pattern(RecipePattern.createPattern(
                        RecipePattern.TripleLine.of(Pattern.BLOCK, Pattern.CIRCUIT, Pattern.BLOCK),
                        RecipePattern.TripleLine.of(Pattern.TANK, Pattern.ROBIT, 'W'),
                        RecipePattern.TripleLine.of(Pattern.BLOCK, Pattern.CIRCUIT, Pattern.BLOCK)))
                .key(Pattern.BLOCK, items, MekanismTags.Items.STORAGE_BLOCKS_STEEL)
                .key(Pattern.CIRCUIT, items, MekanismTags.Items.CIRCUITS_ULTIMATE)
                .key(Pattern.TANK, LargeMachineBlocks.ULTIMATE_MAX_CHEMICAL_TANK)
                .key(Pattern.ROBIT, MekanismItems.ROBIT)
                .key('W', MekanismBlocks.ULTIMATE_FLUID_TANK)
                // .save(consumer, Mekmm.rl("large_rotary_condensentrator"));
                .save(output);
        // 大灌注
        MoreMachineDataShapedRecipeBuilder.shapedRecipe(LargeMachineBlocks.LARGE_CHEMICAL_INFUSER)
                .pattern(RecipePattern.createPattern(
                        RecipePattern.TripleLine.of(Pattern.BLOCK, Pattern.CIRCUIT, Pattern.BLOCK),
                        RecipePattern.TripleLine.of(Pattern.TANK, Pattern.ROBIT, Pattern.TANK),
                        RecipePattern.TripleLine.of(Pattern.BLOCK, Pattern.CIRCUIT, Pattern.BLOCK)))
                .key(Pattern.BLOCK, items, MekanismTags.Items.STORAGE_BLOCKS_STEEL)
                .key(Pattern.CIRCUIT, items, MekanismTags.Items.CIRCUITS_ULTIMATE)
                .key(Pattern.TANK, LargeMachineBlocks.ULTIMATE_MAX_CHEMICAL_TANK)
                .key(Pattern.ROBIT, MekanismItems.ROBIT)
                // .save(consumer, Mekmm.rl("large_chemical_infuser"));
                .save(output);
        // 大电解
        MoreMachineDataShapedRecipeBuilder.shapedRecipe(LargeMachineBlocks.LARGE_ELECTROLYTIC_SEPARATOR)
                .pattern(RecipePattern.createPattern(
                        RecipePattern.TripleLine.of(Pattern.BLOCK, 'E', Pattern.BLOCK),
                        RecipePattern.TripleLine.of('W', Pattern.ROBIT, Pattern.TANK),
                        RecipePattern.TripleLine.of(Pattern.BLOCK, 'E', Pattern.BLOCK)))
                .key(Pattern.BLOCK, items, MekanismTags.Items.STORAGE_BLOCKS_STEEL)
                .key('E', MoreMachineItems.ADVANCED_ELECTROLYSIS_CORE)
                .key('W', MekanismBlocks.ULTIMATE_FLUID_TANK)
                .key(Pattern.ROBIT, MekanismItems.ROBIT)
                .key(Pattern.TANK, LargeMachineBlocks.ULTIMATE_MAX_CHEMICAL_TANK)
                // .save(consumer, Mekmm.rl("large_electrolytic_separator"));
                .save(output);
        // 大中子，需要加载MekanismGenerators
        MoreMachineDataShapedRecipeBuilder.shapedRecipe(LargeMachineBlocks.LARGE_ANTIPROTONIC_NUCLEOSYNTHESIZER)
                .pattern(RecipePattern.createPattern(
                        RecipePattern.TripleLine.of(Pattern.BLOCK, Pattern.CIRCUIT, Pattern.BLOCK),
                        RecipePattern.TripleLine.of(Pattern.TANK, Pattern.ROBIT, Pattern.PREVIOUS),
                        RecipePattern.TripleLine.of(Pattern.BLOCK, Pattern.CIRCUIT, Pattern.BLOCK)))
                .key(Pattern.BLOCK, items, MekanismTags.Items.STORAGE_BLOCKS_STEEL)
                .key(Pattern.CIRCUIT, items, MekanismTags.Items.PELLETS_ANTIMATTER)
                .key(Pattern.TANK, LargeMachineBlocks.ULTIMATE_MAX_CHEMICAL_TANK)
                .key(Pattern.ROBIT, MekanismItems.ROBIT)
                .key(Pattern.PREVIOUS, items, MekanismTags.Items.PERSONAL_STORAGE)
                .save(output);
        MoreMachineDataShapedRecipeBuilder.shapedRecipe(LargeMachineBlocks.LARGE_PIGMENT_MIXER)
                .pattern(RecipePattern.createPattern(
                        RecipePattern.TripleLine.of(Pattern.BLOCK, Pattern.CIRCUIT, Pattern.BLOCK),
                        RecipePattern.TripleLine.of(Pattern.TANK, Pattern.ROBIT, Pattern.TANK),
                        RecipePattern.TripleLine.of(Pattern.BLOCK, Pattern.TANK, Pattern.BLOCK)))
                .key(Pattern.BLOCK, items, MekanismTags.Items.STORAGE_BLOCKS_STEEL)
                .key(Pattern.CIRCUIT, MekanismItems.HDPE_ROD)
                .key(Pattern.TANK, LargeMachineBlocks.ULTIMATE_MAX_CHEMICAL_TANK)
                .key(Pattern.ROBIT, MekanismItems.ROBIT)
                .save(output);
        MoreMachineDataShapedRecipeBuilder.shapedRecipe(LargeMachineBlocks.LARGE_SOLAR_NEUTRON_ACTIVATOR)
                .pattern(RecipePattern.createPattern(
                        RecipePattern.TripleLine.of('S', 'S', 'S'),
                        RecipePattern.TripleLine.of(Pattern.BLOCK, Pattern.ROBIT, Pattern.BLOCK),
                        RecipePattern.TripleLine.of(Pattern.TANK, 'L', Pattern.TANK)))
                .key('S', GeneratorsBlocks.ADVANCED_SOLAR_GENERATOR)
                .key(Pattern.BLOCK, items, MekanismTags.Items.STORAGE_BLOCKS_STEEL)
                .key(Pattern.ROBIT, MekanismItems.ROBIT)
                .key(Pattern.TANK, LargeMachineBlocks.ULTIMATE_MAX_CHEMICAL_TANK)
                .key('L', MekanismBlocks.LASER)
                .addCondition(new ModLoadedCondition("mekanismgenerators"))
                // .save(consumer, Mekmm.rl("large_solar_neutron_activator"));
                .save(output);
        // 大热力
        if (Mekmm.hooks.mekanismgenerators.isLoaded()) {
            MoreMachineDataShapedRecipeBuilder.shapedRecipe(LargeGeneratorBlocks.LARGE_HEAT_GENERATOR)
                    .pattern(RecipePattern.createPattern(
                            RecipePattern.TripleLine.of(Pattern.BLOCK, 'S', Pattern.BLOCK),
                            RecipePattern.TripleLine.of(Pattern.TANK, Pattern.ROBIT, Pattern.TANK),
                            RecipePattern.TripleLine.of(Pattern.BLOCK, 'S', Pattern.BLOCK)))
                    .key(Pattern.BLOCK, items, MekanismTags.Items.STORAGE_BLOCKS_STEEL)
                    .key('S', MekanismBlocks.SUPERHEATING_ELEMENT)
                    .key(Pattern.TANK, MekanismBlocks.ULTIMATE_FLUID_TANK)
                    .key(Pattern.ROBIT, MekanismItems.ROBIT)
                    .addCondition(new ModLoadedCondition("mekanismgenerators"))
                    // .save(consumer, Mekmm.rl("large_heat_generator"));
                    .save(output);
            // 大燃气
            MoreMachineDataShapedRecipeBuilder.shapedRecipe(LargeGeneratorBlocks.LARGE_GAS_BURNING_GENERATOR)
                    .pattern(RecipePattern.createPattern(
                            RecipePattern.TripleLine.of(Pattern.TANK, 'E', Pattern.TANK),
                            RecipePattern.TripleLine.of(Pattern.BLOCK, Pattern.ROBIT, Pattern.BLOCK),
                            RecipePattern.TripleLine.of(Pattern.TANK, 'E', Pattern.TANK)))
                    .key(Pattern.TANK, LargeMachineBlocks.ULTIMATE_MAX_CHEMICAL_TANK)
                    .key('E', MoreMachineItems.ADVANCED_ELECTROLYSIS_CORE)
                    .key(Pattern.BLOCK, items, MekanismTags.Items.STORAGE_BLOCKS_STEEL)
                    .key(Pattern.ROBIT, MekanismItems.ROBIT)
                    .addCondition(new ModLoadedCondition("mekanismgenerators"))
                    // .save(consumer, Mekmm.rl("large_gas_burning_generator"));
                    .save(output);
        }
    }

    private void addChemicalTankRecipes() {
        addChemicalTankRecipe(LargeMachineBlocks.BASIC_MID_CHEMICAL_TANK, MekanismBlocks.BASIC_CHEMICAL_TANK, "mid_chemical_tank/use_tank/basic");
        addChemicalTankRecipe(LargeMachineBlocks.ADVANCED_MID_CHEMICAL_TANK, MekanismBlocks.ADVANCED_CHEMICAL_TANK, "mid_chemical_tank/use_tank/advanced");
        addChemicalTankRecipe(LargeMachineBlocks.ELITE_MID_CHEMICAL_TANK, MekanismBlocks.ELITE_CHEMICAL_TANK, "mid_chemical_tank/use_tank/elite");
        addChemicalTankRecipe(LargeMachineBlocks.ULTIMATE_MID_CHEMICAL_TANK, MekanismBlocks.ULTIMATE_CHEMICAL_TANK, "mid_chemical_tank/use_tank/ultimate");

        addChemicalTankRecipe(LargeMachineBlocks.BASIC_MAX_CHEMICAL_TANK, LargeMachineBlocks.BASIC_MID_CHEMICAL_TANK, "max_chemical_tank/use_tank/basic");
        addChemicalTankRecipe(LargeMachineBlocks.ADVANCED_MAX_CHEMICAL_TANK, LargeMachineBlocks.ADVANCED_MID_CHEMICAL_TANK, "max_chemical_tank/use_tank/advanced");
        addChemicalTankRecipe(LargeMachineBlocks.ELITE_MAX_CHEMICAL_TANK, LargeMachineBlocks.ELITE_MID_CHEMICAL_TANK, "max_chemical_tank/use_tank/elite");
        addChemicalTankRecipe(LargeMachineBlocks.ULTIMATE_MAX_CHEMICAL_TANK, LargeMachineBlocks.ULTIMATE_MID_CHEMICAL_TANK, "max_chemical_tank/use_tank/ultimate");

        addChemicalTankUpgradeRecipe(LargeMachineBlocks.ADVANCED_MID_CHEMICAL_TANK, LargeMachineBlocks.BASIC_MID_CHEMICAL_TANK,
                MekanismTags.Items.ALLOYS_INFUSED, osmiumIngot(items), "mid_chemical_tank/advanced");
        addChemicalTankUpgradeRecipe(LargeMachineBlocks.ELITE_MID_CHEMICAL_TANK, LargeMachineBlocks.ADVANCED_MID_CHEMICAL_TANK,
                MekanismTags.Items.ALLOYS_REINFORCED, items.getOrThrow(Tags.Items.INGOTS_GOLD), "mid_chemical_tank/elite");
        addChemicalTankUpgradeRecipe(LargeMachineBlocks.ULTIMATE_MID_CHEMICAL_TANK, LargeMachineBlocks.ELITE_MID_CHEMICAL_TANK,
                MekanismTags.Items.ALLOYS_ATOMIC, items.getOrThrow(Tags.Items.GEMS_DIAMOND), "mid_chemical_tank/ultimate");
        addChemicalTankUpgradeRecipe(LargeMachineBlocks.ADVANCED_MAX_CHEMICAL_TANK, LargeMachineBlocks.BASIC_MAX_CHEMICAL_TANK,
                MekanismTags.Items.ALLOYS_INFUSED, osmiumIngot(items), "max_chemical_tank/advanced");
        addChemicalTankUpgradeRecipe(LargeMachineBlocks.ELITE_MAX_CHEMICAL_TANK, LargeMachineBlocks.ADVANCED_MAX_CHEMICAL_TANK,
                MekanismTags.Items.ALLOYS_REINFORCED, items.getOrThrow(Tags.Items.INGOTS_GOLD), "max_chemical_tank/elite");
        addChemicalTankUpgradeRecipe(LargeMachineBlocks.ULTIMATE_MAX_CHEMICAL_TANK, LargeMachineBlocks.ELITE_MAX_CHEMICAL_TANK,
                MekanismTags.Items.ALLOYS_ATOMIC, items.getOrThrow(Tags.Items.GEMS_DIAMOND), "max_chemical_tank/ultimate");
    }

    private void addChemicalTankRecipe(BlockRegistryObject<?, ?> result, BlockRegistryObject<?, ?> chemicalTank, String path) {
        MoreMachineDataShapedRecipeBuilder.shapedRecipe(result)
                .pattern(RecipePattern.createPattern(
                        RecipePattern.TripleLine.of(Pattern.ALLOY, Pattern.ALLOY, Pattern.ALLOY),
                        RecipePattern.TripleLine.of(Pattern.ALLOY, Pattern.CIRCUIT, Pattern.ALLOY),
                        RecipePattern.TripleLine.of(Pattern.ALLOY, Pattern.CIRCUIT, Pattern.ALLOY)))
                .key(Pattern.ALLOY, MekanismBlocks.DYNAMIC_TANK)
                .key(Pattern.CIRCUIT, chemicalTank)
                .save(output, Mekmm.rl(path));
    }

    private void addChemicalTankUpgradeRecipe(BlockRegistryObject<?, ?> result, BlockRegistryObject<?, ?> previous, TagKey<Item> alloyTag, HolderSet<Item> ingot, String path) {
        MoreMachineDataShapedRecipeBuilder.shapedRecipe(result)
                .pattern(RecipePattern.createPattern(
                        RecipePattern.TripleLine.of(Pattern.ALLOY, Pattern.OSMIUM, Pattern.ALLOY),
                        RecipePattern.TripleLine.of(Pattern.OSMIUM, Pattern.PREVIOUS, Pattern.OSMIUM),
                        RecipePattern.TripleLine.of(Pattern.ALLOY, Pattern.OSMIUM, Pattern.ALLOY)))
                .key(Pattern.PREVIOUS, previous)
                .key(Pattern.OSMIUM, ingot)
                .key(Pattern.ALLOY, items, alloyTag)
                .save(output, Mekmm.rl(path));
    }

    private void addGearModuleRecipes() {}

    private void addLateGameRecipes() {
        // 复制机
        MoreMachineDataShapedRecipeBuilder.shapedRecipe(MoreMachineBlocks.REPLICATOR)
                .pattern(RecipePattern.createPattern(
                        RecipePattern.TripleLine.of(Pattern.ALLOY, Pattern.CONSTANT, Pattern.ALLOY),
                        RecipePattern.TripleLine.of(Pattern.CIRCUIT, Pattern.STEEL_CASING, Pattern.CIRCUIT),
                        RecipePattern.TripleLine.of(Pattern.ALLOY, 'S', Pattern.ALLOY)))
                .key(Pattern.ALLOY, items, MekanismTags.Items.ALLOYS_ATOMIC)
                .key(Pattern.CIRCUIT, items, MekanismTags.Items.CIRCUITS_ULTIMATE)
                .key(Pattern.CONSTANT, MoreMachineItems.UU_MATTER)
                .key('S', items, Tags.Items.CHESTS)
                .key(Pattern.STEEL_CASING, MekanismBlocks.STEEL_CASING)
                // .save(consumer, Mekmm.rl("replicator"));
                .save(output);
        // 流体复制机
        MoreMachineDataShapedRecipeBuilder.shapedRecipe(MoreMachineBlocks.FLUID_REPLICATOR)
                .pattern(RecipePattern.createPattern(
                        RecipePattern.TripleLine.of(Pattern.ALLOY, Pattern.CONSTANT, Pattern.ALLOY),
                        RecipePattern.TripleLine.of(Pattern.CIRCUIT, Pattern.STEEL_CASING, Pattern.CIRCUIT),
                        RecipePattern.TripleLine.of(Pattern.ALLOY, Pattern.BUCKET, Pattern.ALLOY)))
                .key(Pattern.ALLOY, items, MekanismTags.Items.ALLOYS_ATOMIC)
                .key(Pattern.CIRCUIT, items, MekanismTags.Items.CIRCUITS_ULTIMATE)
                .key(Pattern.CONSTANT, MoreMachineItems.UU_MATTER)
                .key(Pattern.BUCKET, Items.BUCKET)
                .key(Pattern.STEEL_CASING, MekanismBlocks.STEEL_CASING)
                // .save(consumer, Mekmm.rl("fluid_replicator"));
                .save(output);
        // 化学品复制机
        MoreMachineDataShapedRecipeBuilder.shapedRecipe(MoreMachineBlocks.CHEMICAL_REPLICATOR)
                .pattern(RecipePattern.createPattern(
                        RecipePattern.TripleLine.of(Pattern.ALLOY, Pattern.CONSTANT, Pattern.ALLOY),
                        RecipePattern.TripleLine.of(Pattern.CIRCUIT, Pattern.STEEL_CASING, Pattern.CIRCUIT),
                        RecipePattern.TripleLine.of(Pattern.ALLOY, 'T', Pattern.ALLOY)))
                .key(Pattern.ALLOY, items, MekanismTags.Items.ALLOYS_ATOMIC)
                .key(Pattern.CIRCUIT, items, MekanismTags.Items.CIRCUITS_ULTIMATE)
                .key(Pattern.CONSTANT, MoreMachineItems.UU_MATTER)
                .key('T', MekanismBlocks.BASIC_CHEMICAL_TANK)
                .key(Pattern.STEEL_CASING, MekanismBlocks.STEEL_CASING)
                // .save(consumer, Mekmm.rl("chemical_replicator"));
                .save(output);
        // 环境气体收集器
        ExtendedShapedRecipeBuilder.shapedRecipe(MoreMachineBlocks.AMBIENT_GAS_COLLECTOR)
                .pattern(RecipePattern.createPattern(
                        RecipePattern.TripleLine.of(Pattern.ALLOY, Pattern.CONSTANT, Pattern.ALLOY),
                        RecipePattern.TripleLine.of(Pattern.TANK, Pattern.STEEL_CASING, Pattern.TANK),
                        RecipePattern.TripleLine.of(Pattern.OSMIUM, Pattern.OSMIUM, Pattern.OSMIUM)))
                .key(Pattern.ALLOY, items, MekanismTags.Items.ALLOYS_ATOMIC)
                .key(Pattern.CONSTANT, MekanismBlocks.ULTIMATE_PRESSURIZED_TUBE)
                .key(Pattern.TANK, MekanismBlocks.ULTIMATE_CHEMICAL_TANK)
                .key(Pattern.STEEL_CASING, MekanismBlocks.STEEL_CASING)
                .key(Pattern.OSMIUM, items, MekanismTags.Items.PROCESSED_RESOURCE_BLOCKS.get(PrimaryResource.OSMIUM))
                // .save(consumer, Mekmm.rl("ambient_gas_collector"));
                .save(output);
        // 无线充电站
        ExtendedShapedRecipeBuilder.shapedRecipe(MoreMachineBlocks.WIRELESS_CHARGING_STATION)
                .pattern(RecipePattern.createPattern(
                        RecipePattern.TripleLine.of(Pattern.HDPE_CHAR, Pattern.CONSTANT, Pattern.HDPE_CHAR),
                        RecipePattern.TripleLine.of(Pattern.HDPE_CHAR, Pattern.ROBIT, Pattern.HDPE_CHAR),
                        RecipePattern.TripleLine.of(Pattern.CIRCUIT, 'E', Pattern.CIRCUIT)))
                .key(Pattern.HDPE_CHAR, MekanismItems.HDPE_SHEET)
                .key(Pattern.CONSTANT, MekanismItems.TELEPORTATION_CORE)
                .key(Pattern.CIRCUIT, items, MekanismTags.Items.CIRCUITS_ULTIMATE)
                .key(Pattern.ROBIT, MekanismItems.ROBIT)
                .key('E', MekanismBlocks.ULTIMATE_ENERGY_CUBE)
                // .save(consumer, Mekmm.rl("wireless_charging_station"));
                .save(output);
        // 无线传输站
        ExtendedShapedRecipeBuilder.shapedRecipe(MoreMachineBlocks.WIRELESS_TRANSMISSION_STATION)
                .pattern(RecipePattern.createPattern(
                        RecipePattern.TripleLine.of(Pattern.HDPE_CHAR, Pattern.CONSTANT, Pattern.HDPE_CHAR),
                        RecipePattern.TripleLine.of(Pattern.HDPE_CHAR, Pattern.ROBIT, Pattern.HDPE_CHAR),
                        RecipePattern.TripleLine.of(Pattern.PLUTONIUM, 'Q', Pattern.PLUTONIUM)))
                .key(Pattern.HDPE_CHAR, MekanismItems.HDPE_SHEET)
                .key(Pattern.CONSTANT, MekanismBlocks.SUPERCHARGED_COIL)
                .key(Pattern.PLUTONIUM, items, MekanismTags.Items.PELLETS_PLUTONIUM)
                .key(Pattern.ROBIT, MekanismItems.ROBIT)
                .key('Q', MekanismBlocks.QUANTUM_ENTANGLOPORTER)
                // .save(consumer, Mekmm.rl("wireless_transmission_station"));
                .save(output);
    }
}
