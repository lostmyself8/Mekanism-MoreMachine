package com.jerry.datagen.recipe.imp;

import com.jerry.datagen.recipe.ISubRecipeProvider;
import com.jerry.datagen.recipe.builder.MekMMDataShapedRecipeBuilder;
import com.jerry.datagen.recipe.pattern.Pattern;
import com.jerry.mekmm.common.block.attribute.MMAttributeFactoryType;
import com.jerry.mekmm.common.block.prefab.MMBlockFactoryMachine;
import com.jerry.mekmm.common.content.blocktype.MMFactoryType;
import com.jerry.mekmm.common.registries.MMBlocks;
import com.jerry.mekmm.Mekmm;
import com.jerry.mekmm.common.item.block.machine.MMItemBlockFactory;
import com.jerry.mekmm.common.util.MMEnumUtils;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.registration.impl.BlockRegistryObject;
import mekanism.common.resource.PrimaryResource;
import mekanism.common.resource.ResourceType;
import mekanism.common.tags.MekanismTags;
import mekanism.common.tier.FactoryTier;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.Tags;

class MMFactoryRecipeProvider implements ISubRecipeProvider {

    @Override
    public void addRecipes(RecipeOutput consumer, HolderLookup.Provider registries) {
        String basePath = "factory/";
        String basicPath = basePath + "basic/";
        String advancedPath = basePath + "advanced/";
        String elitePath = basePath + "elite/";
        String ultimatePath = basePath + "ultimate/";
        TagKey<Item> osmiumIngot = MekanismTags.Items.PROCESSED_RESOURCES.get(ResourceType.INGOT, PrimaryResource.OSMIUM);
        for (MMFactoryType type : MMEnumUtils.MM_FACTORY_TYPES) {
            BlockRegistryObject<MMBlockFactoryMachine.MMBlockFactory<?>, MMItemBlockFactory> basicFactory = MMBlocks.getMMFactory(FactoryTier.BASIC, type);
            BlockRegistryObject<MMBlockFactoryMachine.MMBlockFactory<?>, MMItemBlockFactory> advancedFactory = MMBlocks.getMMFactory(FactoryTier.ADVANCED, type);
            BlockRegistryObject<MMBlockFactoryMachine.MMBlockFactory<?>, MMItemBlockFactory> eliteFactory = MMBlocks.getMMFactory(FactoryTier.ELITE, type);
            addFactoryRecipe(consumer, basicPath, basicFactory, type.getBaseBlock().getItemHolder(), Tags.Items.INGOTS_IRON, MekanismTags.Items.ALLOYS_BASIC, MekanismTags.Items.CIRCUITS_BASIC);
            addFactoryRecipe(consumer, advancedPath, advancedFactory, basicFactory.getItemHolder(), osmiumIngot, MekanismTags.Items.ALLOYS_INFUSED, MekanismTags.Items.CIRCUITS_ADVANCED);
            addFactoryRecipe(consumer, elitePath, eliteFactory, advancedFactory.getItemHolder(), Tags.Items.INGOTS_GOLD, MekanismTags.Items.ALLOYS_REINFORCED, MekanismTags.Items.CIRCUITS_ELITE);
            addFactoryRecipe(consumer, ultimatePath, MMBlocks.getMMFactory(FactoryTier.ULTIMATE, type), eliteFactory.getItemHolder(), Tags.Items.GEMS_DIAMOND, MekanismTags.Items.ALLOYS_ATOMIC, MekanismTags.Items.CIRCUITS_ULTIMATE);
        }
    }

    private void addFactoryRecipe(RecipeOutput consumer, String basePath, BlockRegistryObject<MMBlockFactoryMachine.MMBlockFactory<?>, ?> factory, Holder<Item> toUpgrade,
                                  TagKey<Item> ingotTag, TagKey<Item> alloyTag, TagKey<Item> circuitTag) {
        MekMMDataShapedRecipeBuilder.shapedRecipe(factory)
              .pattern(MoreMachineRecipeProvider.TIER_PATTERN)
              .key(Pattern.PREVIOUS, toUpgrade.value())
              .key(Pattern.CIRCUIT, circuitTag)
              .key(Pattern.INGOT, ingotTag)
              .key(Pattern.ALLOY, alloyTag)
              .build(consumer, Mekmm.rl(basePath + Attribute.getOrThrow(factory, MMAttributeFactoryType.class).getMMFactoryType().getRegistryNameComponent()));
    }
}