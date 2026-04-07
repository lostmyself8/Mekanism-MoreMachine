package com.jerry.mekmm.mixin;

import com.jerry.mekaf.common.registries.AdvancedFactoryBlockTypes;

import mekanism.common.block.prefab.BlockTile.BlockTileModel;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.resource.BlockResourceInfo;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Supplier;

@Mixin(value = MekanismBlocks.class, remap = false)
public class MixinMekanismBlocks {

    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lmekanism/common/registration/impl/BlockDeferredRegister;register(Ljava/lang/String;Ljava/util/function/Supplier;Ljava/util/function/BiFunction;)Lmekanism/common/registration/impl/BlockRegistryObject;", ordinal = 15), index = 1)
    private static Supplier<?> modifyOxidizer(Supplier<?> blockSupplier) {
        return () -> new BlockTileModel<>(AdvancedFactoryBlockTypes.CHEMICAL_OXIDIZER, properties -> properties.mapColor(BlockResourceInfo.STEEL.getMapColor()));
    }

    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lmekanism/common/registration/impl/BlockDeferredRegister;register(Ljava/lang/String;Ljava/util/function/Supplier;Ljava/util/function/BiFunction;)Lmekanism/common/registration/impl/BlockRegistryObject;", ordinal = 20), index = 1)
    private static Supplier<?> modifyDissolution(Supplier<?> blockSupplier) {
        return () -> new BlockTileModel<>(AdvancedFactoryBlockTypes.CHEMICAL_DISSOLUTION_CHAMBER, properties -> properties.mapColor(BlockResourceInfo.STEEL.getMapColor()));
    }

    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lmekanism/common/registration/impl/BlockDeferredRegister;register(Ljava/lang/String;Ljava/util/function/Supplier;Ljava/util/function/BiFunction;)Lmekanism/common/registration/impl/BlockRegistryObject;", ordinal = 21), index = 1)
    private static Supplier<?> modifyWasher(Supplier<?> blockSupplier) {
        return () -> new BlockTileModel<>(AdvancedFactoryBlockTypes.CHEMICAL_WASHER, properties -> properties.mapColor(BlockResourceInfo.STEEL.getMapColor()));
    }

    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lmekanism/common/registration/impl/BlockDeferredRegister;register(Ljava/lang/String;Ljava/util/function/Supplier;Ljava/util/function/BiFunction;)Lmekanism/common/registration/impl/BlockRegistryObject;", ordinal = 22), index = 1)
    private static Supplier<?> modifyCrystallizer(Supplier<?> blockSupplier) {
        return () -> new BlockTileModel<>(AdvancedFactoryBlockTypes.CHEMICAL_CRYSTALLIZER, properties -> properties.mapColor(BlockResourceInfo.STEEL.getMapColor()));
    }

    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lmekanism/common/registration/impl/BlockDeferredRegister;register(Ljava/lang/String;Ljava/util/function/Supplier;Ljava/util/function/BiFunction;)Lmekanism/common/registration/impl/BlockRegistryObject;", ordinal = 23), index = 1)
    private static Supplier<?> modifyReaction(Supplier<?> blockSupplier) {
        return () -> new BlockTileModel<>(AdvancedFactoryBlockTypes.PRESSURIZED_REACTION_CHAMBER, properties -> properties.mapColor(BlockResourceInfo.STEEL.getMapColor()));
    }

    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lmekanism/common/registration/impl/BlockDeferredRegister;register(Ljava/lang/String;Ljava/util/function/Supplier;Ljava/util/function/BiFunction;)Lmekanism/common/registration/impl/BlockRegistryObject;", ordinal = 24), index = 1)
    private static Supplier<?> modifyCentrifuge(Supplier<?> blockSupplier) {
        return () -> new BlockTileModel<>(AdvancedFactoryBlockTypes.ISOTOPIC_CENTRIFUGE, properties -> properties.mapColor(BlockResourceInfo.STEEL.getMapColor()));
    }

    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lmekanism/common/registration/impl/BlockDeferredRegister;register(Ljava/lang/String;Ljava/util/function/Supplier;Ljava/util/function/BiFunction;)Lmekanism/common/registration/impl/BlockRegistryObject;", ordinal = 25), index = 1)
    private static Supplier<?> modifyLiquifier(Supplier<?> blockSupplier) {
        return () -> new BlockTileModel<>(AdvancedFactoryBlockTypes.NUTRITIONAL_LIQUIFIER, properties -> properties.noOcclusion().mapColor(BlockResourceInfo.STEEL.getMapColor()));
    }

    // @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target =
    // "Lmekanism/common/registration/impl/BlockDeferredRegister;register(Ljava/lang/String;Ljava/util/function/Supplier;Ljava/util/function/BiFunction;)Lmekanism/common/registration/impl/BlockRegistryObject;",
    // ordinal = 34), index = 1)
    // private static Supplier<?> modifyExtractor(Supplier<?> blockSupplier) {
    // return () -> new BlockTileModel<>(AdvancedFactoryBlockTypes.PIGMENT_EXTRACTOR, properties ->
    // properties.mapColor(BlockResourceInfo.STEEL.getMapColor()));
    // }
    //
    // @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target =
    // "Lmekanism/common/registration/impl/BlockDeferredRegister;register(Ljava/lang/String;Ljava/util/function/Supplier;Ljava/util/function/BiFunction;)Lmekanism/common/registration/impl/BlockRegistryObject;",
    // ordinal = 36), index = 1)
    // private static Supplier<?> modifyPainting(Supplier<?> blockSupplier) {
    // return () -> new BlockTileModel<>(AdvancedFactoryBlockTypes.PAINTING_MACHINE, properties ->
    // properties.mapColor(BlockResourceInfo.STEEL.getMapColor()));
    // }
}
