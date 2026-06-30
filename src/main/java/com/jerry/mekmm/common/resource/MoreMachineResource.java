package com.jerry.mekmm.common.resource;

import com.jerry.mekmm.common.tags.MoreMachineTags.Items;

import mekanism.common.resource.IResource;
import mekanism.common.resource.ResourceType;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public enum MoreMachineResource implements IResource {

    SILVER("silver", 0xFFD9D9E6, () -> Items.SILVER_ORES, MoreMachineBlockResourceInfo.SILVER, MoreMachineBlockResourceInfo.RAW_SILVER);

    private final String name;
    @Getter
    private final int tint;
    private final Supplier<TagKey<Item>> oreTag;
    private final MoreMachineBlockResourceInfo resourceBlockInfo;
    private final MoreMachineBlockResourceInfo rawResourceBlockInfo;

    MoreMachineResource(String name, int tint, Supplier<TagKey<Item>> oreTag, MoreMachineBlockResourceInfo resourceBlockInfo,
                        MoreMachineBlockResourceInfo rawResourceBlockInfo) {
        this.name = name;
        this.tint = tint;
        this.oreTag = oreTag;
        this.resourceBlockInfo = resourceBlockInfo;
        this.rawResourceBlockInfo = rawResourceBlockInfo;
    }

    @Override
    public String getRegistrySuffix() {
        return name;
    }

    public TagKey<Item> getOreTag() {
        return oreTag.get();
    }

    public boolean has(ResourceType type) {
        return true;
    }

    @Nullable
    public MoreMachineBlockResourceInfo getResourceBlockInfo() {
        return resourceBlockInfo;
    }

    @Nullable
    public MoreMachineBlockResourceInfo getRawResourceBlockInfo() {
        return rawResourceBlockInfo;
    }
}
