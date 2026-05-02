package com.jerry.meklm.common.base.holiday.holiday_info;

import com.jerry.meklm.common.base.holiday.AprilFools;
import com.jerry.meklm.common.base.holiday.Holiday;
import com.jerry.meklm.common.base.holiday.May4;

import com.jerry.mekmm.Mekmm;

import mekanism.client.render.lib.QuadTransformation;
import mekanism.common.Mekanism;
import mekanism.common.base.holiday.HolidayManager;

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.TextureAtlasStitchedEvent;

import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.function.Predicate;

@EventBusSubscriber(modid = Mekmm.MOD_ID, value = Dist.CLIENT)
public class LargeSNAHolidayInfo extends BaseHolidayInfo {

    private static final Predicate<Identifier> IS_FIRST = s -> s.getPath().contains("screen_hello");
    private static final Predicate<Identifier> IS_SECOND = s -> s.getPath().contains("screen_cmd");
    private static Map<Holiday, QuadTransformation> HOLIDAY_MINER_TRANSFORMS = Collections.emptyMap();

    private LargeSNAHolidayInfo() {}

    @SubscribeEvent
    public static void onStitch(TextureAtlasStitchedEvent event) {
        TextureAtlas atlas = event.getAtlas();
        if (!atlas.location().equals(TextureAtlas.LOCATION_BLOCKS)) {
            return;
        }
        HOLIDAY_MINER_TRANSFORMS = Map.of(
                AprilFools.INSTANCE, QuadTransformation.list(
                        QuadTransformation.TextureFilteredTransformation.of(suffixTexture(atlas, Mekanism.rl("block/models/digital_miner_screen_"), "afd_sad"), IS_FIRST),
                        QuadTransformation.TextureFilteredTransformation.of(suffixTexture(atlas, Mekmm.rl("block/large_machine/large_solar_neutron_activator/screen_"), "afd_text"), IS_SECOND)),
                May4.INSTANCE, QuadTransformation.TextureFilteredTransformation.of(suffixTexture(atlas, Mekanism.rl("block/models/digital_miner_screen_"), "may4th"), IS_FIRST));
    }

    @Nullable
    public static QuadTransformation getTransform() {
        if (HolidayManager.areHolidaysEnabled()) {
            for (Map.Entry<Holiday, QuadTransformation> entry : HOLIDAY_MINER_TRANSFORMS.entrySet()) {
                if (entry.getKey().isToday()) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }
}
