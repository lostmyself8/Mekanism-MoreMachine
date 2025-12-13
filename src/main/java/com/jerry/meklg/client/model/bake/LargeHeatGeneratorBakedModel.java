package com.jerry.meklg.client.model.bake;

import com.jerry.meklm.common.base.holiday.holiday_info.HeatGeneratorHolidayInfo;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.client.model.baked.ExtensionBakedModel.TransformedBakedModel;
import mekanism.client.render.lib.QuadTransformation;

import net.minecraft.client.resources.model.BakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;

import org.jetbrains.annotations.Nullable;

@NothingNullByDefault
public class LargeHeatGeneratorBakedModel extends TransformedBakedModel<Void> {

    public LargeHeatGeneratorBakedModel(BakedModel original) {
        super(original, QuadTransformation.translate(0, 1, 0));
    }

    @Nullable
    @Override
    protected QuadsKey<Void> createKey(QuadsKey<Void> key, ModelData data) {
        QuadTransformation holidayTransform = HeatGeneratorHolidayInfo.getTransform();
        if (holidayTransform != null) {
            return key.transform(holidayTransform.and(QuadTransformation.translate(0, 1, 0)));
        }
        return super.createKey(key, data);
    }

    @Override
    protected LargeHeatGeneratorBakedModel wrapModel(BakedModel model) {
        return new LargeHeatGeneratorBakedModel(model);
    }
}
