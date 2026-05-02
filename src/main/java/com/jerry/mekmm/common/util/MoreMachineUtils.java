package com.jerry.mekmm.common.util;

import com.jerry.mekaf.common.block.attribute.AttributeAdvancedFactoryType;

import com.jerry.mekmm.Mekmm;
import com.jerry.mekmm.common.block.attribute.MoreMachineAttributeFactoryType;

import mekanism.api.text.EnumColor;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.tier.FactoryTier;
import mekanism.common.util.EnumUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import java.util.Arrays;
import java.util.function.Function;

public class MoreMachineUtils {

    // 维度居然没有翻译文件。。。
    public static Component formatDim(Identifier id) {
        return Component.translatableWithFallback(id.toLanguageKey("dimension"), id.toString());
    }

    public static String formatPos(BlockPos pos) {
        return "[" + pos.getX() + "," + pos.getY() + "," + pos.getZ() + "]";
    }

    public static EnumColor getColorByTransmitterType(TransmissionType type) {
        return switch (type) {
            case ENERGY -> EnumColor.BRIGHT_GREEN;
            case FLUID -> EnumColor.AQUA;
            case CHEMICAL -> EnumColor.YELLOW;
            case ITEM -> EnumColor.GRAY;
            case HEAT -> EnumColor.ORANGE;
        };
    }

    public static Codec<Long> longRange(final long minInclusive, final long maxInclusive) {
        final Function<Long, DataResult<Long>> checker = Codec.checkRange(minInclusive, maxInclusive);
        return Codec.LONG.flatXmap(checker, checker);
    }

    /**
     * 为了适配EMek，由于它在mixin没有采用重载序列化与反序列化的操作，导致Mek的FactoryTier识别不到新加入的Tier
     * 因此需要在有Emek时将两个数组手动合并为一个。
     *
     * @return FactoryTier[]
     */
    public static FactoryTier[] getFactoryTier() {
        // Compatible wit Emek
        if (Mekmm.hooks.evolvedMekanism.isLoaded()) {
            FactoryTier[] mergedTiers;
            mergedTiers = Arrays.copyOf(EnumUtils.FACTORY_TIERS, EnumUtils.FACTORY_TIERS.length + MoreMachineEnumUtils.EM_TIERS.length);
            System.arraycopy(MoreMachineEnumUtils.EM_TIERS, 0, mergedTiers, EnumUtils.FACTORY_TIERS.length, MoreMachineEnumUtils.EM_TIERS.length);
            return mergedTiers;
        } else {
            return EnumUtils.FACTORY_TIERS;
        }
    }

    // 从MekanismUtils的isSameTypeFactory单拎出来的
    public static boolean isSameMMTypeFactory(Holder<Block> block, Block factoryBlockType) {
        MoreMachineAttributeFactoryType attribute = Attribute.get(block, MoreMachineAttributeFactoryType.class);
        if (attribute != null) {
            MoreMachineAttributeFactoryType otherType = Attribute.get(factoryBlockType, MoreMachineAttributeFactoryType.class);
            return otherType != null && attribute.getMoreMachineFactoryType() == otherType.getMoreMachineFactoryType();
        }
        return false;
    }

    // 从MekanismUtils的isSameTypeFactory单拎出来的
    public static boolean isSameAFTypeFactory(Holder<Block> block, Block factoryBlockType) {
        AttributeAdvancedFactoryType attribute = Attribute.get(block, AttributeAdvancedFactoryType.class);
        if (attribute != null) {
            AttributeAdvancedFactoryType otherType = Attribute.get(factoryBlockType, AttributeAdvancedFactoryType.class);
            return otherType != null && attribute.getAdvancedFactoryType() == otherType.getAdvancedFactoryType();
        }
        return false;
    }
}
