package com.jerry.mekaf.common.item.block.machine;

import com.jerry.mekaf.common.attachments.component.AdvancedFactoryAttachedSideConfig;
import com.jerry.mekaf.common.block.attribute.AttributeAdvancedFactoryType;
import com.jerry.mekaf.common.block.prefab.BlockAdvancedFactoryMachine.BlockAdvancedFactory;

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

public class ItemBlockAdvancedFactory extends ItemBlockTooltip<BlockTile<?, ?>> {

    // 这里只是预设，真正控制侧面配置应当在方块实体中处理
    // 预设可以不遵守方块实体中设置的配置，但人为调控后可能无法恢复到预设状态
    private static AttachedSideConfig getSideConfig(BlockAdvancedFactory<?> block) {
        return switch (Attribute.getOrThrow(block.builtInRegistryHolder(), AttributeAdvancedFactoryType.class).getAdvancedFactoryType()) {
            case OXIDIZING, PIGMENT_EXTRACTING -> AdvancedFactoryAttachedSideConfig.CHEMICAL_OUT_NO_ITEM_OUT_MACHINE;
            case DISSOLVING -> AdvancedFactoryAttachedSideConfig.DISSOLUTION;
            case WASHING -> AttachedSideConfig.WASHER;
            case PRESSURISED_REACTING -> AttachedSideConfig.REACTION;
            case CRYSTALLIZING -> AdvancedFactoryAttachedSideConfig.CRYSTALLIZER;
            case CENTRIFUGING -> AdvancedFactoryAttachedSideConfig.CENTRIFUGE;
            case LIQUIFYING -> AttachedSideConfig.LIQUIFIER;
            case PAINTING -> AttachedSideConfig.PAINTING;
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
        return Attribute.getTierNN(getBlock(), FactoryTier.class);
    }

    @Override
    protected void addTypeDetails(@NotNull ItemStack stack, @NotNull ItemAccess itemAccess, @NotNull TooltipContext context, @NotNull TooltipDisplay tooltipDisplay, @NotNull Consumer<Component> tooltipAdder, @NotNull TooltipFlag flag) {
        // Should always be present but validate it just in case
        AttributeAdvancedFactoryType factoryType = Attribute.get(getBlock(), AttributeAdvancedFactoryType.class);
        if (factoryType != null) {
            tooltipAdder.accept(MekanismLang.FACTORY_TYPE.translateColored(EnumColor.INDIGO, EnumColor.GRAY, factoryType.getAdvancedFactoryType()));
        }
        super.addTypeDetails(stack, itemAccess, context, tooltipDisplay, tooltipAdder, flag);
    }
}
