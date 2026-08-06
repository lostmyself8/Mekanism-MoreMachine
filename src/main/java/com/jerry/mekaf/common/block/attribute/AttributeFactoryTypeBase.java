package com.jerry.mekaf.common.block.attribute;

import com.jerry.mekaf.common.content.blocktype.IAdvancedFactoryType;

import mekanism.common.block.attribute.Attribute;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Stores the advanced factory type associated with a machine block definition.
 *
 * @param <TYPE> the concrete factory type supplied by the owning mod
 */
public class AttributeFactoryTypeBase<TYPE extends IAdvancedFactoryType<?>> implements Attribute {

    protected final TYPE type;

    public AttributeFactoryTypeBase(TYPE type) {
        this.type = Objects.requireNonNull(type, "IAdvancedFactoryType can not be null.");
    }

    /**
     * Returns the factory type associated with this block definition.
     *
     * @return the associated factory type
     */
    @NotNull
    public TYPE getAdvancedFactoryType() {
        return type;
    }
}
