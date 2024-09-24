package net.xsapi.panat.xsserverutilsbungee.discord;

public class verifyUser {

    private String discordID;
    private String userName;
    private long verifyDate;

    public verifyUser(String userName,String discordID,long verifyDate) {
        this.discordID = discordID;
        this.userName = userName;
        this.verifyDate = verifyDate;
    }

    public String getUserName() {
        return userName;
    }

    public long getVerifyDate() {
        return verifyDate;
    }

    public String getDiscordID() {
        return discordID;
    }
}
