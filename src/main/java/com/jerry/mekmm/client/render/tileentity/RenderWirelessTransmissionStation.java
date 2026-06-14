package com.jerry.mekmm.client.render.tileentity;

import com.jerry.mekmm.client.render.RenderLineHelper;
import com.jerry.mekmm.client.render.tileentity.RenderWirelessTransmissionStation.WirelessTransmissionStationRenderState;
import com.jerry.mekmm.common.attachments.component.ConnectionConfig;
import com.jerry.mekmm.common.item.ItemConnector;
import com.jerry.mekmm.common.tile.machine.TileEntityWirelessTransmissionStation;

import mekanism.client.render.tileentity.MekanismTileEntityRenderer;
import mekanism.common.lib.transmitter.TransmissionType;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Vector3f;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;

@NullMarked
public class RenderWirelessTransmissionStation extends MekanismTileEntityRenderer<TileEntityWirelessTransmissionStation, WirelessTransmissionStationRenderState> {

    public RenderWirelessTransmissionStation(Context context) {
        super(context);
    }

    /**
     * 渲染单个连接
     */
    private void renderConnection(PoseStack matrix, SubmitNodeCollector submitNodeCollector, BlockPos tilePos, ConnectionConfig config) {
        Vector3f start = new Vector3f(0.5f, 2.7f, 0.5f);
        BlockPos targetPos = config.pos();
        Direction targetFace = config.direction();
        // 计算相对坐标
        float relX = targetPos.getX() - tilePos.getX();
        float relY = targetPos.getY() - tilePos.getY();
        float relZ = targetPos.getZ() - tilePos.getZ();
        // 目标方块中心
        Vector3f end = new Vector3f(relX + 0.5f, relY + 0.5f, relZ + 0.5f);
        // 偏移到面上
        Vector3f faceOffset = getFaceOffset(targetFace);
        end.add(faceOffset);
        // 根据传输类型选择颜色
        ColorRGB color = getColorForType(config.type());
        // 渲染光束
        submitNodeCollector.submitCustomGeometry(matrix, RenderTypes.lightning(), (pose, vertexConsumer) -> RenderLineHelper.renderLine(pose, vertexConsumer, start, end, color.r, color.g, color.b, 0.15f));
    }

    /**
     * 获取方块某个面的中心点
     */
    private Vector3f getFaceOffset(Direction face) {
        // 根据方向偏移到面的中心
        // 偏移量为 0.5 (半个方块) 加上一个小的间隙 (0.01) 避免 Z-fighting
        float offset = 0.51f;
        return switch (face) {
            case UP -> new Vector3f(0, offset, 0);
            case DOWN -> new Vector3f(0, -offset, 0);
            case NORTH -> new Vector3f(0, 0, -offset);
            case SOUTH -> new Vector3f(0, 0, offset);
            case WEST -> new Vector3f(-offset, 0, 0);
            case EAST -> new Vector3f(offset, 0, 0);
        };
    }

    /**
     * 根据传输类型返回颜色
     */
    private ColorRGB getColorForType(TransmissionType type) {
        return switch (type) {
            case ENERGY -> new ColorRGB(1.0f, 0.0f, 0.0f);      // 红色 - 能量
            case FLUID -> new ColorRGB(0.0f, 0.4f, 1.0f);       // 蓝色 - 流体
            case CHEMICAL -> new ColorRGB(1.0f, 1.0f, 0.0f);    // 黄色 - 化学品
            case ITEM -> new ColorRGB(0.0f, 1.0f, 0.0f);        // 绿色 - 物品
            case HEAT -> new ColorRGB(1.0f, 0.5f, 0.0f);        // 橙色 - 热量
        };
    }

    @Override
    protected String getProfilerSection() {
        return "wirelessTransmissionStation";
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    @Override
    public boolean shouldRender(TileEntityWirelessTransmissionStation blockEntity, Vec3 cameraPos) {
        return isHandHeldConnector() && super.shouldRender(blockEntity, cameraPos);
    }

    @Override
    public AABB getRenderBoundingBox(TileEntityWirelessTransmissionStation blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();
        if (isHandHeldConnector()) {
            return new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY() + 2, pos.getZ());
        }
        return super.getRenderBoundingBox(blockEntity);
    }

    private boolean isHandHeldConnector() {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player != null) {
            ItemStack mainHand = player.getMainHandItem();
            ItemStack offHand = player.getOffhandItem();
            return mainHand.getItem() instanceof ItemConnector || offHand.getItem() instanceof ItemConnector;
        }
        return false;
    }

    @Override
    public WirelessTransmissionStationRenderState createRenderState() {
        return new WirelessTransmissionStationRenderState();
    }

    @Override
    public void extractRenderState(TileEntityWirelessTransmissionStation tile, WirelessTransmissionStationRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        super.extractRenderState(tile, state, partialTicks, cameraPosition, breakProgress);
        // 获取传输站的位置 (中心点)
        state.stationPos = tile.getBlockPos();
        // 获取所有连接配置
        state.connections = tile.connectionManager.getAllConnections();
    }

    @Override
    public void submit(WirelessTransmissionStationRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        // 为每个连接绘制光束
        if (state.connections != null) {
            for (ConnectionConfig config : state.connections) {
                if (state.stationPos != null) {
                    renderConnection(poseStack, submitNodeCollector, state.stationPos, config);
                }
            }
        }
    }

    /**
     * 简单的 RGB 颜色封装
     */
    private record ColorRGB(float r, float g, float b) {}

    public static class WirelessTransmissionStationRenderState extends BlockEntityRenderState {

        @Nullable
        public BlockPos stationPos;
        @Nullable
        public Collection<ConnectionConfig> connections;
    }
}
