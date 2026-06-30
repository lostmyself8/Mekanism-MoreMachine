package com.jerry.mekmm.common.tags;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class MoreMachineTags {

    private MoreMachineTags() {}

    public static class Blocks {

        private Blocks() {}

        public static final TagKey<Block> SILVER_ORES = commonTag("ores/silver");
        public static final TagKey<Block> SILVER_STORAGE_BLOCKS = commonTag("storage_blocks/silver");
        public static final TagKey<Block> RAW_SILVER_STORAGE_BLOCKS = commonTag("storage_blocks/raw_silver");

        private static TagKey<Block> commonTag(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", name));
        }
    }

    public static class Items {

        private Items() {}

        public static final TagKey<Item> SILVER_ORES = commonTag("ores/silver");

        private static TagKey<Item> commonTag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", name));
        }
    }
}
