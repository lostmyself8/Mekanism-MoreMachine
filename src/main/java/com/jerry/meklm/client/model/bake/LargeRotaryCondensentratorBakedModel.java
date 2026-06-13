package com.jerry.meklm.client.model.bake;

import com.jerry.meklm.common.base.holiday.holiday_info.RotCondHolidayInfo;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class LargeRotaryCondensentratorBakedModel extends TranslatedHolidayBakedModel {

    public LargeRotaryCondensentratorBakedModel(Object original) {
        super(original, RotCondHolidayInfo::getTransform);
    }
}
