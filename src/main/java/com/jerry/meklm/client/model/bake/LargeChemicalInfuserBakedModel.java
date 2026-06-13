package com.jerry.meklm.client.model.bake;

import com.jerry.meklm.common.base.holiday.holiday_info.ChemicalInfuserHolidayInfo;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class LargeChemicalInfuserBakedModel extends TranslatedHolidayBakedModel {

    public LargeChemicalInfuserBakedModel(Object original) {
        super(original, ChemicalInfuserHolidayInfo::getTransform);
    }
}
