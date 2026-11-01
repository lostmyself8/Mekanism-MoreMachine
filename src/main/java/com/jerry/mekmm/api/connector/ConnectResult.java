package com.jerry.mekmm.api.connector;

import com.jerry.mekmm.common.tile.prefab.TileEntityConnectableMachine.ConnectStatus;

/**
 * ItemConnector 绑定校验和连接切换的结果。
 */
public enum ConnectResult {

    /** 本次操作新建了连接。 */
    CONNECT,
    /** 本次操作移除了已有连接。 */
    DISCONNECT,
    /** 目标方块不支持当前连接或连接操作被拒绝。 */
    CONNECT_FAIL,
    /** 目标是连接源本身或其附属方块。 */
    CONNECTOR_SELF,
    /** 保存的连接源方块已经不存在或不再可连接。 */
    SOURCE_LOST,
    /** 连接源所在维度与当前操作维度不同。 */
    ACROSS_DIMENSION;

    /**
     *
     * @param status 连接状态
     * @return 连接结果
     */
    public static ConnectResult toResult(ConnectStatus status) {
        return switch (status) {
            case CONNECT -> CONNECT;
            case DISCONNECT -> DISCONNECT;
            case CONNECT_FAIL -> CONNECT_FAIL;
        };
    }
}
