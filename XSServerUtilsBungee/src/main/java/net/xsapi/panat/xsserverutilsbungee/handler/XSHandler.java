package net.xsapi.panat.xsserverutilsbungee.handler;

import net.xsapi.panat.xsserverutilsbungee.objects.XSBanplayers;
import net.xsapi.panat.xsserverutilsbungee.objects.XSMuteplayers;
import net.xsapi.panat.xsserverutilsbungee.scp.scpUsers;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class XSHandler {

    private static HashMap<String, XSBanplayers> banlist = new HashMap<>();
    private static HashMap<String, XSMuteplayers> mutelist = new HashMap<>();

    private static HashMap<String, scpUsers> scpUsers = new HashMap<>();

    public static HashMap<String, XSBanplayers> getBanList() {
        return banlist;
    }
    public static HashMap<String, XSMuteplayers> getMuteList() {
        return mutelist;
    }
    public static HashMap<String, scpUsers> getScpUsers() {
        return scpUsers;
    }

    public static String getSubChannel() {
        return "xsserverutils:channel_bungeecord";
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
    }
}
