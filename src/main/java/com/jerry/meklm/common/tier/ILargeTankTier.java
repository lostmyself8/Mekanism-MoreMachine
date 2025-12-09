package com.jerry.meklm.common.tier;

import mekanism.api.tier.ITier;

public interface ILargeTankTier extends ITier {

    long getStorage();

    long getOutput();

    /**
     * 大型储罐的类型
     *
     * @return 大型储罐的类型的字符串
     */
    String type();
}
