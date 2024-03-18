package me.lightningreflex.lightningutils.managers;

import me.lightningreflex.lightningutils.LightningUtils;
import me.lightningreflex.lightningutils.configurations.impl.MainConfig;
import me.lightningreflex.lightningutils.features.listeners.ClearchatListener;
import me.lightningreflex.lightningutils.features.listeners.FallbackListener;
import me.lightningreflex.lightningutils.features.listeners.StaffChatListener;

public class ListenerManager {
    public static void registerListeners() {
        MainConfig mainConfig = LightningUtils.getMainConfig();
        if (mainConfig.getFallback().isEnabled())
            registerListener(new FallbackListener());
        if (mainConfig.getStaffchat().isEnabled()) // You can have staffchat without the command, but not vice versa
            registerListener(new StaffChatListener());
        if (mainConfig.getClearchat().isEnabled())
            registerListener(new ClearchatListener());
    }

    private static void registerListener(Object listener) {
        LightningUtils.getProxy().getEventManager().register(LightningUtils.getInstance(), listener);
    }
}
