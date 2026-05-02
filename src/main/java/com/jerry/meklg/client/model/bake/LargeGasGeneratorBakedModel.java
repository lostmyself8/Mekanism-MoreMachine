package com.jerry.meklg.client.model.bake;

import com.jerry.meklm.common.base.holiday.holiday_info.GasGeneratorHolidayInfo;

import mekanism.api.annotations.NothingNullByDefault;

@NothingNullByDefault
public class LargeGasGeneratorBakedModel extends TranslatedHolidayBakedModel {

    public LargeGasGeneratorBakedModel(Object original) {
        super(original, GasGeneratorHolidayInfo::getTransform);
    }
}
