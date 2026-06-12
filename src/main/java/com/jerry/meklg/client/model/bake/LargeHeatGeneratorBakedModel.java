package com.jerry.meklg.client.model.bake;

import com.jerry.meklm.common.base.holiday.holiday_info.HeatGeneratorHolidayInfo;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class LargeHeatGeneratorBakedModel extends TranslatedHolidayBakedModel {

    public LargeHeatGeneratorBakedModel(Object original) {
        super(original, HeatGeneratorHolidayInfo::getTransform);
    }
}
