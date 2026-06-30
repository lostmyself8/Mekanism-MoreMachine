package com.jerry.mekmm.common.resource;

import mekanism.common.resource.IResource;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

public enum MoreMachineBlockResourceInfo implements IResource {

    SILVER("silver", 5.0F, 7.0F, MapColor.COLOR_LIGHT_GRAY),
    RAW_SILVER("raw_silver", 5.0F, 7.0F, MapColor.COLOR_LIGHT_GRAY, NoteBlockInstrument.BASEDRUM);

    private final String registrySuffix;
    private final MapColor mapColor;
    private final PushReaction pushReaction;
    @Getter
    private final boolean portalFrame;
    private final boolean burnsInFire;
    private final NoteBlockInstrument instrument;
    private final float hardness;
    private final float resistance;

    MoreMachineBlockResourceInfo(String registrySuffix, float hardness, float resistance, MapColor mapColor) {
        this(registrySuffix, hardness, resistance, mapColor, null);
    }

    MoreMachineBlockResourceInfo(String registrySuffix, float hardness, float resistance, MapColor mapColor, @Nullable NoteBlockInstrument instrument) {
        this(registrySuffix, hardness, resistance, mapColor, instrument, true, false, PushReaction.NORMAL);
    }

    MoreMachineBlockResourceInfo(String registrySuffix, float hardness, float resistance, MapColor mapColor, @Nullable NoteBlockInstrument instrument,
                                 boolean burnsInFire, boolean portalFrame, PushReaction pushReaction) {
        this.registrySuffix = registrySuffix;
        this.mapColor = mapColor;
        this.pushReaction = pushReaction;
        this.portalFrame = portalFrame;
        this.burnsInFire = burnsInFire;
        this.instrument = instrument;
        this.hardness = hardness;
        this.resistance = resistance;
    }

    @Override
    public String getRegistrySuffix() {
        return registrySuffix;
    }

    public boolean burnsInFire() {
        return burnsInFire;
    }

    public BlockBehaviour.Properties modifyProperties(BlockBehaviour.Properties properties) {
        if (instrument != null) {
            properties.instrument(instrument);
        }
        return properties.mapColor(mapColor).strength(hardness, resistance).pushReaction(pushReaction);
    }
}
