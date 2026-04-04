package com.jerry.mekmm.mixin;

import com.jerry.mekaf.common.registries.AdvancedFactoryBlockTypes;

import mekanism.common.block.prefab.BlockTile.BlockTileModel;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.resource.BlockResourceInfo;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Supplier;

@Mixin(value = MekanismBlocks.class)
public class MixinMekanismBlocks {

    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lmekanism/common/registration/impl/BlockDeferredRegister;register(Ljava/lang/String;Ljava/util/function/Supplier;Ljava/util/function/BiFunction;)Lmekanism/common/registration/impl/BlockRegistryObject;", ordinal = 15), index = 1)
    private static Supplier<?> mixinOxidizer(Supplier<?> blockSupplier) {
        return () -> new BlockTileModel<>(AdvancedFactoryBlockTypes.CHEMICAL_OXIDIZER, properties -> properties.mapColor(BlockResourceInfo.STEEL.getMapColor()));
    }
}
