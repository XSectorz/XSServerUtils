package net.xsapi.panat.xsserverutilsbungee.scp;

import net.xsapi.panat.xsserverutilsbungee.core;

public class scpUsers {

    private String username;
    private String server;
    private long onlineTime;
    private long currentTime;
    private String rank;

    private boolean isOnline = false;

    public scpUsers(String username,String server,long onlineTime,String rank) {
        this.username = username;
        this.server = server;
        this.onlineTime = onlineTime;
        this.rank = rank;

    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setIsOnline(boolean isOnline) {

        core.getPlugin().getLogger().info("Online now is" + isOnline);
        this.isOnline = isOnline;
    }

    public String getUsername() {
        return username;
    }

    public String getServer() {
        return server;
    }

    public String getRank() {
        return rank;
    }

    public long getOnlineTime() {
        return onlineTime;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public void setOnlineTime(long onlineTime) {
        this.onlineTime = onlineTime;
    }
}
