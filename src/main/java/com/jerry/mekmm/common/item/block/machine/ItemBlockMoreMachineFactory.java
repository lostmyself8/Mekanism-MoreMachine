package com.jerry.mekmm.common.item.block.machine;

import com.jerry.mekmm.common.block.attribute.MoreMachineAttributeFactoryType;
import com.jerry.mekmm.common.block.prefab.BlockMoreFactoryMachine;

import mekanism.api.text.EnumColor;
import mekanism.common.MekanismLang;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.block.prefab.BlockTile;
import mekanism.common.component.component.AttachedEjector;
import mekanism.common.component.component.AttachedSideConfig;
import mekanism.common.item.block.ItemBlockTooltip;
import mekanism.common.registries.MekanismDataComponents;
import mekanism.common.tier.FactoryTier;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.neoforged.neoforge.transfer.access.ItemAccess;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ItemBlockMoreMachineFactory extends ItemBlockTooltip<BlockTile<?, ?>> {

    private static AttachedSideConfig getSideConfig(BlockMoreFactoryMachine.BlockMoreMachineFactory<?> block) {
        return switch (Attribute.getOrThrow(block.builtInRegistryHolder(), MoreMachineAttributeFactoryType.class).getMoreMachineFactoryType()) {
            // case COMPRESSING, INFUSING -> AttachedSideConfig.ADVANCED_MACHINE;
            // case COMBINING -> AttachedSideConfig.EXTRA_MACHINE;
            // case PURIFYING, INJECTING -> AttachedSideConfig.ADVANCED_MACHINE_INPUT_ONLY;
            case CNC_STAMPING, PRESSING -> AttachedSideConfig.EXTRA_MACHINE;
            case RECYCLING, CNC_LATHING, CNC_ROLLING_MILL -> AttachedSideConfig.ELECTRIC_MACHINE;
            case PLANTING_STATION, REPLICATING -> AttachedSideConfig.ADVANCED_MACHINE_INPUT_ONLY;
        };
    }

    public ItemBlockMoreMachineFactory(BlockMoreFactoryMachine.BlockMoreMachineFactory<?> block, Properties properties) {
        super(block, true, properties
                .component(MekanismDataComponents.SORTING, false)
                .component(MekanismDataComponents.EJECTOR, AttachedEjector.DEFAULT)
                .component(MekanismDataComponents.SIDE_CONFIG, getSideConfig(block)));
    }

    @Override
    public FactoryTier getTier() {
        return Attribute.getTierNN(getBlock(), FactoryTier.class);
    }

    @Override
    protected void addTypeDetails(@NotNull ItemStack stack, @NotNull ItemAccess itemAccess, @NotNull TooltipContext context, @NotNull TooltipDisplay tooltipDisplay, @NotNull Consumer<Component> tooltipAdder, @NotNull TooltipFlag flag) {
        // Should always be present but validate it just in case
        MoreMachineAttributeFactoryType factoryType = Attribute.get(getBlock(), MoreMachineAttributeFactoryType.class);
        if (factoryType != null) {
            tooltipAdder.accept(MekanismLang.FACTORY_TYPE.translateColored(EnumColor.INDIGO, EnumColor.GRAY, factoryType.getMoreMachineFactoryType()));
        }
        super.addTypeDetails(stack, itemAccess, context, tooltipDisplay, tooltipAdder, flag);
    }
}
