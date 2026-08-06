package com.jerry.mekaf.common.block.attribute;

import com.jerry.mekaf.common.content.blocktype.AdvancedFactoryType;

import org.jetbrains.annotations.NotNull;

public class AttributeAdvancedFactoryType extends AttributeFactoryTypeBase<AdvancedFactoryType> {

    public AttributeAdvancedFactoryType(AdvancedFactoryType type) {
        super(type);
    }

    @Override
    public @NotNull AdvancedFactoryType getAdvancedFactoryType() {
        return type;
    }
}
