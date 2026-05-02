package com.jerry.meklm.client.model.bake;

import java.util.function.Supplier;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.client.render.lib.QuadTransformation;
import org.jetbrains.annotations.Nullable;

@NothingNullByDefault
abstract class TranslatedHolidayBakedModel {

    private static final QuadTransformation BASE_TRANSFORM = QuadTransformation.translate(0, 1, 0);
    protected final Object originalModel;
    private final Supplier<@Nullable QuadTransformation> holidayTransform;

    protected TranslatedHolidayBakedModel(Object originalModel, Supplier<@Nullable QuadTransformation> holidayTransform) {
        this.originalModel = originalModel;
        this.holidayTransform = holidayTransform;
    }

    protected QuadTransformation getTransform() {
        QuadTransformation holiday = holidayTransform.get();
        return holiday == null ? BASE_TRANSFORM : holiday.and(BASE_TRANSFORM);
    }
}