package net.xsapi.panat.xsserverutilsbungee.discord;

public class verifyDiscordQueue {

    private long verifyTime;
    private String discordName;
    private String secretDigit;
    private String currentUserUUID;
    private String currentUserName;

    public verifyDiscordQueue(String discordName,long verifyTime,String secretDigit,String currentUserUUID,String currentUserName) {
        this.discordName = discordName;
        this.verifyTime = verifyTime;
        this.secretDigit = secretDigit;
        this.currentUserUUID = currentUserUUID;
        this.currentUserName = currentUserName;
    }

    public String getCurrentUserName() {
        return currentUserName;
    }

    public String getCurrentUserUUID() {
        return currentUserUUID;
    }

    public String getSecretDigit() {
        return secretDigit;
    }

    public long getVerifyTime() {
        return verifyTime;
    }

    public String getDiscordName() {
        return discordName;
    }
}
