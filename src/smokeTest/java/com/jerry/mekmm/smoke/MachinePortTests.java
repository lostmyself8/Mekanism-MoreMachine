package com.jerry.mekmm.smoke;

import com.jerry.meklm.common.registries.LargeMachineBlocks;
import com.jerry.meklm.common.tile.machine.TileEntityLargeRotaryCondensentrator;

import com.jerry.mekmm.common.registries.MoreMachineBlocks;

import mekanism.api.AutomationType;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalIds;
import mekanism.api.chemical.ChemicalResource;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.block.attribute.AttributeHasBounding;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.tile.base.TileEntityMekanism;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import com.jerry.meklg.common.registries.LargeGeneratorBlocks;
import com.jerry.meklg.common.tile.generator.TileEntityLargeGasGenerator;

final class MachinePortTests {

    private static TileEntityMekanism place(GameTestHelper h, Block block, Direction facing) {
        BlockPos pos = h.absolutePos(new BlockPos(8, 2, 8));
        var state = Attribute.setFacing(block.defaultBlockState(), facing);
        h.getLevel().setBlockAndUpdate(pos, state);
        var bounding = Attribute.get(block, AttributeHasBounding.class);
        if (bounding != null) bounding.placeBoundingBlocks(h.getLevel(), pos, state);
        return (TileEntityMekanism) h.getLevel().getBlockEntity(pos);
    }

    private static void remove(GameTestHelper h, TileEntityMekanism tile) {
        var bounding = Attribute.get(tile.getBlockState().getBlock(), AttributeHasBounding.class);
        if (bounding != null) bounding.removeBoundingBlocks(h.getLevel(), tile.getBlockPos(), tile.getBlockState());
        h.getLevel().removeBlock(tile.getBlockPos(), false);
    }

    private static <T> T port(GameTestHelper h, TileEntityMekanism tile, BlockCapability<T, Direction> capability, Vec3i offset, Direction side) {
        T handler = h.getLevel().getCapability(capability, tile.getBlockPos().offset(offset), side);
        h.assertTrue(handler != null, "Missing " + capability.name() + " port: " + tile.getBlockState() + " offset=" + offset + " side=" + side);
        return handler;
    }

    private static TileEntityMekanism receiver(GameTestHelper h, BlockPos pos) {
        h.getLevel().setBlockAndUpdate(pos, MekanismBlocks.ULTIMATE_ENERGY_CUBE.get().defaultBlockState());
        var tile = (mekanism.common.tile.TileEntityEnergyCube) h.getLevel().getBlockEntity(pos);
        var config = tile.getConfig().getConfig(mekanism.common.lib.transmitter.TransmissionType.ENERGY);
        for (var side : mekanism.api.RelativeSide.values()) config.setDataType(mekanism.common.tile.component.config.DataType.INPUT, side);
        h.getLevel().invalidateCapabilities(pos);
        return tile;
    }

    static void gasProduction(GameTestHelper h, ResourceKey<Chemical> fuelKey) {
        var tile = (TileEntityLargeGasGenerator) place(h, LargeGeneratorBlocks.LARGE_GAS_BURNING_GENERATOR.get(), Direction.NORTH);
        var fuel = ChemicalResource.of(h.getLevel().registryAccess().getOrThrow(fuelKey));
        var standard = fuel.getFuel(h.getLevel().registryAccess());
        int rate = standard.maxBurnPerTick() * 64;
        long expectedEnergy = (long) rate * standard.energyDensity();
        var output = port(h, tile, Capabilities.ENERGY.block(), new Vec3i(0, 2, 0), Direction.UP);
        var input = port(h, tile, Capabilities.CHEMICAL.block(), new Vec3i(1, 1, 0), Direction.EAST);
        try (var tx = Transaction.openRoot()) {
            h.assertTrue(input.insert(fuel, 180_000, tx) == 180_000, "Cannot fill large gas generator through physical fuel port");
            tx.commit();
        }
        h.assertTrue(tile.getCachedFuel().maxBurnPerTick() == rate, "Large generator did not scale fuel burn rate");
        h.assertTrue(tile.getMaxOutput() == 2 * expectedEnergy, "Large generator has wrong output limit");
        h.runAfterDelay(1, () -> {
            // Reset to a full tank after the ordinary first tick, then isolate one complete production tick.
            tile.getEnergyContainer().setEnergy(0, null);
            tile.fuelTank.setContents(fuel, 180_000, null);
            try {
                var tick = TileEntityLargeGasGenerator.class.getDeclaredMethod("onUpdateServer", net.minecraft.server.level.ServerLevel.class);
                tick.setAccessible(true);
                tick.invoke(tile, h.getLevel());
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
            h.assertTrue(tile.getUsed() == rate, "Wrong large generator fuel consumption: " + tile.getUsed());
            h.assertTrue(tile.getProductionRate() == expectedEnergy && tile.getEnergyContainer().getAmountAsLong() == expectedEnergy,
                    "Wrong large generator production: " + tile.getProductionRate());
            h.assertTrue(tile.fuelTank.amountAsLong() == 180_000 - rate, "Incorrect fuel balance");
            try (var tx = Transaction.openRoot()) {
                h.assertTrue(output.extract((int) expectedEnergy, tx) == expectedEnergy, "Cannot extract power through top bounding port");
                tx.commit();
            }
            var receiverPos = tile.getBlockPos().above(3);
            receiver(h, receiverPos);
            h.runAfterDelay(8, () -> {
                var receiver = (TileEntityMekanism) h.getLevel().getBlockEntity(receiverPos);
                h.assertTrue(receiver.getEnergyContainer().getAmountAsLong() > 0, "Large gas generator did not automatically output energy");
                tile.fuelTank.setContents(ChemicalResource.EMPTY, 0, null);
                h.assertTrue(tile.getCachedFuel() == null && tile.getMaxOutput() > 0, "Empty fuel tank cannot drain stored power");
                System.out.println("MEKMM_GAS_SMOKE: " + fuelKey.identifier() + " burn=" + rate + " mB/t production=" + expectedEnergy + " FE/t");
                h.succeed();
            });
        });
    }

    static void heatOutput(GameTestHelper h) {
        var tile = place(h, LargeGeneratorBlocks.LARGE_HEAT_GENERATOR.get(), Direction.NORTH);
        Direction back = tile.getOppositeDirection();
        Vec3i offset = new Vec3i(back.getStepX(), 1, back.getStepZ());
        port(h, tile, Capabilities.ENERGY.block(), offset, back);
        tile.getEnergyContainer().setEnergy(1_000_000, null);
        var receiverPos = tile.getBlockPos().offset(offset).relative(back);
        receiver(h, receiverPos);
        h.runAfterDelay(8, () -> {
            var receiver = (TileEntityMekanism) h.getLevel().getBlockEntity(receiverPos);
            h.assertTrue(receiver.getEnergyContainer().getAmountAsLong() > 0, "Large heat generator did not output stored energy");
            h.succeed();
        });
    }

    static void rotaryPorts(GameTestHelper h, Direction facing) {
        var tile = (TileEntityLargeRotaryCondensentrator) place(h, LargeMachineBlocks.LARGE_ROTARY_CONDENSENTRATOR.get(), facing);
        var left = tile.getLeftSide();
        var right = tile.getRightSide();
        var back = tile.getOppositeDirection();
        var energy = port(h, tile, Capabilities.ENERGY.block(), back.getUnitVec3i(), back);
        var gasIn = port(h, tile, Capabilities.CHEMICAL.block(), left.getUnitVec3i().offset(back.getUnitVec3i()), back);
        var gasOut = port(h, tile, Capabilities.CHEMICAL.block(), new Vec3i(left.getStepX(), 1, left.getStepZ()), left);
        var fluidIn = port(h, tile, Capabilities.FLUID.block(), right.getUnitVec3i().offset(back.getUnitVec3i()), back);
        var fluidOut = port(h, tile, Capabilities.FLUID.block(), new Vec3i(right.getStepX(), 1, right.getStepZ()), right);
        var vapor = ChemicalResource.of(h.getLevel().registryAccess().getOrThrow(ChemicalIds.WATER_VAPOR));
        var water = FluidResource.of(Fluids.WATER);
        try (var tx = Transaction.openRoot()) {
            h.assertTrue(energy.insert(10_000, tx) == 10_000, "Rotary energy input rejected power");
            if (tile.getMode()) tile.nextMode();
            h.assertTrue(gasIn.insert(vapor, 100, tx) == 100, "Rotary chemical input rejected vapor");
            tx.commit();
        }
        h.runAfterDelay(5, () -> {
            h.assertTrue(tile.fluidTank.amountAsLong() > 0, "Rotary did not convert vapor into water");
            try (var tx = Transaction.openRoot()) {
                h.assertTrue(fluidOut.extract(water, 1, tx) == 1, "Rotary fluid output port rejected extraction");
                tile.chemicalTank.setContents(ChemicalResource.EMPTY, 0, tx);
                tile.fluidTank.setContents(FluidResource.EMPTY, 0, tx);
                tx.commit();
            }
            tile.nextMode();
            try (var tx = Transaction.openRoot()) {
                h.assertTrue(fluidIn.insert(water, 100, tx) == 100, "Rotary fluid input rejected water");
                tx.commit();
            }
            h.runAfterDelay(5, () -> {
                h.assertTrue(tile.chemicalTank.amountAsLong() > 0, "Rotary did not convert water into vapor");
                try (var tx = Transaction.openRoot()) {
                    h.assertTrue(gasOut.extract(vapor, 1, tx) == 1, "Rotary chemical output port rejected extraction");
                    tx.commit();
                }
                h.succeed();
            });
        });
    }

    static void allMachinePorts(GameTestHelper h, Direction facing) {
        for (var entry : java.util.List.of(LargeMachineBlocks.LARGE_CHEMICAL_INFUSER, LargeMachineBlocks.LARGE_ELECTROLYTIC_SEPARATOR,
                LargeMachineBlocks.LARGE_ANTIPROTONIC_NUCLEOSYNTHESIZER, LargeMachineBlocks.LARGE_PIGMENT_MIXER)) {
            var tile = place(h, entry.get(), facing);
            var back = tile.getOppositeDirection();
            var offset = back.getUnitVec3i();
            if (entry == LargeMachineBlocks.LARGE_ANTIPROTONIC_NUCLEOSYNTHESIZER || entry == LargeMachineBlocks.LARGE_PIGMENT_MIXER) {
                offset = offset.offset(tile.getLeftSide().getUnitVec3i());
            }
            var handler = port(h, tile, Capabilities.ENERGY.block(), offset, back);
            try (var tx = Transaction.openRoot()) {
                h.assertTrue(handler.insert(1, tx) == 1, "Large machine energy port rejected power");
                tx.commit();
            }
            remove(h, tile);
        }
        var planter = place(h, MoreMachineBlocks.PLANTING_STATION.get(), facing);
        ((com.jerry.mekmm.common.tile.machine.TileEntityPlantingStation) planter).getConfig()
                .getConfig(mekanism.common.lib.transmitter.TransmissionType.CHEMICAL)
                .setDataType(mekanism.common.tile.component.config.DataType.INPUT, mekanism.api.RelativeSide.TOP);
        port(h, planter, Capabilities.CHEMICAL.block(), new Vec3i(0, 1, 0), Direction.UP);
        remove(h, planter);
        for (var entry : java.util.List.of(LargeMachineBlocks.BASIC_MID_CHEMICAL_TANK, LargeMachineBlocks.BASIC_MAX_CHEMICAL_TANK)) {
            var tile = place(h, entry.get(), facing);
            ((com.jerry.meklm.common.tile.prefab.TileEntityLargeChemicalTank<?>) tile).getConfig()
                    .getConfig(mekanism.common.lib.transmitter.TransmissionType.CHEMICAL)
                    .setDataType(mekanism.common.tile.component.config.DataType.INPUT_OUTPUT, mekanism.api.RelativeSide.TOP);
            int height = entry == LargeMachineBlocks.BASIC_MID_CHEMICAL_TANK ? 1 : 2;
            port(h, tile, Capabilities.CHEMICAL.block(), new Vec3i(0, height, 0), Direction.UP);
            remove(h, tile);
        }
        var sna = place(h, LargeMachineBlocks.LARGE_SOLAR_NEUTRON_ACTIVATOR.get(), facing);
        var snaBack = sna.getOppositeDirection();
        port(h, sna, Capabilities.CHEMICAL.block(), snaBack.getUnitVec3i().offset(sna.getLeftSide().getUnitVec3i()), snaBack);
        remove(h, sna);
        var solar = place(h, LargeGeneratorBlocks.SOLAR_HEAT_GENERATOR.get(), facing);
        var solarBack = solar.getOppositeDirection();
        var solarOffset = solarBack.getUnitVec3i().multiply(3).offset(solar.getLeftSide().getUnitVec3i());
        port(h, solar, Capabilities.ENERGY.block(), solarBack.getUnitVec3i().multiply(3), solarBack);
        port(h, solar, Capabilities.CHEMICAL.block(), solarOffset.offset(0, 1, 0), solarBack);
        port(h, solar, Capabilities.FLUID.block(), solarOffset, solarBack);
        remove(h, solar);
        h.succeed();
    }
}
