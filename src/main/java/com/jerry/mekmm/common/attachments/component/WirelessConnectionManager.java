package com.jerry.mekmm.common.attachments.component;

import com.jerry.mekmm.Mekmm;
import com.jerry.mekmm.common.tile.machine.TileEntityWirelessTransmissionStation;
import com.jerry.mekmm.common.tile.prefab.TileEntityConnectableMachine.ConnectStatus;

import mekanism.api.chemical.IChemicalHandler;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.integration.energy.BlockEnergyCapabilityCache;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.util.WorldUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;

import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class WirelessConnectionManager {

    private final TileEntityWirelessTransmissionStation tile;
    // 使用 Set 存储连接配置,避免重复
    private final List<ConnectionConfig> connections = new ArrayList<>();
    // 缓存各类型的 capability
    private final Map<TransmissionType, List<Object>> capabilityCache = new EnumMap<>(TransmissionType.class);
    private boolean cacheDirty = true;

    public WirelessConnectionManager(TileEntityWirelessTransmissionStation tile) {
        this.tile = tile;
    }

    /**
     * 添加或移除连接
     */
    public ConnectStatus linkOrCut(BlockPos pos, Direction direction, TransmissionType type) {
        ConnectionConfig config = new ConnectionConfig(pos, direction, type);
        if (connections.contains(config)) {
            // 已存在,移除连接
            connections.remove(config);
            cacheDirty = true;
            return ConnectStatus.DISCONNECT;
        }
        // 尝试添加新连接 - 先验证能力是否存在
        if (validateCapability(pos, direction, type)) {
            connections.add(config);
            cacheDirty = true;
            return ConnectStatus.CONNECT;
        }
        return ConnectStatus.CONNECT_FAIL;
    }

    /**
     * 验证目标方块是否具有所需的 capability
     */
    private boolean validateCapability(BlockPos pos, Direction direction, TransmissionType type) {
        if (tile.getLevel() == null || !tile.getLevel().isLoaded(pos)) {
            return false;
        }
        return switch (type) {
            case ENERGY -> WorldUtils.getCapability(tile.getLevel(), Capabilities.ENERGY.block(), pos, direction) != null;
            case FLUID -> WorldUtils.getCapability(tile.getLevel(), Capabilities.FLUID.block(), pos, direction) != null;
            case CHEMICAL -> WorldUtils.getCapability(tile.getLevel(), Capabilities.CHEMICAL.block(), pos, direction) != null;
            case ITEM -> WorldUtils.getCapability(tile.getLevel(), Capabilities.ITEM.block(), pos, direction) != null;
            case HEAT -> WorldUtils.getCapability(tile.getLevel(), Capabilities.HEAT, pos, direction) != null;
        };
    }

    /**
     * 获取能量连接
     */
    @SuppressWarnings("unchecked")
    public Collection<BlockEnergyCapabilityCache> getEnergyCaches() {
        if (cacheDirty) {
            rebuildAllCaches();
        }
        List<?> rawList = capabilityCache.getOrDefault(TransmissionType.ENERGY, Collections.emptyList());
        return (Collection<BlockEnergyCapabilityCache>) rawList;
    }

    /**
     * 获取流体连接
     */
    @SuppressWarnings("unchecked")
    public Collection<BlockCapabilityCache<IFluidHandler, @Nullable Direction>> getFluidCaches() {
        if (cacheDirty) {
            rebuildAllCaches();
        }
        List<?> rawList = capabilityCache.getOrDefault(TransmissionType.FLUID, Collections.emptyList());
        return (Collection<BlockCapabilityCache<IFluidHandler, @Nullable Direction>>) rawList;
    }

    /**
     * 获取化学品连接
     */
    @SuppressWarnings("unchecked")
    public Collection<BlockCapabilityCache<IChemicalHandler, @Nullable Direction>> getChemicalCaches() {
        if (cacheDirty) {
            rebuildAllCaches();
        }
        List<?> rawList = capabilityCache.getOrDefault(TransmissionType.CHEMICAL, Collections.emptyList());
        return (Collection<BlockCapabilityCache<IChemicalHandler, @Nullable Direction>>) rawList;
    }

    /**
     * 获取物品连接
     */
    @SuppressWarnings("unchecked")
    public Collection<BlockCapabilityCache<IItemHandler, @Nullable Direction>> getItemCaches() {
        if (cacheDirty) {
            rebuildAllCaches();
        }
        List<?> rawList = capabilityCache.getOrDefault(TransmissionType.ITEM, Collections.emptyList());
        return (Collection<BlockCapabilityCache<IItemHandler, @Nullable Direction>>) rawList;
    }

    /**
     * 获取所有连接（包括热量连接）
     */
    public Collection<ConnectionConfig> getConnectionsByType(TransmissionType type) {
        return connections.stream().filter(c -> c.type() == type).collect(Collectors.toList());
    }

    /**
     * 获取所有连接配置 (用于遍历、渲染等)
     */
    public Collection<ConnectionConfig> getAllConnections() {
        return Collections.unmodifiableList(connections);
    }

    /**
     * 重建所有类型的缓存
     */
    private void rebuildAllCaches() {
        if (tile.getLevel() == null || tile.getLevel().isClientSide()) {
            return;
        }
        ServerLevel level = (ServerLevel) tile.getLevel();
        capabilityCache.clear();
        // 按传输类型分组并创建缓存
        Map<TransmissionType, List<ConnectionConfig>> grouped = connections.stream().collect(Collectors.groupingBy(ConnectionConfig::type));
        for (Map.Entry<TransmissionType, List<ConnectionConfig>> entry : grouped.entrySet()) {
            TransmissionType type = entry.getKey();
            List<Object> caches = new ArrayList<>();
            for (ConnectionConfig config : entry.getValue()) {
                Object cache = createCache(level, config);
                if (cache != null) {
                    caches.add(cache);
                }
            }
            if (!caches.isEmpty()) {
                capabilityCache.put(type, caches);
            }
        }
        cacheDirty = false;
    }

    /**
     * 创建指定类型的 capability 缓存
     */
    private Object createCache(ServerLevel level, ConnectionConfig config) {
        return switch (config.type()) {
            case ENERGY -> BlockEnergyCapabilityCache.create(level, config.pos(), config.direction());
            case FLUID -> Capabilities.FLUID.createCache(level, config.pos(), config.direction());
            case CHEMICAL -> Capabilities.CHEMICAL.createCache(level, config.pos(), config.direction());
            case ITEM -> Capabilities.ITEM.createCache(level, config.pos(), config.direction());
            case HEAT -> BlockCapabilityCache.create(Capabilities.HEAT, level, config.pos(), config.direction());
        };
    }

    /**
     * 定期验证并清理无效连接
     */
    public void validateConnections() {
        if (tile.getLevel() == null || tile.getLevel().isClientSide()) {
            return;
        }
        boolean removed = connections.removeIf(config -> !tile.getLevel().isLoaded(config.pos()) || tile.getLevel().isEmptyBlock(config.pos()) || !validateCapability(config.pos(), config.direction(), config.type()));
        if (removed) {
            // 删除之后要发送数据包更新渲染状态
            tile.sendUpdatePacket();
            tile.markForSave();
            cacheDirty = true;
        }
    }

    /**
     * 保存到NBT
     */
    public void saveToNBT(CompoundTag tag) {
        ListTag list = new ListTag();
        for (ConnectionConfig config : connections) {
            list.add(config.toNBT());
        }
        tag.put("connections", list);
    }

    /**
     * 从NBT加载
     */
    public void loadFromNBT(CompoundTag tag) {
        connections.clear();
        tag.getList("connections").ifPresent(list -> {
            for (int i = 0; i < list.size(); i++) {
                try {
                    list.getCompound(i).map(ConnectionConfig::fromNBT).ifPresent(connections::add);
                } catch (Exception e) {
                    Mekmm.LOGGER.warn("Failed to load wireless connection: {}", e.getMessage());
                }
            }
        });
        // 标记需要重建缓存
        cacheDirty = true;
    }

    public final List<ConnectionConfig> getConnections() {
        return connections;
    }

    /**
     * 获取指定类型的连接数量
     */
    public int getConnectionCount(TransmissionType type) {
        return (int) connections.stream().filter(c -> c.type() == type).count();
    }

    public final int getConnectionCount() {
        return connections.size();
    }

    public void remove(ConnectionConfig config) {
        connections.remove(config);
        cacheDirty = true;
        tile.sendUpdatePacket();
        tile.markForSave();
    }

    public void clear() {
        connections.clear();
        capabilityCache.clear();
        cacheDirty = true;
    }
}
