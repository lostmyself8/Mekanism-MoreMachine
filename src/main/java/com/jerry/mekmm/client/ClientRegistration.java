package com.jerry.mekmm.client;

import com.jerry.mekaf.client.gui.machine.GuiAdvancedFactory;
import com.jerry.mekaf.common.registries.AdvancedFactoryBlocks;
import com.jerry.mekaf.common.registries.AdvancedFactoryContainerTypes;

import com.jerry.meklm.client.gui.machine.GuiLargeChemicalInfuser;
import com.jerry.meklm.client.gui.machine.GuiLargeElectrolyticSeparator;
import com.jerry.meklm.client.gui.machine.GuiLargeRotaryCondensentrator;
import com.jerry.meklm.client.gui.machine.GuiLargeSolarNeutronActivator;
import com.jerry.meklm.client.gui.machine.base.GuiLargeChemicalTank;
import com.jerry.meklm.client.model.bake.*;
import com.jerry.meklm.common.registries.LargeMachineBlocks;
import com.jerry.meklm.common.registries.LargeMachineContainerTypes;

import com.jerry.mekmm.Mekmm;
import com.jerry.mekmm.client.gui.GuiWirelessChargingStation;
import com.jerry.mekmm.client.gui.GuiWirelessTransmissionStation;
import com.jerry.mekmm.client.gui.GuiWirelessTransmissionStationConfig;
import com.jerry.mekmm.client.gui.machine.*;
import com.jerry.mekmm.client.render.RenderTickHandler;
import com.jerry.mekmm.client.render.tileentity.RenderWirelessTransmissionStation;
import com.jerry.mekmm.common.item.ItemConnector;
import com.jerry.mekmm.common.item.ItemConnector.ConnectorMode;
import com.jerry.mekmm.common.registries.MoreMachineBlocks;
import com.jerry.mekmm.common.registries.MoreMachineContainerTypes;
import com.jerry.mekmm.common.registries.MoreMachineItems;
import com.jerry.mekmm.common.registries.MoreMachineTileEntityTypes;

import mekanism.client.ClientRegistrationUtil;
import mekanism.client.model.baked.ExtensionBakedModel.TransformedBakedModel;
import mekanism.client.render.lib.QuadTransformation;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.common.NeoForge;

import com.jerry.meklg.client.gui.generator.GuiLargeGasGenerator;
import com.jerry.meklg.client.gui.generator.GuiLargeHeatGenerator;
import com.jerry.meklg.client.model.bake.LargeGasGeneratorBakedModel;
import com.jerry.meklg.client.model.bake.LargeHeatGeneratorBakedModel;
import com.jerry.meklg.common.registries.LargeGeneratorBlocks;
import com.jerry.meklg.common.registries.LargeGeneratorContainerTypes;

import static mekanism.client.ClientRegistration.addCustomModel;

@EventBusSubscriber(modid = Mekmm.MOD_ID, value = Dist.CLIENT)
public class ClientRegistration {

    @SubscribeEvent
    public static void init(FMLClientSetupEvent event) {
        NeoForge.EVENT_BUS.register(new ClientTickHandler());
        NeoForge.EVENT_BUS.register(new RenderTickHandler());

        addCustomModel(MoreMachineBlocks.WIRELESS_CHARGING_STATION, (orig, evt) -> new TransformedBakedModel<Void>(orig,
                QuadTransformation.translate(0, 1, 0)));
        addCustomModel(MoreMachineBlocks.WIRELESS_TRANSMISSION_STATION, (orig, evt) -> new TransformedBakedModel<Void>(orig,
                QuadTransformation.translate(0, 1, 0)));
        // LargeMachine
        addCustomModel(LargeMachineBlocks.BASIC_MAX_CHEMICAL_TANK, (orig, evt) -> new TransformedBakedModel<Void>(orig,
                QuadTransformation.translate(0, 1, 0)));
        addCustomModel(LargeMachineBlocks.ADVANCED_MAX_CHEMICAL_TANK, (orig, evt) -> new TransformedBakedModel<Void>(orig,
                QuadTransformation.translate(0, 1, 0)));
        addCustomModel(LargeMachineBlocks.ELITE_MAX_CHEMICAL_TANK, (orig, evt) -> new TransformedBakedModel<Void>(orig,
                QuadTransformation.translate(0, 1, 0)));
        addCustomModel(LargeMachineBlocks.ULTIMATE_MAX_CHEMICAL_TANK, (orig, evt) -> new TransformedBakedModel<Void>(orig,
                QuadTransformation.translate(0, 1, 0)));
        // 偏移在LargeRotaryCondensentratorBakedModel中
        addCustomModel(LargeMachineBlocks.LARGE_ROTARY_CONDENSENTRATOR, (orig, evt) -> new LargeRotaryCondensentratorBakedModel(orig));
        addCustomModel(LargeMachineBlocks.LARGE_CHEMICAL_INFUSER, (orig, evt) -> new LargeChemicalInfuserBakedModel(orig));
        addCustomModel(LargeMachineBlocks.LARGE_ELECTROLYTIC_SEPARATOR, (orig, evt) -> new LargeElectrolyticSeparatorBakedModel(orig));
        addCustomModel(LargeMachineBlocks.LARGE_SOLAR_NEUTRON_ACTIVATOR, (orig, evt) -> new LargeSNABakedModel(orig));
        if (Mekmm.hooks.mekanismgenerators.isLoaded()) {
            addCustomModel(LargeGeneratorBlocks.LARGE_HEAT_GENERATOR, (orig, evt) -> new LargeHeatGeneratorBakedModel(orig));
            addCustomModel(LargeGeneratorBlocks.LARGE_GAS_BURNING_GENERATOR, (orig, evt) -> new LargeGasGeneratorBakedModel(orig));
        }

        ClientRegistrationUtil.setPropertyOverride(MoreMachineItems.CONNECTOR, Mekmm.rl("mode"), (stack, world, entity, seed) -> {
            ConnectorMode mode = ((ItemConnector) stack.getItem()).getMode(stack);
            return switch (mode) {
                case ITEMS -> 1;
                case FLUIDS -> 2;
                case CHEMICALS -> 3;
                case ENERGY -> 4;
                case HEAT -> 5;
            };
        });
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(MoreMachineTileEntityTypes.WIRELESS_TRANSMISSION_STATION.get(), RenderWirelessTransmissionStation::new);
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
        if (Mekmm.hooks.mekanismgenerators.isLoaded()) {
            ClientRegistrationUtil.registerScreen(event, LargeGeneratorContainerTypes.LARGE_HEAT_GENERATOR, GuiLargeHeatGenerator::new);
            ClientRegistrationUtil.registerScreen(event, LargeGeneratorContainerTypes.LARGE_GAS_BURNING_GENERATOR, GuiLargeGasGenerator::new);
        }
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
