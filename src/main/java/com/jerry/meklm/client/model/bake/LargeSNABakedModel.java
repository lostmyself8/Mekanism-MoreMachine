package com.jerry.meklm.client.model.bake;

import com.jerry.meklm.common.base.holiday.holiday_info.LargeSNAHolidayInfo;

import mekanism.api.annotations.NothingNullByDefault;

@NothingNullByDefault
public class LargeSNABakedModel extends TranslatedHolidayBakedModel {

    public LargeSNABakedModel(Object original) {
        super(original, LargeSNAHolidayInfo::getTransform);
    }
}
