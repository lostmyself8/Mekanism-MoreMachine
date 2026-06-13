package com.jerry.meklm.client.model.bake;

import com.jerry.meklm.common.base.holiday.holiday_info.AntiNucleHolidayInfo;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class LargeAntiprotonicNucleosynthesizerBakedModel extends TranslatedHolidayBakedModel {

    public LargeAntiprotonicNucleosynthesizerBakedModel(Object original) {
        super(original, AntiNucleHolidayInfo::getTransform);
    }
}
