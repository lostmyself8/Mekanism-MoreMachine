package com.jerry.mekaf.common.content.blocktype;

import com.jerry.mekaf.common.block.attribute.AttributeAdvancedFactoryType;
import com.jerry.mekaf.common.registries.AdvancedFactoryBlocks;
import com.jerry.mekaf.common.registries.AdvancedFactoryContainerTypes;
import com.jerry.mekaf.common.tile.base.TileEntityAdvancedFactoryBase;

import com.jerry.mekmm.common.content.blocktype.MoreMachineBlockShapes;
import com.jerry.mekmm.common.content.blocktype.MoreMachineMachine.MoreMachineFactoryMachine;

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

public class AdvancedFactory<TILE extends TileEntityAdvancedFactoryBase<?>> extends MoreMachineFactoryMachine<TILE> {

    private final MoreMachineFactoryMachine<?> origMachine;

    public AdvancedFactory(Supplier<TileEntityTypeRegistryObject<TILE>> tileEntityRegistrar, Supplier<ContainerTypeRegistryObject<? extends MekanismContainer>> containerRegistrar,
                           MoreMachineFactoryMachine<?> origMachine, FactoryTier tier) {
        super(tileEntityRegistrar, MekanismLang.DESCRIPTION_FACTORY, origMachine.getAdvancedFactoryType());
        this.origMachine = origMachine;
        setMachineData(tier);
        add(new AttributeGui(containerRegistrar, null), new AttributeTier<>(tier));

        if (tier.ordinal() < EnumUtils.FACTORY_TIERS.length - 1) {
            add(new AttributeUpgradeable(() -> AdvancedFactoryBlocks.getAdvancedFactory(EnumUtils.FACTORY_TIERS[tier.ordinal() + 1], origMachine.getAdvancedFactoryType())));
        }
    }

    private void setMachineData(FactoryTier tier) {
        setFrom(origMachine, AttributeSound.class, AttributeAdvancedFactoryType.class, AttributeUpgradeSupport.class);
        AttributeEnergy origEnergy = origMachine.get(AttributeEnergy.class);
        if (origEnergy != null) {
            add(new AttributeEnergy(origEnergy::getUsage, () -> origEnergy.getConfigStorage().max(origEnergy.getUsage()).multiply(tier.processes)));
        }
    }

    public static class AdvancedFactoryBuilder<FACTORY extends AdvancedFactory<TILE>, TILE extends TileEntityAdvancedFactoryBase<?>, T extends MoreMachineMachineBuilder<FACTORY, TILE, T>>
                                              extends BlockTileBuilder<FACTORY, TILE, T> {

        protected AdvancedFactoryBuilder(FACTORY holder) {
            super(holder);
        }

        @SuppressWarnings("unchecked")
        public static <TILE extends TileEntityAdvancedFactoryBase<?>> AdvancedFactoryBuilder<AdvancedFactory<TILE>, TILE, ?> createAdvancedFactory(Supplier<?> tileEntityRegistrar, AdvancedFactoryType type,
                                                                                                                                                   FactoryTier tier) {
            AdvancedFactoryBuilder<AdvancedFactory<TILE>, TILE, ?> builder = new AdvancedFactoryBuilder<>(new AdvancedFactory<>((Supplier<TileEntityTypeRegistryObject<TILE>>) tileEntityRegistrar,
                    () -> AdvancedFactoryContainerTypes.ADVANCED_FACTORY, type.getBaseMachine(), tier));
            builder.withComputerSupport(tier, type.getRegistryNameComponentCapitalized() + "Factory");
            builder.withCustomShape(MoreMachineBlockShapes.getShape(tier, type));
            // 由于1.20.1Mek没有将BoundingBlock加入到ItemTierInstaller中，导致升级有BoundingBlock方块会导致直接消失
            // if (type == AdvancedFactoryType.CENTRIFUGING) {
            // builder.withBounding((pos, state, builderPos) -> builderPos.add(pos.above()));
            // }
            builder.replace(new AttributeParticleFX().addDense(ParticleTypes.SMOKE, 5, rand -> new Pos3D(
                    rand.nextFloat() * 0.7F - 0.3F,
                    rand.nextFloat() * 0.1F + 0.7F,
                    rand.nextFloat() * 0.7F - 0.3F)));
            return builder;
        }
    }
}
