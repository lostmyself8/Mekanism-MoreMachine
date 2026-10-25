package com.jerry.mekmm.common.registries;

import com.jerry.mekaf.common.registries.AdvancedFactoryBlocks;

import com.jerry.meklm.common.registries.LargeMachineBlocks;

import com.jerry.mekmm.Mekmm;
import com.jerry.mekmm.common.MoreMachineLang;

import mekanism.common.registration.MekanismDeferredHolder;
import mekanism.common.registration.impl.CreativeTabDeferredRegister;

import net.minecraft.world.item.CreativeModeTab;

import com.jerry.meklg.common.registries.LargeGeneratorBlocks;

public class MoreMachineCreativeTabs {

    public static final CreativeTabDeferredRegister MM_CREATIVE_TABS = new CreativeTabDeferredRegister(Mekmm.MOD_ID);

    public static final MekanismDeferredHolder<CreativeModeTab, CreativeModeTab> MEKANISM_MORE_MACHINE = MM_CREATIVE_TABS.registerMain(MoreMachineLang.MEKANISM_MORE_MACHINE,
            MoreMachineItems.SCRAP, builder -> builder.displayItems((displayParameters, output) -> {
                CreativeTabDeferredRegister.addToDisplay(MoreMachineItems.MM_ITEMS, displayParameters, output);
                CreativeTabDeferredRegister.addToDisplay(MoreMachineBlocks.MM_BLOCKS, displayParameters, output);
                CreativeTabDeferredRegister.addToDisplay(AdvancedFactoryBlocks.AF_BLOCKS, displayParameters, output);
                CreativeTabDeferredRegister.addToDisplay(LargeMachineBlocks.LM_BLOCKS, displayParameters, output);
                if (Mekmm.hooks.mekanismgenerators.isLoaded()) {
                    CreativeTabDeferredRegister.addToDisplay(displayParameters, output, LargeGeneratorBlocks.LG_BLOCKS.getSecondaryEntries().stream().filter(holder -> !LargeGeneratorBlocks.LARGE_WIND_GENERATOR_PROXY.secondaryKeyMatches(holder)).toList());
                }
            }));
}
