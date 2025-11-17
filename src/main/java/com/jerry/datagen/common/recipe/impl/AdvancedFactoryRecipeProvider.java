package com.jerry.datagen.common.recipe.impl;

import com.jerry.datagen.common.recipe.ISubRecipeProvider;
import com.jerry.datagen.common.recipe.builder.MoreMachineDataShapedRecipeBuilder;
import com.jerry.datagen.common.recipe.pattern.Pattern;

import com.jerry.mekaf.common.block.attribute.AttributeAdvancedFactoryType;
import com.jerry.mekaf.common.block.prefab.BlockAdvancedFactoryMachine.BlockAdvancedFactory;
import com.jerry.mekaf.common.content.blocktype.AdvancedFactoryType;
import com.jerry.mekaf.common.item.ItemBlockAdvancedFactory;
import com.jerry.mekaf.common.registries.AdvancedFactoryBlocks;

import com.jerry.mekmm.Mekmm;
import com.jerry.mekmm.common.util.MoreMachineEnumUtils;

import mekanism.api.providers.IItemProvider;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.registration.impl.BlockRegistryObject;
import mekanism.common.resource.PrimaryResource;
import mekanism.common.resource.ResourceType;
import mekanism.common.tags.MekanismTags;
import mekanism.common.tier.FactoryTier;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

class AdvancedFactoryRecipeProvider implements ISubRecipeProvider {

    @Override
    public void addRecipes(Consumer<FinishedRecipe> consumer) {
        String basePath = "factory/";
        String basicPath = basePath + "basic/";
        String advancedPath = basePath + "advanced/";
        String elitePath = basePath + "elite/";
        String ultimatePath = basePath + "ultimate/";
        TagKey<Item> osmiumIngot = MekanismTags.Items.PROCESSED_RESOURCES.get(ResourceType.INGOT, PrimaryResource.OSMIUM);
        for (AdvancedFactoryType type : MoreMachineEnumUtils.ADVANCED_FACTORY_TYPES) {
            BlockRegistryObject<BlockAdvancedFactory<?>, ItemBlockAdvancedFactory> basicFactory = AdvancedFactoryBlocks.getAdvancedFactory(FactoryTier.BASIC, type);
            BlockRegistryObject<BlockAdvancedFactory<?>, ItemBlockAdvancedFactory> advancedFactory = AdvancedFactoryBlocks.getAdvancedFactory(FactoryTier.ADVANCED, type);
            BlockRegistryObject<BlockAdvancedFactory<?>, ItemBlockAdvancedFactory> eliteFactory = AdvancedFactoryBlocks.getAdvancedFactory(FactoryTier.ELITE, type);
            addFactoryRecipe(consumer, basicPath, basicFactory, type.getBaseBlock(), Tags.Items.INGOTS_IRON, MekanismTags.Items.ALLOYS_BASIC, MekanismTags.Items.CIRCUITS_BASIC);
            addFactoryRecipe(consumer, advancedPath, advancedFactory, basicFactory, osmiumIngot, MekanismTags.Items.ALLOYS_INFUSED, MekanismTags.Items.CIRCUITS_ADVANCED);
            addFactoryRecipe(consumer, elitePath, eliteFactory, advancedFactory, Tags.Items.INGOTS_GOLD, MekanismTags.Items.ALLOYS_REINFORCED, MekanismTags.Items.CIRCUITS_ELITE);
            addFactoryRecipe(consumer, ultimatePath, AdvancedFactoryBlocks.getAdvancedFactory(FactoryTier.ULTIMATE, type), eliteFactory, Tags.Items.GEMS_DIAMOND, MekanismTags.Items.ALLOYS_ATOMIC, MekanismTags.Items.CIRCUITS_ULTIMATE);
        }
    }

    private void addFactoryRecipe(Consumer<FinishedRecipe> consumer, String basePath, BlockRegistryObject<BlockAdvancedFactory<?>, ?> factory, IItemProvider toUpgrade,
                                  TagKey<Item> ingotTag, TagKey<Item> alloyTag, TagKey<Item> circuitTag) {
        MoreMachineDataShapedRecipeBuilder.shapedRecipe(factory)
                .pattern(MoreMachineRecipeProvider.TIER_PATTERN)
                .key(Pattern.PREVIOUS, toUpgrade)
                .key(Pattern.CIRCUIT, circuitTag)
                .key(Pattern.INGOT, ingotTag)
                .key(Pattern.ALLOY, alloyTag)
                .build(consumer, Mekmm.rl(basePath + Attribute.get(factory, AttributeAdvancedFactoryType.class).getAdvancedFactoryType().getRegistryNameComponent()));
    }
}
