package com.jerry.meklm.common.item.block;

import com.jerry.meklm.common.tier.MidChemicalTankTier;
import com.jerry.meklm.common.tile.TileEntityMidChemicalTank;

import mekanism.api.text.EnumColor;
import mekanism.common.MekanismLang;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.block.prefab.BlockTile.BlockTileModel;
import mekanism.common.content.blocktype.Machine;
import mekanism.common.util.ItemAccessUtils;
import mekanism.common.util.StorageUtils;
import mekanism.common.util.text.TextUtils;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ItemBlockMidChemicalTank extends ItemBlockLargeChemicalTank<TileEntityMidChemicalTank> {

    public ItemBlockMidChemicalTank(BlockTileModel<TileEntityMidChemicalTank, Machine<TileEntityMidChemicalTank>> block, Properties properties) {
        super(block, properties);
    }

    @Override
    public MidChemicalTankTier getTier() {
        return Attribute.getTier(getBlock(), MidChemicalTankTier.class);
    }

    @Override
    @Deprecated
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull TooltipDisplay tooltipDisplay, @NotNull Consumer<Component> tooltipAdder, @NotNull TooltipFlag flag) {
        MidChemicalTankTier tier = getTier();
        StorageUtils.addStoredSubstance(ItemAccessUtils.sideEffectFreeAccess(stack), tooltipAdder, false);
        tooltipAdder.accept(MekanismLang.CAPACITY_MB.translateColored(EnumColor.INDIGO, EnumColor.GRAY, TextUtils.format(tier.getStorage())));
        super.appendHoverText(stack, context, tooltipDisplay, tooltipAdder, flag);
    }
}
