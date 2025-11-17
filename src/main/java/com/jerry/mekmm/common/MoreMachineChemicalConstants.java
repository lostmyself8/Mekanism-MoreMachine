package com.jerry.mekmm.common;

import mekanism.common.base.IChemicalConstant;

public enum MoreMachineChemicalConstants implements IChemicalConstant {

    NUTRITIONAL_PASTE("nutritional_paste", 0xFFEB6CA3, 0, 284F, 1_254F),
    NUTRIENT_SOLUTION("nutrient_solution", 0xFF265F0C, 0, 300F, 1_513F),
    UU_MATTER("uu_matter", 0xFF530570, 0, 300F, 1000F),
    UNSTABLE_DIMENSIONAL_GAS("unstable_dimensional_gas", 0xFFE19427, 0, 10.24F, 84.65F);

    private final String name;
    private final int color;
    private final int lightLevel;
    private final float temperature;
    private final float density;

    /**
     * @param name        The name of the chemical
     * @param color       Visual color in ARGB format
     * @param lightLevel  Light level
     * @param temperature Temperature in Kelvin that the chemical exists as a liquid
     * @param density     Density as a liquid in kg/m^3
     */
    MoreMachineChemicalConstants(String name, int color, int lightLevel, float temperature, float density) {
        this.name = name;
        this.color = color;
        this.lightLevel = lightLevel;
        this.temperature = temperature;
        this.density = density;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getColor() {
        return color;
    }

    @Override
    public float getTemperature() {
        return temperature;
    }

    @Override
    public float getDensity() {
        return density;
    }

    @Override
    public int getLightLevel() {
        return lightLevel;
    }
}
