package com.jerry.mekmm.common.registries;

import com.jerry.mekaf.common.registries.AdvancedFactoryBlocks;

import com.jerry.mekmm.Mekmm;
import com.jerry.mekmm.common.MoreMachineLang;

import mekanism.common.registration.impl.CreativeTabDeferredRegister;
import mekanism.common.registration.impl.CreativeTabRegistryObject;

import com.jerry.meklm.common.registries.LargeMachineBlocks;

public class MoreMachineCreativeTabs {

    public static final CreativeTabDeferredRegister MM_CREATIVE_TABS = new CreativeTabDeferredRegister(Mekmm.MOD_ID);

    public static final CreativeTabRegistryObject MEKANISM_MORE_MACHINE = MM_CREATIVE_TABS.registerMain(MoreMachineLang.MEKANISM_MORE_MACHINE, MoreMachineItems.SCRAP, builder -> builder.displayItems((displayParameters, output) -> {
        CreativeTabDeferredRegister.addToDisplay(MoreMachineItems.MM_ITEMS, output);
        CreativeTabDeferredRegister.addToDisplay(MoreMachineBlocks.MM_BLOCKS, output);
        CreativeTabDeferredRegister.addToDisplay(AdvancedFactoryBlocks.AF_BLOCKS, output);
        CreativeTabDeferredRegister.addToDisplay(LargeMachineBlocks.LM_BLOCKS, output);
    }));
}
