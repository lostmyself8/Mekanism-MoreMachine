package com.jerry.mekmm.common.integration.ultimine;

import com.jerry.mekmm.api.connector.ConnectResult;
import com.jerry.mekmm.api.connector.ConnectorBinding;
import com.jerry.mekmm.api.connector.IBinding;
import com.jerry.mekmm.common.MoreMachineLang;
import com.jerry.mekmm.common.item.ItemConnector;
import com.jerry.mekmm.common.registries.MoreMachineDataComponents;
import com.jerry.mekmm.common.util.MoreMachineUtils;

import mekanism.api.text.EnumColor;
import mekanism.api.text.TextComponentUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import dev.ftb.mods.ftbultimine.api.rightclick.RightClickHandler;
import dev.ftb.mods.ftbultimine.api.shape.ShapeContext;

import java.util.Collection;
import java.util.Objects;

/**
 * 将 FTB Ultimité 提供的选区逐目标交给通用 ItemConnector 绑定逻辑。
 */
public class ConnectorHandler implements RightClickHandler {

    private final IBinding binding;

    public ConnectorHandler() {
        this(new ConnectorBinding());
    }

    public ConnectorHandler(IBinding binding) {
        this.binding = Objects.requireNonNull(binding, "binding");
    }

    // 处理 FTB Ultimité 当前右键选区；返回正数表示接管此次连锁操作
    @Override
    public int handleRightClickBlock(ShapeContext context, InteractionHand hand, Collection<BlockPos> positions) {
        ServerPlayer player = context.player();
        if (player.isShiftKeyDown()) {
            return 0;
        }
        ItemStack stack = player.getItemInHand(hand);
        if (!(stack.getItem() instanceof ItemConnector connector)) {
            return 0;
        }
        GlobalPos source = stack.get(MoreMachineDataComponents.CONNECT_FROM);
        if (source == null) {
            return 0;
        }

        int connected = 0;
        int disconnected = 0;
        int failed = 0;
        for (BlockPos target : positions) {
            ConnectResult result = binding.bind(player, player.level(), source, target, context.face(), connector.getMode(stack).getTransmissionType(), false);
            switch (result) {
                case CONNECT -> connected++;
                case DISCONNECT -> disconnected++;
                case CONNECT_FAIL -> failed++;
                case CONNECTOR_SELF -> {
                    player.displayClientMessage(MoreMachineLang.CONNECTOR_SELF.translate(EnumColor.DARK_RED), true);
                    return 1;
                }
                case SOURCE_LOST -> {
                    player.displayClientMessage(MoreMachineLang.CONNECTOR_LOSE.translate(EnumColor.DARK_RED, MoreMachineUtils.formatPos(source.pos())), true);
                    return 1;
                }
                case ACROSS_DIMENSION -> {
                    player.displayClientMessage(MoreMachineLang.CONNECTOR_ACROSS_DIMENSION.translate(EnumColor.DARK_RED), true);
                    return 1;
                }
            }
        }
        // 如果能连锁那必然是同类型方块，直接获取第一个方块的名字就好
        MutableComponent targetName = TextComponentUtil.translate(context.origState().getBlock().getDescriptionId());
        if (connected != 0 || disconnected != 0) {
            player.displayClientMessage(MoreMachineLang.CONNECTOR_CHAIN_RESULT.translate(EnumColor.INDIGO, targetName, EnumColor.INDIGO, context.face(), EnumColor.BRIGHT_GREEN, connected, EnumColor.BRIGHT_GREEN, disconnected, EnumColor.DARK_RED, failed), true);
            return 1;
        }
        player.displayClientMessage(MoreMachineLang.CONNECTOR_CHAIN_FAIL.translate(EnumColor.DARK_RED, targetName, EnumColor.DARK_RED, context.face()), true);
        // 一次连接器操作算一个处理单元：FTB 保留冷却，(1 - 1) 产生零额外经验扣除，零有效目标也不重放单点切换。
        return 1;
    }
}
