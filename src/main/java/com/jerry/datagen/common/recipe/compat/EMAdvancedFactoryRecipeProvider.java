package com.jerry.datagen.common.recipe.compat;

import com.jerry.datagen.common.recipe.builder.MoreMachineDataShapedRecipeBuilder;
import com.jerry.datagen.common.recipe.impl.MoreMachineRecipeProvider;
import com.jerry.datagen.common.recipe.pattern.Pattern;

import com.jerry.mekaf.common.block.attribute.AttributeAdvancedFactoryType;
import com.jerry.mekaf.common.block.prefab.BlockAdvancedFactoryMachine.BlockAdvancedFactory;
import com.jerry.mekaf.common.content.blocktype.AdvancedFactoryType;
import com.jerry.mekaf.common.item.ItemBlockAdvancedFactory;
import com.jerry.mekaf.common.registries.AdvancedFactoryBlocks;

import com.jerry.mekmm.Mekmm;
import com.jerry.mekmm.common.util.MoreMachineEnumUtils;

import mekanism.api.annotations.ParametersAreNotNullByDefault;
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

import fr.iglee42.evolvedmekanism.registries.EMTags;
import fr.iglee42.evolvedmekanism.tiers.EMFactoryTier;

import java.util.function.Consumer;

@ParametersAreNotNullByDefault
public class EMAdvancedFactoryRecipeProvider extends CompatRecipeProvider {

    public EMAdvancedFactoryRecipeProvider(String modId) {
        super(modId);
    }

    @Override
    protected void registerRecipes(Consumer<FinishedRecipe> consumer, String basePath) {
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
            addFactoryRecipe(consumer, overclockedPath, overclockedFactory, ultimateFactory, uraniumIngot, EMTags.Items.ALLOYS_OVERCLOCKED, EMTags.Items.CIRCUITS_OVERCLOCKED);
            addFactoryRecipe(consumer, quantumPath, quantumFactory, overclockedFactory, tinIngot, EMTags.Items.ALLOYS_QUANTUM, EMTags.Items.CIRCUITS_QUANTUM);
            addFactoryRecipe(consumer, densePath, denseFactory, quantumFactory, MekanismTags.Items.INGOTS_BRONZE, EMTags.Items.ALLOYS_DENSE, EMTags.Items.CIRCUITS_DENSE);
            addFactoryRecipe(consumer, multiversalPath, multiversalFactory, denseFactory, Tags.Items.INGOTS_NETHERITE, EMTags.Items.ALLOYS_MULTIVERSAL, EMTags.Items.CIRCUITS_MULTIVERSAL);
            addFactoryRecipe(consumer, creativePath, AdvancedFactoryBlocks.getAdvancedFactory(EMFactoryTier.CREATIVE, type), multiversalFactory, Tags.Items.NETHER_STARS, EMTags.Items.ALLOYS_CREATIVE, EMTags.Items.CIRCUITS_CREATIVE_FORGE);
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
                .addCondition(modLoaded)
                .build(consumer, Mekmm.rl(basePath + Attribute.get(factory, AttributeAdvancedFactoryType.class).getAdvancedFactoryType().getRegistryNameComponent()));
    }
}
