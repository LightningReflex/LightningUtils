package me.lightningreflex.lightningutils.managers;

import me.lightningreflex.lightningutils.LightningUtils;
import me.lightningreflex.lightningutils.configurations.impl.MainConfig;
import me.lightningreflex.lightningutils.features.listeners.FallbackListener;

public class ListenerManager {
    public static void registerListeners() {
        MainConfig.Fallback fallback = LightningUtils.getMainConfig().getFallback();
        if (fallback.isEnabled())
            registerListener(new FallbackListener());
    }

    private static void registerListener(Object listener) {
        LightningUtils.getProxy().getEventManager().register(LightningUtils.getInstance(), listener);
    }
}
