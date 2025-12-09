package com.jerry.meklm.common.item.block;

import mekanism.api.text.EnumColor;
import mekanism.common.MekanismLang;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.block.prefab.BlockTile.BlockTileModel;
import mekanism.common.capabilities.ItemCapabilityWrapper.ItemCapability;
import mekanism.common.content.blocktype.Machine;
import mekanism.common.util.StorageUtils;
import mekanism.common.util.text.TextUtils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import com.jerry.meklm.common.capabilities.chemical.item.LargeChemicalTankContentsHandler;
import com.jerry.meklm.common.tier.MidChemicalTankTier;
import com.jerry.meklm.common.tile.TileEntityMidChemicalTank;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ItemBlockMidChemicalTank extends ItemBlockLargeChemicalTank<TileEntityMidChemicalTank> {

    public ItemBlockMidChemicalTank(BlockTileModel<TileEntityMidChemicalTank, Machine<TileEntityMidChemicalTank>> block) {
        super(block);
    }

    @Override
    public MidChemicalTankTier getTier() {
        return Attribute.getTier(getBlock(), MidChemicalTankTier.class);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Level world, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        MidChemicalTankTier tier = getTier();
        StorageUtils.addStoredSubstance(stack, tooltip, false);
        tooltip.add(MekanismLang.CAPACITY_MB.translateColored(EnumColor.INDIGO, EnumColor.GRAY, TextUtils.format(tier.getStorage())));
        super.appendHoverText(stack, world, tooltip, flag);
    }

    @Override
    protected void gatherCapabilities(List<ItemCapability> capabilities, ItemStack stack, CompoundTag nbt) {
        super.gatherCapabilities(capabilities, stack, nbt);
        capabilities.add(LargeChemicalTankContentsHandler.create(getTier()));
    }
}
