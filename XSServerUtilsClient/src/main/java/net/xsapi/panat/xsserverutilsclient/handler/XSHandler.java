package net.xsapi.panat.xsserverutilsclient.handler;

import net.xsapi.panat.xsserverutilsclient.config.configLoader;
import net.xsapi.panat.xsserverutilsclient.config.mainConfig;

public class XSHandler {


    public static String getSubChannel() {
        return "xsserverutils:channel_client_"+mainConfig.getConfig().getString("configuration.server");
    }

    private static void subChannel() {
        XSRedisHandler.subscribeToChannelAsync(getSubChannel());
    }

    public static void initSystem() {
        new configLoader();
        XSRedisHandler.redisConnection();
        subChannel();
    }
}
