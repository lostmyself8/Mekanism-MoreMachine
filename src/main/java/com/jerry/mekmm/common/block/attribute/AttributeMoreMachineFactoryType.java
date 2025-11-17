package com.jerry.mekmm.common.block.attribute;

import com.jerry.mekmm.common.content.blocktype.MoreMachineFactoryType;

import mekanism.common.block.attribute.Attribute;

import org.jetbrains.annotations.NotNull;

public class AttributeMoreMachineFactoryType implements Attribute {

    private final MoreMachineFactoryType type;

    public AttributeMoreMachineFactoryType(MoreMachineFactoryType type) {
        this.type = type;
    }

    @NotNull
    public MoreMachineFactoryType getMoreMachineFactoryType() {
        return type;
    }
}
