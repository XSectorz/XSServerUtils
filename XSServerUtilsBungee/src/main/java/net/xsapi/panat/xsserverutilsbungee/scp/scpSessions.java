package net.xsapi.panat.xsserverutilsbungee.scp;

public class scpSessions {

    public long logoutTime;
    public String ipAddr;

    public scpSessions(String ipAddr,long logoutTime) {
        this.ipAddr = ipAddr;
        this.logoutTime = logoutTime;
    }

    public void setLogoutTime(long logoutTime) {
        this.logoutTime = logoutTime;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public long getLogoutTime() {
        return logoutTime;
    }

    public String getIpAddr() {
        return ipAddr;
    }
}
