package com.jerry.mekmm.common.util;

import mekanism.common.util.WorldUtils;
import mekanism.common.util.MekanismUtils;

import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import lombok.Getter;

import java.util.Objects;

public class WorldUtil {

    public static class SolarCheck {

        @Getter
        protected final boolean needsRainCheck;
        @Getter
        protected float peakMultiplier;
        protected final BlockPos pos;
        protected final Level world;
        protected boolean canSeeSun;

        public SolarCheck(Level world, BlockPos pos) {
            this.world = Objects.requireNonNull(world, "world");
            this.pos = Objects.requireNonNull(pos, "pos");
            Biome b = this.world.getBiomeManager().getBiome(this.pos).value();
            needsRainCheck = b.getPrecipitationAt(this.pos) != Biome.Precipitation.NONE;
            // Consider the best temperature to be 0.8; biomes that are higher than that
            // will suffer an efficiency loss (semiconductors don't like heat); biomes that are cooler
            // get a boost. We scale the efficiency to around 30% so that it doesn't totally dominate
            float tempEff = 0.3F * (0.8F - b.getTemperature(this.pos));

            // Treat rainfall as a proxy for humidity; any humidity works as a drag on overall efficiency.
            // As with temperature, we scale it so that it doesn't overwhelm production. Note the signedness
            // on the scaling factor. Also note that we only use rainfall as a proxy if it CAN rain; some dimensions
            // (like the End) have rainfall set, but can't actually support rain.
            float humidityEff = needsRainCheck ? -0.3F * b.getModifiedClimateSettings().downfall() : 0;
            peakMultiplier = 1.0F + tempEff + humidityEff;
        }

        public void recheckCanSeeSun() {
            canSeeSun = WorldUtils.canSeeSun(world, pos);
        }

        public boolean canSeeSun() {
            return canSeeSun;
        }

        public float getProductionMultiplier() {
            if (!canSeeSun) {
                return 0;
            }
            if (needsRainCheck && (this.world.isRaining() || this.world.isThundering())) {
                // If the generator is in a biome where it can rain, and it's raining penalize production by 80%
                return peakMultiplier * 0.2F;
            }
            return peakMultiplier;
        }
    }

    public static class CachedSolarCheck extends SolarCheck {

        private final int recheckFrequency;
        private long lastCheckedSun;

        public CachedSolarCheck(Level world, BlockPos pos) {
            super(world, pos);
            recheckFrequency = Mth.nextInt(world.random, MekanismUtils.TICKS_PER_HALF_SECOND, MekanismUtils.TICKS_PER_HALF_SECOND + SharedConstants.TICKS_PER_SECOND);
        }

        @Override
        public void recheckCanSeeSun() {
            if (!world.dimensionType().hasSkyLight() || world.getSkyDarken() >= 4) {
                canSeeSun = false;
                return;
            }
            long time = world.getGameTime();
            if (time < lastCheckedSun + recheckFrequency) {
                return;
            }
            lastCheckedSun = time;
            if (world.getFluidState(pos).isEmpty()) {
                canSeeSun = world.canSeeSky(pos);
            } else {
                BlockPos above = pos.above();
                if (world.canSeeSky(above)) {
                    BlockState state = world.getBlockState(above);
                    canSeeSun = !state.liquid() && state.getLightBlock(world, above) <= 0;
                } else {
                    canSeeSun = false;
                }
            }
        }
    }

    public static float calculateSunRayGroundAngle(Level world, BlockPos targetPos) {
        Objects.requireNonNull(world, "world");
        Objects.requireNonNull(targetPos, "targetPos");
        if (!world.dimensionType().hasSkyLight()) {
            return 0F;
        }
        double dayProgress = Math.floorMod(world.getDayTime(), 24_000L) / 24_000D;
        double sunRadians = dayProgress * Math.PI * 2D;
        Vec3 target = Vec3.atCenterOf(targetPos);
        Vec3 sun = target.add(Math.cos(sunRadians) * 1024D, Math.sin(sunRadians) * 1024D, 0D);
        Vec3 ray = target.subtract(sun);
        double horizontalLength = Math.sqrt(ray.x * ray.x + ray.z * ray.z);
        return (float) Math.toDegrees(Math.atan2(-ray.y, horizontalLength));
    }
}
