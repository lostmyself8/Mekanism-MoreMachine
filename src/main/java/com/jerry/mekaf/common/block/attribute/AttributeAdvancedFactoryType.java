package com.jerry.mekaf.common.block.attribute;

import com.jerry.mekaf.common.content.blocktype.AdvancedFactoryType;

import mekanism.common.block.attribute.Attribute;

import org.jetbrains.annotations.NotNull;

public class AttributeAdvancedFactoryType implements Attribute {

    private final AdvancedFactoryType type;

    public AttributeAdvancedFactoryType(AdvancedFactoryType type) {
        this.type = type;
    }

    @NotNull
    public AdvancedFactoryType getAdvancedFactoryType() {
        return type;
    }
}
