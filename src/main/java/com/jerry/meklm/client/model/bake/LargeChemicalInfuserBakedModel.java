package com.jerry.meklm.client.model.bake;

import com.jerry.meklm.common.base.holiday.holiday_info.ChemicalInfuserHolidayInfo;

import mekanism.api.annotations.NothingNullByDefault;

@NothingNullByDefault
public class LargeChemicalInfuserBakedModel extends TranslatedHolidayBakedModel {

    public LargeChemicalInfuserBakedModel(Object original) {
        super(original, ChemicalInfuserHolidayInfo::getTransform);
    }
}