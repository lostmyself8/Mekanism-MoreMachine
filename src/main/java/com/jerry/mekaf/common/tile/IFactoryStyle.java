package com.jerry.mekaf.common.tile;

public interface IFactoryStyle {

    /**
     * 该工厂竖直方向上的储罐数量，例如离心工厂是2，氧化工厂是1
     *
     * @return gui中竖直方向上的储罐数量
     */
    default int getTankCount() {
        return 0;
    }

    /**
     * 该工厂上方储罐的数量
     *
     * @return gui中竖直方向上的储罐数量
     */
    default int UpperTankCount() {
        return 0;
    }

    /**
     * @return 有无额外资源条
     */
    default boolean hasExtraResourceBar() {
        return false;
    }

    /**
     * 额外资源条数量，一般为1，但加压工厂为2
     *
     * @return 额外资源条数量
     */
    default int getBarCount() {
        return 1;
    }
}
