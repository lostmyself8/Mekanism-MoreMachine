package com.jerry.meklm.client.model.bake;

import com.jerry.meklm.common.base.holiday.holiday_info.PigmentMixerHolidayInfo;

import mekanism.api.annotations.NothingNullByDefault;

@NothingNullByDefault
public class LargePigmentMixerBakedModel extends TranslatedHolidayBakedModel {

    public LargePigmentMixerBakedModel(Object original) {
        super(original, PigmentMixerHolidayInfo::getTransform);
    }
}
