package com.jerry.mekmm.mixin.resource.ore;

import com.jerry.mekmm.common.resource.MoreMachineResource;
import com.jerry.mekmm.common.resource.ore.MoreMachineOreType;

import mekanism.common.resource.IResource;
import mekanism.common.resource.ore.BaseOreConfig;
import mekanism.common.resource.ore.OreAnchor;
import mekanism.common.resource.ore.OreType;
import mekanism.common.world.height.HeightShape;

import net.minecraft.util.StringRepresentable;

import com.mojang.serialization.Codec;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;

@Mixin(value = OreType.class, remap = false)
public class MixinOreType {

    @Shadow
    @Final
    @Mutable
    private static OreType[] $VALUES;

    @Shadow
    @Mutable
    public static Codec<OreType> CODEC;

    @Invoker("<init>")
    public static OreType mekmm$initInvoker(String internalName, int internalId, IResource resource, BaseOreConfig... configs) {
        throw new AssertionError();
    }

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void mekmm$addOreTypes(CallbackInfo ci) {
        MoreMachineOreType.SILVER = mekmm$addVariant("SILVER", MoreMachineResource.SILVER,
                new BaseOreConfig("small", 16, 0, 4, HeightShape.TRAPEZOID, OreAnchor.absolute(-24), OreAnchor.absolute(88)),
                new BaseOreConfig("large", 10, 0, 8, HeightShape.TRAPEZOID, OreAnchor.absolute(-32), OreAnchor.absolute(64)));
        CODEC = StringRepresentable.fromEnum(OreType::values);
    }

    @Unique
    private static OreType mekmm$addVariant(String internalName, IResource resource, BaseOreConfig... configs) {
        ArrayList<OreType> variants = new ArrayList<>(Arrays.asList($VALUES));
        OreType ore = mekmm$initInvoker(internalName, variants.getLast().ordinal() + 1, resource, configs);
        variants.add(ore);
        $VALUES = variants.toArray(new OreType[0]);
        return ore;
    }
}
