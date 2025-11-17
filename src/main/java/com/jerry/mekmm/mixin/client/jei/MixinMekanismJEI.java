package com.jerry.mekmm.mixin.client.jei;

import com.jerry.mekmm.common.MoreMachineLang;
import com.jerry.mekmm.common.config.MoreMachineConfig;
import com.jerry.mekmm.common.registries.MoreMachineGas;

import mekanism.client.jei.MekanismJEI;

import net.minecraftforge.fluids.FluidType;

import mezz.jei.api.registration.IRecipeRegistration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MekanismJEI.class, remap = false)
public class MixinMekanismJEI {

    @Inject(method = "registerRecipes", at = @At(value = "TAIL"))
    public void mixinRegisterRecipes(IRecipeRegistration registry, CallbackInfo ci) {
        registry.addIngredientInfo(MoreMachineGas.UNSTABLE_DIMENSIONAL_GAS.getStack(FluidType.BUCKET_VOLUME), MekanismJEI.TYPE_GAS,
                MoreMachineLang.JEI_INFO_UNSTABLE_DIMENSIONAL_GAS.translate(MoreMachineConfig.general.gasCollectAmount.get()));
    }
}
