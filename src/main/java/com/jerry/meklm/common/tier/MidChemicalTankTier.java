package com.jerry.meklm.common.tier;

import com.jerry.meklm.api.tier.ILargeChemicalTankTier;

import mekanism.api.tier.BaseTier;
import mekanism.common.config.value.CachedLongValue;

import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.fluids.FluidType;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum MidChemicalTankTier implements StringRepresentable, ILargeChemicalTankTier {

    BASIC(BaseTier.BASIC, (long) (2.5 * 64 * FluidType.BUCKET_VOLUME), (long) (2.5 * FluidType.BUCKET_VOLUME)),
    ADVANCED(BaseTier.ADVANCED, (long) (2.5 * 256 * FluidType.BUCKET_VOLUME), (long) (2.5 * 16 * FluidType.BUCKET_VOLUME)),
    ELITE(BaseTier.ELITE, (long) (2.5 * 1_024 * FluidType.BUCKET_VOLUME), (long) (2.5 * 128 * FluidType.BUCKET_VOLUME)),
    ULTIMATE(BaseTier.ULTIMATE, (long) (2.5 * 8_192 * FluidType.BUCKET_VOLUME), (long) (2.5 * 512 * FluidType.BUCKET_VOLUME));

    @Getter
    private final long baseStorage;
    @Getter
    private final long baseOutput;
    private final BaseTier baseTier;
    private CachedLongValue storageReference;
    private CachedLongValue outputReference;

    MidChemicalTankTier(BaseTier tier, long s, long o) {
        baseStorage = s;
        baseOutput = o;
        baseTier = tier;
    }

    @Override
    public BaseTier getBaseTier() {
        return baseTier;
    }

    @NotNull
    @Override
    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }

    @Override
    public long getStorage() {
        return storageReference == null ? getBaseStorage() : storageReference.getOrDefault();
    }

    @Override
    public long getOutput() {
        return outputReference == null ? getBaseOutput() : outputReference.getOrDefault();
    }

    @Override
    public String getType() {
        return "Mid";
    }

    /**
     * ONLY CALL THIS FROM TierConfig. It is used to give the GasTankTier a reference to the actual config value object
     */
    public void setConfigReference(CachedLongValue storageReference, CachedLongValue outputReference) {
        this.storageReference = storageReference;
        this.outputReference = outputReference;
    }
}
