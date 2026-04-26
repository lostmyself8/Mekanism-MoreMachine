package com.jerry.mekmm.common.item;

import com.jerry.mekmm.Mekmm;
import com.jerry.mekmm.api.MoreMachineItemAbilities;
import com.jerry.mekmm.common.MoreMachineLang;
import com.jerry.mekmm.common.item.ItemConnector.ConnectorMode;
import com.jerry.mekmm.common.registries.MoreMachineDataComponents;
import com.jerry.mekmm.common.tile.interfaces.ITileConnect;
import com.jerry.mekmm.common.tile.prefab.TileEntityConnectableMachine;
import com.jerry.mekmm.common.util.MoreMachineUtils;

import mekanism.api.IIncrementalEnum;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.radial.IRadialDataHelper;
import mekanism.api.radial.RadialData;
import mekanism.api.radial.mode.IRadialMode;
import mekanism.api.text.EnumColor;
import mekanism.api.text.IHasTextComponent.IHasEnumNameTextComponent;
import mekanism.api.text.ILangEntry;
import mekanism.api.text.TextComponentUtil;
import mekanism.common.MekanismLang;
import mekanism.common.item.interfaces.IItemHUDProvider;
import mekanism.common.lib.radial.IRadialModeItem;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.tile.TileEntityBoundingBlock;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.WorldUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.util.Lazy;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.IntFunction;

public class ItemConnector extends Item implements IRadialModeItem<ConnectorMode>, IItemHUDProvider {

    private static final Lazy<RadialData<ConnectorMode>> LAZY_RADIAL_DATA = Lazy.of(() -> IRadialDataHelper.INSTANCE.dataForEnum(Mekmm.rl("connector_mode"), ConnectorMode.class));

    public ItemConnector(Properties properties) {
        super(properties.rarity(Rarity.UNCOMMON).stacksTo(1)
                .component(MoreMachineDataComponents.CONNECTOR_MODE, ConnectorMode.ENERGY));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(MekanismLang.STATE.translateColored(EnumColor.PINK, getMode(stack)));
        // 显示绑定的源位置
        GlobalPos globalPos = getStoredPosition(stack);
        if (globalPos != null) {
            tooltip.add(MoreMachineLang.CONNECTOR_DETAIL.translate(EnumColor.ORANGE, MoreMachineUtils.formatDim(globalPos.dimension().location()), EnumColor.ORANGE, MoreMachineUtils.formatPos(globalPos.pos())));
        }
    }

    @NotNull
    @Override
    public Component getName(@NotNull ItemStack stack) {
        return TextComponentUtil.build(EnumColor.AQUA, super.getName(stack));
    }

    @Override
    public boolean canPerformAction(@NotNull ItemStack stack, @NotNull ItemAbility action) {
        if (action == MoreMachineItemAbilities.CONNECT_CHEMICALS) {
            return getMode(stack) == ConnectorMode.CHEMICALS;
        } else if (action == MoreMachineItemAbilities.CONNECT_ENERGY) {
            return getMode(stack) == ConnectorMode.ENERGY;
        } else if (action == MoreMachineItemAbilities.CONNECT_FLUIDS) {
            return getMode(stack) == ConnectorMode.FLUIDS;
        } else if (action == MoreMachineItemAbilities.CONNECT_HEAT) {
            return getMode(stack) == ConnectorMode.HEAT;
        } else if (action == MoreMachineItemAbilities.CONNECT_ITEMS) {
            return getMode(stack) == ConnectorMode.ITEMS;
        }
        return super.canPerformAction(stack, action);
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        Player player = context.getPlayer();
        Level world = context.getLevel();
        if (player == null) {
            return InteractionResult.PASS;
        }
        BlockPos pos = context.getClickedPos();
        Direction side = context.getClickedFace();
        ItemStack stack = context.getItemInHand();
        Block block = world.getBlockState(pos).getBlock();
        BlockEntity tile = WorldUtils.getTileEntity(world, pos);
        if (player.isShiftKeyDown()) {
            if (!(tile instanceof ITileConnect)) {
                return InteractionResult.CONSUME;
            }
            if (!world.isClientSide) {
                stack.set(MoreMachineDataComponents.CONNECT_FROM, GlobalPos.of(world.dimension(), pos));
                player.displayClientMessage(MoreMachineLang.CONNECTOR_FROM.translate(EnumColor.INDIGO, TextComponentUtil.translate(block.getDescriptionId())), true);
            }
        } else {
            // 禁用交互效果在MoreMachinePlayerTracker
            GlobalPos globalPos = getStoredPosition(stack);
            if (globalPos == null) {
                return InteractionResult.CONSUME;
            }
            // 如果跨纬度
            if (world.dimension() != globalPos.dimension()) {
                // Ciallo～(∠・ω< )⌒★
                player.displayClientMessage(MoreMachineLang.CONNECTOR_ACROSS_DIMENSION.translate(EnumColor.DARK_RED), true);
                return InteractionResult.CONSUME;
            }
            // 如果绑定到自己
            if (pos.equals(globalPos.pos())) {
                player.displayClientMessage(MoreMachineLang.CONNECTOR_SELF.translate(EnumColor.DARK_RED), true);
                return InteractionResult.CONSUME;
            }
            // 防止出现端口在绑定方块上的情况。这个情况应当视为连接自身
            if (tile instanceof TileEntityBoundingBlock boundingBlock) {
                BlockPos mainPos = boundingBlock.getMainPos();
                if (mainPos.equals(globalPos.pos())) {
                    player.displayClientMessage(MoreMachineLang.CONNECTOR_SELF.translate(EnumColor.DARK_RED), true);
                    return InteractionResult.CONSUME;
                }
            }
            // 获取保存位置的方块实体
            TileEntityConnectableMachine linkTile = WorldUtils.getTileEntity(TileEntityConnectableMachine.class, world, globalPos.pos(), true);
            if (linkTile != null) {
                Component translateName = TextComponentUtil.translate(block.getDescriptionId());
                switch (linkTile.connectOrCut(pos, side, getMode(stack).transmissionType)) {
                    case CONNECT -> {
                        player.displayClientMessage(MoreMachineLang.CONNECTOR_TO.translate(EnumColor.INDIGO, translateName, EnumColor.INDIGO, side), true);
                        return InteractionResult.SUCCESS;
                    }
                    case DISCONNECT -> {
                        player.displayClientMessage(MoreMachineLang.CONNECTOR_DISCONNECT.translate(EnumColor.INDIGO, translateName, EnumColor.INDIGO, side), true);
                        return InteractionResult.SUCCESS;
                    }
                    case CONNECT_FAIL -> {
                        // 连接到没有能力或者不能连接的方块上时发出的消息
                        player.displayClientMessage(MoreMachineLang.CONNECTOR_FAIL.translate(EnumColor.DARK_RED, translateName, EnumColor.DARK_RED, side), true);
                        return InteractionResult.FAIL;
                    }
                }
                return InteractionResult.PASS;
            } else {
                // 绑定后中心方块被拆除
                player.displayClientMessage(MoreMachineLang.CONNECTOR_LOSE.translate(EnumColor.DARK_RED, MoreMachineUtils.formatPos(globalPos.pos())), true);
                return InteractionResult.FAIL;
            }
        }
        return InteractionResult.sidedSuccess(world.isClientSide);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand usedHand) {
        if (player.isShiftKeyDown()) {
            ItemStack connector = player.getItemInHand(usedHand);
            if (!level.isClientSide) {
                if (getStoredPosition(connector) != null) {
                    connector.remove(MoreMachineDataComponents.CONNECT_FROM);
                    player.displayClientMessage(MoreMachineLang.CONNECTOR_CLEARED.translate(), true);
                }
            }
            return InteractionResultHolder.sidedSuccess(connector, level.isClientSide);
        }
        return super.use(level, player, usedHand);
    }

    private GlobalPos getStoredPosition(ItemStack stack) {
        return stack.get(MoreMachineDataComponents.CONNECT_FROM);
    }

    @Override
    public @NotNull RadialData<ConnectorMode> getRadialData(ItemStack stack) {
        return LAZY_RADIAL_DATA.get();
    }

    @Override
    public DataComponentType<ConnectorMode> getModeDataType() {
        return MoreMachineDataComponents.CONNECTOR_MODE.get();
    }

    @Override
    public ConnectorMode getDefaultMode() {
        return ConnectorMode.ENERGY;
    }

    @Override
    public void addHUDStrings(List<Component> list, Player player, ItemStack stack, EquipmentSlot slotType) {
        list.add(MekanismLang.MODE.translateColored(EnumColor.PINK, getMode(stack)));
    }

    @Override
    public void changeMode(@NotNull Player player, @NotNull ItemStack stack, int shift, DisplayChange displayChange) {
        ConnectorMode mode = getMode(stack);
        ConnectorMode newMode = mode.adjust(shift);
        if (mode != newMode) {
            setMode(stack, player, newMode);
            displayChange.sendMessage(player, newMode, MekanismLang.CONFIGURE_STATE::translate);
        }
    }

    @NothingNullByDefault
    public enum ConnectorMode implements IIncrementalEnum<ConnectorMode>, IHasEnumNameTextComponent, IRadialMode, StringRepresentable {

        ITEMS(MekanismLang.CONFIGURATOR_CONFIGURATE, TransmissionType.ITEM, EnumColor.GRAY, null),
        FLUIDS(MekanismLang.CONFIGURATOR_CONFIGURATE, TransmissionType.FLUID, EnumColor.DARK_AQUA, null),
        CHEMICALS(MekanismLang.CONFIGURATOR_CONFIGURATE, TransmissionType.CHEMICAL, EnumColor.YELLOW, null),
        ENERGY(MekanismLang.CONFIGURATOR_CONFIGURATE, TransmissionType.ENERGY, EnumColor.BRIGHT_GREEN, null),
        HEAT(MekanismLang.CONFIGURATOR_CONFIGURATE, TransmissionType.HEAT, EnumColor.ORANGE, null);

        public static final Codec<ConnectorMode> CODEC = StringRepresentable.fromEnum(ConnectorMode::values);
        public static final IntFunction<ConnectorMode> BY_ID = ByIdMap.continuous(ConnectorMode::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
        public static final StreamCodec<ByteBuf, ConnectorMode> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, ConnectorMode::ordinal);

        private final String serializedName;
        private final ILangEntry langEntry;
        @Nullable
        private final TransmissionType transmissionType;
        private final EnumColor color;
        private final ResourceLocation icon;

        ConnectorMode(ILangEntry langEntry, @Nullable TransmissionType transmissionType, EnumColor color, @Nullable ResourceLocation icon) {
            this.serializedName = name().toLowerCase(Locale.ROOT);
            this.langEntry = langEntry;
            this.transmissionType = transmissionType;
            this.color = color;
            if (transmissionType == null) {
                this.icon = Objects.requireNonNull(icon, "Icon should only be null if there is a transmission type present.");
            } else {
                this.icon = MekanismUtils.getResource(MekanismUtils.ResourceType.GUI, transmissionType.getTransmission() + ".png");
            }
        }

        @Override
        public ConnectorMode byIndex(int index) {
            return BY_ID.apply(index);
        }

        @Override
        public @NotNull Component sliceName() {
            return transmissionType != null ? transmissionType.getLangEntry().translateColored(color) : getTextComponent();
        }

        @Override
        public @NotNull ResourceLocation icon() {
            return icon;
        }

        @Override
        public Component getTextComponent() {
            if (transmissionType == null) {
                return langEntry.translateColored(color);
            }
            return langEntry.translateColored(color, transmissionType);
        }

        @Override
        public String getSerializedName() {
            return serializedName;
        }
    }
}
