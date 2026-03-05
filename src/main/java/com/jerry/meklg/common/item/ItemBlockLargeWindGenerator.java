package com.jerry.meklg.common.item;

import mekanism.common.block.prefab.BlockTile;
import mekanism.common.item.block.machine.ItemBlockMachine;

import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import com.jerry.meklg.client.render.LargeGeneratorRenderPropertiesProvider;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ItemBlockLargeWindGenerator extends ItemBlockMachine {

    public ItemBlockLargeWindGenerator(BlockTile<?, ?> block) {
        super(block);
    }

    @Override
    public void initializeClient(@NotNull Consumer<IClientItemExtensions> consumer) {
        consumer.accept(LargeGeneratorRenderPropertiesProvider.wind());
    }
}
