package com.jerry.mekmm.mixin;

import com.jerry.mekmm.api.MoreMachineUpgrade;
import com.jerry.mekmm.api.text.APIMoreMachineLang;

import mekanism.api.Upgrade;
import mekanism.api.text.EnumColor;
import mekanism.api.text.ILangEntry;

import net.minecraft.network.codec.StreamCodec;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.IntFunction;

@Mixin(value = Upgrade.class, remap = false)
public class MixinUpgrade {

    @Shadow
    @Final
    @Mutable
    private static Upgrade[] $VALUES;

    @Mutable
    @Shadow
    @Final
    public static Codec<Upgrade> CODEC;

    @Mutable
    @Shadow
    @Final
    public static StreamCodec<ByteBuf, Upgrade> STREAM_CODEC;

    @Mutable
    @Shadow
    @Final
    public static IntFunction<Upgrade> BY_ID;

    public MixinUpgrade() {}

    @Invoker("<init>")
    public static Upgrade upgrade$initInvoker(String internalName, int internalId, String name, ILangEntry langKey, ILangEntry descLangKey, int maxStack, EnumColor color) {
        throw new AssertionError();
    }

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void upgradeClinit(CallbackInfo ci) {
        MoreMachineUpgrade.THREAD = mekanismMoreMachine$addVariant("THREAD", APIMoreMachineLang.UPGRADE_THREAD, APIMoreMachineLang.UPGRADE_THREAD_DESCRIPTION, 8, EnumColor.BRIGHT_PINK);

        // 重新初始化静态参数，这非常重要
        mekanismMoreMachine$reinitializeByIdMap();
    }

    @Unique
    private static Upgrade mekanismMoreMachine$addVariant(String internalName, ILangEntry langKey, ILangEntry descLangKey, int maxStack, EnumColor color) {
        ArrayList<Upgrade> variants = new ArrayList<>(Arrays.asList($VALUES));
        Upgrade upgrade = upgrade$initInvoker(internalName,
                variants.getLast().ordinal() + 1,
                internalName.toLowerCase(),
                langKey,
                descLangKey,
                maxStack,
                color);
        variants.add(upgrade);
        MixinUpgrade.$VALUES = variants.toArray(new Upgrade[0]);
        return upgrade;
    }

    @Unique
    private static void mekanismMoreMachine$reinitializeByIdMap() {
        // Upgrade[] values = $VALUES;
        // Function<String, Upgrade> nameLookup = StringRepresentable.createNameLookup(values, Function.identity());
        // Function<String, Upgrade> remapper = it -> "gas".equals(it) ? CHEMICAL : nameLookup.apply(it);
        // CODEC = new StringRepresentable.EnumCodec<>(values, remapper);
        // BY_ID = ByIdMap.continuous(Upgrade::ordinal, values, ByIdMap.OutOfBoundsStrategy.WRAP);
        // STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Upgrade::ordinal);
    }
}
