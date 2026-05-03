package com.jerry.mekmm.mixin;

import com.jerry.mekaf.common.registries.AdvancedFactoryBlockTypes;

import mekanism.common.block.prefab.BlockTile;
import mekanism.common.block.prefab.BlockTile.BlockTileModel;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.resource.BlockResourceInfo;

import net.minecraft.world.level.block.state.BlockBehaviour;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Function;

@Mixin(value = MekanismBlocks.class, remap = false)
public class MixinMekanismBlocks {

    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lmekanism/common/registration/impl/BlockDeferredRegister;register(Ljava/lang/String;Ljava/util/function/Supplier;Ljava/util/function/BiFunction;)Lmekanism/common/registration/impl/BlockRegistryObject;", ordinal = 15), index = 1)
    private static Function<BlockBehaviour.Properties, ?> modifyOxidizer(Function<BlockBehaviour.Properties, ?> blockSupplier) {
        return properties -> new BlockTileModel<>(AdvancedFactoryBlockTypes.CHEMICAL_OXIDIZER, BlockTile.defaultProperties(properties).mapColor(BlockResourceInfo.STEEL.getMapColor()));
    }

    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lmekanism/common/registration/impl/BlockDeferredRegister;register(Ljava/lang/String;Ljava/util/function/Supplier;Ljava/util/function/BiFunction;)Lmekanism/common/registration/impl/BlockRegistryObject;", ordinal = 20), index = 1)
    private static Function<BlockBehaviour.Properties, ?> modifyDissolution(Function<BlockBehaviour.Properties, ?> blockSupplier) {
        return properties -> new BlockTileModel<>(AdvancedFactoryBlockTypes.CHEMICAL_DISSOLUTION_CHAMBER, BlockTile.defaultProperties(properties).mapColor(BlockResourceInfo.STEEL.getMapColor()));
    }

    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lmekanism/common/registration/impl/BlockDeferredRegister;register(Ljava/lang/String;Ljava/util/function/Supplier;Ljava/util/function/BiFunction;)Lmekanism/common/registration/impl/BlockRegistryObject;", ordinal = 21), index = 1)
    private static Function<BlockBehaviour.Properties, ?> modifyWasher(Function<BlockBehaviour.Properties, ?> blockSupplier) {
        return properties -> new BlockTileModel<>(AdvancedFactoryBlockTypes.CHEMICAL_WASHER, BlockTile.defaultProperties(properties).mapColor(BlockResourceInfo.STEEL.getMapColor()));
    }

    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lmekanism/common/registration/impl/BlockDeferredRegister;register(Ljava/lang/String;Ljava/util/function/Supplier;Ljava/util/function/BiFunction;)Lmekanism/common/registration/impl/BlockRegistryObject;", ordinal = 22), index = 1)
    private static Function<BlockBehaviour.Properties, ?> modifyCrystallizer(Function<BlockBehaviour.Properties, ?> blockSupplier) {
        return properties -> new BlockTileModel<>(AdvancedFactoryBlockTypes.CHEMICAL_CRYSTALLIZER, BlockTile.defaultProperties(properties).mapColor(BlockResourceInfo.STEEL.getMapColor()));
    }

    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lmekanism/common/registration/impl/BlockDeferredRegister;register(Ljava/lang/String;Ljava/util/function/Supplier;Ljava/util/function/BiFunction;)Lmekanism/common/registration/impl/BlockRegistryObject;", ordinal = 23), index = 1)
    private static Function<BlockBehaviour.Properties, ?> modifyReaction(Function<BlockBehaviour.Properties, ?> blockSupplier) {
        return properties -> new BlockTileModel<>(AdvancedFactoryBlockTypes.PRESSURIZED_REACTION_CHAMBER, BlockTile.defaultProperties(properties).mapColor(BlockResourceInfo.STEEL.getMapColor()));
    }

    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lmekanism/common/registration/impl/BlockDeferredRegister;register(Ljava/lang/String;Ljava/util/function/Supplier;Ljava/util/function/BiFunction;)Lmekanism/common/registration/impl/BlockRegistryObject;", ordinal = 24), index = 1)
    private static Function<BlockBehaviour.Properties, ?> modifyCentrifuge(Function<BlockBehaviour.Properties, ?> blockSupplier) {
        return properties -> new BlockTileModel<>(AdvancedFactoryBlockTypes.ISOTOPIC_CENTRIFUGE, BlockTile.defaultProperties(properties).mapColor(BlockResourceInfo.STEEL.getMapColor()));
    }

    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lmekanism/common/registration/impl/BlockDeferredRegister;register(Ljava/lang/String;Ljava/util/function/Supplier;Ljava/util/function/BiFunction;)Lmekanism/common/registration/impl/BlockRegistryObject;", ordinal = 25), index = 1)
    private static Function<BlockBehaviour.Properties, ?> modifyLiquifier(Function<BlockBehaviour.Properties, ?> blockSupplier) {
        return properties -> new BlockTileModel<>(AdvancedFactoryBlockTypes.NUTRITIONAL_LIQUIFIER, BlockTile.defaultProperties(properties).noCollision().mapColor(BlockResourceInfo.STEEL.getMapColor()));
    }

    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lmekanism/common/registration/impl/BlockDeferredRegister;register(Ljava/lang/String;Ljava/util/function/Supplier;Ljava/util/function/BiFunction;)Lmekanism/common/registration/impl/BlockRegistryObject;", ordinal = 34), index = 1)
    private static Function<BlockBehaviour.Properties, ?> modifyExtractor(Function<BlockBehaviour.Properties, ?> blockSupplier) {
        return properties -> new BlockTileModel<>(AdvancedFactoryBlockTypes.PIGMENT_EXTRACTOR, BlockTile.defaultProperties(properties).mapColor(BlockResourceInfo.STEEL.getMapColor()));
    }

    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lmekanism/common/registration/impl/BlockDeferredRegister;register(Ljava/lang/String;Ljava/util/function/Supplier;Ljava/util/function/BiFunction;)Lmekanism/common/registration/impl/BlockRegistryObject;", ordinal = 36), index = 1)
    private static Function<BlockBehaviour.Properties, ?> modifyPainting(Function<BlockBehaviour.Properties, ?> blockSupplier) {
        return properties -> new BlockTileModel<>(AdvancedFactoryBlockTypes.PAINTING_MACHINE, BlockTile.defaultProperties(properties).mapColor(BlockResourceInfo.STEEL.getMapColor()));
    }
}
