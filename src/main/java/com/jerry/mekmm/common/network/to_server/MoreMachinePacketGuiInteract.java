package com.jerry.mekmm.common.network.to_server;

import com.jerry.mekaf.common.tile.factory.base.TileEntityAdvancedFactoryBase;

import com.jerry.mekmm.Mekmm;
import com.jerry.mekmm.common.tile.factory.TileEntityMoreMachineFactory;
import com.jerry.mekmm.common.tile.machine.TileEntityWirelessChargingStation;

import mekanism.api.functions.TriConsumer;
import mekanism.common.network.IMekanismPacket;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.util.WorldUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntFunction;

/**
 * Used for informing the server that an action happened in a GUI
 */
public class MoreMachinePacketGuiInteract implements IMekanismPacket {

    public static final Type<MoreMachinePacketGuiInteract> TYPE = new Type<>(Mekmm.rl("gui_interact"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MoreMachinePacketGuiInteract> STREAM_CODEC = MoreMachineInteractionType.STREAM_CODEC.<RegistryFriendlyByteBuf>cast()
            .dispatch(packet -> packet.interactionType, type -> switch (type) {
                case ENTITY -> null;
                case INT -> StreamCodec.composite(
                        MoreMachineGuiInteraction.STREAM_CODEC, packet -> packet.interaction,
                        BlockPos.STREAM_CODEC, packet -> packet.tilePosition,
                        // TODO - 1.18?: Eventually we may want to try to make some form of this that can compact
                        // negatives better as well
                        ByteBufCodecs.VAR_INT, packet -> packet.extra,
                        MoreMachinePacketGuiInteract::new);
                case ITEM -> StreamCodec.composite(
                        MMGuiInteractionItem.STREAM_CODEC, packet -> packet.itemInteraction,
                        BlockPos.STREAM_CODEC, packet -> packet.tilePosition,
                        ItemStack.OPTIONAL_STREAM_CODEC, packet -> packet.extraItem,
                        MoreMachinePacketGuiInteract::new);
            });

    private final MoreMachineInteractionType interactionType;

    private MoreMachineGuiInteraction interaction;
    private MMGuiInteractionItem itemInteraction;
    // private MMPacketGuiInteract.GuiInteractionEntity entityInteraction;
    private BlockPos tilePosition;
    private ItemStack extraItem;
    private int entityID;
    private int extra;

    // public MMPacketGuiInteract(MMPacketGuiInteract.GuiInteractionEntity interaction, Entity entity) {
    // this(interaction, entity, 0);
    // }
    //
    // public MMPacketGuiInteract(MMPacketGuiInteract.GuiInteractionEntity interaction, Entity entity, int extra) {
    // this(interaction, entity.getId(), extra);
    // }
    //
    // public MMPacketGuiInteract(MMPacketGuiInteract.GuiInteractionEntity interaction, int entityID, int extra) {
    // this.interactionType = MMPacketGuiInteract.MMInteractionType.ENTITY;
    // this.entityInteraction = interaction;
    // this.entityID = entityID;
    // this.extra = extra;
    // }

    public MoreMachinePacketGuiInteract(MoreMachineGuiInteraction interaction, BlockEntity tile) {
        this(interaction, tile.getBlockPos());
    }

    public MoreMachinePacketGuiInteract(MoreMachineGuiInteraction interaction, BlockEntity tile, int extra) {
        this(interaction, tile.getBlockPos(), extra);
    }

    public MoreMachinePacketGuiInteract(MoreMachineGuiInteraction interaction, BlockPos tilePosition) {
        this(interaction, tilePosition, 0);
    }

    public MoreMachinePacketGuiInteract(MoreMachineGuiInteraction interaction, BlockPos tilePosition, int extra) {
        this.interactionType = MoreMachineInteractionType.INT;
        this.interaction = interaction;
        this.tilePosition = tilePosition;
        this.extra = extra;
    }

    public MoreMachinePacketGuiInteract(MMGuiInteractionItem interaction, BlockEntity tile, ItemStack stack) {
        this(interaction, tile.getBlockPos(), stack);
    }

    public MoreMachinePacketGuiInteract(MMGuiInteractionItem interaction, BlockPos tilePosition, ItemStack stack) {
        this.interactionType = MoreMachineInteractionType.ITEM;
        this.itemInteraction = interaction;
        this.tilePosition = tilePosition;
        this.extraItem = stack;
    }

    @Override
    public void handle(IPayloadContext context) {
        Player player = context.player();
        if (interactionType == MoreMachineInteractionType.ENTITY) {

        } else {
            TileEntityMekanism tile = WorldUtils.getTileEntity(TileEntityMekanism.class, player.level(), tilePosition);
            if (tile != null) {
                if (interactionType == MoreMachineInteractionType.INT) {
                    interaction.consume(tile, player, extra);
                } else
                    if (interactionType == MoreMachineInteractionType.ITEM) {
                        itemInteraction.consume(tile, player, extraItem);
                    }
            }
        }
    }

    @Override
    public @NotNull Type<MoreMachinePacketGuiInteract> type() {
        return TYPE;
    }

    public enum MMGuiInteractionItem {
        ;

        public static final IntFunction<MMGuiInteractionItem> BY_ID = ByIdMap.continuous(MMGuiInteractionItem::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
        public static final StreamCodec<ByteBuf, MMGuiInteractionItem> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, MMGuiInteractionItem::ordinal);

        private final TriConsumer<TileEntityMekanism, Player, ItemStack> consumerForTile;

        MMGuiInteractionItem(TriConsumer<TileEntityMekanism, Player, ItemStack> consumerForTile) {
            this.consumerForTile = consumerForTile;
        }

        public void consume(TileEntityMekanism tile, Player player, ItemStack stack) {
            consumerForTile.accept(tile, player, stack);
        }
    }

    public enum MoreMachineGuiInteraction {

        AUTO_SORT_BUTTON((tile, player, extra) -> {
            if (tile instanceof TileEntityMoreMachineFactory<?> factory) {
                factory.toggleSorting();
            }
            if (tile instanceof TileEntityAdvancedFactoryBase<?> advancedFactory) {
                advancedFactory.toggleSorting();
            }
        }),
        // Wireless Charging Station
        CHARGING_EQUIPS((tile, player, extra) -> {
            if (tile instanceof TileEntityWirelessChargingStation chargingStation) {
                chargingStation.toggleChargeEquipment();
            }
        }),
        CHARGING_INVENTORY((tile, player, extra) -> {
            if (tile instanceof TileEntityWirelessChargingStation chargingStation) {
                chargingStation.toggleChargeInventory();
            }
        }),
        CHARGING_CURIOS((tile, player, extra) -> {
            if (tile instanceof TileEntityWirelessChargingStation chargingStation) {
                chargingStation.toggleChargeCurios();
            }
        });

        public static final IntFunction<MoreMachineGuiInteraction> BY_ID = ByIdMap.continuous(MoreMachineGuiInteraction::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
        public static final StreamCodec<ByteBuf, MoreMachineGuiInteraction> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, MoreMachineGuiInteraction::ordinal);

        private final TriConsumer<TileEntityMekanism, Player, Integer> consumerForTile;

        MoreMachineGuiInteraction(TriConsumer<TileEntityMekanism, Player, Integer> consumerForTile) {
            this.consumerForTile = consumerForTile;
        }

        public void consume(TileEntityMekanism tile, Player player, int extra) {
            consumerForTile.accept(tile, player, extra);
        }
    }

    private enum MoreMachineInteractionType {

        ENTITY,
        ITEM,
        INT;

        public static final IntFunction<MoreMachineInteractionType> BY_ID = ByIdMap.continuous(MoreMachineInteractionType::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
        public static final StreamCodec<ByteBuf, MoreMachineInteractionType> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, MoreMachineInteractionType::ordinal);
    }
}
