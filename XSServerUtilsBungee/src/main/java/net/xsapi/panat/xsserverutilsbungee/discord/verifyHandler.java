package net.xsapi.panat.xsserverutilsbungee.discord;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class verifyHandler {

    private static HashMap<UUID,verifyUser> verifyUser = new HashMap<>();
    private static HashMap<String, verifyDiscordQueue> verifyDiscordQueue =  new HashMap<>();
    private static HashMap<String, String> discordUsername =  new HashMap<>();


    public static String getUserFromVerify(String discordID) {

        for(Map.Entry<UUID,verifyUser> verifyUser : verifyUser.entrySet()) {
            if(verifyUser.getValue().getDiscordID().equalsIgnoreCase(discordID)) {
                return verifyUser.getValue().getUserName();
            }
        }
        return "";

    }

    public static HashMap<String, String> getDiscordUsername() {
        return discordUsername;
    }

    public static HashMap<UUID,verifyUser> getVerifyUser() {
        return verifyUser;
    }

    public static HashMap<String,verifyDiscordQueue> getVerifyDiscordQueue() {
        return verifyDiscordQueue;
    }

}
