package com.jerry.meklm.api;

public interface INotNeedConfig {

    default boolean notNeedConfig() {
        return true;
    }
}
