package net.xsapi.panat.xsserverutilsbungee;

import net.md_5.bungee.api.plugin.Plugin;
import net.xsapi.panat.xsserverutilsbungee.commands.commandsLoader;
import net.xsapi.panat.xsserverutilsbungee.config.configLoader;
import net.xsapi.panat.xsserverutilsbungee.handler.XSDatabaseHandler;
import net.xsapi.panat.xsserverutilsbungee.handler.XSHandler;
import net.xsapi.panat.xsserverutilsbungee.handler.XSRedisHandler;
import net.xsapi.panat.xsserverutilsbungee.listeners.eventLoader;

import java.io.IOException;

public final class core extends Plugin {

    private static core plugin;
    public static core getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {

        plugin = this;

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        try {
            new configLoader();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        XSDatabaseHandler.createSQLDatabase();
        XSDatabaseHandler.loadBanList();
        XSDatabaseHandler.loadMuteList();
        new commandsLoader();
        new eventLoader();
        XSHandler.initSystem();


    }

    @Override
    public void onDisable() {
        try {
            XSHandler.checkDeleteFromDatabase();
        } catch (Exception e) {
            this.getLogger().info("Cant check ban caught some error");
        }

        XSRedisHandler.destroyThreads();

    }
}
