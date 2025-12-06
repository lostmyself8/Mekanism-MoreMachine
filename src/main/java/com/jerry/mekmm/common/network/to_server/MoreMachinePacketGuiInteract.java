package com.jerry.mekmm.common.network.to_server;

import com.jerry.mekaf.common.tile.base.TileEntityAdvancedFactoryBase;

import com.jerry.mekmm.common.tile.TileEntityWirelessChargingStation;
import com.jerry.mekmm.common.tile.factory.TileEntityMoreMachineFactory;

import mekanism.api.functions.TriConsumer;
import mekanism.common.network.IMekanismPacket;
import mekanism.common.tile.TileEntityLogisticalSorter;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.machine.TileEntityDigitalMiner;
import mekanism.common.util.WorldUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

public class MoreMachinePacketGuiInteract implements IMekanismPacket {

    private final Type interactionType;

    private GuiInteraction interaction;
    private BlockPos tilePosition;
    private int extra;

    public MoreMachinePacketGuiInteract(GuiInteraction interaction, BlockEntity tile) {
        this(interaction, tile.getBlockPos());
    }

    public MoreMachinePacketGuiInteract(GuiInteraction interaction, BlockEntity tile, int extra) {
        this(interaction, tile.getBlockPos(), extra);
    }

    public MoreMachinePacketGuiInteract(GuiInteraction interaction, BlockPos tilePosition) {
        this(interaction, tilePosition, 0);
    }

    public MoreMachinePacketGuiInteract(GuiInteraction interaction, BlockPos tilePosition, int extra) {
        this.interactionType = Type.INT;
        this.interaction = interaction;
        this.tilePosition = tilePosition;
        this.extra = extra;
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        Player player = context.getSender();
        if (player != null) {
            TileEntityMekanism tile = WorldUtils.getTileEntity(TileEntityMekanism.class, player.level(), tilePosition);
            if (tile != null) {
                if (interactionType == Type.INT) {
                    interaction.consume(tile, player, extra);
                }
            }
        }
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeEnum(interactionType);
        if (interactionType == Type.INT) {
            buffer.writeEnum(interaction);
            buffer.writeBlockPos(tilePosition);
            buffer.writeVarInt(extra);
        }
    }

    public static MoreMachinePacketGuiInteract decode(FriendlyByteBuf buffer) {
        return switch (buffer.readEnum(Type.class)) {
            case INT -> new MoreMachinePacketGuiInteract(buffer.readEnum(GuiInteraction.class), buffer.readBlockPos(), buffer.readVarInt());
        };
    }

    public enum GuiInteraction {

        AUTO_SORT_BUTTON((tile, player, extra) -> {
            if (tile instanceof TileEntityAdvancedFactoryBase<?> factory) {
                factory.toggleSorting();
            } else if (tile instanceof TileEntityMoreMachineFactory<?> factory) {
                factory.toggleSorting();
            }
        }),
        CHARGING_EQUIPS((tile, player, extra) -> {
            if (tile instanceof TileEntityWirelessChargingStation charging) {
                charging.toggleChargeEquipment();
            }
        }),
        CHARGING_INVENTORY((tile, player, extra) -> {
            if (tile instanceof TileEntityWirelessChargingStation charging) {
                charging.toggleChargeInventory();
            }
        }),
        CHARGING_CURIOS((tile, player, extra) -> {
            if (tile instanceof TileEntityWirelessChargingStation charging) {
                charging.toggleChargeCurios();
            }
        });

        private final TriConsumer<TileEntityMekanism, Player, Integer> consumerForTile;

        GuiInteraction(TriConsumer<TileEntityMekanism, Player, Integer> consumerForTile) {
            this.consumerForTile = consumerForTile;
        }

        public void consume(TileEntityMekanism tile, Player player, int extra) {
            consumerForTile.accept(tile, player, extra);
        }
    }

    private enum Type {
        INT;
    }
}
