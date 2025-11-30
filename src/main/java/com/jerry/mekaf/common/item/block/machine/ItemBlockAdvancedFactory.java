package com.jerry.mekaf.common.item.block.machine;

import com.jerry.mekaf.common.attachments.component.AdvancedFactoryAttachedSideConfig;
import com.jerry.mekaf.common.block.attribute.AttributeAdvancedFactoryType;
import com.jerry.mekaf.common.block.prefab.BlockAdvancedFactoryMachine.BlockAdvancedFactory;

import mekanism.api.text.EnumColor;
import mekanism.common.MekanismLang;
import mekanism.common.attachments.component.AttachedEjector;
import mekanism.common.attachments.component.AttachedSideConfig;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.block.prefab.BlockTile;
import mekanism.common.item.block.ItemBlockTooltip;
import mekanism.common.registries.MekanismDataComponents;
import mekanism.common.tier.FactoryTier;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ItemBlockAdvancedFactory extends ItemBlockTooltip<BlockTile<?, ?>> {

    private static AttachedSideConfig getSideConfig(BlockAdvancedFactory<?> block) {
        return switch (Attribute.getOrThrow(block.builtInRegistryHolder(), AttributeAdvancedFactoryType.class).getAdvancedFactoryType()) {
            case OXIDIZING -> AttachedSideConfig.CHEMICAL_OUT_MACHINE;
            case DISSOLVING -> AttachedSideConfig.DISSOLUTION;
            case CHEMICAL_INFUSING -> AdvancedFactoryAttachedSideConfig.CHEMICAL_INFUSING;
            case WASHING -> AttachedSideConfig.WASHER;
            case PRESSURISED_REACTING -> AttachedSideConfig.REACTION;
            case CRYSTALLIZING -> AttachedSideConfig.CRYSTALLIZER;
            case CENTRIFUGING -> AttachedSideConfig.CENTRIFUGE;
            case LIQUIFYING -> AttachedSideConfig.LIQUIFIER;
            // case SOLAR_NEUTRON_ACTIVATING -> AttachedSideConfig.SNA;
        };
    }

    public ItemBlockAdvancedFactory(BlockAdvancedFactory<?> block, Properties properties) {
        super(block, true, properties
                .component(MekanismDataComponents.SORTING, false)
                .component(MekanismDataComponents.EJECTOR, AttachedEjector.DEFAULT)
                .component(MekanismDataComponents.SIDE_CONFIG, getSideConfig(block)));
    }

    @Override
    public FactoryTier getTier() {
        return Attribute.getTier(getBlock(), FactoryTier.class);
    }

    @Override
    protected void addTypeDetails(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        // Should always be present but validate it just in case
        AttributeAdvancedFactoryType factoryType = Attribute.get(getBlock(), AttributeAdvancedFactoryType.class);
        if (factoryType != null) {
            tooltip.add(MekanismLang.FACTORY_TYPE.translateColored(EnumColor.INDIGO, EnumColor.GRAY, factoryType.getAdvancedFactoryType()));
        }
        super.addTypeDetails(stack, context, tooltip, flag);
    }
}
