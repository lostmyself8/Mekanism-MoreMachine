package com.jerry.meklm.common.tile;

public interface INotNeedConfig {

    default boolean notNeedConfig() {
        return true;
    }
}
