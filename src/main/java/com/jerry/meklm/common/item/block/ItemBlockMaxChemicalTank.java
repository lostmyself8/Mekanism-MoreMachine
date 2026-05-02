package com.jerry.meklm.common.item.block;

import com.jerry.meklm.common.tier.MaxChemicalTankTier;
import com.jerry.meklm.common.tile.TileEntityMaxChemicalTank;

import mekanism.api.text.EnumColor;
import mekanism.common.MekanismLang;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.block.prefab.BlockTile.BlockTileModel;
import mekanism.common.content.blocktype.Machine;
import mekanism.common.util.StorageUtils;
import mekanism.common.util.text.TextUtils;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ItemBlockMaxChemicalTank extends ItemBlockLargeChemicalTank<TileEntityMaxChemicalTank> {

    public ItemBlockMaxChemicalTank(BlockTileModel<TileEntityMaxChemicalTank, Machine<TileEntityMaxChemicalTank>> block, Properties properties) {
        super(block, properties);
    }

    @Override
    public MaxChemicalTankTier getTier() {
        return Attribute.getTier(getBlock(), MaxChemicalTankTier.class);
    }

    @Override
    @Deprecated
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull TooltipDisplay tooltipDisplay, @NotNull Consumer<Component> tooltipAdder, @NotNull TooltipFlag flag) {
        MaxChemicalTankTier tier = getTier();
        StorageUtils.addStoredSubstance(stack, tooltipAdder, false);
        tooltipAdder.accept(MekanismLang.CAPACITY_MB.translateColored(EnumColor.INDIGO, EnumColor.GRAY, TextUtils.format(tier.getStorage())));
        super.appendHoverText(stack, context, tooltipDisplay, tooltipAdder, flag);
    }
}
