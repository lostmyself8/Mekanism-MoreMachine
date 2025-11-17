package com.jerry.mekmm;

import com.jerry.mekaf.common.registries.AdvancedFactoryBlocks;
import com.jerry.mekaf.common.registries.AdvancedFactoryContainerTypes;
import com.jerry.mekaf.common.registries.AdvancedFactoryTileEntityTypes;

import com.jerry.mekmm.common.config.MoreMachineConfig;
import com.jerry.mekmm.common.integration.MoreMachineHooks;
import com.jerry.mekmm.common.network.MoreMachinePacketHandler;
import com.jerry.mekmm.common.registries.*;

import mekanism.common.Mekanism;
import mekanism.common.base.IModModule;
import mekanism.common.lib.Version;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Mekmm.MOD_ID)
public class Mekmm implements IModModule {

    public static final String MOD_ID = "mekmm";
    public static final String MOD_NAME = "MekanismMoreMachine";
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);
    /**
     * Mekanism More Machine Packet Pipeline
     */
    private final MoreMachinePacketHandler packetHandler;
    /**
     * Mekanism More Machine mod instance
     */
    public static Mekmm instance;
    /**
     * Mekanism More Machine hooks instance
     */
    public static final MoreMachineHooks hooks = new MoreMachineHooks();
    /**
     * Mekanism More Machine version number
     */
    public final Version versionNumber;

    public Mekmm(FMLJavaModLoadingContext context) {
        Mekanism.addModule(instance = this);
        IEventBus modEventBus = context.getModEventBus();
        ModContainer modContainer = context.getContainer();
        modEventBus.addListener(this::commonSetup);
        MoreMachineConfig.registerConfigs(modContainer);
        versionNumber = new Version(modContainer);

        MoreMachineItems.MM_ITEMS.register(modEventBus);
        MoreMachineBlocks.MM_BLOCKS.register(modEventBus);
        MoreMachineTileEntityTypes.MM_TILE_ENTITY_TYPES.register(modEventBus);
        MoreMachineContainerTypes.MM_CONTAINER_TYPES.register(modEventBus);
        MoreMachineCreativeTabs.MM_CREATIVE_TABS.register(modEventBus);
        MoreMachineRecipeSerializers.MM_RECIPE_SERIALIZERS.register(modEventBus);
        MoreMachineGas.MM_GASES.register(modEventBus);

        registerAdvancedFactory(modEventBus);

        packetHandler = new MoreMachinePacketHandler();
    }

    public static MoreMachinePacketHandler packetHandler() {
        return instance.packetHandler;
    }

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        hooks.hookCommonSetup();
    }

    private void registerAdvancedFactory(IEventBus modEventBus) {
        AdvancedFactoryBlocks.AF_BLOCKS.register(modEventBus);
        AdvancedFactoryTileEntityTypes.AF_TILE_ENTITY_TYPES.register(modEventBus);
        AdvancedFactoryContainerTypes.AF_CONTAINER_TYPES.register(modEventBus);
    }

    @Override
    public Version getVersion() {
        return versionNumber;
    }

    @Override
    public String getName() {
        return "MoreMachine";
    }

    @Override
    public void resetClient() {}
}
