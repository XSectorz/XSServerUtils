package net.xsapi.panat.xsserverutilsbungee.handler;

import com.google.gson.Gson;
import net.xsapi.panat.xsserverutilsbungee.config.botConfig;
import net.xsapi.panat.xsserverutilsbungee.config.mainConfig;
import net.xsapi.panat.xsserverutilsbungee.objects.XSBanplayers;
import net.xsapi.panat.xsserverutilsbungee.objects.XSMuteplayers;
import net.xsapi.panat.xsserverutilsbungee.scp.scpSessions;
import net.xsapi.panat.xsserverutilsbungee.scp.scpUsers;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class XSHandler {

    private static HashMap<String, XSBanplayers> banlist = new HashMap<>();
    private static HashMap<String, XSMuteplayers> mutelist = new HashMap<>();

    private static HashMap<String, scpUsers> scpUsers = new HashMap<>();
    public static HashMap<String, scpSessions> scpUserSessions = new HashMap<>();


    public static HashMap<String, XSBanplayers> getBanList() {
        return banlist;
    }
    public static HashMap<String, XSMuteplayers> getMuteList() {
        return mutelist;
    }
    public static HashMap<String, scpUsers> getScpUsers() {
        return scpUsers;
    }
    public static HashMap<String, scpSessions> getScpUserSessions() {
        return scpUserSessions;
    }

    private static HashMap<String,String> botData = new HashMap<>();

    public static HashMap<String,String> getBotData() {
        return botData;
    }

    public static String getSubChannel() {
        return "xsserverutils:channel_bungeecord";
    }

    public static void updateSCPUser() {
        Gson gson = new Gson();
        String scpJSON = gson.toJson(XSHandler.getScpUsers());
        for(String serverGroup : mainConfig.getConfig().getSection("configuration.serverList").getKeys()) {
            for(String server : mainConfig.getConfig().getStringList("configuration.serverList." + serverGroup)) {
                XSRedisHandler.sendRedisMessage(XSRedisHandler.getClientPrefix() + server,"UPDATE_SCP_USER<SPLIT>" + scpJSON);
            }
        }
    }

    public static void loadBotData() {
        for(String section : botConfig.getConfig().getSection("bot_configuration").getKeys()) {

            String targetServer = botConfig.getConfig().getString("bot_configuration." + section + ".target_servers");

            for(String name : botConfig.getConfig().getStringList("bot_configuration." + section + ".names")) {
                getBotData().put(name,targetServer);
            }

        }
    }

    public static void checkDeleteFromDatabase() {
        for(Map.Entry<String,XSBanplayers> ban : banlist.entrySet()) {
            if(new Date().getTime()- ban.getValue().getEnd_date() >= 0 && ban.getValue().getEnd_date() != -1) { //time out
                XSDatabaseHandler.deleteBanPlayerFromDatabase(ban.getValue().getIdRef());
            }
        }
        for(Map.Entry<String,XSMuteplayers> mute : mutelist.entrySet()) {
            if(new Date().getTime()- mute.getValue().getEnd_date() >= 0 && mute.getValue().getEnd_date() != -1) { //time out
                XSDatabaseHandler.deleteMutePlayerFromDatabase(mute.getValue().getIdRef());
            }
        }
    }

    private static void subChannel() {
        XSRedisHandler.subscribeToChannelAsync(getSubChannel());
    }

    public static void initSystem() {
        XSRedisHandler.redisConnection();
        subChannel();
        loadBotData();
    }
}
