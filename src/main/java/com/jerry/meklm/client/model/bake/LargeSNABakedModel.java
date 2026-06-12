package com.jerry.meklm.client.model.bake;

import com.jerry.meklm.common.base.holiday.holiday_info.LargeSNAHolidayInfo;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class LargeSNABakedModel extends TranslatedHolidayBakedModel {

    public LargeSNABakedModel(Object original) {
        super(original, LargeSNAHolidayInfo::getTransform);
    }
}
