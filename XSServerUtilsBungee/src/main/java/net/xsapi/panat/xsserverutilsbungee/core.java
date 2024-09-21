package net.xsapi.panat.xsserverutilsbungee;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.xsapi.panat.xsserverutilsbungee.commands.commandsLoader;
import net.xsapi.panat.xsserverutilsbungee.config.configLoader;
import net.xsapi.panat.xsserverutilsbungee.handler.XSDatabaseHandler;
import net.xsapi.panat.xsserverutilsbungee.handler.XSHandler;
import net.xsapi.panat.xsserverutilsbungee.handler.XSRedisHandler;
import net.xsapi.panat.xsserverutilsbungee.listeners.eventLoader;
import net.xsapi.panat.xsserverutilsbungee.websocket.scpWebSocket;
import xyz.kyngs.librelogin.api.LibreLoginPlugin;
import xyz.kyngs.librelogin.api.provider.LibreLoginProvider;

import java.io.IOException;
import java.net.URI;

public final class core extends Plugin {

    private static core plugin;
    public static core getPlugin() {
        return plugin;
    }

    private static scpWebSocket scpWebSocket;

    public static scpWebSocket getSCPWebSocket() {
        return scpWebSocket;
    }

    private static LibreLoginPlugin<ProxiedPlayer, ServerInfo> apiLibre;

    public static LibreLoginPlugin<ProxiedPlayer, ServerInfo> getLibreAPI() {
        return apiLibre;
    }

    @Override
    public void onEnable() {

        plugin = this;
        apiLibre = ((LibreLoginProvider<ProxiedPlayer, ServerInfo>) getProxy().getPluginManager().getPlugin("LibreLogin")).getLibreLogin();

        apiLibre.getEventProvider().subscribe(apiLibre.getEventTypes().authenticated, (e) -> {
            //core.getPlugin().getLogger().info("Player "  + e.getPlayer());
            //core.getPlugin().getLogger().info("Reason "  + e.getReason());
            ProxiedPlayer p = e.getPlayer();

            if(XSHandler.getBotData().containsKey(p.getName())) {
                ServerInfo serverInfo = core.getPlugin().getProxy().getServerInfo(XSHandler.getBotData().get(p.getName()));
                p.connect(serverInfo);
            }

        });

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
        XSDatabaseHandler.loadSCPUsers();
        new commandsLoader();
        new eventLoader();
        XSHandler.initSystem();
        try {
            URI uri = new URI("wss://ws.siamcraft.net?client=xsserverutils_bungeecord");
            scpWebSocket = new scpWebSocket(uri);
            scpWebSocket.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onDisable() {
        try {
            XSHandler.checkDeleteFromDatabase();
        } catch (Exception e) {
            this.getLogger().info("Cant check ban caught some error");
        }

        if (scpWebSocket != null && scpWebSocket.isOpen()) {
            scpWebSocket.close();
        }

        XSRedisHandler.destroyThreads();

    }
}
