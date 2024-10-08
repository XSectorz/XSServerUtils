package net.xsapi.panat.xsserverutilsclient.scp;

public class scpUsers {

    private String username;
    private String server;
    private long onlineTime;
    private long currentTime;
    private String rank;

    private boolean isOnline = false;

    public scpUsers(String username, String server, long onlineTime, String rank) {
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

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public void setOnlineTime(long onlineTime) {
        this.onlineTime = onlineTime;
    }
}
