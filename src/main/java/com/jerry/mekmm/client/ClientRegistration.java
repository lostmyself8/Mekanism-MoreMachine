package com.jerry.mekmm.client;

import com.jerry.mekaf.client.gui.machine.GuiAdvancedFactory;
import com.jerry.mekaf.common.registries.AdvancedFactoryBlocks;
import com.jerry.mekaf.common.registries.AdvancedFactoryContainerTypes;

import com.jerry.meklm.client.gui.machine.*;
import com.jerry.meklm.client.gui.machine.base.GuiLargeChemicalTank;
import com.jerry.meklm.client.model.LargeMachineModelCache;
import com.jerry.meklm.client.render.tileentity.RenderLargeAntiprotonicNucleosynthesizer;
import com.jerry.meklm.client.render.tileentity.RenderLargePigmentMixer;
import com.jerry.meklm.common.registries.LargeMachineBlocks;
import com.jerry.meklm.common.registries.LargeMachineContainerTypes;
import com.jerry.meklm.common.registries.LargeMachineTileEntityTypes;

import com.jerry.mekmm.Mekmm;
import com.jerry.mekmm.client.gui.GuiWirelessChargingStation;
import com.jerry.mekmm.client.gui.GuiWirelessTransmissionStation;
import com.jerry.mekmm.client.gui.GuiWirelessTransmissionStationConfig;
import com.jerry.mekmm.client.gui.machine.*;
import com.jerry.mekmm.client.render.RenderTickHandler;
import com.jerry.mekmm.client.render.tileentity.RenderWirelessTransmissionStation;
import com.jerry.mekmm.common.registries.MoreMachineBlocks;
import com.jerry.mekmm.common.registries.MoreMachineContainerTypes;
import com.jerry.mekmm.common.registries.MoreMachineTileEntityTypes;

import mekanism.client.ClientRegistrationUtil;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent.BakingCompleted;
import net.neoforged.neoforge.client.event.ModelEvent.RegisterStandalone;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.common.NeoForge;

import com.jerry.meklg.client.gui.generator.GuiLargeGasGenerator;
import com.jerry.meklg.client.gui.generator.GuiLargeHeatGenerator;
import com.jerry.meklg.client.gui.generator.GuiLargeWindGenerator;
import com.jerry.meklg.common.registries.LargeGeneratorBlocks;
import com.jerry.meklg.common.registries.LargeGeneratorContainerTypes;

@EventBusSubscriber(modid = Mekmm.MOD_ID, value = Dist.CLIENT)
public class ClientRegistration {

    @SubscribeEvent
    public static void init(FMLClientSetupEvent event) {
        NeoForge.EVENT_BUS.register(new ClientTickHandler());
        NeoForge.EVENT_BUS.register(new RenderTickHandler());
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(MoreMachineTileEntityTypes.WIRELESS_TRANSMISSION_STATION.get(), RenderWirelessTransmissionStation::new);
        event.registerBlockEntityRenderer(LargeMachineTileEntityTypes.LARGE_ANTIPROTONIC_NUCLEOSYNTHESIZER.get(), RenderLargeAntiprotonicNucleosynthesizer::new);
        event.registerBlockEntityRenderer(LargeMachineTileEntityTypes.LARGE_PIGMENT_MIXER.get(), RenderLargePigmentMixer::new);
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        ClientRegistrationUtil.registerScreen(event, MoreMachineContainerTypes.RECYCLER, GuiRecycler::new);
        ClientRegistrationUtil.registerScreen(event, MoreMachineContainerTypes.PLANTING_STATION, GuiPlantingStation::new);
        ClientRegistrationUtil.registerScreen(event, MoreMachineContainerTypes.CNC_STAMPER, GuiStamper::new);
        ClientRegistrationUtil.registerElectricScreen(event, MoreMachineContainerTypes.CNC_LATHE);
        ClientRegistrationUtil.registerElectricScreen(event, MoreMachineContainerTypes.CNC_ROLLING_MILL);
        ClientRegistrationUtil.registerScreen(event, MoreMachineContainerTypes.REPLICATOR, GuiReplicator::new);
        ClientRegistrationUtil.registerScreen(event, MoreMachineContainerTypes.FLUID_REPLICATOR, GuiFluidReplicator::new);
        ClientRegistrationUtil.registerScreen(event, MoreMachineContainerTypes.CHEMIcAL_REPLICATOR, GuiChemicalReplicator::new);
        ClientRegistrationUtil.registerScreen(event, MoreMachineContainerTypes.AMBIENT_GAS_COLLECTOR, GuiAmbientGasCollector::new);
        ClientRegistrationUtil.registerScreen(event, MoreMachineContainerTypes.WIRELESS_CHARGING_STATION, GuiWirelessChargingStation::new);
        ClientRegistrationUtil.registerScreen(event, MoreMachineContainerTypes.WIRELESS_TRANSMISSION_STATION, GuiWirelessTransmissionStation::new);

        ClientRegistrationUtil.registerScreen(event, MoreMachineContainerTypes.WIRELESS_TRANSMISSION_STATION_CONFIG, GuiWirelessTransmissionStationConfig::new);
        ClientRegistrationUtil.registerScreen(event, MoreMachineContainerTypes.MM_FACTORY, GuiMoreMachineFactory::new);

        // Advanced Factory
        ClientRegistrationUtil.registerScreen(event, AdvancedFactoryContainerTypes.ADVANCED_FACTORY, GuiAdvancedFactory::new);

        // Large Machine
        ClientRegistrationUtil.registerScreen(event, LargeMachineContainerTypes.CHEMICAL_TANK, GuiLargeChemicalTank::new);
        ClientRegistrationUtil.registerScreen(event, LargeMachineContainerTypes.LARGE_ROTARY_CONDENSENTRATOR, GuiLargeRotaryCondensentrator::new);
        ClientRegistrationUtil.registerScreen(event, LargeMachineContainerTypes.LARGE_CHEMICAL_INFUSER, GuiLargeChemicalInfuser::new);
        ClientRegistrationUtil.registerScreen(event, LargeMachineContainerTypes.LARGE_ELECTROLYTIC_SEPARATOR, GuiLargeElectrolyticSeparator::new);
        ClientRegistrationUtil.registerScreen(event, LargeMachineContainerTypes.LARGE_SOLAR_NEUTRON_ACTIVATOR, GuiLargeSolarNeutronActivator::new);
        ClientRegistrationUtil.registerScreen(event, LargeMachineContainerTypes.LARGE_ANTIPROTONIC_NUCLEOSYNTHESIZER, GuiLargeAntiprotonicNucleosynthesizer::new);
        ClientRegistrationUtil.registerScreen(event, LargeMachineContainerTypes.LARGE_PIGMENT_MIXER, GuiLargePigmentMixer::new);
        if (Mekmm.hooks.mekanismgenerators.isLoaded()) {
            ClientRegistrationUtil.registerScreen(event, LargeGeneratorContainerTypes.LARGE_HEAT_GENERATOR, GuiLargeHeatGenerator::new);
            ClientRegistrationUtil.registerScreen(event, LargeGeneratorContainerTypes.LARGE_GAS_BURNING_GENERATOR, GuiLargeGasGenerator::new);
            ClientRegistrationUtil.registerScreen(event, LargeGeneratorContainerTypes.LARGE_WIND_GENERATOR, GuiLargeWindGenerator::new);
        }
    }

    @SubscribeEvent
    public static void registerAdditionalModels(RegisterStandalone event) {
        LargeMachineModelCache.INSTANCE.setup(event);
    }

    @SubscribeEvent
    public static void onModelBake(BakingCompleted event) {
        LargeMachineModelCache.INSTANCE.onBake(event);
    }

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        ClientRegistrationUtil.registerBlockExtensions(event, MoreMachineBlocks.MM_BLOCKS);
        ClientRegistrationUtil.registerBlockExtensions(event, AdvancedFactoryBlocks.AF_BLOCKS);
        ClientRegistrationUtil.registerBlockExtensions(event, LargeMachineBlocks.LM_BLOCKS);
        if (Mekmm.hooks.mekanismgenerators.isLoaded()) {
            ClientRegistrationUtil.registerBlockExtensions(event, LargeGeneratorBlocks.LG_BLOCKS);
        }
    }
}
