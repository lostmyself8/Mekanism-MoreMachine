package com.jerry.meklg.client.model.bake;

import com.jerry.meklm.common.base.holiday.holiday_info.GasGeneratorHolidayInfo;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class LargeGasGeneratorBakedModel extends TranslatedHolidayBakedModel {

    public LargeGasGeneratorBakedModel(Object original) {
        super(original, GasGeneratorHolidayInfo::getTransform);
    }
}
