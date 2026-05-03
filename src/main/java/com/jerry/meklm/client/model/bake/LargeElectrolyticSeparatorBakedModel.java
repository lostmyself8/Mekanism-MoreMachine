package com.jerry.meklm.client.model.bake;

import com.jerry.meklm.common.base.holiday.holiday_info.ElectroSeparatorHolidayInfo;

import mekanism.api.annotations.NothingNullByDefault;

@NothingNullByDefault
public class LargeElectrolyticSeparatorBakedModel extends TranslatedHolidayBakedModel {

    public LargeElectrolyticSeparatorBakedModel(Object original) {
        super(original, ElectroSeparatorHolidayInfo::getTransform);
    }
}
