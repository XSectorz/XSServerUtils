package net.xsapi.panat.xsserverutilsclient;

import net.xsapi.panat.xsserverutilsclient.handler.XSHandler;
import net.xsapi.panat.xsserverutilsclient.handler.XSRedisHandler;
import org.bukkit.plugin.java.JavaPlugin;

public final class core extends JavaPlugin {


    private static core plugin;

    public static core getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;

        XSHandler.initSystem();

    }

    @Override
    public void onDisable() {
        XSRedisHandler.destroyThreads();
    }
}
