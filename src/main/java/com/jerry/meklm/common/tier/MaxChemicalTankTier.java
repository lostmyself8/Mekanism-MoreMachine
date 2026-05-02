package com.jerry.meklm.common.tier;

import com.jerry.meklm.api.tier.ILargeChemicalTankTier;

import mekanism.api.tier.BaseTier;
import mekanism.common.config.value.CachedLongValue;

import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.fluids.FluidType;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum MaxChemicalTankTier implements StringRepresentable, ILargeChemicalTankTier {

    BASIC(BaseTier.BASIC, 4 * 64 * FluidType.BUCKET_VOLUME, 4 * FluidType.BUCKET_VOLUME),
    ADVANCED(BaseTier.ADVANCED, 4 * 256 * FluidType.BUCKET_VOLUME, 4 * 16 * FluidType.BUCKET_VOLUME),
    ELITE(BaseTier.ELITE, 4 * 1_024 * FluidType.BUCKET_VOLUME, 4 * 128 * FluidType.BUCKET_VOLUME),
    ULTIMATE(BaseTier.ULTIMATE, 4 * 8_192 * FluidType.BUCKET_VOLUME, 4 * 512 * FluidType.BUCKET_VOLUME);

    @Getter
    private final long baseStorage;
    @Getter
    private final long baseOutput;
    private final BaseTier baseTier;
    private CachedLongValue storageReference;
    private CachedLongValue outputReference;

    MaxChemicalTankTier(BaseTier tier, long s, long o) {
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
        return "Max";
    }

    /**
     * ONLY CALL THIS FROM TierConfig. It is used to give the GasTankTier a reference to the actual config value object
     */
    public void setConfigReference(CachedLongValue storageReference, CachedLongValue outputReference) {
        this.storageReference = storageReference;
        this.outputReference = outputReference;
    }
}
