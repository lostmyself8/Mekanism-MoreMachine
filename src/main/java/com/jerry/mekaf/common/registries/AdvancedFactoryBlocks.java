package com.jerry.mekaf.common.registries;

import com.jerry.mekaf.common.attachments.containers.chemical.AFChemicalTanksBuilder;
import com.jerry.mekaf.common.attachments.containers.item.AFItemSlotsBuilder;
import com.jerry.mekaf.common.block.prefab.BlockAdvancedFactoryMachine.BlockAdvancedFactory;
import com.jerry.mekaf.common.content.blocktype.AdvancedFactory;
import com.jerry.mekaf.common.content.blocktype.AdvancedFactoryType;
import com.jerry.mekaf.common.item.block.machine.ItemBlockAdvancedFactory;
import com.jerry.mekaf.common.tile.factory.TileEntityLiquifyingFactory;
import com.jerry.mekaf.common.tile.factory.base.TileEntityAdvancedFactoryBase;

import com.jerry.mekmm.Mekmm;
import com.jerry.mekmm.common.util.MoreMachineEnumUtils;
import com.jerry.mekmm.common.util.MoreMachineUtils;

import mekanism.api.chemical.ChemicalResource;
import mekanism.api.tier.ITier;
import mekanism.common.attachments.containers.fluid.FluidTanksBuilder;
import mekanism.common.attachments.containers.item.ItemSlotsBuilder;
import mekanism.common.attachments.containers.type.ContainerType;
import mekanism.common.block.attribute.AttributeTier;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.recipe.lookup.cache.InputRecipeCache;
import mekanism.common.registration.impl.BlockDeferredRegister;
import mekanism.common.registration.impl.BlockRegistryObject;
import mekanism.common.tier.FactoryTier;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.transfer.item.ItemResource;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class AdvancedFactoryBlocks {

    private AdvancedFactoryBlocks() {}

    public static final BlockDeferredRegister AF_BLOCKS = new BlockDeferredRegister(Mekmm.MOD_ID);

    private static final Table<FactoryTier, AdvancedFactoryType, BlockRegistryObject<@NotNull BlockAdvancedFactory<?>, @NotNull ItemBlockAdvancedFactory>> AF_FACTORIES = HashBasedTable.create();

    static {
        // factories
        for (FactoryTier tier : MoreMachineUtils.getFactoryTier()) {
            for (AdvancedFactoryType type : MoreMachineEnumUtils.ADVANCED_FACTORY_TYPES) {
                AF_FACTORIES.put(tier, type, registerAdvancedFactory(AdvancedFactoryBlockTypes.getAdvancedFactory(tier, type)));
            }
        }
    }

    private static <TILE extends TileEntityAdvancedFactoryBase<?>> BlockRegistryObject<@NotNull BlockAdvancedFactory<?>, @NotNull ItemBlockAdvancedFactory> registerAdvancedFactory(AdvancedFactory<TILE> type) {
        FactoryTier tier = (FactoryTier) Objects.requireNonNull(type.get(AttributeTier.class)).tier();
        BlockRegistryObject<@NotNull BlockAdvancedFactory<?>, @NotNull ItemBlockAdvancedFactory> factory = registerTieredBlock(tier, "_" + type.getAdvancedFactoryType().getRegistryNameComponent() + "_factory", properties -> new BlockAdvancedFactory<>(type, properties), ItemBlockAdvancedFactory::new);
        factory.forItemHolder(holder -> {
            int processes = tier.processes;
            Predicate<ItemResource> recipeItemInputPredicate = switch (type.getAdvancedFactoryType()) {
                case OXIDIZING -> s -> MekanismRecipeType.OXIDIZING.getInputCache().containsInput(null, s);
                case DISSOLVING -> s -> MekanismRecipeType.DISSOLUTION.getInputCache().containsInputA(null, s);
                case PRESSURISED_REACTING -> s -> MekanismRecipeType.REACTION.getInputCache().containsInputA(null, s);
                case LIQUIFYING -> s -> TileEntityLiquifyingFactory.isValidInputStatic(s.toStack());
                case PIGMENT_EXTRACTING -> s -> MekanismRecipeType.PIGMENT_EXTRACTING.getInputCache().containsInput(null, s);
                case PAINTING -> s -> MekanismRecipeType.PAINTING.getInputCache().containsInputA(null, s);
                default -> null;
            };
            Predicate<ChemicalResource> recipeChemicalInputPredicate = switch (type.getAdvancedFactoryType()) {
                case DISSOLVING -> s -> MekanismRecipeType.DISSOLUTION.getInputCache().containsInputB(null, s);
                case WASHING -> s -> MekanismRecipeType.WASHING.getInputCache().containsInputB(null, s);
                case CRYSTALLIZING -> s -> MekanismRecipeType.CRYSTALLIZING.getInputCache().containsInput(null, s);
                case PRESSURISED_REACTING -> s -> MekanismRecipeType.REACTION.getInputCache().containsInputC(null, s);
                case CENTRIFUGING -> s -> MekanismRecipeType.CENTRIFUGING.getInputCache().containsInput(null, s);
                case PAINTING -> s -> MekanismRecipeType.PAINTING.getInputCache().containsInputB(null, s);
                default -> null;
            };
            switch (type.getAdvancedFactoryType()) {
                // 没问题
                case OXIDIZING -> holder
                        .addAttachmentOnlyContainers(ContainerType.CHEMICAL, () -> AFChemicalTanksBuilder.builder()
                                // 化学品输出（多个）
                                .addOutputFactoryTank(processes, TileEntityAdvancedFactoryBase.MAX_CHEMICAL * tier.processes)
                                .build())
                        .addAttachmentOnlyContainers(ContainerType.ITEM, () -> AFItemSlotsBuilder.builder()
                                // 物品输入（多个）
                                .addInputFactorySlots(processes, recipeItemInputPredicate)
                                .addEnergy()
                                .build());
                // 输入储罐错位，输出储罐气体消失
                case DISSOLVING -> holder
                        .addAttachmentOnlyContainers(ContainerType.CHEMICAL, () -> AFChemicalTanksBuilder.builder()
                                // 化学品输入
                                .addBasic(TileEntityAdvancedFactoryBase.MAX_CHEMICAL * processes, recipeChemicalInputPredicate)
                                // 化学品输出（多个）
                                .addOutputFactoryTank(processes, TileEntityAdvancedFactoryBase.MAX_CHEMICAL * processes)
                                .build())
                        .addAttachmentOnlyContainers(ContainerType.ITEM, () -> AFItemSlotsBuilder.builder()
                                .addInputFactorySlots(processes, recipeItemInputPredicate)
                                .addChemicalFillOrConvertSlot(0)
                                .addEnergy()
                                .build());
                // 没问题
                case WASHING -> holder
                        .addAttachmentOnlyContainers(ContainerType.FLUID, () -> FluidTanksBuilder.builder()
                                .addBasic(TileEntityAdvancedFactoryBase.MAX_FLUID * processes * processes, MekanismRecipeType.WASHING, InputRecipeCache.FluidChemical::containsInputA)
                                .build())
                        .addAttachmentOnlyContainers(ContainerType.CHEMICAL, () -> AFChemicalTanksBuilder.builder()
                                .addInputFactoryTank(processes, TileEntityAdvancedFactoryBase.MAX_CHEMICAL * processes, recipeChemicalInputPredicate)
                                .addOutputFactoryTank(processes, TileEntityAdvancedFactoryBase.MAX_CHEMICAL * processes)
                                .build())
                        .addAttachmentOnlyContainers(ContainerType.ITEM, () -> ItemSlotsBuilder.builder()
                                .addFluidFillSlot(0)
                                .addOutput()
                                .addEnergy()
                                .build());
                // 使用工作台合成升级机器导致能量槽错位（mek原生bug）
                case PRESSURISED_REACTING -> holder
                        .addAttachmentOnlyContainers(ContainerType.FLUID, () -> FluidTanksBuilder.builder()
                                .addBasic(TileEntityAdvancedFactoryBase.MAX_FLUID * processes, MekanismRecipeType.REACTION, InputRecipeCache.ItemFluidChemical::containsInputB)
                                .build())
                        .addAttachmentOnlyContainers(ContainerType.CHEMICAL, () -> AFChemicalTanksBuilder.builder()
                                .addBasic(TileEntityAdvancedFactoryBase.MAX_CHEMICAL * processes, recipeChemicalInputPredicate)
                                .addBasic(TileEntityAdvancedFactoryBase.MAX_CHEMICAL * processes)
                                .build())
                        .addAttachmentOnlyContainers(ContainerType.ITEM, () -> ItemSlotsBuilder.builder()
                                .addBasicFactorySlots(processes, recipeItemInputPredicate)
                                .addEnergy()
                                .build());
                // 使用工作台合成升级机器导致能量槽错位（mek原生bug）
                case CRYSTALLIZING -> holder.addAttachmentOnlyContainers(ContainerType.CHEMICAL, () -> AFChemicalTanksBuilder.builder()
                        .addInputFactoryTank(tier.processes, TileEntityAdvancedFactoryBase.MAX_CHEMICAL * tier.processes, recipeChemicalInputPredicate)
                        .build()).addAttachmentOnlyContainers(ContainerType.ITEM, () -> AFItemSlotsBuilder.builder()
                                .addOutputFactorySlots(tier.processes)
                                .addEnergy()
                                .build());
                // 没问题
                case CENTRIFUGING -> holder
                        .addAttachmentOnlyContainers(ContainerType.CHEMICAL, () -> AFChemicalTanksBuilder.builder()
                                .addInputFactoryTank(processes, TileEntityAdvancedFactoryBase.MAX_CHEMICAL * processes, recipeChemicalInputPredicate)
                                .addOutputFactoryTank(processes, TileEntityAdvancedFactoryBase.MAX_CHEMICAL * processes)
                                .build())
                        .addAttachmentOnlyContainers(ContainerType.ITEM, () -> ItemSlotsBuilder.builder()
                                .addEnergy()
                                .build());
                // 偶现升级后槽位不可以的情况
                case LIQUIFYING -> holder
                        .addAttachmentOnlyContainers(ContainerType.FLUID, () -> FluidTanksBuilder.builder()
                                .addBasic(TileEntityLiquifyingFactory.MAX_FLUID * processes)
                                .build())
                        .addAttachmentOnlyContainers(ContainerType.ITEM, () -> ItemSlotsBuilder.builder()
                                .addBasicFactorySlots(processes, recipeItemInputPredicate)
                                .addEnergy()
                                .build());
                case PIGMENT_EXTRACTING -> holder
                        .addAttachmentOnlyContainers(ContainerType.CHEMICAL, () -> AFChemicalTanksBuilder.builder()
                                .addOutputFactoryTank(processes, TileEntityAdvancedFactoryBase.MAX_CHEMICAL * tier.processes)
                                .build())
                        .addAttachmentOnlyContainers(ContainerType.ITEM, () -> AFItemSlotsBuilder.builder()
                                .addInputFactorySlots(processes, recipeItemInputPredicate)
                                .addEnergy()
                                .build());
                case PAINTING -> holder
                        .addAttachmentOnlyContainers(ContainerType.CHEMICAL, () -> AFChemicalTanksBuilder.builder()
                                .addInputFactoryTank(tier.processes, TileEntityAdvancedFactoryBase.MAX_CHEMICAL * tier.processes, recipeChemicalInputPredicate)
                                .build())
                        .addAttachmentOnlyContainers(ContainerType.ITEM, () -> ItemSlotsBuilder.builder()
                                .addBasicFactorySlots(processes, recipeItemInputPredicate)
                                .addEnergy()
                                .build());
            }
        });
        return factory;
    }

    private static <BLOCK extends Block, ITEM extends BlockItem> BlockRegistryObject<BLOCK, ITEM> registerTieredBlock(ITier tier, String suffix,
                                                                                                                      Function<BlockBehaviour.Properties, ? extends BLOCK> blockSupplier, BiFunction<BLOCK, Item.Properties, ITEM> itemCreator) {
        return AF_BLOCKS.register(tier.getBaseTier().getLowerName() + suffix, blockSupplier, itemCreator);
    }

    /**
     * Retrieves a Factory with a defined tier and recipe type.
     *
     * @param tier - tier to add to the Factory
     * @param type - recipe type to add to the Factory
     * @return factory with defined tier and recipe type
     */
    public static BlockRegistryObject<@NotNull BlockAdvancedFactory<?>, @NotNull ItemBlockAdvancedFactory> getAdvancedFactory(@NotNull FactoryTier tier, @NotNull AdvancedFactoryType type) {
        return AF_FACTORIES.get(tier, type);
    }

    @SuppressWarnings("unchecked")
    public static BlockRegistryObject<@NotNull BlockAdvancedFactory<?>, @NotNull ItemBlockAdvancedFactory>[] getAdvancedFactoryBlocks() {
        return AF_FACTORIES.values().toArray(new BlockRegistryObject[0]);
    }
}
