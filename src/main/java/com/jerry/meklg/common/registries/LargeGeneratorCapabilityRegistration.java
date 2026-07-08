package com.jerry.meklg.common.registries;

import mekanism.api.security.IBlockSecurityUtils;
import mekanism.common.capabilities.Capabilities;

import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import com.jerry.meklg.common.block.BlockLargeWindGeneratorProxy;

public class LargeGeneratorCapabilityRegistration {

    public static void register(RegisterCapabilitiesEvent event) {
        Block proxyBlock = LargeGeneratorBlocks.LARGE_WIND_GENERATOR_PROXY.value();
        BlockLargeWindGeneratorProxy.alwaysProxyCapability(event, Capabilities.CONFIG_CARD, proxyBlock);
        BlockLargeWindGeneratorProxy.alwaysProxyCapability(event, IBlockSecurityUtils.INSTANCE.ownerCapability(), proxyBlock);
        BlockLargeWindGeneratorProxy.alwaysProxyCapability(event, IBlockSecurityUtils.INSTANCE.securityCapability(), proxyBlock);
        BlockLargeWindGeneratorProxy.proxyCapability(event, Capabilities.ENERGY.block(), proxyBlock);
        BlockLargeWindGeneratorProxy.proxyCapability(event, Capabilities.ITEM.block(), proxyBlock);
    }

    private LargeGeneratorCapabilityRegistration() {}
}
