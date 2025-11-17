package com.jerry.mekmm.common.content.blocktype;

import com.jerry.mekmm.common.block.attribute.AttributeMoreMachineFactoryType;
import com.jerry.mekmm.common.content.blocktype.MoreMachineMachine.MoreMachineFactoryMachine;
import com.jerry.mekmm.common.registries.MoreMachineBlocks;
import com.jerry.mekmm.common.registries.MoreMachineContainerTypes;
import com.jerry.mekmm.common.tile.factory.TileEntityMoreMachineFactory;

import mekanism.common.MekanismLang;
import mekanism.common.block.attribute.*;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.lib.math.Pos3D;
import mekanism.common.registration.impl.ContainerTypeRegistryObject;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.common.tier.FactoryTier;
import mekanism.common.util.EnumUtils;

import net.minecraft.core.particles.ParticleTypes;

import java.util.function.Supplier;

public class MoreMachineFactory<TILE extends TileEntityMoreMachineFactory<?>> extends MoreMachineFactoryMachine<TILE> {

    private final MoreMachineFactoryMachine<?> origMachine;

    public MoreMachineFactory(Supplier<TileEntityTypeRegistryObject<TILE>> tileEntityRegistrar, Supplier<ContainerTypeRegistryObject<? extends MekanismContainer>> containerRegistrar,
                              MoreMachineFactoryMachine<?> origMachine, FactoryTier tier) {
        super(tileEntityRegistrar, MekanismLang.DESCRIPTION_FACTORY, origMachine.getMMFactoryType());
        this.origMachine = origMachine;
        setMachineData(tier);
        add(new AttributeGui(containerRegistrar, null), new AttributeTier<>(tier));

        if (tier.ordinal() < EnumUtils.FACTORY_TIERS.length - 1) {
            add(new AttributeUpgradeable(() -> MoreMachineBlocks.getMoreMachineFactory(EnumUtils.FACTORY_TIERS[tier.ordinal() + 1], origMachine.getMMFactoryType())));
        }
    }

    private void setMachineData(FactoryTier tier) {
        setFrom(origMachine, AttributeSound.class, AttributeMoreMachineFactoryType.class, AttributeUpgradeSupport.class);
        AttributeEnergy origEnergy = origMachine.get(AttributeEnergy.class);
        if (origEnergy != null) {
            add(new AttributeEnergy(origEnergy::getUsage, () -> origEnergy.getConfigStorage().multiply(0.5).max(origEnergy.getUsage()).multiply(tier.processes)));
        }
    }

    public static class MoreMachineFactoryBuilder<FACTORY extends MoreMachineFactory<TILE>, TILE extends TileEntityMoreMachineFactory<?>, T extends MoreMachineMachineBuilder<FACTORY, TILE, T>>
                                                 extends BlockTileBuilder<FACTORY, TILE, T> {

        protected MoreMachineFactoryBuilder(FACTORY holder) {
            super(holder);
        }

        @SuppressWarnings("unchecked")
        public static <TILE extends TileEntityMoreMachineFactory<?>> MoreMachineFactoryBuilder<MoreMachineFactory<TILE>, TILE, ?> createMMFactory(Supplier<?> tileEntityRegistrar, MoreMachineFactoryType type,
                                                                                                                                                  FactoryTier tier) {
            // this is dirty but unfortunately necessary for things to play right
            MoreMachineFactoryBuilder<MoreMachineFactory<TILE>, TILE, ?> builder = new MoreMachineFactoryBuilder<>(new MoreMachineFactory<>((Supplier<TileEntityTypeRegistryObject<TILE>>) tileEntityRegistrar,
                    () -> MoreMachineContainerTypes.MM_FACTORY, type.getBaseMachine(), tier));
            // Note, we can't just return the builder here as then it gets all confused about object types, so we just
            // assign the value here, and then return the builder itself as it is the same object
            builder.withComputerSupport(tier, type.getRegistryNameComponentCapitalized() + "Factory");
            builder.withCustomShape(MoreMachineBlockShapes.getShape(tier, type));
            builder.replace(new AttributeParticleFX().addDense(ParticleTypes.SMOKE, 5, rand -> new Pos3D(
                    rand.nextFloat() * 0.7F - 0.3F,
                    rand.nextFloat() * 0.1F + 0.7F,
                    rand.nextFloat() * 0.7F - 0.3F)));
            return builder;
        }
    }
}
