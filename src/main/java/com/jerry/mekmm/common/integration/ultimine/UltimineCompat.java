package com.jerry.mekmm.common.integration.ultimine;

import com.jerry.mekmm.Mekmm;

import dev.ftb.mods.ftbultimine.api.rightclick.RegisterRightClickHandlerEvent;

/**
 * FTB Ultimité 可选集成入口；只有检测到模组存在时才调用注册方法。
 */
public final class UltimineCompat {

    private UltimineCompat() {}

    public static void init() {
        if (Mekmm.hooks.ftbUltimine.isLoaded()) {
            RegisterRightClickHandlerEvent.REGISTER.register(dispatcher -> dispatcher.registerHandler(new ConnectorHandler()));
        }
    }
}
