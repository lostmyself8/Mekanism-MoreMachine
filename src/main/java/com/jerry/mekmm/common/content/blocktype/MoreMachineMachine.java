package com.jerry.mekmm.common.content.blocktype;

import com.jerry.mekaf.common.block.attribute.AttributeAdvancedFactoryType;
import com.jerry.mekaf.common.content.blocktype.AdvancedFactoryType;
import com.jerry.mekaf.common.registries.AdvancedFactoryBlocks;

import com.jerry.mekmm.common.block.attribute.AttributeMoreMachineFactoryType;
import com.jerry.mekmm.common.registries.MoreMachineBlocks;

import mekanism.api.Upgrade;
import mekanism.api.text.ILangEntry;
import mekanism.common.block.attribute.*;
import mekanism.common.content.blocktype.BlockTypeTile;
import mekanism.common.lib.math.Pos3D;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.common.tier.FactoryTier;
import mekanism.common.tile.base.TileEntityMekanism;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;

import java.util.EnumSet;
import java.util.Objects;
import java.util.function.Supplier;

public class MoreMachineMachine<TILE extends TileEntityMekanism> extends BlockTypeTile<TILE> {

    public MoreMachineMachine(Supplier<TileEntityTypeRegistryObject<TILE>> tileEntityRegistrar, ILangEntry description) {
        super(tileEntityRegistrar, description);
        add(new AttributeParticleFX()
                .add(ParticleTypes.SMOKE, rand -> new Pos3D(rand.nextFloat() * 0.6F - 0.3F, rand.nextFloat() * 6.0F / 16.0F, 0.52))
                .add(DustParticleOptions.REDSTONE, rand -> new Pos3D(rand.nextFloat() * 0.6F - 0.3F, rand.nextFloat() * 6.0F / 16.0F, 0.52)));
        add(Attributes.ACTIVE_LIGHT, new AttributeStateFacing(), Attributes.INVENTORY, Attributes.SECURITY, Attributes.REDSTONE, Attributes.COMPARATOR);
        add(new AttributeUpgradeSupport(EnumSet.of(Upgrade.SPEED, Upgrade.ENERGY, Upgrade.MUFFLING)));
    }

    public static class MoreMachineFactoryMachine<TILE extends TileEntityMekanism> extends MoreMachineMachine<TILE> {

        public MoreMachineFactoryMachine(Supplier<TileEntityTypeRegistryObject<TILE>> tileEntitySupplier, ILangEntry description, MoreMachineFactoryType factoryType) {
            super(tileEntitySupplier, description);
            add(new AttributeMoreMachineFactoryType(factoryType), new AttributeUpgradeable(() -> MoreMachineBlocks.getMoreMachineFactory(FactoryTier.BASIC, getMMFactoryType())));
        }

        public MoreMachineFactoryMachine(Supplier<TileEntityTypeRegistryObject<TILE>> tileEntitySupplier, ILangEntry description, AdvancedFactoryType factoryType) {
            super(tileEntitySupplier, description);
            add(new AttributeAdvancedFactoryType(factoryType), new AttributeUpgradeable(() -> AdvancedFactoryBlocks.getAdvancedFactory(FactoryTier.BASIC, getAdvancedFactoryType())));
        }

        public MoreMachineFactoryType getMMFactoryType() {
            return Objects.requireNonNull(get(AttributeMoreMachineFactoryType.class)).getMoreMachineFactoryType();
        }

        public AdvancedFactoryType getAdvancedFactoryType() {
            return Objects.requireNonNull(get(AttributeAdvancedFactoryType.class)).getAdvancedFactoryType();
        }
    }

    public static class MoreMachineMachineBuilder<MACHINE extends MoreMachineMachine<TILE>, TILE extends TileEntityMekanism, T extends MoreMachineMachineBuilder<MACHINE, TILE, T>> extends BlockTileBuilder<MACHINE, TILE, T> {

        protected MoreMachineMachineBuilder(MACHINE holder) {
            super(holder);
        }

        public static <TILE extends TileEntityMekanism> MoreMachineMachineBuilder<MoreMachineMachine<TILE>, TILE, ?> createMMMachine(Supplier<TileEntityTypeRegistryObject<TILE>> tileEntityRegistrar,
                                                                                                                                     ILangEntry description) {
            return new MoreMachineMachineBuilder<>(new MoreMachineMachine<>(tileEntityRegistrar, description));
        }

        public static <TILE extends TileEntityMekanism> MoreMachineMachineBuilder<MoreMachineFactoryMachine<TILE>, TILE, ?> createMMFactoryMachine(Supplier<TileEntityTypeRegistryObject<TILE>> tileEntityRegistrar,
                                                                                                                                                   ILangEntry description, MoreMachineFactoryType factoryType) {
            return new MoreMachineMachineBuilder<>(new MoreMachineFactoryMachine<>(tileEntityRegistrar, description, factoryType));
        }

        public static <TILE extends TileEntityMekanism> MoreMachineMachineBuilder<MoreMachineFactoryMachine<TILE>, TILE, ?> createAdvancedFactoryMachine(Supplier<TileEntityTypeRegistryObject<TILE>> tileEntityRegistrar,
                                                                                                                                                         ILangEntry description, AdvancedFactoryType factoryType) {
            return new MoreMachineMachineBuilder<>(new MoreMachineFactoryMachine<>(tileEntityRegistrar, description, factoryType));
        }
    }
}
