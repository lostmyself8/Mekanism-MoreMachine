package com.jerry.mekaf.common.content.blocktype;

import com.jerry.mekaf.common.block.attribute.AttributeAdvancedFactoryType;
import com.jerry.mekaf.common.registries.AdvancedFactoryBlocks;
import com.jerry.mekaf.common.registries.AdvancedFactoryContainerTypes;
import com.jerry.mekaf.common.tile.factory.TileEntityAdvancedFactoryBase;

import com.jerry.mekmm.common.content.blocktype.MoreMachineMachine;
import com.jerry.mekmm.common.util.MoreMachineUtils;

import mekanism.api.math.MathUtils;
import mekanism.common.MekanismLang;
import mekanism.common.block.attribute.*;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.lib.math.Pos3D;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.registration.impl.ContainerTypeRegistryObject;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.common.tier.FactoryTier;

import net.minecraft.core.particles.ParticleTypes;

import java.util.function.Supplier;

public class AdvancedFactory<TILE extends TileEntityAdvancedFactoryBase<?>> extends MoreMachineMachine.MoreMachineFactoryMachine<TILE> {

    private final MoreMachineFactoryMachine<?> origMachine;

    public AdvancedFactory(Supplier<TileEntityTypeRegistryObject<TILE>> tileEntityRegistrar, Supplier<ContainerTypeRegistryObject<? extends MekanismContainer>> containerRegistrar,
                           MoreMachineFactoryMachine<?> origMachine, FactoryTier tier) {
        super(tileEntityRegistrar, MekanismLang.DESCRIPTION_FACTORY, origMachine.getAdvancedFactoryType());
        this.origMachine = origMachine;
        setMachineData(tier);
        add(new AttributeGui(containerRegistrar, null), new AttributeTier<>(tier));

        if (tier.ordinal() < MoreMachineUtils.getFactoryTier().length - 1) {
            add(new AttributeUpgradeable(() -> AdvancedFactoryBlocks.getAdvancedFactory(MoreMachineUtils.getFactoryTier()[tier.ordinal() + 1], origMachine.getAdvancedFactoryType())));
        }
    }

    private void setMachineData(FactoryTier tier) {
        setFrom(origMachine, AttributeSound.class, AttributeAdvancedFactoryType.class, AttributeUpgradeSupport.class);
        AttributeEnergy origEnergy = origMachine.get(AttributeEnergy.class);
        if (origEnergy != null) {
            // 相比于原版，这里将0.5的乘数去除
            add(new AttributeEnergy(origEnergy::getUsage, () -> MathUtils.clampToLong(Math.max(origEnergy.getConfigStorage(), origEnergy.getUsage()) * tier.processes)));
        }
    }

    public static class AdvancedFactoryBuilder<FACTORY extends AdvancedFactory<TILE>, TILE extends TileEntityAdvancedFactoryBase<?>, T extends MMMachineBuilder<FACTORY, TILE, T>>
                                              extends BlockTileBuilder<FACTORY, TILE, T> {

        protected AdvancedFactoryBuilder(FACTORY holder) {
            super(holder);
        }

        @SuppressWarnings("unchecked")
        public static <TILE extends TileEntityAdvancedFactoryBase<?>> AdvancedFactoryBuilder<AdvancedFactory<TILE>, TILE, ?> createAdvancedFactory(Supplier<?> tileEntityRegistrar, AdvancedFactoryType type,
                                                                                                                                                   FactoryTier tier) {
            // this is dirty but unfortunately necessary for things to play right
            AdvancedFactoryBuilder<AdvancedFactory<TILE>, TILE, ?> builder = new AdvancedFactoryBuilder<>(new AdvancedFactory<>((Supplier<TileEntityTypeRegistryObject<TILE>>) tileEntityRegistrar,
                    () -> AdvancedFactoryContainerTypes.ADVANCED_FACTORY, type.getBaseMachine(), tier));
            // Note, we can't just return the builder here as then it gets all confused about object types, so we just
            // assign the value here, and then return the builder itself as it is the same object
            builder.withComputerSupport(tier, type.getRegistryNameComponentCapitalized() + "Factory");
            builder.withCustomShape(AdvancedFactoryBlockShapes.getShape(type));
            builder.with(switch (type) {
                case OXIDIZING, DISSOLVING, CRYSTALLIZING -> AttributeSideConfig.ADVANCED_ELECTRIC_MACHINE;
                case CHEMICAL_INFUSING, CENTRIFUGING -> AttributeSideConfig.create(TransmissionType.CHEMICAL, TransmissionType.ITEM, TransmissionType.ENERGY);
                case WASHING -> AttributeSideConfig.create(TransmissionType.CHEMICAL, TransmissionType.FLUID, TransmissionType.ITEM, TransmissionType.ENERGY);
                case PRESSURISED_REACTING -> AttributeSideConfig.create(TransmissionType.ITEM, TransmissionType.CHEMICAL, TransmissionType.FLUID, TransmissionType.ENERGY);
                case LIQUIFYING -> AttributeSideConfig.create(TransmissionType.FLUID, TransmissionType.ITEM, TransmissionType.ENERGY);
            });
            //在注册之前似乎拿不到AttributeHasBounding这个属性，不然可以使用Attribute.get()
            if (type == AdvancedFactoryType.CENTRIFUGING) {
                builder.with(AttributeHasBounding.ABOVE_ONLY);
            }
            builder.replace(new AttributeParticleFX().addDense(ParticleTypes.SMOKE, 5, rand -> new Pos3D(
                    rand.nextFloat() * 0.7F - 0.3F,
                    rand.nextFloat() * 0.1F + 0.7F,
                    rand.nextFloat() * 0.7F - 0.3F)));
            return builder;
        }
    }
}
