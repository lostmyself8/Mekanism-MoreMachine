package com.jerry.meklm.client.model.bake;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.client.model.baked.ExtensionBakedModel;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraftforge.client.model.data.ModelData;

import org.jetbrains.annotations.Nullable;

@NothingNullByDefault
public class LargeElectrolyticSeparatorBakedModel extends ExtensionBakedModel<Void> {

    public LargeElectrolyticSeparatorBakedModel(BakedModel original) {
        super(original);
    }

    @Override
    protected @Nullable QuadsKey<Void> createKey(QuadsKey<Void> key, ModelData data) {
        return super.createKey(key, data);
    }

    @Override
    protected LargeElectrolyticSeparatorBakedModel wrapModel(BakedModel model) {
        return new LargeElectrolyticSeparatorBakedModel(model);
    }
}
