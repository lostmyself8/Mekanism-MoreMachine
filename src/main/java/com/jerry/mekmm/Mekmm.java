package com.jerry.mekmm;

import com.jerry.mekaf.common.registries.AdvancedFactoryBlocks;
import com.jerry.mekaf.common.registries.AdvancedFactoryContainerTypes;
import com.jerry.mekaf.common.registries.AdvancedFactoryTileEntityTypes;

import com.jerry.meklm.common.base.holiday.HolidayManager;
import com.jerry.meklm.common.registries.LargeMachineBlocks;
import com.jerry.meklm.common.registries.LargeMachineContainerTypes;
import com.jerry.meklm.common.registries.LargeMachineTileEntityTypes;

import com.jerry.mekmm.common.MoreMachinePlayerTracker;
import com.jerry.mekmm.common.config.MoreMachineConfig;
import com.jerry.mekmm.common.integration.MoreMachineHooks;
import com.jerry.mekmm.common.network.MoreMachinePacketHandler;
import com.jerry.mekmm.common.registries.*;

import mekanism.common.Mekanism;
import mekanism.common.base.IModModule;
import mekanism.common.lib.Version;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

import com.jerry.meklg.common.registries.LargeGeneratorBlocks;
import com.jerry.meklg.common.registries.LargeGeneratorContainerTypes;
import com.jerry.meklg.common.registries.LargeGeneratorTileEntityTypes;
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

    public Mekmm(IEventBus modEventBus, ModContainer modContainer) {
        Mekanism.addModule(instance = this);
        // Set our version number to match the neoforge.mods.toml file, which matches the one in our build.gradle
        versionNumber = new Version(modContainer);

        // MoreMachine相关的注册
        MoreMachineConfig.registerConfigs(modContainer);
        MoreMachineItems.MM_ITEMS.register(modEventBus);
        MoreMachineBlocks.MM_BLOCKS.register(modEventBus);
        MoreMachineTileEntityTypes.MM_TILE_ENTITY_TYPES.register(modEventBus);
        MoreMachineContainerTypes.MM_CONTAINER_TYPES.register(modEventBus);
        MoreMachineRecipeSerializersInternal.MM_RECIPE_SERIALIZERS.register(modEventBus);
        MoreMachineChemicals.MM_CHEMICALS.register(modEventBus);
        MoreMachineCreativeTabs.MM_CREATIVE_TABS.register(modEventBus);
        MoreMachineDataComponents.MM_DATA_COMPONENTS.register(modEventBus);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(MoreMachineConfig::onConfigLoad);
        // LargeMachine相关的注册
        // LMConfig.registerConfigs(modContainer);
        registerAdvancedFactory(modEventBus);
        registerLargeMachine(modEventBus);
        registerLargeGenerator(modEventBus);

        packetHandler = new MoreMachinePacketHandler(modEventBus, versionNumber);
    }

    public static MoreMachinePacketHandler packetHandler() {
        return instance.packetHandler;
    }

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    private void registerAdvancedFactory(IEventBus modEventBus) {
        AdvancedFactoryBlocks.AF_BLOCKS.register(modEventBus);
        AdvancedFactoryTileEntityTypes.AF_TILE_ENTITY_TYPES.register(modEventBus);
        AdvancedFactoryContainerTypes.AF_CONTAINER_TYPES.register(modEventBus);
    }

    private void registerLargeMachine(IEventBus modEventBus) {
        LargeMachineBlocks.LM_BLOCKS.register(modEventBus);
        LargeMachineTileEntityTypes.LM_TILE_ENTITY_TYPES.register(modEventBus);
        LargeMachineContainerTypes.LM_CONTAINER_TYPES.register(modEventBus);
    }

    private void registerLargeGenerator(IEventBus modEventBus) {
        if (Mekmm.hooks.mekanismgenerators.isLoaded()) {
            LargeGeneratorBlocks.LG_BLOCKS.register(modEventBus);
            LargeGeneratorTileEntityTypes.LG_TILE_ENTITY_TYPES.register(modEventBus);
            LargeGeneratorContainerTypes.LG_CONTAINER_TYPES.register(modEventBus);
        }
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Initialization notification
        LOGGER.info("Version {} initializing...", versionNumber);
        HolidayManager.init();
        // Register player tracker
        NeoForge.EVENT_BUS.register(new MoreMachinePlayerTracker());
    }

    @Override
    public Version getVersion() {
        return versionNumber;
    }

    @Override
    public String getName() {
        return "MoreMachine";
    }
}
