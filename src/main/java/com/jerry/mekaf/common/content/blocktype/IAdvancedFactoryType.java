package com.jerry.mekaf.common.content.blocktype;

import com.jerry.mekmm.common.content.blocktype.MoreMachineMachine;

/**
 * Identifies a type that can supply the base machine for an advanced factory family.
 *
 * @param <MACHINE> the base machine type associated with this factory type
 */
public interface IAdvancedFactoryType<MACHINE extends MoreMachineMachine<?>> {

    /**
     * Returns the base machine from which this factory family is upgraded.
     *
     * @return the base machine definition
     */
    MACHINE getBaseMachine();
}
