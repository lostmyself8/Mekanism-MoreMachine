package com.jerry.meklg.client.model.bake;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.client.model.baked.ExtensionBakedModel.TransformedBakedModel;
import mekanism.client.render.lib.QuadTransformation;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraftforge.client.model.data.ModelData;

import org.jetbrains.annotations.Nullable;

@NothingNullByDefault
public class LargeGasBurningGeneratorBakedModel extends TransformedBakedModel<Void> {

    public LargeGasBurningGeneratorBakedModel(BakedModel original) {
        super(original, QuadTransformation.translate(0, 1, 0));
    }

    @Override
    protected @Nullable QuadsKey<Void> createKey(QuadsKey<Void> key, ModelData data) {
        return super.createKey(key, data);
    }

    @Override
    protected LargeGasBurningGeneratorBakedModel wrapModel(BakedModel model) {
        return new LargeGasBurningGeneratorBakedModel(model);
    }
}
