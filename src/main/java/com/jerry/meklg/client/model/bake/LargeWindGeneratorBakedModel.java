package com.jerry.meklg.client.model.bake;

import com.jerry.meklm.common.base.holiday.holiday_info.WindGeneratorHolidayInfo;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.client.model.baked.ExtensionBakedModel;
import mekanism.client.render.lib.QuadTransformation;

import net.minecraft.client.resources.model.BakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;

import org.jetbrains.annotations.Nullable;

@NothingNullByDefault
public class LargeWindGeneratorBakedModel extends ExtensionBakedModel<Void> {

    public LargeWindGeneratorBakedModel(BakedModel original) {
        super(original);
    }

    @Nullable
    @Override
    protected QuadsKey<Void> createKey(QuadsKey<Void> key, ModelData data) {
        QuadTransformation holidayTransform = WindGeneratorHolidayInfo.getTransform();
        if (holidayTransform != null) {
            return key.transform(holidayTransform);
        }
        return null;
    }

    @Override
    protected LargeWindGeneratorBakedModel wrapModel(BakedModel model) {
        return new LargeWindGeneratorBakedModel(model);
    }
}
