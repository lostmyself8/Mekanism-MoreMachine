package com.jerry.meklm.api;

/**
 * 控制改机器是否需要被配置器配置
 * 尝试于mek合并但未被同意，因此该接口将持续存在
 */
public interface INeedConfig {

    /**
     * 控制改机器是否需要被配置器配置
     * <p>
     * 默认为true，但事实上返回true和不使用该接口的效果是一样的，只是为了防止某些意外情况，
     * <br>
     * 不过这也是为了用于mek合并做的预设。
     * <br>
     * 覆写后一般返回false，以中断配置器的配置功能，这在一些大型机器中比较适用。
     */
    default boolean needConfig() {
        return true;
    }
}
