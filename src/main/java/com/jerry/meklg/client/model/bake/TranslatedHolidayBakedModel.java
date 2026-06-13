package com.jerry.meklg.client.model.bake;

import mekanism.client.render.lib.QuadTransformation;

import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.function.Supplier;

@NullMarked
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
