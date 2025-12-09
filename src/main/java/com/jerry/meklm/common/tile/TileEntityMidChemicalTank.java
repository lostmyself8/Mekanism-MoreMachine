package com.jerry.meklm.common.tile;

import mekanism.api.providers.IBlockProvider;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.resolver.manager.ChemicalHandlerManager;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.util.WorldUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;

import com.jerry.meklm.common.capabilities.chemical.LargeChemicalTankChemicalTank;
import com.jerry.meklm.common.tier.MidChemicalTankTier;
import com.jerry.meklm.common.tile.prefab.TileEntityLargeChemicalTank;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public class TileEntityMidChemicalTank extends TileEntityLargeChemicalTank<MidChemicalTankTier> {

    public TileEntityMidChemicalTank(IBlockProvider blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state);
    }

    @Override
    protected void presetVariables() {
        super.presetVariables();
        tier = Attribute.getTier(getBlockType(), MidChemicalTankTier.class);
        if (tier != null) {
            chemicalTank = LargeChemicalTankChemicalTank.create(tier, this);
        }
    }

    @Override
    protected BlockEntity fromTile() {
        return WorldUtils.getTileEntity(getLevel(), getBlockPos().above(1));
    }

    @Override
    public @NotNull <T> LazyOptional<T> getOffsetCapabilityIfEnabled(@NotNull Capability<T> capability, Direction side, @NotNull Vec3i offset) {
        Field gasField, infusionField, pigmentField, slurryField;
        try {
            gasField = TileEntityMekanism.class.getDeclaredField("gasHandlerManager");
            gasField.setAccessible(true);
            infusionField = TileEntityMekanism.class.getDeclaredField("infusionHandlerManager");
            infusionField.setAccessible(true);
            pigmentField = TileEntityMekanism.class.getDeclaredField("pigmentHandlerManager");
            pigmentField.setAccessible(true);
            slurryField = TileEntityMekanism.class.getDeclaredField("slurryHandlerManager");
            slurryField.setAccessible(true);
            if (capability == Capabilities.GAS_HANDLER) {
                return ((ChemicalHandlerManager.GasHandlerManager) (gasField.get(this))).resolve(capability, side);
            } else if (capability == Capabilities.INFUSION_HANDLER) {
                return ((ChemicalHandlerManager.InfusionHandlerManager) (infusionField.get(this))).resolve(capability, side);
            } else if (capability == Capabilities.PIGMENT_HANDLER) {
                return ((ChemicalHandlerManager.PigmentHandlerManager) (pigmentField.get(this))).resolve(capability, side);
            } else if (capability == Capabilities.SLURRY_HANDLER) {
                return ((ChemicalHandlerManager.SlurryHandlerManager) (slurryField.get(this))).resolve(capability, side);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        if (capability == ForgeCapabilities.ITEM_HANDLER) {
            return itemHandlerManager.resolve(capability, side);
        }
        return getCapability(capability, side);
    }

    @Override
    public boolean isOffsetCapabilityDisabled(@NotNull Capability<?> capability, Direction side, @NotNull Vec3i offset) {
        if (capability == Capabilities.GAS_HANDLER) {
            return notChemicalPort(side, offset);
        } else if (capability == ForgeCapabilities.ITEM_HANDLER) {
            return notItemPort(side, offset);
        }
        return notChemicalPort(side, offset) && notItemPort(side, offset);
    }

    private boolean notChemicalPort(Direction side, Vec3i offset) {
        if (offset.equals(new Vec3i(0, 1, 0))) {
            return side != Direction.UP;
        }
        return true;
    }

    private boolean notItemPort(Direction side, Vec3i offset) {
        if (offset.equals(new Vec3i(0, 1, 0))) {
            return side != Direction.UP;
        }
        return true;
    }
}
