package com.jerry.mekmm.common.registries;

import com.jerry.mekmm.Mekmm;
import com.jerry.mekmm.common.item.ItemConnector.ConnectorMode;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.common.registration.MekanismDeferredHolder;
import mekanism.common.registration.impl.DataComponentDeferredRegister;

import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;

import com.mojang.serialization.Codec;

@NothingNullByDefault
public class MoreMachineDataComponents {

    private MoreMachineDataComponents() {}

    public static final DataComponentDeferredRegister MM_DATA_COMPONENTS = new DataComponentDeferredRegister(Mekmm.MOD_ID);

    public static final MekanismDeferredHolder<DataComponentType<?>, DataComponentType<ConnectorMode>> CONNECTOR_MODE = MM_DATA_COMPONENTS.simple("connector_mode",
            builder -> builder.persistent(ConnectorMode.CODEC)
                    .networkSynchronized(ConnectorMode.STREAM_CODEC));

    public static final MekanismDeferredHolder<DataComponentType<?>, DataComponentType<GlobalPos>> CONNECT_FROM = MM_DATA_COMPONENTS.simple("connect_from",
            builder -> builder.persistent(GlobalPos.CODEC)
                    .networkSynchronized(GlobalPos.STREAM_CODEC));

    public static final MekanismDeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> CHARGE_EQUIPMENT = MM_DATA_COMPONENTS.registerBoolean("charge_equipment");
    public static final MekanismDeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> CHARGE_INVENTORY = MM_DATA_COMPONENTS.registerBoolean("charge_inventory");
    public static final MekanismDeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> CHARGE_CURIOS = MM_DATA_COMPONENTS.registerBoolean("charge_curios");

    public static final MekanismDeferredHolder<DataComponentType<?>, DataComponentType<Long>> ENERGY_RATE = MM_DATA_COMPONENTS.registerNonNegativeLong("energy_rate");
    public static final MekanismDeferredHolder<DataComponentType<?>, DataComponentType<Integer>> FLUIDS_RATE = MM_DATA_COMPONENTS.registerInt("fluids_rate");
    public static final MekanismDeferredHolder<DataComponentType<?>, DataComponentType<Long>> CHEMICALS_RATE = MM_DATA_COMPONENTS.registerNonNegativeLong("chemicals_rate");
    public static final MekanismDeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ITEMS_RATE = MM_DATA_COMPONENTS.registerInt("items_rate");
    public static final MekanismDeferredHolder<DataComponentType<?>, DataComponentType<Double>> HEAT_RATE = MM_DATA_COMPONENTS.simple("heat_rate",
            builder -> builder.persistent(Codec.DOUBLE)
                    .networkSynchronized(ByteBufCodecs.DOUBLE));
}
