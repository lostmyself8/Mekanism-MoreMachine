package com.jerry.mekmm.common.registries;

import com.jerry.mekmm.Mekmm;
import com.jerry.mekmm.common.item.ItemConnector;
import com.jerry.mekmm.common.item.ItemReflector;
import com.jerry.mekmm.common.resource.MoreMachineResource;
import com.jerry.mekmm.common.util.MoreMachineEnumUtils;

import mekanism.api.Upgrade;
import mekanism.common.item.ItemUpgrade;
import mekanism.common.registration.impl.ItemDeferredRegister;
import mekanism.common.registration.impl.ItemRegistryObject;
import mekanism.common.resource.IResource;
import mekanism.common.resource.ResourceType;
import mekanism.common.util.EnumUtils;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class MoreMachineItems {

    private MoreMachineItems() {}

    public static final ItemDeferredRegister MM_ITEMS = new ItemDeferredRegister(Mekmm.MOD_ID);
    public static final Table<ResourceType, MoreMachineResource, ItemRegistryObject<Item>> PROCESSED_RESOURCES = HashBasedTable.create();

    // 得在mods.toml设置：在mekanism之后加载，不然会导致空指针
    // public static final ItemRegistryObject<ItemUpgrade> THREAD_UPGRADE = registerUpgrade(MMUpgrade.THREAD,
    // Rarity.RARE);

    public static final ItemRegistryObject<Item> SCRAP = MM_ITEMS.register("scrap");
    public static final ItemRegistryObject<Item> SCRAP_BOX = MM_ITEMS.register("scrap_box", Rarity.UNCOMMON);
    public static final ItemRegistryObject<Item> EMPTY_CRYSTAL = MM_ITEMS.register("empty_crystal", Rarity.RARE);
    public static final ItemRegistryObject<Item> UU_MATTER = MM_ITEMS.register("uu_matter", Rarity.EPIC);
    public static final ItemRegistryObject<Item> CONNECTOR = MM_ITEMS.registerItem("connector", ItemConnector::new);
    public static final ItemRegistryObject<Item> ADVANCED_ELECTROLYSIS_CORE = MM_ITEMS.register("advanced_electrolysis_core");
    public static final ItemRegistryObject<Item> REFLECTOR = MM_ITEMS.registerItem("reflector", ItemReflector::new);

    static {
        for (ResourceType type : EnumUtils.RESOURCE_TYPES) {
            for (MoreMachineResource resource : MoreMachineEnumUtils.MM_RESOURCES) {
                if (resource.has(type)) {
                    PROCESSED_RESOURCES.put(type, resource, registerResource(type, resource));
                }
            }
        }
    }

    private static ItemRegistryObject<ItemUpgrade> registerUpgrade(Upgrade type, Rarity rarity) {
        return MM_ITEMS.registerItem("upgrade_" + type.getSerializedName(), properties -> new ItemUpgrade(type, properties.rarity(rarity)));
    }

    private static ItemRegistryObject<Item> registerResource(ResourceType type, IResource resource) {
        return MM_ITEMS.register(type.getRegistryPrefix() + "_" + resource.getRegistrySuffix());
    }
}
