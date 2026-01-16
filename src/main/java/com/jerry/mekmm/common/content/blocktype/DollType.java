package com.jerry.mekmm.common.content.blocktype;

import com.jerry.mekmm.common.MoreMachineLang;

import mekanism.api.text.ILangEntry;

import lombok.Getter;

public enum DollType {

    AUTHOR("author", MoreMachineLang.DESCRIPTION_AUTHOR_DOLL),
    MODELER("modeler", MoreMachineLang.DESCRIPTION_MODELER_DOLL),
    CEU_AUTHOR("ceu_author", MoreMachineLang.DESCRIPTION_AUTHOR_DOLL);

    @Getter
    private final String registryNameComponent;
    @Getter
    private final ILangEntry description;

    DollType(String registryNameComponent, ILangEntry description) {
        this.registryNameComponent = registryNameComponent;
        this.description = description;
    }
}
