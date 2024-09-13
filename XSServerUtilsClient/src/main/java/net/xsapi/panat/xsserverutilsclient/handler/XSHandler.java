package net.xsapi.panat.xsserverutilsclient.handler;

import net.xsapi.panat.xsserverutilsclient.config.configLoader;
import net.xsapi.panat.xsserverutilsclient.config.mainConfig;
import net.xsapi.panat.xsserverutilsclient.core;
import net.xsapi.panat.xsserverutilsclient.objects.XSMuteplayers;
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

    public static void initSystem() {
        new configLoader();
        XSRedisHandler.redisConnection();
        subChannel();
        requestData();
    }
}
