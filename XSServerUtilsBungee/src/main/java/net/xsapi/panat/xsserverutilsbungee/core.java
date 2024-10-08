package net.xsapi.panat.xsserverutilsbungee;

import com.google.gson.Gson;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.xsapi.panat.xsserverutilsbungee.commands.commandsLoader;
import net.xsapi.panat.xsserverutilsbungee.config.configLoader;
import net.xsapi.panat.xsserverutilsbungee.config.mainConfig;
import net.xsapi.panat.xsserverutilsbungee.discord.xsbot;
import net.xsapi.panat.xsserverutilsbungee.handler.XSDatabaseHandler;
import net.xsapi.panat.xsserverutilsbungee.handler.XSHandler;
import net.xsapi.panat.xsserverutilsbungee.handler.XSRedisHandler;
import net.xsapi.panat.xsserverutilsbungee.listeners.eventLoader;
import net.xsapi.panat.xsserverutilsbungee.utils.XSUtils;
import net.xsapi.panat.xsserverutilsbungee.websocket.scpWebSocket;
import xyz.kyngs.librelogin.api.LibreLoginPlugin;
import xyz.kyngs.librelogin.api.provider.LibreLoginProvider;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
                getProxy().getScheduler().schedule(this, new Runnable() {
                    public void run() {
                        if(p != null) {
                            ServerInfo serverInfo = core.getPlugin().getProxy().getServerInfo(XSHandler.getBotData().get(p.getName()));
                            core.getPlugin().getLogger().info("Send " + p.getName() + " to " + serverInfo.getName());
                            p.connect(serverInfo);
                        }
                    }
                }, 5, TimeUnit.SECONDS);
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

        connectWebSocket();
        try {
            new xsbot();
        } catch (LoginException e) {
            core.getPlugin().getLogger().info("ERROR: Provided bot token is invalid!");
        }

        getProxy().getScheduler().schedule(this, new Runnable() {
            @Override
            public void run() {
                Gson gson = new Gson();
                XSHandler.loadDefaultOnlineGroup();
                String onlineList = gson.toJson(XSHandler.getOnlineListServerGroup());
                for(String serverGroup : mainConfig.getConfig().getSection("configuration.serverList").getKeys()) {
                    for(String server : mainConfig.getConfig().getStringList("configuration.serverList." + serverGroup)) {
                        XSRedisHandler.sendRedisMessage(XSRedisHandler.getClientPrefix() + server,"UPDATE_ONLINE<SPLIT>" + onlineList);
                    }
                }
            }
        }, 10, 10, TimeUnit.SECONDS);

        ProxyServer.getInstance().getScheduler().schedule(core.getPlugin(), new Runnable() {
            public void run() {
                System.out.println("Task new session");
                core.getPlugin().getLogger().info("Attempting to reconnect WebSocket...");
                core.connectWebSocket();
            }
        }, 5, 5, TimeUnit.MINUTES);


    }

    public static void connectWebSocket() {
        core.getPlugin().getLogger().info("Try Reconnect WSS v2");
        try {

            if (scpWebSocket != null && scpWebSocket.isOpen()) {
                core.getPlugin().getLogger().info("Closing previous WebSocket connection...");
                scpWebSocket.close();
            }

            scpWebSocket = null;

            URI uri = new URI("wss://ws.siamcraft.net?client=xsserverutils_bungeecord");
            scpWebSocket = new scpWebSocket(uri);
            scpWebSocket.connect();
            core.getPlugin().getLogger().info("Connected successfully");
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
