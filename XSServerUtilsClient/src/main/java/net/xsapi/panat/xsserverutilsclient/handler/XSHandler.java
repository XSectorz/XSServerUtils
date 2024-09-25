package net.xsapi.panat.xsserverutilsclient.handler;

import net.xsapi.panat.xsserverutilsclient.config.configLoader;
import net.xsapi.panat.xsserverutilsclient.config.mainConfig;
import net.xsapi.panat.xsserverutilsclient.core;
import net.xsapi.panat.xsserverutilsclient.objects.XSMuteplayers;
import net.xsapi.panat.xsserverutilsclient.placeholder.XSPlaceholders;
import net.xsapi.panat.xsserverutilsclient.scp.scpUsers;

import java.util.HashMap;

public class XSHandler {
    private static HashMap<String, scpUsers> scpUsers = new HashMap<>();
    private static HashMap<String, XSMuteplayers> mutelist = new HashMap<>();
    public static HashMap<String, XSMuteplayers> getMuteList() {
        return mutelist;
    }
    public static HashMap<String, scpUsers> getScpUsers() {
        return scpUsers;
    }
    private static HashMap<String,Integer> onlineListServerGroup = new HashMap<>();
    private static XSPlaceholders xsPlaceholders;

    public static XSPlaceholders getXsPlaceholders() {
        return xsPlaceholders;
    }


    public static void setOnlineListServerGroup(HashMap<String, Integer> onlineListServerGroup) {
        XSHandler.onlineListServerGroup = onlineListServerGroup;
    }

    public static HashMap<String,Integer> getOnlineListServerGroup() {
        return onlineListServerGroup;
    }
    public static void setMutelist(HashMap<String, XSMuteplayers> mutelist) {
        XSHandler.mutelist = mutelist;
    }
    public static String getSubChannel() {
        return "xsserverutils:channel_client_"+mainConfig.getConfig().getString("configuration.server");
       // return "xsserverutils:channel_client_";
    }

    public static void setSCPUser(HashMap<String, scpUsers> scpUser) {
        scpUsers = scpUser;
    }

    private static void subChannel() {
        XSRedisHandler.subscribeToChannelAsync(getSubChannel());
    }

    private static void requestData() {
        XSRedisHandler.sendRedisMessage(XSRedisHandler.getHostPrefix(),"REQUEST_DATA<SPLIT>"+mainConfig.getConfig().getString("configuration.server"));
        //core.getPlugin().getLogger().info("Req data sent");
    }

    private static void registerPlaceholder() {
        xsPlaceholders = new XSPlaceholders();
        xsPlaceholders.register();
    }

    public static void unregisterPlaceholder() {
        xsPlaceholders.unregister();
    }

    public static void initSystem() {
        new configLoader();
        XSRedisHandler.redisConnection();
        subChannel();
        requestData();
        registerPlaceholder();
    }
}
