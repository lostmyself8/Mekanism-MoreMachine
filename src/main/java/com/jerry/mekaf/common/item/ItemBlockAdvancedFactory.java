package com.jerry.mekaf.common.item;

import com.jerry.mekaf.common.block.attribute.AttributeAdvancedFactoryType;

import mekanism.api.text.EnumColor;
import mekanism.common.MekanismLang;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.block.prefab.BlockTile;
import mekanism.common.item.block.machine.ItemBlockMachine;
import mekanism.common.tier.FactoryTier;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ItemBlockAdvancedFactory extends ItemBlockMachine {

    public ItemBlockAdvancedFactory(BlockTile<?, ?> block) {
        super(block);
    }

    @Override
    public FactoryTier getTier() {
        return Attribute.getTier(getBlock(), FactoryTier.class);
    }

    @Override
    protected void addTypeDetails(@NotNull ItemStack stack, Level world, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        // Should always be present but validate it just in case
        Attribute.ifPresent(getBlock(), AttributeAdvancedFactoryType.class, attribute -> tooltip.add(MekanismLang.FACTORY_TYPE.translateColored(EnumColor.INDIGO, EnumColor.GRAY,
                attribute.getAdvancedFactoryType())));
        super.addTypeDetails(stack, world, tooltip, flag);
    }
}
