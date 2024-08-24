package net.xsapi.panat.xsserverutilsbungee.listeners;

import net.xsapi.panat.xsserverutilsbungee.core;

public class eventLoader {

    public eventLoader() {
        core.getPlugin().getProxy().getPluginManager().registerListener(core.getPlugin(), new joinEvent());
        core.getPlugin().getProxy().getPluginManager().registerListener(core.getPlugin(), new serverConnectEvent());
        core.getPlugin().getProxy().getPluginManager().registerListener(core.getPlugin(), new onLeave());
    }
}
