package com.jerry.datagen.common.recipe.compat;

import com.jerry.datagen.common.recipe.builder.MoreMachineDataShapedRecipeBuilder;
import com.jerry.datagen.common.recipe.imp.MoreMachineRecipeProvider;
import com.jerry.datagen.common.recipe.pattern.Pattern;

import com.jerry.mekaf.common.block.prefab.BlockAdvancedFactoryMachine.BlockAdvancedFactory;
import com.jerry.mekaf.common.content.blocktype.AdvancedFactoryType;
import com.jerry.mekaf.common.item.block.machine.ItemBlockAdvancedFactory;
import com.jerry.mekaf.common.registries.AdvancedFactoryBlocks;

import com.jerry.mekmm.Mekmm;
import com.jerry.mekmm.common.util.MoreMachineEnumUtils;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.common.registration.impl.BlockRegistryObject;
import mekanism.common.resource.PrimaryResource;
import mekanism.common.resource.ResourceType;
import mekanism.common.tags.MekanismTags;
import mekanism.common.tier.FactoryTier;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.Tags;

import fr.iglee42.evolvedmekanism.registries.EMTags;
import fr.iglee42.evolvedmekanism.tiers.EMFactoryTier;

@NothingNullByDefault
public class EMAdvancedFactoryRecipeProvider extends CompatRecipeProvider {

    public EMAdvancedFactoryRecipeProvider(String modId) {
        super(modId);
    }

    @Override
    protected void registerRecipes(RecipeOutput consumer, String basePath, Provider registries) {
        basePath += "factory/";
        String overclockedPath = basePath + "overclocked/";
        String quantumPath = basePath + "quantum/";
        String densePath = basePath + "dense/";
        String multiversalPath = basePath + "multiversal/";
        String creativePath = basePath + "creative/";
        TagKey<Item> uraniumIngot = MekanismTags.Items.PROCESSED_RESOURCES.get(ResourceType.INGOT, PrimaryResource.URANIUM);
        TagKey<Item> tinIngot = MekanismTags.Items.PROCESSED_RESOURCES.get(ResourceType.INGOT, PrimaryResource.TIN);
        for (AdvancedFactoryType type : MoreMachineEnumUtils.ADVANCED_FACTORY_TYPES) {
            BlockRegistryObject<BlockAdvancedFactory<?>, ItemBlockAdvancedFactory> ultimateFactory = AdvancedFactoryBlocks.getAdvancedFactory(FactoryTier.ULTIMATE, type);
            BlockRegistryObject<BlockAdvancedFactory<?>, ItemBlockAdvancedFactory> overclockedFactory = AdvancedFactoryBlocks.getAdvancedFactory(EMFactoryTier.OVERCLOCKED, type);
            BlockRegistryObject<BlockAdvancedFactory<?>, ItemBlockAdvancedFactory> quantumFactory = AdvancedFactoryBlocks.getAdvancedFactory(EMFactoryTier.QUANTUM, type);
            BlockRegistryObject<BlockAdvancedFactory<?>, ItemBlockAdvancedFactory> denseFactory = AdvancedFactoryBlocks.getAdvancedFactory(EMFactoryTier.DENSE, type);
            BlockRegistryObject<BlockAdvancedFactory<?>, ItemBlockAdvancedFactory> multiversalFactory = AdvancedFactoryBlocks.getAdvancedFactory(EMFactoryTier.MULTIVERSAL, type);
            addFactoryRecipe(consumer, overclockedPath, type, overclockedFactory, ultimateFactory.getItemHolder(), uraniumIngot, EMTags.Items.ALLOYS_OVERCLOCKED, EMTags.Items.CIRCUITS_OVERCLOCKED);
            addFactoryRecipe(consumer, quantumPath, type, quantumFactory, overclockedFactory.getItemHolder(), tinIngot, EMTags.Items.ALLOYS_QUANTUM, EMTags.Items.CIRCUITS_QUANTUM);
            addFactoryRecipe(consumer, densePath, type, denseFactory, quantumFactory.getItemHolder(), MekanismTags.Items.INGOTS_BRONZE, EMTags.Items.ALLOYS_DENSE, EMTags.Items.CIRCUITS_DENSE);
            addFactoryRecipe(consumer, multiversalPath, type, multiversalFactory, denseFactory.getItemHolder(), Tags.Items.INGOTS_NETHERITE, EMTags.Items.ALLOYS_MULTIVERSAL, EMTags.Items.CIRCUITS_MULTIVERSAL);
            addFactoryRecipe(consumer, creativePath, type, AdvancedFactoryBlocks.getAdvancedFactory(EMFactoryTier.CREATIVE, type), multiversalFactory.getItemHolder(), Tags.Items.NETHER_STARS, EMTags.Items.ALLOYS_CREATIVE, EMTags.Items.CIRCUITS_CREATIVE_FORGE);
        }
    }

    private void addFactoryRecipe(RecipeOutput consumer, String basePath, AdvancedFactoryType type, BlockRegistryObject<BlockAdvancedFactory<?>, ?> factory, Holder<Item> toUpgrade,
                                  TagKey<Item> ingotTag, TagKey<Item> alloyTag, TagKey<Item> circuitTag) {
        MoreMachineDataShapedRecipeBuilder.shapedRecipe(factory)
                .pattern(MoreMachineRecipeProvider.TIER_PATTERN)
                .key(Pattern.PREVIOUS, toUpgrade.value())
                .key(Pattern.CIRCUIT, circuitTag)
                .key(Pattern.INGOT, ingotTag)
                .key(Pattern.ALLOY, alloyTag)
                .addCondition(modLoaded)
                .build(consumer, Mekmm.rl(basePath + type.getRegistryNameComponent()));
    }
}
