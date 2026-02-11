package com.jerry.datagen.common.recipe.compat;

import com.jerry.datagen.common.recipe.builder.MoreMachineDataShapedRecipeBuilder;
import com.jerry.datagen.common.recipe.imp.MoreMachineRecipeProvider;
import com.jerry.datagen.common.recipe.pattern.Pattern;

import com.jerry.mekmm.Mekmm;
import com.jerry.mekmm.common.block.attribute.MoreMachineAttributeFactoryType;
import com.jerry.mekmm.common.block.prefab.BlockMoreFactoryMachine.BlockMoreMachineFactory;
import com.jerry.mekmm.common.content.blocktype.MoreMachineFactoryType;
import com.jerry.mekmm.common.item.block.machine.ItemBlockMoreMachineFactory;
import com.jerry.mekmm.common.registries.MoreMachineBlocks;
import com.jerry.mekmm.common.util.MoreMachineEnumUtils;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.common.block.attribute.Attribute;
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
public class EMMoreMachineRecipeProvider extends CompatRecipeProvider {

    public EMMoreMachineRecipeProvider(String modId) {
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
        for (MoreMachineFactoryType type : MoreMachineEnumUtils.MM_FACTORY_TYPES) {
            BlockRegistryObject<BlockMoreMachineFactory<?>, ItemBlockMoreMachineFactory> ultimateFactory = MoreMachineBlocks.getMoreMachineFactory(FactoryTier.ULTIMATE, type);
            BlockRegistryObject<BlockMoreMachineFactory<?>, ItemBlockMoreMachineFactory> overclockedFactory = MoreMachineBlocks.getMoreMachineFactory(EMFactoryTier.OVERCLOCKED, type);
            BlockRegistryObject<BlockMoreMachineFactory<?>, ItemBlockMoreMachineFactory> quantumFactory = MoreMachineBlocks.getMoreMachineFactory(EMFactoryTier.QUANTUM, type);
            BlockRegistryObject<BlockMoreMachineFactory<?>, ItemBlockMoreMachineFactory> denseFactory = MoreMachineBlocks.getMoreMachineFactory(EMFactoryTier.DENSE, type);
            BlockRegistryObject<BlockMoreMachineFactory<?>, ItemBlockMoreMachineFactory> multiversalFactory = MoreMachineBlocks.getMoreMachineFactory(EMFactoryTier.MULTIVERSAL, type);
            addFactoryRecipe(consumer, overclockedPath, overclockedFactory, ultimateFactory.getItemHolder(), uraniumIngot, EMTags.Items.ALLOYS_OVERCLOCKED, EMTags.Items.CIRCUITS_OVERCLOCKED);
            addFactoryRecipe(consumer, quantumPath, quantumFactory, overclockedFactory.getItemHolder(), tinIngot, EMTags.Items.ALLOYS_QUANTUM, EMTags.Items.CIRCUITS_QUANTUM);
            addFactoryRecipe(consumer, densePath, denseFactory, quantumFactory.getItemHolder(), MekanismTags.Items.INGOTS_BRONZE, EMTags.Items.ALLOYS_DENSE, EMTags.Items.CIRCUITS_DENSE);
            addFactoryRecipe(consumer, multiversalPath, multiversalFactory, denseFactory.getItemHolder(), Tags.Items.INGOTS_NETHERITE, EMTags.Items.ALLOYS_MULTIVERSAL, EMTags.Items.CIRCUITS_MULTIVERSAL);
            addFactoryRecipe(consumer, creativePath, MoreMachineBlocks.getMoreMachineFactory(EMFactoryTier.CREATIVE, type), multiversalFactory.getItemHolder(), Tags.Items.NETHER_STARS, EMTags.Items.ALLOYS_CREATIVE, EMTags.Items.CIRCUITS_CREATIVE_FORGE);
        }
    }

    private void addFactoryRecipe(RecipeOutput consumer, String basePath, BlockRegistryObject<BlockMoreMachineFactory<?>, ?> factory, Holder<Item> toUpgrade,
                                  TagKey<Item> ingotTag, TagKey<Item> alloyTag, TagKey<Item> circuitTag) {
        MoreMachineDataShapedRecipeBuilder.shapedRecipe(factory)
                .pattern(MoreMachineRecipeProvider.TIER_PATTERN)
                .key(Pattern.PREVIOUS, toUpgrade.value())
                .key(Pattern.CIRCUIT, circuitTag)
                .key(Pattern.INGOT, ingotTag)
                .key(Pattern.ALLOY, alloyTag)
                .addCondition(modLoaded)
                .build(consumer, Mekmm.rl(basePath + Attribute.getOrThrow(factory, MoreMachineAttributeFactoryType.class).getMoreMachineFactoryType().getRegistryNameComponent()));
    }
}
