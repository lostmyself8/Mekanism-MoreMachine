package com.jerry.datagen.common.recipe.imp;

import com.jerry.datagen.common.recipe.ISubRecipeProvider;
import com.jerry.datagen.common.recipe.builder.MoreMachineDataShapedRecipeBuilder;
import com.jerry.datagen.common.recipe.pattern.Pattern;

import com.jerry.mekmm.Mekmm;
import com.jerry.mekmm.common.block.attribute.MoreMachineAttributeFactoryType;
import com.jerry.mekmm.common.block.prefab.BlockMoreFactoryMachine.BlockMoreMachineFactory;
import com.jerry.mekmm.common.content.blocktype.MoreMachineFactoryType;
import com.jerry.mekmm.common.item.block.machine.ItemBlockMoreMachineFactory;
import com.jerry.mekmm.common.registries.MoreMachineBlocks;
import com.jerry.mekmm.common.util.MoreMachineEnumUtils;

import mekanism.common.block.attribute.Attribute;
import mekanism.common.registration.impl.BlockRegistryObject;
import mekanism.common.resource.PrimaryResource;
import mekanism.common.resource.ResourceType;
import mekanism.common.tags.MekanismTags;
import mekanism.common.tier.FactoryTier;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.Tags;

import org.jetbrains.annotations.NotNull;

class MMFactoryRecipeProvider implements ISubRecipeProvider {

    private final HolderGetter<Item> items;

    public MMFactoryRecipeProvider(HolderGetter<Item> items) {
        this.items = items;
    }

    @Override
    public void addRecipes(RecipeOutput consumer, HolderLookup.Provider registries) {
        String basePath = "factory/";
        String basicPath = basePath + "basic/";
        String advancedPath = basePath + "advanced/";
        String elitePath = basePath + "elite/";
        String ultimatePath = basePath + "ultimate/";
        TagKey<Item> osmiumIngot = MekanismTags.Items.PROCESSED_RESOURCES.get(ResourceType.INGOT, PrimaryResource.OSMIUM);
        for (MoreMachineFactoryType type : MoreMachineEnumUtils.MM_FACTORY_TYPES) {
            BlockRegistryObject<@NotNull BlockMoreMachineFactory<?>, @NotNull ItemBlockMoreMachineFactory> basicFactory = MoreMachineBlocks.getMoreMachineFactory(FactoryTier.BASIC, type);
            BlockRegistryObject<@NotNull BlockMoreMachineFactory<?>, @NotNull ItemBlockMoreMachineFactory> advancedFactory = MoreMachineBlocks.getMoreMachineFactory(FactoryTier.ADVANCED, type);
            BlockRegistryObject<@NotNull BlockMoreMachineFactory<?>, @NotNull ItemBlockMoreMachineFactory> eliteFactory = MoreMachineBlocks.getMoreMachineFactory(FactoryTier.ELITE, type);
            addFactoryRecipe(consumer, basicPath, basicFactory, type.getBaseBlock().getItemHolder(), Tags.Items.INGOTS_IRON, MekanismTags.Items.ALLOYS_BASIC, MekanismTags.Items.CIRCUITS_BASIC);
            addFactoryRecipe(consumer, advancedPath, advancedFactory, basicFactory.getItemHolder(), osmiumIngot, MekanismTags.Items.ALLOYS_INFUSED, MekanismTags.Items.CIRCUITS_ADVANCED);
            addFactoryRecipe(consumer, elitePath, eliteFactory, advancedFactory.getItemHolder(), Tags.Items.INGOTS_GOLD, MekanismTags.Items.ALLOYS_REINFORCED, MekanismTags.Items.CIRCUITS_ELITE);
            addFactoryRecipe(consumer, ultimatePath, MoreMachineBlocks.getMoreMachineFactory(FactoryTier.ULTIMATE, type), eliteFactory.getItemHolder(), Tags.Items.GEMS_DIAMOND, MekanismTags.Items.ALLOYS_ATOMIC, MekanismTags.Items.CIRCUITS_ULTIMATE);
        }
    }

    private void addFactoryRecipe(RecipeOutput consumer, String basePath, BlockRegistryObject<@NotNull BlockMoreMachineFactory<?>, ?> factory, Holder<Item> toUpgrade,
                                  TagKey<Item> ingotTag, TagKey<Item> alloyTag, TagKey<Item> circuitTag) {
        MoreMachineDataShapedRecipeBuilder.shapedRecipe(factory)
                .pattern(MoreMachineRecipeProvider.TIER_PATTERN)
                .key(Pattern.PREVIOUS, toUpgrade.value())
                .key(Pattern.CIRCUIT, items, circuitTag)
                .key(Pattern.INGOT, items, ingotTag)
                .key(Pattern.ALLOY, items, alloyTag)
                .save(consumer, Mekmm.rl(basePath + Attribute.getOrThrow(factory, MoreMachineAttributeFactoryType.class).getMoreMachineFactoryType().getRegistryNameComponent()));
    }
}
